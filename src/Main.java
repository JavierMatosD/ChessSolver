//TODO: better exception handling?

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception{

        File directoryPath = new File("puzzles");
        //List of all files and directories
        String[] contents;
        if(args.length==0)
            contents = directoryPath.list();
        else contents = args;

        long start = System.nanoTime();
        for(String s: contents){
            ChessPuzzle puzzle = ChessBoardParser.parse("puzzles/" + s);
            System.out.println(s +": " + puzzle.nodeSolvePuzzle());
            System.out.println(puzzle.solvePuzzleOneMove());

        }
        long end = System.nanoTime();

        long mstime = ((end - start) / 1_000_000);

        System.out.println("Took "+mstime+" ms to solve this puzzle.");



    }

}
