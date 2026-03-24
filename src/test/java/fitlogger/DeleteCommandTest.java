package fitlogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import fitlogger.exception.FitLoggerException;
import fitlogger.workout.RunWorkout;
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

        DeleteCommand command = new DeleteCommand("squat");
        command.execute(storage, workouts, ui);

        assertEquals(1, workouts.getSize());
        assertFalse(workouts.getWorkoutAtIndex(0).getDescription().equalsIgnoreCase("Squat"));
        assertEquals("Deleted workout: Squat", ui.lastOutput);
    }

    @Test
    void deleteWorkout_byIndex_deletesWorkoutAtOneBasedPosition() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Squat", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        workouts.addWorkout(new RunWorkout("Bench Press", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        workouts.addWorkout(new RunWorkout("Deadlift", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();

        DeleteCommand command = new DeleteCommand("3");
        command.execute(storage, workouts, ui);

        assertEquals(2, workouts.getSize());
        assertFalse(workouts.getWorkoutAtIndex(0).getDescription().equalsIgnoreCase("Deadlift"));
        assertFalse(workouts.getWorkoutAtIndex(1).getDescription().equalsIgnoreCase("Deadlift"));
        assertEquals("Deleted workout: Deadlift", ui.lastOutput);
    }

    @Test
    void deleteWorkout_missingWorkoutName_showsUsageMessage() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Deadlift", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();

        DeleteCommand command = new DeleteCommand(" ");
        command.execute(storage, workouts, ui);

        assertTrue(workouts.getWorkoutAtIndex(0).getDescription().equals("Deadlift"));
        assertEquals(
            "Please specify a workout to delete. Usage: delete <WORKOUT_NAME> or delete <index>",
                ui.lastOutput);
    }

    @Test
    void deleteWorkout_nonExistingWorkout_showsNotFoundMessage() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Deadlift", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();

        DeleteCommand command = new DeleteCommand("Pull Up");
        command.execute(storage, workouts, ui);

        assertEquals(1, workouts.getSize());
        assertEquals("Workout not found: Pull Up", ui.lastOutput);
    }

    @Test
    void deleteWorkout_zeroIndex_showsNotFoundMessage() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Deadlift", LocalDate.of(2026, 3, 15), 1.0, 1.0));
        TestUi ui = new TestUi();

        DeleteCommand command = new DeleteCommand("0");
        command.execute(storage, workouts, ui);

        assertEquals(1, workouts.getSize());
        assertEquals("Workout not found: 0", ui.lastOutput);
    }

    private static class TestUi extends Ui {
        private String lastOutput;

        @Override
        public void showMessage(String command) {
            lastOutput = command;
        }
    }
}
