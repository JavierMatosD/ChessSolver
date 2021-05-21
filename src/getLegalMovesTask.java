import java.util.concurrent.atomic.AtomicReferenceArray;

public class getLegalMovesTask implements Runnable {
    
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
            sharedMoves.getAndSet(id, true);
        }
    }
    
}
