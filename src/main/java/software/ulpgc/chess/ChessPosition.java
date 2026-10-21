package software.ulpgc.chess;

import java.util.Optional;

/** A complete FEN snapshot. Position represents one square; this represents game state.
 * Metadata describes the loaded snapshot and is not updated by Board.movePiece().
 */
public record ChessPosition(Board board, Piece.Color activeColor, String castlingRights,
                            Optional<Position> enPassantTarget,
                            int halfmoveClock, int fullmoveNumber) {
}
