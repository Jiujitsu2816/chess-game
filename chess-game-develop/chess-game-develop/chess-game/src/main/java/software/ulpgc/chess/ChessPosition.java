package software.ulpgc.chess;

import java.util.Objects;
import java.util.Optional;

/** A FEN snapshot. Moves must go through ChessGame to keep metadata in sync. */
public record ChessPosition(Board board, Piece.Color activeColor, String castlingRights,
                            Optional<Position> enPassantTarget,
                            int halfmoveClock, int fullmoveNumber) {
    public ChessPosition {
        board = Objects.requireNonNull(board, "board").copy();
        Objects.requireNonNull(activeColor, "activeColor");
        Objects.requireNonNull(castlingRights, "castlingRights");
        Objects.requireNonNull(enPassantTarget, "enPassantTarget");
    }

    @Override public Board board() { return board.copy(); }
}
