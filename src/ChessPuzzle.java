//TODO: VARIABLE NAMES IN THIS CLASS ARE FUCKED, SAME FOR OTHERS. X AND Y ARE BACKWARDS I THINK, IT'S A MESS


import java.util.ArrayList;

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
    public ArrayList<Move> getLegalMoves() {

        ArrayList<Move> legalMoves = new ArrayList<Move>();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                legalMoves.addAll(getLocationLegalMoves(i, j));
            }

        return legalMoves;

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

        ChessPiece rook = new ChessPiece(type.ROOK, true);
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
        ArrayList<Move> legalMoves = getLegalMoves();
        int counter = 0;
        for (Move move : legalMoves) {
            if (move.checkMate(board)) {
                winningMoves[counter] = move;
                counter++;
            }
        }

        return winningMoves;
    }


}
