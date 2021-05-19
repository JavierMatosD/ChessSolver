import java.util.ArrayList;

// testing chessboard graphics

public class testBoard {

    public static void main(String[] args) {
        // // initialize chessboard
        // ChessPiece[][] chessPieces = new ChessPiece[8][8];
        // chessPieces[0] = new ChessPiece[] { new ChessPiece(type.ROOK, true), new ChessPiece(type.KNIGHT, true),
        //         new ChessPiece(type.BISHOP, true), new ChessPiece(type.QUEEN, true), new ChessPiece(type.KING, true),
        //         new ChessPiece(type.BISHOP, true), new ChessPiece(type.KNIGHT, true), new ChessPiece(type.ROOK, true) };
        // chessPieces[1] = new ChessPiece[] { new ChessPiece(type.PAWN, true), new ChessPiece(type.PAWN, true),
        //         new ChessPiece(type.PAWN, true), new ChessPiece(type.PAWN, true), new ChessPiece(type.PAWN, true),
        //         new ChessPiece(type.PAWN, true), new ChessPiece(type.PAWN, true), new ChessPiece(type.PAWN, true) };

        // for (int i = 2; i < 6; i++) {
        //     chessPieces[i] = new ChessPiece[] { new ChessPiece(), new ChessPiece(), new ChessPiece(), new ChessPiece(),
        //             new ChessPiece(), new ChessPiece(), new ChessPiece(), new ChessPiece() };
        // }
        // chessPieces[6] = new ChessPiece[] { new ChessPiece(type.PAWN, false), new ChessPiece(type.PAWN, false),
        //         new ChessPiece(type.PAWN, false), new ChessPiece(type.PAWN, false), new ChessPiece(type.PAWN, false),
        //         new ChessPiece(type.PAWN, false), new ChessPiece(type.PAWN, false), new ChessPiece(type.PAWN, false) };
        // chessPieces[7] = new ChessPiece[] { new ChessPiece(type.ROOK, false), new ChessPiece(type.KNIGHT, false),
        //         new ChessPiece(type.BISHOP, false), new ChessPiece(type.QUEEN, false), new ChessPiece(type.KING, false),
        //         new ChessPiece(type.BISHOP, false), new ChessPiece(type.KNIGHT, false),
        //         new ChessPiece(type.ROOK, false) };

        // // who's turn was it ? "BLACK"/"WHITE"
        // Gui gui = new Gui(chessPieces, "WHITE");

        // // arraylist of moves
        // ArrayList<Move> moves = new ArrayList<>();
        // moves.add(new Move(chessPieces[0][1], 0, 0, 3, 0));
        // gui.moves = moves;
        MainGui gui = new MainGui();
        
    }
}
