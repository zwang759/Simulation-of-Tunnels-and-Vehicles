package cs131.pa2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import cs131.pa2.Abstract.Direction;
import cs131.pa2.Abstract.Tunnel;
import cs131.pa2.Abstract.Vehicle;
import cs131.pa2.Abstract.Log.Event;
import cs131.pa2.Abstract.Log.EventType;

public class BehaviorTest {

    @BeforeEach
    public void setUp() {
        Tunnel.DEFAULT_LOG.clearLog();
    }

    @BeforeAll
    public static void broadcast() {
        System.out.printf("Running Behavior Tests using %s \n", TestUtilities.factory.getClass().getCanonicalName());
    }
    
    private static final int TIMES = 1;

    /**
     * Vehicle RollCall checks the basic functions of a vehicle. Note if the test
     * does not pass neither will any other test *
     */
    @RepeatedTest(TIMES)
    public void Vehicle_RollCall() {

        for (Direction direction : Direction.values()) {
            Vehicle car = TestUtilities.factory.createNewCar(TestUtilities.gbNames[0], direction);
            Vehicle sled = TestUtilities.factory.createNewSled(TestUtilities.gbNames[1], direction);

            assertTrue(car.getDirection().equals(direction), "car is the wrong direction");
            assertTrue(sled.getDirection().equals(direction), "sled is the wrong industry");

            assertTrue(car.getName().equals(TestUtilities.gbNames[0]), "car has the wrong name");
            assertTrue(sled.getName().equals(TestUtilities.gbNames[1]), "sled has the wrong name");

            assertTrue(car.getPriority() == 0, "car has the wrong priority");
            assertTrue(sled.getPriority() == 0, "sled has the wrong priority");

            assertTrue(String.format("%s %s %s", direction, TestUtilities.carName, TestUtilities.gbNames[0]).equals(car.toString()), "car toString does not function as expected");
            assertTrue(String.format("%s %s %s", direction, TestUtilities.sledName, TestUtilities.gbNames[1]).equals(sled.toString()), "sled toString does not function as expected");

        }
    }
    
    @RepeatedTest(TIMES)
    public void Tunnel_Basic() {
        Tunnel tunnel = TestUtilities.factory.createNewBasicTunnel(TestUtilities.mrNames[0]);
        assertTrue(TestUtilities.mrNames[0].equals(tunnel.getName()), "Tunnel has the wrong name");
        assertTrue(String.format("%s", TestUtilities.mrNames[0]).equals(tunnel.toString()), "Tunnel toString does not function as expected");
    }

    @RepeatedTest(TIMES)
    public void car_Enter() {
        Vehicle car = TestUtilities.factory.createNewCar(TestUtilities.gbNames[0], Direction.random());
        Tunnel tunnel = TestUtilities.factory.createNewBasicTunnel(TestUtilities.mrNames[0]);
        TestUtilities.VehicleEnters(car, tunnel);
        Event logEvent = Tunnel.DEFAULT_LOG.get();
        assertTrue(new Event(car, tunnel, EventType.ENTER_ATTEMPT).weakEquals(logEvent), "Tunnel log did not record vehicle entering tunnel");
        logEvent = Tunnel.DEFAULT_LOG.get();
        assertTrue(new Event(car, tunnel, EventType.ENTER_SUCCESS).weakEquals(logEvent), "Tunnel log did not record vehicle entering tunnel");
        logEvent = Tunnel.DEFAULT_LOG.get();
        assertTrue(new Event(car, tunnel, EventType.LEAVE_START).weakEquals(logEvent), "Tunnel log did not record vehicle leaving tunnel");
        logEvent = Tunnel.DEFAULT_LOG.get();
        assertTrue(new Event(car, tunnel, EventType.LEAVE_END).weakEquals(logEvent), "Tunnel log did not record vehicle leaving tunnel");
    }
    

    @RepeatedTest(TIMES)
    public void sled_Enter() {
        Vehicle sled = TestUtilities.factory.createNewSled(TestUtilities.gbNames[0], Direction.random());
        Tunnel tunnel = TestUtilities.factory.createNewBasicTunnel(TestUtilities.mrNames[0]);
        TestUtilities.VehicleEnters(sled, tunnel);
        Event logEvent = Tunnel.DEFAULT_LOG.get();
        
        assertTrue(new Event(sled, tunnel, EventType.ENTER_ATTEMPT).weakEquals(logEvent), "Tunnel log did not record sled entering tunnel");
        logEvent = Tunnel.DEFAULT_LOG.get();
        assertTrue(new Event(sled, tunnel, EventType.ENTER_SUCCESS).weakEquals(logEvent), "Tunnel log did not record sled entering tunnel");
        logEvent = Tunnel.DEFAULT_LOG.get();
        assertTrue(new Event(sled, tunnel, EventType.LEAVE_START).weakEquals(logEvent), "Tunnel log did not record sled entering tunnel");
        logEvent = Tunnel.DEFAULT_LOG.get();
        assertTrue(new Event(sled, tunnel, EventType.LEAVE_END).weakEquals(logEvent), "Tunnel log did not record sled entering tunnel");
    }

    @RepeatedTest(TIMES)
    public void Direction_Constraint() {
        Vehicle car = TestUtilities.factory.createNewCar(TestUtilities.gbNames[0], Direction.NORTH);
        Vehicle violator = TestUtilities.factory.createNewCar(TestUtilities.gbNames[1], Direction.SOUTH);
        Tunnel tunnel = TestUtilities.factory.createNewBasicTunnel(TestUtilities.mrNames[0]);
        boolean canUse = tunnel.tryToEnter(car);
        assertTrue(canUse, String.format("%s cannot use", car));
        canUse = tunnel.tryToEnter(violator);
        assertTrue(!canUse, String.format("%s is using with %s. Violates industry constraint", violator, car));
    }

    @RepeatedTest(TIMES)
    public void Multiple_cars() {
    		Vehicle nick = TestUtilities.factory.createNewCar(TestUtilities.gbNames[0], Direction.NORTH);
        Vehicle peter = TestUtilities.factory.createNewCar(TestUtilities.gbNames[0], Direction.NORTH);
        Vehicle ray = TestUtilities.factory.createNewCar(TestUtilities.gbNames[1], Direction.NORTH);
        Vehicle walter = TestUtilities.factory.createNewCar(TestUtilities.gbNames[7], Direction.NORTH);
        Tunnel tunnel = TestUtilities.factory.createNewBasicTunnel(TestUtilities.mrNames[0]);
        boolean canUse = tunnel.tryToEnter(peter);
        assertTrue(canUse, String.format("%s cannot use", peter));
        canUse = tunnel.tryToEnter(ray);
        assertTrue(canUse, String.format("%s is not using with %s.", peter, ray));
        canUse = tunnel.tryToEnter(nick);
        assertTrue(canUse, String.format("%s is not using with %s and %s.", nick, peter, ray));
        canUse = tunnel.tryToEnter(walter);
        assertTrue(!canUse, String.format("%s is using with %s, %s and %s violates number constraint.", walter, peter, ray, nick));
        peter.doWhileInTunnel();
        tunnel.exitTunnel(peter);
        ray.doWhileInTunnel();
        tunnel.exitTunnel(ray);
        canUse = tunnel.tryToEnter(walter);
        assertTrue(canUse, String.format("%s cannot use, %s and %s did not leave tunnel.", walter, peter, ray));
    }
}
