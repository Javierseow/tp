package fitlogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fitlogger.command.FilterTypeCommand;
import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.exception.FitLoggerException;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FilterTypeCommandTest {
    private WorkoutList workouts;
    private Storage storage;
    private TestUi ui;
    private UserProfile profile;
    private ExerciseDictionary dictionary;

    @BeforeEach
    void setUp() throws FitLoggerException {
        workouts = new WorkoutList();
        storage = new Storage();
        ui = new TestUi();
        profile = new UserProfile();
        dictionary = new ExerciseDictionary();

        // Add test workouts
        // Squat: QUADS, GLUTES, HAMSTRING
        workouts.addWorkout(new StrengthWorkout("Squat", 100.0, 3, 12, LocalDate.of(2026, 4, 10)));
        workouts.addWorkout(new StrengthWorkout("Squat", 100.0, 3, 12, LocalDate.of(2026, 4, 10)));

        // Bench Press: PECS, TRICEPS, DELTS
        workouts.addWorkout(
                new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 4, 10)));

        // Deadlift: HAMSTRING, GLUTES, LOWER_BACK, TRAPS
        workouts.addWorkout(
                new StrengthWorkout("Deadlift", 120.0, 1, 5, LocalDate.of(2026, 4, 10)));
    }

    @Test
    void filterSingleMuscle_quads_returnsSquatOnly() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("quads", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Filter should return the 2 Squats (which have QUADS)
        assertEquals(2, ui.filteredWorkouts.size());
        assertTrue(ui.filteredWorkouts.get(0).getDescription().equalsIgnoreCase("Squat"));
    }

    @Test
    void filterMultipleMusclesSpaceSeparated_quadsGlutes_returnsSquatAndDeadlift()
            throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("quads glutes", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should return Squats (2) and Deadlifts (1) since both have QUADS or GLUTES
        assertEquals(3, ui.filteredWorkouts.size());
        assertTrue(ui.filteredWorkouts.stream()
                .allMatch(w -> w.getDescription().equalsIgnoreCase("Squat")
                        || w.getDescription().equalsIgnoreCase("Deadlift")));
    }

    @Test
    void filterMultipleMuscles_quadsAndGlutesWithComma_returnsSquatAndDeadlift()
            throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("quads,glutes", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should return Squats (2) and Deadlifts (1) since both have QUADS or GLUTES
        assertEquals(3, ui.filteredWorkouts.size());
    }

    @Test
    void filterMultipleMuscles_tricepsDelts_returnsBenchPressOnly() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("triceps delts", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should return Bench Press (has both TRICEPS and DELTS)
        assertEquals(1, ui.filteredWorkouts.size());
        assertTrue(ui.filteredWorkouts.get(0).getDescription().equalsIgnoreCase("Bench Press"));
    }

    @Test
    void filterMultipleMuscles_quadsHamstring_returnsSquatAndDeadlift() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("quads hamstring", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should return Squats (2 - have QUADS) and Deadlifts (1 - have HAMSTRING)
        assertEquals(3, ui.filteredWorkouts.size());
    }

    @Test
    void filterBlankInput_showsUsageMessage() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("", dictionary);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.messages.get(0).toLowerCase().contains("please specify"));
    }

    @Test
    void filterNullInput_showsUsageMessage() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand(null, dictionary);
        command.execute(storage, workouts, ui, profile);

        assertTrue(ui.messages.get(0).toLowerCase().contains("please specify"));
    }

    @Test
    void filterCaseInsensitive_uppercaseInput_filtersCorrectly() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("QUADS", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should work case-insensitive
        assertEquals(2, ui.filteredWorkouts.size());
    }

    @Test
    void filterCaseInsensitive_mixedCaseInput_filtersCorrectly() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("QuAdS GlUtEs", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should work case-insensitive and stacked
        assertEquals(3, ui.filteredWorkouts.size());
    }

    @Test
    void filterWithExtraSpaces_multipleSpacesBetweenMuscles_parsesCorrectly()
            throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("quads    glutes", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should handle multiple spaces and parse as separate muscle groups
        assertEquals(3, ui.filteredWorkouts.size());
    }

    @Test
    void filterWithCommaAndSpace_mixedSeparators_parsesCorrectly() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("quads, glutes", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should handle both comma and space as separators
        assertEquals(3, ui.filteredWorkouts.size());
    }

    @Test
    void filterNonExistentMuscle_noMatches_returnsEmptyList() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("nonexistentmuscle", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should show filter message but no workouts found
        assertEquals(0, ui.filteredWorkouts.size());
        assertTrue(
                ui.messages.stream().anyMatch(m -> m.toLowerCase().contains("no workouts found")));
    }

    @Test
    void filterMultiWordMuscleGroup_lowerBackWithUnderscore_matchesCorrectly()
            throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("lower_back", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should match Deadlift which has LOWER_BACK
        assertEquals(1, ui.filteredWorkouts.size());
        assertTrue(ui.filteredWorkouts.get(0).getDescription().equalsIgnoreCase("Deadlift"));
    }

    @Test
    void filterMultiWordMuscleGroup_upperBackWithUnderscore_noMatches() throws FitLoggerException {
        // No workouts with upper_back in test setup
        FilterTypeCommand command = new FilterTypeCommand("upper_back", dictionary);
        command.execute(storage, workouts, ui, profile);

        assertEquals(0, ui.filteredWorkouts.size());
    }

    @Test
    void filterWithLeadingTrailingSpaces_trimmedCorrectly() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("  quads  glutes  ", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should trim and parse as separate groups
        assertEquals(3, ui.filteredWorkouts.size());
    }

    @Test
    void filterWithCommaOnlyNoSpaces_parsesCorrectly() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("quads,glutes,hamstring", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should return Squats (2) and Deadlifts (1)
        assertEquals(3, ui.filteredWorkouts.size());
    }

    @Test
    void filterWithMixedUnderscoreAndSpace_multiWordAndSingleWord() throws FitLoggerException {
        FilterTypeCommand command = new FilterTypeCommand("lower_back quads", dictionary);
        command.execute(storage, workouts, ui, profile);

        // Should match Deadlift (lower_back) and both Squats (quads)
        assertEquals(3, ui.filteredWorkouts.size());
    }

    private static class TestUi extends Ui {
        public List<String> messages = new ArrayList<>();
        public List<Workout> filteredWorkouts = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void showWorkoutList(List<Workout> workouts) {
            filteredWorkouts.addAll(workouts);
            super.showWorkoutList(workouts);
        }

        @Override
        public void showError(String message) {
            messages.add(message);
        }

        @Override
        public void showLine() {
            // No-op for testing
        }
    }
}
