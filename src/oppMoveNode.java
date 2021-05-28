import java.util.LinkedList;

//OppMoveNode and MyMoveNode implementation, used before the MoveTree class was created to hold both.

@Deprecated
public class oppMoveNode {
    myMoveNode parent; //null for root node
    LinkedList<myMoveNode> children;
    Move value; //null for root node
    boolean checkMate; //true if true for any children
    ChessPuzzle puzzle;
    static boolean isSolved = false;

    public oppMoveNode(Move value, myMoveNode parent, ChessPuzzle puzzle) {
        this.value = value;
        this.parent = parent;
        this.puzzle = puzzle;
        this.children = new LinkedList<>();
    }

    public ChessPiece[][] getBoardState() {
        if (this.value == null) //root node
            return this.puzzle.board;
        return this.value.executeMove(this.parent.getBoardState());
    }

    public void setChildren() {

        ChessPiece[][] boardState = this.getBoardState();
        LinkedList<Move> moves = new ChessPuzzle(this.puzzle.whiteTurn, boardState).getLegalMoves();
        for (Move m : moves) {
            children.add(new myMoveNode(m, this, this.puzzle));
        }
    }

    //if any children lead to mate, return true
    public boolean checkChildren() {
        for (myMoveNode child : children) {
            if (child.checkMate)
                return true;
        }
        return false;
    }

    //sets this node to checkmate. Checks parents too. Triggered by child
    public void setCheckMate() {
        this.checkMate = true;
        if (parent != null)
            parent.triggeredCheckCheckMate();
        else isSolved = true;
    }

    public LinkedList<LinkedList<Move>> getSolutions() {
        LinkedList<LinkedList<Move>> solutions = new LinkedList<>();
        for (myMoveNode child : children) {
            if (child.checkMate) {
                solutions.addAll(child.getSolutions()); //get solutions from child
                for (LinkedList<Move> solution : solutions) //prepend this node's move to each solution
                    if (this.value != null)
                        solution.add(0, this.value);
                break; //break out of loop, since checkmating move found. To find all checkmate moves, comment this out
            }
        }
        return solutions;
    }

    public String toString() {
        if (this.value == null)
            return "null";
        return this.value.toString();

    }

    static class myMoveNode {
        oppMoveNode parent;
        LinkedList<oppMoveNode> children;
        Move value;
        boolean checkMate; //set to true if true for all children
        ChessPuzzle puzzle;

        public myMoveNode(Move value, oppMoveNode parent, ChessPuzzle puzzle) {
            this.value = value;
            this.parent = parent;
            this.puzzle = puzzle;
            this.children = new LinkedList<>();
        }

        public String toString() {
            return this.value.toString();

        }

        //state after the value move and all parents are executed
        public ChessPiece[][] getBoardState() {
            return this.value.executeMove(this.parent.getBoardState());
        }


        //sets the children, or sets checkMate to true and parent checkmate to true
        public void setChildren() {
            ChessPiece[][] boardState = this.getBoardState();
            ChessPuzzle p = new ChessPuzzle(!this.puzzle.whiteTurn, boardState); //the state of the board after your move
            LinkedList<Move> oppMoves = p.getLegalMoves(); //all of the opponent's legal moves
            if (oppMoves.size() == 0 && p.checkCheckNoMove(p.whiteTurn)) { //if opponent is in check and has no legal moves
                this.checkMate = true;
                parent.setCheckMate(); //set parent to checkmate, since you know if parent move is made, child move can mate them
                return;
            }
            for (Move m : oppMoves) {
                children.add(new oppMoveNode(m, this, this.puzzle));
            }
            this.checkMate = false;
        }

        //if all children lead to mate, return true
        public boolean checkChildren() {
            for (oppMoveNode child : children) {
                if (!child.checkMate)
                    return false;
            }
            return true;
        }

        //check if this node is checkmate by checking if all children are checkmate. Triggered by children.
        public void triggeredCheckCheckMate() {
            if (checkChildren()){
                this.checkMate = true;
            parent.setCheckMate();
        }}

        public LinkedList<LinkedList<Move>> getSolutions() {
            LinkedList<LinkedList<Move>> solutions = new LinkedList<>(); //for debugging, can be removed
            if (this.children.size() == 0) { //if this node has no children, it's a mate in 1 move
                LinkedList<Move> toAdd = new LinkedList<>();
                toAdd.add(this.value);
                solutions = new LinkedList<>();
                solutions.add(toAdd);
                return solutions;
            }
            for (oppMoveNode child : children) {
                solutions.addAll(child.getSolutions()); //get solutions from child
            }
            for (LinkedList<Move> solution : solutions) //prepend this node's move to each solution
                solution.add(0, this.value);
            return solutions;
        }


    }
}