package fitlogger;

import fitlogger.command.AddWorkoutCommand;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddWorkoutCommandTest {

    @Test
    void execute_addsWorkoutAndCallsSaveData() throws Exception {
        FakeStorage storage = new FakeStorage();
        WorkoutList workouts = new WorkoutList();
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();
        RunWorkout workout = new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 15), 5.0, 30.0);

        AddWorkoutCommand command = new AddWorkoutCommand(workout);
        command.execute(storage, workouts, ui, profile);

        assertEquals(1, workouts.getSize());
        assertEquals("Morning Jog", workouts.getWorkoutAtIndex(0).getDescription());
        assertTrue(storage.saveCalled);
        assertEquals(1, storage.savedWorkouts.size());
        assertEquals(profile, storage.savedProfile);
        assertTrue(ui.outputs.stream().anyMatch(line -> line.contains("added this workout")));
    }

    @Test
    void execute_saveFailure_showsErrorAndNoSuccessMessage() throws Exception {
        FakeStorage storage = new FakeStorage();
        storage.shouldSaveSucceed = false;
        WorkoutList workouts = new WorkoutList();
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();
        RunWorkout workout = new RunWorkout("Morning Jog", LocalDate.of(2026, 3, 15), 5.0, 30.0);

        AddWorkoutCommand command = new AddWorkoutCommand(workout);
        command.execute(storage, workouts, ui, profile);

        assertEquals(1, workouts.getSize());
        assertTrue(storage.saveCalled);
        assertTrue(ui.errors.stream().anyMatch(line -> line.contains("Failed to save workouts")));
        assertFalse(ui.outputs.stream().anyMatch(line -> line.contains("added this workout")));
    }

    private static class FakeStorage extends Storage {
        private boolean saveCalled;
        private boolean shouldSaveSucceed = true;
        private List<Workout> savedWorkouts = new ArrayList<>();
        private UserProfile savedProfile;

        @Override
        public boolean saveData(List<Workout> workouts, UserProfile profile) {
            saveCalled = true;
            savedWorkouts = new ArrayList<>(workouts);
            savedProfile = profile;
            return shouldSaveSucceed;
        }
    }

    private static class TestUi extends Ui {
        private final List<String> outputs = new ArrayList<>();
        private final List<String> errors = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            outputs.add(message);
        }

        @Override
        public void printWorkout(Workout workout) {
            outputs.add(workout.toString());
        }

        @Override
        public void showError(String message) {
            errors.add(message);
        }
    }
}
