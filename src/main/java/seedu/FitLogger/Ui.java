package seedu.FitLogger;
import java.util.Scanner;

public class Ui {
    private static final String LINE = "-----------------------------------------------------";

    private Scanner in = new Scanner(System.in);

    public String readCommand() {
        return in.nextLine();
    }

    public void showWelcome() {
        String logo =
                " ______   _   _                                 \n" +
                        "|  ____(_) | | |                                \n" +
                        "| |__   _| |_| |     ___   __ _  __ _  ___ _ __  \n" +
                        "|  __| | | __| |    / _ \\ / _` |/ _` |/ _ \\ '__| \n" +
                        "| |    | | |_| |___| (_) | (_| | (_| |  __/ |    \n" +
                        "|_|    |_|\\__|______\\___/ \\__, |\\__, |\\___|_|    \n" +
                        "                           __/ | __/ |           \n" +
                        "                          |___/ |___/            ";
        System.out.println(logo);
        System.out.println(LINE);
        System.out.println("Welcome to FitLogger!");
        System.out.println("Feel free to Add your own workout");
    }

    public void showGoodbye() {
        System.out.println("Goodbye! See you at your next workout!");
    }

    public void temporaryOutput(String command) {
        System.out.println(command);
    }
}
