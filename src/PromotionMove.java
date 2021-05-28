//Special move for when a pawn promotes.

public class PromotionMove extends Move {
    ChessPiece promoteTo;

    public PromotionMove(ChessPiece pawn, int x_start, int y_start, int x_end, int y_end, ChessPiece promoteTo) {
        super(pawn, x_start, y_start, x_end, y_end);

        this.promoteTo = promoteTo;

    }
    //executes promotion
    public ChessPiece[][] executeMove(ChessPiece[][] start_state) {
        ChessPiece[][] toReturn = new ChessPiece[8][8]; //copy the array. we don't want the original array modified here, we want to be making a new one only
        for(int i = 0; i < 8; i++)
            for(int j = 0; j<8; j++)
                toReturn[i][j] = start_state[i][j];
        toReturn[x_start][y_start] = new ChessPiece();
        toReturn[x_end][y_end] = this.promoteTo;

        return toReturn;
    }

    public String toString(){
        return super.toString() + " promotes to " + this.promoteTo.toString();

    }
}
