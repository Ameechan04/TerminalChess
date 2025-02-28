import Pieces.Piece;

public class Board {
    public static int[] square = new int[64];
    public static int colourToMove;

    public static char[][] parseFEN(String fen) {
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


    public static void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char square : row) {
                System.out.print(square + " ");
            }
            System.out.println();
        }
    }

    public void initialBoard(){
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        char[][] boardB = Board.parseFEN(fen);
        Board.printBoard(boardB);

        parseFen1DArray(fen);

        int counter  = 1;
        for (int i = 63; i >= 0; i--) {
            if (counter % 8 == 0) {
                System.out.println(square[i]);
            } else {
            System.out.print(square[i]  + ",") ;
            }
            counter++;

        }
    }

    public void switchTurn() {
        colourToMove = (colourToMove == Piece.WHITE) ? Piece.BLACK : Piece.WHITE;
    }

    public static void parseFen1DArray(String fen) {
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
                        Board.square[squareIndex++] = Piece.None;  // Fill empty squares
                    }
                } else {
                    int piece = getPieceFromChar(c);  // Get the piece type from char
                    Board.square[squareIndex++] = piece;
                }
            }
        }
    }

    public static int getPieceFromChar(char c) {
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
                return Piece.None;
        }
    }




}
