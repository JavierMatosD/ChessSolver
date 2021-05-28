import java.util.Objects;
//ChessPiece object. Has a type and a boolean for what color it is.
public class ChessPiece{

    type myType;
    boolean isWhite;

    public ChessPiece(type myType, boolean isWhite){
        this.myType = myType;
        this.isWhite = isWhite;
    }

    public ChessPiece(){ // for empty squares
        this.myType = type.EMPTY;
    }

    public type getMyType() {
        return myType;
    }

    public void setMyType(type myType) {
        this.myType = myType;
    }

    public boolean isEmpty() {
        return myType==type.EMPTY;
    }


    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    public String toString(){
        if(getMyType() == type.EMPTY)
            return "empty";
        if(isWhite)
            return "White " + this.getMyType().toString();
        return "Black " + this.getMyType().toString();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return isWhite == that.isWhite &&
                myType == that.myType;
    }

}

enum type {
    ROOK, KNIGHT, BISHOP, QUEEN, KING, PAWN, EMPTY
}

