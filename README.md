# Chess Puzzle ChessPuzzle

## Description

This program solves chess puzzles and displays the solution in a graphical representation.

## Preliminary description

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

## Updated description

Most of the preliminary description is accurate. Since then, the most significant thing we added is a tree structure for solving puzzles.

MoveTree.java (and MoveTreeParallel.java) store a tree with each node representing a single move. There are myMoveNodes and oppMoveNodes.
Each node has children of the other type representing all the moves the other player can make after the move on the node is made.
The tree is populated level by level, with checkmate being searched for as children are set. If a move the user can make leads directly
to checkmate, that node's boolean checkmate is set to true, and the parent node has checkmate set to true, since you know that if the opponent makes that
move you can win the game. If the parent's parent has all of its children with checkmate set to true, then it is set to true.
That is, for a move you make to lead to checkmate, it must be the case that every move your opponent can make will lead to checkmate. But for 
a move your opponent makes to lead to checkmate, you only need one checkmating move. This process continues with checkmate 
being set up the tree as necessary until the root node has checkmate set to true, in which case the winning series of moves
can be found and returned by the solver function.


## parallelization strategies

We used a simple thread pool with threads equal to the number of available processors on the user's computer. Other numbers of threads had different effects,
but we found this was the sweet spot for larger puzzles. We'd rather the 1 second puzzle take 2 seconds than the 10 second puzzle take 20, so we optimize for 
the longer puzzles.

The first thing we parallelized was the function getLegalMoves, which takes a board state and returns a LinkedList of all the moves
the active player can make given that state. This includes iterating over the board and getting all the moves a given piece can make, 
and then iterating over the moves returned by the first step and removing any that put your own king in check. We started by parallelizing the 
more expensive second step. This led to performance gains, which was encouraging. We then also parallelized the first step, putting it into
the same task as the second step. This didn't lead to any noticeable gain, which was expected. Checking for check for each move is the more expensive task than
getting all the moves, since checking for check involves getting all the moves the opponent can make. We used a CountDownLatch to make sure
all the moves were found before returning the final list.

The problem with this parallelization strategy is that first there's a bit of downtime and second there's a lot of tasks being created. So next
we looked to parallelize the setChildren method, which we did in MoveTreeParallel. Since each node really only interacts with its
children, there should be very little concurrent accesses with this. The only thing that multiple threads might need to access is the 
boolean checkmate of parents, which we simply made atomic. So each thread sets the children for its node, and a CountDownLatch waits for all
nodes to have their children set before the next set of children are set. This led to worse performance than both the sequential and the other
parallelization implementation, which is surprising. In theory, with fewer tasks created, this
should be better than the other implementation. We tried replacing the shared data structure with one that is updated sequentially by 
the main thread, but that made no difference. We also made fields final to make sure checks for cache invalidations aren't slowing us down,
but still no difference. Ultimately we gave up on this implementation and focused on the other two, since we couldn't figure out why it 
was underperforming.

The biggest optimization we made was to look for checkmates more intelligently. When a layer of children is set, the program first
loops through all the nodes on the previous layer and does the ones that lead to check first. It then checks if the puzzle is solved (root.checkmate is set)
and returns if it is. So instead of setting the whole layer at once, the program looks for solutions first and if it finds a solution it 
terminates early. This saves lots of time, cutting runtime down by a factor of 10 in both the sequential and the parallel implementation. Since 
we saw that the set children parallelization was slower even when more work was needed to be done in each puzzle (no early termination) we knew 
it definitely wouldn't outperform sequential when there's less work to be done, so we didn't implement early termination for set children parallelization 
(also known by the misnomer super parallelization). 

To fix the problem of running out of heap memory, we wrote a method setChildrenPrune. When the solver is at the maximum depth (an argument which says
the maximum number of moves to look for in a solution), it doesn't set the children of the node it's looking at and removes the node from its parent's
children list if it doesn't lead to checkmate. Non winning leaves are pruned from the tree, basically. Unfortunately, because java doesn't allow
manual garbage collection, there's no way of guaranteeing the memory actually gets freed up, so we just have to hope that this improves performance.

## Using the program

Compile simply by running javac *.java in the src directory. To run any of the classes listed below, navigate to the root directory and use the command 
"java -classpath src ClassName.java"

Running Main.java with puzzle file names as command line args will search for those files in the puzzles directory and run the solver on them,
printing results and the time it took. It will run the solver each of three ways: sequentially, with get legal moves parallelized, and with set children in the nodes parallelized.
Running it with no args will do this for every puzzle in the puzzles directory.

Running MainGui.java with a puzzle file name as a command line argument will display that puzzle in a nice looking GUI and run the solver. Pressing the 
next move button will update the board with the next move taken from the solver. If the solver isn't done yet, the button will be updated
to a waiting state until the solution is ready. Pressing the next move button when all the moves are done and the game is over/the puzzle 
is solved/checkmate is reached will result in a checkmate message being displayed. The solver in MainGui will either run sequentially or 
using get legal moves parallelization. For the latter, set the static boolean parallel to true.

MainGui is the way the puzzles would be used by a user, and Main is more for performance testing. Early termination is on by default, but it can be turned off 
by changing the static boolean earlyTermination to false in MoveTree.java. This way the performance of super parallelization can be directly compared to the 
other two, since super parallelization does not have early termination implemented. 

In the puzzles directory are 10 mate in 1 puzzles and 7 mate in 2 puzzles, as well as a single mate in 3 puzzle. There are other mate in 3 puzzles in
the unused_puzzles directory. If you have access to a supercomputer, you could move those into puzzles/ and test them, but when we tried them we run out of heap
memory space, even with the pruning we mentioned above.

There's also Tests.java, which tests all the board states in tests/ by calling getLegalMoves and making sure that the program detects the right 
number of legal moves in each case. The user doesn't need to worry about Tests unless they're making modifications to the code,
in which case Tests is useful to make sure you haven't broken anything in getLegalMoves. You can also pass a command line argument to 
Tests.java to run specific tests as opposed to all of them.