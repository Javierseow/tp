package fitlogger;

import fitlogger.command.ViewProfileCommand;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewProfileCommandTest {

    private ViewProfileCommand command;
    private TestUi ui;
    private Storage storage;
    private WorkoutList workouts;
    private UserProfile profile;

    @BeforeEach
    void setUp() {
        command = new ViewProfileCommand();
        ui = new TestUi();
        storage = new Storage();
        workouts = new WorkoutList();
        profile = new UserProfile();
    }

    @Test
    void execute_allFieldsSet_displaysCorrectValues() {
        profile.setName("Alice");
        profile.setHeight(1.75);
        profile.setWeight(65.5);

        command.execute(storage, workouts, ui, profile);

        List<String> outputs = ui.getOutputs();
        assertTrue(outputs.contains("Alice"));
        assertTrue(outputs.contains("1.75m"));
        assertTrue(outputs.contains("65.50kg"));
    }

    @Test
    void execute_noFieldsSet_displaysNotSetMessages() {
        command.execute(storage, workouts, ui, profile);

        List<String> outputs = ui.getOutputs();
        assertTrue(outputs.contains("name not set yet"));
        assertTrue(outputs.contains("height not set yet"));
        assertTrue(outputs.contains("weight not set yet"));
    }

    @Test
    void execute_onlyNameSet_displaysNameAndPlaceholders() {
        profile.setName("Bob");

        command.execute(storage, workouts, ui, profile);

        List<String> outputs = ui.getOutputs();
        assertTrue(outputs.contains("Bob"));
        assertTrue(outputs.contains("height not set yet"));
        assertTrue(outputs.contains("weight not set yet"));
    }

    @Test
    void execute_outputIncludesLabels() {
        command.execute(storage, workouts, ui, profile);

        List<String> outputs = ui.getOutputs();
        assertTrue(outputs.contains("Name: "));
        assertTrue(outputs.contains("Height: "));
        assertTrue(outputs.contains("Weight: "));
    }

    @Test
    void execute_outputHasTwoSeparatorLines() {
        command.execute(storage, workouts, ui, profile);

        long lineCount = ui.getOutputs().stream()
                .filter(s -> s.equals("---line---"))
                .count();
        assertEquals(2, lineCount);
    }

    @Test
    void execute_nameWithSpaces_displaysCorrectly() {
        profile.setName("John Doe");

        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.getOutputs().contains("John Doe"));
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
