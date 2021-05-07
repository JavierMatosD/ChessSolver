import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;


//from: https://techblogstation.com/java/read-text-file-in-java/

public class ChessBoardParser {

	public static void main(String[] args) throws Exception {
		ChessPuzzle testboard = null;
		if(args.length==0)
         	testboard = parse("templateboard.txt");
        else try {
			testboard = parse(args[0]);
		}catch (IOException e){
        	System.err.println("Please pass a valid text file as a command line argument.");
        	System.exit(1);
		}
		ChessBoard board = new ChessBoard(testboard.board);
        /*
		Path filePath = FileSystems.getDefault().getPath("templateboard.txt");
		Charset charset = StandardCharsets.UTF_8;
		try {
			List<String> lines = Files.readAllLines(filePath, charset);
			for (String line : lines) {
				System.out.println(line);
			}
		} catch (IOException ex) {
			System.out.format("I/O Exception", ex);
		}
        */
	}

	public static ChessPuzzle parse (String filename) throws Exception{


		Path filePath = FileSystems.getDefault().getPath(filename);
		Charset charset = StandardCharsets.UTF_8;
		ChessPiece[][] chessPieces = new ChessPiece[8][8];
		boolean whiteTurn = false;

		try {
			List<String> lines = Files.readAllLines(filePath, charset);
			int count = 0;
			String playerTurn=lines.remove(0);
			if(playerTurn.equals("black"))
				whiteTurn = false;
			else if(playerTurn.equals("white"))
				whiteTurn = true;
			else throw new Exception("invalid puzzle, can't read player turn");

			for (String line : lines) {

				//https://www.w3docs.com/snippets/java/how-to-split-a-string-in-java.html

       			String[] splitline = line.split(":");
       			int inboardcount = 0;
        		for (String a: splitline){
        			if (a.equals("wp")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.PAWN, true);
        			}
        			else if (a.equals("wn")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.KNIGHT, true);
        			}
        			else if (a.equals("wb")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.BISHOP, true);
        			}
        			else if (a.equals("wr")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.ROOK, true);
        			}
        			else if (a.equals("wq")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.QUEEN, true);
        			}
        			else if (a.equals("wk")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.KING, true);
        			}
        			else if (a.equals("bp")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.PAWN, false);
        			}
        			else if (a.equals("bn")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.KNIGHT, false);
        			}
        			else if (a.equals("bb")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.BISHOP, false);
        			}
        			else if (a.equals("br")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.ROOK, false);
        			}
        			else if (a.equals("bq")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.QUEEN, false);
        			}
        			else if (a.equals("bk")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.KING, false);
        			}
        			else{
        				chessPieces[count][inboardcount] = new ChessPiece();
        			}
        			inboardcount++;
        		}
        		count++;
			}
		
		}

		catch (IOException ex) {
			System.out.format("I/O Exception", ex);
		}

		return new ChessPuzzle(whiteTurn, chessPieces);

	}

}