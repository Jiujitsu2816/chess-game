package software.ulpgc.chess;

/** Zero-based coordinates: (0, 0) is a1, (7, 7) is h8. */
public record Position(int row, int column) {
    public Position {
        if (row < 0 || row >= 8 || column < 0 || column >= 8)
            throw new IllegalArgumentException("Coordinates must be between 0 and 7");
    }

    public static Position fromNotation(String notation) {
        if (notation == null || !notation.matches("[a-h][1-8]"))
            throw new IllegalArgumentException("Use a square such as e4");
        return new Position(notation.charAt(1) - '1', notation.charAt(0) - 'a');
    }

    @Override
    public String toString() { return "" + (char) ('a' + column) + (row + 1); }
}
