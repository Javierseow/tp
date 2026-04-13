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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Edits a single field of an existing workout identified by one-based index.
 *
 * <p>Supported fields are name for all workouts, weight/sets/reps
 * for strength workouts, and distance/duration for run workouts.
 */
public class EditCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(EditCommand.class.getName());

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
        assert storage != null : "Storage must not be null";
        assert workouts != null : "WorkoutList must not be null";
        assert ui != null : "Ui must not be null";
        assert profile != null : "UserProfile must not be null";
        assert fieldName != null : "Field name must not be null";
        assert newValue != null : "New value must not be null";

        LOGGER.log(Level.INFO, "Edit requested for index {0}, field {1}",
                new Object[]{oneBasedIndex, fieldName});

        int zeroBasedIndex = oneBasedIndex - 1;
        if (zeroBasedIndex < 0 || zeroBasedIndex >= workouts.getSize()) {
            LOGGER.log(Level.WARNING, "Edit rejected: invalid one-based index {0}",
                    oneBasedIndex);
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
                LOGGER.log(Level.WARNING, "Edit rejected for workout name field", exception);
                ui.showMessage(exception.getMessage());
                return;
            }
            break;

        case "weight":
            if (!(workout instanceof StrengthWorkout)) {
                LOGGER.log(Level.WARNING, "Edit rejected: field {0} used on non-lift workout",
                        normalizedField);
                ui.showMessage("Field 'weight' is only valid for lift workouts.");
                return;
            }
            isUpdated = editWeight((StrengthWorkout) workout, ui);
            break;

        case "sets":
            if (!(workout instanceof StrengthWorkout)) {
                LOGGER.log(Level.WARNING, "Edit rejected: field {0} used on non-lift workout",
                        normalizedField);
                ui.showMessage("Field 'sets' is only valid for lift workouts.");
                return;
            }
            isUpdated = editSets((StrengthWorkout) workout, ui);
            break;

        case "reps":
            if (!(workout instanceof StrengthWorkout)) {
                LOGGER.log(Level.WARNING, "Edit rejected: field {0} used on non-lift workout",
                        normalizedField);
                ui.showMessage("Field 'reps' is only valid for lift workouts.");
                return;
            }
            isUpdated = editReps((StrengthWorkout) workout, ui);
            break;

        case "distance":
            if (!(workout instanceof RunWorkout)) {
                LOGGER.log(Level.WARNING, "Edit rejected: field {0} used on non-run workout",
                        normalizedField);
                ui.showMessage("Field 'distance' is only valid for run workouts.");
                return;
            }
            isUpdated = editDistance((RunWorkout) workout, ui);
            break;

        case "duration":
            if (!(workout instanceof RunWorkout)) {
                LOGGER.log(Level.WARNING, "Edit rejected: field {0} used on non-run workout",
                        normalizedField);
                ui.showMessage("Field 'duration' is only valid for run workouts.");
                return;
            }
            isUpdated = editDuration((RunWorkout) workout, ui);
            break;

        default:
            LOGGER.log(Level.WARNING, "Edit rejected: unknown field {0}", fieldName);
            ui.showMessage("Unknown editable field: " + fieldName);
            return;
        }

        if (isUpdated) {
            boolean isSaved = storage.saveData(workouts.getWorkouts(), profile);
            if (!isSaved) {
                LOGGER.log(Level.WARNING, "Edit applied in memory but save failed for index {0}",
                        oneBasedIndex);
                ui.showError("Failed to save workouts to disk. Changes remain only in memory.");
                return;
            }
            LOGGER.log(Level.INFO, "Edit succeeded for one-based index {0}", oneBasedIndex);
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
            String valueText = newValue.trim();
            if (!Parser.isPlainDecimalNumber(valueText)) {
                throw new NumberFormatException();
            }
            double value = Double.parseDouble(valueText);
            if (!Double.isFinite(value)) {
                throw new NumberFormatException();
            }
            workout.setWeight(value);
            return true;
        } catch (NumberFormatException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected: invalid weight value {0}", newValue);
            ui.showMessage("Invalid weight value: " + newValue);
        } catch (FitLoggerException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected while setting weight", exception);
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
            int value = parseIntegerFieldWithinLimit("Sets");
            workout.setSets(value);
            return true;
        } catch (NumberFormatException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected: invalid sets value {0}", newValue);
            ui.showMessage("Invalid sets value: " + newValue);
        } catch (FitLoggerException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected while setting sets", exception);
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
            int value = parseIntegerFieldWithinLimit("Reps");
            workout.setReps(value);
            return true;
        } catch (NumberFormatException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected: invalid reps value {0}", newValue);
            ui.showMessage("Invalid reps value: " + newValue);
        } catch (FitLoggerException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected while setting reps", exception);
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
            String valueText = newValue.trim();
            if (!Parser.isPlainDecimalNumber(valueText)) {
                throw new NumberFormatException();
            }
            double value = Double.parseDouble(valueText);
            if (!Double.isFinite(value)) {
                throw new NumberFormatException();
            }
            workout.setDistance(value);
            return true;
        } catch (NumberFormatException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected: invalid distance value {0}", newValue);
            ui.showMessage("Invalid distance value: " + newValue);
        } catch (FitLoggerException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected while setting distance", exception);
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
            String valueText = newValue.trim();
            if (!Parser.isPlainDecimalNumber(valueText)) {
                throw new NumberFormatException();
            }
            double value = Double.parseDouble(valueText);
            if (!Double.isFinite(value)) {
                throw new NumberFormatException();
            }
            workout.setDurationMinutes(value);
            return true;
        } catch (NumberFormatException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected: invalid duration value {0}", newValue);
            ui.showMessage("Invalid duration value: " + newValue);
        } catch (FitLoggerException exception) {
            LOGGER.log(Level.WARNING, "Edit rejected while setting duration", exception);
            ui.showMessage(exception.getMessage());
        }
        return false;
    }

    /**
     * Parses an integer edit value and rejects values above the application limit.
     */
    private int parseIntegerFieldWithinLimit(String fieldName) throws FitLoggerException {
        try {
            int value = Integer.parseInt(newValue.trim());
            if (value > Parser.MAX_INTEGER_INPUT) {
                throw new FitLoggerException(fieldName + " must not exceed "
                        + Parser.MAX_INTEGER_INPUT + ".");
            }
            return value;
        } catch (NumberFormatException exception) {
            if (newValue.trim().matches("\\d+")) {
                throw new FitLoggerException(fieldName + " must not exceed "
                        + Parser.MAX_INTEGER_INPUT + ".");
            }
            throw exception;
        }
    }
}
