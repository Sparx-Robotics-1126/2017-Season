package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.sensors.PID;
import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * This class controls the drives system of the 2017 in tank or arcade drive 
 * @author Allison Morgan
 */
public class Drives extends GenericSubsystem {

	/** Constants */

	// TODO : Calculate KI, KP, MAX_SPEED for 2017 Robot
	private static final double DISTANCE_PER_TICK = .031219576995;				// The Formula: (Gear Ratio * Circumference)/ticks
	private static final double STOP_MOTOR_SPEED = 0;							// Speed for the motors when they are stopped
	private static final double rightKI = 0.005 * 50;							// Integral for the right PID
	private static final double rightKP = (1.0 / 50);							// Proportional for the right PID
	private static final double leftKI = 0.005 * 50;							// Integral for the left PID
	private static final double leftKP = (1.0 / 50);							// Proportional for the left PID
	private static final double MAX_SPEED = 888;        						// Maximum speed for the robot
	private static final double X_SENSITIVITY = 1.25;							// Sensitivity in the x-axis for arcade drive

	/** Objects */

	private static Drives drives;												// An instance of drives
	private CANTalon rightMotor;												// Right CANTalon
	private CANTalon leftMotor;													// Left CANTalon
	private Encoder rightEncoder; 												// Right Encoder
	private Encoder leftEncoder;												// Left Encoder
	private EncoderData rightEncoderData;										// Encoder data for the right encoder
	private EncoderData leftEncoderData;										// Encoder data for the left encoder
	private AHRS gyro; 															// NAVX gyro
	private PID rightPID;														// PID for the right speed and such
	private PID leftPID;														// PID for the left speed and such
	private RobotState currentRobotState; 			    						// The current state of the robot, 
																				// ex) disabled, auto

	/** Variables */

	private double rightWantedSpeed;				    						// Wanted speed for the right motor
	private double leftWantedSpeed;												// Wanted speed for the left motor
	private double rightCurrentSpeed;											// Wanted speed for the right motor
	private double leftCurrentSpeed;											// Current speed of the left motor
	private double rightSetPower;												// Power for the right motor
	private double leftSetPower;							    				// Power for the left motor
	private double startingX;													// Starting x value of the robot
	private double startingY;													// Starting y value of the robot
	
	
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
		rightMotor = new CANTalon(888);
																				// might need to invert motor
		rightEncoder = new Encoder(888,888);
		rightEncoderData = new EncoderData(rightEncoder, DISTANCE_PER_TICK);	// might need a negative distance per tick
		rightPID = new PID(rightKI, rightKP);
		rightPID.breakMode(true);
		rightCurrentSpeed = 0;
		rightWantedSpeed = 0;

		//Left
		leftMotor = new CANTalon(888);
																				// might need to invert motor
		leftEncoder = new Encoder(888,888);
		leftEncoderData = new EncoderData(leftEncoder, DISTANCE_PER_TICK);		// might need a negative distance per tick
		leftPID = new PID(leftKI, leftKP);
		leftPID.breakMode(true);
		leftCurrentSpeed = 0;
		leftWantedSpeed = 0;

		//Other
		gyro = new AHRS(SerialPort.Port.kUSB);
		currentRobotState = RobotState.DISABLED;
		startingX = 0;
		startingY = 0;
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
		if(dsc.isAutonomous()){
			currentRobotState = RobotState.AUTO;
		}else if(dsc.isDisabled()){
			currentRobotState = RobotState.DISABLED;
		}else{
			currentRobotState = RobotState.TELEOP;
		}
		rightEncoderData.calculateSpeed();
		leftEncoderData.calculateSpeed();
		rightCurrentSpeed = rightEncoderData.getSpeed();
		leftCurrentSpeed = leftEncoderData.getSpeed();
		switch(currentRobotState){

		case AUTO:
			break;

		case TELEOP:
			rightSetPower = rightPID.loop(rightCurrentSpeed, rightWantedSpeed);
			leftSetPower = leftPID.loop(leftCurrentSpeed, leftWantedSpeed);
			//rightSetPower = rightWantedSpeed;			        				// In case driver doesn't want PID loop
			//leftSetPower = leftWantedSpeed;									// In case driver doesn't want PID loop
			rightMotor.set(rightSetPower);
			leftMotor.set(leftSetPower);
			break;

		case DISABLED:
			rightMotor.set(STOP_MOTOR_SPEED);
			leftMotor.set(STOP_MOTOR_SPEED);
			break;

		default:
			LOG.logError("ERROR: Current Robot State is: " + currentRobotState);
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
	public void setTankSpeed(double right, double left, boolean isInverted){
		if(!isInverted){
			rightWantedSpeed = right * MAX_SPEED;
			leftWantedSpeed = left * MAX_SPEED;
		}else{
			rightWantedSpeed = -(right * MAX_SPEED);
			leftWantedSpeed = -(left * MAX_SPEED);
		}
	}

	/**
	 * sets the speed based on the power of the joystick for arcade dribe
	 * @param xAxis value from the xAxis on the joystick
	 * @param yAxis value from the yAxis on the joystick
	 */
	public void setArcadeSpeed(double xAxis, double yAxis, boolean isInverted){
		if(!isInverted){
			rightWantedSpeed = (yAxis + xAxis/X_SENSITIVITY) * MAX_SPEED;
			leftWantedSpeed = (yAxis - xAxis/X_SENSITIVITY) * MAX_SPEED;
		}else{
			rightWantedSpeed = -((yAxis + xAxis/X_SENSITIVITY) * MAX_SPEED);
			leftWantedSpeed = -((yAxis - xAxis/X_SENSITIVITY) * MAX_SPEED);
		}
	}
	
	/**
	 * Drives the robot in auto
	 * @param distance the distance the robot will go 
	 * @param speed the speed at which the robot should drive
	 * @return true if the robot has reached its destination, false otherwise
	 */
	public boolean autoDrive(double distance, double speed){
		return false;
	}
	
	/**
	 * Turns the robot in auto
	 * @param angle the angle the robot needs to turn
	 * @param speed the speed at which the robot should turn
	 * @return true if the robot has turned to the angle, false otherwise
	 */
	public boolean autoTurn(double angle, double speed){
		return false;
	}
	
	/**
	 * Moves the robot to a specific coordinate
	 * @param xValue the x the robot needs to travel to 
	 * @param yValue the y value the robot needs to travel to
	 * @param speed the speed at which the robot needs to travel
	 * @return true if the robot has made it to the coordinate, false otherwise
	 */
	public boolean travelToCoordinate(double xValue, double yValue, double speed){
		return false;
	}
	
	/**
	 * Allows auto to set the starting coordinate of the robot
	 * @param x the x value the robot starts in 
	 * @param y the y value the robot starts in
	 */
	public void setStartingCoordinate(double x, double y){
		startingX = x;
		startingY = y;
	}

	public enum RobotState{
		AUTO,
		TELEOP,
		DISABLED;

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
			case DISABLED:
				return "Disabled";
			default:
				return "Error :(";
			}
		}
	}
}
