//TODO: minor, but we're declaring new ChessPiece objects when we don't always need to
//TODO instead of empty chesspieces, why not null?

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.AbstractCollection;

/**
 * Constructor
 */
public class ChessPuzzle {

    public boolean whiteTurn;
    public ChessPiece[][] board;

    int blackKingXPos;
    int blackKingYPos;
    int whiteKingXPos;
    int whiteKingYPos;
    public static boolean parallel; //controls whether getLegalMoves is sequential or parallel.
    public static ExecutorService pool; //pool is static since new ChessPuzzles are created by solver
    public ChessPuzzle(boolean whiteTurn, ChessPiece[][] board) {
        this.board = board;
        this.whiteTurn = whiteTurn;
        boolean whiteKingExists = false;
        boolean blackKingExists = false;
        //find and record location of kings to make checking for check easier
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board[i][j];
                if (piece.getMyType() == type.KING) {
                    if (piece.isWhite()) {
                        whiteKingXPos = i;
                        whiteKingYPos = j;
                        whiteKingExists = true;
                    } else {
                        blackKingXPos = i;
                        blackKingYPos = j;
                        blackKingExists = true;
                    }

                }
            }
        }
        //for some test cases, there's no king. This deals with that
        if (!whiteKingExists) {
            whiteKingXPos = -1;
            whiteKingYPos = -1;
        }
        if (!blackKingExists) {
            blackKingXPos = -1;
            blackKingYPos = -1;
        }


    }

    //same constructor as above, just adds in a pool as well
    public ChessPuzzle(boolean whiteTurn, ChessPiece[][] board, ExecutorService pool) {
        this.board = board;
        this.whiteTurn = whiteTurn;
        boolean whiteKingExists = false;
        boolean blackKingExists = false;
        //find and record location of kings to make checking for check easier
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board[i][j];
                if (piece.getMyType() == type.KING) {
                    if (piece.isWhite()) {
                        whiteKingXPos = i;
                        whiteKingYPos = j;
                        whiteKingExists = true;
                    } else {
                        blackKingXPos = i;
                        blackKingYPos = j;
                        blackKingExists = true;
                    }

                }
            }
        }
        //for some test cases, there's no king. This deals with that
        if (!whiteKingExists) {
            whiteKingXPos = -1;
            whiteKingYPos = -1;
        }
        if (!blackKingExists) {
            blackKingXPos = -1;
            blackKingYPos = -1;
        }
        parallel = true;
        this.pool = pool;

    }


    //method that adds a pool and sets parallel to true
    public void addPool(ExecutorService pool) {
        parallel = true;
        this.pool = pool;
    }

    //returns the first move found resulting in checkmate. One move mates only
    @Deprecated
    public Move solvePuzzleOneMove() {
        LinkedList<Move> legalMoves = this.getLegalMoves();
        //if a move does not lead to check on the opposing king, remove it
        legalMoves.removeIf(o -> !checkCheck((Move) o, !this.whiteTurn, this.board));
        ChessPuzzle p;
        for (Move m : legalMoves) {
            p = new ChessPuzzle(!this.whiteTurn, m.executeMove(this.board)); //create a new puzzle that represents the state after the move is executed
            if (p.getLegalMoves().size() == 0) //if opponent has no moves, that's the game
                return m;
        }
        return null;
    }
    @Deprecated
    //old attempt to solve the puzzle using the MoveTree.
    public LinkedList<LinkedList<Move>> nodeSolvePuzzle() {
        oppMoveNode root = new oppMoveNode(null, null, this);
        root.setChildren();
        for (oppMoveNode.myMoveNode n : root.children) {
            n.setChildren();
        }
        if (root.checkMate)
            return root.getSolutions();
        LinkedList<oppMoveNode.myMoveNode> children = root.children;
        for (int i = 0; i < children.size(); i++) {
            oppMoveNode.myMoveNode n = children.get(i);
            LinkedList<oppMoveNode> oppMoveNodes = n.children;
            for (int i1 = 0; i1 < oppMoveNodes.size(); i1++) {
                oppMoveNode m = oppMoveNodes.get(i1);
                m.setChildren();
                LinkedList<oppMoveNode.myMoveNode> myMoveNodes = m.children;
                for (int i2 = 0; i2 < myMoveNodes.size(); i2++) {
                    oppMoveNode.myMoveNode a = myMoveNodes.get(i2);
                    a.setChildren();
                }
            }
        }
        return root.getSolutions();
    }

    //solves the puzzle, max depth 3 hardcoded. Could be sequential or with getLegalParallelized
    public LinkedList<LinkedList<Move>> solvePuzzle() {
        MoveTree mt = new MoveTree(this);
        return mt.solveTree(3);
    }

    //solves the puzzle, max depth 3 hardcoded.
    public LinkedList<LinkedList<Move>> solvePuzzleSuperParallel(ExecutorService pool) {
        MoveTreeParallel mt = new MoveTreeParallel(this, pool);
        return mt.solveTree(3);
    }

    /**
     * Legal moves for each piece
     * Kings  : One square space in any direction as long as the square cannot be attacked by another piece.
     * Special move Kings Castling
     * Queens : Diagonally, horizontally, or vertically any number of spaces. Cannot jump over pieces.
     * Rooks  : Horizontally or vertically any number of squares. They are Unable to jump over pieces.
     * Rooks move when king castles.
     * Bishops: Diagonally any number of squares. They are unable to jump over pieces.
     * Knights: Move in an L shape: 2 squares horizontally or vertically. They can jump over pieces.
     * Pawns  : Vertically forward one move unless they haven't moved before.
     * Pawns Capture one square in forwards diagonal movement
     *
     * @return Array of Move's
     */
    public LinkedList<Move> getLegalMovesIgnoreCheck(boolean whiteTurn) {

        LinkedList<Move> legalMoves = new LinkedList<Move>();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                legalMoves.addAll(getLocationLegalMoves(i, j, whiteTurn, this.board, false)); //the castling parameter is to prevent infinite recursion when both players can castle.
            }
        legalMoves.removeIf(m -> m instanceof CastleMove);
        return legalMoves;
    }

    /**
     * Same as above, except this version ensure you can't make any moves that would put yourself in check.
     * The above version is for checking for check; that is, you can't make a move putting yourself in check, even if the move your opp
     * would make to take your king would put himself in check too
     */
    public LinkedList<Move> getLegalMoves() {
        if (parallel)
            return getLegalMovesParallel();
//        long start = System.nanoTime();

        LinkedList<Move> moves = new LinkedList<Move>();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                moves.addAll(getLocationLegalMoves(i, j, this.whiteTurn, this.board, true));
            }

        //if a move leads to check for the player whose turn it is, remove it
        moves.removeIf(o -> checkCheck(o, this.whiteTurn, this.board));

        return moves;

    }

    /**
     * Same as getLegalMoves but parallelized
     */
    public LinkedList<Move> getLegalMovesParallel() {

        // master list of moves
        LinkedList<Move> moves = new LinkedList<Move>();

        // list of moves after removing all moves that place king in check
        LinkedList<Move> legalMoves = new LinkedList<Move>();

        // sharedList contains all the possible moves. It is shared by multiple threads
        AtomicReferenceArray<LinkedList<Move>> sharedList = new AtomicReferenceArray<LinkedList<Move>>(64);
        CountDownLatch latch = new CountDownLatch(64);

        //================= Get location legal moves ==================== //
        int id = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (this.board[i][j].isWhite == this.whiteTurn) { //only create a task when there is a piece of the right color present
                    ChessPuzzle p = new ChessPuzzle(this.whiteTurn, this.board);
                    pool.execute(new getLocationLegalMovesTask(p, i, j, id, sharedList, latch));
                    id++;
                } else latch.countDown(); //if a task isn't created, countdown the latch.
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("Latch interrupted. That's not good.");
            System.exit(-1);
        }

        // add all moves to main moves list
        for (int i = 0; i < sharedList.length(); i++) {
            if (sharedList.get(i) != null) {
                legalMoves.addAll(sharedList.get(i));
            }
        }



        // Returns list of legal moves
        return legalMoves;
    }




    /**
     * Method returns a list of legal moves for the chess piece in chessboard[x_start][y_start]
     *
     * @param x_start x position of chess piece
     * @param y_start y position of chess piece
     * @return List of legal moves
     */
    public LinkedList<Move> getLocationLegalMoves(int x_start, int y_start, boolean whiteTurn, ChessPiece[][] Cboard, boolean castling) {
        LinkedList<Move> moves = new LinkedList<>();
        if (Cboard[x_start][y_start].isWhite() != whiteTurn)
            return moves;
        try {
            switch (Cboard[x_start][y_start].getMyType()) {
                case ROOK: //assuming the player is black
                    //  System.out.println("rook");
                    moves.addAll(getRookMoves(x_start, y_start, Cboard));
                    break;
                case BISHOP:
                    //   System.out.println("Bishop");
                    moves.addAll(getBishopMoves(x_start, y_start, Cboard));
                    break;
                case KING:
                    //  System.out.println("King");
                    moves.addAll(getKingMoves(x_start, y_start, Cboard, castling));
                    break;
                case KNIGHT:
                    //  System.out.println("Knight");
                    moves.addAll(getKnightMoves(x_start, y_start, Cboard));
                    break;
                case PAWN:
                    //  System.out.println("PAWN");
                    moves.addAll(getPawnMoves(x_start, y_start, Cboard));
                    break;
                case QUEEN:
                    // System.out.println("QUEEN");
                    moves.addAll(getQueenMoves(x_start, y_start, Cboard));
                    break;
                case EMPTY:
                    // System.out.println("EMPTY");
                    break;
                default:
                    break;
            }
        } catch (Exception e) { //if there's an exception, make a new GUI with the board state so we can make a test case out of the exception and work on fixing it
            Gui board = this.makeGui();
            System.out.println(e.getMessage());
            return null;
        }

        return moves;
    }

    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of moves for the king at board[x][y]. Castling is included
     *
     * @param row   starting index on board
     * @param col   starting index on board
     * @param board chess board
     * @return list of moves
     */
    public LinkedList<Move> getKingMoves(int row, int col, ChessPiece[][] board, boolean castling) {
        LinkedList<Move> moves = new LinkedList<>();
        ChessPiece king = new ChessPiece(type.KING, this.whiteTurn);

        // Check up and down
        // Ensure that moves is within the bounds of the board and that the space is either empty or enemy
        if (row + 1 < 8 && (board[row + 1][col].getMyType() == type.EMPTY || board[row + 1][col].isWhite() != this.whiteTurn))
            moves.add(new Move(king, row, col, row + 1, col));
        if (row - 1 >= 0 && (board[row - 1][col].getMyType() == type.EMPTY || board[row - 1][col].isWhite() != this.whiteTurn))
            moves.add(new Move(king, row, col, row - 1, col));

        // Check right and left
        if (col + 1 < 8 && (board[row][col + 1].getMyType() == type.EMPTY || board[row][col + 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, row, col, row, col + 1));
        if (col - 1 >= 0 && (board[row][col - 1].getMyType() == type.EMPTY || board[row][col - 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, row, col, row, col - 1));

        // Check diagonal up/right and down/right
        if (row + 1 < 8 && col + 1 < 8 && (board[row + 1][col + 1].getMyType() == type.EMPTY || board[row + 1][col + 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, row, col, row + 1, col + 1));
        if (row - 1 >= 0 && col + 1 < 8 && (board[row - 1][col + 1].getMyType() == type.EMPTY || board[row - 1][col + 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, row, col, row - 1, col + 1));

        // Check diagonal up/left and down/left
        if (row - 1 >= 0 && col - 1 >= 0 && (board[row - 1][col - 1].getMyType() == type.EMPTY || board[row - 1][col - 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, row, col, row - 1, col - 1));
        if (row + 1 < 8 && col - 1 >= 0 && (board[row + 1][col - 1].getMyType() == type.EMPTY || board[row + 1][col - 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, row, col, row + 1, col - 1));

        //CASTLING! this code is horrible and I hate it. Note that oppmoves is only calculated inside ifs to avoid unnecessary labor


        if(castling) {
            //white kingside
            if (this.whiteTurn && board[7][7].getMyType() == type.ROOK && board[7][7].isWhite()
                    && board[7][4].getMyType() == type.KING && board[7][4].isWhite()) {
                //create new puzzle with other player's turn, same board, and calculate their moves
                LinkedList<Move> oppMoves = new ChessPuzzle(!this.whiteTurn, this.board).getLegalMovesIgnoreCheck(!this.whiteTurn);
                //check that all spots along the way are not threatened and are empty
                if (!checkThreatened(7, 4, oppMoves)
                        && !checkThreatened(7, 5, oppMoves) && board[7][5].isEmpty()
                        && !checkThreatened(7, 6, oppMoves) && board[7][6].isEmpty())
                    moves.add(new CastleMove(board[7][4], 7, 4, 7, 6, board[7][7]));
//            Gui gui = new Gui(moves.get(moves.size() - 1).executeMove(board));
            }

            //white queenside
            if (this.whiteTurn && board[7][0].getMyType() == type.ROOK && board[7][0].isWhite()
                    && board[7][4].getMyType() == type.KING && board[7][4].isWhite()) {
                //create new puzzle with other player's turn, same board, and calculate their moves
                LinkedList<Move> oppMoves = new ChessPuzzle(!this.whiteTurn, this.board).getLegalMovesIgnoreCheck(!this.whiteTurn);
                //check that all spots along the way are not threatened and are empty
                if (!checkThreatened(7, 4, oppMoves)
                        && !checkThreatened(7, 3, oppMoves) && board[7][3].isEmpty()
                        && !checkThreatened(7, 2, oppMoves) && board[7][2].isEmpty())
                    moves.add(new CastleMove(board[7][4], 7, 4, 7, 2, board[7][0]));
            }

            //black kingside
            if (!this.whiteTurn && board[0][7].getMyType() == type.ROOK && !board[0][7].isWhite()
                    && board[0][4].getMyType() == type.KING && !board[0][4].isWhite()) {
                //create new puzzle with other player's turn, same board, and calculate their moves
                LinkedList<Move> oppMoves = new ChessPuzzle(!this.whiteTurn, this.board).getLegalMovesIgnoreCheck(!this.whiteTurn);
                //check that all spots along the way are not threatened and are empty
                if (!checkThreatened(0, 4, oppMoves)
                        && !checkThreatened(0, 5, oppMoves) && board[0][5].isEmpty()
                        && !checkThreatened(0, 6, oppMoves) && board[0][6].isEmpty())
                    moves.add(new CastleMove(board[0][4], 0, 4, 0, 6, board[0][7]));
//            Gui gui = new Gui(moves.get(moves.size() - 1).executeMove(board));
            }

            //black queenside
            if (!this.whiteTurn && board[0][0].getMyType() == type.ROOK && !board[0][0].isWhite()
                    && board[0][4].getMyType() == type.KING && !board[0][4].isWhite()) {
                //create new puzzle with other player's turn, same board, and calculate their moves
                LinkedList<Move> oppMoves = new ChessPuzzle(!this.whiteTurn, this.board).getLegalMovesIgnoreCheck(!this.whiteTurn);
                //check that all spots along the way are not threatened and are empty
                if (!checkThreatened(0, 4, oppMoves)
                        && !checkThreatened(0, 3, oppMoves) && board[0][3].isEmpty()
                        && !checkThreatened(0, 2, oppMoves) && board[0][2].isEmpty())
                    moves.add(new CastleMove(board[0][4], 0, 4, 0, 2, board[0][0]));
            }
        }
        return moves;
    }

    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of moves for the knight at board[x][y]. NOTE: Knight can jump over pieces
     *
     * @param row   starting index on board
     * @param col   starting index on board
     * @param board chess board
     * @return list of moves
     */
    public LinkedList<Move> getKnightMoves(int row, int col, ChessPiece[][] board) {
        LinkedList<Move> moves = new LinkedList<>();
        ChessPiece knight = new ChessPiece(type.KNIGHT, this.whiteTurn);

        // (two spaces to the left and one up or down)
        // |_ _
        // |

        // Ensure move is within bounds of the board and that the new space is either empty or enemy
        if (row - 1 >= 0 && col - 2 >= 0 && (board[row - 1][col - 2].getMyType() == type.EMPTY || board[row - 1][col - 2].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, row, col, row - 1, col - 2));
        if (row + 1 < 8 && col - 2 >= 0 && (board[row + 1][col - 2].getMyType() == type.EMPTY || board[row + 1][col - 2].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, row, col, row + 1, col - 2));

        // (two spaces to the right and one up or down)
        // _ _ |
        //     |
        if (row - 1 >= 0 && col + 2 < 8 && (board[row - 1][col + 2].getMyType() == type.EMPTY || board[row - 1][col + 2].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, row, col, row - 1, col + 2));
        if (row + 1 < 8 && col + 2 < 8 && (board[row + 1][col + 2].getMyType() == type.EMPTY || board[row + 1][col + 2].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, row, col, row + 1, col + 2));

        // (two spaces up and one left or right
        //  _ _
        //   |
        //   |
        if (row - 2 >= 0 && col + 1 < 8 && (board[row - 2][col + 1].getMyType() == type.EMPTY || board[row - 2][col + 1].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, row, col, row - 2, col + 1));
        if (row - 2 >= 0 && col - 1 >= 0 && (board[row - 2][col - 1].getMyType() == type.EMPTY || board[row - 2][col - 1].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, row, col, row - 2, col - 1));

        // (two spaces down and one to left or right)
        //  |
        // _|_
        if (row + 2 < 8 && col + 1 < 8 && (board[row + 2][col + 1].getMyType() == type.EMPTY || board[row + 2][col + 1].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, row, col, row + 2, col + 1));
        if (row + 2 < 8 && col - 1 >= 0 && (board[row + 2][col - 1].getMyType() == type.EMPTY || board[row + 2][col - 1].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, row, col, row + 2, col - 1));

        return moves;
    }

    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of moves for the queen at board[x][y]
     *
     * @param x     starting index on board
     * @param y     starting index on board
     * @param board chess board
     * @return list of moves
     */
    public LinkedList<Move> getQueenMoves(int x, int y, ChessPiece[][] board) {
        LinkedList<Move> moves = new LinkedList<>();
        ChessPiece queen = new ChessPiece(type.QUEEN, this.whiteTurn);

        //----------Check Horizontal and Vertical Movement--------------//
        // Check right movement
        for (int i = x + 1; i < 8; i++) {
            if (board[i][y].getMyType() != type.EMPTY) { //check if it's not empty
                if (board[i][y].isWhite() != this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(queen, x, y, i, y)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(queen, x, y, i, y));
        }

        // Check left movement
        for (int i = x - 1; i >= 0; i--) {
            if (board[i][y].getMyType() != type.EMPTY) {
                if (board[i][y].isWhite() != this.whiteTurn)
                    moves.add(new Move(queen, x, y, i, y));
                break;
            }
            moves.add(new Move(queen, x, y, i, y));
        }

        // Check up movement
        for (int i = y + 1; i < 8; i++) {
            if (board[x][i].getMyType() != type.EMPTY) {
                if (board[x][i].isWhite() != this.whiteTurn)
                    moves.add(new Move(queen, x, y, x, i));
                break;
            }
            moves.add(new Move(queen, x, y, x, i));

        }

        // Check down movement
        for (int i = y - 1; i >= 0; i--) {
            if (board[x][i].getMyType() != type.EMPTY) {
                if (board[x][i].isWhite() != this.whiteTurn)
                    moves.add(new Move(queen, x, y, x, i));
                break;
            }
            moves.add(new Move(queen, x, y, x, i));
        }

        //----------Check Diagonal Movement--------------//
        // temp variable so as to not modify original y
        // temp variable so as to not modify original col
        // temp variable so as to not modify original col
        int tempY = y;

        // Check up / right
        for (int i = x; i >= 0; i--) {
            // Check if it is not empty
            if (tempY + 1 < 8 && i - 1 >= 0) {
                if (board[i - 1][tempY + 1].getMyType() != type.EMPTY) {
                    // Check if piece is from other side
                    if (board[i - 1][tempY + 1].isWhite() != this.whiteTurn)
                        moves.add(new Move(queen, x, y, i - 1, tempY + 1));
                    break;
                }
                moves.add(new Move(queen, x, y, i - 1, tempY + 1));
                tempY++;
            }
        }

        //rest tempY
        tempY = y;

        // check up / left
        for (int i = x; i >= 1; i--) {
            // Check if it is not empty
            if (tempY - 1 >= 0) {
                if (board[i - 1][tempY - 1].getMyType() != type.EMPTY) {
                    // Check if piece is from other side
                    if (board[i - 1][tempY - 1].isWhite() != this.whiteTurn)
                        moves.add(new Move(queen, x, y, i - 1, tempY - 1));
                    break;
                }
                moves.add(new Move(queen, x, y, i - 1, tempY - 1));
                tempY--;
            }
        }

        tempY = y;

        // down / right
        for (int i = x; i < 7; i++) {
            // Check if it is not empty
            if (tempY + 1 < 8 && i + 1 < 8) {
                if (board[i + 1][tempY + 1].getMyType() != type.EMPTY) {
                    // Check if piece is from other side
                    if (board[i + 1][tempY + 1].isWhite() != this.whiteTurn)
                        moves.add(new Move(queen, x, y, i + 1, tempY + 1));
                    break;
                }
                moves.add(new Move(queen, x, y, i + 1, tempY + 1));
                tempY++;
            }
        }

        tempY = y;

        // down / left
        for (int i = x; i < 7; i++) {
            // Check if it is not empty
            if (tempY - 1 >= 0) {
                if (board[i + 1][tempY - 1].getMyType() != type.EMPTY) {
                    // Check if piece is from other side
                    if (board[i + 1][tempY - 1].isWhite() != this.whiteTurn)
                        moves.add(new Move(queen, x, y, i + 1, tempY - 1));
                    break;
                }
                moves.add(new Move(queen, x, y, i + 1, tempY - 1));
                tempY--;
            }
        }

        return moves;
    }

    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of moves for the bishop at board[x][y]
     *
     * @param row   starting index on board
     * @param col   starting index on board
     * @param board chess board
     * @return list of moves
     */
    public LinkedList<Move> getBishopMoves(int row, int col, ChessPiece[][] board) {

        LinkedList<Move> moves = new LinkedList<>();
        ChessPiece bishop = new ChessPiece(type.BISHOP, this.whiteTurn);

        // temp variable so as to not modify original col
        int tempCol = col;

        // Check up / right
        for (int i = row; i >= 0; i--) {
            // Check if it is not empty
            if (tempCol + 1 < 8 && i - 1 >= 0) {
                if (board[i - 1][tempCol + 1].getMyType() != type.EMPTY) {
                    // Check if piece is from other side
                    if (board[i - 1][tempCol + 1].isWhite() != this.whiteTurn)
                        moves.add(new Move(bishop, row, col, i - 1, tempCol + 1));
                    break;
                }
                moves.add(new Move(bishop, row, col, i - 1, tempCol + 1));
                tempCol++;
            }
        }

        //rest tempCol
        tempCol = col;

        // check up / left
        for (int i = row; i >= 1; i--) {
            // Check if it is not empty
            if (tempCol - 1 >= 0) {
                if (board[i - 1][tempCol - 1].getMyType() != type.EMPTY) {
                    // Check if piece is from other side
                    if (board[i - 1][tempCol - 1].isWhite() != this.whiteTurn)
                        moves.add(new Move(bishop, row, col, i - 1, tempCol - 1));
                    break;
                }
                moves.add(new Move(bishop, row, col, i - 1, tempCol - 1));
                tempCol--;
            }
        }

        tempCol = col;

        // down / right
        for (int i = row; i < 7; i++) {
            // Check if it is not empty
            if (tempCol + 1 < 8 && i + 1 < 8) {
                if (board[i + 1][tempCol + 1].getMyType() != type.EMPTY) {
                    // Check if piece is from other side
                    if (board[i + 1][tempCol + 1].isWhite() != this.whiteTurn)
                        moves.add(new Move(bishop, row, col, i + 1, tempCol + 1));
                    break;
                }
                moves.add(new Move(bishop, row, col, i + 1, tempCol + 1));
                tempCol++;
            }
        }

        tempCol = col;

        // down / left
        for (int i = row; i < 7; i++) {
            // Check if it is not empty
            if (tempCol - 1 >= 0) {
                if (board[i + 1][tempCol - 1].getMyType() != type.EMPTY) {
                    // Check if piece is from other side
                    if (board[i + 1][tempCol - 1].isWhite() != this.whiteTurn)
                        moves.add(new Move(bishop, row, col, i + 1, tempCol - 1));
                    break;
                }
                moves.add(new Move(bishop, row, col, i + 1, tempCol - 1));
                tempCol--;
            }
        }


        return moves;
    }

    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of available moves for the pawn at board[x][y]
     *
     * @param row   starting index on board
     * @param col   starting index on board
     * @param board chess board
     * @return list of moves
     */
    public LinkedList<Move> getPawnMoves(int row, int col, ChessPiece[][] board) {

        LinkedList<Move> moves = new LinkedList<>();
        ChessPiece pawn = new ChessPiece(type.PAWN, this.whiteTurn);
        boolean whitePawn = board[row][col].isWhite();
        boolean firstMove = ((row == 6 && whitePawn) || (row == 1) && !whitePawn) ? true : false;

        if (whitePawn) {
            // check one forward movement
            if (row - 1 >= 0 && board[row - 1][col].getMyType() == type.EMPTY)
                moves.add(new Move(pawn, row, col, row - 1, col));

            // check two forward
            if (firstMove)
                if (board[row - 2][col].getMyType() == type.EMPTY)
                    moves.add(new Move(pawn, row, col, row - 2, col));

            //                  check captures
            // right and up
            if (row - 1 >= 0 && col + 1 < 8 && board[row - 1][col + 1].getMyType() != type.EMPTY)
                if (!board[row - 1][col + 1].isWhite())
                    moves.add(new Move(pawn, row, col, row - 1, col + 1));

            // left and up
            if (row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1].getMyType() != type.EMPTY)
                if (!board[row - 1][col - 1].isWhite())
                    moves.add(new Move(pawn, row, col, row - 1, col - 1));

        } else {
            // check one forward movement
            if (row + 1 < 8 && board[row + 1][col].getMyType() == type.EMPTY)
                moves.add(new Move(pawn, row, col, row + 1, col));

            // check two forward
            if (firstMove)
                if (board[row + 2][col].getMyType() == type.EMPTY)
                    moves.add(new Move(pawn, row, col, row + 2, col));

            //                  check captures
            // right capture
            if (row + 1 < 8 && col + 1 < 8 && board[row + 1][col + 1].getMyType() != type.EMPTY)
                if (board[row + 1][col + 1].isWhite())
                    moves.add(new Move(pawn, row, col, row + 1, col + 1));

            // left capture
            if (row + 1 < 8 && col - 1 >= 0 && board[row + 1][col - 1].getMyType() != type.EMPTY)
                if (board[row + 1][col - 1].isWhite())
                    moves.add(new Move(pawn, row, col, row + 1, col - 1));

        }

        //check moves for promotions

        ListIterator<Move> iter = moves.listIterator();
        while (iter.hasNext()) {
            Move toCheck = iter.next();
            if ((this.whiteTurn && (toCheck).x_end == 0) || (!this.whiteTurn && (toCheck).x_end == 7)) {
                iter.remove();

                iter.add(new PromotionMove(toCheck.chessPiece, toCheck.x_start, toCheck.y_start, toCheck.x_end, toCheck.y_end, new ChessPiece(type.QUEEN, this.whiteTurn)));
                iter.add(new PromotionMove(toCheck.chessPiece, toCheck.x_start, toCheck.y_start, toCheck.x_end, toCheck.y_end, new ChessPiece(type.KNIGHT, this.whiteTurn)));
            }

        }

        return moves;
    }

    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of available moves for the rook at board[x][y]
     *
     * @param x_start
     * @param y_start
     * @return
     */
    public LinkedList<Move> getRookMoves(int x_start, int y_start, ChessPiece[][] board) {

        ChessPiece rook = new ChessPiece(type.ROOK, this.whiteTurn);
        LinkedList<Move> moves = new LinkedList<>();

        // Check right movement
        for (int i = x_start + 1; i < 8; i++) {
            if (board[i][y_start].getMyType() != type.EMPTY) { //check if it's not empty
                if (board[i][y_start].isWhite() != this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(rook, x_start, y_start, i, y_start)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(rook, x_start, y_start, i, y_start));
        }

        // Check left movement
        for (int i = x_start - 1; i >= 0; i--) {
            if (board[i][y_start].getMyType() != type.EMPTY) { //check if it's not empty
                if (board[i][y_start].isWhite() != this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(rook, x_start, y_start, i, y_start)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(rook, x_start, y_start, i, y_start));
        }

        // Check up movement
        for (int i = y_start + 1; i < 8; i++) {
            if (board[x_start][i].getMyType() != type.EMPTY) { //check if it's not empty
                if (board[x_start][i].isWhite() != this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(rook, x_start, y_start, x_start, i)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(rook, x_start, y_start, x_start, i));

        }

        // Check down movement
        for (int i = y_start - 1; i >= 0; i--) {
            if (board[x_start][i].getMyType() != type.EMPTY) { //check if it's not empty
                if (board[x_start][i].isWhite() != this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(rook, x_start, y_start, x_start, i)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(rook, x_start, y_start, x_start, i));
        }
        return moves;
    }

    /**
     * Checks if the move leads to check. This method is called by ChessPuzzle.getLegalMoves().
     * Additionally this method depends on methods ChessPuzzle.getLegalMovesIgnoreCheck() and
     * Move.check(ChessPuzzle board). There are three different checkCheck methods with slightly different functionality.
     * This is bad coding practice. We know.
     *
     *
     * @param move       move to be executed
     * @param checkWhite if true, then we're looking to see if white king in check. If false, black king
     * @return boolean, true if the king is in check
     */
    public boolean checkCheck(Move move, boolean checkWhite, ChessPiece[][] board) {
        ChessPuzzle p = new ChessPuzzle(!checkWhite, move.executeMove(board)); //create a new puzzle that represents the state after the move is executed
        LinkedList<Move> oppMoves = p.getLegalMovesIgnoreCheck(p.whiteTurn); //get all the moves the opponent can make
        //iterate over those moves, see if any of them lead to capturing the king. If so, this move leads to check

        if (checkWhite)
            return p.checkThreatened(p.whiteKingXPos, p.whiteKingYPos, oppMoves);

        return p.checkThreatened(p.blackKingXPos, p.blackKingYPos, oppMoves);

    }

    public static boolean staticCheckCheck(boolean checkWhite, ChessPiece[][] board) {
        ChessPuzzle p = new ChessPuzzle(!checkWhite, board); //create a new puzzle that represents the state after the move is executed
        LinkedList<Move> oppMoves = p.getLegalMovesIgnoreCheck(p.whiteTurn); //get all the moves the opponent can make
        //iterate over those moves, see if any of them lead to capturing the king. If so, this move leads to check
        if (checkWhite)
            return p.checkThreatened(p.whiteKingXPos, p.whiteKingYPos, oppMoves);

        return p.checkThreatened(p.blackKingXPos, p.blackKingYPos, oppMoves);

    }

    public boolean checkCheckNoMove(boolean checkWhite) {
        ChessPuzzle p = new ChessPuzzle(!checkWhite, this.board); //create a new puzzle that represents the state after the move is executed
        LinkedList<Move> oppMoves = p.getLegalMovesIgnoreCheck(p.whiteTurn); //get all the moves the opponent can make
        //iterate over those moves, see if any of them lead to capturing the king. If so, this move leads to check
        if (checkWhite)
            return p.checkThreatened(p.whiteKingXPos, p.whiteKingYPos, oppMoves);

        return p.checkThreatened(p.blackKingXPos, p.blackKingYPos, oppMoves);

    }

    //Checks if a square is threatened. Used in checkCheck, but could be useful for advanced chess AI that, for example,
    //doesn't want to hang their queen.
    public boolean checkThreatened(int xpos, int ypos, LinkedList<Move> moves) {
        for (Move m : moves)
            if (m.x_end == xpos && m.y_end == ypos)
                return true;
        return false;
    }

    //creates Gui from boardstate
    public Gui makeGui() {
        if (whiteTurn)
            return new Gui(this.board, "WHITE");
        else return new Gui(this.board, "BLACK");

    }



}
