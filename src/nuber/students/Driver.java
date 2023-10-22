package nuber.students;

public class Driver extends Person {
	
	private  Passenger currentPassenger;
	
	public Driver(String driverName, int maxSleep)
	{
		super(driverName, maxSleep);
	}
	
	/**
	 * Stores the provided passenger as the driver's current passenger and then
	 * sleeps the thread for between 0-maxDelay milliseconds.
	 * 
	 * @param newPassenger Passenger to collect
	 * @throws InterruptedException
	 */
	public void pickUpPassenger(Passenger newPassenger) throws InterruptedException
	{
		currentPassenger = newPassenger;
		int pickupTime = (int)(Math.random() * maxSleep);
		Thread.sleep(pickupTime);
	}

	/**
	 * Sleeps the thread for the amount of time returned by the current 
	 * passenger's getTravelTime() function
	 * 
	 * @throws InterruptedException
	 */
	public void driveToDestination() throws InterruptedException {
		int travelTime = currentPassenger.getTravelTime();
		Thread.sleep(travelTime);
	}
	
}
