package seedu.FitLogger;

import java.time.LocalDate;

public class FitLogger {
    private Ui ui;
    private Parser parser;

    public FitLogger() {
        ui = new Ui();
        parser = new Parser();
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            String command = ui.readCommand();
            String[] temps = parser.parse(command);
            Command c = new ExitCommand();
            for (String temp : temps) {
                System.out.println(temp);
            }
            c.execute(ui);

            //running workout temporary test
            LocalDate date = LocalDate.of(2026, 3, 13);
            RunWorkout tempRunWorkout = new RunWorkout("night run", date , "10km", "2 hours");
            System.out.println(tempRunWorkout);

            isExit = c.isExit();
        }
        ui.showGoodbye();
    }

    public static void main(String[] args) {
        new FitLogger().run();
    }
}
