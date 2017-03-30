package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.util.VisionNetworkTable;
import org.gosparx.team1126.robot.util.SharedData;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Relay;

public class Vision extends GenericSubsystem {

	private static Vision vision;												// An instance of drives
	protected VisionNetworkTable visionSystem;
	private DigitalOutput reset;												// Reset Jetson board on bootup
	private Relay led;															// LED Control
	private long startTime;
	private SharedData.Target target;

	
	/**
	 * Constructors a drives object with normal priority
	 */
	private Vision(){
		super("Vision",Thread.NORM_PRIORITY);
	}

	/**
	 * ensures that there is only one instance of drives		
	 * @return the instance of drives 
	 */
	public static synchronized Vision getInstance(){
		if(vision == null){
			vision = new Vision();
		}
		return vision;											
	}

	/**
	 * Instantiates all the objects and initializes the variables  
	 * @return true if it runs once, false if it continues, should return true
	 */
	@Override
	protected boolean init(){
		visionSystem = new VisionNetworkTable();
		reset = new DigitalOutput(IO.DIO_JETSON_RESET);
		reset.set(false);														// Reset the Jetson board
		led = new Relay(0, Relay.Direction.kForward); 
		led.set(Relay.Value.kOff);	//todo change to off											// Turn off the LED
		startTime = System.currentTimeMillis();
		target = SharedData.Target.NONE;											// Get Initial state of target
		return true;  
	}

	/**
	 * Continues as long as it returns false
	 */
	@Override
	protected boolean execute()
	{	
		//LOG.logMessage("YOUR ANGLE IS angle: " + SharedData.angleToBoiler);
		
		if (System.currentTimeMillis() - startTime > 5000)						// Boot the Jetson board
			reset.set(true);
		
		if (dsc.isOperatorControl())										// When in operator control, 
		{
			if(dsc.isPressed(IO.FLIP_SHOOTING_SYSTEM_ON) && !dsc.isPressed(IO.FLIP_BOILER_SHOT)){
				SharedData.targetType = SharedData.Target.BOILER;
			}else{
				SharedData.targetType = SharedData.Target.NONE;
			}
		}
		
		if (SharedData.targetType != target)									// Check for a change in target
		{
			led.set((SharedData.targetType == SharedData.Target.NONE) ? 						// Update LED status
					Relay.Value.kOff : Relay.Value.kOn);

			visionSystem.serverUpdate();										// Target change can occur from the		
			target = SharedData.targetType;										//  code above, or Autonomous
			
			if (target != SharedData.Target.BOILER)
			{
				dsc.sharedData.clearImageData(SharedData.Target.BOILER);
			}

			if (target != SharedData.Target.LIFT)
			{
				dsc.sharedData.clearImageData(SharedData.Target.LIFT);
			}
		}
		
//		led.set(Relay.Value.kOn);   //TODO take out 
		
		return false;
	}
	
	/**
	 * the time the system "sleeps" until it is called again
	 * @return the time in milliseconds
	 */
	@Override
	protected long sleepTime() {
		return 20;
	}
	
	/**
	 * Sets up liveWindow to set values during test mode
	 */
	@Override
	protected void liveWindow() {
	}

	/**
	 * Writes logs to the console every 5 seconds
	 */
	@Override
	protected void writeLog() {
	}
}
