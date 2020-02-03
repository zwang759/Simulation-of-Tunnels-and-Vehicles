package cs131.pa2.CarsTunnels;

import cs131.pa2.Abstract.Direction;
import cs131.pa2.Abstract.Vehicle;

/**
 * A Car is a fast Vehicle.
 * @author cs131a
 * 
 */
public class Car extends Vehicle {

	/**
	 * Creates a new instance of the class Car with the given name and direction
	 * @param name the name of the car to create
	 * @param direction the direction of the car to create
	 */
    public Car(String name, Direction direction) {
        super(name, direction);
    }

    @Override
    protected int getDefaultSpeed() {
        return 6;
    }
    
    @Override
    public String toString() {
        return String.format("%s CAR %s", super.getDirection(), super.getName());
    }
}
