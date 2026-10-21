package software.ulpgc.chess;

import java.util.Objects;

public class Piece {
    public enum Type { KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN }
    public enum Color { WHITE, BLACK }
    private final Type type;
    private final Color color;

    public Piece(Type type, Color color) {
        this.type = Objects.requireNonNull(type, "type");
        this.color = Objects.requireNonNull(color, "color");
    }

    public Type getType() { return type; }
    public Color getColor() { return color; }

    @Override
    public String toString() {
        String symbol = type == Type.KNIGHT ? "N" : type.name().substring(0, 1);
        return color == Color.WHITE ? symbol : symbol.toLowerCase(java.util.Locale.ROOT);
    }
}
