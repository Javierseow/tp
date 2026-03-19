package fitlogger.command;

import fitlogger.ui.Ui;

public abstract class Command {
    public abstract void execute(Ui ui);

    public boolean isExit() {
        return false;
    }
}
