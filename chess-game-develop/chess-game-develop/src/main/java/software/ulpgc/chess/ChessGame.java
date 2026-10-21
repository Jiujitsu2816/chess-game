package software.ulpgc.chess;

import java.util.Objects;
import java.util.Optional;

/** Owns the board and FEN metadata for the console prototype. */
public class ChessGame {
    private final Board board;
    private Piece.Color activeColor;
    private String castlingRights;
    private Optional<Position> enPassantTarget;
    private int halfmoveClock;
    private int fullmoveNumber;

    public ChessGame(Board board, Piece.Color activeColor) {
        this(new ChessPosition(board, activeColor, "-", Optional.empty(), 0, 1));
    }

    public static ChessGame initial() {
        return new ChessGame(new ChessPosition(Board.initialPosition(), Piece.Color.WHITE,
                "KQkq", Optional.empty(), 0, 1));
    }

    public ChessGame(ChessPosition position) {
        Objects.requireNonNull(position, "position");
        this.board = position.board();
        this.activeColor = Objects.requireNonNull(position.activeColor(), "activeColor");
        this.castlingRights = Objects.requireNonNull(position.castlingRights(), "castlingRights");
        this.enPassantTarget = Objects.requireNonNull(position.enPassantTarget(), "enPassantTarget");
        this.halfmoveClock = position.halfmoveClock();
        this.fullmoveNumber = position.fullmoveNumber();
    }

    public Board getBoard() { return board.copy(); }
    public Piece.Color getActiveColor() { return activeColor; }
    public ChessPosition position() {
        return new ChessPosition(board, activeColor, castlingRights, enPassantTarget,
                halfmoveClock, fullmoveNumber);
    }

    public void move(Position from, Position to) {
        Piece piece = board.getSquare(from).getPiece();
        if (piece == null) throw new IllegalArgumentException("No piece at " + from);
        if (piece.getColor() != activeColor)
            throw new IllegalArgumentException("It is " + activeColor + "'s turn");
        Piece captured = board.getSquare(to).getPiece();
        board.movePiece(from, to); // Only update metadata after successful relocation.

        String rights = castlingRights;
        if (piece.getType() == Piece.Type.KING)
            rights = remove(rights, piece.getColor() == Piece.Color.WHITE ? "KQ" : "kq");
        if (piece.getType() == Piece.Type.ROOK) rights = removeRookRight(rights, from);
        if (captured != null && captured.getType() == Piece.Type.ROOK)
            rights = removeRookRight(rights, to);
        castlingRights = rights.isEmpty() ? "-" : rights;

        enPassantTarget = piece.getType() == Piece.Type.PAWN
                && from.column() == to.column() && Math.abs(to.row() - from.row()) == 2
                ? Optional.of(new Position((from.row() + to.row()) / 2, from.column()))
                : Optional.empty();
        halfmoveClock = piece.getType() == Piece.Type.PAWN || captured != null ? 0 : halfmoveClock + 1;
        if (activeColor == Piece.Color.BLACK) fullmoveNumber++;
        activeColor = activeColor == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
    }

    private static String remove(String rights, String letters) {
        if (rights.equals("-")) return "";
        for (char letter : letters.toCharArray()) rights = rights.replace(String.valueOf(letter), "");
        return rights;
    }

    private static String removeRookRight(String rights, Position square) {
        if (square.equals(Position.fromNotation("a1"))) return remove(rights, "Q");
        if (square.equals(Position.fromNotation("h1"))) return remove(rights, "K");
        if (square.equals(Position.fromNotation("a8"))) return remove(rights, "q");
        if (square.equals(Position.fromNotation("h8"))) return remove(rights, "k");
        return rights.equals("-") ? "" : rights;
    }
}

