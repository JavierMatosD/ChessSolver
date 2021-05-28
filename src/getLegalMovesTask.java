import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReferenceArray;
//old task for getting legal moves without checking for check
@Deprecated
public class getLegalMovesTask implements Runnable, Callable 
{
    
    AtomicReferenceArray<Boolean> sharedMoves;
    ChessPuzzle puzzle;
    int id;
    Move move;
    CountDownLatch latch;
    
    public getLegalMovesTask(AtomicReferenceArray<Boolean> sharedMoves, ChessPuzzle puzzle, int id, Move move, CountDownLatch latch) 
    {
        this.sharedMoves = sharedMoves;
        this.puzzle      = puzzle;
        this.id          = id;
        this.move        = move;
        this.latch       = latch;
    }


    @Override
    public void run() 
    {

        if (puzzle.checkCheck(move, puzzle.whiteTurn, puzzle.board))
        {
            sharedMoves.getAndSet(id, true);
        }
        latch.countDown();
    }

    @Override
    public Object call() throws Exception 
    {
        return null;
    }
}
