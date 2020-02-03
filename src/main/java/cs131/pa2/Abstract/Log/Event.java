package cs131.pa2.Abstract.Log;

import cs131.pa2.Abstract.Tunnel;
import cs131.pa2.Abstract.Vehicle;

public class Event {

    private final Vehicle vehicle;
    private final Tunnel tunnel;
    private final EventType event;
    private final int signifier;
    //there is no guarantee the signifier is 100% unique
    //but given the size of our tests vs the possible values for the signifier we forsee little clashing

    public Event(Vehicle vehicle, Tunnel tunnel, EventType event, int signifier) {
        this.vehicle = vehicle;
        this.tunnel = tunnel;
        this.event = event;
        this.signifier = signifier;
    }

    public Event(Vehicle vehicle, Tunnel tunnel, EventType event) {
        this(vehicle, tunnel, event, (int) System.currentTimeMillis());
    }

    public Event(Vehicle vehicle, EventType event) {
        this(vehicle, null, event);
    }

    public Event(EventType event) {
        this(null, null, event);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Tunnel getTunnel() {
        return tunnel;
    }

    public EventType getEvent() {
        return event;
    }
    
    public int getSignifier() {
        return signifier;
    }

    @Override
    public String toString() {
        switch (event) {
            case END_TEST:
            case ERROR:
                return event.toString();
            case COMPLETE:
                return String.format("%s %s", vehicle, event);
            default:
                return String.format("%s %s %s", vehicle, event, tunnel);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Event) {
            Event event = (Event) o;
            return event.getSignifier() == this.signifier && this.weakEquals(event);
        } else {
            return false;
        }

    }

    public boolean weakEquals(Event event) {
        //A weaker version of equals,  checks the tunnel, vehicle, and event type are the same.
        //Useful for checking if an event was logged, when the exact logging details are unknown.
        return this.toString().equals(event.toString());
    }
}
