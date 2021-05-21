import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class getLegalMovesTask implements Runnable, Callable {
    
    AtomicReferenceArray<Boolean> sharedMoves;
    ChessPuzzle puzzle;
    int id;
    Move move;
    
    public getLegalMovesTask(AtomicReferenceArray<Boolean> sharedMoves, ChessPuzzle puzzle, int id, Move move) 
    {
        this.sharedMoves = sharedMoves;
        this.puzzle      = puzzle;
        this.id          = id;
        this.move        = move;
    }


    @Override
    public void run() {

        if (puzzle.checkCheck(move, puzzle.whiteTurn, puzzle.board))
        {
            //System.out.println("ID: " + id + ", Checking: " + move.chessPiece);
            sharedMoves.getAndSet(id, true);
        }
    }

    @Override
    public Object call() throws Exception {
        return null;
    }
}
