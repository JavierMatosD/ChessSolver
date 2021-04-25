/**
 * 
 */
public class Solver
{

    /**
     * Method reads a puzzle from a text file
     * @param  string with the filename
     * @return 2D array representing the chess board
     */
    public static ChessPiece[][] ReadAndConvert(String FILENAME)
    {
        ChessPiece[][] board = new ChessPiece[8][8];

        return board;
    }

    /**
     * Legal moves for each piece
     * Kings  : One square space in any direction as long as the square cannot be attacked by another piece.
     *          Special move Kings Castling
     * Queens : Diagonally, horizontally, or vertically any number of spaces. Cannot jump over pieces. 
     * Rooks  : Horizontally or vertically any number of squares. They are Unable to jump over pieces. 
     *          Rooks move when king castles.
     * Bishops: Diagonally any number of squares. They are unable to jump over pieces.
     * Knights: Move in an L shape: 2 squares horizontally or vertically. They can jump over pieces.
     * Pawns  : Vertically forward one move unless they haven't moved before. 
     *          Pawns Capture one square in forwards diagonal movement
     * @param 2D array of enums representing the chess board
     * @param boolean true if its whites turn
     * @return Array of Move's
     */
    public static Move[] GetLegalMoves(ChessPiece[][] board, Boolean turn)
    {

        Move[] legalMoves = new Move[16];

        return legalMoves;

    }

    /**
     * 
     * @param 2D array of enums representing the chess board
     * @param int number of moves to Checkmate
     * @return Array of Moves for Checkmate
     */
    public static Move[] SolvePuzzle(ChessPiece[][] board, int numMoves)
    {
        Move[] winningMoves = new Move[2];

        //Brute force implementation
        Move[] legalMoves = GetLegalMoves(board, true);
        int counter = 0;
        for (Move move : legalMoves) 
        {
            if (move.checkMate(board))
            {
                winningMoves[counter] = move;
                counter++;
            }
        }

        return winningMoves;
    }



    public static void main(String[] args) 
    {
        //Enum[][] board = ReadAndConvert("Puzzles/" + args[0]);
        
    }
}
