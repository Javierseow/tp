package fitlogger.workoutlist;

import fitlogger.workout.Workout;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a list of workouts.
 * Provides methods to manage the collection of workouts such as adding, deleting, and searching.
 */
public class WorkoutList {
    private static final Logger logger = Logger.getLogger(WorkoutList.class.getName());
    protected ArrayList<Workout> workouts;

    /**
     * Initializes an empty workout list.
     */
    public WorkoutList() {
        workouts = new ArrayList<>();
    }

    /**
     * Adds a workout to the list.
     *
     * @param workout The Workout object to be added.
     */
    public void addWorkout(Workout workout) {
        logger.log(Level.INFO, "Adding workout: " + workout.getDescription());
        workouts.add(workout);
    }

    /**
     * Removes a workout from the list based on its index.
     *
     * @param indexToRemove The index of the workout to be removed.
     */
    public void deleteWorkout(int indexToRemove) {
        if (indexToRemove >= 0 && indexToRemove < workouts.size()) {
            logger.log(Level.INFO, "Deleting workout at index: " + indexToRemove);
        } else {
            logger.log(Level.WARNING, "Attempted to delete invalid index: " + indexToRemove);
        }
        workouts.remove(indexToRemove);
    }

    /**
     * Checks if a specific workout contains a keyword in its description.
     *
     * @param index The index of the workout to check.
     * @param keyword The string to search for within the description.
     * @return true if the description contains the keyword, false otherwise.
     */
    public boolean findWorkout(int index, String keyword) {
        return workouts.get(index).getDescription().contains(keyword);
    }

    /**
     * Checks if the workout list is empty.
     *
     * @return true if there are no workouts in the list, false otherwise.
     */
    public boolean isEmpty() {
        return workouts.isEmpty();
    }

    /**
     * Returns the number of workouts currently in the list.
     *
     * @return The size of the workout list.
     */
    public int getSize() {
        return workouts.size();
    }

    /**
     * Retrieves a workout at a specific index.
     *
     * @param index The index of the workout to retrieve.
     * @return The Workout object at the specified index.
     */
    public Workout getWorkoutAtIndex(int index) {
        assert index >= 0 && index < workouts.size() : "Index out of bounds in WorkoutList";
        return workouts.get(index);
    }

    /**
     * Returns the entire list of workouts.
     *
     * @return An ArrayList containing all workouts.
     */
    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }
}
