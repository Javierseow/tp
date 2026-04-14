package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

/**
 * Command to update specific fields of the user's profile. Supports partial updates where only
 * provided non-sentinel values are changed.
 */
public class UpdateProfileCommand extends ProfileCommand {
    private String newName;
    private double newHeight;
    private double newWeight;

    /**
     * Constructs an UpdateProfileCommand with the specified profile details.
     *
     * @param newName The new name for the user (null if not being updated).
     * @param newHeight The new height for the user (-1 if not being updated).
     * @param newWeight The new weight for the user (-1 if not being updated).
     */
    public UpdateProfileCommand(String newName, double newHeight, double newWeight) {
        this.newName = newName;
        assert newHeight == -1 || newHeight >= 0 : "Height is invalid";
        this.newHeight = newHeight;
        assert newWeight == -1 || newWeight >= 0 : "Weight is invalid";
        this.newWeight = newWeight;
    }

    /**
     * Updates the user profile with the new values provided and notifies the user.
     *
     * @param storage The storage handler.
     * @param workouts The list of workouts.
     * @param ui The user interface to show update confirmations.
     * @param profile The user profile to be updated.
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        ui.showLine();
        if (newName != null) {
            profile.setName(newName);
            ui.showMessage("Name has been updated to " + newName);
        }
        if (newHeight != -1) {
            profile.setHeight(newHeight);
            ui.showMessage("Height has been updated to " + String.format("%.2f", newHeight) + "m");
        }
        if (newWeight != -1) {
            profile.setWeight(newWeight);
            ui.showMessage("Weight has been updated to " + String.format("%.2f", newWeight) + "kg");
        }
        ui.showLine();
    }
}
