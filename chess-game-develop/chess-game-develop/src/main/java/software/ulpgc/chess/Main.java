package software.ulpgc.chess;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new ConsoleGame(new Scanner(System.in), System.out, new FenDeserializer()).run();
    }
}
