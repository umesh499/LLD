package liskov.substitution;

public class Car extends EngineVehicle{
	@Override
	public Integer noOfWheels() {
		return 4;
	}
}
