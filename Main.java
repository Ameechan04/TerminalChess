import Pieces.Piece;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

import static Pieces.Piece.*;

public class Main {
    static Scanner input;
    private static boolean connectedToClient;
    private static int multiplayerPlayer1, multiplayerPlayer2;

    private static String fenState;
    private static boolean connectedToHost;
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED_BACKGROUND_WHITE_TEXT = "\u001B[97;41m";
    public static final String ANSI_RED_BACKGROUND_BLACK_TEXT = "\u001B[30;41m";


    private static int friendlyColour;  // The colour of the player whose turn it is
    private static int opponentColour;

    private static PreviousMove previousMove;

    static boolean inCheck;
    static Board b;


    public static void main(String[] args) {
        input = new Scanner(System.in);
        boolean multiplayerGame = mainMenu();
        if (multiplayerGame) {
            multiplayerGameSetUp();
        } else {
            newGame();
        }




    }

    public static void newGame(){
        for (int i = 0; i< 100; i++) {
            System.out.println();
        }

        System.out.println("░▒█▄░▒█░▒█▀▀▀░▒█░░▒█░░░▒█▀▀█░█▀▀▄░▒█▀▄▀█░▒█▀▀▀");
        System.out.println("░▒█▒█▒█░▒█▀▀▀░▒█▒█▒█░░░▒█░▄▄▒█▄▄█░▒█▒█▒█░▒█▀▀▀");
        System.out.println("░▒█░░▀█░▒█▄▄▄░▒▀▄▀▄▀░░░▒█▄▄▀▒█░▒█░▒█░░▒█░▒█▄▄▄");

        for (int i = 0; i< 10; i++) {
            System.out.println();
        }


        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        b = new Board(fen);
        b.printBoard();
        friendlyColour = b.colourToMove;
        boolean finished = false;
        boolean valid_move;
        List<Move> resolvingCheckMoves = null;

        while (!finished) {
            valid_move = false;
            System.out.println();
            if (friendlyColour == WHITE) {
                System.out.println(ANSI_RED_BACKGROUND_WHITE_TEXT + playerNumToString(friendlyColour) + "'s turn:" + ANSI_RESET);
            } else {
                System.out.println(ANSI_RED_BACKGROUND_BLACK_TEXT + playerNumToString(friendlyColour) + "'s turn:" + ANSI_RESET);
            }

            int newSquare;
            int currentSquare;
            int piece = 0;
            Move move = null;

            do {
                currentSquare = UserInput.getValidSquareInput(friendlyColour);
                piece = b.square[currentSquare];
                System.out.println("Selected Piece: " + FENtoString(piece));
                newSquare = UserInput.getValidMoveSquare(currentSquare, piece, friendlyColour, opponentColour);

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
    }

    static String playerNumToString(int playerNum) {
        if (playerNum == 8) {return "White";}
        return "Black";
    }

    public static int getFriendlyColour() {
        return friendlyColour;
    }

    public static void multiplayerGameSetUp() {

            for (int i = 0; i < 100; i++) {
                System.out.println();
            }
            String in;
            System.out.println("░▒█▀▄▀█░█░▒█░█░░▀█▀░░▀░░▄▀▀▄░█░░█▀▀▄░█░░█░█▀▀░█▀▀▄░░░█▀▄▀█░█▀▀░█▀▀▄░█░▒█");
            System.out.println("░▒█▒█▒█░█░▒█░█░░░█░░░█▀░█▄▄█░█░░█▄▄█░█▄▄█░█▀▀░█▄▄▀░░░█░▀░█░█▀▀░█░▒█░█░▒█");
            System.out.println("░▒█░░▒█░░▀▀▀░▀▀░░▀░░▀▀▀░█░░░░▀▀░▀░░▀░▄▄▄▀░▀▀▀░▀░▀▀░░░▀░░▒▀░▀▀▀░▀░░▀░░▀▀▀");
            for (int i = 0; i < 20; i++) {
                System.out.println();
            }
            System.out.println("CHOSE BETWEEN HOSTING AND JOINING LOBBY");
            System.out.println("Enter HOST, JOIN, or RETURN");


            do {
                in = (input.nextLine()).toUpperCase();

                switch (in)  {
                    case "HOST":
                        hostGame();
                    case "JOIN":
                        joinGame();
                    case "RETURN":
                        return;
                    default:
                        System.out.println(ANSI_RED + "Enter HOST, JOIN or RETURN.");
                }
            } while (true);



    }

    private static void joinGame(){
        multiplayerPlayer2 = 16;
       String in;
       boolean valid = false;
        do {
            System.out.println("Enter lobby code: ");
            in = (input.nextLine());

            //if (in.length() == 4) {
                valid = true;
            //}
        } while (!valid);

        String serverAddress = decodeIP(in);
        int port = 14589; // Same port as the server

        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Connected to lobby.");
            connectedToHost = true;
            // Setup input and output streams
            BufferedReader inM = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String serverMessage;
            String fen = inM.readLine(); // Receive initial FEN
            System.out.println("Initial board state: " + fen);

            while (true) {
                // Client makes a move
                fen = multiplayerGame(fen, multiplayerPlayer2);
                System.out.println("Client move, new FEN: " + fen);

                // Send updated FEN to server
                out.println(fen);

                // Receive updated FEN from server
                serverMessage = inM.readLine();
                if (serverMessage == null) {
                    System.out.println("Server disconnected.");
                    break;
                }

                System.out.println("Received updated FEN from server: " + serverMessage);
                if (serverMessage.equals("CHECKMATE") || serverMessage.equals("STALEMATE")) {
                    System.out.println("Game over: " + serverMessage);
                    break;
                }

                fen = serverMessage;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void hostGame() {
        multiplayerPlayer1 = 8; //host is white for now
        String privateIP = getPrivateIPAddress();
        if (privateIP != null) {
            String hash = hashIP(privateIP);

            System.out.println("LOBBY CODE: " + hash);
            int port = 14589; // Server listens on this port

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Lobby started. Waiting for other player to connect...");

                while (true) {
                    // Accept client connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Opponent connected.");
                    connectedToClient = true;

                    // Setup input and output streams
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Read message from client
                    /*
                    String clientMessage = in.readLine();
                    System.out.println("Client says: " + clientMessage);
                    clientMessage = clientMessage.toUpperCase();

                     */
                    String clientMessage;
                    String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
                    System.out.println("EXPECTED: " + fen);
                    Board board = new Board(fen);
                    System.out.println("TEST: " + board.boardToFen1DArray());

                    fen = multiplayerGame(fen, multiplayerPlayer1);
                    out.println(fen);
                    while (true) {
                        clientMessage = in.readLine();
                        if (clientMessage == null) {
                            System.out.println("Client disconnected.");
                            break;
                        }
                        if (clientMessage.equals("CHECKMATE") || clientMessage.equals("STALEMATE")) {
                            System.out.println("Game over: " + clientMessage);
                            break;
                        }

                        // Receive move and update FEN
                        fen = clientMessage;
                        System.out.println("Received move, new FEN: " + fen);

                        // Host makes their move
                        fen = multiplayerGame(fen, multiplayerPlayer1);
                        System.out.println("Host move, new FEN: " + fen);

                        // Send updated FEN to client
                        out.println(fen);
                    }


                    /*
                    // Send response to client
                    out.println("Hello, client! You said: " + clientMessage);
                    int clientMessageI = Integer.parseInt(in.readLine());
                    System.out.println("Client says: " + clientMessageI);


                     */

                    System.out.println("GAME OVER");

                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Could not determine private IP.");
        }
    }


    public static String decodeIP(String hash) {
        StringBuilder ip = new StringBuilder();

        // Process the hash two characters at a time
        for (int i = 0; i < hash.length(); i += 2) {
            String encodedOctet = hash.substring(i, i + 2);
            int num = decodeToNumber(encodedOctet);
            ip.append(num);

            // Add dot separator except for the last octet
            if (i < hash.length() - 2) {
                ip.append(".");
            }
        }

        return ip.toString();
    }

    private static int decodeToNumber(String encoded) {
        // Map two characters back to a number in the range of 0-255
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int firstCharValue = chars.indexOf(encoded.charAt(0)) * 52;
        int secondCharValue = chars.indexOf(encoded.charAt(1));

        return firstCharValue + secondCharValue;  // Combine both values to get the original number
    }




    public static String hashIP(String ip) {
        String[] octets = ip.split("\\.");
        StringBuilder hash = new StringBuilder();

        for (String octet : octets) {
            int num = Integer.parseInt(octet);
            String encoded = encodeToLetters(num);
            hash.append(encoded);
        }

        return hash.toString();
    }

    private static String encodeToLetters(int num) {
        // Map 0-255 to a base-52 range (we need two characters for each number)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder encoded = new StringBuilder();

        // Encode each number as a pair of characters
        encoded.append(chars.charAt(num / 52));  // First character (higher 52)
        encoded.append(chars.charAt(num % 52));  // Second character (lower 52)

        return encoded.toString();
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

    public static void printChess(){
        System.out.println("░▀▀█▀▀░▒█▀▀▀░▒█▀▀▄░▒█▀▄▀█░▀█▀░▒█▄░▒█░█▀▀▄░▒█░░░░░░▒█▀▀▄░▒█░▒█░▒█▀▀▀░▒█▀▀▀█░▒█▀▀▀█");
        System.out.println("░░▒█░░░▒█▀▀▀░▒█▄▄▀░▒█▒█▒█░▒█░░▒█▒█▒█▒█▄▄█░▒█░░░░░░▒█░░░░▒█▀▀█░▒█▀▀▀░░▀▀▀▄▄░░▀▀▀▄▄");
        System.out.println("░░▒█░░░▒█▄▄▄░▒█░▒█░▒█░░▒█░▄█▄░▒█░░▀█▒█░▒█░▒█▄▄█░░░▒█▄▄▀░▒█░▒█░▒█▄▄▄░▒█▄▄▄█░▒█▄▄▄█");

        System.out.println("         _                                        \n" +
                "|_ \\/   |_| _  _|  _ _       |V| _  _  _ |_  _  _ \n" +
                "|_)/    | || |(_| | (/_\\^/   | |(/_(/_(_ | |(_|| |\n");
    }

    public static boolean mainMenu(){
        System.out.print(ANSI_BLUE);
        printChess();
        System.out.println(" █▄ ▄█ ▄▀▄ █ █▄ █   █▄ ▄█ ██▀ █▄ █ █ █\n" +
                " █ ▀ █ █▀█ █ █ ▀█   █ ▀ █ █▄▄ █ ▀█ ▀▄█\n");
        System.out.println(ANSI_BLUE +"OPTIONS:");
        System.out.println("LOCAL GAME");
        System.out.println("MULTIPLAYER GAME");
        System.out.println("EXIT");
        System.out.print("Enter 'LOCAL','MULTI' or 'EXIT': ");
        System.out.print(ANSI_RESET);
        String in;
        boolean valid = false;


        do {
            in = (input.nextLine()).toUpperCase();

            switch (in)  {
                case "LOCAL":
                    System.out.print(ANSI_RESET);
                    return false;
                case "MULTI":
                    System.out.print(ANSI_RESET);
                    return true;
                case "EXIT":
                    System.out.println("Goodbye.");
                    System.exit(0);
                    break;

                default:
                    System.out.println(ANSI_RED + "Enter LOCAL, MULTI or EXIT.");
            }
        } while (true);

    }

    /*Takes in fen state, returns a new fen state */
    public static String multiplayerGame(String fen, int player) {
        b = new Board(fen);
        b.printBoard();
        friendlyColour = player;
        opponentColour = (friendlyColour == WHITE) ? BLACK : WHITE;
        boolean finished = false;
        boolean valid_move;
        List<Move> resolvingCheckMoves = null;
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
            b.printBoard();
            fen = b.boardToFen1DArray();


            // Only switch turns after checking for game over


        //THIS ALL HAPPENS AFTER THE PLAY SWITCHES USUALLY SO PROBALBY BROKEN TODO
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
                    return "CHECKMATE!";
                }
            } else {
                if (UserInput.generateLegalMoves(friendlyColour).isEmpty()) {
                    System.out.println("STALEMATE!");
                    return "STALEMATE!";
                }
            }
            System.out.println(fen);
            return fen;

    }


}
