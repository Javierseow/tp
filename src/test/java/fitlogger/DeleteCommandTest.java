package fitlogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import fitlogger.exception.FitLoggerException;
import fitlogger.profile.UserProfile;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.Workout;
import org.junit.jupiter.api.Test;

import fitlogger.command.DeleteCommand;
import fitlogger.storage.Storage;
import fitlogger.workoutlist.WorkoutList;
import fitlogger.ui.Ui;

class DeleteCommandTest {
    @Test
    void deleteWorkout_existingWorkout_deletesWorkout() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Squat", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        workouts.addWorkout(new RunWorkout("Bench Press", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        DeleteCommand command = new DeleteCommand(1);
        command.execute(storage, workouts, ui, profile);

        assertEquals(1, workouts.getSize());
        assertFalse(workouts.getWorkoutAtIndex(0).getDescription().equalsIgnoreCase("Squat"));
        assertEquals("Deleted workout: Squat", ui.lastOutput);
    }

    @Test
    void isExit_returnsFalse() {
        DeleteCommand command = new DeleteCommand(1);
        assertFalse(command.isExit());
    }

    @Test
    void deleteWorkout_byIndex_deletesWorkoutAtOneBasedPosition() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Squat", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        workouts.addWorkout(new RunWorkout("Bench Press", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        workouts.addWorkout(new RunWorkout("Deadlift", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        DeleteCommand command = new DeleteCommand(3);
        command.execute(storage, workouts, ui, profile);

        assertEquals(2, workouts.getSize());
        assertFalse(workouts.getWorkoutAtIndex(0).getDescription().equalsIgnoreCase("Deadlift"));
        assertFalse(workouts.getWorkoutAtIndex(1).getDescription().equalsIgnoreCase("Deadlift"));
        assertEquals("Deleted workout: Deadlift", ui.lastOutput);
    }

    @Test
    void deleteWorkout_outOfRangeIndex_showsInvalidIndexMessage() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Deadlift", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        DeleteCommand command = new DeleteCommand(2);
        command.execute(storage, workouts, ui, profile);

        assertTrue(workouts.getWorkoutAtIndex(0).getDescription().equals("Deadlift"));
        assertEquals("Invalid workout index: 2", ui.lastOutput);
    }

    @Test
    void deleteWorkout_success_callsSaveData() throws FitLoggerException {
        FakeStorage storage = new FakeStorage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Deadlift", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        DeleteCommand command = new DeleteCommand(1);
        command.execute(storage, workouts, ui, profile);

        assertTrue(storage.saveCalled);
        assertEquals(profile, storage.savedProfile);
        assertEquals(0, storage.savedWorkouts.size());
    }

    @Test
    void deleteWorkout_outOfRangeIndex_doesNotCallSaveData() throws FitLoggerException {
        FakeStorage storage = new FakeStorage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Deadlift", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        DeleteCommand command = new DeleteCommand(2);
        command.execute(storage, workouts, ui, profile);

        assertFalse(storage.saveCalled);
    }

    @Test
    void deleteWorkout_saveFailure_showsErrorAndNoSuccessMessage() throws FitLoggerException {
        FakeStorage storage = new FakeStorage();
        storage.shouldSaveSucceed = false;
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Deadlift", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        DeleteCommand command = new DeleteCommand(1);
        command.execute(storage, workouts, ui, profile);

        assertTrue(storage.saveCalled);
        assertEquals("Failed to save workouts to disk. Changes remain only in memory.", ui.lastError);
        assertFalse("Deleted workout: Deadlift".equals(ui.lastOutput));
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

