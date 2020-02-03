package cs131.pa2.CarsTunnels;

import cs131.pa2.Abstract.Direction;
import cs131.pa2.Abstract.Tunnel;
import cs131.pa2.Abstract.Vehicle;

/**
 * 
 * The class for the Basic Tunnel, extending Tunnel.
 * @author cs131a
 *
 */
public class BasicTunnel extends Tunnel{

	/**
	 * Creates a new instance of a basic tunnel with the given name
	 * @param name the name of the basic tunnel
	 */
	// the current direction for this tunnel
	public Direction direction;
	// number of cars currently in the tunnel
	public int numCars;
	// number of sleds currently in the tunnel
	public int numSleds;

	// number of ambulances currently in the tunnel
	public int numAmbulance;

	public BasicTunnel(String name) {
		super(name);
		this.numCars = 0;
		this.numSleds = 0;
		this.numAmbulance = 0;
	}

	@Override
	public synchronized boolean tryToEnterInner(Vehicle vehicle) {
		if (vehicle instanceof Ambulance) {
			if (this.numAmbulance == 0) {
				this.numAmbulance++;
				return true;
			}
			else return false;
		}

		if (this.numCars == 0 && this.numSleds == 0) {
			if (vehicle instanceof Car) {
				this.numCars++;
			}
			if (vehicle instanceof Sled) {
				this.numSleds++;
			}
			this.direction = vehicle.getDirection();
			return true;
		}
		if (this.numCars >= 3 || this.numSleds >= 1 || vehicle.getDirection() != this.direction) {
			return false;
		}
		if (vehicle instanceof Car) {
			if (this.numSleds < 1 && this.numCars < 3) {
				numCars++;
				return true;
			}
		}
		if (vehicle instanceof Sled) {
			if (this.numSleds < 1 && this.numCars < 1) {
				numSleds++;
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized void exitTunnelInner(Vehicle vehicle) {
		if (vehicle instanceof Car) numCars--;
		if (vehicle instanceof Sled) numSleds--;
		if (vehicle instanceof Ambulance) numAmbulance--;
	}

	public boolean hasAmbulance() {
		return this.numAmbulance > 0;
	}
}
