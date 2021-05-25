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
            int nThreads = Runtime.getRuntime().availableProcessors();
            ExecutorService pool = Executors.newFixedThreadPool(nThreads);
            long start = System.nanoTime();

            ChessPuzzle puzzle = ChessBoardParser.parse(directoryPath.getName() + "/" + s);

            System.out.println(s + " tree: " + puzzle.solvePuzzle());
            long end = System.nanoTime();
            long mstime = ((end - start) / 1_000_000);
            System.out.println("Took " + mstime + " ms to solve this puzzle.");
            start = System.nanoTime();

            puzzle.addPool(pool);

            System.out.println(s + " tree: " + puzzle.solvePuzzle());
            end = System.nanoTime();
            mstime = ((end - start) / 1_000_000);
            System.out.println("Took " + mstime + " ms to solve this puzzle with parallelism.");

            puzzle.parallel = false;
            System.out.println(s + " tree: " + puzzle.solvePuzzleSuperParallel(pool));
            end = System.nanoTime();
            mstime = ((end - start) / 1_000_000);
            System.out.println("Took " + mstime + " ms to solve this puzzle with super parallelism.");
            pool.shutdown();
        }
        





    }

}
