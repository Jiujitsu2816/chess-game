package software.ulpgc.chess;

import java.util.Objects;

public class Board {
    private final Square[][] squares = new Square[8][8];

    /** Creates an empty board. */
    public Board() {
        for (int row = 0; row < 8; row++)
            for (int column = 0; column < 8; column++)
                squares[row][column] = new Square(new Position(row, column));
    }

    public static Board initialPosition() {
        Board board = new Board();
        Piece.Type[] backRank = {Piece.Type.ROOK, Piece.Type.KNIGHT, Piece.Type.BISHOP,
                Piece.Type.QUEEN, Piece.Type.KING, Piece.Type.BISHOP, Piece.Type.KNIGHT, Piece.Type.ROOK};
        for (int column = 0; column < 8; column++) {
            board.placePiece(new Position(0, column), new Piece(backRank[column], Piece.Color.WHITE));
            board.placePiece(new Position(1, column), new Piece(Piece.Type.PAWN, Piece.Color.WHITE));
            board.placePiece(new Position(6, column), new Piece(Piece.Type.PAWN, Piece.Color.BLACK));
            board.placePiece(new Position(7, column), new Piece(backRank[column], Piece.Color.BLACK));
        }
        return board;
    }

    public Square getSquare(Position position) {
        Objects.requireNonNull(position, "position");
        return squares[position.row()][position.column()];
    }

    public void placePiece(Position position, Piece piece) {
        Objects.requireNonNull(piece, "piece");
        Square square = getSquare(position);
        if (!square.isEmpty()) throw new IllegalArgumentException("Square is occupied: " + position);
        square.setPiece(piece);
    }

    /** Basic relocation/capture only. Chess movement rules and turns are not enforced yet. */
    public void movePiece(Position from, Position to) {
        Square source = getSquare(from);
        Square destination = getSquare(to);
        if (from.equals(to)) throw new IllegalArgumentException("Choose a different destination");
        if (source.isEmpty()) throw new IllegalArgumentException("No piece at " + from);
        if (!destination.isEmpty() && destination.getPiece().getColor() == source.getPiece().getColor())
            throw new IllegalArgumentException("Cannot capture your own piece");
        destination.setPiece(source.getPiece());
        source.setPiece(null);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int row = 7; row >= 0; row--) {
            result.append(row + 1).append(" ");
            for (int column = 0; column < 8; column++) {
                Square square = squares[row][column];
                result.append(square.isEmpty() ? "." : square.getPiece()).append(" ");
            }
            result.append('\n');
        }
        return result.append("  a b c d e f g h\n").toString();
    }
}
