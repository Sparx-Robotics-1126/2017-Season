package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.sensors.EncoderData;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Encoder;

/**
 * This class controls the drives system of the 2017 in tank drive 
 * @author Allison Morgan
 */
public class Drives extends GenericSubsystem {
	
//INSTANCES
	
	/**
	 * an instance of drives
	 */
	private static Drives drives;
	
//MOTOR CONTROLLERS
	
	/**
	 * the right CAN Talon
	 */
	private static CANTalon rightTalon;
	
	/**
	 * the left CAN Talon
	 */
	private static CANTalon leftTalon;
	
//ENCODERS & ENCODER DATAS
	
	/**
	 * the right encoder
	 */
	private static Encoder rightEncoder;
	
	/**
	 * the left encoder
	 */
	private static Encoder leftEncoder;
	
	/**
	 * the encoder data for the right encoder
	 */
	private static EncoderData rightEncoderData;
	
	/**
	 * the encoder data for the left encoder
	 */
	private static EncoderData leftEncoderData;
	
//METHODS	
	
	/**
	 * Constructors a drives object with normal priority
	 */
	private Drives(){
		super("Drives",Thread.NORM_PRIORITY);
	}
	
	/**
	 * ensures that there is only one instance of drives
	 * @return the instance of drives 
	 */
	public static synchronized Drives getInstanced(){
		if(drives == null){
			drives = new Drives();
		}
		return drives;
	}

	/**
	 * Instantiates all the objects and initializes the variables  
	 * @return true if it runs once, false if it continues, should return true
	 */
	@Override
	protected boolean init(){
		rightTalon = new CANTalon(8);
		leftTalon = new CANTalon(7);
		return true;
	}

	/**
	 * Sets up liveWindow to set values during test mode
	 */
	@Override
	protected void liveWindow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean execute() {
		// TODO Auto-generated method stub
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
	 * Writes logs to the console every 5 seconds
	 */
	@Override
	protected void writeLog() {
		// TODO Auto-generated method stub
		
	}
}
