public class Solver
{

    /**
     * Method reads a puzzle from a text file
     * @param  string with the filename
     * @return 2D array representing the chess board
     */
    public static Enum[][] ReadAndConvert(String FILENAME)
    {
        Enum[][] board = new Enum[8][8];

        return board;
    }

    /**
     * 
     * @param 2D array of enums representing the chess board
     * @param boolean true if its whites turn
     * @return Array of Move's
     */
    public static Move[] GetLegalMoves(Enum[][] board, Boolean turn)
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
    public static Move[] SolvePuzzle(Enum[][] board, int numMoves)
    {
        Move[] winningMoves = new Move[2];

        return winningMoves;
    }



    public static void main(String[] args) 
    {
        Enum[][] board = ReadAndConvert("Puzzles/" + args[0]);
    }
}
