import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MoveTreeParallel {
    AtomicBoolean isSolved;
    final oppMoveNode root;
    final ChessPuzzle puzzle;
    AtomicReferenceArray<AtomicReferenceArray<ChessPiece>> board;
    ExecutorService pool;

    public MoveTreeParallel(ChessPuzzle puzzle, ExecutorService pool) {
        this.puzzle = puzzle;
        this.root = new oppMoveNode(null, null, puzzle);
        this.isSolved = new AtomicBoolean(false);
        this.pool = pool;
//        this.board = new AtomicReferenceArray<AtomicReferenceArray<ChessPiece>>(8);
//        for(int i = 0; i < 8; i++)
//            this.board.set(i, new AtomicReferenceArray<>(this.puzzle.board[i]));
    }

    public LinkedList<LinkedList<Move>> solveTree(int maxDepth) {

        ConcurrentLinkedQueue<myMoveNode> myCurrentMoves = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<oppMoveNode> oppCurrentMoves = new ConcurrentLinkedQueue<>();


        oppCurrentMoves.add(root);
        for (int i = 0; i < maxDepth; i++) {

            CountDownLatch latch = new CountDownLatch(oppCurrentMoves.size());
            for (Node n : oppCurrentMoves) { //set children of all oppCurrentMoves, put them in myCurrentMoves
                pool.execute(new setChildrenTask<>(n, myCurrentMoves, latch));
            }
            try {
                latch.await(); //interrupt when mate found
            } catch (InterruptedException e){
                System.out.println("Latch interrupted. That's not good.");
                System.exit(-1);
            }
            for (Node n : oppCurrentMoves) {
                myCurrentMoves.addAll(n.getChildren());
            }
            oppCurrentMoves.clear();


            latch = new CountDownLatch(myCurrentMoves.size());

            //set children of all myCurrentMoves. Put them in oppCurrentMoves
            if (i < maxDepth - 1) { //TODO: check those with check first?
                for (Node n : myCurrentMoves) { //set children for the rest
                    pool.execute(new setChildrenTask<>(n, oppCurrentMoves, latch));
                }
                try {
                    latch.await();
                } catch (InterruptedException e){
                    System.out.println("Latch interrupted. That's not good.");
                    System.exit(-1);
                }
                for (Node n : myCurrentMoves) {
                    oppCurrentMoves.addAll(n.getChildren());
                }
            }
            else { //when on the last level
                for (myMoveNode n : myCurrentMoves){
                    n.setChildrenPrune();
                }
            }

            myCurrentMoves.clear();
            if (root.checkMate.get()) {

                return root.getSolutions();
            }

        }

        return root.getSolutions();

    }


    public class oppMoveNode extends Node {
        final myMoveNode parent; //null for root node
        LinkedList<myMoveNode> children;
        final Move value; //null for root node
        AtomicBoolean checkMate; //true if true for any children
        final ChessPuzzle puzzle;
        MoveTree tree;


        public oppMoveNode(Move value, myMoveNode parent, ChessPuzzle puzzle) {
            this.value = value;
            this.parent = parent;
            this.puzzle = puzzle;
            this.children = new LinkedList<>();
            this.checkMate = new AtomicBoolean(false);
        }

        public ChessPiece[][] getBoardState() {
            if (this.value == null) //root node
                return this.puzzle.board;
            return this.value.executeMove(this.parent.getBoardState());
        }

        public void setChildren() {
//            System.out.println("Setting children for " + this.value);
            ChessPiece[][] boardState = this.getBoardState();

            LinkedList<Move> moves = new ChessPuzzle(this.puzzle.whiteTurn, boardState).getLegalMoves();


            for (Move m : moves) {
                this.children.add(new myMoveNode(m, this, this.puzzle));
            }
        }

        //if any children lead to mate, return true
        public LinkedList<myMoveNode> getChildren(){
            return this.children;
        }

        //sets this node to checkmate. Checks parents too. Triggered by child
        public void setCheckMate() {
            this.checkMate.set(true);
            if (parent != null)
                parent.triggeredCheckCheckMate();
            else isSolved.set(true);
        }

        public LinkedList<LinkedList<Move>> getSolutions() {
            LinkedList<LinkedList<Move>> solutions = new LinkedList<>();
            for (myMoveNode child : children) {
                if (child.checkMate.get()) {
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
            return this.value.toString() + " " + this.checkMate;

        }
    }

    class myMoveNode extends Node {
        final oppMoveNode parent;
        LinkedList<oppMoveNode> children;
        final Move value;
        AtomicBoolean checkMate; //set to true if true for all children
        final ChessPuzzle puzzle;
        AtomicBoolean check;

        public myMoveNode(Move value, oppMoveNode parent, ChessPuzzle puzzle) {
            this.value = value;
            this.parent = parent;
            this.puzzle = puzzle;
            this.children = new LinkedList<>();
            this.check = new AtomicBoolean(false);
            this.checkMate = new AtomicBoolean(false);
        }

        public String toString() {
            return this.value.toString() + " " + this.checkMate;

        }

        //state after the value move and all parents are executed
        public ChessPiece[][] getBoardState() {
            return this.value.executeMove(this.parent.getBoardState());
        }


        //sets the children, or sets checkMate to true and parent checkmate to true
        public void setChildren() {

            ChessPiece[][] boardState = this.getBoardState();
            //the state of the board after your move
            ChessPuzzle p = new ChessPuzzle(!this.puzzle.whiteTurn, boardState);
            //all of the opponent's legal moves
            LinkedList<Move> oppMoves = p.getLegalMoves();


            if (oppMoves.size() == 0 && p.checkCheckNoMove(p.whiteTurn)) { //if opponent is in check and has no legal moves
                this.checkMate.set(true);

                parent.setCheckMate(); //set parent to checkmate, since you know if parent move is made, child move can mate them
                return;
            }
            for (Move m : oppMoves) {
                children.add(new oppMoveNode(m, this, this.puzzle));
            }
            this.checkMate.set(false);
        }

        public LinkedList<oppMoveNode> getChildren(){
            return this.children;
        }

        //Checks for checkmate, prunes self if not
        public void setChildrenPrune() {

            ChessPiece[][] boardState = this.getBoardState();
            //the state of the board after your move
            ChessPuzzle p = new ChessPuzzle(!this.puzzle.whiteTurn, boardState);
            //all of the opponent's legal moves
            LinkedList<Move> oppMoves = p.getLegalMoves();


            if (oppMoves.size() == 0 && p.checkCheckNoMove(p.whiteTurn)) { //if opponent is in check and has no legal moves
                this.checkMate.set(true);

                parent.setCheckMate(); //set parent to checkmate, since you know if parent move is made, child move can mate them
                return;
            }
            this.checkMate .set(false);
            this.parent.children.remove(this); //prune self from tree
        }

        //if all children lead to mate, return true
        public boolean checkChildren() {
            for (oppMoveNode child : children) {
                if (!child.checkMate.get())
                    return false;
            }
            return true;
        }

        //check if this node is checkmate by checking if all children are checkmate. Triggered by children.
        public void triggeredCheckCheckMate() {
            if (checkChildren()) {
                this.checkMate.set(true);
                parent.setCheckMate();
            }
        }


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

    abstract class Node{
        Move value;
        boolean checkmate;
        boolean check;
        LinkedList children;
        public abstract void setChildren();
        public abstract LinkedList getChildren();

    }
}

