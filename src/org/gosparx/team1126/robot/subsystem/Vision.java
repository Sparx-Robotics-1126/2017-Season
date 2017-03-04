package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.util.VisionNetworkTable;
import org.gosparx.team1126.robot.util.DriverStationControls;
import org.gosparx.team1126.robot.util.SharedData;

import edu.wpi.first.wpilibj.DigitalOutput;

public class Vision extends GenericSubsystem {

	private static Vision vision;												// An instance of drives
	protected VisionNetworkTable visionSystem;
	private DigitalOutput reset;												// Reset Jetson board on bootup
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
		startTime = System.currentTimeMillis();
		target = dsc.sharedData.targetType;
		return true;
	}

	/**
	 * Continues as long as it returns false
	 */
	@Override
	protected boolean execute() {
		if (System.currentTimeMillis() - startTime > 5000)						// Boot the Jetson board
			reset.set(true);
		
		if (dsc.sharedData.targetType != target){
			visionSystem.serverUpdate();
			target = dsc.sharedData.targetType;
		}
		
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
