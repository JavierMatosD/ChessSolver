//CastleMove stores as its own move the King's movement, and a Move rookMove for the rook's movement.

public class CastleMove extends Move {
    Move rookMove;

    public CastleMove(ChessPiece king, int x_start, int y_start, int x_end, int y_end, ChessPiece rook) {
        super(king, x_start, y_start, x_end, y_end);

        //queenside castle
        if (y_end == 2)
            this.rookMove = new Move(rook, x_start, 0, x_end, 3);
        //kingside castle
        if (y_end == 6)
            this.rookMove = new Move(rook, x_start, 7, x_end, 5);


        //the super constructor is called for the king move

    }
    //executes both moves
    public ChessPiece[][] executeMove(ChessPiece[][] start_state) {
        return rookMove.executeMove(super.executeMove(start_state));
    }

    public String toString(){
        String toReturn = "";
        if(this.chessPiece.isWhite())
            toReturn += "White O-O";
        else toReturn += "Black O-O";
        if(this.y_end==2)
            toReturn+="-O";
        return toReturn;

    }
}
