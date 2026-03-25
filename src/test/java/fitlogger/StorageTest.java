package fitlogger;

import fitlogger.exception.FitLoggerException;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.workout.RunWorkout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fitlogger.workout.Workout;

class StorageTest {

    private static final String FILE_PATH = "data/fitlogger.txt";
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new Storage();
    }

    @AfterEach
    void tearDown() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void saveData_emptyList_createsFileWithOnlyProfileLine() throws IOException {
        List<Workout> emptyList = new ArrayList<>();
        UserProfile profile = new UserProfile();

        storage.saveData(emptyList, profile);

        File file = new File(FILE_PATH);
        assertTrue(file.exists(), "File should be created even for empty workout list");
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(1, lines.size(), "File should contain only the profile line");
        assertTrue(lines.get(0).startsWith("name: "), "First line should be the profile entry");
    }

    @Test
    void saveData_singleRunWorkout_writesProfileOnLine1AndWorkoutOnLine2()
            throws IOException, FitLoggerException {
        List<Workout> workouts = new ArrayList<>();
        workouts.add(new RunWorkout("Morning run", LocalDate.of(2024, 3, 15), 5.0, 1.0));

        UserProfile profile = new UserProfile();
        storage.saveData(workouts, profile);

        List<String> lines = Files.readAllLines(new File(FILE_PATH).toPath());
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).startsWith("name: "), "Line 1 should be the profile entry");
        assertEquals("R | Morning run | 2024-03-15 | 5.0 | 1.0", lines.get(1));
    }

    @Test
    void saveData_multipleWorkouts_writesProfileThenAllWorkoutLines()
            throws IOException, FitLoggerException {
        List<Workout> workouts = new ArrayList<>();
        workouts.add(new RunWorkout("Easy jog", LocalDate.of(2024, 3, 10), 3.0, 1.0));
        workouts.add(new RunWorkout("Long run", LocalDate.of(2024, 3, 12), 10.5, 1.0));

        UserProfile profile = new UserProfile();
        storage.saveData(workouts, profile);

        List<String> lines = Files.readAllLines(new File(FILE_PATH).toPath());
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).startsWith("name: "), "Line 1 should be the profile entry");
        assertEquals("R | Easy jog | 2024-03-10 | 3.0 | 1.0", lines.get(1));
        assertEquals("R | Long run | 2024-03-12 | 10.5 | 1.0", lines.get(2));
    }

    @Test
    void saveData_createsDataDirectory_ifNotExists() throws FitLoggerException {
        File dir = new File("data");
        if (dir.exists()) {
            new File(FILE_PATH).delete();
            dir.delete();
        }

        List<Workout> workouts = new ArrayList<>();
        workouts.add(new RunWorkout("Test run", LocalDate.now(), 1.0, 1.0));

        UserProfile profile = new UserProfile();
        storage.saveData(workouts, profile);

        assertTrue(dir.exists(), "data/ directory should be created automatically");
        assertTrue(new File(FILE_PATH).exists(), "fitlogger.txt should be created");
    }
}
