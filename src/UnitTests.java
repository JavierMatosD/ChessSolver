//import java.util.ArrayList;
//
//public class UnitTests {
//    public static boolean rookMovesTest(){
//        boolean toReturn = false;
//        ChessPiece rook = new ChessPiece(type.ROOK, true);
//        ChessPiece empty = new ChessPiece(type.EMPTY, false);
//
//        // Test Board Rook can move up to 14 different spaces.
//        ChessPiece[][] board = new ChessPiece[8][8];
//
//        // initialize board
//        for (int i = 0; i < board.length; i++)
//            for (int j = 0; j < board.length; j++)
//                board[i][j] = empty;
//
//        // place rook on board
//        board[4][3] = rook;
//        ArrayList<Move> rookMoves = ChessPuzzle.getRookMoves(4, 3, board);
//
//        // Validate
//        System.out.println(rookMoves.size());
//        if (rookMoves.size() == 14)
//            toReturn = true;
//
//        if (toReturn)
//            System.out.println("Solver.getRookMoves() works!");
//        else
//            System.out.println("Solver.getRookMoves() does not work");
//        return toReturn;
//    }
//
//    public static void printMoves(ArrayList<Move> move) {
//        for (Move move2 : move)
//            System.out.println(
//            move2.chessPiece.toString() + " from ("
//            + move2.x_start + ", " + move2.y_start + ") to ("
//            + move2.x_end + ", " + move2.y_end + ")"
//            );
//    }
//
//    public static void main(String[] args) {
//
//        boolean testRookMoves = rookMovesTest();
//
//        if (testRookMoves)
//            System.out.println("All Tests passed");
//        else
//            System.out.println("Something went wrong");
//    }
//}
