package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workoutlist.WorkoutList;

import java.time.LocalDate;

public class ViewShoeMileageCommand extends Command {
    private final int daysLimit;

    public ViewShoeMileageCommand() {
        this.daysLimit = -1;
    }

    public ViewShoeMileageCommand(int daysLimit) {
        assert daysLimit > 0 : "Days limit must be positive";
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

                if (daysLimit == -1 || !run.getDate().isBefore(cutoffDate)) {
                    totalMileage += run.getDistance();
                    numberOfRuns++;
                }
            }
        }

        if (daysLimit == -1) {
            ui.showMessage("Total shoe mileage (all time): " + String.format("%.2f", totalMileage) + "km"
                    + " across " + numberOfRuns + " runs.");
        } else {
            ui.showMessage("Total shoe mileage (past " + daysLimit + " days): "
                    + String.format("%.2f", totalMileage) + "km" + " across " + numberOfRuns + " runs.");
        }
    }
}
