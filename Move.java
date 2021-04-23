import java.security.PublicKey;

/**
 * Move represents a single move on the chess board
 */
public class Move 
{
    ChessPiece chessPiece;
    int x;
    int y;

    Move(ChessPiece piece, int x, int y)
    {
        this.chessPiece = piece;
        this.x = x;
        this.y = y;
    }

    public boolean checkMate(ChessPiece[][] board)
    {
        if(board[this.x][this.y] == ChessPiece.B_KING || board[this.x][this.y] == ChessPiece.W_KING)
        {
            return true;
        }
        return false;
    }
}
