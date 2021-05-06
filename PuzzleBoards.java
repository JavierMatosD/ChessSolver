public class PuzzleBoards{

ChessPiece[][] templateBoard = new ChessPiece[8][8]


templateBoard[0] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

templateBoard[1] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

templateBoard[2] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

templateBoard[3] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

templateBoard[4] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

templateBoard[5] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

templateBoard[6] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

templateBoard[7] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };



ChessPiece[][] puzzleone = new ChessPiece[8][8]


puzzleone[0] = { new ChessPiece(ROOK, false), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(KING, false) };

puzzleone[1] = { new ChessPiece(), new ChessPiece(), new ChessPiece(PAWN, false),
                     new ChessPiece(), new ChessPiece(KNIGHT, true), new ChessPiece(PAWN, false),
                     new ChessPiece(ROOK, false), new ChessPiece() };

puzzleone[2] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(PAWN, false), new ChessPiece(), new ChessPiece(QUEEN, true),
                     new ChessPiece(), new ChessPiece() };

puzzleone[3] = { new ChessPiece(PAWN, false), new ChessPiece(PAWN, false), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(PAWN, false), new ChessPiece(),
                     new ChessPiece(PAWN, true), new ChessPiece() };

puzzleone[4] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(PAWN, true), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

puzzleone[5] = { new ChessPiece(), new ChessPiece(), new ChessPiece(QUEEN, false),
                     new ChessPiece(KNIGHT, false), new ChessPiece(PAWN, true), new ChessPiece(),
                     new ChessPiece(KING, true), new ChessPiece() };

puzzleone[6] = { new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

puzzleone[7] = { new ChessPiece(ROOK, true), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece(), new ChessPiece(),
                     new ChessPiece(), new ChessPiece() };

}
