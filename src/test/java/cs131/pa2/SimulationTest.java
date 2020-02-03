package cs131.pa2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import cs131.pa2.Abstract.Direction;
import cs131.pa2.Abstract.Tunnel;
import cs131.pa2.Abstract.Vehicle;
import cs131.pa2.Abstract.Log.Event;
import cs131.pa2.Abstract.Log.EventType;
import cs131.pa2.Abstract.Log.Log;

public class SimulationTest {

    public static boolean DEBUG_MODE = true;
    private static final int wave1Cars = 50;
    private static final int wave2Sleds= 50;
    private static final int wave3Cars = 50;    
    
    private static final int TIMES = 1;
    
    @BeforeEach
    public void setUp() {
        Tunnel.DEFAULT_LOG.clearLog();
    }
    
    @BeforeAll
    public static void broadcast() {
        System.out.printf("Running Simulation Tests using %s \n", TestUtilities.factory.getClass().getCanonicalName());
    }

    @RepeatedTest(TIMES)
    public void Basic_Tunnel_Test() {
        LogVerifier verifier = new LogVerifier(Tunnel.DEFAULT_LOG);
        Thread verifierThread = new Thread(verifier);
        verifierThread.start();
        Collection<Tunnel> tunnels = new ArrayList<Tunnel>();
        Collection<Thread> vehicleThread = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            tunnels.add(TestUtilities.factory.createNewBasicTunnel(TestUtilities.mrNames[i]));
        }
        for (int i = 0; i < wave1Cars; i++) {
            Vehicle car = TestUtilities.factory.createNewCar(Integer.toString(i), Direction.values()[i % Direction.values().length]);
            car.addTunnel(tunnels);
            Thread sharedThread = new Thread(car);
            sharedThread.start();
            vehicleThread.add(sharedThread);
        }
        for (int i = wave1Cars; i < wave1Cars+wave2Sleds; i++) {
            Vehicle sled = TestUtilities.factory.createNewSled(Integer.toString(i), Direction.values()[i % Direction.values().length]);
            sled.addTunnel(tunnels);
            Thread basicThread = new Thread(sled);
            basicThread.start();
            vehicleThread.add(basicThread);
        }
      
        for (int i = wave1Cars+wave2Sleds; i < wave1Cars+wave2Sleds+wave3Cars; i++) {
            Vehicle car = TestUtilities.factory.createNewCar(Integer.toString(i), Direction.values()[i % Direction.values().length]);
            car.addTunnel(tunnels);
            Thread sharedThread = new Thread(car);
            sharedThread.start();
            vehicleThread.add(sharedThread);
        }
        try {
            for (Thread t : vehicleThread) {
                t.join();
            }
            Tunnel.DEFAULT_LOG.addToLog(EventType.END_TEST);
            verifierThread.join();
        } catch (InterruptedException ex) {
            assertTrue(false, "Interruption exception occurred.");
        }
        assertTrue(!verifier.hasErrors(), verifier.printErrors());
    }

    private class LogVerifier implements Runnable {

        private final Log log;
        private final Collection<Vehicle> satisfiedVehicles;
        private Map<Tunnel, Collection<Vehicle>> tunnels;
        private Map<Integer, Event> potential_entry_events;
        private Map<Tunnel, Collection<Vehicle>> exitSet;
        private Set<String> errors;

        public LogVerifier(Log log) {
            this.log = log;
            this.tunnels = new HashMap<Tunnel, Collection<Vehicle>>();
            this.potential_entry_events = new HashMap<Integer, Event>();
            this.exitSet = new HashMap<Tunnel, Collection<Vehicle>>();
            this.satisfiedVehicles = new ArrayList<Vehicle>();
            this.errors = new HashSet<String>();
        }

        @Override
        public void run() {
            Event currentEvent;
            do {
                currentEvent = log.get();
                Vehicle curVehicle = currentEvent.getVehicle();
                Tunnel curTunnel = currentEvent.getTunnel();
                if (curTunnel != null) {
                    if (exitSet.get(curTunnel) == null) {
                        exitSet.put(curTunnel, new ArrayList<Vehicle>());
                    }
                    if (tunnels.get(curTunnel) == null) {
                        tunnels.put(curTunnel, new ArrayList<Vehicle>());
                    }
                }
                if(SimulationTest.DEBUG_MODE && (!currentEvent.getEvent().equals(EventType.ENTER_ATTEMPT) && !currentEvent.getEvent().equals(EventType.ENTER_FAILED) && !currentEvent.getEvent().equals(EventType.LEAVE_START))){
                    System.out.println(currentEvent.toString());
                }
                switch (currentEvent.getEvent()) {
                    case ENTER_ATTEMPT:
                        potential_entry_events.put(currentEvent.getSignifier(), currentEvent);
                        break;
                    case ENTER_SUCCESS:
                        potential_entry_events.remove(currentEvent.getSignifier());
                        checkEnterConditions(curVehicle, curTunnel);
                        tunnels.get(curTunnel).add(curVehicle);
                        break;
                    case ENTER_FAILED:
                        potential_entry_events.remove(currentEvent.getSignifier());
                        break;
                    case LEAVE_START:
                        checkLeaveConditions(curVehicle, curTunnel);
                        tunnels.get(curTunnel).remove(curVehicle);
                        exitSet.get(curTunnel).add(curVehicle);
                    case LEAVE_END:
                        exitSet.get(curTunnel).remove(curVehicle);
                        break;
                    case COMPLETE:
                        satisfiedVehicles.add(curVehicle);
                        break;
                    case ERROR:
                        errors.add("An error occurred during the simulation");
                        break;
                    case INTERRUPTED:
                        break;
                    case END_TEST:
                    	break;
                }
            } while (!currentEvent.getEvent().equals(EventType.END_TEST));
        }

        private void checkEnterConditions(Vehicle newVehicle, Tunnel toTunnel) {
            if (satisfiedVehicles.contains(newVehicle)) {
                errors.add(String.format("%s entered %s when the vehicle is already entered.", newVehicle, toTunnel));
            }


            if (isSled(newVehicle)) {
                this.errors.addAll(verifySledEntry(newVehicle, toTunnel.toString(), tunnels.get(toTunnel)));
            }
            if (isCar(newVehicle)) {
                this.errors.addAll(verifyCarEntry(newVehicle, toTunnel.toString(), tunnels.get(toTunnel)));
            }
           
        }

        private void checkLeaveConditions(Vehicle newVehicle, Tunnel tunnel) {
            if (satisfiedVehicles.contains(newVehicle)) {
                errors.add(String.format("%s was satisfied before leaving %s.", newVehicle, tunnel));
            }
            Collection<Vehicle> currentOccupants = tunnels.get(tunnel);
            if (currentOccupants == null || currentOccupants.isEmpty() || !currentOccupants.contains(newVehicle)) {
                errors.add(String.format("%s exit %s before entering.", newVehicle, tunnel));
            }
        }

        private Collection<String> verifyCarEntry(Vehicle shared, String tunnel, Collection<Vehicle> occupants) {
            Collection<String> errors = new ArrayList<String>();
            if (occupants != null && !occupants.isEmpty()) {
                int sharedCount = 0;
                for (Vehicle din : occupants) {
                    if (isSled(din)) {
                        errors.add(String.format("%s entered %s with %s.", shared, tunnel, din));
                    }
                    if (isCar(din)) {
                        sharedCount++;
                        if (sharedCount > 3) {
                            errors.add(String.format("%s entered %s with multiple shared vehicles present already.", shared, tunnel, din));
                        }
                    }
                    if (!shared.getDirection().equals(din.getDirection())) {
                        errors.add(String.format("%s entered %s with vehicles of the different direction.", shared, tunnel));
                    }
                }
            }
            return errors;
        }

        private Collection<String> verifySledEntry(Vehicle basic, String tunnel, Collection<Vehicle> occupants) {
            Collection<String> errors = new ArrayList<String>();
            if (occupants != null && !occupants.isEmpty()) {
                for (Vehicle din : occupants) {
                    if (!isGuest(din)) {
                        errors.add(String.format("%s entered %s with %s.", basic, tunnel, din));
                    }
                    if (!basic.getDirection().equals(din.getDirection())) {
                        errors.add(String.format("%s entered %s with vehicles of the different direction.", basic, tunnel));
                    }
                }
            }
            return errors;
        }

        private boolean isCar(Vehicle vehicle) {
            return vehicle.toString().contains(TestUtilities.carName);

        }

        private boolean isSled(Vehicle vehicle) {
            return vehicle.toString().contains(TestUtilities.sledName);

        }

        private boolean isGuest(Vehicle vehicle) { //legacy
            return false;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public String printErrors() {
            StringBuilder builder = new StringBuilder();
            for (String er : errors) {
                builder.append(er);
                builder.append("\n");
            }
            return builder.toString();
        }
    }
}
