package Pieces;

import static java.lang.Math.min;

public class Piece {

    static final public int NONE = 0;
    static final public int KING = 1;
    static final public int PAWN = 2;
    static final public int KNIGHT = 3;
    static final public int BISHOP = 4;
    static final public int ROOK = 5;
    static final public int QUEEN = 6;

    static final public int WHITE = 8;
    static final public int BLACK = 16;

    public static final int PIECE_TYPE_MASK = 0x7;  // Mask to extract the last 3 bits (piece type)

    public static int[] directionOffsets = { 8, -8, -1, 1, 7, -7, 9, -9};
    public static int[][] numSquaresToEdge;

    public static void precomputerMoveData(){
        numSquaresToEdge = new int[64][8];
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {

                int numNorth = 7 - rank;
                int numSouth = rank;
                int numWest = file;
                int numEast = 7 - file;

                int squareIndex = rank * 8 + file;

                numSquaresToEdge[squareIndex] = new int[] {
                        numNorth,
                        numSouth,
                        numWest,
                        numEast,
                        Math.min(numNorth, numWest),
                        Math.min(numSouth, numEast),
                        Math.min(numNorth, numEast),
                        Math.min(numSouth, numWest),
                };



            }
        }
    }

    /*check if the piece colour matches the specified colour */
    public static boolean isColour(int piece, int colour) {
        return (piece & (WHITE | BLACK)) != 0 && (piece & (WHITE | BLACK)) == colour;
    }


    // Method to check if a piece is of a certain type
    public static boolean isType(int piece, int type) {
        return (piece & 0x7) == type;  // Mask the last 3 bits (piece type)
    }

    public static boolean isSlidingPiece(int piece) {
        return (isType(piece, ROOK) || isType(piece, BISHOP) || isType(piece, QUEEN));
    }

    public static String FENtoString(int fenPiece) {
        // Extract the piece type (lower 3 bits)
        int pieceType = fenPiece & 0x7;  // 0x7 is binary 111, masking the lower 3 bits

        // Determine the piece colour by checking if the higher bits are set
        String colour = (fenPiece == 0) ? "EMPTY" : ((fenPiece & Piece.WHITE) != 0 ? "WHITE" : "BLACK");

        // Determine the piece name
        String pieceName = "";
        switch (pieceType) {
            case Piece.KING:
                pieceName = "KING";
                break;
            case Piece.PAWN:
                pieceName = "PAWN";
                break;
            case Piece.KNIGHT:
                pieceName = "KNIGHT";
                break;
            case Piece.BISHOP:
                pieceName = "BISHOP";
                break;
            case Piece.ROOK:
                pieceName = "ROOK";
                break;
            case Piece.QUEEN:
                pieceName = "QUEEN";
                break;
            default:
                pieceName = "EMPTY";  // If the piece type is None (0)
                break;
        }

        return colour + " " + pieceName;
    }

    public static String getPieceColour(int piece) {
        return (piece == 0) ? "EMPTY" : ((piece & Piece.WHITE) != 0 ? "WHITE" : "BLACK");
    }



    // **Added isPawn, isKnight, isBishop, isRook, isQueen, isKing functions**
    public static boolean isPawn(int piece) {
        return isType(piece, PAWN);
    }

    public static boolean isKnight(int piece) {
        return isType(piece, KNIGHT);
    }

    public static boolean isBishop(int piece) {
        return isType(piece, BISHOP);
    }

    public static boolean isRook(int piece) {
        return isType(piece, ROOK);
    }

    public static boolean isQueen(int piece) {
        return isType(piece, QUEEN);
    }

    public static boolean isKing(int piece) {
        return isType(piece, KING);
    }


}
