package fitlogger;

import fitlogger.command.ViewCalendarCommand;
import fitlogger.exception.FitLoggerException;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workoutlist.WorkoutList;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewCalendarCommandTest {
    @Test
    void execute_singleMonthWithWorkouts_showsCalendarWithActiveDays() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        YearMonth targetMonth = YearMonth.of(2026, 4);
        workouts.addWorkout(new StrengthWorkout("Squat", 100.0, 3, 8, LocalDate.of(2026, 4, 1)));
        workouts.addWorkout(new RunWorkout("Run", LocalDate.of(2026, 4, 15), 5.0, 30.0));
        workouts.addWorkout(new StrengthWorkout("Bench", 80.0, 3, 8, LocalDate.of(2026, 4, 30)));

        TestUi ui = new TestUi();
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        // Should show calendar for April 2026 with days 1, 15, 30 marked as active
        assertEquals(targetMonth, ui.calendarMonth);
        assertTrue(ui.activeDays.contains(1));
        assertTrue(ui.activeDays.contains(15));
        assertTrue(ui.activeDays.contains(30));
        assertEquals(3, ui.activeDays.size());
    }

    @Test
    void execute_monthWithNoWorkouts_showsCalendarWithNoDaysMarked() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        // Add workouts in different months
        workouts.addWorkout(new RunWorkout("Run", LocalDate.of(2026, 3, 15), 5.0, 30.0));
        workouts.addWorkout(new StrengthWorkout("Lift", 100.0, 3, 8, LocalDate.of(2026, 5, 20)));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 4);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        // Should show calendar for April 2026 with no days marked as active
        assertEquals(targetMonth, ui.calendarMonth);
        assertTrue(ui.activeDays.isEmpty());
    }

    @Test
    void execute_multipleWorkoutsSameDay_markedOncePerDay() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        LocalDate sameDay = LocalDate.of(2026, 4, 10);
        workouts.addWorkout(new StrengthWorkout("Squat", 100.0, 3, 8, sameDay));
        workouts.addWorkout(new StrengthWorkout("Bench", 80.0, 3, 8, sameDay));
        workouts.addWorkout(new RunWorkout("Run", sameDay, 5.0, 30.0));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 4);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        // Should show only day 10 once, not three times
        assertTrue(ui.activeDays.contains(10));
        assertEquals(1, ui.activeDays.size());
    }

    @Test
    void execute_differentMonths_onlyTargetMonthDaysMarked() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        workouts.addWorkout(new RunWorkout("Run1", LocalDate.of(2026, 3, 15), 5.0, 30.0));
        workouts.addWorkout(new StrengthWorkout("Lift1", 100.0, 3, 8, LocalDate.of(2026, 4, 15)));
        workouts.addWorkout(new RunWorkout("Run2", LocalDate.of(2026, 5, 15), 5.0, 30.0));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 4);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        // Should only show day 15 from April
        assertTrue(ui.activeDays.contains(15));
        assertEquals(1, ui.activeDays.size());
    }

    @Test
    void execute_januaryMonth_showsCorrectCalendar() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        workouts.addWorkout(
                new StrengthWorkout("New Year Lift", 100.0, 3, 8, LocalDate.of(2026, 1, 1)));
        workouts.addWorkout(new RunWorkout("Run", LocalDate.of(2026, 1, 31), 5.0, 30.0));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 1);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        assertEquals(targetMonth, ui.calendarMonth);
        assertTrue(ui.activeDays.contains(1));
        assertTrue(ui.activeDays.contains(31));
        assertEquals(2, ui.activeDays.size());
    }

    @Test
    void execute_decemberMonth_showsCorrectCalendar() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        workouts.addWorkout(
                new StrengthWorkout("Year-End Lift", 100.0, 3, 8, LocalDate.of(2026, 12, 1)));
        workouts.addWorkout(new RunWorkout("Run", LocalDate.of(2026, 12, 31), 5.0, 30.0));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 12);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        assertEquals(targetMonth, ui.calendarMonth);
        assertTrue(ui.activeDays.contains(1));
        assertTrue(ui.activeDays.contains(31));
        assertEquals(2, ui.activeDays.size());
    }

    @Test
    void execute_februaryLeapYear_showsCorrectDays() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        // 2024 is a leap year with 29 days in February
        workouts.addWorkout(new StrengthWorkout("Lift", 100.0, 3, 8, LocalDate.of(2024, 2, 1)));
        workouts.addWorkout(new RunWorkout("Run", LocalDate.of(2024, 2, 29), 5.0, 30.0));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2024, 2);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        assertEquals(targetMonth, ui.calendarMonth);
        assertTrue(ui.activeDays.contains(1));
        assertTrue(ui.activeDays.contains(29));
        assertEquals(2, ui.activeDays.size());
    }

    @Test
    void execute_februaryNonLeapYear_showsCorrectDays() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        // 2026 is not a leap year with 28 days in February
        workouts.addWorkout(new StrengthWorkout("Lift", 100.0, 3, 8, LocalDate.of(2026, 2, 1)));
        workouts.addWorkout(new RunWorkout("Run", LocalDate.of(2026, 2, 28), 5.0, 30.0));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 2);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        assertEquals(targetMonth, ui.calendarMonth);
        assertTrue(ui.activeDays.contains(1));
        assertTrue(ui.activeDays.contains(28));
        assertEquals(2, ui.activeDays.size());
    }

    @Test
    void execute_monthWith30Days_april_showsCorrectMaxDay() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        // April has 30 days
        workouts.addWorkout(new StrengthWorkout("Lift", 100.0, 3, 8, LocalDate.of(2026, 4, 30)));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 4);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        assertEquals(targetMonth, ui.calendarMonth);
        assertTrue(ui.activeDays.contains(30));
        assertEquals(1, ui.activeDays.size());
    }

    @Test
    void execute_monthWith31Days_january_showsCorrectMaxDay() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        // January has 31 days
        workouts.addWorkout(new StrengthWorkout("Lift", 100.0, 3, 8, LocalDate.of(2026, 1, 31)));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 1);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        assertEquals(targetMonth, ui.calendarMonth);
        assertTrue(ui.activeDays.contains(31));
        assertEquals(1, ui.activeDays.size());
    }

    @Test
    void execute_emptyWorkoutList_showsCalendarWithNoDaysMarked() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 4);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        assertEquals(targetMonth, ui.calendarMonth);
        assertTrue(ui.activeDays.isEmpty());
    }

    @Test
    void execute_differentYearsSameMonth_onlyTargetYearMarked() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        workouts.addWorkout(
                new StrengthWorkout("Lift2025", 100.0, 3, 8, LocalDate.of(2025, 4, 15)));
        workouts.addWorkout(
                new StrengthWorkout("Lift2026", 100.0, 3, 8, LocalDate.of(2026, 4, 15)));
        workouts.addWorkout(
                new StrengthWorkout("Lift2027", 100.0, 3, 8, LocalDate.of(2027, 4, 15)));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 4);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        // Should only show day 15 from April 2026
        assertTrue(ui.activeDays.contains(15));
        assertEquals(1, ui.activeDays.size());
    }

    @Test
    void execute_allWorkoutTypesInMonth_showsAllActiveDays() throws FitLoggerException {
        WorkoutList workouts = new WorkoutList();
        Storage storage = new Storage();
        UserProfile profile = new UserProfile();

        // Add various workouts
        workouts.addWorkout(new StrengthWorkout("Squat", 100.0, 3, 8, LocalDate.of(2026, 4, 5)));
        workouts.addWorkout(new StrengthWorkout("Bench", 80.0, 3, 8, LocalDate.of(2026, 4, 5)));
        workouts.addWorkout(new RunWorkout("Run1", LocalDate.of(2026, 4, 10), 5.0, 30.0));
        workouts.addWorkout(new RunWorkout("Run2", LocalDate.of(2026, 4, 10), 3.0, 20.0));
        workouts.addWorkout(
                new StrengthWorkout("Deadlift", 120.0, 1, 5, LocalDate.of(2026, 4, 20)));

        TestUi ui = new TestUi();
        YearMonth targetMonth = YearMonth.of(2026, 4);
        ViewCalendarCommand command = new ViewCalendarCommand(targetMonth);
        command.execute(storage, workouts, ui, profile);

        // Should show days 5, 10, and 20
        assertTrue(ui.activeDays.contains(5));
        assertTrue(ui.activeDays.contains(10));
        assertTrue(ui.activeDays.contains(20));
        assertEquals(3, ui.activeDays.size());
    }

    private static class TestUi extends Ui {
        public YearMonth calendarMonth;
        public Set<Integer> activeDays;

        @Override
        public void showCalendar(YearMonth month, Set<Integer> days) {
            this.calendarMonth = month;
            this.activeDays = days;
        }

        @Override
        public void showMessage(String message) {
            // No-op for testing
        }

        @Override
        public void showLine() {
            // No-op for testing
        }
    }
}
