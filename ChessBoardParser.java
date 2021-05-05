package ReadFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

//from: https://techblogstation.com/java/read-text-file-in-java/

public class ChessBoardParser {
	enum type {
    ROOK, KNIGHT, BISHOP, QUEEN, KING, PAWN, EMPTY //replace empty with null?
	}

	public static void main(String[] args) throws Exception {

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
	}

	public ChessPiece[][] parse (String filename) throws Exception {


		Path filePath = FileSystems.getDefault().getPath(filename);
		Charset charset = StandardCharsets.UTF_8;
		ChessPiece[][] chessPieces = new ChessPiece[8][8];

		try {
			List<String> lines = Files.readAllLines(filePath, charset);
			int count = 0;
			for (String line : lines) {
				//System.out.println(line);

				//https://www.w3docs.com/snippets/java/how-to-split-a-string-in-java.html

       			String[] splitline = line.split(":");
       			int inboardcount = 0;
        		for (String a: arrOfStr){
        			if (a.equals("wp")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.PAWN, false);
        			}
        			else if (a.equals("wn")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.KNIGHT, false);
        			}
        			else if (a.equals("wb")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.BISHOP, false);
        			}
        			else if (a.equals("wr")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.ROOK, false);
        			}
        			else if (a.equals("wq")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.QUEEN, false);
        			}
        			else if (a.equals("wk")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.KING, false);
        			}
        			else if (a.equals("bp")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.PAWN, true);
        			}
        			else if (a.equals("bn")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.KNIGHT, true);
        			}
        			else if (a.equals("bb")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.BISHOP, true);
        			}
        			else if (a.equals("br")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.ROOK, true);
        			}
        			else if (a.equals("bq")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.QUEEN, true);
        			}
        			else if (a.equals("bk")){
        				chessPieces[count][inboardcount] = new ChessPiece(type.KING, true);
        			}
        			else{
        				chessPieces[count][inboardcount] = new ChessPiece();
        			}
        			System.out.print(a);
        			inboardcount++;
        		}

        		count++;
			}
		
		}

		catch (IOException ex) {
			System.out.format("I/O Exception", ex);
		}

		return chessPieces;

	}

}