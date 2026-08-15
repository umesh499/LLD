package Questions;

import java.util.*;

/*
Functional :
1. User should be able to request to go up/down
2. User should be able to enter destination floor
3. User should be able to open and close the elevator door 


Non-Functional:

1. Extensible to add more elevators or more floors .
2. System should be used for more than one buildings

Core Entities:
1. Elevator
2. Request

3. SchedulingStratergy
4. NearestElevatorStratergy

5. ElevatorController





*/

enum ElevatorStatus{
    MOVING,IDLE,MAINTENANACE,STOPPED;
}

enum Direction{
    UP,DOWN,IDLE;
}

class ElevatorRequest{
    int floor;
    Direction direction;
    ElevatorRequest(int floor, Direction direction){
        this.floor = floor;
        this.direction = direction;
    }
}

class Elevator implements Runnable{
    String id;
    private final Object lock = new Object();
    Direction direction = Direction.IDLE;
    ElevatorStatus status = ElevatorStatus.IDLE;
    TreeSet<ElevatorRequest> upStops =
    new TreeSet<>((a,b) -> Integer.compare(a.floor, b.floor));

    TreeSet<ElevatorRequest> downStops =
    new TreeSet<>((a,b) -> Integer.compare(b.floor, a.floor));
    int currentFLoor;
    boolean running = false;
    Elevator(String id, int currentFLoor){
        this.id = id;
        this.currentFLoor = currentFLoor;

    }
    boolean isIdle(){
        return status.equals(ElevatorStatus.IDLE);
    }

    void startElevator(){
        this.running = true;
        this.status = ElevatorStatus.MOVING;
    }

    void stopElevator(){
        this.running = false;
        this.status = ElevatorStatus.IDLE;
    }

    void addRequest(ElevatorRequest request){
        synchronized(lock){
            if(request.floor > currentFLoor){
                upStops.add(request);
                if(this.status.equals(ElevatorStatus.IDLE)){
                    this.status = ElevatorStatus.MOVING;
                    this.direction = Direction.UP;
                }
            }else if(request.floor < currentFLoor){
                downStops.add(request);
                if(this.status.equals(ElevatorStatus.IDLE)){
                    this.status = ElevatorStatus.MOVING;
                    this.direction = Direction.DOWN;
                }
            }else{
                //Nothing to do in the same floor
            }
        }
    }


    @Override
    public void run() {
        while(running){
            
            try {
                step();
                Thread.sleep(200);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
    private void openAndCloseDoors() throws InterruptedException {
        status = ElevatorStatus.STOPPED;
        System.out.println("Elevator " + this.id + " arrived at floor " + currentFLoor + ", doors OPEN");
        Thread.sleep(100);
        System.out.println("Elevator " + this.id + " doors CLOSED at floor " + currentFLoor);
    }
    // Stepping to the next request to serve the user
    private void step() throws InterruptedException {
        if(this.direction.equals(Direction.UP)){
            if(upStops.isEmpty()){
                this.direction = downStops.isEmpty() ? Direction.IDLE : Direction.DOWN;
                this.status = downStops.isEmpty() ? ElevatorStatus.IDLE : ElevatorStatus.MOVING;
                return;
            }
            ElevatorRequest rq = upStops.pollFirst();
            openAndCloseDoors();
            int prevFloor = currentFLoor;
            currentFLoor = rq.floor;
            System.out.println(Thread.currentThread().getName()+" from floor:" +prevFloor +": Moving up to floor: "+ currentFLoor);

        }else if(this.direction.equals(Direction.DOWN)){
            if(downStops.isEmpty()){
                this.direction = upStops.isEmpty() ? Direction.IDLE : Direction.UP;
                this.status = upStops.isEmpty() ? ElevatorStatus.IDLE : ElevatorStatus.MOVING;
                return;
            }
            ElevatorRequest rq = downStops.pollFirst();
            openAndCloseDoors();
            int prevFloor = currentFLoor;
            currentFLoor = rq.floor;
            System.out.println(Thread.currentThread().getName()+" from floor : " +prevFloor + ": Moving down to floor: "+ currentFLoor);
        }else{
            this.direction = Direction.IDLE;
            this.status = ElevatorStatus.IDLE;
        }
    }
            
                

}

public class ElevatorSystemDemo{
    public static void main(String[] args) throws InterruptedException {
        ElevatorSystem system = new ElevatorSystem(2, new NearestElevatorStratergy());

        system.addRequest(new ElevatorRequest(5, Direction.UP));
        system.addRequest(new ElevatorRequest(2, Direction.UP));
        system.addRequest(new ElevatorRequest(3, Direction.DOWN));
        system.addRequest(new ElevatorRequest(3, Direction.UP));
        system.addRequest(new ElevatorRequest(8, Direction.DOWN));
        system.addRequest(new ElevatorRequest(1, Direction.DOWN));
        system.addRequest(new ElevatorRequest(10, Direction.UP));
        
        Thread.sleep(2000);
        system.addRequest(new ElevatorRequest(5, Direction.UP));
        system.addRequest(new ElevatorRequest(1, Direction.UP));

        boolean finished = system.awaitAllIdle(3000);
        
        System.out.println("\nAll requests serviced: " + finished);
        System.out.println("Final fleet status:");
        system.printFleetStatus();

        system.shutdown();
    }
}
class ElevatorSystem {
    List<Elevator> elevators = new ArrayList<>();
    List<Thread> threads = new ArrayList<>();
    int numberOfElevators;
    SchedulingStratergy stratergy;

    ElevatorSystem(int numberOfElevators, SchedulingStratergy stratergy){
        this.numberOfElevators = numberOfElevators;
        this.stratergy = stratergy;
        for(int i=0;i<numberOfElevators;i++){
            Elevator elevator = new Elevator(""+i, 0);
            elevators.add(elevator);
            Thread t = new Thread(elevator, "elevator -"+i);
            threads.add(t);
            elevator.startElevator();
            t.start();
        }
    }

    void addRequest(ElevatorRequest request) {
        Elevator elevator = this.stratergy.getElevatorFromList(this.elevators, request);
        elevator.addRequest(request);
        System.out.println("Assigned request for floor " + request.floor + " to elevator " + elevator.id);
    }

    // No busy-polling on the caller's side either: sleep between checks
    // instead of spinning, and give up after timeoutMs so a bug in one
    // elevator can't hang the demo forever.
    boolean awaitAllIdle(long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            boolean allIdle = true;
            for (Elevator elevator : elevators) {
                if (!elevator.isIdle()) {
                    allIdle = false;
                    break;
                }
            }
            if (allIdle) {
                return true;
            }
            Thread.sleep(20);
        }
        return false;
    }

    void printFleetStatus() {
        for (Elevator elevator : elevators) {
            System.out.println("  Elevator " + elevator.id + " resting at floor " + elevator.currentFLoor);
        }
    }

    void shutdown() throws InterruptedException {
        for (Elevator elevator : elevators) {
            elevator.stopElevator();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}

interface SchedulingStratergy{
    Elevator getElevatorFromList(List<Elevator> elevators, ElevatorRequest request);
}

class NearestElevatorStratergy implements SchedulingStratergy{

    @Override
    public Elevator getElevatorFromList(List<Elevator> elevators, ElevatorRequest request) {
        Elevator best = null;
        int bestScore = Integer.MAX_VALUE;
        for (Elevator elevator : elevators) {
            int floor = elevator.currentFLoor;
            boolean idle = elevator.isIdle();
            int distance = Math.abs(floor - request.floor);
            // Idle elevators are scored as if they were half as far away,
            // so a busy elevator only wins when it is clearly closer.
            int score = idle ? distance : distance * 2;
            if (score < bestScore) {
                bestScore = score;
                best = elevator;
            }
        }
        return best;
    }
    
}
