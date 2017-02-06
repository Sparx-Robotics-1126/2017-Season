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
	private static final double STOP_MOTOR_POWER_SPEED = 0;						// Speed for the motors when they are stopped
	private static final double MAX_SPEED = 60;        							// Maximum speed for the robot
	private static final double HOLDING_DRIVE_SPEED = 30;						// Speed for driving while in hold state
	private static final double HOLDING_TURN_SPEED = 30;						// Speed for turning while in hold state
	private static final double CHECK_POWER = .1;								// Power for diagnostics
	private static final double rightKI = 0.005 * 50;							// Integral for the right PID
	private static final double rightKP = (1.0 / 50);							// Proportional for the right PID
	private static final double leftKI = 0.005 * 50;							// Integral for the left PID
	private static final double leftKP = (1.0 / 50);							// Proportional for the left PID
	private static final double X_SENSITIVITY = 1.25;							// Sensitivity in the x-axis for arcade drive
	private static final double JOYSTICK_DEADBAND = .06; 						// Axis for the deadband
	
	/** Right */
	
	private CANTalon rightMotorTop;												// Right CANTalon 1
	private CANTalon rightMotorFront;											// Right CANTalon 2
	private CANTalon rightMotorBack;											// Right CANTalon 3
	private Encoder rightEncoder; 												// Right Encoder
	private EncoderData rightEncoderData;										// Encoder data for the right encoder
	private PID rightPID;														// PID for the right speed and such
	private double rightCurrentSpeed;											// Wanted speed for the right motor
	private double rightWantedSpeed;				    						// Wanted speed for the right motor
	private double rightPreviousDistance;										// Previous right distance			
	private double rightCurrentDistance;										// Current right distance	
	private double rightSetPower;												// Power for the right motor

	/** Left */
	
	private CANTalon leftMotorTop;												// Left CANTalon 1
	private CANTalon leftMotorFront;											// Left CANTalon 2
	private CANTalon leftMotorBack;												// Left CANTalon 3
	private Encoder leftEncoder;												// Left Encoder
	private EncoderData leftEncoderData;										// Encoder data for the left encoder
	private PID leftPID;														// PID for the left speed and such
	private double leftCurrentSpeed;											// Current speed of the left motor
	private double leftWantedSpeed;												// Wanted speed for the left motor
	private double leftPreviousDistance;										// Previous left distance
	private double leftCurrentDistance;											// Current left distance
	private double leftSetPower;							    				// Power for the left motor
	
	/** Other */
	
	private static Drives drives;												// An instance of drives
	private DriveState currentDriveState;										// The current state
	private AHRS gyro; 															// NAVX gyro
	private double currentX;													// Starting x value of the robot
	private double currentY;													// Starting y value of the robot
	private double previousX;													// Previous X value
	private double previousY;													// Previous Y value
	private double currentAngle;												// Current angle
	private boolean isInverse;													// If the drives is inverted
	private double wantedSpeed;													// Speed passed in through methods
	private double wantedDistance;												// Wanted Distance
	private double wantedAngle;													// Wanted Angle
	private double angleOffset;													// Offset of the angle
	private boolean turnDone;													// True if auto turn is done
	private boolean driveDone;													// True if auto drive is done
	private double averageSpeed;												// Average Speed
	private double averageDistance;												// Average Distance
	private double previousAngle;												// Previous Angle
	
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
		rightSetPower = 0;

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
		leftSetPower = 0;

		//Other
		currentDriveState = DriveState.STANDBY;
		gyro = new AHRS(SerialPort.Port.kUSB);
		gyro.zeroYaw();
		currentX = 0;
		currentY = 0;
		previousX = 0;
		previousY = 0;
		currentAngle = 0;
		isInverse = false;
		wantedSpeed = 0;
		wantedDistance = 0;
		wantedAngle = 0;
		angleOffset = 0;
		turnDone = false;
		driveDone = false;
		averageSpeed = 0;
		averageDistance = 0;
		previousAngle = 0;
		
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
		dsc.setAxisDeadband(IO.RIGHT_JOY_Y, JOYSTICK_DEADBAND);
		dsc.setAxisDeadband(IO.LEFT_JOY_Y, JOYSTICK_DEADBAND);
		rightEncoderData.calculateSpeed();
		leftEncoderData.calculateSpeed();
		rightCurrentSpeed = rightEncoderData.getSpeed();
		leftCurrentSpeed = leftEncoderData.getSpeed();
		averageSpeed = (rightCurrentSpeed + leftCurrentSpeed) / 2;
		currentAngle = gyro.getAngle() % 360;
		rightCurrentDistance = rightEncoderData.getDistance();
		leftCurrentDistance = leftEncoderData.getDistance();
		averageDistance = ((rightCurrentDistance - rightPreviousDistance) + (leftCurrentDistance - leftPreviousDistance))/2;
		currentX += Math.sin(Math.toRadians(currentAngle)) * averageDistance;
		currentY += Math.cos(Math.toRadians(currentAngle)) * averageDistance;
		
		switch(currentDriveState){
			
		case STANDBY:
			if(dsc.isDisabled()){
				currentDriveState = DriveState.DISABLED;
			}else if(dsc.isOperatorControl()){
				currentDriveState = DriveState.TELEOP;
			}
			break;
			
		case AUTO_DRIVE:
			drive();
			break;
			
		case AUTO_TURN:
			turn();
			break;
			
		case AUTO_HOLD:
			hold();
			break;
			
		case AUTO_ABORT:
			abort();
			currentDriveState = DriveState.STANDBY;
			break;
			
		case AUTO_STOP:
			if(stopDrives()){
				currentDriveState = DriveState.STANDBY;
			}
			break;
			
		case TELEOP:
			if(dsc.getButtonRising(IO.INVERT_DRIVES_BUTTON)){
				isInverse = !isInverse;
			}
			if(dsc.getButtonRising(IO.ABORT_AUTO_DRIVES)){
				abortAuto();
			}
			if(dsc.getButtonRising(IO.HOLD_DRIVES)){
				holdDrives();
			}
			if(dsc.runDiagnostics()){
				diagnostics();
			}
			
			setTankSpeed(dsc.getAxis(IO.RIGHT_JOY_Y), dsc.getAxis(IO.LEFT_JOY_Y), isInverse);
			//setArcadeSpeed(dsc.getAxis(IO.RIGHT_JOY_X), 								// In case driver wants to use Arcade drive 
			//		dsc.getAxis(IO.RIGHT_JOY_Y), isInverse);					
			
			break;
			
		case DISABLED:
			rightEncoder.reset();
			rightEncoderData.reset();
			leftEncoder.reset();
			leftEncoderData.reset();
			gyro.zeroYaw();
			currentDriveState = DriveState.AUTO_STOP;
			break;
			
		default:
			LOG.logError("Error :( Current Drive State: " + currentDriveState);
		}
	
		//rightSetPower = rightPID.loop(rightCurrentSpeed, rightWantedSpeed);
		//leftSetPower = leftPID.loop(leftCurrentSpeed, leftWantedSpeed);
		rightSetPower = rightWantedSpeed/MAX_SPEED;			        			// In case driver doesn't want PID loop
		leftSetPower = leftWantedSpeed/MAX_SPEED;								// In case driver doesn't want PID loop
		
		rightMotorTop.set(rightSetPower);
		rightMotorFront.set(rightSetPower);
		rightMotorBack.set(rightSetPower);
		leftMotorTop.set(leftSetPower);
		leftMotorFront.set(leftSetPower);
		leftMotorBack.set(leftSetPower);
		
		rightPreviousDistance = rightCurrentDistance;
		leftPreviousDistance = leftCurrentDistance;
		previousX = currentX;
		previousY = currentY;
		previousAngle = currentAngle;
		
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
		LOG.logMessage(0, 10, "Current Speeds (Right,Left): (" + rightCurrentSpeed + "," + leftCurrentSpeed + ")");
		LOG.logMessage(1, 10, "Wanted Speeds (Right,Left): (" + rightWantedSpeed + "," + leftWantedSpeed + ")");
		LOG.logMessage(2, 10, "Set Powers (Right,Left): (" + rightSetPower + "," + leftSetPower + ")");
		LOG.logMessage(3, 10, "Current Angle: " + currentAngle);
		LOG.logMessage(4, 10, "Wanted Angle: " + wantedAngle);
		LOG.logMessage(5, 10, "Previous Distances (Right,Left): (" + rightPreviousDistance + "," + leftCurrentDistance + ")");
		LOG.logMessage(6, 10, "Current Distances (Right, Left): (" + rightCurrentDistance + "," + leftCurrentDistance + ")");
		LOG.logMessage(8, 10, "Current Drive State: " + currentDriveState);
		LOG.logMessage(9, 10, "Current Position (x,y): (" + currentX + "," + currentY + ")");
		if(isInverse){
			LOG.logMessage(10, 10, "The drives are inverted");
		}else{
			LOG.logMessage(11, 10, "The drives are not inverted");
		}
		LOG.logMessage(12, 5, "Right Y: " + dsc.getAxis(IO.RIGHT_JOY_Y));
		LOG.logMessage(13, 5, "Left Y: " + dsc.getAxis(IO.LEFT_JOY_Y));
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
	 * @param distance the distance the robot will go, if distance > 0 then forward
	 * if distance < 0 then backward 
	 * @param speed the speed at which the robot should drive
	 * @return true if the robot has reached its destination, false otherwise
	 */
	public void autoDrive(double distance, double speed){
		driveDone = false;
		rightEncoderData.reset();
		leftEncoderData.reset();
		wantedDistance = distance;
		if(distance < 0){
			wantedSpeed = -speed;
		}else{
			wantedSpeed = speed;
		}
		currentDriveState = DriveState.AUTO_DRIVE;
		if(!currentDriveState.equals(DriveState.AUTO_DRIVE)){
			driveDone = true;
		}else{
			driveDone = false;
		}
	}
	
	/**
	 * Turns the robot in auto
	 * @param angle the angle the robot needs to turn
	 * @param speed the speed at which the robot should turn
	 * @return true if the robot has turned to the angle, false otherwise
	 */
	public void autoTurn(double angle, double speed){
		turnDone = false;
		gyro.zeroYaw();
		rightEncoderData.reset();
		leftEncoderData.reset();
		wantedAngle = angle;
		wantedSpeed = speed;
		currentDriveState = DriveState.AUTO_TURN;
		if(!currentDriveState.equals(DriveState.AUTO_TURN)){
			turnDone = true;
		}else{
			turnDone = false;
		}
	}
	
	/**
	 * aborts the current auto function
	 */
	public void abortAuto(){
		currentDriveState = DriveState.AUTO_ABORT;
	}
	
	/**
	 * stops the drives
	 * @return if the drives have actually stopped
	 */
	public boolean stopDrives(){
		currentDriveState = DriveState.AUTO_STOP;
		wantedSpeed = STOP_MOTOR_POWER_SPEED;
		rightWantedSpeed = STOP_MOTOR_POWER_SPEED;
		leftWantedSpeed = STOP_MOTOR_POWER_SPEED;
		rightSetPower = STOP_MOTOR_POWER_SPEED;
		leftSetPower = STOP_MOTOR_POWER_SPEED;
		if(Math.abs(averageSpeed) < .1){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * holds the drives a certain position
	 */
	public void holdDrives(){
		currentDriveState = DriveState.AUTO_HOLD;
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
	 * Moves the robot to a specific coordinate
	 * @param xValue the x the robot needs to travel to 
	 * @param yValue the y value the robot needs to travel to
	 * @param driveSpeed the speed at which the robot needs to drive
	 * @param turnSpeed the speed at which the robot needs to turn
	 * @return true if the robot has made it to the coordinate, false otherwise
	 */
	public boolean travelToCoordinate(double xValue, double yValue, double driveSpeed, double turnSpeed){
		trig(xValue,yValue);
		autoTurn(wantedAngle, turnSpeed);
		if(turnDone){;
			autoDrive(wantedDistance, driveSpeed);
		}
		if(driveDone){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks all the motors
	 */
	public void diagnostics(){
		check(rightMotorTop, rightEncoder, rightEncoderData, CHECK_POWER);
		check(rightMotorFront, rightEncoder, rightEncoderData, CHECK_POWER);
		check(rightMotorBack, rightEncoder, rightEncoderData, CHECK_POWER);
		check(leftMotorTop, leftEncoder, leftEncoderData, CHECK_POWER);
		check(leftMotorFront, leftEncoder, leftEncoderData, CHECK_POWER);
		check(leftMotorBack, leftEncoder, leftEncoderData, CHECK_POWER);
	}
	
	/**
	 * Accessor method for current x position
	 * @return  current x value
	 */
	public double getCurrentX(){
		return currentX;
	}
	
	/**
	 * Accessor method for current y position
	 * @return current y value
	 */
	public double getCurrentY(){
		return currentY;
	}
	
	/**
	 * Helper method for auto turn
	 */
	private void turn(){
		angleOffset = wantedAngle - currentAngle;
		if(Math.abs(angleOffset)<3){
			rightWantedSpeed = 0;
			leftWantedSpeed = 0;
			currentDriveState = DriveState.STANDBY;
		}
		if((angleOffset>=0 && angleOffset<=180) || (angleOffset<=-180 && angleOffset>=-360)){
			rightWantedSpeed = -wantedSpeed;
			leftWantedSpeed = wantedSpeed;
		}else{
			rightWantedSpeed = wantedSpeed;
			leftWantedSpeed = -wantedSpeed;
		}
	}
	
	/**
	 * Help method for auto drive
	 */
	private void drive(){
		rightWantedSpeed = wantedSpeed;
		leftWantedSpeed = wantedSpeed;
		//correction = angleGyro.getAngle() - initialHeading;
		averageDistance = (Math.abs(rightEncoderData.getDistance()) + Math.abs(leftEncoderData.getDistance()))/2;

		if(averageDistance >= Math.abs(wantedDistance - (.05*wantedSpeed)) - .5){
			rightWantedSpeed = 0;
			leftWantedSpeed = 0;
			//correction = 0;
			LOG.logMessage("Distance Traveled: " + averageDistance);
			LOG.logMessage("Gryo Angle: " + gyro.getAngle());
			currentDriveState = DriveState.STANDBY;
		}
	}
	
	/**
	 * holds the drives at a specific y coordinate and angle...nothing can be done to 
	 * hold the x since we don't have lateral movement
	 */
	public void hold(){
		double changeX = currentX - previousX;
		double changeY = currentY - previousY;
		double changeAngle = currentAngle - previousAngle;
		if (Math.abs(changeX) > 3){
			LOG.logMessage("We have been pushed off course! Lateral Change: " + changeX);
		}
		if(Math.abs(changeY) > 3){
			autoDrive(changeY, HOLDING_DRIVE_SPEED);
		}
		if((Math.abs(changeAngle) > 3) && driveDone){
			autoTurn(previousAngle, HOLDING_TURN_SPEED);
		}
	}
	
	/**
	 * Aborts the auto drives
	 */
	public void abort(){
		rightMotorTop.set(STOP_MOTOR_POWER_SPEED);
		rightMotorFront.set(STOP_MOTOR_POWER_SPEED);
		rightMotorBack.set(STOP_MOTOR_POWER_SPEED);
		leftMotorTop.set(STOP_MOTOR_POWER_SPEED);
		leftMotorFront.set(STOP_MOTOR_POWER_SPEED);
		leftMotorBack.set(STOP_MOTOR_POWER_SPEED);
		wantedSpeed = STOP_MOTOR_POWER_SPEED;
		rightWantedSpeed = STOP_MOTOR_POWER_SPEED;
		leftWantedSpeed = STOP_MOTOR_POWER_SPEED;
		rightSetPower = STOP_MOTOR_POWER_SPEED;
		leftSetPower = STOP_MOTOR_POWER_SPEED;
	}
	
	/**
	 * Checks to see if the motor and corresponding encoder are reading the same and logs results
	 * @param motor the motor to check
	 * @param encoder the encoder to check
	 * @param encoderData the encoderData to check
	 * @param power the power to run the motor
	 */
	private void check(CANTalon motor, Encoder encoder, EncoderData encoderData, double power){
		double encoderSpeed;
		motor.set(power);
		encoderData.calculateSpeed();
		encoderSpeed = encoderData.getSpeed();
		if(power > 0 && encoderSpeed > 0){
			LOG.logMessage(motor + "is going forward and " + encoder + " is reading positive - Good");
		}else if(power > 0 && encoderSpeed < 0){
			LOG.logMessage(motor + "is going forward and " + encoder + " is reading negative - Bad");
		}else if(power > 0 && encoderSpeed == 0){
			LOG.logMessage(motor + "is going forward and " + encoder + " is reading zero - Bad");
		}
		
		else if(power < 0 && encoderSpeed > 0){
			LOG.logMessage(motor + "is going backwards and " + encoder + " is reading positive - Bad");
		}else if(power < 0 && encoderSpeed < 0){
			LOG.logMessage(motor + "is going backwards and " + encoder + " is reading negative - Good");
		}else if(power < 0 && encoderSpeed == 0){
			LOG.logMessage(motor + "is going backwards and " + encoder + " is reading zero - Bad");
		}
		
		else if(power == 0 && encoderSpeed > 0){
			LOG.logMessage(motor + "is not moving and " + encoder + " is reading positive - Bad");
		}else if(power == 0 && encoderSpeed < 0){
			LOG.logMessage(motor + "is not moving and " + encoder + " is reading negative - Bad");
		}else if(power == 0 && encoderSpeed == 0){
			LOG.logMessage(motor + "is not moving and " + encoder + " is reading zero - Good");
		}
	}
	
	/**
	 * Calculates the wanted angle and wanted distance to travel to a coordinate
	 * @param newX the x value of the position we're driving to
	 * @param newY the y value of the position we're driving to 
	 */
	private void trig(double newX, double newY){
		double xChange, yChange;
		xChange = newX - currentX;
		yChange = newY - currentY;
		wantedDistance = Math.sqrt((xChange * xChange) + (yChange * yChange));
		wantedAngle = Math.atan(xChange/yChange);
	}
	
	/**
	 * Enables the Drives to know if the robot is disabled, in auto, or if it's in teleop
	 */
	public enum DriveState{
		STANDBY,
		AUTO_DRIVE,
		AUTO_TURN,
		AUTO_HOLD,
		AUTO_ABORT,
		AUTO_STOP,
		TELEOP,
		DISABLED;

		/**
		 * Gets the name of the robot state
		 * @return the correct robot state 
		 */
		@Override
		public String toString(){
			switch(this){
			case STANDBY:
				return "Auto Standby";
			case AUTO_DRIVE:
				return "Auto Drive";
			case AUTO_TURN:
				return "Auto Turn";
			case AUTO_HOLD:
				return "Auto Hold";
			case AUTO_ABORT:
				return "Auto Abort";
			case AUTO_STOP:
				return "Auto Stop";
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