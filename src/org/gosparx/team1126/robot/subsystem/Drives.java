package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.IO;
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
	private final double DISTANCE_PER_TICK = 0.00689;							// Last Year's Distance Per Tick
	//private static final double DISTANCE_PER_TICK = .031219576995;			// The Formula: (Gear Ratio * Circumference)/ticks
	private static final double STOP_MOTOR_SPEED = 0;							// Speed for the motors when they are stopped
	private static final double rightKI = 0.005 * 50;							// Integral for the right PID
	private static final double rightKP = (1.0 / 50);							// Proportional for the right PID
	private static final double leftKI = 0.005 * 50;							// Integral for the left PID
	private static final double leftKP = (1.0 / 50);							// Proportional for the left PID
	private static final double MAX_SPEED = 30;        							// Maximum speed for the robot
	private static final double X_SENSITIVITY = 1.25;							// Sensitivity in the x-axis for arcade drive
	private static final double JOYSTICK_DEADBAND = .06; 						// Axis for the deadband

	/** Objects */

	private static Drives drives;												// An instance of drives
	private CANTalon rightMotorTop;												// Right CANTalon 1
	private CANTalon rightMotorFront;											// Right CANTalon 2
	private CANTalon rightMotorBack;											// Right CANTalon 3
	private CANTalon leftMotorTop;												// Left CANTalon 1
	private CANTalon leftMotorFront;											// Left CANTalon 2
	private CANTalon leftMotorBack;												// Left CANTalon 3
	private Encoder rightEncoder; 												// Right Encoder
	private Encoder leftEncoder;												// Left Encoder
	private EncoderData rightEncoderData;										// Encoder data for the right encoder
	private EncoderData leftEncoderData;										// Encoder data for the left encoder
	private AHRS gyro; 															// NAVX gyro
	private PID rightPID;														// PID for the right speed and such
	private PID leftPID;														// PID for the left speed and such
	private RobotState currentRobotState; 			    						// The current state of the robot, ex) disabled
	private AutoState currentAutoState;											// The current auto state;

	/** Variables */

	private double rightWantedSpeed;				    						// Wanted speed for the right motor
	private double leftWantedSpeed;												// Wanted speed for the left motor
	private double rightCurrentSpeed;											// Wanted speed for the right motor
	private double leftCurrentSpeed;											// Current speed of the left motor
	private double rightSetPower;												// Power for the right motor
	private double leftSetPower;							    				// Power for the left motor
	private double currentX;													// Starting x value of the robot
	private double currentY;													// Starting y value of the robot
	private double rightPreviousDistance;										// Previous right distance			
	private double leftPreviousDistance;										// Previous left distance
	private double rightCurrentDistance;										// Current right distance			
	private double leftCurrentDistance;											// Current left distance
	private double currentAngle;												// Current angle
	private double averageDistance;												// Average Distance
	private double wantedDistance;												// Wanted Distance
	private double wantedAngle;													// Wanted Angle
	private boolean isInverse;													// If the drives is inverted
	private double wantedSpeed;													// Speed passed in through methods
	private double counter;														// For logging
	
	
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
	public static synchronized Drives getInstance(){
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
		rightMotorTop = new CANTalon(IO.CAN_DRIVES_RIGHT_TOP);
		rightMotorFront = new CANTalon(IO.CAN_DRIVES_RIGHT_FRONT);
		rightMotorBack = new CANTalon(IO.CAN_DRIVES_RIGHT_BACK);									
		rightEncoder = new Encoder(IO.DIO_RIGHT_DRIVES_ENC_A,IO.DIO_RIGHT_DRIVES_ENC_B);
		rightEncoderData = new EncoderData(rightEncoder, DISTANCE_PER_TICK);	
		rightPID = new PID(rightKI, rightKP);
		rightPID.breakMode(true);
		rightCurrentSpeed = 0;
		rightWantedSpeed = 0;
		rightPreviousDistance = 0;
		rightCurrentDistance = 0;

		//Left
		leftMotorTop = new CANTalon(IO.CAN_DRIVES_LEFT_TOP);
		leftMotorTop.setInverted(true);
		leftMotorFront = new CANTalon(IO.CAN_DRIVES_LEFT_FRONT);
		leftMotorFront.setInverted(true);
		leftMotorBack = new CANTalon(IO.CAN_DRIVES_LEFT_BACK);
		leftMotorBack.setInverted(true);
		leftEncoder = new Encoder(IO.DIO_LEFT_DRIVES_ENC_A,IO.DIO_LEFT_DRIVES_ENC_B);
		leftEncoderData = new EncoderData(leftEncoder, -DISTANCE_PER_TICK);		
		leftPID = new PID(leftKI, leftKP);
		leftPID.breakMode(true);
		leftCurrentSpeed = 0;
		leftWantedSpeed = 0;
		leftPreviousDistance = 0;
		leftCurrentDistance = 0;

		//Other
		gyro = new AHRS(SerialPort.Port.kUSB);
		gyro.zeroYaw();
		currentRobotState = RobotState.DISABLED;
		currentAutoState = AutoState.AUTO_STANDBY;
		currentX = 0;
		currentY = 0;
		currentAngle = 0;
		isInverse = false;
		wantedSpeed = 0;
		counter = 0;
		return true;
	}

	/**
	 * Sets up liveWindow to set values during test mode
	 */

	@Override
	protected void liveWindow() {
		String motorName = "Drives Motors";
		String sensorName = "Drives Sensors";
		LiveWindow.addActuator(motorName, "Right Motor 1", rightMotorTop);
		LiveWindow.addActuator(motorName, "Right Motor 2", rightMotorFront);
		LiveWindow.addActuator(motorName, "Right Motor 3", rightMotorBack);
		LiveWindow.addActuator(motorName, "Left Motor 1", leftMotorTop);
		LiveWindow.addActuator(motorName, "Left Motor 2", leftMotorFront);
		LiveWindow.addActuator(motorName, "Left Motor 3", leftMotorBack);
		LiveWindow.addSensor(sensorName, "Right Encoder", rightEncoder);
		LiveWindow.addSensor(sensorName, "Left Encoder", leftEncoder);
		LiveWindow.addSensor(sensorName, "Gyro", gyro);
	}
	
	/**
	 * Continues as long as it returns false
	 */
	@Override
	protected boolean execute() {
		
		dsc.update();
		
		if(dsc.isAutonomous()){
			currentRobotState = RobotState.AUTO;
		}else if(dsc.isDisabled()){
			currentRobotState = RobotState.DISABLED;
		}else{
			currentRobotState = RobotState.TELEOP;
		}
		
		dsc.setAxisDeadband(IO.RIGHT_JOY_Y, JOYSTICK_DEADBAND);
		dsc.setAxisDeadband(IO.LEFT_JOY_Y, JOYSTICK_DEADBAND);
		rightEncoderData.calculateSpeed();
		leftEncoderData.calculateSpeed();
		rightCurrentSpeed = rightEncoderData.getSpeed();
		leftCurrentSpeed = leftEncoderData.getSpeed();
		currentAngle = gyro.getAngle() % 360;
		rightCurrentDistance = rightEncoderData.getDistance();
		leftCurrentDistance = leftEncoderData.getDistance();
		averageDistance = ((rightCurrentDistance - rightPreviousDistance) + (leftCurrentDistance - leftPreviousDistance))/2;
		currentX += Math.sin(Math.toRadians(currentAngle)) * averageDistance;
		currentY += Math.cos(Math.toRadians(currentAngle)) * averageDistance;
		counter++;
		
		switch(currentRobotState){

		case AUTO:
			switch(currentAutoState){
			
			case AUTO_STANDBY:
				break;
				
			case AUTO_DRIVE:
				break;
				
			case AUTO_TURN:
				break;
				
			default:
				LOG.logError("ERROR: Current Auto State is: " + currentAutoState);
			}
			break;

		case TELEOP:
			currentAutoState = AutoState.AUTO_STANDBY;
			
			if(dsc.getButtonRising(IO.INVERT_DRIVES_BUTTON)){
				isInverse = !isInverse;
			}
			
			setTankSpeed(dsc.getAxis(IO.RIGHT_JOY_Y), dsc.getAxis(IO.LEFT_JOY_Y), isInverse);
			if(counter>5){
				LOG.logMessage("Right Y: " + dsc.getAxis(IO.RIGHT_JOY_Y));
				LOG.logMessage("Left Y: " + dsc.getAxis(IO.LEFT_JOY_Y));
				counter = 0;
			}
			//setArcadeSpeed(dsc.getAxis(IO.RIGHT_JOY_X), 						// In case driver wants to use Arcade drive 
			//		dsc.getAxis(IO.RIGHT_JOY_Y), isInverse);					
			//rightSetPower = rightPID.loop(rightCurrentSpeed, rightWantedSpeed);
			//leftSetPower = leftPID.loop(leftCurrentSpeed, leftWantedSpeed);
			rightSetPower = rightWantedSpeed;			        				// In case driver doesn't want PID loop
			leftSetPower = leftWantedSpeed;									// In case driver doesn't want PID loop
			
			rightMotorTop.set(rightSetPower);
			rightMotorFront.set(rightSetPower);
			rightMotorBack.set(rightSetPower);
			leftMotorTop.set(leftSetPower);
			leftMotorFront.set(leftSetPower);
			leftMotorBack.set(leftSetPower);
			
			break;

		case DISABLED:
			
			rightMotorTop.set(STOP_MOTOR_SPEED);
			rightMotorFront.set(STOP_MOTOR_SPEED);
			rightMotorBack.set(STOP_MOTOR_SPEED);
			leftMotorTop.set(STOP_MOTOR_SPEED);
			leftMotorFront.set(STOP_MOTOR_SPEED);
			leftMotorBack.set(STOP_MOTOR_SPEED);
			rightEncoder.reset();
			rightEncoderData.reset();
			leftEncoder.reset();
			leftEncoderData.reset();
			gyro.zeroYaw();
			
			break;

		default:
			LOG.logError("ERROR: Current Robot State is: " + currentRobotState);
		}
		
		rightPreviousDistance = rightCurrentDistance;
		leftPreviousDistance = leftCurrentDistance;
		
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
//		LOG.logMessage("Current Speeds (Right,Left): (" + rightCurrentSpeed + "," + leftCurrentSpeed + ")");
//		LOG.logMessage("Wanted Speeds (Right,Left): (" + rightWantedSpeed + "," + leftWantedSpeed + ")");
//		LOG.logMessage("Set Powers (Right,Left): (" + rightSetPower + "," + leftSetPower + ")");
//		LOG.logMessage("Current Angle: " + currentAngle);
//		LOG.logMessage("Wanted Angle: " + wantedAngle);
//		LOG.logMessage("Previous Distances (Right,Left): (" + rightPreviousDistance + "," + leftCurrentDistance + ")");
//		LOG.logMessage("Current Distances (Right, Left): (" + rightCurrentDistance + "," + leftCurrentDistance + ")");
//		LOG.logMessage("Current Robot State: " + currentRobotState);
//		LOG.logMessage("Current Auto State: " + currentAutoState);
//		LOG.logMessage("Current Position (x,y): (" + currentX + "," + currentY + ")");
//		if(isInverse){
//			LOG.logMessage("The drives are inverted");
//		}else{
//			LOG.logMessage("The drives are not inverted");
//		}
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
			rightWantedSpeed = -(left * MAX_SPEED);
			leftWantedSpeed = -(right * MAX_SPEED);
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
			rightWantedSpeed = -((yAxis - xAxis/X_SENSITIVITY) * MAX_SPEED);
			leftWantedSpeed = -((yAxis + xAxis/X_SENSITIVITY) * MAX_SPEED);
		}
	}
	
	/**
	 * Drives the robot in auto
	 * @param distance the distance the robot will go 
	 * @param speed the speed at which the robot should drive
	 * @return true if the robot has reached its destination, false otherwise
	 */
	public void autoDrive(double distance, double speed){
		rightEncoderData.reset();
		leftEncoderData.reset();
		wantedDistance = distance;
		wantedSpeed = speed;
		currentAutoState = AutoState.AUTO_DRIVE;
	}
	
	/**
	 * Turns the robot in auto
	 * @param angle the angle the robot needs to turn
	 * @param speed the speed at which the robot should turn
	 * @return true if the robot has turned to the angle, false otherwise
	 */
//	public boolean autoTurn(double angle, double speed){
//		double offset = angle - currentAngle;
//
//		if(Math.abs(offset)<3){
//			rightWantedSpeed = 0;
//			leftWantedSpeed = 0;
//			return true;
//		}
//		if((offset>=0 && offset<=180) || (offset<=-180 && offset>=-360)){
//			rightWantedSpeed = -speed;
//			leftWantedSpeed = speed;
//		}else{
//			rightWantedSpeed = speed;
//			leftWantedSpeed = -speed;
//		}
//		return false;
//	}
	
	/**
	 * Calculates the wanted angle and wanted distance to travel to a coordinate
	 * @param newX the x value of the position we're driving to
	 * @param newY the y value of the position we're driving to 
	 */
	public void trig(double newX, double newY){
		double xChange, yChange;
		xChange = newX - currentX;
		yChange = newY - currentY;
		wantedDistance = Math.sqrt((xChange * xChange) + (yChange * yChange));
		wantedAngle = Math.atan(xChange/yChange);
	}
	
	/**
	 * Moves the robot to a specific coordinate
	 * @param xValue the x the robot needs to travel to 
	 * @param yValue the y value the robot needs to travel to
	 * @param speed the speed at which the robot needs to travel
	 * @return true if the robot has made it to the coordinate, false otherwise
	 */
	public boolean travelToCoordinate(double xValue, double yValue, double speed){
		trig(xValue,yValue);
		//autoTurn(wantedAngle, speed);
		autoDrive(wantedDistance, speed);
		return true;
	}
	
	/**
	 * Allows auto to set the starting coordinate of the robot
	 * @param x the x value the robot starts in 
	 * @param y the y value the robot starts in
	 */
	public void setStartingCoordinate(double x, double y){
		currentX = x;
		currentY = y;
	}

	/**
	 * Enables the Drives to know if the robot is disabled, in auto, or if it's in teleop
	 */
	public enum RobotState{
		AUTO,
		TELEOP,
		DISABLED;

		/**
		 * Gets the name of the robot state
		 * @return the correct robot state 
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
	
	/**
	 * Enables the Drives to know if the robot is disabled, in auto, or if it's in teleop
	 */
	public enum AutoState{
		AUTO_STANDBY,
		AUTO_DRIVE,
		AUTO_TURN;

		/**
		 * Gets the name of the robot state
		 * @return the correct robot state 
		 */
		@Override
		public String toString(){
			switch(this){
			case AUTO_STANDBY:
				return "Auto Standby";
			case AUTO_DRIVE:
				return "Auto Drive";
			case AUTO_TURN:
				return "Auto Turn";
			default:
				return "Error :(";
			}
		}
	}
}
