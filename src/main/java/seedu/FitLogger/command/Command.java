package seedu.fitlogger.command;

import seedu.fitlogger.Ui;

public abstract class Command {
    public abstract void execute(Ui ui);

    public boolean isExit() {
        return false;
    }
}
