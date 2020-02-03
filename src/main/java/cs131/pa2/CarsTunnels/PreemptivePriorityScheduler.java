package cs131.pa2.CarsTunnels;

import cs131.pa2.Abstract.Log.Log;
import cs131.pa2.Abstract.Tunnel;
import cs131.pa2.Abstract.Vehicle;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The preemptive priority scheduler assigns vehicles to tunnels based on their priority and supports ambulances.
 * It extends the Tunnel class.
 * @author cs131a
 *
 */
public class PreemptivePriorityScheduler extends Tunnel{

    private List<Tunnel> tunnelList;
    private Queue<Vehicle> maxPQ;
    private ReentrantLock allLock;
    private Condition condition;
    private Hashtable<Vehicle, Tunnel> hashtableTunnel = new Hashtable<Vehicle, Tunnel> ();


	/**
	 * Creates a new instance of the class PreemptivePriorityScheduler with given name by calling the constructor of the super class
	 * @param name the name of the preemptive priority scheduler to create
	 */
	public PreemptivePriorityScheduler(String name, Collection<Tunnel> tunnels , Log log) {
		super(name, log);
		this.tunnelList = new LinkedList<Tunnel>(tunnels);
        this.maxPQ = new PriorityQueue<Vehicle>((x, y) -> y.getPriority() - x.getPriority());
        allLock = new ReentrantLock();
        condition = allLock.newCondition();
		// set tunnelLock for every tunnel
		for (Tunnel tunnel: tunnelList) {
            tunnel.setTunnelLockAndCondition();
        }
	}

	@Override
	public boolean tryToEnterInner(Vehicle vehicle) {
	    // copy code from priority scheduler
        allLock.lock();
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
            allLock.unlock();
        }
    }


    private boolean canEnterTunnel(Vehicle vehicle) {
        // find a tunnel to enter
        allLock.lock();
        try {
            for (Tunnel tunnel: tunnelList) {
                if (tunnel.tryToEnter(vehicle)) {
                    hashtableTunnel.put(vehicle, tunnel);
                    vehicle.setTunnel(tunnel);
                    vehicle.setTunnelAndCondition(tunnel.getTunnelLock(), tunnel.getCondition1(), tunnel.getCondition2());
                    vehicle.addTunnel(tunnel);
                    return true;
                }
            }
            return false;
        } finally {
            allLock.unlock();
        }
    }

	@Override
	public void exitTunnelInner(Vehicle vehicle) {
        allLock.lock();
        try {
            hashtableTunnel.get(vehicle).exitTunnel(vehicle);
            hashtableTunnel.remove(vehicle);
            vehicle.clearTunnel();
            condition.signalAll();
        } finally {
            allLock.unlock();
        }
	}
}

