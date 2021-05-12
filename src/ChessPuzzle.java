
import java.util.ArrayList;
import java.util.Iterator;
/**
 *
 */
public class ChessPuzzle {

    private boolean whiteTurn;
    public ChessPiece[][] board;


    public ChessPuzzle(boolean whiteTurn, ChessPiece[][] board) {
        this.board = board;
        this.whiteTurn = whiteTurn;

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
    public ArrayList<Move> getLegalMovesIgnoreCheck() {

        ArrayList<Move> legalMoves = new ArrayList<Move>();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                legalMoves.addAll(getLocationLegalMoves(i, j));
            }
        return legalMoves;
    }

    // Same as above, except this version ensure you can't make any moves that would put yourself in check.
    // The above version is for checking for check; that is, you can't make a move putting yourself in check, even if the move your opp
    // would make to take your king would put himself in check too
    public ArrayList<Move> getLegalMoves() {

        ArrayList<Move> moves = new ArrayList<Move>();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                moves.addAll(getLocationLegalMoves(i, j));
            }

        Iterator itr = moves.iterator();
        while (itr.hasNext()) {
            if (checkCheck((Move) itr.next(), this.whiteTurn)) //if a move leads to check for the player whose turn it is, remove it
                itr.remove();
        }
        return moves;
    }
    
    /**
     * Method returns a list of legal moves for the chess piece in chessboard[x_start][y_start]
     * @param x_start x position of chess piece
     * @param y_start y position of chess piece
     * @return List of legal moves 
     */
    public ArrayList<Move> getLocationLegalMoves(int x_start, int y_start) {
        ArrayList<Move> moves = new ArrayList<>();
        if (board[x_start][y_start].isWhite() != this.whiteTurn)
            return moves;
        switch (board[x_start][y_start].getMyType()) {
            case ROOK: //assuming the player is black
                moves.addAll(getRookMoves(x_start, y_start));
                break;
            case BISHOP:
                moves.addAll(getBishopMoves(x_start, y_start, board));
                break;
            case KING:
                moves.addAll(getKingMoves(x_start, y_start, board));
                break;
            case KNIGHT:
                moves.addAll(getKnightMoves(x_start, y_start, board));
                break;
            case PAWN:
                moves.addAll(getPawnMoves(x_start, y_start, board));
                break;
            case QUEEN:
                moves.addAll(getQueenMoves(x_start, y_start, board));
                break;
            case EMPTY:
                break;
            default:
                break;
        }

        return moves;
    }
    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of moves for the king at board[x][y]. NOTE: Knight can jump over pieces
     * @param x starting index on board
     * @param y starting index on board
     * @param board chess board
     * @return list of moves
     */
    public ArrayList<Move> getKingMoves(int x, int y, ChessPiece[][] board) {
        ArrayList<Move> moves = new ArrayList<>();
        ChessPiece king = new ChessPiece(type.KING, this.whiteTurn);

        // Check left and right
        // Ensure that moves is within the bounds of the board and that the space is either empty or enemy
        if (x + 1 < 8 && (board[x + 1][y].getMyType() == type.EMPTY || board[x + 1][y].isWhite() != this.whiteTurn))
            moves.add(new Move(king, x, y, x + 1, y));
        if (x - 1 >= 0 && (board[x - 1][y].getMyType() == type.EMPTY || board[x - 1][y].isWhite() != this.whiteTurn))
            moves.add(new Move(king, x, y, x - 1, y));
        
        // Check up and down
        if (y + 1 < 8 && (board[x][y + 1].getMyType() == type.EMPTY || board[x][y + 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, x, y, x, y + 1));
        if (y - 1 >= 0 && (board[x][y - 1].getMyType() == type.EMPTY || board[x][y - 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, x, y, x, y - 1));
        
        // Check diagonal up/left and up/right
        if (x + 1 < 8 && y + 1 < 8 && (board[x + 1][y + 1].getMyType() == type.EMPTY || board[x + 1][y + 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, x, y, x + 1, y + 1));
        if (x - 1 >= 0 && y + 1 < 8&& (board[x - 1][y + 1].getMyType() == type.EMPTY || board[x - 1][y + 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, x, y, x - 1, y + 1));
        
        // Check diagonal down/left and down/right
        if (x - 1 >= 0 && y - 1 >=0 && (board[x-1][y-1].getMyType() == type.EMPTY || board[x-1][y-1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, x, y, x - 1, y - 1));
        if (x + 1 < 8 && y - 1 >= 0 && (board[x + 1][y - 1].getMyType() == type.EMPTY || board[x + 1][y - 1].isWhite() != this.whiteTurn))
            moves.add(new Move(king, x, y, x + 1, y - 1));
        
        return moves;
    }
    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of moves for the knight at board[x][y]. NOTE: Knight can jump over pieces
     * @param x starting index on board
     * @param y starting index on board
     * @param board chess board
     * @return list of moves
     */
    public ArrayList<Move> getKnightMoves(int x, int y, ChessPiece[][] board) {
        ArrayList<Move> moves = new ArrayList<>();
        ChessPiece knight = new ChessPiece(type.KNIGHT, this.whiteTurn);

        // (two spaces to the left and one up or down)
        // |_ _ 
        // |

        // Ensure move is within bounds of the board and that the new space is either empty or enemy
        if (x - 2 >= 0 && y + 1 < 8 && (board[x-2][y+1].getMyType() == type.EMPTY || board[x-2][y+1].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, x, y, x - 2, y + 1));
        if (x - 2 >= 0 && y - 1 >= 0 && (board[x-2][y-1].getMyType() == type.EMPTY || board[x-2][y-1].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, x, y, x - 2, y - 1));
        
        // (two spaces to the right and one up or down)
        // _ _ |
        //     |
        if (x + 2 < 8 && y + 1 < 8 && (board[x+2][y+1].getMyType() == type.EMPTY || board[x+2][y+1].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, x, y, x + 2, y + 1));
        if (x + 2 < 8 && y - 1 >= 0 && (board[x+2][y-1].getMyType() == type.EMPTY || board[x + 2][y - 1].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, x, y, x + 2, y - 1));

        // (two spaces up and one left or right
        //  _ _
        //   |
        //   |
        if (x - 1 >= 0 &&  y + 2 < 8 && (board[x-1][y+2].getMyType() == type.EMPTY || board[x - 1][y + 2].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, x, y, x - 1, y + 2));
        if (x + 1 < 8 && y + 2 < 8  && (board[x+1][y+2].getMyType() == type.EMPTY || board[x + 1][y + 2].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, x, y, x + 1, y + 2));

        // (two spaces down and one to left or right)
        //  |
        // _|_
        if (x - 1 >= 0 && y - 2 >= 0 && (board[x-1][y-2].getMyType() == type.EMPTY || board[x - 1][y - 2].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, x, y, x - 1, y - 2));
        if (x + 1 < 8 && y - 2 >= 0 && (board[x+1][y-2].getMyType() == type.EMPTY || board[x + 1][y - 2].isWhite() != this.whiteTurn))
            moves.add(new Move(knight, x, y, x + 1, y - 2));
        
        return moves;
    }
    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of moves for the queen at board[x][y]
     * @param x starting index on board
     * @param y starting index on board
     * @param board chess board
     * @return list of moves
     */
    public ArrayList<Move> getQueenMoves(int x, int y, ChessPiece[][] board) {
        ArrayList<Move> moves = new ArrayList<>();
        ChessPiece queen = new ChessPiece(type.QUEEN, this.whiteTurn);

        //----------Check Horizontal and Vertical Movement--------------//
                // Check right movement
        for (int i = x + 1; i < 8; i++) {
            if (board[i][y].getMyType() != type.EMPTY){ //check if it's not empty
                if(board[i][y].isWhite()!=this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(queen, x, y, i, y)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(queen, x, y, i, y));
        }

        // Check left movement
        for (int i = x - 1; i >= 0; i--) {
            if (board[i][y].getMyType() != type.EMPTY){ 
                if(board[i][y].isWhite()!=this.whiteTurn) 
                    moves.add(new Move(queen, x, y, i, y)); 
                break; 
            }
            moves.add(new Move(queen, x, y, i, y));
        }

        // Check up movement
        for (int i = y + 1; i < 8; i++) {
            if (board[x][i].getMyType() != type.EMPTY){ 
                if(board[x][i].isWhite()!=this.whiteTurn) 
                    moves.add(new Move(queen, x, y, x, i)); 
                break; 
            }
            moves.add(new Move(queen, x, y, x, i));

        }

        // Check down movement
        for (int i = y - 1; i >= 0; i--) {
            if (board[x][i].getMyType() != type.EMPTY){ 
                if(board[x][i].isWhite()!=this.whiteTurn) 
                    moves.add(new Move(queen, x, y, x, i)); 
                break; 
            }
            moves.add(new Move(queen, x, y, x, i));
        }

        //----------Check Diagonal Movement--------------//
        // temp variable so as to not modify original y
        int tempY = y;

        // Check up / right
        for (int i = x; i < board.length; i++) {
            // Check if it is not empty
            if (board[i + 1][tempY + 1].getMyType() != type.EMPTY) {
                // Check if piece is from other side
                if (board[i + 1][tempY + 1].isWhite() != this.whiteTurn)
                    moves.add(new Move(queen, x, y, i + 1, tempY + 1));
                break;
            }
            moves.add(new Move(queen, x, y, i + 1, tempY + 1));
            tempY++;
        }

        //rest tempY
        tempY = y; 

        // check up / left
        for (int i = x; i < board.length; i--) {
            // Check if it is not empty
            if (board[i - 1][tempY + 1].getMyType() != type.EMPTY) {
                // Check if piece is from other side
                if (board[i - 1][tempY + 1].isWhite() != this.whiteTurn)
                    moves.add(new Move(queen, x, y, i - 1, tempY + 1));
                break;
            }
            moves.add(new Move(queen, x, y, i - 1, tempY + 1));
            tempY++;
        }

        tempY = y; 
        
        // down / right
        for (int i = x; i < board.length; i++) {
            // Check if it is not empty
            if (board[i + 1][tempY - 1].getMyType() != type.EMPTY) {
                // Check if piece is from other side
                if (board[i + 1][tempY - 1].isWhite() != this.whiteTurn)
                    moves.add(new Move(queen, x, y, i + 1, tempY - 1));
                break;
            }
            moves.add(new Move(queen, x, y, i + 1, tempY - 1));
            tempY++;
        }

        tempY = y; 

        // down / left
        for (int i = x; i < board.length; i--) {
            // Check if it is not empty
            if (board[i - 1][tempY - 1].getMyType() != type.EMPTY) {
                // Check if piece is from other side
                if (board[i - 1][tempY - 1].isWhite() != this.whiteTurn)
                    moves.add(new Move(queen, x, y, i - 1, tempY - 1));
                break;
            }
            moves.add(new Move(queen, x, y, i - 1, tempY - 1));
            tempY++;
        }
        return moves;
    }
    
    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of moves for the bishop at board[x][y]
     * @param x starting index on board
     * @param y starting index on board
     * @param board chess board
     * @return list of moves
     */
    public ArrayList<Move> getBishopMoves(int x, int y, ChessPiece[][] board) {
        
        ArrayList<Move> moves = new ArrayList<>();
        ChessPiece bishop = new ChessPiece(type.BISHOP, this.whiteTurn);

        // temp variable so as to not modify original y
        int tempY = y;

        // Check up / right
        for (int i = x; i < board.length; i++) {
            // Check if it is not empty
            if (board[i + 1][tempY + 1].getMyType() != type.EMPTY) {
                // Check if piece is from other side
                if (board[i + 1][tempY + 1].isWhite() != this.whiteTurn)
                    moves.add(new Move(bishop, x, y, i + 1, tempY + 1));
                break;
            }
            moves.add(new Move(bishop, x, y, i + 1, tempY + 1));
            tempY++;
        }

        //rest tempY
        tempY = y; 

        // check up / left
        for (int i = x; i < board.length; i--) {
            // Check if it is not empty
            if (board[i - 1][tempY + 1].getMyType() != type.EMPTY) {
                // Check if piece is from other side
                if (board[i - 1][tempY + 1].isWhite() != this.whiteTurn)
                    moves.add(new Move(bishop, x, y, i - 1, tempY + 1));
                break;
            }
            moves.add(new Move(bishop, x, y, i - 1, tempY + 1));
            tempY++;
        }

        tempY = y; 
        
        // down / right
        for (int i = x; i < board.length; i++) {
            // Check if it is not empty
            if (board[i + 1][tempY - 1].getMyType() != type.EMPTY) {
                // Check if piece is from other side
                if (board[i + 1][tempY - 1].isWhite() != this.whiteTurn)
                    moves.add(new Move(bishop, x, y, i + 1, tempY - 1));
                break;
            }
            moves.add(new Move(bishop, x, y, i + 1, tempY - 1));
            tempY++;
        }

        tempY = y; 

        // down / left
        for (int i = x; i < board.length; i--) {
            // Check if it is not empty
            if (board[i - 1][tempY - 1].getMyType() != type.EMPTY) {
                // Check if piece is from other side
                if (board[i - 1][tempY - 1].isWhite() != this.whiteTurn)
                    moves.add(new Move(bishop, x, y, i - 1, tempY - 1));
                break;
            }
            moves.add(new Move(bishop, x, y, i - 1, tempY - 1));
            tempY++;
        }


        return moves;
    }
    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of available moves for the pawn at board[x][y]
     * @param x starting index on board
     * @param y starting index on board
     * @param board chess board
     * @return list of moves
     */
    public ArrayList<Move> getPawnMoves(int x, int y, ChessPiece[][] board) {

        ArrayList<Move> moves = new ArrayList<>();
        ChessPiece pawn = new ChessPiece(type.PAWN, this.whiteTurn);
        boolean firstMove = (x == 6) ?  true : false;


        // check one forward movement
        if (board[x][y + 1].getMyType() == type.EMPTY) {
            moves.add(new Move(pawn, x, y, x, y + 1));
            // check two forward
            if (firstMove)
                if (board[x][y + 2].getMyType() == type.EMPTY)
                    moves.add(new Move(pawn, x, y, x, y + 2));
        }

        //                  check captures
        // right and up
        if (board[x + 1][y + 1].getMyType() != type.EMPTY)
            // check if piece is from other side
            if (board[x + 1][y + 1].isWhite != this.whiteTurn)
                moves.add(new Move(pawn, x, y, x + 1, y + 1));
        
        // left and up
        if (board[x - 1][y + 1].getMyType() != type.EMPTY)
            if (board[x - 1][y + 1].isWhite != this.whiteTurn)
                moves.add(new Move(pawn, x, y, x - 1, y + 1));

        return moves;
    }
    
    /**
     * Method is called by ChessPuzzle.getLocationLegalMoves(int x, int y) and returns the list
     * of available moves for the rook at board[x][y]
     * @param x_start
     * @param y_start
     * @return
     */
    public ArrayList<Move> getRookMoves(int x_start, int y_start) {

        ChessPiece rook = new ChessPiece(type.ROOK, this.whiteTurn);
        ArrayList<Move> moves = new ArrayList<>();

        // Check right movement
        for (int i = x_start + 1; i < 8; i++) {
            if (board[i][y_start].getMyType() != type.EMPTY){ //check if it's not empty
                if(board[i][y_start].isWhite()!=this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(rook, x_start, y_start, i, y_start)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(rook, x_start, y_start, i, y_start));
        }

        // Check left movement
        for (int i = x_start - 1; i >= 0; i--) {
            if (board[i][y_start].getMyType() != type.EMPTY){ //check if it's not empty
                if(board[i][y_start].isWhite()!=this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(rook, x_start, y_start, i, y_start)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(rook, x_start, y_start, i, y_start));
        }

        // Check up movement
        for (int i = y_start + 1; i < 8; i++) {
            if (board[x_start][i].getMyType() != type.EMPTY){ //check if it's not empty
                if(board[x_start][i].isWhite()!=this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(rook, x_start, y_start, x_start, i)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(rook, x_start, y_start, x_start, i));

        }

        // Check down movement
        for (int i = y_start - 1; i >= 0; i--) {
            if (board[x_start][i].getMyType() != type.EMPTY){ //check if it's not empty
                if(board[x_start][i].isWhite()!=this.whiteTurn) //check if it's a piece from the other side (capturable)
                    moves.add(new Move(rook, x_start, y_start, x_start, i)); //if so, add the new move
                break; //either way, break out of the loop
            }
            moves.add(new Move(rook, x_start, y_start, x_start, i));
        }
        return moves;
    }

    /**
     * Method called by main to solve the chess puzzle. Additionally, this method
     * depends on ChessPuzzle.getLegalMovesIgnoreCheck()
     * @param board    2D  array of ChessPiece's representing the chess board
     * @param numMoves int number of moves to Checkmate
     * @return Array of Moves for Checkmate
     */
    public Move[] SolvePuzzle(ChessPiece[][] board, int numMoves) {
        Move[] winningMoves = new Move[2];

        //Brute force implementation
        ArrayList<Move> legalMoves = getLegalMovesIgnoreCheck();
        int counter = 0;
        for (Move move : legalMoves) {
            if (move.check(board)) {
                winningMoves[counter] = move;
                counter++;
            }
        }

        return winningMoves;
    }

    /** 
     * Checks if the move leads to check. This method is called by ChessPuzzle.getLegalMoves().
     * Additionally this method depends on methods ChessPuzzle.getLegalMovesIgnoreCheck() and
     * Move.check(ChessPuzzle board)
     * //TODO: I think the functionality here should be split up
     * @param move    move to be executed
     * @param checkWhite if true, then we're looking to see if white king in check. If false, black king
     * @return boolean, true if the king is in check
     */
    public boolean checkCheck(Move move, boolean checkWhite){
        ChessPuzzle p = new ChessPuzzle(!checkWhite, move.executeMove(this.board)); //create a new puzzle that represents the state after the move is executed
        ArrayList<Move> oppMoves = p.getLegalMovesIgnoreCheck(); //get all the moves the opponent can make
        for(Move m: oppMoves) //iterate over those moves, see if any of them lead to capturing the king. If so, this move leads to check
            if(m.check(p.board)){
                return true;
            }
        return false;
    }


}
