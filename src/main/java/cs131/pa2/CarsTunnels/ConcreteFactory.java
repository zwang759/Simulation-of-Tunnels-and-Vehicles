package cs131.pa2.CarsTunnels;

import java.util.Collection;

import cs131.pa2.Abstract.Direction;
import cs131.pa2.Abstract.Factory;
import cs131.pa2.Abstract.Tunnel;
import cs131.pa2.Abstract.Vehicle;
import cs131.pa2.Abstract.Log.Log;

/**
 * The class implementing the Factory interface for creating instances of classes
 * @author cs131a
 *
 */
public class ConcreteFactory implements Factory {

    @Override
    public Tunnel createNewBasicTunnel(String name){
        return new BasicTunnel(name);
    }

    @Override
    public Vehicle createNewCar(String name, Direction direction){
        return new Car(name, direction);
    }

    @Override
    public Vehicle createNewSled(String name, Direction direction){
        return new Sled(name, direction);
    }

    @Override
    public Tunnel createNewPriorityScheduler(String name, Collection<Tunnel> tunnels, Log log){
        return new PriorityScheduler(name, tunnels, log);
    }

	@Override
	public Vehicle createNewAmbulance(String name, Direction direction) {
		return new Ambulance(name, direction);
	}

	@Override
	public Tunnel createNewPreemptivePriorityScheduler(String name, Collection<Tunnel> tunnels, Log log) {
		return new PreemptivePriorityScheduler(name, tunnels, log);
	}
}
