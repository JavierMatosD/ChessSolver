
/**
 * Move represents a single move on the chess board
 */
public class Move 
{
    ChessPiece chessPiece;
    int x_start;
    int y_start;
    int x_end;
    int y_end;

    Move(ChessPiece piece, int x_start, int y_start, int x_end, int y_end)
    {
        this.chessPiece = piece;
        this.x_start = x_start;
        this.y_start = y_start;
        this.y_end = y_end;
        this.x_end = x_end;
    }

    public boolean checkMate(ChessPiece[][] board)
    {
        if(board[this.x_end][this.y_end].getMyType() == type.KING)
        {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return chessPiece + " " + convertNumToLet(y_start) + (8-x_start) + " to " + convertNumToLet(y_end) + (8-x_end);

    }

    public static char convertNumToLet(int num){
        return (char) ((int) 'a' + num);
    }
}
