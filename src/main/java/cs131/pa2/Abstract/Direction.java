package cs131.pa2.Abstract;

/**
 * 
 * The enum holding the direction that a vehicle is driving
 * @author cs131a
 * 
 */
public enum Direction {

	NORTH("NORTH"),
	SOUTH("SOUTH");
    private final String name;

    /**
     * Constructor of the enum Direction.
     * @param name the name of this direction
     */
    private Direction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns a random value for direction from the available values.
     * @return a random value for direction
     */
    public static Direction random() {
        int i = (int) (Math.random() * Direction.values().length);
        return Direction.values()[i];
    }
};