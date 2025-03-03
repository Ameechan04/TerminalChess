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
        List<Move> validMoves = generateValidMoves(currentSquare, piece, friendlyColour);

        // If there are no valid moves for the selected piece, prompt the user to select another piece
        if (validMoves.isEmpty()) {
            System.out.println("No valid moves available for this piece. Please select another piece.");
            return -1;  // Indicating that no move was made
        }

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
        moves = new ArrayList<>();
        int pieceOnTargetSquare = b.square[targetSquare];
        int opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
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
                    moves = generatePawnMoves(currentSquare,piece,friendlyColour,opponentColour);
                    break;
                case Piece.KNIGHT:
                    moves = generateKnightMoves(currentSquare,piece,friendlyColour,opponentColour);
                    break;
                case Piece.BISHOP, Piece.ROOK, Piece.QUEEN:
                    moves = generateSlidingMoves(currentSquare,piece,friendlyColour,opponentColour);
                    break;
                case Piece.KING:
                    moves = generateKingMoves(currentSquare,piece,friendlyColour,opponentColour);
                    break;
                default:
                    System.out.println("Unknown piece type.");
                    return false;
            }

            // Check if the target square is in the list of valid moves for the piece
            if (moves.isEmpty()) {
                System.out.println("No available moves with this piece, select another piece");


                return false;
            }
            for (Move move : moves) {
                if (move.getTargetSquare() == targetSquare) {
                    return true;
                }
            }

            System.out.println("Invalid move for this piece.");
            return false;

        } else {
            System.out.println("No piece of your colour at the selected square.");
            return false;
        }
    }

    public static List<Move> generateKingMoves(int startSquare, int piece, int friendlyColour, int opponentColour) {
        List<Move> moves = new ArrayList<>();
        int[] kingOffsets = {-9, -8, -7, -1, 1, 7, 8, 9};

        for (int offset : kingOffsets) {
            int targetSquare = startSquare + offset;

            // Ensure the target square is within the bounds of the board
            if (targetSquare >= 0 && targetSquare < 64) {
                int targetRow = targetSquare / 8;
                int targetCol = targetSquare % 8;
                int startRow = startSquare / 8;
                int startCol = startSquare % 8;

                // Ensure the move does not wrap around the board
                if (Math.abs(targetCol - startCol) <= 1) {
                    int pieceOnTargetSquare = b.square[targetSquare];

                    // If the target square is empty or contains an opponent's piece
                    if (pieceOnTargetSquare == Piece.NONE || Piece.isColour(pieceOnTargetSquare, opponentColour)) {
                        // Create a move object for the potential move
                        Move potentialMove = new Move(startSquare, targetSquare, pieceOnTargetSquare);

                        // Check if the move results in a check for the king
                        if (!CheckIfMoveResultsInCheck(potentialMove, friendlyColour)) {
                            moves.add(potentialMove);
                        }
                    }
                }
            }
        }
        return moves;
    }




    public static boolean isSquareAttacked(int square, int attackingColour) {
        List<Move> opponentMoves = generateAllMoves(attackingColour);
        for (Move move : opponentMoves) {
            if (move.getTargetSquare() == square) {
                return true; // The square is attacked
            }
        }
        return false;
    }

        /*
        for (Move move : moves) {
            System.out.println("Legal move: " + convertSquareToInput(move.getStartSquare()) + " to " + convertSquareToInput(move.getTargetSquare()));
        }

         */




    public static List<Move> generateKnightMoves(int startSquare, int piece, int friendlyColour, int opponentColour) {
        moves = new ArrayList<>();

        // List of all possible knight move offsets (8 possible L-shaped moves)
        int[] knightOffsets = {-17, -15, -10, -6, 6, 10, 15, 17};

        // Loop through each of the 8 possible moves
        for (int offset : knightOffsets) {
            int targetSquare = startSquare + offset;

            // Check if the move stays within the bounds of the board
            // A knight's move must not go off the board, especially in columns 0 and 7
            if (targetSquare >= 0 && targetSquare < 64) {
                int targetRow = targetSquare / 8;
                int targetCol = targetSquare % 8;
                int startRow = startSquare / 8;
                int startCol = startSquare % 8;

                // Ensure the knight doesn't "wrap around" the board from one row to another in invalid ways
                if (Math.abs(targetCol - startCol) == 2 && Math.abs(targetRow - startRow) == 1 ||
                        Math.abs(targetCol - startCol) == 1 && Math.abs(targetRow - startRow) == 2) {

                    // Get the piece on the target square
                    int pieceOnTargetSquare = b.square[targetSquare];

                    // Check if the move is to a valid square (either empty or opponent's piece)
                    if (pieceOnTargetSquare == Piece.NONE) {
                        moves.add(new Move(startSquare, targetSquare, 0));
                    }

                    if (Piece.isColour(pieceOnTargetSquare, opponentColour)) {
                        moves.add(new Move(startSquare, targetSquare, pieceOnTargetSquare));
                    }
                }
            }
        }

        /*
        for (Move move : moves) {
            System.out.println("Legal move: " + convertSquareToInput(move.getStartSquare()) + " to " + convertSquareToInput(move.getTargetSquare()));
        }

         */

        return moves;
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
                moves.add(new Move(startSquare, forwardSquare, 0));
            }
        }

        // Check for double pawn move (2 squares forward on the first move)
        if ((startSquare >= 8 && startSquare <= 15 && friendlyColour == Piece.WHITE) || (startSquare >= 48 && startSquare <= 55 && friendlyColour == Piece.BLACK)) {
            int doubleForwardSquare = startSquare + (direction * 2 * 8);
            if (doubleForwardSquare >= 0 && doubleForwardSquare < 64) {
                int pieceOnDoubleForwardSquare = b.square[doubleForwardSquare];
                if (pieceOnDoubleForwardSquare == NONE) {
                    moves.add(new Move(startSquare, doubleForwardSquare, 0));
                }
            }
        }

        // Check for capturing moves (diagonal left and right)
        int[] captureOffsets = (direction == 1) ? new int[] {9, 7} : new int[] {-9, -7};

        for (int offset : captureOffsets) {
            int captureSquare = startSquare + offset;
            if (captureSquare >= 0 && captureSquare < 64) {
                int pieceOnCaptureSquare = b.square[captureSquare];
                // Check if the square is not empty and contains an opponent's piece
                if (pieceOnCaptureSquare != Piece.NONE && Piece.isColour(pieceOnCaptureSquare, opponentColour)) {
                    moves.add(new Move(startSquare, captureSquare, pieceOnCaptureSquare));  // Capture opponent's piece
                }
            }
        }


        return moves;
    }

    public static List<Move> generateSlidingMoves(int startSquare, int piece, int friendlyColour, int opponentColour) {
        int startDirIndex = (Piece.isType(piece, Piece.BISHOP)) ? 4 : 0;
        int endDirIndex = (Piece.isType(piece, Piece.ROOK)) ? 4 : 8;
        moves = new ArrayList<>();
        for (int directionIndex = startDirIndex; directionIndex < endDirIndex; directionIndex++) {
            for (int n = 0; n < numSquaresToEdge[startSquare][directionIndex]; n++) {
                int targetSquare = startSquare + directionOffsets[directionIndex] * (n + 1);
                int pieceOnTargetSquare = b.square[targetSquare];

                // Blocked by friendly piece, so can't move any further in this direction
                if (Piece.isColour(pieceOnTargetSquare, friendlyColour)) {
                    break;
                }


                moves.add(new Move(startSquare, targetSquare, pieceOnTargetSquare));
               // System.out.println("Moving from " + convertSquareToInput(startSquare) + " to " + convertSquareToInput(targetSquare) + " is legal");

                // Can't move any further in this direction after capturing opponent's piece
                if (Piece.isColour(pieceOnTargetSquare, opponentColour)) {
                    break;
                }
            }
        }
        return moves;
    }


    public static List<Move> generateValidMoves(int currentSquare, int piece, int friendlyColour) {
        int opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
        List<Move> moves = new ArrayList<>();

        int pieceType = piece & 0b111;
        switch (pieceType) {
            case Piece.PAWN:
                moves = generatePawnMoves(currentSquare, piece, friendlyColour, opponentColour);
                break;
            case Piece.KNIGHT:
                moves = generateKnightMoves(currentSquare, piece, friendlyColour, opponentColour);
                break;
            case Piece.BISHOP, Piece.ROOK, Piece.QUEEN:
                moves = generateSlidingMoves(currentSquare, piece, friendlyColour, opponentColour);
                break;
            case Piece.KING:
                moves = generateKingMoves(currentSquare, piece, friendlyColour, opponentColour);
                break;
            default:
                System.out.println("Unknown piece type.");
                break;
        }
        return moves;
    }

    public static List<Move> generateAllMoves(int friendlyColour) {
        int opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
        List<Move> legalMoves = new ArrayList<>();

        for (int currentSquare = 0; currentSquare < 64; currentSquare++) {
            int piece = b.square[currentSquare];

            if (Piece.isColour(piece, friendlyColour)) {

                int pieceType = piece & 0b111;


                switch (pieceType) {
                    case Piece.PAWN:
                        legalMoves.addAll(generatePawnMoves(currentSquare, piece, friendlyColour, opponentColour));
                        break;
                    case Piece.KNIGHT:
                        legalMoves.addAll(generateKnightMoves(currentSquare, piece, friendlyColour, opponentColour));
                        break;
                    case Piece.BISHOP, Piece.ROOK, Piece.QUEEN:
                        legalMoves.addAll(generateSlidingMoves(currentSquare, piece, friendlyColour, opponentColour));
                        break;
                    case Piece.KING:
                        legalMoves.addAll(generateKingMoves(currentSquare, piece, friendlyColour, opponentColour));
                        break;
                    default:
                        System.out.println("Unknown piece type.");
                        break;
                }

            }
        }
        return legalMoves;
    }

    public static List<Move> generateLegalMoves (int friendlyColour) {
        int opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
        List<Move> pseudoLegalMoves = generateAllMoves(friendlyColour);
        List<Move> legalMoves = new ArrayList<>();
        int myKingSquare = b.getKingSquare(friendlyColour);
        int enemyKingSquare = b.getKingSquare(opponentColour);

        System.out.println("/DEB/ Current player is at " + friendlyColour);
        System.out.println("/DEB/ Enemy King at = " + convertSquareToInput(enemyKingSquare));
        System.out.println("/DEB/ My King at = " + (myKingSquare));
        for (Move moveToVerify : pseudoLegalMoves) {
           // System.out.println("/DEB/ testing a pseudo legal move: ");
            //moveToVerify.printMove(); System.out.println();

            b.updateBoard(moveToVerify);
            List<Move> opponentResponses  = generateAllMoves(opponentColour);

            boolean kingCaptured = false;
            for (Move response : opponentResponses) {
                if (response.getTargetSquare() == myKingSquare) {
                    kingCaptured = true;
                    break;
                }
            }

            if (!kingCaptured) {
                legalMoves.add(moveToVerify);
            }

            //unmake move
            b.unmakeMove(moveToVerify);
        }
        return legalMoves;

    }

    //

    public static boolean isGameOver(int friendlyColour, boolean inCheck) {
        List<Move> legalMoves = generateLegalMoves(friendlyColour);

        if (inCheck) {
            System.out.println("King is in check!");
        }
        if (legalMoves.isEmpty()) {
            if (inCheck) {
                System.out.println("Checkmate! " + Main.playerNumToString(friendlyColour) + " loses.");
            } else {
                System.out.println("Stalemate! No legal moves but not in check.");
            }
            return true;
        }
        return false;
    }

    /*If WHITE: generates all of whites moves that could put the black king in check */
    public static List<Move> generateMovesThatKillEnemyKing(int friendlyColour){
        int opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
        List<Move> pseudoLegalMoves = generateAllMoves(friendlyColour);
        List<Move> kingCapturingMoves = new ArrayList<>();
        int enemyKingSquare = b.getKingSquare(opponentColour);

        /*
        System.out.println("/DEB/ Current player is at " + friendlyColour);
        System.out.println("/DEB/ Enemy King at = " + convertSquareToInput(enemyKingSquare));
        System.out.println("/DEB/ Total pseudo-legal moves: " + pseudoLegalMoves.size());


         */
        for (Move moveToVerify : pseudoLegalMoves) {
            b.updateBoard(moveToVerify);
            List<Move> opponentResponses  = generateAllMoves(opponentColour);
            boolean kingCaptured = false;
            for (Move response : opponentResponses) {
               // System.out.println("/DEB/ Checking move: " + convertSquareToInput(response.getTargetSquare()));

                if (response.getTargetSquare() == enemyKingSquare) {
                    kingCaptured = true;
                   // System.out.println("/DEB/ Move found that captures king!");
                    break;
                }
            }

            if (kingCaptured) {
                kingCapturingMoves.add(moveToVerify);
            }

            //unmake move
            b.unmakeMove(moveToVerify);
        }
        return kingCapturingMoves;

    }

    public static List<Move> generateMovesThatResolveCheck(int friendlyColour) {
        int myKingSquare = b.getKingSquare(friendlyColour);
        int opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
        List<Move> validMoves = new ArrayList<>();

        // Generate all possible moves
        List<Move> pseudoLegalMoves = generateAllMoves(friendlyColour);

        // Check if the king is in check and filter moves
        for (Move move : pseudoLegalMoves) {
            // Temporarily make the move
            b.updateBoard(move);

            // Check if the move resolves the check (king is not in check after the move)
            boolean isInCheck = isKingInCheck(friendlyColour);
            if (!isInCheck) {
                validMoves.add(move);  // Add move if it resolves the check
            }

            // Undo the move
            b.unmakeMove(move);
        }

        return validMoves;
    }

    public static boolean isKingInCheck(int friendlyColour) {
        int opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;

        List<Move> killinKingMoves = UserInput.generateMovesThatKillEnemyKing(opponentColour);

        if (killinKingMoves.isEmpty()) {
            return false;
        }
        return true;

    }


    public static boolean CheckIfMoveResultsInCheck(Move move, int friendlyColour) {
        int opponentColour = (friendlyColour == Piece.WHITE) ? Piece.BLACK : Piece.WHITE;

        // Simulate the move
        b.updateBoard(move);

        // Find the new position of the king
        int kingSquare = b.getKingSquare(friendlyColour);

        // Check if any opponent piece attacks the king, WITHOUT using generateAllMoves
        boolean inCheck = isKingAttacked(kingSquare, opponentColour);

        // Undo the move
        b.unmakeMove(move);

        return inCheck;
    }

    public static boolean isKingAttacked(int kingSquare, int opponentColour) {
        // Check for pawn attacks
        if (isPawnAttackingKing(kingSquare, opponentColour)) {
            return true;
        }

        // Check for knight attacks
        if (isKnightAttackingKing(kingSquare, opponentColour)) {
            return true;
        }

        // Check for sliding piece attacks (bishops, rooks, queen)
        if (isSlidingPieceAttackingKing(kingSquare, opponentColour)) {
            return true;
        }

        return false;
    }

    public static boolean isPawnAttackingKing(int kingSquare, int opponentColour) {
        int direction = (opponentColour == Piece.WHITE) ? -1 : 1; // Opponent pawns move in opposite direction
        int[] pawnAttackOffsets = {direction * 9, direction * 7};

        for (int offset : pawnAttackOffsets) {
            int attackSquare = kingSquare + offset;
            if (attackSquare >= 0 && attackSquare < 64) {
                if (Piece.isPawn(b.square[attackSquare]) && Piece.isColour(b.square[attackSquare], opponentColour)) {
                    return true; // King is under attack by a pawn
                }
            }
        }
        return false;
    }

    public static boolean isKnightAttackingKing(int kingSquare, int opponentColour) {
        int[] knightOffsets = {-17, -15, -10, -6, 6, 10, 15, 17};

        for (int offset : knightOffsets) {
            int attackSquare = kingSquare + offset;
            if (attackSquare >= 0 && attackSquare < 64) {
                if (Piece.isKnight(b.square[attackSquare]) && Piece.isColour(b.square[attackSquare], opponentColour)) {
                    return true; // King is under attack by a knight
                }
            }
        }
        return false;
    }

    public static boolean isSlidingPieceAttackingKing(int kingSquare, int opponentColour) {
        int friendlyColour = (opponentColour == WHITE) ? BLACK : WHITE;

        int[][] directions = {
                {-9, -7, 7, 9}, // Bishop & Queen diagonals
                {-8, 8, -1, 1}  // Rook & Queen straight lines
        };

        for (int i = 0; i < directions.length; i++) {
            for (int offset : directions[i]) {
                int attackSquare = kingSquare;
                while (true) {
                    attackSquare += offset;
                    if (attackSquare < 0 || attackSquare >= 64) break;

                    int pieceOnSquare = b.square[attackSquare];

                    if (pieceOnSquare == Piece.NONE) continue;
                    if (Piece.isColour(pieceOnSquare, friendlyColour)) break; // Blocked by friendly piece

                    if ((i == 0 && (Piece.isBishop(pieceOnSquare) || Piece.isQueen(pieceOnSquare))) ||
                            (i == 1 && (Piece.isRook(pieceOnSquare) || Piece.isQueen(pieceOnSquare)))) {
                        return true; // King is attacked
                    }

                    break; // Stop checking this direction after encountering a piece
                }
            }
        }
        return false;
    }





}
