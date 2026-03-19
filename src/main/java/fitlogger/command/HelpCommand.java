package fitlogger.command;

import fitlogger.ui.Ui;

public class HelpCommand extends Command {

    public HelpCommand() {
        super();
    }

    public void execute(Ui ui) {
        ui.showHelpMenu();
    }
}
