import Pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static Pieces.Piece.*;
import static Pieces.Piece.directionOffsets;

public class UserInput {
    private static List<Move> moves;
    static Board b = Main.b;
    private static Scanner scanner = new Scanner(System.in);

    // Method to prompt the user for a valid square (0-63)
    //Only checks if on the board
    public static int getValidSquareInput(int friendlyColour) {
        int square = -1;
        boolean validColour = false;
        while (square < 0 || square >= 64 || !validColour) {
            System.out.println("Enter the location of the piece to move: ");
            System.out.println("You are " + friendlyColour);
            String input = scanner.nextLine();
            try {
                square = convertInputToSquare(input);
                if (square < 0 || square >= 64) {
                    System.out.println("Invalid square. Please enter a valid square (e.g., 'a2', 'h5').");
                }

                validColour = isCorrectColour(friendlyColour, square);
                if (!validColour) {
                    System.out.println("Invalid square. Must select your own colour!");
                }


            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input format. Please enter a square in chess notation (e.g., 'a2', 'h5').");
            }
        }
        return square;
    }

    // Converts a chess notation input (e.g., 'a2') to a square index (0-63)
    public static int convertInputToSquare(String input) {
        input = input.toLowerCase();
        if (input.length() != 2) {
            throw new IllegalArgumentException("Invalid format");
        }

        char file = input.charAt(0); // 'a' to 'h'
        char rank = input.charAt(1); // '1' to '8'

        // Validate the file and rank
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Invalid chess notation");
        }

        int fileIndex = file - 'a';
        int rankIndex = rank - '1';
        return rankIndex * 8 + fileIndex; // Calculate the square index
    }

    public static String convertSquareToInput(int square) {
        if (square < 0 || square >= 64) {
            throw new IllegalArgumentException("Invalid square index");
        }

        int fileIndex = square % 8; // Columns (files)
        int rankIndex = square / 8; // Rows (ranks)

        // Convert fileIndex to chess notation (a-h)
        char file = (char) ('a' + fileIndex);

        // Convert rankIndex to chess notation (1-8)
        char rank = (char) ('1' + rankIndex);

        // Combine file and rank to create the standard chess notation
        return "" + file + rank;
    }


    // Prompts for a valid target square and checks that the move is legal
    public static int getValidMoveSquare(int currentSquare, int piece, int friendlyColour, int opponentColour) {
        int targetSquare = -1;
        while (targetSquare < 0 || targetSquare >= 64 || !isValidMove(currentSquare, targetSquare, piece, friendlyColour)) {
            System.out.println("You have selected piece " + Piece.FENtoString(piece));
            System.out.println("Enter the target square for your move (e.g., 'a3', 'h6'):");
            String input = scanner.nextLine();
            try {
                targetSquare = convertInputToSquare(input);
                if (targetSquare < 0 || targetSquare >= 64) {
                    System.out.println("Invalid square. Please enter a valid square.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input format. Please enter a valid square.");
            }
        }
       // System.out.println("Moving to square " + targetSquare +  "...");
        return targetSquare;
    }

    public static boolean isCorrectColour(int friendlyColour, int boardSquare) {
        //if colour matches the players colour
        int piece = b.square[boardSquare];
        return Piece.isColour(piece, friendlyColour);
    }

    // Validates if the move is allowed for the piece (based on piece type, colour, etc.)
    private static boolean isValidMove(int currentSquare, int targetSquare, int piece, int friendlyColour) {
        // You can add piece-specific move validation logic here based on piece type (e.g., pawn, rook, knight)
        // For simplicity, we'll assume the piece is valid if it's of the correct colour and the target square is empty or contains an opponent's piece.

        int pieceOnTargetSquare = b.square[targetSquare];

        // Ensure the piece belongs to the correct player
        if (Piece.isColour(piece, friendlyColour)) {
            // Ensure the target square doesn't contain a friendly piece
            if (Piece.isColour(pieceOnTargetSquare, friendlyColour)) {
                System.out.println("You cannot move to a square occupied by your own piece.");
                return false;
            }
            int pieceType = piece & 0b111;
            switch (pieceType) {
                case Piece.PAWN:
                    return isValidPawnMove(currentSquare, targetSquare);
                case Piece.KNIGHT:
                    return isValidKnightMove(currentSquare, targetSquare);
                case Piece.BISHOP, Piece.ROOK, Piece.QUEEN:
                    return isSlidingPieceMove(currentSquare, targetSquare);
                case Piece.KING:
                    return isValidKingMove(currentSquare, targetSquare);
                default:
                    System.out.println("Unknown piece type.");
                    return false;
            }
        } else {
            System.out.println("No piece of your colour at the selected square.");
            return false;
        }
    }

    private static boolean isValidKingMove(int currentSquare, int targetSquare) {
        return false;
    }


    private static boolean isValidKnightMove(int currentSquare, int targetSquare) {
        return false;
    }

    private static boolean isValidPawnMove(int currentSquare, int targetSquare) {
        int piece = b.square[currentSquare];
        int friendlyColour = b.colourToMove;
        int opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
        List<Move> pawnMoves = generatePawnMoves(currentSquare, piece, friendlyColour, opponentColour);


        // Check if the target square is in the list of valid moves for the piece
        if (pawnMoves.isEmpty()) {
            System.out.println("No available moves with this pawn.");
            return false;
        }

        for (Move move : pawnMoves) {
            if (move.getTargetSquare() == targetSquare) {
                return true;  // Valid move found
            }
        }

        System.out.println("Invalid move for this pawn piece.");
        return false;
    }


    //TODO: MISSING EN PASSANT
    public static List<Move> generatePawnMoves(int startSquare, int piece, int friendlyColour, int opponentColour) {
        moves = new ArrayList<>();

        int direction = (Piece.isColour(piece, Piece.WHITE)) ? 1 : -1; // White moves up (1), Black moves down (-1)


        // Check for normal pawn move (1 square forward)
        int forwardSquare = startSquare + (direction * 8);
        if (forwardSquare >= 0 && forwardSquare < 64) {
            int pieceOnForwardSquare = b.square[forwardSquare];
            if (pieceOnForwardSquare == Piece.NONE) {
                moves.add(new Move(startSquare, forwardSquare));
            }
        }

        // Check for double pawn move (2 squares forward on the first move)
        if ((startSquare >= 8 && startSquare <= 15 && friendlyColour == Piece.WHITE) || (startSquare >= 48 && startSquare <= 55 && friendlyColour == Piece.BLACK)) {
            int doubleForwardSquare = startSquare + (direction * 2 * 8);
            if (doubleForwardSquare >= 0 && doubleForwardSquare < 64) {
                int pieceOnDoubleForwardSquare = b.square[doubleForwardSquare];
                if (pieceOnDoubleForwardSquare == NONE) {
                    moves.add(new Move(startSquare, doubleForwardSquare));
                }
            }
        }

        // Check for capturing moves (diagonal left and right)
        int[] captureOffsets = (direction == 1) ? new int[] {9, 7} : new int[] {-9, -7};

        for (int offset : captureOffsets) {
            int captureSquare = startSquare + offset;
            if (captureSquare >= 0 && captureSquare < 64) {
                int pieceOnCaptureSquare = b.square[captureSquare];
                if (Piece.isColour(pieceOnCaptureSquare, opponentColour)) {
                    moves.add(new Move(startSquare, captureSquare));  // Capture opponent's piece
                    System.out.println("Capture move: " + convertSquareToInput(startSquare) + " to " + convertSquareToInput(captureSquare));
                }
            }
        }

        for (Move move: moves) {
             System.out.println("Moving from " + convertSquareToInput(move.getStartSquare()) + " to " + convertSquareToInput(move.getTargetSquare()) + " is legal");

        }
        return moves;
    }

    public static List<Move> generateSlidingMoves(int startSquare, int piece, int friendlyColour, int opponentColour) {
        int startDirIndex = (Piece.isType(piece, Piece.BISHOP)) ? 4 : 0;
        int endDirIndex = (Piece.isType(piece, Piece.ROOK)) ? 4 : 8;
        moves = new ArrayList<>();
        for (int directionIndex = startDirIndex; directionIndex < endDirIndex; directionIndex++) {

            System.out.println(directionIndex + ", " + startDirIndex);
            for (int n = 0; n < numSquaresToEdge[startSquare][directionIndex]; n++) {
                int targetSquare = startSquare + directionOffsets[directionIndex] * (n + 1);
                int pieceOnTargetSquare = b.square[targetSquare];

                // Blocked by friendly piece, so can't move any further in this direction
                if (Piece.isColour(pieceOnTargetSquare, friendlyColour)) {
                    break;
                }

                moves.add(new Move(startSquare, targetSquare));
               // System.out.println("Moving from " + convertSquareToInput(startSquare) + " to " + convertSquareToInput(targetSquare) + " is legal");

                // Can't move any further in this direction after capturing opponent's piece
                if (Piece.isColour(pieceOnTargetSquare, opponentColour)) {
                    break;
                }
            }
        }
        return moves;
    }

    private static boolean isSlidingPieceMove(int currentSquare, int targetSquare) {
        // Generate all valid sliding moves for the piece from the current square
        int piece = b.square[currentSquare];
        int friendlyColour = b.colourToMove;
        int opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
        List<Move> slidingMoves = generateSlidingMoves(currentSquare, piece, friendlyColour, opponentColour);


        // Check if the target square is in the list of valid moves for the piece
        if (slidingMoves.isEmpty()) {
            System.out.println("No available moves with this piece.");
            return false;
        }
        for (Move move : slidingMoves) {
            if (move.getTargetSquare() == targetSquare) {
                return true;  // Valid move found
            }
        }

        System.out.println("Invalid move for this sliding piece.");
        return false;
    }

}
