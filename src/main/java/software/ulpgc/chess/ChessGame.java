package software.ulpgc.chess;

import java.util.Objects;

/** Manages the board and alternating turns for the console prototype. */
public class ChessGame {
    private final Board board;
    private Piece.Color activeColor;

    public ChessGame(Board board, Piece.Color activeColor) {
        this.board = Objects.requireNonNull(board, "board");
        this.activeColor = Objects.requireNonNull(activeColor, "activeColor");
    }

    public Board getBoard() { return board; }
    public Piece.Color getActiveColor() { return activeColor; }

    public void move(Position from, Position to) {
        Piece piece = board.getSquare(from).getPiece();
        if (piece == null) throw new IllegalArgumentException("No piece at " + from);
        if (piece.getColor() != activeColor)
            throw new IllegalArgumentException("It is " + activeColor + "'s turn");
        board.movePiece(from, to);
        activeColor = activeColor == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
    }
}
