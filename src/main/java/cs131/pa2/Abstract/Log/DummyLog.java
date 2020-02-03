package cs131.pa2.Abstract.Log;

import cs131.pa2.Abstract.Tunnel;
import cs131.pa2.Abstract.Vehicle;

public class DummyLog extends Log {

    public final void addToLog(Vehicle vehicle, Tunnel tunel, EventType type) {
        //do nothing
    }

    public void addToLog(Vehicle vehicle, EventType type) {
        //do nothing
    }

    public void addToLog(EventType type) {
        //do nothing
    }
}
