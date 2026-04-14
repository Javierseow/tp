package fitlogger;

import fitlogger.command.SearchDateCommand;
import fitlogger.exception.FitLoggerException;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workoutlist.WorkoutList;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchDateCommandTest {
    @Test
    void execute_matchingDate_printsOnlyMatchingWorkouts() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        LocalDate targetDate = LocalDate.of(2026, 3, 15);
        workouts.addWorkout(new RunWorkout("Morning Run", targetDate, 5.0, 30.0));
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, targetDate));
        workouts.addWorkout(new RunWorkout("Evening Run", LocalDate.of(2026, 3, 16), 3.0, 20.0));

        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        SearchDateCommand command = new SearchDateCommand(targetDate);
        command.execute(storage, workouts, ui, profile);

        assertEquals("Workouts on 2026-03-15:", ui.outputs.get(0));
        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Morning Run")));
        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Bench Press")));
        assertTrue(ui.outputs.stream().noneMatch(line -> line.contains("Evening Run")));
    }

    @Test
    void execute_noMatchingWorkouts_printsEmptyMessage() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        workouts.addWorkout(new RunWorkout("Morning Run", LocalDate.of(2026, 3, 15), 5.0, 30.0));

        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        SearchDateCommand command = new SearchDateCommand(LocalDate.of(2026, 3, 16));
        command.execute(storage, workouts, ui, profile);

        assertEquals(1, ui.outputs.size());
        assertTrue(ui.outputs.contains("No workouts found."));
    }

    @Test
    void execute_multipleWorkoutsSameDate_returnsAll() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        LocalDate targetDate = LocalDate.of(2026, 4, 10);

        // Add multiple workouts on the same date
        workouts.addWorkout(new StrengthWorkout("Squat", 100.0, 3, 8, targetDate));
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, targetDate));
        workouts.addWorkout(new RunWorkout("Evening Run", targetDate, 5.0, 30.0));

        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        SearchDateCommand command = new SearchDateCommand(targetDate);
        command.execute(storage, workouts, ui, profile);

        // Should display all 3 workouts for the date
        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Squat")));
        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Bench Press")));
        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Evening Run")));
    }

    @Test
    void execute_boundaryDateJanuary1st_returnsMatchingWorkouts() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        LocalDate boundaryDate = LocalDate.of(2026, 1, 1);

        workouts.addWorkout(new RunWorkout("New Year Run", boundaryDate, 3.0, 20.0));
        workouts.addWorkout(new RunWorkout("Later Run", LocalDate.of(2026, 1, 2), 4.0, 25.0));

        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        SearchDateCommand command = new SearchDateCommand(boundaryDate);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("New Year Run")));
        assertTrue(ui.outputs.stream().noneMatch(line -> line.contains("Later Run")));
    }

    @Test
    void execute_boundaryDateDecember31st_returnsMatchingWorkouts() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        LocalDate boundaryDate = LocalDate.of(2026, 12, 31);

        workouts.addWorkout(new StrengthWorkout("Year-End Lift", 100.0, 2, 5, boundaryDate));
        workouts.addWorkout(new RunWorkout("Next Year Run", LocalDate.of(2027, 1, 1), 5.0, 30.0));

        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        SearchDateCommand command = new SearchDateCommand(boundaryDate);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Year-End Lift")));
        assertTrue(ui.outputs.stream().noneMatch(line -> line.contains("Next Year Run")));
    }

    @Test
    void execute_onlyStrengthWorkoutsOnDate_returnsAllWorkouts() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        LocalDate targetDate = LocalDate.of(2026, 5, 20);

        workouts.addWorkout(new StrengthWorkout("Deadlift", 150.0, 3, 1, targetDate));
        workouts.addWorkout(new StrengthWorkout("Row", 120.0, 4, 6, targetDate));
        workouts.addWorkout(new RunWorkout("Run", LocalDate.of(2026, 5, 21), 5.0, 30.0));

        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        SearchDateCommand command = new SearchDateCommand(targetDate);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Deadlift")));
        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Row")));
        assertTrue(ui.outputs.stream().noneMatch(line -> line.contains("Run")));
    }

    @Test
    void execute_onlyRunWorkoutsOnDate_returnsAllWorkouts() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        LocalDate targetDate = LocalDate.of(2026, 6, 15);

        workouts.addWorkout(new RunWorkout("Morning Run", targetDate, 5.0, 25.0));
        workouts.addWorkout(new RunWorkout("Evening Run", targetDate, 3.0, 20.0));
        workouts.addWorkout(new StrengthWorkout("Lift", 100.0, 3, 8, LocalDate.of(2026, 6, 16)));

        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        SearchDateCommand command = new SearchDateCommand(targetDate);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Morning Run")));
        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("Evening Run")));
        assertTrue(ui.outputs.stream().noneMatch(line -> line.contains("Lift")));
    }

    @Test
    void execute_emptyWorkoutList_showsNoWorkoutsFound() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        LocalDate targetDate = LocalDate.of(2026, 3, 15);

        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        SearchDateCommand command = new SearchDateCommand(targetDate);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.outputs.contains("No workouts found."));
    }

    @Test
    void execute_differentMonthSameDay_noMatches() throws FitLoggerException {
        Storage storage = new Storage();
        WorkoutList workouts = new WorkoutList();
        LocalDate targetDate = LocalDate.of(2026, 3, 15);

        // Add workout on different month, same day of month
        workouts.addWorkout(new RunWorkout("April Run", LocalDate.of(2026, 4, 15), 5.0, 30.0));

        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        SearchDateCommand command = new SearchDateCommand(targetDate);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.outputs.contains("No workouts found."));
    }

    private static class TestUi extends Ui {
        private final List<String> outputs = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            outputs.add(message);
        }

        @Override
        public void showMessageNoNewline(String message) {
            outputs.add(message);
        }

        @Override
        public void showLine() {
            outputs.add("---line---");
        }
    }
}
