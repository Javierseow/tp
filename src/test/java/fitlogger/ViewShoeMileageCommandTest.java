package fitlogger;

import fitlogger.command.ViewShoeMileageCommand;
import fitlogger.exception.FitLoggerException;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workoutlist.WorkoutList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewShoeMileageCommandTest {
    private WorkoutList workouts;
    private TestUi ui;
    private Storage storage;
    private UserProfile profile;

    @BeforeEach
    void setUp() {
        workouts = new WorkoutList();
        ui = new TestUi();
        storage = new Storage();
        profile = new UserProfile();
    }

    @Test
    void execute_allTime_calculatesCorrectTotal() throws FitLoggerException {
        // Add runs at different dates
        workouts.addWorkout(new RunWorkout("Old Run", LocalDate.of(2020, 1, 1), 10.0, 60.0));
        workouts.addWorkout(new RunWorkout("Recent Run", LocalDate.now(), 5.5, 30.0));

        ViewShoeMileageCommand command = new ViewShoeMileageCommand(); // -1 limit
        command.execute(storage, workouts, ui, profile);

        String output = ui.getLastMessage();
        // Check for 15.50km across 2 runs
        assertTrue(output.contains("Total shoe mileage (all time): 15.50km across 2 run(s)."));
    }

    @Test
    void execute_withDaysLimit_filtersCorrectly() throws FitLoggerException {
        LocalDate today = LocalDate.now();
        LocalDate tenDaysAgo = today.minusDays(10);
        LocalDate oneYearAgo = today.minusDays(365);

        workouts.addWorkout(new RunWorkout("Ten days ago", tenDaysAgo, 5.0, 30.0));
        workouts.addWorkout(new RunWorkout("Way back", oneYearAgo, 100.0, 600.0));

        // Filter for past 30 days - should only catch the 5km run
        ViewShoeMileageCommand command = new ViewShoeMileageCommand(30);
        command.execute(storage, workouts, ui, profile);

        String output = ui.getLastMessage();
        assertTrue(output.contains("Total shoe mileage (past 30 day(s)): 5.00km across 1 run(s)."));
    }

    @Test
    void execute_emptyList_showsZero() {
        ViewShoeMileageCommand command = new ViewShoeMileageCommand(7);
        command.execute(storage, workouts, ui, profile);

        String output = ui.getLastMessage();
        assertTrue(output.contains("Total shoe mileage (past 7 day(s)): 0.00km across 0 run(s)."));
    }

    /**
     * UI Stub to capture output messages for verification.
     */
    private static class TestUi extends Ui {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        public String getLastMessage() {
            return messages.isEmpty() ? "" : messages.get(messages.size() - 1);
        }
    }
}