import java.io.File;

public class Tests {

    public static void main(String[] args) throws Exception{
        File directoryPath = new File("testcases");
        //List of all files and directories
        String contents[] = directoryPath.list();
        int failures = 0;
        for(String a: contents) {
            System.out.println("Running test " + a);
            if(!runTest(ChessBoardParser.parse("testcases/"+a)))
                failures++;
        }

    }


    public static boolean runTest(ChessPuzzle puzzle){
        int count = 0;
        for(Move m: puzzle.getLegalMoves()) {
            System.out.println(m);
            count++;
        }
        System.out.println("there are " + count + " legal moves");
        return true;
    }
}
