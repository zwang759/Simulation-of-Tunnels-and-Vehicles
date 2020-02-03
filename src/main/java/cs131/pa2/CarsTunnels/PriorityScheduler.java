package cs131.pa2.CarsTunnels;

import cs131.pa2.Abstract.Log.Log;
import cs131.pa2.Abstract.Tunnel;
import cs131.pa2.Abstract.Vehicle;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The priority scheduler assigns vehicles to tunnels based on their priority
 * It extends the Tunnel class.
 * @author cs131a
 *
 */
public class PriorityScheduler extends Tunnel {

	private List<Tunnel> basicTunnelList;

	private Queue<Vehicle> maxPQ;

	private ReentrantLock reentrantLock;

	private Condition condition;

	// hashtable is thread safe
	// use to record the vehicle and its current tunnel
	private Hashtable<Vehicle, Tunnel> hashtable = new Hashtable<Vehicle, Tunnel>();

	/**
	 * Creates a new instance of the class PriorityScheduler with given name by calling the constructor of the super class
	 * @param name the name of the priority scheduler to create
	 */
	public PriorityScheduler(String name, Collection<Tunnel> tunnels, Log log) {
		super(name, log);
		this.basicTunnelList = new LinkedList<Tunnel>(tunnels);
		// reverse the pq from min to max
		this.maxPQ = new PriorityQueue<Vehicle>((x, y) -> y.getPriority() - x.getPriority());
		this.reentrantLock = new ReentrantLock();
		this.condition = reentrantLock.newCondition();
	}

	@Override
	public boolean tryToEnterInner(Vehicle vehicle) {
		reentrantLock.lock();

		// if either this vehicle is not highest priority or there are no tunnels that can be entered by this vehicle
		// then this vehicle must wait.
		// take the negation of this statement
		// so to enter the tunnel, vehicle must (highest priority && can enter one of the tunnels)
		try {
			maxPQ.offer(vehicle);

			while (!vehicle.equals(maxPQ.peek()) || !canEnterTunnel(vehicle)) {
				try {
					condition.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// now this vehicle is able to enter
			maxPQ.poll();
			return true;

		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * Return true if there exists a tunnel that can be entered by this vehicle
	 */
	public boolean canEnterTunnel(Vehicle vehicle) {
		// find a tunnel to enter
		for (Tunnel tunnel: basicTunnelList) {
			if (tunnel.tryToEnter(vehicle)) {
				hashtable.put(vehicle, tunnel);
				return true;
			}
		}
		return false;
	}

	@Override
	public void exitTunnelInner(Vehicle vehicle) {
		reentrantLock.lock();
		try {
			hashtable.get(vehicle).exitTunnel(vehicle);
			hashtable.remove(vehicle);
			condition.signalAll();
		} finally {
			reentrantLock.unlock();
		}

	}

}
