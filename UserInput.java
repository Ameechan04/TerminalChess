import Pieces.Piece;

import java.util.Scanner;

public class UserInput {
    private static Scanner scanner = new Scanner(System.in);

    // Method to prompt the user for a valid square (0-63)
    public static int getValidSquareInput(String prompt) {
        int square = -1;
        while (square < 0 || square >= 64) {
            System.out.println(prompt);
            String input = scanner.nextLine();
            try {
                square = convertInputToSquare(input);
                if (square < 0 || square >= 64) {
                    System.out.println("Invalid square. Please enter a valid square (e.g., 'a2', 'h5').");
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
        while (targetSquare < 0 || targetSquare >= 64 || !isValidMove(currentSquare, targetSquare, piece, friendlyColour, opponentColour)) {
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
        return targetSquare;
    }

    // Validates if the move is allowed for the piece (based on piece type, colour, etc.)
    private static boolean isValidMove(int currentSquare, int targetSquare, int piece, int friendlyColour, int opponentColour) {
        // You can add piece-specific move validation logic here based on piece type (e.g., pawn, rook, knight)
        // For simplicity, we'll assume the piece is valid if it's of the correct colour and the target square is empty or contains an opponent's piece.

        int pieceOnTargetSquare = Board.square[targetSquare];

        // Ensure the piece belongs to the correct player
        if (Piece.isColour(piece, friendlyColour)) {
            // Ensure the target square doesn't contain a friendly piece
            if (Piece.isColour(pieceOnTargetSquare, friendlyColour)) {
                System.out.println("You cannot move to a square occupied by your own piece.");
                return false;
            }

            // Further validation for each piece type (rook, bishop, etc.) can be added here
            return true;
        } else {
            System.out.println("No piece of your colour at the selected square.");
            return false;
        }
    }

}
