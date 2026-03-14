package seedu.FitLogger;

import java.util.ArrayList;

public class WorkoutList {
    protected ArrayList<Workout> workouts;

    public WorkoutList() {
        workouts = new ArrayList<>();
    }

    public void addWorkout(Workout workout) {
        workouts.add(workout);
    }

    public void deleteWorkout(int workoutToRemove) {
        workouts.remove(workoutToRemove);
    }

    public void markDone(int index) {
        workouts.get(index).markAsDone();
    }

    public void markNotDone(int index) {
        workouts.get(index).markAsNotDone();
    }

    public boolean findWorkout(int index, String keyword) {
        return workouts.get(index).getDescription().contains(keyword);
    }

    public boolean isEmpty() {
        return workouts.isEmpty();
    }

    public int getSize() {
        return workouts.size();
    }

    public Workout getWorkout(int index) {
        return workouts.get(index);
    }

    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }
}
