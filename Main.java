import Pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import static Pieces.Piece.*;

public class Main {
   // static final char LIGHT_SQUARE = '□';
    //
   // static final char DARK_SQUARE = '■';
    private static int friendlyColour;  // The colour of the player whose turn it is
    private static int opponentColour;
    static Board b;
    public static void main(String[] args) {

       // Main board = new Main();
       // board.createGUIBoard();




        /*
        while (true) {
            generateMoves();
            int currentSquare = UserInput.getValidSquareInput("Enter the current piece's location:");

            System.out.println(currentSquare);
            System.out.println(b.square[currentSquare]);
            System.out.println(UserInput.convertSquareToInput(currentSquare));
            int piece = b.square[currentSquare];
            System.out.println("Piece: " + Piece.FENtoString(piece) + " , aka "  + piece);
            boolean invalid = true;
            while (invalid) {
                if (!UserInput.isCorrectColour(friendlyColour,piece)) {
                    System.out.println("INVALID");
                    currentSquare = UserInput.getValidSquareInput("Enter the current piece's location:");

                    System.out.println("Piece: " + Piece.FENtoString(piece) + " , aka " + piece);
                } else {
                    invalid = false;
                }
            }


         */
        b = new Board();
        b.printBoard();
        friendlyColour = b.colourToMove;
        boolean finished = false;
        boolean valid_move = false;
        int counter = 0;
        while (!finished) {
            System.out.println();
            System.out.println( playerNumToString(friendlyColour)+ "'s turn:");
            int newSquare;
            int currentSquare;
            do {
                currentSquare = UserInput.getValidSquareInput(friendlyColour);
                int piece = b.square[currentSquare];
                System.out.println("Selected Piece: " + Piece.FENtoString(piece));
                newSquare = UserInput.getValidMoveSquare(currentSquare, piece, friendlyColour, opponentColour);
                valid_move = true;
            } while (!valid_move);

            //update board
            {
                //System.out.println("got: " + newSquare);
                b.updateBoard(currentSquare, newSquare);
                b.printBoard();
            }

             b.switchTurn();  // Assuming colourToMove is either Piece.White or Piece.Black
            friendlyColour = b.colourToMove;
            opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;

            if (counter > 10) {
                finished = true; }
            counter++;


        }
        /* CHESS LOOP PSEUDOCODE:
        WHILE game is not over:
            DISPLAY board with ranks (1-8) and files (A-H)
            PRINT whose turn it is (WHITE or BLACK)

            REPEAT:
                ASK player for input (e.g., "e2 e4")
                VALIDATE input (correct format, valid piece, legal move)
            UNTIL valid move is given

            UPDATE board:
                Move piece from oldSquare to newSquare
                Handle captures
                Handle special moves (castling, en passant, pawn promotion)

            SWITCH turn (WHITE ↔ BLACK)

            CHECK for check or checkmate:
                IF checkmate:
                    PRINT "Checkmate! [Winner] wins."
                    END game
                ELSE IF stalemate:
                    PRINT "Stalemate! It's a draw."
                    END game

        END game
         */

            /*
            if (Piece.isColour(piece, board.friendlyColour)) {
                int targetSquare = UserInput.getValidMoveSquare(currentSquare, piece, board.friendlyColour, board.opponentColour);
                // Move the piece (logic to update board state goes here)
                // Example: Board.movePiece(currentSquare, targetSquare);

                // After each move, switch turns
                b.switchTurn();
            } else {
                System.out.println("You must select a piece that belongs to you.");
            }

             */


    }

    private static String playerNumToString(int playerNum) {
        if (playerNum == 8) {return "White";}
        return "Black";
    }

    public static int getFriendlyColour() {
        return friendlyColour;
    }


    // Method to generate the moves

}
