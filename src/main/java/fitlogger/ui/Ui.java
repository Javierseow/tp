package fitlogger.ui;

import fitlogger.workout.Workout;

import java.util.Scanner;

public class Ui {
    private static final String LINE = "-----------------------------------------------------";

    private Scanner in = new Scanner(System.in);

    public String readCommand() {
        return in.nextLine();
    }

    public void showWelcome() {
        String logo = " ______   _   _                                 \n"
                + "|  ____(_) | | |                                \n"
                + "| |__   _| |_| |     ___   __ _  __ _  ___ _ __  \n"
                + "|  __| | | __| |    / _ \\ / _` |/ _` |/ _ \\ '__| \n"
                + "| |    | | |_| |___| (_) | (_| | (_| |  __/ |    \n"
                + "|_|    |_|\\__|______\\___/ \\__, |\\__, |\\___|_|    \n"
                + "                           __/ | __/ |           \n"
                + "                          |___/ |___/            ";
        showMessage(logo);
        showLine();
        showMessage("Welcome to FitLogger!");
        showMessage("Feel free to Add your own workout");
    }

    public void showGoodbye() {
        showMessage("Goodbye! See you at your next workout!");
    }

    public void showMessage(String command) {
        System.out.println(command);
    }

    public void showHelpMenu() {
        String helpMessage = "Command Guide:\n" + "    help: List available commands\n"
                + "    delete workout <WORKOUT_NAME>: Delete workout by name\n"
                + "    delete workout <index>: Delete workout by index\n"
                + "    history: View saved workouts\n" + "    exit: Close FitLogger";
        System.out.println(helpMessage);
    }

    public void showMessageNoNewline(String command) {
        System.out.print(command);
    }

    public void showLine() {
        showMessage(LINE);
    }

    public void printWorkout(Workout workout) {
        showMessage(workout.toString());
    }
}
