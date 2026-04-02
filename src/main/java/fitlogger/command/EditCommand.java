package fitlogger.command;

import fitlogger.exception.FitLoggerException;
import fitlogger.parser.Parser;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

/**
 * Edits a single field of an existing workout identified by one-based index.
 *
 * <p>Supported fields are name/description for all workouts, weight/sets/reps
 * for strength workouts, and distance/duration for run workouts.
 */
public class EditCommand extends Command {
    private final int oneBasedIndex;
    private final String fieldName;
    private final String newValue;

    /**
     * Creates an edit command targeting one workout field.
     *
     * @param oneBasedIndex User-facing one-based index of workout.
     * @param fieldName     Field name to update.
     * @param newValue      New field value as user input.
     */
    public EditCommand(int oneBasedIndex, String fieldName, String newValue) {
        this.oneBasedIndex = oneBasedIndex;
        this.fieldName = fieldName;
        this.newValue = newValue;
    }

    /**
     * Executes the edit operation and reports success or validation errors.
     *
      * @param storage Storage component used to persist workouts and profile.
      * @param workouts Workout list containing workouts to edit.
      * @param ui UI used to display command result messages.
      * @param profile User profile saved together with workouts.
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        int zeroBasedIndex = oneBasedIndex - 1;
        if (zeroBasedIndex < 0 || zeroBasedIndex >= workouts.getSize()) {
            ui.showMessage("Invalid workout index: " + oneBasedIndex);
            return;
        }

        Workout workout = workouts.getWorkoutAtIndex(zeroBasedIndex);
        String normalizedField = fieldName.trim().toLowerCase();
        boolean isUpdated;

        switch (normalizedField) {
        case "name":
        case "description":
            try {
                Parser.validateNoStorageDelimiters(newValue.trim(), "Workout name");
                workout.setDescription(newValue.trim());
                isUpdated = true;
            } catch (FitLoggerException exception) {
                ui.showMessage(exception.getMessage());
                return;
            }
            break;

        case "weight":
            if (!(workout instanceof StrengthWorkout)) {
                ui.showMessage("Field 'weight' is only valid for lift workouts.");
                return;
            }
            isUpdated = editWeight((StrengthWorkout) workout, ui);
            break;

        case "sets":
            if (!(workout instanceof StrengthWorkout)) {
                ui.showMessage("Field 'sets' is only valid for lift workouts.");
                return;
            }
            isUpdated = editSets((StrengthWorkout) workout, ui);
            break;

        case "reps":
            if (!(workout instanceof StrengthWorkout)) {
                ui.showMessage("Field 'reps' is only valid for lift workouts.");
                return;
            }
            isUpdated = editReps((StrengthWorkout) workout, ui);
            break;

        case "distance":
            if (!(workout instanceof RunWorkout)) {
                ui.showMessage("Field 'distance' is only valid for run workouts.");
                return;
            }
            isUpdated = editDistance((RunWorkout) workout, ui);
            break;

        case "duration":
            if (!(workout instanceof RunWorkout)) {
                ui.showMessage("Field 'duration' is only valid for run workouts.");
                return;
            }
            isUpdated = editDuration((RunWorkout) workout, ui);
            break;

        default:
            ui.showMessage("Unknown editable field: " + fieldName);
            return;
        }

        if (isUpdated) {
            boolean isSaved = storage.saveData(workouts.getWorkouts(), profile);
            if (!isSaved) {
                ui.showError("Failed to save workouts to disk. Changes remain only in memory.");
                return;
            }
            ui.showMessage("Updated workout " + oneBasedIndex + ": " + workout);
        }
    }

    /**
     * Parses and applies a new weight to a strength workout.
     *
     * @param workout Strength workout being edited.
     * @param ui      UI used for validation error messages.
     * @return {@code true} if update succeeds; {@code false} otherwise.
     */
    private boolean editWeight(StrengthWorkout workout, Ui ui) {
        try {
            double value = Double.parseDouble(newValue.trim());
            workout.setWeight(value);
            return true;
        } catch (NumberFormatException exception) {
            ui.showMessage("Invalid weight value: " + newValue);
        } catch (FitLoggerException exception) {
            ui.showMessage(exception.getMessage());
        }
        return false;
    }

    /**
     * Parses and applies a new sets value to a strength workout.
     *
     * @param workout Strength workout being edited.
     * @param ui      UI used for validation error messages.
     * @return {@code true} if update succeeds; {@code false} otherwise.
     */
    private boolean editSets(StrengthWorkout workout, Ui ui) {
        try {
            int value = Integer.parseInt(newValue.trim());
            workout.setSets(value);
            return true;
        } catch (NumberFormatException exception) {
            ui.showMessage("Invalid sets value: " + newValue);
        } catch (FitLoggerException exception) {
            ui.showMessage(exception.getMessage());
        }
        return false;
    }

    /**
     * Parses and applies a new reps value to a strength workout.
     *
     * @param workout Strength workout being edited.
     * @param ui      UI used for validation error messages.
     * @return {@code true} if update succeeds; {@code false} otherwise.
     */
    private boolean editReps(StrengthWorkout workout, Ui ui) {
        try {
            int value = Integer.parseInt(newValue.trim());
            workout.setReps(value);
            return true;
        } catch (NumberFormatException exception) {
            ui.showMessage("Invalid reps value: " + newValue);
        } catch (FitLoggerException exception) {
            ui.showMessage(exception.getMessage());
        }
        return false;
    }

    /**
     * Parses and applies a new distance value to a run workout.
     *
     * @param workout Run workout being edited.
     * @param ui      UI used for validation error messages.
     * @return {@code true} if update succeeds; {@code false} otherwise.
     */
    private boolean editDistance(RunWorkout workout, Ui ui) {
        try {
            double value = Double.parseDouble(newValue.trim());
            workout.setDistance(value);
            return true;
        } catch (NumberFormatException exception) {
            ui.showMessage("Invalid distance value: " + newValue);
        } catch (FitLoggerException exception) {
            ui.showMessage(exception.getMessage());
        }
        return false;
    }

    /**
     * Parses and applies a new duration value to a run workout.
     *
     * @param workout Run workout being edited.
     * @param ui      UI used for validation error messages.
     * @return {@code true} if update succeeds; {@code false} otherwise.
     */
    private boolean editDuration(RunWorkout workout, Ui ui) {
        try {
            double value = Double.parseDouble(newValue.trim());
            workout.setDurationMinutes(value);
            return true;
        } catch (NumberFormatException exception) {
            ui.showMessage("Invalid duration value: " + newValue);
        } catch (FitLoggerException exception) {
            ui.showMessage(exception.getMessage());
        }
        return false;
    }
}
