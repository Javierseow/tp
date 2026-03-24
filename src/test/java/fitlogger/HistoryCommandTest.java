package fitlogger;

import fitlogger.command.ViewHistoryCommand;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryCommandTest {
    private WorkoutList workouts;
    private TestUi ui;
    private Storage storage;
    private ViewHistoryCommand historyCommand;

    @BeforeEach
    void setUp() {
        workouts = new WorkoutList();
        ui = new TestUi();
        storage = new Storage();
        historyCommand = new ViewHistoryCommand();
    }

    @Test
    void execute_emptyList_showsOnlyHeader() {
        historyCommand.execute(storage, workouts, ui);

        assertTrue(ui.getOutputs().contains("Here's your past exercises"));
        assertEquals(3, ui.getOutputs().size());
    }

    @Test
    void execute_withWorkouts_printsWorkoutsInOrder() {
        Storage storage = new Storage();
        workouts.addWorkout(new RunWorkout("Morning Run", LocalDate.of(2026, 3, 15), 5.0, 1.0));

        historyCommand.execute(storage, workouts, ui);

        assertTrue(ui.getOutputs().stream().anyMatch(s -> s.contains("1. ")));
        assertTrue(ui.getOutputs().stream().anyMatch(s -> s.contains("Morning Run")));
    }

    @Test
    void execute_multipleWorkouts_matchesExactSequence() {
        Storage storage = new Storage();
        workouts.addWorkout(new RunWorkout("test 1", LocalDate.of(2026, 3, 20), 1.0, 1.0));
        workouts.addWorkout(new RunWorkout("test 2", LocalDate.of(2026, 3, 21), 5.0, 30.0));

        historyCommand.execute(storage, workouts, ui);

        List<String> results = ui.getOutputs();

        // "[Run] <desc> (Date: <date>) (Distance: <d>km, Duration: <t> mins)"
        String expectedLine1 =
                "1. [Run] test 1 (Date: 2026-03-20) (Distance: 1.0km, Duration: 1.0 mins)";
        String expectedLine2 =
                "2. [Run] test 2 (Date: 2026-03-21) (Distance: 5.0km, Duration: 30.0 mins)";

        String actualFullLine1 = results.get(2) + results.get(3);
        String actualFullLine2 = results.get(4) + results.get(5);

        assertEquals(expectedLine1, actualFullLine1, "First workout format is incorrect.");
        assertEquals(expectedLine2, actualFullLine2, "Second workout format is incorrect.");
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

        public List<String> getOutputs() {
            return outputs;
        }
    }
}
