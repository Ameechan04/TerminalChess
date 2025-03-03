public class PreviousMove {
    private final int piece, startingPosition, endPosition;
    public PreviousMove(int piece, int startingPosition, int endPosition){
        this.piece = piece;
        this.startingPosition = startingPosition;
        this.endPosition = endPosition;
    }

    public int getPiece() {
        return piece;
    }

    public int getStartingPosition() {
        return startingPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void printPreviousMove(){
        System.out.println("Moved " + piece + " from " + startingPosition + " to " + endPosition);
    }
}
