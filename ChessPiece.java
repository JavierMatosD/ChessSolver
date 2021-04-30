//maybe we should this make this a class
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
}

enum type {
    ROOK, KNIGHT, BISHOP, QUEEN, KING, PAWN, EMPTY //replace empty with null?
}