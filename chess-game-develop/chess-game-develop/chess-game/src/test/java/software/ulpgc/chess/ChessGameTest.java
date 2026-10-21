package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameTest {
    private final FenDeserializer parser = new FenDeserializer();

    @Test void preservesLoadedFenMetadata() {
        ChessGame game = new ChessGame(parser.deserialize("8/8/8/8/4P3/8/8/K6k b - e3 12 42"));
        ChessPosition state = game.position();
        assertEquals(Piece.Color.BLACK, state.activeColor());
        assertEquals("-", state.castlingRights());
        assertEquals(Position.fromNotation("e3"), state.enPassantTarget().orElseThrow());
        assertEquals(12, state.halfmoveClock());
        assertEquals(42, state.fullmoveNumber());
    }

    @Test void updatesTurnCountersAndEnPassantAfterAcceptedMoves() {
        ChessGame game = ChessGame.initial();
        game.move(at("e2"), at("e4"));
        ChessPosition white = game.position();
        assertEquals(Piece.Color.BLACK, white.activeColor());
        assertEquals(at("e3"), white.enPassantTarget().orElseThrow());
        assertEquals(0, white.halfmoveClock());
        assertEquals(1, white.fullmoveNumber());
        game.move(at("g8"), at("f6"));
        ChessPosition black = game.position();
        assertEquals(Piece.Color.WHITE, black.activeColor());
        assertTrue(black.enPassantTarget().isEmpty());
        assertEquals(1, black.halfmoveClock());
        assertEquals(2, black.fullmoveNumber());
    }

    @Test void rejectedMoveLeavesAllStateUntouched() {
        ChessGame game = ChessGame.initial();
        ChessPosition before = game.position();
        assertThrows(IllegalArgumentException.class, () -> game.move(at("a1"), at("a2")));
        ChessPosition after = game.position();
        assertEquals(before.board().toString(), after.board().toString());
        assertEquals(before.activeColor(), after.activeColor());
        assertEquals(before.castlingRights(), after.castlingRights());
        assertEquals(before.enPassantTarget(), after.enPassantTarget());
        assertEquals(before.halfmoveClock(), after.halfmoveClock());
        assertEquals(before.fullmoveNumber(), after.fullmoveNumber());
    }

    @Test void rookAndKingMovesRevokeCastlingRights() {
        ChessGame game = new ChessGame(parser.deserialize("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"));
        game.move(at("h1"), at("h2"));
        assertEquals("Qkq", game.position().castlingRights());
        game.move(at("e8"), at("e7"));
        assertEquals("Q", game.position().castlingRights());
    }

    @Test void returnedBoardDoesNotMutateGame() {
        ChessGame game = ChessGame.initial();
        game.getBoard().movePiece(at("e2"), at("e4"));
        assertNotNull(game.position().board().getSquare(at("e2")).getPiece());
    }

    private static Position at(String square) { return Position.fromNotation(square); }
}
