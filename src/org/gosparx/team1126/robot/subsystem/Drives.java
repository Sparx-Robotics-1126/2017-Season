package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.sensors.PID;
import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * This class controls the drives system of the 2017 in tank drive 
 * @author Allison Morgan
 */
public class Drives extends GenericSubsystem {
	
/** Constants */
	
	// TODO : Calculate DISTANCE_PER_TICK, KI, KP, MAX_SPEED for 2017 Robot
	private static final double DISTANCE_PER_TICK = 5;  // The Formula:
	private static final double STOP_MOTOR_SPEED = 0;   // The Speed for the motors when they are stopped
	private static final double KI = 0.005 * 50;        // The integral for the PID
	private static final double KP = (1.0 / 50); 		// The proportional for the PID
	private static final double MAX_SPEED = 50;         // Maximum speed for the robot
	private static final double X_SENSITIVITY = 1.25;   // The Sensitivity in the x-axis for arcade drive
	
/** Objects */
	
	private static Drives drives;                       // An instance of drives
	private static CANTalon rightMotor;					// Right CANTalon
	private static CANTalon leftMotor;					// Left CANTalon
	private static Encoder rightEncoder; 				// Right Encoder
	private static Encoder leftEncoder;					// Left Encoder
	private static EncoderData rightEncoderData;		// Encoder data for the right encoder
	private static EncoderData leftEncoderData;			// Encoder data for the left encoder
	private static AHRS gyro; 							// NAVX gyro
	private static PID rightPID;						// PID for the right speed and such
	private static PID leftPID;							// PID for the left speed and such

/** Variables */
	
	private static double rightWantedSpeed;				// The wanted speed for the right motor
	private static double leftWantedSpeed;				// The wanted speed for the left motor
	private static double rightCurrentSpeed;			// The wanted speed for the right motor
	private static double leftCurrentSpeed;				// The current speed of the left motor
	private static double rightSetPower;				// The power we'll give the right motor
	private static double leftSetPower;					// The power we'll give the left motor
	
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
		//Right
		rightMotor = new CANTalon(8);
		rightEncoder = new Encoder(3,6);
		rightEncoderData = new EncoderData(rightEncoder, DISTANCE_PER_TICK);
		rightPID = new PID(KI, KP);
		rightPID.breakMode(true);
		rightCurrentSpeed = 0;
		rightWantedSpeed = 0;
		
		//Left
		leftMotor = new CANTalon(7);
		leftEncoder = new Encoder(5,9);
		leftEncoderData = new EncoderData(leftEncoder, DISTANCE_PER_TICK);
		leftPID = new PID(KI, KP);
		leftPID.breakMode(true);
		leftCurrentSpeed = 0;
		leftWantedSpeed = 0;
		
		//Other
		gyro = new AHRS(SerialPort.Port.kUSB);
		
		return true;
	}

	/**
	 * Sets up liveWindow to set values during test mode
	 */
	
	@Override
	protected void liveWindow() {
		String motorName = "Drives Motors";
		String sensorName = "Drives Sensors";
		LiveWindow.addActuator(motorName, "Right Motor", rightMotor);
		LiveWindow.addActuator(motorName, "Left Motor", leftMotor);
		LiveWindow.addSensor(sensorName, "Right Encoder", rightEncoder);
		LiveWindow.addSensor(sensorName, "Left Encoder", leftEncoder);
		LiveWindow.addSensor(sensorName, "Gyro", gyro);
	}

	@Override
	protected boolean execute() {
		rightEncoderData.calculateSpeed();
		leftEncoderData.calculateSpeed();
		rightCurrentSpeed = rightEncoderData.getSpeed();
		leftCurrentSpeed = leftEncoderData.getSpeed();
		rightSetPower = rightPID.loop(rightCurrentSpeed, rightWantedSpeed);
		leftSetPower = leftPID.loop(leftCurrentSpeed, leftWantedSpeed);
		rightMotor.set(rightSetPower);
		leftMotor.set(leftSetPower);
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
		LOG.logMessage("Current Speeds (Right,Left): (" + rightCurrentSpeed + "," + leftCurrentSpeed + ")");
		LOG.logMessage("Wanted Speeds (Right,Left): (" + rightWantedSpeed + "," + leftWantedSpeed + ")");
		LOG.logMessage("Set Powers (Right,Left): (" + rightSetPower + "," + leftSetPower + ")");
	}
	
	/**
	 * sets the speed based on the power of the joysticks for tank drive
	 * @param right the power from the right joystick
	 * @param left the power from the left joystick
	 */
	public void setTankSpeed(double right, double left){
		rightWantedSpeed = right * MAX_SPEED;
		leftWantedSpeed = left * MAX_SPEED;
	}
	
	/**
	 * sets the speed based on the power of the joystick for arcade dribe
	 * @param xAxis value from the xAxis on the joystick
	 * @param yAxis value from the yAxis on the joystick
	 */
	public void setArcadeSpeed(double xAxis, double yAxis){
		rightWantedSpeed = (yAxis + xAxis/X_SENSITIVITY) * MAX_SPEED;
		leftWantedSpeed = (yAxis - xAxis/X_SENSITIVITY) * MAX_SPEED;
	}
	
	public enum MatchState{
		AUTO,
		TELEOP;
		
		/**
		 * Gets the name of the state
		 * @return the correct state 
		 */
		@Override
		public String toString(){
			switch(this){
			case AUTO:
				return "Auto";
			case TELEOP:
				return "Teleop";
			default:
				return "Error :(";
			}
		}
	}
	
}
