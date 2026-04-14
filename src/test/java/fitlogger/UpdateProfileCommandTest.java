package fitlogger;

import fitlogger.command.UpdateProfileCommand;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateProfileCommandTest {

    private TestUi ui;
    private Storage storage;
    private WorkoutList workouts;
    private UserProfile profile;

    @BeforeEach
    void setUp() {
        ui = new TestUi();
        storage = new Storage();
        workouts = new WorkoutList();
        profile = new UserProfile();
    }

    @Test
    void execute_updateAllFields_allProfileFieldsUpdated() {
        UpdateProfileCommand command = new UpdateProfileCommand("Alice", 1.75, 65.5);
        command.execute(storage, workouts, ui, profile);

        assertEquals("Alice", profile.getName());
        assertEquals(1.75, profile.getHeight(), 0.001);
        assertEquals(65.5, profile.getWeight(), 0.001);
    }

    @Test
    void execute_updateNameOnly_onlyNameChanges() {
        UpdateProfileCommand command = new UpdateProfileCommand("Bob", -1, -1);
        command.execute(storage, workouts, ui, profile);

        assertEquals("Bob", profile.getName());
        assertEquals(-1, profile.getHeight(), 0.001);
        assertEquals(-1, profile.getWeight(), 0.001);
    }

    @Test
    void execute_updateHeightOnly_onlyHeightChanges() {
        UpdateProfileCommand command = new UpdateProfileCommand(null, 1.80, -1);
        command.execute(storage, workouts, ui, profile);

        assertNull(profile.getName());
        assertEquals(1.80, profile.getHeight(), 0.001);
        assertEquals(-1, profile.getWeight(), 0.001);
    }

    @Test
    void execute_updateWeightOnly_onlyWeightChanges() {
        UpdateProfileCommand command = new UpdateProfileCommand(null, -1, 70.0);
        command.execute(storage, workouts, ui, profile);

        assertNull(profile.getName());
        assertEquals(-1, profile.getHeight(), 0.001);
        assertEquals(70.0, profile.getWeight(), 0.001);
    }

    @Test
    void execute_noFieldsProvided_profileUnchanged() {
        UpdateProfileCommand command = new UpdateProfileCommand(null, -1, -1);
        command.execute(storage, workouts, ui, profile);

        assertNull(profile.getName());
        assertEquals(-1, profile.getHeight(), 0.001);
        assertEquals(-1, profile.getWeight(), 0.001);
        // Two separator lines are shown (before and after), even when no fields are provided
        List<String> outputs = ui.getOutputs();
        assertEquals(2, outputs.size());
        assertTrue(outputs.get(0).equals("---line---"));
        assertTrue(outputs.get(1).equals("---line---"));
    }

    @Test
    void execute_updateName_showsCorrectConfirmationMessage() {
        UpdateProfileCommand command = new UpdateProfileCommand("Charlie", -1, -1);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.getOutputs().contains("Name has been updated to Charlie"));
    }

    @Test
    void execute_updateHeight_showsCorrectConfirmationMessage() {
        UpdateProfileCommand command = new UpdateProfileCommand(null, 1.65, -1);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.getOutputs().contains("Height has been updated to 1.65m"));
    }

    @Test
    void execute_updateWeight_showsCorrectConfirmationMessage() {
        UpdateProfileCommand command = new UpdateProfileCommand(null, -1, 58.0);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.getOutputs().contains("Weight has been updated to 58.00kg"));
    }

    @Test
    void execute_updateAllFields_allThreeMessagesShown() {
        UpdateProfileCommand command = new UpdateProfileCommand("Dana", 1.70, 60.0);
        command.execute(storage, workouts, ui, profile);

        List<String> outputs = ui.getOutputs();
        // 5 total outputs: line + 3 messages + line
        assertEquals(5, outputs.size());
        assertTrue(outputs.get(0).equals("---line---"));
        assertTrue(outputs.get(1).contains("Name has been updated to Dana"));
        assertTrue(outputs.get(2).contains("Height has been updated to 1.70m"));
        assertTrue(outputs.get(3).contains("Weight has been updated to 60.00kg"));
        assertTrue(outputs.get(4).equals("---line---"));
    }

    @Test
    void execute_overwriteExistingValues_profileUpdatedToNewValues() {
        profile.setName("Old Name");
        profile.setHeight(1.60);
        profile.setWeight(55.0);

        UpdateProfileCommand command = new UpdateProfileCommand("New Name", 1.75, 65.0);
        command.execute(storage, workouts, ui, profile);

        assertEquals("New Name", profile.getName());
        assertEquals(1.75, profile.getHeight(), 0.001);
        assertEquals(65.0, profile.getWeight(), 0.001);
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
