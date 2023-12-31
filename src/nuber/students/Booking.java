package nuber.students;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Booking represents the overall "job" for a passenger getting to their destination.
 * 
 * It begins with a passenger, and when the booking is commenced by the region 
 * responsible for it, an available driver is allocated from dispatch. If no driver is 
 * available, the booking must wait until one is. When the passenger arrives at the destination,
 * a BookingResult object is provided with the overall information for the booking.
 * 
 * The Booking must track how long it takes, from the instant it is created, to when the 
 * passenger arrives at their destination. This should be done using Date class' getTime().
 * 
 * Booking's should have a globally unique, sequential ID, allocated on their creation. 
 * This should be multi-thread friendly, allowing bookings to be created from different threads.
 * 
 * @author james
 *
 */
public class Booking {
	
	static AtomicInteger globalJobID = new AtomicInteger(0);
	
	private int jobID;
	private NuberDispatch dispatch;
	private Passenger passenger;
	private Driver driver;

	/**
	 * Creates a new booking for a given Nuber dispatch and passenger, noting that no
	 * driver is provided as it will depend on whether one is available when the region 
	 * can begin processing this booking.
	 * 
	 * @param dispatch
	 * @param passenger
	 */
	public Booking(NuberDispatch dispatch, Passenger passenger)
	{
		this.jobID = globalJobID.incrementAndGet();
		this.dispatch = dispatch;
		this.passenger = passenger;
	}
	
	/**
	 * At some point, the Nuber Region responsible for the booking can start it (has free spot),
	 * and calls the Booking.call() function, which:
	 * 1.	Asks Dispatch for an available driver
	 * 2.	If no driver is currently available, the booking must wait until one is available. 
	 * 3.	Once it has a driver, it must call the Driver.pickUpPassenger() function, with the 
	 * 			thread pausing whilst as function is called.
	 * 4.	It must then call the Driver.driveToDestination() function, with the thread pausing 
	 * 			whilst as function is called.
	 * 5.	Once at the destination, the time is recorded, so we know the total trip duration. 
	 * 6.	The driver, now free, is added back into Dispatches list of available drivers. 
	 * 7.	The call() function the returns a BookingResult object, passing in the appropriate 
	 * 			information required in the BookingResult constructor.
	 *
	 * @return A BookingResult containing the final information about the booking 
	 */
	public BookingResult call() {
		// step 1 & 2
		dispatch.logEvent(this, "starts, asks for driver");
		driver = dispatch.getDriver();
		dispatch.logEvent(this, "has a driver");
		
		
		long startTime = new Date().getTime();
		long duration = 0;
		try {
			// step 3
			dispatch.logEvent(this, "is picking up passenger");
			driver.pickUpPassenger(passenger);
			dispatch.logEvent(this, "has picked up passenger");
			
			// step 4
			dispatch.logEvent(this, "is traveling");
			driver.driveToDestination();
			
			// step 5
			long endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			dispatch.logEvent(this, "is at destination, using " + duration + " ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// step 6
		dispatch.logEvent(this, "driver finished task and is idle now");
		dispatch.addDriver(driver);
		
		// step 7
		return new BookingResult(jobID, passenger, driver, duration);
	}
	
	/***
	 * Should return the:
	 * - booking ID, 
	 * - followed by a colon, 
	 * - followed by the driver's name (if the driver is null, it should show the word "null")
	 * - followed by a colon, 
	 * - followed by the passenger's name (if the passenger is null, it should show the word "null")
	 * 
	 * @return The compiled string
	 */
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(jobID)
					 .append(":")
					 .append(driver == null ? "null" : driver.name)
					 .append(":")
					 .append(passenger == null ? "null" : passenger.name);
		return stringBuilder.toString();
	}

}
