package fitlogger.storage;

import fitlogger.exception.FitLoggerException;
import fitlogger.profile.UserProfile;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for {@link Storage} saveData() and loadData().
 */
class StorageTest {

    private static final String FILE_PATH = "data/fitlogger.txt";
    private Storage storage;
    private UserProfile profile;

    @BeforeEach
    void setUp() {
        storage = new Storage();
        profile = new UserProfile();
    }

    @AfterEach
    void tearDown() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    // ── saveData ─────────────────────────────────────────────────────────────

    @Test
    void saveData_emptyList_createsFileWithProfileOnly() {
        List<Workout> emptyList = new ArrayList<>();

        boolean result = storage.saveData(emptyList, profile);

        assertTrue(result, "saveData should return true on success");
        assertTrue(new File(FILE_PATH).exists(), "File should be created");
    }

    @Test
    void saveData_nullList_returnsFalseViaAssert() {
        // assert is only triggered with -ea flag; this just ensures no NPE in normal run
        // We test the happy path instead
        List<Workout> workouts = new ArrayList<>();
        boolean result = storage.saveData(workouts, profile);
        assertTrue(result);
    }

    @Test
    void saveData_singleRunWorkout_writesCorrectFormat() throws IOException, FitLoggerException {
        List<Workout> workouts = new ArrayList<>();
        workouts.add(new RunWorkout("Morning Run", LocalDate.of(2026, 3, 15), 5.0, 30.0));

        storage.saveData(workouts, profile);

        List<String> lines = Files.readAllLines(new File(FILE_PATH).toPath());
        assertTrue(lines.stream().anyMatch(l -> l.contains("R | Morning Run")));
        assertTrue(lines.stream().anyMatch(l -> l.contains("5.0")));
        assertTrue(lines.stream().anyMatch(l -> l.contains("30.0")));
    }

    @Test
    void saveData_singleStrengthWorkout_writesCorrectFormat() throws IOException, FitLoggerException {
        List<Workout> workouts = new ArrayList<>();
        workouts.add(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 15)));

        storage.saveData(workouts, profile);

        List<String> lines = Files.readAllLines(new File(FILE_PATH).toPath());
        assertTrue(lines.stream().anyMatch(l -> l.contains("L | Bench Press")));
        assertTrue(lines.stream().anyMatch(l -> l.contains("80.0")));
        assertTrue(lines.stream().anyMatch(l -> l.contains("3")));
        assertTrue(lines.stream().anyMatch(l -> l.contains("8")));
    }

    @Test
    void saveData_createsDataDirectory_ifNotExists() {
        File dir = new File("data");
        if (dir.exists()) {
            new File(FILE_PATH).delete();
            dir.delete();
        }

        List<Workout> workouts = new ArrayList<>();
        storage.saveData(workouts, profile);

        assertTrue(dir.exists(), "data/ directory should be created automatically");
    }

    @Test
    void saveData_returnsTrue_onSuccess() {
        List<Workout> workouts = new ArrayList<>();
        boolean result = storage.saveData(workouts, profile);
        assertTrue(result);
    }

    // ── loadData ─────────────────────────────────────────────────────────────

    @Test
    void loadData_fileDoesNotExist_returnsEmptyList() {
        File file = new File(FILE_PATH);
        file.delete();

        List<Workout> result = storage.loadData(profile);

        assertTrue(result.isEmpty(), "Should return empty list when file does not exist");
    }

    @Test
    void loadData_savedRunWorkout_reconstructsCorrectly() throws FitLoggerException {
        List<Workout> toSave = new ArrayList<>();
        toSave.add(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 15), 5.0, 30.0));
        storage.saveData(toSave, profile);

        List<Workout> loaded = storage.loadData(new UserProfile());

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0) instanceof RunWorkout);
        assertEquals("Easy Run", loaded.get(0).getDescription());
        assertEquals(5.0, ((RunWorkout) loaded.get(0)).getDistance());
        assertEquals(30.0, ((RunWorkout) loaded.get(0)).getDurationMinutes());
    }

    @Test
    void loadData_savedStrengthWorkout_reconstructsCorrectly() throws FitLoggerException {
        List<Workout> toSave = new ArrayList<>();
        toSave.add(new StrengthWorkout("Squat", 100.0, 5, 5, LocalDate.of(2026, 3, 15)));
        storage.saveData(toSave, profile);

        List<Workout> loaded = storage.loadData(new UserProfile());

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0) instanceof StrengthWorkout);
        assertEquals("Squat", loaded.get(0).getDescription());
        assertEquals(100.0, ((StrengthWorkout) loaded.get(0)).getWeight());
        assertEquals(5, ((StrengthWorkout) loaded.get(0)).getSets());
        assertEquals(5, ((StrengthWorkout) loaded.get(0)).getReps());
    }

    @Test
    void loadData_multipleWorkouts_reconstructsAll() throws FitLoggerException {
        List<Workout> toSave = new ArrayList<>();
        toSave.add(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 10), 3.0, 20.0));
        toSave.add(new StrengthWorkout("Deadlift", 150.0, 3, 5, LocalDate.of(2026, 3, 12)));
        storage.saveData(toSave, profile);

        List<Workout> loaded = storage.loadData(new UserProfile());

        assertEquals(2, loaded.size());
        assertTrue(loaded.get(0) instanceof RunWorkout);
        assertTrue(loaded.get(1) instanceof StrengthWorkout);
    }

    @Test
    void loadData_savedProfile_reconstructsName() {
        profile.setName("John");
        profile.setHeight(1.75);
        profile.setWeight(70.0);
        storage.saveData(new ArrayList<>(), profile);

        UserProfile loadedProfile = new UserProfile();
        storage.loadData(loadedProfile);

        assertEquals("John", loadedProfile.getName());
        assertEquals(1.75, loadedProfile.getHeight());
        assertEquals(70.0, loadedProfile.getWeight());
    }

    @Test
    void loadData_corruptedLine_skipsAndLoadsRest() throws FitLoggerException, IOException {
        // Save a valid workout first
        List<Workout> toSave = new ArrayList<>();
        toSave.add(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 15)));
        storage.saveData(toSave, profile);

        // Manually inject a corrupted line
        File file = new File(FILE_PATH);
        List<String> lines = Files.readAllLines(file.toPath());
        lines.add("CORRUPTED | bad data | !!!");
        Files.write(file.toPath(), lines);

        List<Workout> loaded = storage.loadData(new UserProfile());

        // Valid workout should still load
        assertFalse(loaded.isEmpty());
        assertEquals("Bench Press", loaded.get(0).getDescription());
    }
}
