//TODO: better exception handling?

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception{
        ChessPuzzle puzzle = null;
        if(args.length==0)
            puzzle = ChessBoardParser.parse("puzzles/puzzle3.txt");
        else try {
            puzzle = ChessBoardParser.parse(args[0]);
        }catch (IOException e){
            System.err.println("Please pass a valid text file as a command line argument.");
            System.exit(1);
        }
        System.out.println(puzzle.solvePuzzle());



    }
}
