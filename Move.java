import java.security.PublicKey;

/**
 * Move represents a single move on the chess board
 */
public class Move 
{
    Enum chessPiece;
    int x;
    int y;

    Move(Enum piece, int x, int y)
    {
        this.chessPiece = piece;
        this.x = x;
        this.y = y;
    }
}
