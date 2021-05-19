//TODO: better exception handling?

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {

        File directoryPath = new File("puzzles");
        if (!directoryPath.exists())
            directoryPath = new File("../puzzles");
        //List of all files and directories
        String[] contents;
        if (args.length == 0)
            contents = directoryPath.list();
        else contents = args;

        for(String s: contents){
            long start = System.nanoTime();

            ChessPuzzle puzzle = ChessBoardParser.parse(directoryPath.getName() +"/"+ s);
//            System.out.println(s +" 2 hard coded: " + puzzle.nodeSolvePuzzle());
            System.out.println(s +" tree: " + puzzle.solvePuzzle());
            long end = System.nanoTime();

            long mstime = ((end - start) / 1_000_000);

            System.out.println("Took " + mstime + " ms to solve this puzzle.");
//            System.out.println(s + " one only: " +puzzle.solvePuzzleOneMove());

        }




    }

}
