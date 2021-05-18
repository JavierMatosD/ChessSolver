import java.io.File;
import java.util.*;

public class Tests {
    static Hashtable<String, Integer> expectedResults = new Hashtable<String, Integer>();


    public static void main(String[] args) throws Exception {

        expectedResults.put("rook_1.txt", 14); //hacky method of validation but hey it works
        expectedResults.put("rook_2.txt", 8);
        expectedResults.put("rookcheck.txt", 6);
        expectedResults.put("pawn_1.txt", 4);
        expectedResults.put("pawn_2.txt", 4);
        expectedResults.put("bishop_1.txt", 13);
        expectedResults.put("knight_1.txt", 8);
        expectedResults.put("king_1.txt", 8);
        expectedResults.put("queen_1.txt", 27);
        expectedResults.put("queen_2.txt", 21);
        expectedResults.put("rook_3.txt", 14);
        expectedResults.put("castling_1.txt", 26);
        expectedResults.put("castling_2.txt", 25);
        expectedResults.put("castling_3.txt", 4);




        File directoryPath = new File("../testcases");
        //List of all files and directories
        String[] contents;
        if(args.length==0)
             contents = directoryPath.list();
        else contents = args;
        int failures = 0;
        for (String a : contents) {
            System.out.println("\nRunning test " + a);
            try {
                if (runTest(ChessBoardParser.parse("../testcases/" + a)) != expectedResults.get(a)) {
                    failures++;
                    System.out.println("Test case failed!"); //This is printing to stdout instead of stderr because intellij messes up the timing of the two output streams for some reason
                }
            } catch (NullPointerException e) {
                System.out.println(": "+ a + " not found in expectedResults. Did you forget to add it to the dictionary?");
                failures++;
            }

        }
        System.out.println();
        if(failures == 0)
            System.out.println("There were no failures.");
        else {
            System.err.println("There were " + failures + " failures.");
        }

    }


    public static int runTest(ChessPuzzle puzzle) {
        int count = 0;
        for (Move m : puzzle.getLegalMoves()) {
            System.out.println(m);

            count++;
        }
        System.out.println("there are " + count + " legal moves");

        return count;

    }
}
