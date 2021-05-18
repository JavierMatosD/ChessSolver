# Chess Puzzle ChessPuzzle

## Description

This program solves chess puzzles and displays the solution in a graphical representation.

## How the program works

The program takes a text representation of a chessboard and creates a chess puzzle object. Then it iterates through the board and finds all the possible legal moves a player can make. Once the initial list of moves is generated, the program creates a Tree of Move objects where each node in the tree maintains a list of child nodes. Every child node is an available move.
Right now, we have many of the component parts completed. First, we have the GUI implemented. This can be seen by running testBoard.java.
There's still some bugs and implementation details to iron out, but this shows roughly what the end result will look like.
The GUI can also be called by running ChessBoardParser with a command line argument that's the filepath
to a text file that can be parsed into a puzzle, as described below.

We also have our data processing set up, in ChessBoardParser.java. This program can read in a text file (such as those found in testcases/),
and parse it into a ChessPuzzle object. In the ChessPuzzle object we have implemented functionality that will, given whose turn it is
and a board state, find all the legal moves a player can take. Currently implemented are the rules for rook moves and functionality
to invalidate any moves that would (illegally) put your own king into check. This is all done with a pretty brute force method.
The file Tests.java runs all the unit tests created for the rules we've completed that are stored in testcases/, prints all the moves found, and validates that the number of
moves matches what's expected.

When we include the actual puzzles, we'll instead be validating by checking for the single solution that leads to checkmate.

We also need the GUI to display the solution to the user. And, of course, we need to implement
parallelization in the solving, which will be evaluated by simple timing functions called during runtime.

We also need to clean up the code. Some functions need to be changed up, comments need to be added,
variable names need to be made to make sense.
