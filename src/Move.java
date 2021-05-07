
/**
 * Move represents a single move on the chess board
 */

//TODO: perhaps move could include an instance variable pointing to a parent ChessPuzzle?
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

    public ChessPiece[][] executeMove(ChessPiece[][] start_state){
        if(!start_state[x_start][y_start].equals(this.chessPiece)) {
            System.out.println("Tried to execute move " + this + " but " + start_state[x_start][y_start] + " was the piece found in the starting spot");
            System.exit(1);
        }
        ChessPiece[][] toReturn = new ChessPiece[8][8]; //copy the array. we don't want the original array modified here, we want to be making a new one only
        for(int i = 0; i < 8; i++)
            for(int j = 0; j<8; j++)
                toReturn[i][j] = start_state[i][j];
        toReturn[x_start][y_start] = new ChessPiece();
        toReturn[x_end][y_end] = this.chessPiece;
        return toReturn;
    }

    public boolean check(ChessPiece[][] board)
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
