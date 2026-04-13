package fitlogger.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the user's personal health profile.
 * Stores and manages user-specific data including name, weight, and height.
 */
public class UserProfile {
    private String name;
    private double weight;
    private double height;
    private static final Logger logger = Logger.getLogger(UserProfile.class.getName());

    /**
     * Initializes a new UserProfile with default null and sentinel values.
     */
    public UserProfile() {
        name = null;
        weight = -1;
        height = -1;
    }

    /**
     * Returns the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name The name to be assigned to the profile.
     */
    public void setName(String name) {
        logger.log(Level.INFO, "Updating profile name to: " + name);
        this.name = name;
    }

    /**
     * Returns the weight of the user.
     *
     * @return The user's weight in kilograms.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the user.
     *
     * @param weight The weight value to be stored.
     */
    public void setWeight(double weight) {
        logger.log(Level.INFO, "Updating profile weight: " + weight);
        this.weight = weight;
    }

    /**
     * Returns the height of the user.
     *
     * @return The user's height in centimeters.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the height of the user.
     *
     * @param height The height value to be stored.
     */
    public void setHeight(double height) {
        logger.log(Level.INFO, "Updating profile height: " + height);
        this.height = height;
    }

    /**
     * Formats the user profile data into a string suitable for file storage.
     *
     * @return A formatted string containing the name, height, and weight.
     */
    public String toFileFormat() {
        return "name: " + name + " height: " + height + " weight: " + weight;
    }
}
