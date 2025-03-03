# TerminalChess
A game of Chess ran in the terminal for the vintage 80s aesthetic, with peer-to-peer multiplayer to show who is really the true chess champion.

# How to Run:


# Behind the Scenes:
# The Board is Stored in a 1D Array
The entire board is stored in a 1D array from 0 to 63. This makes the maths significantly easier to calculate.
To check if a move results in a piece being off the board, simply add the offset and see if the resulting square is < 0 or > 63.

Each piece is stored in a Piece object. These work using bitwise operations. 
The first 2 bits are used to signify the colour of the Piece, while the last 3 show the piece's type.

The values are: NONE = 0, KING = 1, PAWN = 2, KNIGHT = 3, BISHOP = 4, ROOK = 5, QUEEN = 6;
WHITE is stored as 8, BLACK is stored as 16.

The black queen is therefore stored as Piece 25. In the board array, square[59] == 00011001 or in English: 8D is Black Queen.

This allows some very useful and simply functions such as:

public static boolean isType(int piece, int type) {
        return (piece & 0x7) == type; 
    }

# Offsets: How do these work?

Let us consider the case of a pawn whose offsets to attack diagonally are {7, 9} or {-7, -9}.

  A B C D E F G H 
3 . . . . . . . .
2 . . . . . . . .
1 . . P . . . . .

in the Array this looks like (indexes):
16 17 18 19 20 21 22 23 
8  9  10 11 12 13 14 15
0  1  P  3  4  5  6  7

Pawn on C1 can attack diagonally onto B2 or D2. In the board array these are squares 9 and 11. Pawn occupies square to, which with the offsets makes the magic numbers
9 and 11. This principle is applied to all the pieces. The King moves in all directions so the offsets are: {-9, -8, -7, -1, 1, 7, 8, 9}
Using this visualtion (or the helpful b.printBoardDebug() function) we can see how these all correspond to a one tile move for the king.

