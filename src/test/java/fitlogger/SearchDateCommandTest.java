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
