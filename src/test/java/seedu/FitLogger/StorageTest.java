package seedu.FitLogger;

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

class StorageTest {

    private static final String FILE_PATH = "data/fitlogger.txt";
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new Storage();
    }

    @AfterEach
    void tearDown() {
        // Clean up the test file after each test
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void saveData_emptyList_createsEmptyFile() throws IOException {
        List<Workout> emptyList = new ArrayList<>();

        storage.saveData(emptyList);

        File file = new File(FILE_PATH);
        assertTrue(file.exists(), "File should be created even for empty workout list");
        assertEquals(0, file.length(), "File should be empty when no workouts saved");
    }

    @Test
    void saveData_singleRunningWorkout_writesCorrectFormat() throws IOException {
        List<Workout> workouts = new ArrayList<>();
        workouts.add(new Running("Morning run", LocalDate.of(2024, 3, 15), 5.0));

        storage.saveData(workouts);

        List<String> lines = Files.readAllLines(new File(FILE_PATH).toPath());
        assertEquals(1, lines.size());
        assertEquals("R | 0 | Morning run | 2024-03-15 | 5.0", lines.get(0));
    }

    @Test
    void saveData_multipleWorkouts_writesAllLines() throws IOException {
        List<Workout> workouts = new ArrayList<>();
        workouts.add(new Running("Easy jog", LocalDate.of(2024, 3, 10), 3.0));
        workouts.add(new Running("Long run", LocalDate.of(2024, 3, 12), 10.5));

        storage.saveData(workouts);

        List<String> lines = Files.readAllLines(new File(FILE_PATH).toPath());
        assertEquals(2, lines.size());
        assertEquals("R | 0 | Easy jog | 2024-03-10 | 3.0", lines.get(0));
        assertEquals("R | 0 | Long run | 2024-03-12 | 10.5", lines.get(1));
    }

    @Test
    void saveData_markedAsDoneWorkout_savesDoneStatus() throws IOException {
        List<Workout> workouts = new ArrayList<>();
        Running run = new Running("Tempo run", LocalDate.of(2024, 3, 20), 8.0);
        run.markAsDone();
        workouts.add(run);

        storage.saveData(workouts);

        List<String> lines = Files.readAllLines(new File(FILE_PATH).toPath());
        assertEquals(1, lines.size());
        assertEquals("R | 1 | Tempo run | 2024-03-20 | 8.0", lines.get(0));
    }

    @Test
    void saveData_createsDataDirectory_ifNotExists() {
        File dir = new File("data");
        // Remove the directory first to simulate a fresh environment
        if (dir.exists()) {
            new File(FILE_PATH).delete();
            dir.delete();
        }

        List<Workout> workouts = new ArrayList<>();
        workouts.add(new Running("Test run", LocalDate.now(), 1.0));

        storage.saveData(workouts);

        assertTrue(dir.exists(), "data/ directory should be created automatically");
        assertTrue(new File(FILE_PATH).exists(), "fitlogger.txt should be created");
    }
}
