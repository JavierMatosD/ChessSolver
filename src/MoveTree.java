import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class MoveTree {
    boolean isSolved;
    oppMoveNode root;
    ChessPuzzle puzzle;

    public MoveTree(ChessPuzzle puzzle) {
        this.puzzle = puzzle;
        this.root = new oppMoveNode(null, null, puzzle);
        this.isSolved = false;
    }

    public ArrayList<ArrayList<Move>> solveTree(int maxDepth, boolean parallel, ExecutorService pool) {

        ArrayList<myMoveNode> myCurrentMoves = new ArrayList<>();
        ArrayList<oppMoveNode> oppCurrentMoves = new ArrayList<>();
        oppCurrentMoves.add(root);
        for (int i = 0; i < maxDepth; i++) {
            for (oppMoveNode n : oppCurrentMoves) { //set children of all oppCurrentMoves, put them in myCurrentMoves
                n.setChildren(parallel, pool);
                myCurrentMoves.addAll(n.children);
            }
            oppCurrentMoves.clear();
            for (myMoveNode n : myCurrentMoves) { //set children of all myCurrentMoves. Put them in oppCurrentMoves
                if (ChessPuzzle.staticCheckCheck(!this.puzzle.whiteTurn, n.getBoardState())) { //do the ones that lead to check first
                    n.check = true;
                    n.setChildren(parallel, pool);
                    oppCurrentMoves.addAll(n.children);
                }
            }
            for (myMoveNode n : myCurrentMoves) {
                if (!n.check) {
                    n.setChildren(parallel, pool);
                    oppCurrentMoves.addAll(n.children);
                    
                }
                
            }
            myCurrentMoves.clear();
            if (root.checkMate){
                
                return root.getSolutions();
            }
            
        }
        
        return root.getSolutions();

    }


    public class oppMoveNode {
        myMoveNode parent; //null for root node
        ArrayList<myMoveNode> children;
        Move value; //null for root node
        boolean checkMate; //true if true for any children
        ChessPuzzle puzzle;
        MoveTree tree;


        public oppMoveNode(Move value, myMoveNode parent, ChessPuzzle puzzle) {
            this.value = value;
            this.parent = parent;
            this.puzzle = puzzle;
            this.children = new ArrayList<>();
        }

        public ChessPiece[][] getBoardState() {
            if (this.value == null) //root node
                return this.puzzle.board;
            return this.value.executeMove(this.parent.getBoardState());
        }

        public void setChildren(boolean parallel, ExecutorService pool) {
            ChessPiece[][] boardState = this.getBoardState();
            ArrayList<Move> moves = new ArrayList<Move>();
            if(parallel){
                ChessPuzzle cp = new ChessPuzzle(this.puzzle.whiteTurn, boardState);
                moves = cp.getLegalMovesParallel(pool);
            } else {
                moves = new ChessPuzzle(this.puzzle.whiteTurn, boardState).getLegalMoves();
            }
            
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

        public ArrayList<ArrayList<Move>> getSolutions() {
            ArrayList<ArrayList<Move>> solutions = new ArrayList<>();
            for (myMoveNode child : children) {
                if (child.checkMate) {
                    solutions.addAll(child.getSolutions()); //get solutions from child
                    for (ArrayList<Move> solution : solutions) //prepend this node's move to each solution
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
            return this.value.toString() + " " + this.checkMate;

        }
    }

    class myMoveNode {
        oppMoveNode parent;
        ArrayList<oppMoveNode> children;
        Move value;
        boolean checkMate; //set to true if true for all children
        ChessPuzzle puzzle;
        boolean check;

        public myMoveNode(Move value, oppMoveNode parent, ChessPuzzle puzzle) {
            this.value = value;
            this.parent = parent;
            this.puzzle = puzzle;
            this.children = new ArrayList<>();
            this.check = false;
        }

        public String toString() {
            return this.value.toString() + " " + this.checkMate;

        }

        //state after the value move and all parents are executed
        public ChessPiece[][] getBoardState() {
            return this.value.executeMove(this.parent.getBoardState());
        }


        //sets the children, or sets checkMate to true and parent checkmate to true
        public void setChildren(boolean parallel, ExecutorService pool) {
            ChessPiece[][] boardState = this.getBoardState();
            //the state of the board after your move
            ChessPuzzle p = new ChessPuzzle(!this.puzzle.whiteTurn, boardState); 
            //all of the opponent's legal moves
            ArrayList<Move> oppMoves = new ArrayList<Move>();
            if (parallel) {
                oppMoves = p.getLegalMovesParallel(pool);
            } else {
                oppMoves = p.getLegalMoves();
            }
            
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
            if (checkChildren()) {
                this.checkMate = true;
                parent.setCheckMate();
            }
        }


        public ArrayList<ArrayList<Move>> getSolutions() {
            ArrayList<ArrayList<Move>> solutions = new ArrayList<>(); //for debugging, can be removed
            if (this.children.size() == 0) { //if this node has no children, it's a mate in 1 move
                ArrayList<Move> toAdd = new ArrayList<>();
                toAdd.add(this.value);
                solutions = new ArrayList<>();
                solutions.add(toAdd);
                return solutions;
            }
            for (oppMoveNode child : children) {
                solutions.addAll(child.getSolutions()); //get solutions from child
            }
            for (ArrayList<Move> solution : solutions) //prepend this node's move to each solution
                solution.add(0, this.value);
            return solutions;
        }


    }
}

