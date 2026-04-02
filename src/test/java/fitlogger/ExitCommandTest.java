package fitlogger;

import fitlogger.command.ExitCommand;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExitCommandTest {
    @Test
    void execute_savesDataAndShowsGoodbye() {
        FakeStorage storage = new FakeStorage();
        WorkoutList workouts = new WorkoutList();
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        ExitCommand command = new ExitCommand();
        command.execute(storage, workouts, ui, profile);

        assertTrue(storage.saveCalled);
        assertNotNull(storage.savedWorkouts);
        assertTrue(storage.savedWorkouts.isEmpty());
        assertEquals(profile, storage.savedProfile);
        assertTrue(ui.savedMessageShown);
        assertTrue(ui.goodbyeShown);
    }

    @Test
    void isExit_returnsTrue() {
        ExitCommand command = new ExitCommand();
        assertTrue(command.isExit());
    }

    @Test
    void execute_saveFails_showsErrorAndStillShowsGoodbye() {
        FakeStorage storage = new FakeStorage();
        storage.shouldSaveSucceed = false;
        WorkoutList workouts = new WorkoutList();
        TestUi ui = new TestUi();
        UserProfile profile = new UserProfile();

        ExitCommand command = new ExitCommand();
        command.execute(storage, workouts, ui, profile);

        assertTrue(storage.saveCalled);
        assertFalse(ui.savedMessageShown);
        assertTrue(ui.errorShown);
        assertTrue(ui.goodbyeShown);
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
        private boolean savedMessageShown;
        private boolean errorShown;
        private boolean goodbyeShown;

        @Override
        public void showMessage(String message) {
            if ("Workouts saved.".equals(message)) {
                savedMessageShown = true;
            }
        }

        @Override
        public void showGoodbye() {
            goodbyeShown = true;
        }

        @Override
        public void showError(String message) {
            errorShown = true;
        }
    }
}
