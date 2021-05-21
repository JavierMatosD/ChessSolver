//TODO: better exception handling?

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        for (String s : contents) {
            long start = System.nanoTime();
            int nThreads = Runtime.getRuntime().availableProcessors();
            ExecutorService pool = Executors.newFixedThreadPool(nThreads);


            ChessPuzzle puzzle = ChessBoardParser.parse(directoryPath.getName() + "/" + s);

            System.out.println(s + " tree: " + puzzle.solvePuzzle(true, pool));
            pool.shutdown();    
            long end = System.nanoTime();


            long mstime = ((end - start) / 1_000_000);
            System.out.println("Took " + mstime + " ms to solve this puzzle.");
            //            System.out.println(s + " one only: " +puzzle.solvePuzzleOneMove());

        }
        




    }

}
