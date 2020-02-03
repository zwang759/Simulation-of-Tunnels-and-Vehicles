package cs131.pa2.Abstract;

import java.util.Collection;

import cs131.pa2.Abstract.Log.Log;

/**
 * The interface that creates instances of specific classes
 * @author cs131a
 *
 */
public interface Factory {

	/**
	 * Creates a new instance of class BasicTunnel
	 * @param name the name of the tunnel to create
	 * @return the newly created instance of the BasicTunnel class
	 */
    public abstract Tunnel createNewBasicTunnel(String name);
    
    /**
     * Creates a new instance of class PriorityScheduler
     * @param name the name of the priority scheduler to create
     * @param tunnels the collection of tunnels that the scheduler should manage
     * @param log the log for logging the operations
     * @return the newly created instance of the PriorityScheduler class
     */
    public abstract Tunnel createNewPriorityScheduler(String name, Collection<Tunnel> tunnels, Log log);
    
    /**
     * Creates a new instance of class PreemptivePriorityScheduler
     * @param name the name of the preemptive priority scheduler to create
     * @param tunnels the collection of tunnels that the scheduler should manage
     * @param log the log for logging the operations
     * @return the newly created instance of the PreemptivePriorityScheduler class
     */
    public abstract Tunnel createNewPreemptivePriorityScheduler(String name, Collection<Tunnel> tunnels, Log log);
    
    /**
     * Creates a new instance of class Car
     * @param name the name of the car to create
     * @param direction the direction of the car
     * @return the newly created instance of the Car class
     */
    public abstract Vehicle createNewCar(String name, Direction direction);

    /**
     * Creates a new instance of class Sled
     * @param name the name of the sled to create
     * @param direction the direction of the sled
     * @return the newly created instance of the Sled class
     */
    public abstract Vehicle createNewSled(String name, Direction direction);
    
    /**
     * Creates a new instance of class Ambulance
     * @param name the name of the ambulance to create
     * @param direction the direction of the ambulance
     * @return the newly created instance of the Ambulance class
     */
    public abstract Vehicle createNewAmbulance(String name, Direction direction);

}
