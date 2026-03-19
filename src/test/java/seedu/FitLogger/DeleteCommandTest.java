package seedu.fitlogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.fitlogger.command.DeleteCommand;
import seedu.fitlogger.workout.Running;
import seedu.fitlogger.workout.WorkoutList;

class DeleteCommandTest {
    @Test
    void deleteWorkout_existingWorkout_deletesWorkout() {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new Running("Squat", LocalDate.of(2026, 3, 15), 1.0));
        workouts.addWorkout(new Running("Bench Press", LocalDate.of(2026, 3, 15), 1.0));
        TestUi ui = new TestUi();

        DeleteCommand command = new DeleteCommand(workouts, "squat");
        command.execute(ui);

        assertEquals(1, workouts.getSize());
        assertFalse(workouts.getWorkout(0).getDescription().equalsIgnoreCase("Squat"));
        assertEquals("Deleted workout: Squat", ui.lastOutput);
    }

    @Test
    void deleteWorkout_byIndex_deletesWorkoutAtOneBasedPosition() {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new Running("Squat", LocalDate.of(2026, 3, 15), 1.0));
        workouts.addWorkout(new Running("Bench Press", LocalDate.of(2026, 3, 15), 1.0));
        workouts.addWorkout(new Running("Deadlift", LocalDate.of(2026, 3, 15), 1.0));
        TestUi ui = new TestUi();

        DeleteCommand command = new DeleteCommand(workouts, "3");
        command.execute(ui);

        assertEquals(2, workouts.getSize());
        assertFalse(workouts.getWorkout(0).getDescription().equalsIgnoreCase("Deadlift"));
        assertFalse(workouts.getWorkout(1).getDescription().equalsIgnoreCase("Deadlift"));
        assertEquals("Deleted workout: Deadlift", ui.lastOutput);
    }

    @Test
    void deleteWorkout_missingWorkoutName_showsUsageMessage() {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new Running("Deadlift", LocalDate.of(2026, 3, 15), 1.0));
        TestUi ui = new TestUi();

        DeleteCommand command = new DeleteCommand(workouts, " ");
        command.execute(ui);

        assertTrue(workouts.getWorkout(0).getDescription().equals("Deadlift"));
        assertEquals("Please specify a workout to delete. Usage: delete workout <WORKOUT_NAME> or delete <index>",
            ui.lastOutput);
    }

    @Test
    void deleteWorkout_nonExistingWorkout_showsNotFoundMessage() {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new Running("Deadlift", LocalDate.of(2026, 3, 15), 1.0));
        TestUi ui = new TestUi();

        DeleteCommand command = new DeleteCommand(workouts, "Pull Up");
        command.execute(ui);

        assertEquals(1, workouts.getSize());
        assertEquals("Workout not found: Pull Up", ui.lastOutput);
    }

    @Test
    void deleteWorkout_zeroIndex_showsNotFoundMessage() {
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new Running("Deadlift", LocalDate.of(2026, 3, 15), 1.0));
        TestUi ui = new TestUi();

        DeleteCommand command = new DeleteCommand(workouts, "0");
        command.execute(ui);

        assertEquals(1, workouts.getSize());
        assertEquals("Workout not found: 0", ui.lastOutput);
    }

    private static class TestUi extends Ui {
        private String lastOutput;

        @Override
        public void temporaryOutput(String command) {
            lastOutput = command;
        }
    }
}
