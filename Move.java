public class Move {
    public final int startSquare;
    public final int targetSquare, pieceCaptured;



    // Constructor to initialize the move
    public Move(int startSquare, int targetSquare, int pieceCaptured) {
        this.startSquare = startSquare;
        this.targetSquare = targetSquare;
        this.pieceCaptured = pieceCaptured;
    }

    public int getStartSquare() {
        return startSquare;
    }

    public int getTargetSquare() {
        return targetSquare;
    }

    public void printMove(){
        System.out.print(startSquare + "  -> " + targetSquare + "; ");
    }

    public int getPieceCaptured() {
        return pieceCaptured;
    }
}