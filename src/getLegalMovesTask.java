public class getLegalMovesTask implements Runnable {
    
    boolean[] sharedMoves;
    ChessPuzzle puzzle;
    int id;
    Move move;
    
    public getLegalMovesTask(boolean[] sharedMoves, ChessPuzzle puzzle, int id, Move move) 
    {
        this.sharedMoves = sharedMoves;
        this.puzzle      = puzzle;
        this.id          = id;
        this.move        = move;
    }


    @Override
    public void run() {
        if (puzzle.checkCheck(move, puzzle.whiteTurn))
        {
            sharedMoves[id] = true;
        }
    }
    
}
