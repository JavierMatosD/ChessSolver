// testing chessboard graphics

public class testBoard {

    public static void main(String[] args) {
        // initialize chessboard
        ChessPiece[][] chessPieces = new ChessPiece[8][8];
        chessPieces[0] = new ChessPiece[] { new ChessPiece(type.ROOK, true), new ChessPiece(type.KNIGHT, true), new ChessPiece(type.BISHOP, true),
                new ChessPiece(type.QUEEN, true),new ChessPiece(type.KING, true),new ChessPiece(type.BISHOP, true),
                new ChessPiece(type.KNIGHT, true),new ChessPiece(type.ROOK, true) };
        chessPieces[1] = new ChessPiece[] { new ChessPiece(type.PAWN, true), new ChessPiece(type.PAWN, true), new ChessPiece(type.PAWN, true),
                new ChessPiece(type.PAWN, true),new ChessPiece(type.PAWN, true),new ChessPiece(type.PAWN, true),
                new ChessPiece(type.PAWN, true),new ChessPiece(type.PAWN, true) };

        for (int i = 2; i < 6; i++) {
            chessPieces[i] = new ChessPiece[] { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                    new ChessPiece(), new ChessPiece(), new ChessPiece(),
                    new ChessPiece(), new ChessPiece()};
        }
        chessPieces[6] = new ChessPiece[] { new ChessPiece(type.PAWN, false), new ChessPiece(type.PAWN, false), new ChessPiece(type.PAWN, false),
                new ChessPiece(type.PAWN, false),new ChessPiece(type.PAWN, false),new ChessPiece(type.PAWN, false),
                new ChessPiece(type.PAWN, false),new ChessPiece(type.PAWN, false) };
        chessPieces[7] = new ChessPiece[] { new ChessPiece(type.ROOK, false), new ChessPiece(type.KNIGHT, false), new ChessPiece(type.BISHOP, false),
                new ChessPiece(type.QUEEN, false),new ChessPiece(type.KING, false),new ChessPiece(type.BISHOP, false),
                new ChessPiece(type.KNIGHT, false),new ChessPiece(type.ROOK, false) };

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
