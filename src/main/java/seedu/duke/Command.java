package seedu.duke;

public abstract class Command {
    public abstract void execute(Ui ui);

    public boolean isExit() {
        return false;
    }
}
