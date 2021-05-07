//TODO: VARIABLE NAMES IN THIS CLASS ARE FUCKED, SAME FOR OTHERS. X AND Y ARE BACKWARDS I THINK, IT'S A MESS


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
        while(itr.hasNext()){
            if(checkCheck((Move) itr.next(), this.whiteTurn)) //if a move leads to check for the player whose turn it is, remove it
                itr.remove();
        }
        return moves;
    }
    public ArrayList<Move> getLocationLegalMoves(int x_start, int y_start) {
        ArrayList<Move> moves = new ArrayList<>();
        if(board[x_start][y_start].isWhite()!=this.whiteTurn)
            return moves;
        switch (board[x_start][y_start].getMyType()) {
            case ROOK: { //assuming the player is black
                moves.addAll(getRookMoves(x_start, y_start));
            }
        }

        return moves;
    }

    public static Move[] getPawnMoves(int i, int j, ChessPiece[][] board) {

        return new Move[0];
    }

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
     * @param board    2D  array of enums representing the chess board
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

    /**    checks if the move leads to check
     * @param move    move to be executed
     * @param checkWhite if true, then we're looking to see if white king in check. If false, black king
     * @return boolean, true if the king is in check
     * //TODO: I think the functionality here should be split up
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
