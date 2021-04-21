import java.awt.*;
import javax.swing.*;

//testing chessboard graphics
public class testBoard {
    static final int WHITE = 0, BLACK = 1;
    static final int KING = 0, QUEEN = 1, CASTLE = 2, BISHOP = 3, KNIGHT = 4, PAWN = 5;

    // will take in enum of chess pieces at some point
    public static final int[] order = new int[] { CASTLE, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, CASTLE };

    public static void main(String[] args) {
        JPanel gui = new JPanel(new GridLayout(0, 8, 0, 0));
        ChessBoard chess = new ChessBoard(gui);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        chess.swap(gui);
    }
}
