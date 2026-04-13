package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command to reset all user profile fields to their default (unset) state.
 */
public class ClearProfileCommand extends ProfileCommand {
    private static final Logger logger = Logger.getLogger(ClearProfileCommand.class.getName());

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        logger.log(Level.INFO, "Clearing user profile data.");

        profile.setName(null);
        profile.setHeight(-1.0);
        profile.setWeight(-1.0);

        ui.showMessage("User profile has been cleared.");
    }
}