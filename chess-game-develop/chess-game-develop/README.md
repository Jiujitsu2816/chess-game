# Chess: first approach

Four types in `software.ulpgc.chess` form the model:

- `Piece`: type and color. The public class is stored in `Piece.java`, matching Java's filename convention.
- `Position`: immutable coordinates from 0 to 7, with notation such as `e4`. `(0, 0)` is a1.
- `Square`: a position containing a piece or empty (`getPiece()` returns null).
- `Board`: owns 64 squares, sets up the starting position, and relocates pieces.

Example for JShell or a future main method:

```java
import software.ulpgc.chess.*;

Board board = Board.initialPosition();
System.out.println(board);
board.movePiece(Position.fromNotation("e2"), Position.fromNotation("e4"));
System.out.println(board);
```

Uppercase letters represent white pieces, lowercase letters black pieces; N means knight.
Movement currently checks that the source contains a piece and prevents friendly captures.
Legal movement patterns, blocked paths, turns, check/checkmate, castling, promotion,
en passant, and a user interface are future steps.

Build and test with `mvn test` using JDK 26 or newer (the compiler targets Java 26). Seven JUnit 5 tests cover FEN loading and game-state updates. The test report is written to `target/surefire-reports/`.

## Loading FEN

`Deserializer<T>` defines the conversion contract. `FenDeserializer` handles FEN
parsing and syntax validation, keeping that responsibility outside the board and
square-coordinate classes. `ChessPosition` holds the board and all five metadata
fields so loading a FEN does not discard state:

```java
Deserializer<ChessPosition> deserializer = new FenDeserializer();
ChessPosition game = deserializer.deserialize(
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
System.out.println(game.board());
System.out.println(game.activeColor());
```

Malformed input throws `IllegalArgumentException`. The parser checks syntax,
not whether the position is legally reachable. Counters must fit a Java `int`.
`ChessGame.initial()` starts a game with normal castling rights. The `ChessGame(Board, Color)` constructor starts with no castling rights; use the full `ChessPosition` constructor to load custom rights. `ChessPosition` is a defensive snapshot. Load it into `ChessGame` and call `ChessGame.move()` to update turn, castling rights, en passant target and counters together. Direct `Board.movePiece()` only relocates pieces and does not update game metadata.
FEN reference: https://github.com/echecsjs/fen/blob/main/SPEC.md

## Console commands

Run `software.ulpgc.chess.Main` in IntelliJ, or after `mvn compile` run:

```text
java -cp target/classes software.ulpgc.chess.Main
```

Enter `move e2 e4` (or `e2 e4`), then `move e7 e5`. White and black
alternate after successful moves; rejected commands do not consume a turn.
`board` displays the board, `new` resets it, `help` lists commands, and `quit`
exits. `fen <six-field FEN>` replaces the full game state, including castling rights, en passant target and counters.
Invalid FEN leaves the current game intact.

`Main` wires the application, `ConsoleGame` handles input/output, and `ChessGame`
manages turns. This is a two-player movement prototype: legal piece movement,
check/checkmate, and special moves still need implementation. Loaded FEN metadata is retained and updated by `ChessGame`, although castling and en passant moves themselves are not implemented.



