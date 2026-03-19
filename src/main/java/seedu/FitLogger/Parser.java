package seedu.fitlogger;

public class Parser {
    //temporary for now, change later when we know how much description there is
    private static final int MAX_LIFT_INFO = 100;
    private static final int MAX_RUN_INFO = 50;

    public static String[] parse(String command) {
        String[] details = new String[MAX_LIFT_INFO];
        details = command.split(" ");
        return details;
    }
}
