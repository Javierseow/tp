package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workoutlist.WorkoutList;

import java.time.LocalDate;

/**
 * Command to calculate and display total running mileage.
 * Can filter by a specific number of recent days.
 */
public class ViewShoeMileageCommand extends Command {
    private final int daysLimit;

    public ViewShoeMileageCommand() {
        this.daysLimit = -1;
    }

    public ViewShoeMileageCommand(int daysLimit) {
        assert daysLimit >= 0 : "Days limit must be non-negative";
        this.daysLimit = daysLimit;
    }

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        double totalMileage = 0;
        int numberOfRuns = 0;

        LocalDate cutoffDate = LocalDate.now().minusDays(daysLimit);

        for (int i = 0; i < workouts.getSize(); i++) {
            if (workouts.getWorkoutAtIndex(i) instanceof RunWorkout) {
                RunWorkout run = (RunWorkout) workouts.getWorkoutAtIndex(i);

                // !isBefore(cutoffDate) includes the cutoff date itself
                if (daysLimit == -1 || !run.getDate().isBefore(cutoffDate)) {
                    totalMileage += run.getDistance();
                    numberOfRuns++;
                }
            }
        }

        if (daysLimit == -1) {
            ui.showMessage("Total shoe mileage (all time): " + String.format("%.2f", totalMileage) + "km"
                    + " across " + numberOfRuns + " run(s).");
        } else if (daysLimit == 0) {
            ui.showMessage("Total shoe mileage (today): " + String.format("%.2f", totalMileage) + "km"
                    + " across " + numberOfRuns + " run(s).");
        } else {
            ui.showMessage("Total shoe mileage (past " + daysLimit + " day(s)): "
                    + String.format("%.2f", totalMileage) + "km" + " across " + numberOfRuns + " run(s).");
        }
    }
}