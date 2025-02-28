import Pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import static Pieces.Piece.*;

public class Main {
    static final char LIGHT_SQUARE = '□';
    static final char DARK_SQUARE = '■';
    private int friendlyColour;  // The colour of the player whose turn it is
    private int opponentColour;
    public static void main(String[] args) {
        Main board = new Main();
       // board.createGUIBoard();
        Board b = new Board();
        b.initialBoard();
        while (true) {
            board.generateMoves();
            int currentSquare = UserInput.getValidSquareInput("Enter the current piece's location:");
            System.out.println(currentSquare);
            System.out.println(Board.square[currentSquare]);
            System.out.println(UserInput.convertSquareToInput(currentSquare));
            int piece = Board.square[currentSquare];

            System.out.println("Piece: " + Piece.FENtoString(piece) + " , aka "  + piece);

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

    }

    void createGUIBoard() {
        System.out.println("This is the GUI Board");
        for (int rank = 7; rank >= 0; rank--) { // Ranks go from 8 to 1 in chess
            for (int file = 0; file < 8; file++) { // Files go from a to h
                boolean isLightSquare = (file + rank) % 2 != 0;
                char squareSymbol = isLightSquare ? LIGHT_SQUARE : DARK_SQUARE;
                System.out.print(squareSymbol + " ");
            }
            System.out.println(); // Move to the next rank
        }
    }

    private List<Move> moves;

    // Method to generate the moves
    public List<Move> generateMoves() {
        moves = new ArrayList<>();
        friendlyColour = Board.colourToMove;  // Assuming colourToMove is either Piece.White or Piece.Black
        opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;

        for (int startSquare = 0; startSquare < 64; startSquare++) {
            int piece = Board.square[startSquare];  // Assuming Board is a class with a square array
            if (Piece.isColour(piece, Board.colourToMove)) {  // Assuming Piece is a class with relevant methods
                if (Piece.isSlidingPiece(piece)) {  // Assuming Piece is a class with relevant methods
                    generateSlidingMoves(startSquare, piece);  // Assuming this method exists
                }
            }
        }

        return moves;
    }

    public void generateSlidingMoves(int startSquare, int piece) {
        int startDirIndex = (Piece.isType(piece, Piece.BISHOP)) ? 4 : 0;
        int endDirIndex = (Piece.isType(piece, Piece.ROOK)) ? 4 : 8;

        for (int directionIndex = startDirIndex; directionIndex < endDirIndex; directionIndex++) {
            for (int n = 0; n < numSquaresToEdge[startSquare][directionIndex]; n++) {
                int targetSquare = startSquare + directionOffsets[directionIndex] * (n + 1);
                int pieceOnTargetSquare = Board.square[targetSquare];

                // Blocked by friendly piece, so can't move any further in this direction
                if (Piece.isColour(pieceOnTargetSquare, friendlyColour)) {
                    break;
                }

                moves.add(new Move(startSquare, targetSquare));

                // Can't move any further in this direction after capturing opponent's piece
                if (Piece.isColour(pieceOnTargetSquare, opponentColour)) {
                    break;
                }
            }
        }
    }
}
