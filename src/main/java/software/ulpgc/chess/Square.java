package software.ulpgc.chess;

import java.util.Objects;

public class Square {
    private final Position position;
    private Piece piece;

    public Square(Position position) {
        this.position = Objects.requireNonNull(position, "position");
    }

    public Position getPosition() { return position; }
    /** Returns null when the square is empty. */
    public Piece getPiece() { return piece; }
    public boolean isEmpty() { return piece == null; }
    public boolean isDark() { return (position.row() + position.column()) % 2 == 0; }
    void setPiece(Piece piece) { this.piece = piece; }
}
