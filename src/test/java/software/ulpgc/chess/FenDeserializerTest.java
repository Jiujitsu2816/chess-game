package software.ulpgc.chess;

/** Dependency-free tests. Run this main method after compiling main and test sources. */
public class FenDeserializerTest {
    public static void main(String[] args) {
        Deserializer<ChessPosition> parser = new FenDeserializer();
        ChessPosition initial = parser.deserialize(
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        require(initial.board().toString().equals(Board.initialPosition().toString()), "Initial setup");
        require(initial.activeColor() == Piece.Color.WHITE, "White turn");
        require(initial.castlingRights().equals("KQkq"), "Castling rights");
        require(initial.enPassantTarget().isEmpty(), "No en passant");
        require(initial.halfmoveClock() == 0 && initial.fullmoveNumber() == 1, "Initial counters");

        ChessPosition custom = parser.deserialize("8/8/8/8/4P3/8/8/K6k b - e3 12 42");
        require(custom.board().getSquare(Position.fromNotation("a1")).getPiece().getColor()
                == Piece.Color.WHITE, "Rank orientation");
        require(custom.board().getSquare(Position.fromNotation("h1")).getPiece().getColor()
                == Piece.Color.BLACK, "File orientation");
        require(custom.board().getSquare(Position.fromNotation("e4")).getPiece().getType()
                == Piece.Type.PAWN, "Sparse placement");
        require(custom.activeColor() == Piece.Color.BLACK, "Black turn");
        require(custom.castlingRights().equals("-"), "No castling");
        require(custom.enPassantTarget().orElseThrow().equals(Position.fromNotation("e3")), "En passant");
        require(custom.halfmoveClock() == 12 && custom.fullmoveNumber() == 42, "Counters");

        String[] invalid = {null, "", "8/8/8/8/8/8/8/8", "8/8/8/8/8/8/8 w - - 0 1",
                "9/8/8/8/8/8/8/8 w - - 0 1", "7/8/8/8/8/8/8/8 w - - 0 1",
                "8P/8/8/8/8/8/8/8 w - - 0 1", "44/8/8/8/8/8/8/8 w - - 0 1",
                "7X/8/8/8/8/8/8/8 w - - 0 1", "8/8/8/8/8/8/8/8 x - - 0 1",
                "8/8/8/8/8/8/8/8 w KK - 0 1", "8/8/8/8/8/8/8/8 w - e4 0 1",
                "8/8/8/8/8/8/8/8 w - - -1 1", "8/8/8/8/8/8/8/8 w - - 0 0",
                "8/8/8/8/8/8/8/8 w - - 999999999999 1"};
        for (String fen : invalid) {
            try {
                parser.deserialize(fen);
                throw new AssertionError("Accepted invalid FEN: " + fen);
            } catch (IllegalArgumentException expected) {
                // Expected syntax rejection.
            }
        }
        System.out.println("FEN deserializer tests passed");
    }

    private static void require(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }
}
