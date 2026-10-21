package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FenDeserializerTest {
    private final Deserializer<ChessPosition> parser = new FenDeserializer();

    @Test void loadsInitialAndCustomPositions() {
        ChessPosition initial = parser.deserialize(
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertEquals(Board.initialPosition().toString(), initial.board().toString());
        assertEquals(Piece.Color.WHITE, initial.activeColor());
        assertEquals("KQkq", initial.castlingRights());
        assertTrue(initial.enPassantTarget().isEmpty());
        assertEquals(0, initial.halfmoveClock());
        assertEquals(1, initial.fullmoveNumber());

        ChessPosition custom = parser.deserialize("8/8/8/8/4P3/8/8/K6k b - e3 12 42");
        assertEquals(Piece.Color.WHITE, custom.board().getSquare(Position.fromNotation("a1")).getPiece().getColor());
        assertEquals(Piece.Color.BLACK, custom.board().getSquare(Position.fromNotation("h1")).getPiece().getColor());
        assertEquals(Piece.Type.PAWN, custom.board().getSquare(Position.fromNotation("e4")).getPiece().getType());
        assertEquals(Piece.Color.BLACK, custom.activeColor());
        assertEquals("-", custom.castlingRights());
        assertEquals(Position.fromNotation("e3"), custom.enPassantTarget().orElseThrow());
        assertEquals(12, custom.halfmoveClock());
        assertEquals(42, custom.fullmoveNumber());
    }

    @Test void rejectsInvalidFen() {
        String[] invalid = {null, "", "8/8/8/8/8/8/8/8", "8/8/8/8/8/8/8 w - - 0 1",
                "9/8/8/8/8/8/8/8 w - - 0 1", "7/8/8/8/8/8/8/8 w - - 0 1",
                "8P/8/8/8/8/8/8/8 w - - 0 1", "44/8/8/8/8/8/8/8 w - - 0 1",
                "7X/8/8/8/8/8/8/8 w - - 0 1", "8/8/8/8/8/8/8/8 x - - 0 1",
                "8/8/8/8/8/8/8/8 w KK - 0 1", "8/8/8/8/8/8/8/8 w - e4 0 1",
                "8/8/8/8/8/8/8/8 w - - -1 1", "8/8/8/8/8/8/8/8 w - - 0 0",
                "8/8/8/8/8/8/8/8 w - - 999999999999 1"};
        for (String fen : invalid) assertThrows(IllegalArgumentException.class, () -> parser.deserialize(fen), fen);
    }
}
