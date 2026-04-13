package fitlogger;

import fitlogger.command.EditCommand;
import fitlogger.exception.FitLoggerException;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EditCommandTest {

    private static final Storage STORAGE = new Storage();

    @Test
    void editStrengthWeight_validIndexAndValue_updatesWeight() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "weight", "85.5");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout edited = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(85.5, edited.getWeight(), 0.001);
        assertTrue(ui.lastOutput.startsWith("Updated workout 1:"));
    }

    @Test
    void editStrengthReps_validIndexAndValue_updatesReps() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "reps", "10");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout edited = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(10, edited.getReps());
        assertTrue(ui.lastOutput.startsWith("Updated workout 1:"));
    }

    @Test
    void editRunDistance_validIndexAndValue_updatesDistance() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "distance", "7.5");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout edited = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(7.5, edited.getDistance(), 0.001);
        assertTrue(ui.lastOutput.startsWith("Updated workout 1:"));
    }

    @Test
    void editStrengthSets_validIndexAndValue_updatesSets() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "sets", "5");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout edited = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(5, edited.getSets());
        assertTrue(ui.lastOutput.startsWith("Updated workout 1:"));
    }

    @Test
    void editRunDuration_validIndexAndValue_updatesDuration() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "duration", "27.5");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout edited = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(27.5, edited.getDurationMinutes(), 0.001);
        assertTrue(ui.lastOutput.startsWith("Updated workout 1:"));
    }

    @Test
    void editInvalidIndex_showsMessage() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(2, "distance", "7.5");
        command.execute(STORAGE, workouts, ui, profile);

        assertEquals("Invalid workout index: 2", ui.lastOutput);
    }

    @Test
    void editName_withPipe_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "name", "Bench | Press");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals("Bench Press", workout.getDescription());
        assertTrue(ui.lastOutput.contains("must not contain '|' or '/'"));
    }

    @Test
    void editName_withSlash_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "description", "push/pull");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals("Morning Jog", workout.getDescription());
        assertTrue(ui.lastOutput.contains("must not contain '|' or '/'"));
    }

    @Test
    void editName_validValue_updatesDescription() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "name", "Evening Jog");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals("Evening Jog", workout.getDescription());
        assertTrue(ui.lastOutput.startsWith("Updated workout 1:"));
    }

    @Test
    void editDistance_nan_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "distance", "NaN");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(5.0, workout.getDistance(), 0.001);
        assertEquals("Invalid distance value: NaN", ui.lastOutput);
    }

    @Test
    void editDistance_infinity_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "distance", "Infinity");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(5.0, workout.getDistance(), 0.001);
        assertEquals("Invalid distance value: Infinity", ui.lastOutput);
    }

    @Test
    void editDistance_scientificNotation_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "distance", "888e3");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(5.0, workout.getDistance(), 0.001);
        assertEquals("Invalid distance value: 888e3", ui.lastOutput);
    }

    @Test
    void editDistance_overlargeValue_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();
        String overlargeDistance = "9".repeat(400);

        EditCommand command = new EditCommand(1, "distance", overlargeDistance);
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(5.0, workout.getDistance(), 0.001);
        assertEquals("Invalid distance value: " + overlargeDistance, ui.lastOutput);
    }

    @Test
    void editWeight_overlargeValue_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();
        String overlargeWeight = "9".repeat(400);

        EditCommand command = new EditCommand(1, "weight", overlargeWeight);
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(80.0, workout.getWeight(), 0.001);
        assertEquals("Invalid weight value: " + overlargeWeight, ui.lastOutput);
    }

    @Test
    void editDuration_overlargeValue_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();
        String overlargeDuration = "9".repeat(400);

        EditCommand command = new EditCommand(1, "duration", overlargeDuration);
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(30.0, workout.getDurationMinutes(), 0.001);
        assertEquals("Invalid duration value: " + overlargeDuration, ui.lastOutput);
    }

    @Test
    void editDistance_zero_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "distance", "0");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(5.0, workout.getDistance(), 0.001);
        assertEquals("Distance must be a positive number.", ui.lastOutput);
    }

    @Test
    void editWeight_onRunWorkout_showsFieldNotValidMessage() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "weight", "80");
        command.execute(STORAGE, workouts, ui, profile);

        assertEquals("Field 'weight' is only valid for lift workouts.", ui.lastOutput);
    }

    @Test
    void editSets_onRunWorkout_showsFieldNotValidMessage() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "sets", "3");
        command.execute(STORAGE, workouts, ui, profile);

        assertEquals("Field 'sets' is only valid for lift workouts.", ui.lastOutput);
    }

    @Test
    void editReps_onRunWorkout_showsFieldNotValidMessage() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "reps", "10");
        command.execute(STORAGE, workouts, ui, profile);

        assertEquals("Field 'reps' is only valid for lift workouts.", ui.lastOutput);
    }

    @Test
    void editDistance_onStrengthWorkout_showsFieldNotValidMessage() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "distance", "6");
        command.execute(STORAGE, workouts, ui, profile);

        assertEquals("Field 'distance' is only valid for run workouts.", ui.lastOutput);
    }

    @Test
    void editDuration_onStrengthWorkout_showsFieldNotValidMessage() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "duration", "20");
        command.execute(STORAGE, workouts, ui, profile);

        assertEquals("Field 'duration' is only valid for run workouts.", ui.lastOutput);
    }

    @Test
    void editUnknownField_showsUnknownFieldMessage() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "pace", "6:00");
        command.execute(STORAGE, workouts, ui, profile);

        assertEquals("Unknown editable field: pace", ui.lastOutput);
    }

    @Test
    void editSets_nonNumeric_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "sets", "three");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(3, workout.getSets());
        assertEquals("Invalid sets value: three", ui.lastOutput);
    }

    @Test
    void editWeight_nonNumeric_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "weight", "heavy");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(80.0, workout.getWeight(), 0.001);
        assertEquals("Invalid weight value: heavy", ui.lastOutput);
    }

    @Test
    void editWeight_negative_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "weight", "-1");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(80.0, workout.getWeight(), 0.001);
        assertEquals("Weight cannot be negative.", ui.lastOutput);
    }

    @Test
    void editDistance_nonNumeric_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "distance", "far");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(5.0, workout.getDistance(), 0.001);
        assertEquals("Invalid distance value: far", ui.lastOutput);
    }

    @Test
    void editDuration_nonNumeric_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "duration", "fast");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(30.0, workout.getDurationMinutes(), 0.001);
        assertEquals("Invalid duration value: fast", ui.lastOutput);
    }

    @Test
    void editDuration_zero_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "duration", "0");
        command.execute(STORAGE, workouts, ui, profile);

        RunWorkout workout = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(30.0, workout.getDurationMinutes(), 0.001);
        assertEquals("Duration must be a positive number.", ui.lastOutput);
    }

    @Test
    void editReps_zero_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "reps", "0");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(8, workout.getReps());
        assertEquals("Reps must be a positive integer.", ui.lastOutput);
    }

    @Test
    void editReps_nonNumeric_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "reps", "many");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(8, workout.getReps());
        assertEquals("Invalid reps value: many", ui.lastOutput);
    }

    @Test
    void editSets_zero_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "sets", "0");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(3, workout.getSets());
        assertEquals("Sets must be a positive integer.", ui.lastOutput);
    }

    @Test
    void editSets_overlargeValue_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "sets", "1000001");
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(3, workout.getSets());
        assertEquals("Sets must not exceed 1000000.", ui.lastOutput);
    }

    @Test
    void editSets_integerOverflow_rejectedAndUnchanged() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();
        String overflowingSets = "9".repeat(20);

        EditCommand command = new EditCommand(1, "sets", overflowingSets);
        command.execute(STORAGE, workouts, ui, profile);

        StrengthWorkout workout = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(3, workout.getSets());
        assertEquals("Sets must not exceed 1000000.", ui.lastOutput);
    }

    @Test
    void editSuccess_callsSaveData() throws FitLoggerException {
        FakeStorage storage = new FakeStorage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "weight", "82.5");
        command.execute(storage, workouts, ui, profile);

        assertTrue(storage.saveCalled);
        assertEquals(profile, storage.savedProfile);
        assertEquals(1, storage.savedWorkouts.size());
    }

    @Test
    void editFailure_doesNotCallSaveData() throws FitLoggerException {
        FakeStorage storage = new FakeStorage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 13), 5.0, 30.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "weight", "80");
        command.execute(storage, workouts, ui, profile);

        assertFalse(storage.saveCalled);
    }

    @Test
    void editSuccess_saveFailure_showsErrorAndNoSuccessMessage() throws FitLoggerException {
        FakeStorage storage = new FakeStorage();
        storage.shouldSaveSucceed = false;
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 13)));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        EditCommand command = new EditCommand(1, "weight", "82.5");
        command.execute(storage, workouts, ui, profile);

        assertTrue(storage.saveCalled);
        assertEquals("Failed to save workouts to disk. Changes remain only in memory.", ui.lastError);
        assertNull(ui.lastOutput);
    }

    private static class FakeStorage extends Storage {
        private boolean saveCalled;
        private boolean shouldSaveSucceed = true;
        private List<Workout> savedWorkouts;
        private UserProfile savedProfile;

        @Override
        public boolean saveData(List<Workout> workouts, UserProfile profile) {
            saveCalled = true;
            savedWorkouts = workouts;
            savedProfile = profile;
            return shouldSaveSucceed;
        }
    }

    private static class TestUi extends Ui {
        private String lastOutput;
        private String lastError;

        @Override
        public void showMessage(String command) {
            lastOutput = command;
        }

        @Override
        public void showError(String message) {
            lastError = message;
        }
    }
}

