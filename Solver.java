/**
 *
 */
public class Solver {

    private boolean whiteTurn;

    /**
     * Method reads a puzzle from a text file
     *
     * @param FILENAME String with the filename
     * @return 2D array representing the chess board
     */
    public static ChessPiece[][] ReadAndConvert(String FILENAME) {
        ChessPiece[][] board = new ChessPiece[8][8];

        return board;
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
     * @param board    2darray of enums representing the chess board
     * @param turn true if its whites turn
     * @return Array of Move's
     */
    public static Move[] GetLegalMoves(ChessPiece[][] board, Boolean turn) {

        Move[][][] legalMovesByLocation = new Move[8][8][];

        for (int i = 0; i < 8; i++)
            for (int j = 0; i < 8; i++)
                legalMovesByLocation[i][j] = getLocationLegalMoves(i, j, board);


        Move[] legalMoves = new Move[16];
        return legalMoves;

    }

    public static Move[] getLocationLegalMoves(int i, int j, ChessPiece[][] board) {
        switch (board[i][j].getMyType()) {
            case PAWN: //assuming the player is black

        }

        return new Move[0];
    }

    public static Move[] getPawnMoves(int i, int j, ChessPiece[][] board) {

        return new Move[0];
    }

    public static Move[] getRookMoves(int x, int y, ChessPiece[][] board) {
        int counter = 0;
        Move[] toReturn = new Move[14]; //max possible moves for a rook is 14
        for (int i = x; i < 8; i++) {
            if (board[i][y].getMyType() != type.EMPTY)
                break;
            toReturn[counter] = new Move(board[i][y], x, y, i, y);
            counter++;
        }
        for (int i = x; i >= 0; i--) {
            if (board[i][y].getMyType() != type.EMPTY)
                break;
            toReturn[counter] = new Move(board[i][y], x, y, i, y);
            counter++;
        }
        for (int i = y; i < 8; i++) {
            if (board[x][i].getMyType() != type.EMPTY)
                break;
            toReturn[counter] = new Move(board[x][i], x, y, x, i);
            counter++;
        }
        for (int i = y; i >= 0; i--) {
            if (board[x][i].getMyType() != type.EMPTY)
                break;
            toReturn[counter] = new Move(board[x][i], x, y, x, i);
            counter++;
        }


        return toReturn;
    }

    /**
     * @param 2D  array of enums representing the chess board
     * @param int number of moves to Checkmate
     * @return Array of Moves for Checkmate
     */
    public static Move[] SolvePuzzle(ChessPiece[][] board, int numMoves) {
        Move[] winningMoves = new Move[2];

        //Brute force implementation
        Move[] legalMoves = GetLegalMoves(board, true);
        int counter = 0;
        for (Move move : legalMoves) {
            if (move.checkMate(board)) {
                winningMoves[counter] = move;
                counter++;
            }
        }

        return winningMoves;
    }


    public static void main(String[] args) {
        //Enum[][] board = ReadAndConvert("Puzzles/" + args[0]);

    }
}
