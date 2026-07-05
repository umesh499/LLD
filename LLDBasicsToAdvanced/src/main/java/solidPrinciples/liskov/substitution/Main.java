package solidPrinciples.liskov.substitution;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		List<Vehicle> vehiclesList = new ArrayList<>();
		vehiclesList.add(new BiCycle());
		vehiclesList.add(new MotorCycle());
		vehiclesList.add(new Car());
		for(Vehicle vehicle : vehiclesList){
			System.out.println(vehicle.noOfWheels());
		}
		List<EngineVehicle> vehicles = new ArrayList<>();
		vehicles.add(new MotorCycle());
		vehicles.add(new Car());
		//vehicles.add(new BiCycle()); not allowed 

		for(EngineVehicle v : vehicles){
			System.out.println(v.noOfWheels());
		}
	}
}
