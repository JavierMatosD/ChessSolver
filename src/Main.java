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

        for(String s: contents){
            ChessPuzzle puzzle = ChessBoardParser.parse("puzzles/" + s);
            System.out.println(puzzle.solvePuzzleOneMove());
        }






    }

}
