package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.sensors.PID;
import org.gosparx.team1126.robot.util.DriverStationControls;

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
	private static final double RIGHT_DISTANCE_PER_TICK = .03068;
	private static final double LEFT_DISTANCE_PER_TICK = .02993;
	private static final double STOP_MOTOR_POWER_SPEED = 0;						// Speed for the motors when they are stopped
	private static final double MAX_SPEED = 175;        						// Maximum speed for the robot
	private static final double HOLDING_DRIVE_SPEED = 30;						// Speed for driving while in hold state
	private static final double HOLDING_TURN_SPEED = 30;						// Speed for turning while in hold state
	private static final double CHECK_POWER = .3;								// Power for diagnostics
	private static final double RIGHT_KI = .08;									// Integral for the right PID
	private static final double RIGHT_KP = .015;								// Proportional for the right PID
	private static final double LEFT_KI = .08;									// Integral for the left PID
	private static final double left_KP = .015;									// Proportional for the left PID
	private static final double X_SENSITIVITY = 1.25;							// Sensitivity in the x-axis for arcade drive
	private static final double JOYSTICK_DEADBAND = .06; 						// Axis for the deadband
	private static final double RIGHT_FF = .00538;								// Feed forward for right PID
	private static final double LEFT_FF = .00538;								// Feed forward for left PID
	
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
	private DiagnosticState currentDiagnosticState;
	private boolean isDiagnostic;
	private long diagnosticTime;
	private double initialHeading;
	private double correction;
	private double distanceToGo;
	
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
		rightMotorTop.setInverted(false);
		rightMotorTop.enableBrakeMode(true);
		rightMotorFront = new CANTalon(IO.CAN_DRIVES_RIGHT_FRONT);
		rightMotorFront.setInverted(false);
		rightMotorFront.enableBrakeMode(true);
		rightMotorBack = new CANTalon(IO.CAN_DRIVES_RIGHT_BACK);
		rightMotorBack.setInverted(false);
		rightMotorBack.enableBrakeMode(true);
		rightEncoder = new Encoder(IO.DIO_RIGHT_DRIVES_ENC_A,IO.DIO_RIGHT_DRIVES_ENC_B);
		rightEncoderData = new EncoderData(rightEncoder, RIGHT_DISTANCE_PER_TICK);	
		rightPID = new PID(RIGHT_KI, RIGHT_KP, RIGHT_FF);
		rightPID.breakMode(true);
		rightPID.setMaxMin(-0.95, 0.95);
		rightCurrentSpeed = 0;
		rightWantedSpeed = 0;
		rightPreviousDistance = 0;
		rightCurrentDistance = 0;
		rightSetPower = 0;

		//Left
		leftMotorTop = new CANTalon(IO.CAN_DRIVES_LEFT_TOP);
		leftMotorTop.setInverted(true);
		leftMotorTop.enableBrakeMode(true);
		leftMotorFront = new CANTalon(IO.CAN_DRIVES_LEFT_FRONT);
		leftMotorFront.setInverted(true);
		leftMotorFront.enableBrakeMode(true);
		leftMotorBack = new CANTalon(IO.CAN_DRIVES_LEFT_BACK);
		leftMotorBack.setInverted(true);
		leftMotorBack.enableBrakeMode(true);
		leftEncoder = new Encoder(IO.DIO_LEFT_DRIVES_ENC_A,IO.DIO_LEFT_DRIVES_ENC_B);
		leftEncoderData = new EncoderData(leftEncoder, -LEFT_DISTANCE_PER_TICK);		
		leftPID = new PID(LEFT_KI, left_KP, LEFT_FF);
		leftPID.breakMode(true);
		leftPID.setMaxMin(-0.95, 0.95);
		leftCurrentSpeed = 0;
		leftWantedSpeed = 0;
		leftPreviousDistance = 0;
		leftCurrentDistance = 0;
		leftSetPower = 0;

		//Other
		currentDriveState = DriveState.STANDBY;
		currentDiagnosticState = DiagnosticState.DONE;
		try{
			gyro = new AHRS(SerialPort.Port.kUSB);
			gyro.zeroYaw();
		}catch (RuntimeException ex ) {
            LOG.logError("Error instantiating navX MXP:  " + ex.getMessage());
        }
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
		isDiagnostic = false;
		diagnosticTime = 0;
		correction = 0;
		initialHeading = 0;
		distanceToGo = 0;
		
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
		
		dsc.setAxisDeadband(IO.RIGHT_JOY_Y, JOYSTICK_DEADBAND);
		dsc.setAxisDeadband(IO.LEFT_JOY_Y, JOYSTICK_DEADBAND);
		if(gyro.equals(null)){
			gyro = new AHRS(SerialPort.Port.kUSB);
		}
		
		rightEncoderData.calculateSpeed();
		leftEncoderData.calculateSpeed();
		rightCurrentSpeed = rightEncoderData.getSpeed();
			//LOG.logMessage(14, 25, "Right Speed " + rightCurrentSpeed);
		leftCurrentSpeed = leftEncoderData.getSpeed();
			//LOG.logMessage(15, 25, "Left Speed " + leftCurrentSpeed);
		averageSpeed = (rightCurrentSpeed + leftCurrentSpeed) / 2;
		currentAngle = gyro.getAngle() % 360;
			LOG.logMessage(16, 25, "Current Angle: " + currentAngle);
		rightCurrentDistance = rightEncoderData.getDistance();
			//LOG.logMessage(16, 25, "Right Current Distance: " + rightCurrentDistance);
		leftCurrentDistance = leftEncoderData.getDistance();
			//LOG.logMessage(17, 25, "Left Current Distance: " + leftCurrentDistance);
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
			if(dsc.getButtonRising(IO.RESET_ENCODER)){
				rightEncoder.reset();
				rightEncoderData.reset();
				leftEncoder.reset();
				leftEncoderData.reset();
			}
			if(dsc.getRawButton(2, DriverStationControls.XBOX_B)){
				autoTurn(120, 12);
			}
			if(dsc.getButtonRising(IO.INVERT_DRIVES_BUTTON)){
				isInverse = !isInverse;
			}
			if(dsc.getButtonRising(IO.ABORT_AUTO_DRIVES)){
				abortAuto();
			}
			if(dsc.getButtonRising(IO.HOLD_DRIVES)){
				holdDrives();
			}
			if(dsc.getRawButton(2, DriverStationControls.XBOX_BACK)){
				isDiagnostic = true;
				diagnostics();
			}else{
				currentDiagnosticState = DiagnosticState.TOP;
				isDiagnostic = false;
			}
			if(dsc.getRawButton(1, DriverStationControls.JOY_LEFT)){
				autoDrive(144, 60);
			}
			
			//setTankSpeed(dsc.getAxis(IO.RIGHT_JOY_Y), dsc.getAxis(IO.LEFT_JOY_Y), isInverse);
			setArcadeSpeed(dsc.getAxis(IO.RIGHT_JOY_X), 								// In case driver wants to use Arcade drive 
					dsc.getAxis(IO.RIGHT_JOY_Y), isInverse);					
			if(dsc.getRawButton(2, DriverStationControls.XBOX_B)){
				rightWantedSpeed = 50;
				leftWantedSpeed = 50;
			}
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
	
		if(!isDiagnostic){
			rightSetPower = rightPID.loop(rightCurrentSpeed, rightWantedSpeed);
			leftSetPower = leftPID.loop(leftCurrentSpeed, leftWantedSpeed);
			//rightSetPower = rightWantedSpeed/MAX_SPEED;			        	// In case driver doesn't want PID loop
			//leftSetPower = leftWantedSpeed/MAX_SPEED;							// In case driver doesn't want PID loop
		
			if(rightSetPower < 0){												// to account for deadband, where less than
				rightSetPower -= .05;											// .05 doens't give speed
			}else if (rightSetPower > 0){
				rightSetPower += .05;
			}
			if(leftSetPower < 0){
				leftSetPower -= .05;
			}else if (leftSetPower > 0){
				leftSetPower += .05;
			}
			
			rightMotorTop.set(rightSetPower);
			rightMotorFront.set(rightSetPower);
			rightMotorBack.set(rightSetPower);
			leftMotorTop.set(leftSetPower);
			leftMotorFront.set(leftSetPower);
			leftMotorBack.set(leftSetPower);
		}
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
//		LOG.logMessage(0, 25, "Current Speeds (Right,Left): (" + rightCurrentSpeed + "," + leftCurrentSpeed + ")");
//		LOG.logMessage(1, 10, "Wanted Speeds (Right,Left): (" + rightWantedSpeed + "," + leftWantedSpeed + ")");
//		LOG.logMessage(2, 10, "Set Powers (Right,Left): (" + rightSetPower + "," + leftSetPower + ")");
//		LOG.logMessage(3, 10, "Current Angle: " + currentAngle);
//		LOG.logMessage(4, 10, "Wanted Angle: " + wantedAngle);
//		LOG.logMessage(5, 10, "Previous Distances (Right,Left): (" + rightPreviousDistance + "," + leftCurrentDistance + ")");
//		LOG.logMessage(6, 10, "Current Distances (Right, Left): (" + rightCurrentDistance + "," + leftCurrentDistance + ")");
//		LOG.logMessage(8, 10, "Current Drive State: " + currentDriveState);
//		LOG.logMessage(9, 10, "Current Position (x,y): (" + currentX + "," + currentY + ")");
//		if(isInverse){
//			LOG.logMessage(10, 10, "The drives are inverted");
//		}else{
//			LOG.logMessage(11, 10, "The drives are not inverted");
//		}
//		LOG.logMessage(12, 5, "Right Y: " + dsc.getAxis(IO.RIGHT_JOY_Y));
//		LOG.logMessage(13, 5, "Left Y: " + dsc.getAxis(IO.LEFT_JOY_Y));
	}

	/**
	 * sets the speed based on the power of the joysticks for tank drive
	 * @param right the power from the right joystick
	 * @param left the power from the left joystick
	 */
	public void setTankSpeed(double right, double left, boolean isInverted){
		rightSetPower = right;
		leftSetPower = left;
//		if(!isInverted){
//			rightWantedSpeed = right * MAX_SPEED;
//			leftWantedSpeed = left * MAX_SPEED;
//		}else{
//			rightWantedSpeed = -(left * MAX_SPEED);
//			leftWantedSpeed = -(right * MAX_SPEED);
//		}
	}

	/**
	 * sets the speed based on the power of the joystick for arcade dribe
	 * @param xAxis value from the xAxis on the joystick
	 * @param yAxis value from the yAxis on the joystick
	 */
	public void setArcadeSpeed(double xAxis, double yAxis, boolean isInverted){
//		rightSetPower = (-yAxis - xAxis/X_SENSITIVITY);
//		leftSetPower = (-yAxis + xAxis/X_SENSITIVITY);
		rightWantedSpeed = (-yAxis - xAxis/X_SENSITIVITY)*MAX_SPEED;
		leftWantedSpeed = (-yAxis + xAxis/X_SENSITIVITY)*MAX_SPEED;
//		if(!isInverted){
//			rightWantedSpeed = (yAxis + xAxis/X_SENSITIVITY) * MAX_SPEED;
//			leftWantedSpeed = (yAxis - xAxis/X_SENSITIVITY) * MAX_SPEED;
//		}else{
//			rightWantedSpeed = -((yAxis - xAxis/X_SENSITIVITY) * MAX_SPEED);
//			leftWantedSpeed = -((yAxis + xAxis/X_SENSITIVITY) * MAX_SPEED);
//		}
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
		
		//initialHeading = gyro.getAngle();
		if(distance < 0){
			wantedSpeed = -speed;
		}else{
			wantedSpeed = speed;
		}
		currentDriveState = DriveState.AUTO_DRIVE;
		if(!currentDriveState.equals(DriveState.AUTO_DRIVE)){
			LOG.logMessage("Auto Drive is done, current distance: " + averageDistance);
			driveDone = true;
		}else{
			driveDone = false;
		}
	}
	
	/**
	 * Help method for auto drive
	 */
	private void drive(){
		double calculatedDistance = wantedDistance;
		LOG.logMessage("wanted distance: " + wantedDistance);
		//correction = gyro.getAngle() - initialHeading;
		averageDistance = (Math.abs(rightEncoderData.getDistance()) + Math.abs(leftEncoderData.getDistance()))/2;
		if(Math.abs(averageSpeed) > 16){
			calculatedDistance -= ((Math.abs(averageSpeed) - 12) * .25 +.5);
		}
		distanceToGo = calculatedDistance - averageDistance;
		LOG.logMessage("Distance to go: " + distanceToGo);
		if(wantedSpeed > distanceToGo + 40){
			wantedSpeed = distanceToGo + 40;
		}
		LOG.logMessage("wanted Speed: " + wantedSpeed);
		rightWantedSpeed = wantedSpeed;
		leftWantedSpeed = wantedSpeed;
		if(averageDistance >= Math.abs(calculatedDistance - .5)){
			rightWantedSpeed = 0;
			leftWantedSpeed = 0;
			//correction = 0;
			LOG.logMessage("Distance Traveled: " + averageDistance);
			LOG.logMessage("Gryo Angle: " + gyro.getAngle());
			currentDriveState = DriveState.STANDBY;
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
	 * Helper method for auto turn
	 */
	private void turn(){
		angleOffset = wantedAngle - currentAngle;
		if(Math.abs(angleOffset)<3){
			LOG.logMessage("Current Angle: " + currentAngle);
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
	 * aborts the current auto function
	 */
	public void abortAuto(){
		currentDriveState = DriveState.AUTO_ABORT;
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
		switch(currentDiagnosticState){
		case DONE:
			rightMotorTop.set(STOP_MOTOR_POWER_SPEED);
			rightMotorFront.set(STOP_MOTOR_POWER_SPEED);
			rightMotorBack.set(STOP_MOTOR_POWER_SPEED);
			leftMotorTop.set(STOP_MOTOR_POWER_SPEED);
			leftMotorFront.set(STOP_MOTOR_POWER_SPEED);
			leftMotorBack.set(STOP_MOTOR_POWER_SPEED);
			return;
		case TOP:
			rightMotorTop.set(CHECK_POWER);
			leftMotorTop.set(CHECK_POWER);
			diagnosticTime = System.currentTimeMillis();
			currentDiagnosticState = DiagnosticState.TOP_WAIT;
			break;
		case TOP_WAIT:
			if(System.currentTimeMillis() < diagnosticTime + 500){
				return;
			}else{
				check("Top Right", rightCurrentSpeed, CHECK_POWER);
				check("Top Left", leftCurrentSpeed, CHECK_POWER);
				rightMotorTop.set(STOP_MOTOR_POWER_SPEED);
				leftMotorTop.set(STOP_MOTOR_POWER_SPEED);
				currentDiagnosticState = DiagnosticState.FRONT;
			}
			break;
		case FRONT:
			rightMotorFront.set(CHECK_POWER);
			leftMotorFront.set(CHECK_POWER);
			diagnosticTime = System.currentTimeMillis();
			currentDiagnosticState = DiagnosticState.FRONT_WAIT;
			break;
		case FRONT_WAIT:
			if(System.currentTimeMillis() < diagnosticTime + 500){
				return;
			}else{
				check("Front Right", rightCurrentSpeed, CHECK_POWER);
				check("Front Left", leftCurrentSpeed, CHECK_POWER);
				rightMotorFront.set(STOP_MOTOR_POWER_SPEED);
				leftMotorFront.set(STOP_MOTOR_POWER_SPEED);
				currentDiagnosticState = DiagnosticState.BACK;
			}
			break;
		case BACK:
			rightMotorBack.set(CHECK_POWER);
			leftMotorBack.set(CHECK_POWER);
			diagnosticTime = System.currentTimeMillis();
			currentDiagnosticState = DiagnosticState.BACK_WAIT;
			break;
		case BACK_WAIT:
			if(System.currentTimeMillis() < diagnosticTime + 500){
				return;
			}else{
				check("Back Right", rightCurrentSpeed, CHECK_POWER);
				check("Back Left", leftCurrentSpeed, CHECK_POWER);
				rightMotorBack.set(STOP_MOTOR_POWER_SPEED);
				leftMotorBack.set(STOP_MOTOR_POWER_SPEED);
				currentDiagnosticState = DiagnosticState.DONE;
			}
			break;
		default:
			LOG.logError("Error, we are in the default diagnostic state");
			currentDiagnosticState = DiagnosticState.DONE;
		}
	}
	
	/**
	 * Checks to see if the motor and corresponding encoder are reading the same and logs results
	 * @param motor the motor to check
	 * @param encoder the encoder to check
	 * @param encoderData the encoderData to check
	 * @param power the power to run the motor
	 */
	private void check(String motorName, double encoderSpeed, double power){
		LOG.logMessage(motorName + " speed: " + encoderSpeed);
		if(power > 0 && encoderSpeed > 5){
			LOG.logMessage(motorName + " is going forward and the encoder is reading positive - Good");
		}else if(power > 0 && encoderSpeed < -5){
			LOG.logMessage(motorName + " is going forward and the encoder is reading negative - Bad");
		}else if(power > 0 && Math.abs(encoderSpeed) <= 5){
			LOG.logMessage(motorName + " is going forward and the encoder is reading about zero - Bad");
		}
		
		else if(power < 0 && encoderSpeed > 5){
			LOG.logMessage(motorName + " is going backwards and the encoder is reading positive - Bad");
		}else if(power < 0 && encoderSpeed < -5){
			LOG.logMessage(motorName + " is going backwards and the encoder is reading negative - Good");
		}else if(power < 0 && Math.abs(encoderSpeed) <= 5){
			LOG.logMessage(motorName + " is going backwards and encoder is reading zero - Bad");
		}
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
	
	public enum DiagnosticState{
		DONE,
		TOP,
		TOP_WAIT,
		FRONT,
		FRONT_WAIT,
		BACK,
		BACK_WAIT;
		
	}
}
