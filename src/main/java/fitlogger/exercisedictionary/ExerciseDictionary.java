package fitlogger.exercisedictionary;

import fitlogger.musclegroup.MuscleGroup;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExerciseDictionary {
    private final TreeMap<Integer, String> liftDictionary;
    private final TreeMap<Integer, String> runDictionary;
    private final Map<Integer, EnumSet<MuscleGroup>> liftMuscleGroups;
    private static final Logger logger = Logger.getLogger(ExerciseDictionary.class.getName());

    public ExerciseDictionary() {
        this.liftDictionary = new TreeMap<>();
        this.runDictionary = new TreeMap<>();
        this.liftMuscleGroups = new HashMap<>();
        loadDefaultExercises();
    }

    private void loadDefaultExercises() {
        // Default Lifts
        liftDictionary.put(1, "Squat");
        tagMuscles(1, MuscleGroup.QUADS);
        tagMuscles(1, MuscleGroup.GLUTES);
        tagMuscles(1, MuscleGroup.HAMSTRING);

        liftDictionary.put(2, "Bench Press");
        tagMuscles(2, MuscleGroup.PECS);
        tagMuscles(2, MuscleGroup.TRICEPS);
        tagMuscles(2, MuscleGroup.DELTS);

        liftDictionary.put(3, "Deadlift");
        tagMuscles(3, MuscleGroup.HAMSTRING);
        tagMuscles(3, MuscleGroup.GLUTES);
        tagMuscles(3, MuscleGroup.LOWER_BACK);
        tagMuscles(3, MuscleGroup.TRAPS);

        liftDictionary.put(4, "Overhead Press");
        tagMuscles(4, MuscleGroup.DELTS);
        tagMuscles(4, MuscleGroup.TRICEPS);
        tagMuscles(4, MuscleGroup.TRAPS);

        // Default Runs
        runDictionary.put(1, "Easy Run");
        runDictionary.put(2, "Tempo Run");
        runDictionary.put(3, "Intervals");
    }

    public String getLiftName(int id) {
        assert id > 0 : "Shortcut ID must be positive";
        return liftDictionary.getOrDefault(id, null);
    }

    public String getRunName(int id) {
        assert id > 0 : "Shortcut ID must be positive";
        return runDictionary.getOrDefault(id, null);
    }

    public void addLiftShortcut(int id, String name) {
        logger.log(Level.INFO, "Adding/Overwriting lift shortcut: [" + id + "] " + name);
        assert id > 0 && name != null && !name.trim().isEmpty();
        // Remove old muscle tags associated with this ID before overwriting
        if (liftDictionary.containsKey(id)) {
            logger.log(Level.INFO, "Clearing old muscle tags for ID " + id + " due to overwrite.");
            liftMuscleGroups.remove(id);
        }
        liftDictionary.put(id, name);
        liftMuscleGroups.remove(id);
    }

    public void addRunShortcut(int id, String name) {
        assert id > 0 && name != null && !name.trim().isEmpty();
        runDictionary.put(id, name);
    }

    public void removeLiftShortcut(int id) {
        liftDictionary.remove(id);
        liftMuscleGroups.remove(id);
    }

    public void removeRunShortcut(int id) {
        runDictionary.remove(id);
    }

    public Map<Integer, String> getLiftShortcuts() {
        return liftDictionary;
    }

    public Map<Integer, String> getRunShortcuts() {
        return runDictionary;
    }

    public boolean tagMuscles(int id, MuscleGroup muscleGroup) {
        liftMuscleGroups.putIfAbsent(id, EnumSet.noneOf(MuscleGroup.class));
        // Returns true if the set did not already contain the element
        boolean isAdded = liftMuscleGroups.get(id).add(muscleGroup);

        if (isAdded) {
            logger.log(Level.INFO, "Tagged lift ID " + id + " with " + muscleGroup);
        }
        return isAdded;
    }

    public boolean untagMuscles(int id, MuscleGroup muscleGroup) {
        EnumSet<MuscleGroup> set = liftMuscleGroups.get(id);
        if (set == null) {
            return false;
        }
        // Returns true if the set contained the element
        boolean isRemoved = set.remove(muscleGroup);

        if (isRemoved) {
            logger.log(Level.INFO, "Untagged lift ID " + id + " with " + muscleGroup);
        }
        return isRemoved;
    }

    public Set<MuscleGroup> getMusclesFor(int id) {
        Set<MuscleGroup> set = liftMuscleGroups.get(id);
        if (set == null) {
            return EnumSet.noneOf(MuscleGroup.class);
        }
        return set;
    }

    public int getShortcutIdFor(String description) {
        for (java.util.Map.Entry<Integer, String> entry : getLiftShortcuts().entrySet()) {
            if (entry.getValue().equalsIgnoreCase(description)) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
