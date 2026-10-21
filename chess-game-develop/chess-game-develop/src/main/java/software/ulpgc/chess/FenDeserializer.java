package software.ulpgc.chess;

import java.util.Optional;

/** Parses standard six-field FEN syntax, without checking chess-position legality. */
public class FenDeserializer implements Deserializer<ChessPosition> {
    @Override
    public ChessPosition deserialize(String text) {
        if (text == null || text.isBlank()) throw invalid("FEN must not be empty");
        String[] fields = text.strip().split("\\s+");
        if (fields.length != 6) throw invalid("Expected six fields");
        Board board = parseBoard(fields[0]);
        Piece.Color activeColor = switch (fields[1]) {
            case "w" -> Piece.Color.WHITE;
            case "b" -> Piece.Color.BLACK;
            default -> throw invalid("Active color must be w or b");
        };
        if (!fields[2].equals("-") && !fields[2].matches("(?=.+)K?Q?k?q?"))
            throw invalid("Invalid castling rights");
        Optional<Position> enPassant = parseEnPassant(fields[3]);
        int halfmoveClock = parseCounter(fields[4], 0, "halfmove clock");
        int fullmoveNumber = parseCounter(fields[5], 1, "fullmove number");
        return new ChessPosition(board, activeColor, fields[2], enPassant, halfmoveClock, fullmoveNumber);
    }

    private Board parseBoard(String placement) {
        String[] ranks = placement.split("/", -1);
        if (ranks.length != 8) throw invalid("Expected eight ranks");
        Board board = new Board();
        for (int rank = 0; rank < 8; rank++) {
            int column = 0;
            boolean previousWasDigit = false;
            for (char symbol : ranks[rank].toCharArray()) {
                if (symbol >= '1' && symbol <= '8') {
                    if (previousWasDigit) throw invalid("Adjacent empty-square counts");
                    column += symbol - '0';
                    previousWasDigit = true;
                } else {
                    if (column >= 8) throw invalid("Rank is too wide");
                    board.placePiece(new Position(7 - rank, column++), parsePiece(symbol));
                    previousWasDigit = false;
                }
                if (column > 8) throw invalid("Rank is too wide");
            }
            if (column != 8) throw invalid("Each rank must describe eight squares");
        }
        return board;
    }

    private Piece parsePiece(char symbol) {
        Piece.Type type = switch (symbol) {
            case 'K', 'k' -> Piece.Type.KING;
            case 'Q', 'q' -> Piece.Type.QUEEN;
            case 'R', 'r' -> Piece.Type.ROOK;
            case 'B', 'b' -> Piece.Type.BISHOP;
            case 'N', 'n' -> Piece.Type.KNIGHT;
            case 'P', 'p' -> Piece.Type.PAWN;
            default -> throw invalid("Unknown piece: " + symbol);
        };
        return new Piece(type, Character.isUpperCase(symbol) ? Piece.Color.WHITE : Piece.Color.BLACK);
    }

    private Optional<Position> parseEnPassant(String field) {
        if (field.equals("-")) return Optional.empty();
        if (!field.matches("[a-h][36]")) throw invalid("Invalid en passant target");
        return Optional.of(Position.fromNotation(field));
    }

    private int parseCounter(String field, int minimum, String name) {
        if (!field.matches("[0-9]+")) throw invalid("Invalid " + name);
        try {
            int value = Integer.parseInt(field);
            if (value < minimum) throw invalid("Invalid " + name);
            return value;
        } catch (NumberFormatException exception) {
            throw invalid(name + " exceeds supported integer range");
        }
    }

    private IllegalArgumentException invalid(String message) {
        return new IllegalArgumentException("Invalid FEN: " + message);
    }
}
