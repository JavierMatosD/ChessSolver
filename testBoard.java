import java.awt.*;
import javax.swing.*;

// testing chessboard graphics

public class testBoard {

    public static void main(String[] args) {
        // initialize chessboard
        ChessPiece[][] chessPieces = new ChessPiece[8][8];
        chessPieces[0] = new ChessPiece[] { ChessPiece.W_CASTLE, ChessPiece.W_KNIGHT, ChessPiece.W_BISHOP,
                ChessPiece.W_QUEEN, ChessPiece.W_KING, ChessPiece.W_BISHOP, ChessPiece.W_KNIGHT, ChessPiece.W_CASTLE };
        chessPieces[1] = new ChessPiece[] { ChessPiece.W_PAWN, ChessPiece.W_PAWN, ChessPiece.W_PAWN, ChessPiece.W_PAWN,
                ChessPiece.W_PAWN, ChessPiece.W_PAWN, ChessPiece.W_PAWN, ChessPiece.W_PAWN };

        for (int i = 2; i < 6; i++) {
            chessPieces[i] = new ChessPiece[] { ChessPiece.EMPTY, ChessPiece.EMPTY, ChessPiece.EMPTY, ChessPiece.EMPTY,
                    ChessPiece.EMPTY, ChessPiece.EMPTY, ChessPiece.EMPTY, ChessPiece.EMPTY };
        }
        chessPieces[6] = new ChessPiece[] { ChessPiece.B_PAWN, ChessPiece.B_PAWN, ChessPiece.B_PAWN, ChessPiece.B_PAWN,
                ChessPiece.B_PAWN, ChessPiece.B_PAWN, ChessPiece.B_PAWN, ChessPiece.B_PAWN };
        chessPieces[7] = new ChessPiece[] { ChessPiece.B_CASTLE, ChessPiece.B_KNIGHT, ChessPiece.B_BISHOP,
                ChessPiece.B_QUEEN, ChessPiece.B_KING, ChessPiece.B_BISHOP, ChessPiece.B_KNIGHT, ChessPiece.B_CASTLE };

        ChessBoard gui = new ChessBoard(chessPieces);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        // move piece
        int[] from = new int[] { 0, 1 };
        int[] to = new int[] { 2, 2 };

        gui.move(from, to);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        // move again
        from = new int[] { 6, 4 };
        to = new int[] { 5, 4 };

        gui.move(from, to);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        // move again
        from = new int[] { 1, 0 };
        to = new int[] { 2, 0 };

        gui.move(from, to);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        // move again
        from = new int[] { 7, 5 };
        to = new int[] { 2, 0 };

        gui.move(from, to);
    }
}
