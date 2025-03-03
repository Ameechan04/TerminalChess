import Pieces.Piece;

public class Board {
    int[] square;
    int colourToMove;
    public int enPassantSquare;


    //for styling:
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String ANSI_GREY_BACKGROUND = "\033[48;5;250m";
    public static final String FLASHING = "\033e[5;Nm";
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    public static final String WHITE_BOLD = "\033[1;97m"; // WHITE
    public static final String ANSI_BLUE = "\u001B[34m";

    public static final String ANSI_GREEN_BACKGROUND = "\u001B[102m";
    public static final String ANSI_DARK_GREEN_BACKGROUND = "\033[48;5;35m";
    public static final String ANSI_YELLOW_BACKGROUND = "\033[30;43m";
    public static final String ANSI_RESET = "\u001B[0m";


    // Constructor
    public Board(String fen) {
        square  = new int[64];  // Instance variable instead of static
        enPassantSquare = -1;
        Piece.precomputerMoveData();
        colourToMove = Piece.WHITE;
       // String fen = "rnbqkbnr/8/8/8/8/8/8/RNBQKBNR w KQkq - 0 1";
        parseFen1DArray(fen);
    }

    // Method to update board after a move
    public void updateBoard(int oldSquare, int newSquare) {

            int piece = square[oldSquare];
            square[oldSquare] = Piece.NONE;  // Clear the old square
            square[newSquare] = piece;       // Place the piece on the new square


    }

    public void updateBoard(Move move) {

        int piece = square[move.startSquare];
        square[move.startSquare] = Piece.NONE;  // Clear the old square
        square[move.targetSquare] = piece;       // Place the piece on the new square


    }

    // Print the current FEN board
    public void printFENBoard() {
        int counter = 1;
        for (int i = 63; i >= 0; i--) {
            if (counter % 8 == 0) {
                System.out.println(square[i]);
            } else {
                System.out.print(square[i] + ",");
            }
            counter++;
        }
    }
    public void printBoard() {
        System.out.println(ANSI_RESET);
        // Print the column labels (A to H)
        System.out.print("   ");
        for (char c = 'A'; c <= 'H'; c++) {

            System.out.print(ANSI_DARK_GREEN_BACKGROUND + " " + c + " ");
        }

        System.out.println(ANSI_RESET);
        boolean coloured = false;
        int counter = 0;
        // Iterate through the 1D array in reverse to match board from rank 8 to 1
        for (int row = 7; row >= 0; row--) {
            // Print the row number (1 to 8)
            System.out.print(ANSI_DARK_GREEN_BACKGROUND + (row + 1) + "  " + ANSI_RESET);
            // Print the pieces for the current row

            if (coloured) {
                System.out.print(ANSI_YELLOW_BACKGROUND);
                coloured = false;
            } else {
                System.out.print(ANSI_BLACK_BACKGROUND);
                coloured = true;
            }
            if (row == 7) {
                System.out.print(ANSI_RESET);
            }

            for (int col = 0; col < 8; col++) {
                if (coloured) {
                    System.out.print(ANSI_YELLOW_BACKGROUND);
                    coloured = false;
                } else {
                    System.out.print(BLACK_BACKGROUND_BRIGHT);
                    coloured = true;
                }
                int squareIndex = row * 8 + col;  // Calculate the 1D index for the current square

                    String pieceChar = FENtoString(square[squareIndex]);
                    //IS WHITE
                    if (pieceChar.equals(".")) {
                        System.out.print("   ");
                    } else {
                        if (pieceChar.equals(pieceChar.toUpperCase())) {
                            System.out.print(WHITE_BOLD + " " + FENtoString(square[squareIndex]) + " ");
                        } else {
                            System.out.print(BLACK_BOLD + " " + FENtoString(square[squareIndex]) + " ");
                        }
                    }


                    System.out.print(ANSI_RESET);

            }

            System.out.println(); // Newline after each row
        }
    }

    public void printBoardDebug() {
        // Print the column labels (A to H)
        System.out.print("   ");
        for (char c = 'A'; c <= 'H'; c++) {
            System.out.print(c + " ");
        }
        System.out.println();

        // Iterate through the 1D array in reverse to match board from rank 8 to 1
        int counter = 1;
        for (int row = 7; row >= 0; row--) {
            // Print the row number (1 to 8)
            System.out.print((row + 1) + "  ");

            // Print the pieces for the current row
            for (int col = 0; col < 8; col++) {
                int squareIndex = row * 8 + col;  // Calculate the 1D index for the current square
                System.out.print(FENtoString(square[squareIndex]) + squareIndex + " ");
            }

            System.out.println(); // Newline after each row
        }
    }


    public String FENtoString(int piece) {
        // If the piece is None, return a dot to represent an empty square
        if (piece == Piece.NONE) {
            return ".";  // Empty square
        }

        // Extract the piece type (lower 3 bits)
        int pieceType = piece & 7;  // Mask to get the type of the piece

        // Extract the color (upper 2 bits)
        boolean isWhite = (piece >> 3) == 1;  // Shift and compare to determine color

        // Determine the piece character based on the piece type
        char pieceChar = '.';
        switch (pieceType) {
            case Piece.PAWN:
                pieceChar = 'p';
                break;
            case Piece.ROOK:
                pieceChar = 'r';
                break;
            case Piece.KNIGHT:
                pieceChar = 'n';
                break;
            case Piece.BISHOP:
                pieceChar = 'b';
                break;
            case Piece.QUEEN:
                pieceChar = 'q';
                break;
            case Piece.KING:
                pieceChar = 'k';
                break;
        }

        // If the piece is white, convert the character to uppercase
        if (isWhite) {
            pieceChar = Character.toUpperCase(pieceChar);
        }

        // Return the piece character as a string
        return String.valueOf(pieceChar);
    }




    // Parse FEN string into a 2D board array
    public char[][] parseFEN(String fen) {
        char[][] board = new char[8][8];
        String[] parts = fen.split(" ");
        String rows = parts[0];
        String[] ranks = rows.split("/");

        for (int i = 0; i < 8; i++) {
            int file = 0; // Column index
            for (char c : ranks[i].toCharArray()) {
                if (Character.isDigit(c)) {
                    int emptySquares = Character.getNumericValue(c);
                    for (int j = 0; j < emptySquares; j++) {
                        board[i][file++] = '.'; // Using '.' for empty squares
                    }
                } else {
                    board[i][file++] = c; // Place the piece
                }
            }
        }
        return board;
    }

    // Print the 2D board
    public void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char square : row) {
                System.out.print(square + " ");
            }
            System.out.println();
        }
    }

    // Switch turns between players
    public void switchTurn() {
        colourToMove = (colourToMove == Piece.WHITE) ? Piece.BLACK : Piece.WHITE;
    }

    // Parse the FEN string into the 1D `square` array
    public void parseFen1DArray(String fen) {
        String[] parts = fen.split(" ");
        String rows = parts[0];  // The part before the space is the board layout

        String[] ranks = rows.split("/");  // Split by rank (rows)

        int squareIndex = 0;
        // Start from the bottom-most rank (8th rank in chess notation)
        for (int i = 7; i >= 0; i--) {
            String rank = ranks[i];
            for (char c : rank.toCharArray()) {
                if (Character.isDigit(c)) {
                    int emptySquares = Character.getNumericValue(c);
                    for (int j = 0; j < emptySquares; j++) {
                        square[squareIndex++] = Piece.NONE;  // Fill empty squares
                    }
                } else {
                    int piece = getPieceFromChar(c);  // Get the piece type from char
                    square[squareIndex++] = piece;
                }
            }
        }
    }

    // Get the piece value from a character (like 'p', 'r', 'P', 'R', etc.)
    public int getPieceFromChar(char c) {
        switch (c) {
            case 'p':
                return Piece.PAWN | Piece.BLACK;
            case 'r':
                return Piece.ROOK | Piece.BLACK;
            case 'n':
                return Piece.KNIGHT | Piece.BLACK;
            case 'b':
                return Piece.BISHOP | Piece.BLACK;
            case 'q':
                return Piece.QUEEN | Piece.BLACK;
            case 'k':
                return Piece.KING | Piece.BLACK;
            case 'P':
                return Piece.PAWN | Piece.WHITE;
            case 'R':
                return Piece.ROOK | Piece.WHITE;
            case 'N':
                return Piece.KNIGHT | Piece.WHITE;
            case 'B':
                return Piece.BISHOP | Piece.WHITE;
            case 'Q':
                return Piece.QUEEN | Piece.WHITE;
            case 'K':
                return Piece.KING | Piece.WHITE;
            default:
                return Piece.NONE;
        }
    }

    public int getKingSquare(int friendlyColour) {
        for (int i = 0; i < square.length; i++) {
            int piece = square[i];

            // Check if the piece is a king AND belongs to the correct colour
            if ((piece & 0b111) == Piece.KING && Piece.isColour(piece, friendlyColour)) {
                return i;
            }
        }
        return -1; // Return -1 if the king is not found
    }

    public void unmakeMove(Move move) {
        int piece = square[move.targetSquare];
        int capturePiece = move.getPieceCaptured();

        square[move.startSquare] = piece;
        square[move.targetSquare] = capturePiece;
    }

    public String boardToFen1DArray() {
        StringBuilder fen = new StringBuilder();

        for (int rank = 7; rank >= 0; rank--) {  // Process from rank 8 to rank 1
            int emptyCount = 0;

            for (int file = 0; file < 8; file++) {  // Process left to right within each rank
                int i = rank * 8 + file;  // Convert rank/file to 1D index
                int piece = square[i];

                if (piece == Piece.NONE) {
                    emptyCount++;  // Count consecutive empty squares
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);  // Append empty squares
                        emptyCount = 0;  // Reset count
                    }
                    fen.append(getCharFromPiece(piece));  // Append piece character
                }
            }

            if (emptyCount > 0) {
                fen.append(emptyCount);  // Append remaining empty squares
            }

            if (rank > 0) {
                fen.append("/");  // Add separator except for last rank
            }
        }

        return fen.toString();
    }
    // Get the character for a given piece value
    public char getCharFromPiece(int piece) {
        switch (piece & Piece.PIECE_TYPE_MASK) {  // Mask to get only the piece type, ignoring color
            case Piece.PAWN:
                return (piece & Piece.WHITE) != 0 ? 'P' : 'p';
            case Piece.ROOK:
                return (piece & Piece.WHITE) != 0 ? 'R' : 'r';
            case Piece.KNIGHT:
                return (piece & Piece.WHITE) != 0 ? 'N' : 'n';
            case Piece.BISHOP:
                return (piece & Piece.WHITE) != 0 ? 'B' : 'b';
            case Piece.QUEEN:
                return (piece & Piece.WHITE) != 0 ? 'Q' : 'q';
            case Piece.KING:
                return (piece & Piece.WHITE) != 0 ? 'K' : 'k';
            default:
                return ' '; // Should not happen for valid pieces
        }
    }

}
