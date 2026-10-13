package software.ulpgc.chess;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

/** Reads commands and displays results; FEN conversion stays in the deserializer. */
public class ConsoleGame {
    private final Scanner input;
    private final PrintStream output;
    private final Deserializer<ChessPosition> deserializer;
    private ChessGame game = new ChessGame(Board.initialPosition(), Piece.Color.WHITE);

    public ConsoleGame(Scanner input, PrintStream output, Deserializer<ChessPosition> deserializer) {
        this.input = input;
        this.output = output;
        this.deserializer = deserializer;
    }

    public void run() {
        output.println("Chess console prototype. Type help for commands.");
        output.println("Movement patterns and check/checkmate are not enforced yet.");
        showBoard();
        while (true) {
            output.print(game.getActiveColor() + "> ");
            output.flush();
            if (!input.hasNextLine()) return;
            String line = input.nextLine().strip();
            if (line.isEmpty()) continue;
            String[] command = line.split("\\s+", 2);
            String name = command[0].toLowerCase(Locale.ROOT);
            String argument = command.length == 2 ? command[1] : "";
            try {
                switch (name) {
                    case "quit", "exit" -> {
                        requireNoArgument(argument);
                        output.println("Goodbye!");
                        return;
                    }
                    case "help" -> {
                        requireNoArgument(argument);
                        output.println("move e2 e4  - move a piece (or type e2 e4)");
                        output.println("board       - display the board");
                        output.println("fen <FEN>   - load a position and its active color");
                        output.println("new         - start again");
                        output.println("quit        - exit");
                    }
                    case "board" -> {
                        requireNoArgument(argument);
                        showBoard();
                    }
                    case "new" -> {
                        requireNoArgument(argument);
                        game = new ChessGame(Board.initialPosition(), Piece.Color.WHITE);
                        showBoard();
                    }
                    case "fen" -> {
                        ChessPosition position = deserializer.deserialize(argument);
                        game = new ChessGame(position.board(), position.activeColor());
                        output.println("Position loaded. Special-move metadata is not used by this prototype.");
                        showBoard();
                    }
                    case "move" -> move(argument);
                    default -> {
                        if (name.matches("[a-h][1-8]")) move(line);
                        else throw new IllegalArgumentException("Unknown command. Type help.");
                    }
                }
            } catch (IllegalArgumentException exception) {
                output.println("Error: " + exception.getMessage());
            }
        }
    }

    private void move(String argument) {
        String[] coordinates = argument.toLowerCase(Locale.ROOT).split("\\s+");
        if (coordinates.length != 2) throw new IllegalArgumentException("Use: move e2 e4");
        game.move(Position.fromNotation(coordinates[0]), Position.fromNotation(coordinates[1]));
        showBoard();
    }

    private void showBoard() { output.print(game.getBoard()); }

    private void requireNoArgument(String argument) {
        if (!argument.isEmpty()) throw new IllegalArgumentException("This command takes no arguments");
    }
}
