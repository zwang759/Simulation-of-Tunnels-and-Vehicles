package cs131.pa2.Abstract;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import cs131.pa2.Abstract.Log.EventType;
import cs131.pa2.Abstract.Log.Log;


/**
 * A Tunnel is an object which can be entered by vehicles. Vehicles
 * themselves are responsible for indicating when they want to enter
 * and when they are ready to leave. Tunnels are responsible for
 * indicating if it is safe for a Vehicle to enter.
 *
 * When a Vehicle wants to enter a Tunnel, it calls tryToEnter on the
 * Tunnel instance. If the Vehicle has entered the Tunnel
 * successfully, tryToEnter returns true. Otherwise, tryToEnter
 * returns false. The Vehicle simulates the time spent in the tunnel,
 * and then must call exitTunnel on the same Tunnel instance it
 * entered.
 * 
 * @author cs131a
 */
public abstract class Tunnel {
	
	/**
	 * The name of the tunnel
	 */
	private final String name;
	/**
	 * The default log
	 */
	public static Log DEFAULT_LOG = new Log();
	/**
	 * The log used for this tunnel
	 */
    private final Log log;

    /**
     * The lock used for this tunnel
     */
    private ReentrantLock tunnelLock;

    /**
     * The Condition used for this tunnel
     */
    private Condition ambulanceIn, ambulanceOut;

    /**
     * The time for ambulance to pass the tunnel
     */
    private long ambulanceTime;
    
    /**
     * Constructs an instance of this class with the given name and log
     * @param name the name of the tunnel to create
     * @param log the log to be used for logging
     */
    public Tunnel(String name, Log log) {
        this.name = name;
        this.log = log;
    }

    /**
     *  Constructs an instance of this class with the given name and the default log stored in this class
     * @param name the name of the tunnel to create
     */
    public Tunnel(String name) {
        this(name, Tunnel.DEFAULT_LOG);
    }
    
    /**
     * Checks whether the given vehicle should enter this tunnel, based on the vehicles currently in the tunnel.
     * It calls the tryToEnterInner which is implemented by the subclass, and also adds entries for enter attempt and enter result in the log.
     * @param vehicle the vehicle attempting to enter this tunnel
     * @return true if the vehicle enters this tunnel successfully, false otherwise
     */
    public final boolean tryToEnter(Vehicle vehicle) {
        //Do not overwrite this function, you should be overwriting tryToEnterInner
        int sig = log.nextLogEventNumber();
        log.addToLog(vehicle, this, EventType.ENTER_ATTEMPT, sig);
        if (this.tryToEnterInner(vehicle)) {
            log.addToLog(vehicle, this, EventType.ENTER_SUCCESS, sig);
            return true;
        } else {
            log.addToLog(vehicle, this, EventType.ENTER_FAILED, sig);
            return false;
        }
    }
    
    /**
     * Vehicle tries to enter a tunnel. Implemented by the subclass
     *
     * @param  vehicle The vehicle that is attempting to enter
     * @return true if the vehicle was able to enter, false otherwise
     */
    public abstract boolean tryToEnterInner(Vehicle vehicle);
    
    /**
     * Vehicle exits this tunnel. It calls exitTunnelInner and adds entries in the log.
     * @param vehicle the vehicle exiting the tunnel
     */
    public final void exitTunnel(Vehicle vehicle) {
        //Do not overwrite this function, you should be overwriting disconnectInner
        int sig = log.nextLogEventNumber();
        this.log.addToLog(vehicle, this, EventType.LEAVE_START, sig);
        this.exitTunnelInner(vehicle);
        this.log.addToLog(vehicle, this, EventType.LEAVE_END, sig);
    }
    
    /**
     * Vehicle exits the tunnel.
     * 
     * @param vehicle The vehicle that is exiting the tunnel
     */
    public abstract void exitTunnelInner(Vehicle vehicle);


    /**
     * Returns the name of this tunnel
     *
     * @return The name of this tunnel
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("%s", this.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tunnel other = (Tunnel) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public void setTunnelLockAndCondition() {
        this.tunnelLock = new ReentrantLock();
        this.ambulanceIn = this.tunnelLock.newCondition();
        this.ambulanceOut = this.tunnelLock.newCondition();
    }

    public ReentrantLock getTunnelLock() {
        return this.tunnelLock;
    }

    public Condition getCondition1() {
        return this.ambulanceIn;
    }
    public Condition getCondition2() {
        return this.ambulanceOut;
    }

    public void setAmbulanceTime(long ambulanceTime) {
        this.ambulanceTime = ambulanceTime;
    }

    public long getAmbulanceTime() {
        return this.ambulanceTime;
    }

}
