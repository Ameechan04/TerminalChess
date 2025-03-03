import Pieces.Piece;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import static Pieces.Piece.*;

public class Main {
   // static final char LIGHT_SQUARE = '□';
    //
   // static final char DARK_SQUARE = '■';
    private static int friendlyColour;  // The colour of the player whose turn it is
    private static int opponentColour;

    private static PreviousMove previousMove;

    static boolean inCheck;
    static Board b;


    public static void main(String[] args) {

       // Main board = new Main();
       // board.createGUIBoard();

        String privateIP = getPrivateIPAddress();
        if (privateIP != null) {
            String hash = hashIP(privateIP);
            System.out.println("Private IP: " + privateIP);
            System.out.println("Hashed IP: " + hash);
        } else {
            System.out.println("Could not determine private IP.");
        }
        b = new Board();
        b.printBoard();
      //  b.printBoardDebug();
        friendlyColour = b.colourToMove;
        boolean finished = false;
        boolean valid_move;
        List<Move> resolvingCheckMoves = null;

        while (!finished) {
            valid_move = false;
            System.out.println();
            System.out.println(playerNumToString(friendlyColour) + "'s turn:");
            int newSquare;
            int currentSquare;
            int piece = 0;
            Move move = null;

            do {
                currentSquare = UserInput.getValidSquareInput(friendlyColour);
                piece = b.square[currentSquare];
                System.out.println("Selected Piece: " + FENtoString(piece));
                newSquare = UserInput.getValidMoveSquare(currentSquare, piece, friendlyColour, opponentColour);
                System.out.println(newSquare);

                if (newSquare != -1) {
                    move = new Move(currentSquare, newSquare, b.square[newSquare]);

                    // If in check, ensure the move resolves the check
                    if (inCheck) {
                        System.out.println("Available moves to resolve check: " + resolvingCheckMoves.size());
                        boolean canResolveCheck = false;

                        for (Move resolvingMove : resolvingCheckMoves) {
                            resolvingMove.printMove();
                            if (areMovesEqual(move, resolvingMove)) {
                                // This move resolves the check
                                canResolveCheck = true;
                                valid_move = true;
                                break;
                            }
                        }

                        if (!canResolveCheck) {
                            System.out.println("This move doesn't resolve the check! Please select a valid move.");
                            valid_move = false; // Keep prompting for a valid move
                        }

                    } else {
                        // If not in check, any valid move is fine
                        valid_move = true;
                    }
                }
            } while (!valid_move);  // Loop until a valid move is selected



            b.updateBoard(move);
          //  previousMove = new PreviousMove(piece, currentSquare, newSquare);
            //previousMove.printPreviousMove();
            b.printBoard();


            // Only switch turns after checking for game over
            b.switchTurn();
            friendlyColour = b.colourToMove;
            opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
            List<Move> killinKingMoves = UserInput.generateMovesThatKillEnemyKing(opponentColour);

            if (killinKingMoves.isEmpty()) {
                inCheck = false;
            } else {
                inCheck = true;
            }

            // Check if the opponent has any legal moves left
            if (inCheck) {
                resolvingCheckMoves = UserInput.generateMovesThatResolveCheck(friendlyColour);

                if (resolvingCheckMoves.isEmpty()) {
                    System.out.println("CHECKMATE!");
                    finished = true;
                }
            } else {
                if (UserInput.generateLegalMoves(friendlyColour).isEmpty()) {
                    System.out.println("STALEMATE!");
                    finished = true;
                }
            }




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

    static String playerNumToString(int playerNum) {
        if (playerNum == 8) {return "White";}
        return "Black";
    }

    public static int getFriendlyColour() {
        return friendlyColour;
    }



    public static String hashIP(String ip) {
        String[] octets = ip.split("\\.");
        StringBuilder hash = new StringBuilder();

        for (String octet : octets) {
            int num = Integer.parseInt(octet);
            char letter = encodeToLetter(num);
            hash.append(letter);
        }

        return hash.toString();
    }

    private static char encodeToLetter(int num) {
        // Map 0-255 into a 52-character space (A-Z, a-z)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        return chars.charAt(num % chars.length());
    }

    public static String getPrivateIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();  // Return first private IP found
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean areMovesEqual(Move move1, Move move2) {
        // Compare the current and target squares of both moves
        return move1.getStartSquare() == move2.getStartSquare() &&
                move1.getTargetSquare() == move2.getTargetSquare();
    }

}
