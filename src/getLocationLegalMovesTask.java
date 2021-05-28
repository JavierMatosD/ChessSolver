import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

//this task takes in a location and a puzzle and creates a list of all legal moves from that location (including making sure those moves
//don't put your king in check. It puts that list into an AtomicReferenceArray that's shared by all threads and counts down a latch.
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
        LinkedList<Move> moves = puzzle.getLocationLegalMoves(row, col, puzzle.whiteTurn, puzzle.board, true);
        moves.removeIf(m -> puzzle.checkCheck(m, puzzle.whiteTurn, puzzle.board));
        sharedList.getAndSet(id, moves);
        latch.countDown();
    }
    
}
