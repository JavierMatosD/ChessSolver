import java.io.File;
import java.util.*;

public class Tests {
    static Hashtable<String, Integer> expectedResults = new Hashtable<String, Integer>();


    public static void main(String[] args) throws Exception{

        expectedResults.put("rook_1.txt", 14); //hacky method of validation but hey it works
        expectedResults.put("rook_2.txt", 8);




        File directoryPath = new File("testcases");
        //List of all files and directories
        String contents[] = directoryPath.list();
        int failures = 0;
        for(String a: contents) {
            System.out.println("Running test " + a);
            if(runTest(ChessBoardParser.parse("testcases/"+a)) != expectedResults.get(a)) {
                failures++;
                System.err.println("Test case failed!");
            }
        }
        System.out.println();
        System.out.println("There were " + failures + " failures.");


    }


    public static int runTest(ChessPuzzle puzzle){
        int count = 0;
        for(Move m: puzzle.getLegalMoves()) {
            System.out.println(m);
            count++;
        }
        System.out.println("there are " + count + " legal moves");
        System.out.println();

        return count;

    }
}
