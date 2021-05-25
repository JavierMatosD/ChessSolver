import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;


public class getLocationLegalMovesTask implements Runnable
{
    ChessPuzzle puzzle;
    int row, col, id;
    AtomicReferenceArray<LinkedList<Move>> sharedList;
    CountDownLatch latch;

    public getLocationLegalMovesTask(ChessPuzzle puzzle, int row, int col, int id, AtomicReferenceArray<LinkedList<Move>> sharedList, CountDownLatch latch)
    {
        this.puzzle     = puzzle;
        this.row        = row;
        this.col        = col;
        this.id         = id;
        this.sharedList = sharedList;
        this.latch      = latch; 
    }
    @Override
    public void run() 
    {
        sharedList.getAndSet(id, puzzle.getLocationLegalMoves(row, col, puzzle.whiteTurn, puzzle.board));
        latch.countDown();
    }
    
}
