package seedu.FitLogger;

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
            isExit = c.isExit();
        }
        ui.showGoodbye();
    }

    public static void main(String[] args) {
        new FitLogger().run();
    }
}
