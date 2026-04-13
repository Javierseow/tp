package fitlogger;

import fitlogger.command.AddWorkoutCommand;
import fitlogger.command.Command;
import fitlogger.command.DeleteCommand;
import fitlogger.command.EditCommand;
import fitlogger.command.ExitCommand;
import fitlogger.command.SearchDateCommand;
import fitlogger.command.ViewDatabaseCommand;
import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.exception.FitLoggerException;
import fitlogger.parser.Parser;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.workout.Workout;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workoutlist.WorkoutList;
import fitlogger.ui.Ui;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {

    private WorkoutList workouts;
    private Storage storage;
    private TestUi ui;
    private UserProfile profile;
    private ExerciseDictionary dictionary;

    @BeforeEach
    void setUp() {
        workouts = new WorkoutList();
        storage = new Storage();
        ui = new TestUi();
        profile = new UserProfile();
        dictionary = new ExerciseDictionary();
    }

    // ── add-lift: happy path ──────────────────────────────────────────────

    @Test
    void addLift_validInput_addsWorkout() throws FitLoggerException {
        Command cmd = Parser.parse("add-lift Bench Press w/80.5 s/3 r/8", workouts, dictionary);

        assertTrue(cmd instanceof AddWorkoutCommand, "Expected AddWorkoutCommand for add-lift");

        cmd.execute(storage, workouts, ui, profile);

        assertEquals(1, workouts.getSize());
        assertTrue(workouts.getWorkoutAtIndex(0) instanceof StrengthWorkout);

        StrengthWorkout logged = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals("Bench Press", logged.getDescription());
        assertEquals(80.5, logged.getWeight(), 0.001);
        assertEquals(3, logged.getSets());
        assertEquals(8, logged.getReps());
    }

    @Test
    void addLift_validShortcutId_addsWorkoutWithDictionaryName() throws FitLoggerException {
        Command cmd = Parser.parse("add-lift 1 w/100 s/3 r/5", workouts, dictionary);
        cmd.execute(storage, workouts, ui, profile);

        StrengthWorkout logged = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals("Squat", logged.getDescription());
    }

    @Test
    void addLift_zeroWeight_isAllowed() throws FitLoggerException {
        // Bodyweight exercises log w/0
        Command cmd = Parser.parse("add-lift Pull-up w/0 s/3 r/10", workouts, dictionary);
        cmd.execute(storage, workouts, ui, profile);
        StrengthWorkout logged = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(0.0, logged.getWeight(), 0.001);
    }

    // ── add-lift: error cases ─────────────────────────────────────────────

    @Test
    void addLift_missingArgs_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift", workouts, dictionary));
        assertTrue(ex.getMessage().toLowerCase().contains("missing"),
                "Error should mention missing arguments");
    }

    @Test
    void addLift_missingReps_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/100 s/5", workouts, dictionary));
        assertTrue(
                ex.getMessage().toLowerCase().contains("invalid format")
                        || ex.getMessage().toLowerCase().contains("usage"),
                "Error should describe the correct format");
    }

    @Test
    void addLift_missingWeightFlag_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat s/3 w/100 r/5", workouts, dictionary));
        assertTrue(ex.getMessage().contains("Invalid format"));
    }

    @Test
    void addLift_nonNumericWeight_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/heavy s/3 r/5", workouts, dictionary));
    }

    @Test
    void addLift_scientificNotationWeight_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/8e1 s/3 r/5", workouts, dictionary));
        assertTrue(ex.getMessage().contains("decimal number"));
    }

    @Test
    void addLift_overlargeWeight_throwsException() {
        String overlargeWeight = "2" + "0".repeat(308);
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/" + overlargeWeight + " s/3 r/5",
                        workouts, dictionary));
    }

    @Test
    void addLift_unknownShortcutId_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift 999 w/100 s/3 r/5", workouts, dictionary));
        assertTrue(ex.getMessage().contains("Shortcut ID [999] does not exist"));
    }

    @Test
    void addLift_nonIntegerSets_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/100 s/3.5 r/5", workouts, dictionary));
    }

    @Test
    void addLift_negativeWeight_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/-10 s/3 r/5", workouts, dictionary));
        assertTrue(ex.getMessage().toLowerCase().contains("negative"),
                "Error should mention negative weight");
    }

    @Test
    void addLift_zeroSets_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/100 s/0 r/5", workouts, dictionary));
        assertTrue(ex.getMessage().toLowerCase().contains("sets"), "Error should mention sets");
    }

    @Test
    void addLift_overlargeSets_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/100 s/1000001 r/5", workouts, dictionary));
        assertEquals("Sets must not exceed 1000000.", ex.getMessage());
    }

    @Test
    void addLift_zeroReps_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/100 s/3 r/0", workouts, dictionary));
        assertTrue(ex.getMessage().toLowerCase().contains("reps"), "Error should mention reps");
    }

    // ── add-lift: delimiter injection ────────────────────────────────────

    @Test
    void addLift_pipeInName_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Bad|Name w/80 s/3 r/8", workouts, dictionary));
        assertTrue(ex.getMessage().contains("|"), "Error should call out the pipe character");
    }

    @Test
    void addLift_slashInName_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Bad/Name w/80 s/3 r/8", workouts, dictionary));
        assertTrue(ex.getMessage().contains("/"), "Error should call out the slash character");
    }

    // ── add-run: error cases (previously guarded only by assert) ─────────────

    @Test
    void addRun_missingArgs_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run", workouts, dictionary));
        assertTrue(ex.getMessage().toLowerCase().contains("missing"),
                "Error should mention missing arguments");
    }

    @Test
    void addRun_missingFlag_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/5.0", workouts, dictionary));
    }

    @Test
    void addRun_nonNumericDistance_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/far t/30", workouts, dictionary));
    }

    @Test
    void addRun_scientificNotationDistance_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/888e3 t/30", workouts, dictionary));
        assertTrue(ex.getMessage().contains("valid decimal numbers"));
    }

    @Test
    void addRun_nonNumericDuration_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/5.0 t/long", workouts, dictionary));
    }

    @Test
    void addRun_negativeDistance_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/-5 t/30", workouts, dictionary));
        assertTrue(ex.getMessage().toLowerCase().contains("positive"),
                "Error should mention positive distance");
    }

    @Test
    void addRun_zeroDuration_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/5.0 t/0", workouts, dictionary));
    }

    @Test
    void addRun_pipeInName_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Bad|Name d/5 t/30", workouts, dictionary));
        assertTrue(ex.getMessage().contains("|"));
    }

    // ── add-run: happy path ───────────────────────────────────────────────────

    @Test
    void addRun_validInput_addsWorkout() throws FitLoggerException {
        Command cmd = Parser.parse("add-run Morning Jog d/5.0 t/25.5", workouts, dictionary);
        cmd.execute(storage, workouts, ui, profile);

        assertEquals(1, workouts.getSize());
        assertTrue(workouts.getWorkoutAtIndex(0) instanceof RunWorkout);

        RunWorkout logged = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals("Morning Jog", logged.getDescription());
        assertEquals(5.0, logged.getDistance(), 0.001);
        assertEquals(25.5, logged.getDurationMinutes(), 0.001);
    }

    @Test
    void addRun_validShortcutId_addsWorkoutWithDictionaryName() throws FitLoggerException {
        Command cmd = Parser.parse("add-run 1 d/5.0 t/25.5", workouts, dictionary);
        cmd.execute(storage, workouts, ui, profile);

        RunWorkout logged = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals("Easy Run", logged.getDescription());
    }

    @Test
    void addRun_invalidFlagOrder_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog t/25 d/5", workouts, dictionary));
        assertTrue(ex.getMessage().contains("Invalid format"));
    }

    @Test
    void addRun_overlargeDistance_throwsException() {
        String overlargeDistance = "2" + "0".repeat(308);
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/" + overlargeDistance + " t/30",
                        workouts, dictionary));
    }

    @Test
    void addRun_unknownShortcutId_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run 999 d/5 t/30", workouts, dictionary));
        assertTrue(ex.getMessage().contains("Shortcut ID [999] does not exist"));
    }

    @Test
    void parse_viewDatabase_returnsViewDatabaseCommand() throws FitLoggerException {
        Command cmd = Parser.parse("view-database", workouts, dictionary);
        assertTrue(cmd instanceof ViewDatabaseCommand,
                "Expected ViewDatabaseCommand for view-database");
    }

    @Test
    void parse_searchDate_returnsSearchDateCommand() throws FitLoggerException {
        Command cmd = Parser.parse("search-date 2026-03-15", workouts, dictionary);
        assertTrue(cmd instanceof SearchDateCommand, "Expected SearchDateCommand for search-date");
    }

    @Test
    void searchDate_missingDate_throwsFitLoggerException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("search-date", workouts, dictionary));
    }

    @Test
    void searchDate_invalidDate_throwsFitLoggerException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("search-date 2026-15-03", workouts, dictionary));
    }

    @Test
    void searchDate_extraArgument_throwsFitLoggerException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("search-date 2026-03-15 2026-03-16", workouts, dictionary));
        assertEquals("Invalid format for search-date.\nUsage: search-date <YYYY-MM-DD>",
                ex.getMessage());
    }

    @Test
    void parse_delete_returnsDeleteCommand() throws FitLoggerException {
        Command cmd = Parser.parse("delete 1", workouts, dictionary);
        assertTrue(cmd instanceof DeleteCommand, "Expected DeleteCommand for delete");
    }

    @Test
    void delete_missingIndex_throwsFitLoggerException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("delete", workouts, dictionary));
        assertTrue(ex.getMessage().contains("Usage: delete <index>"));
    }

    @Test
    void delete_nonNumericIndex_throwsFitLoggerException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("delete abc", workouts, dictionary));
        assertEquals("Workout index must be a positive integer.", ex.getMessage());
    }

    @Test
    void delete_extraIndex_throwsFitLoggerException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("delete 1 2", workouts, dictionary));
        assertEquals("Invalid format for delete.\nUsage: delete <index>", ex.getMessage());
    }

    @Test
    void delete_zeroIndex_throwsFitLoggerException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("delete 0", workouts, dictionary));
        assertEquals("Workout index must be a positive integer.", ex.getMessage());
    }

    @Test
    void delete_overlargeIndex_throwsFitLoggerException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("delete 1000001", workouts, dictionary));
        assertEquals("Workout index must not exceed 1000000.", ex.getMessage());
    }

    @Test
    void delete_overflowingIndex_throwsFitLoggerException() {
        String overflowingIndex = "9".repeat(20);
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("delete " + overflowingIndex, workouts, dictionary));
        assertEquals("Workout index must not exceed 1000000.", ex.getMessage());
    }

    @Test
    void edit_overlargeIndex_throwsFitLoggerException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("edit 1000001 distance/5", workouts, dictionary));
        assertEquals("Workout index must not exceed 1000000.", ex.getMessage());
    }

    @Test
    void parse_edit_returnsEditCommand() throws FitLoggerException {
        Command cmd = Parser.parse("edit 1 distance/5", workouts, dictionary);
        assertTrue(cmd instanceof EditCommand, "Expected EditCommand for edit");
    }

    @Test
    void parse_exit_returnsExitCommand() throws FitLoggerException {
        Command cmd = Parser.parse("exit", workouts, dictionary);
        assertTrue(cmd instanceof ExitCommand, "Expected ExitCommand for exit");
    }

    @Test
    void exit_extraArgument_throwsFitLoggerException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("exit now", workouts, dictionary));
        assertEquals("Invalid format for exit.\nUsage: exit", ex.getMessage());
    }

    // ── add-shortcut tests ────────────────────────────────────────────────

    @Test
    void addShortcut_validLift_parsesCorrectly() throws FitLoggerException {
        Command cmd = Parser.parse("add-shortcut lift 99 Muscle Up", workouts, dictionary);
        cmd.execute(storage, workouts, ui, profile);
        assertEquals("Muscle Up", dictionary.getLiftName(99), "Muscle Up should be added to lift dictionary");
    }

    @Test
    void addShortcut_validRun_parsesCorrectly() throws FitLoggerException {
        Command cmd = Parser.parse("add-shortcut run 99 Marathon", workouts, dictionary);
        cmd.execute(storage, workouts, ui, profile);
        assertEquals("Marathon", dictionary.getRunName(99), "Marathon should be added to run dictionary");
    }

    @Test
    void addShortcut_invalidType_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-shortcut swim 5 Freestyle", workouts, dictionary));
        assertTrue(ex.getMessage().contains("'lift' or 'run'"), "Should reject invalid types");
    }

    @Test
    void addShortcut_missingArgs_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-shortcut lift 5", workouts, dictionary));
    }

    @Test
    void addShortcut_zeroId_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-shortcut lift 0 Muscle Up", workouts, dictionary));
        assertEquals("Shortcut ID must be a positive integer.", ex.getMessage());
    }

    // ── unknown command ───────────────────────────────────────────────────────

    @Test
    void parse_unknownCommand_throwsFitLoggerException() {
        assertThrows(FitLoggerException.class, () -> Parser.parse("foobar", workouts, dictionary));
    }

    // ── delimiter validator (unit test for the helper directly) ──────────────

    @Test
    void validateDelimiters_pipe_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.validateNoStorageDelimiters("has|pipe", "Field"));
    }

    @Test
    void validateDelimiters_slash_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.validateNoStorageDelimiters("has/slash", "Field"));
    }

    @Test
    void validateDelimiters_cleanString_passes() throws FitLoggerException {
        // Should complete without throwing
        Parser.validateNoStorageDelimiters("Bench Press", "Exercise name");
    }

    @Test
    void isPlainDecimalNumber_null_returnsFalse() {
        assertFalse(Parser.isPlainDecimalNumber(null));
    }

    private static class TestUi extends Ui {
        @Override
        public void showMessage(String m) {};

        @Override
        public void printWorkout(Workout w) {};
    }
}
