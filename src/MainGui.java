import java.io.File;
import java.util.*;

class MainGui {
    public MainGui() {
        File directoryPath = new File("../puzzles");
        String[] contents;
        contents = directoryPath.list();
        // mate in one puzzle
        String puzzlePath = contents[0];

        long start = System.nanoTime();
        try{          
            ChessPuzzle puzzle = ChessBoardParser.parse("../puzzles/" + puzzlePath);
            String turn = puzzle.whiteTurn == true ? "WHITE" : "BLACK";
            Gui gui = new Gui(puzzle.board, turn);
            ArrayList<ArrayList<Move>> moves =  puzzle.solvePuzzle();
            ArrayList<Move> move = moves.get(0);
            gui.moves = move;
        } catch(Exception e){

        }

        long end = System.nanoTime();

        long mstime = ((end - start) / 1_000_000);

        System.out.println("Took " + mstime + " ms to solve this puzzle.");
    }
}