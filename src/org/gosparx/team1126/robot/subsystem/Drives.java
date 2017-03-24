package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.util.DriverStationControls;
import org.gosparx.team1126.robot.util.PID;
import org.gosparx.team1126.robot.util.SharedData;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * This class controls the drives system of the 2017 in tank or arcade drive 
 * @author Allison Morgan
 */
public class Drives extends GenericSubsystem {

	/** Constants */

	private static final double CALCULATED_DISTANCE_PER_TICK = .031219576995;	// The Formula: (Gear Ratio * Circumference)/ticks
	private static final double RIGHT_DISTANCE_PER_TICK = .03163;				// was .03068
	private static final double LEFT_REVERSE_DISTANCE_PER_TICK = .03098;				// was .02993
	private static final double RIGHT_REVERSE_DISTANCE_PER_TICK = .03261;
	private static final double LEFT_FORWARD_DISTANCE_PER_TICK = .03200;
	private static final double STOP_MOTOR_POWER_SPEED = 0;						// Speed for the motors when they are stopped
	private static final double MAX_SPEED = 175;        						// Maximum speed for the robot
	private static final double HOLDING_DRIVE_SPEED = 30;						// Speed for driving while in hold state
	private static final double HOLDING_TURN_SPEED = 30;						// Speed for turning while in hold state
	private static final double CHECK_POWER = .3;								// Power for diagnostics
	private static final double RIGHT_KI = .08;									// Integral for the right PID
	private static final double RIGHT_KP = .025;								// Proportional for the right PID
	private static final double LEFT_KI = .08;									// Integral for the left PID
	private static final double LEFT_KP = .025;									// Proportional for the left PID
	private static final double X_SENSITIVITY = 1.25;							// Sensitivity in the x-axis for arcade drive
	private static final double JOYSTICK_DEADBAND = .1; 						// Axis for the deadband
	private static final double RIGHT_FF = .00538;								// Feed forward for right PID
	private static final double LEFT_FF = .00538;								// Feed forward for left PID
	private static final double LIFT_TAPE_GEAR_DISTANCE = 20;
	
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
	private DriveState previousDriveState;										// The previous state
	private AHRS gyro; 															// NAVX gyro
	private double currentX;													// Starting x value of the robot
	private double currentY;													// Starting Y value of the robot
	private double previousX;													// Previous X value
	private double previousY;													// Previous Y value
	private double currentAngle;												// Current angle
	private boolean isInverse;													// If the drives is inverted
	private double wantedSpeed;													// Speed passed in through methods
	private double wantedDistance;												// Wanted distance
	private double wantedAngle;													// Wanted angle
	private double angleOffset;													// Offset of the angle
	private boolean turnDone;													// True if auto turn is done
	private boolean driveDone;													// True if auto drive is done
	private double averageSpeed;												// Average speed
	private double averageDistance;												// Average distance
	private double previousAngle;												// Previous angle
	private DiagnosticState currentDiagnosticState;								// Current diagnostic state
	private boolean isDiagnostic;												// Whether diagnostics are being run
	private long diagnosticTime;												// Time diagnostic starts												
	private double initialHeading;												// Intial heading of the robot
	private double straightCorrection;											// Correction for going straight
	private double distanceToGo;												// Distance left in auto drive		
	private double endX;														// End X coordinate	
	private double endY;														// End Y coordinate
	private double angleToEnd;													// Angle to end coordinate
	private double calculatedDistance;											// Calculated distance left (based on testing)
	private double offsetCorrection;											// Correction for the offset to the end point
	private double distanceToPoint;												// Distance to the end point	
	private boolean autoReady;													// Whether auto is ready (for use of calling auto functions in teleop)
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
		System.out.println("initttttttttttttttttttttttttttttttttttttt");
		//rightMotorTop = new CANTalon(IO.CAN_DRIVES_RIGHT_TOP);
		//rightMotorTop.setInverted(false);
		//rightMotorTop.enableBrakeMode(true);
		rightMotorFront = new CANTalon(IO.CAN_DRIVES_RIGHT_FRONT);
		rightMotorFront.setInverted(false);
		rightMotorFront.enableBrakeMode(true);
		rightMotorBack = new CANTalon(IO.CAN_DRIVES_RIGHT_BACK);
		rightMotorBack.setInverted(false);
		rightMotorBack.enableBrakeMode(true);
		rightEncoder = new Encoder(IO.DIO_RIGHT_DRIVES_ENC_A,IO.DIO_RIGHT_DRIVES_ENC_B);
		rightEncoderData = new EncoderData(rightEncoder, RIGHT_DISTANCE_PER_TICK);	
		rightEncoderData.setReverseDistancePerPulse(RIGHT_REVERSE_DISTANCE_PER_TICK);
		rightPID = new PID(RIGHT_KI, RIGHT_KP, RIGHT_FF);
		rightPID.breakMode(true);
		rightPID.setMaxMin(-0.95, 0.95);
		rightPID.setSPRamp(200);
		rightCurrentSpeed = 0;
		rightWantedSpeed = 0;
		rightPreviousDistance = 0;
		rightCurrentDistance = 0;
		rightSetPower = 0;

		//Left
		//leftMotorTop = new CANTalon(IO.CAN_DRIVES_LEFT_TOP);
		//leftMotorTop.setInverted(true);
		//leftMotorTop.enableBrakeMode(true);
		leftMotorFront = new CANTalon(IO.CAN_DRIVES_LEFT_FRONT);
		leftMotorFront.setInverted(true);
		leftMotorFront.enableBrakeMode(true);
		leftMotorBack = new CANTalon(IO.CAN_DRIVES_LEFT_BACK);
		leftMotorBack.setInverted(true);
		leftMotorBack.enableBrakeMode(true);
		leftEncoder = new Encoder(IO.DIO_LEFT_DRIVES_ENC_A,IO.DIO_LEFT_DRIVES_ENC_B, true);
		leftEncoderData = new EncoderData(leftEncoder, LEFT_REVERSE_DISTANCE_PER_TICK);		
		leftEncoderData.setReverseDistancePerPulse(LEFT_FORWARD_DISTANCE_PER_TICK);
		leftPID = new PID(LEFT_KI, LEFT_KP, LEFT_FF);
		leftPID.breakMode(true);
		leftPID.setMaxMin(-0.95, 0.95);
		leftPID.setSPRamp(200);
		leftCurrentSpeed = 0;
		leftWantedSpeed = 0;
		leftPreviousDistance = 0;
		leftCurrentDistance = 0;
		leftSetPower = 0;

		//Other
		currentDriveState = DriveState.STANDBY;
		previousDriveState = DriveState.STANDBY;
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
		straightCorrection = 0;
		initialHeading = 0;
		distanceToGo = 0;
		endX = 0;
		endY = 0;
		angleToEnd = 0;
		calculatedDistance = 0;
		distanceToPoint = 0;
		autoReady = true;
		dsc.setAxisDeadband(IO.RIGHT_JOY_Y, JOYSTICK_DEADBAND);
		dsc.setAxisDeadband(IO.LEFT_JOY_Y, JOYSTICK_DEADBAND);
		dsc.setAxisDeadband(IO.RIGHT_JOY_X, JOYSTICK_DEADBAND);
		dsc.setAxisDeadband(IO.LEFT_JOY_X, JOYSTICK_DEADBAND);

		return true;
	}

	/**
	 * Sets up liveWindow to set values during test mode
	 */

	@Override
	protected void liveWindow() {
		String motorName = "Drives Motors";
		String sensorName = "Drives Sensors";
		//LiveWindow.addActuator(motorName, "Right Motor 1", rightMotorTop);
		LiveWindow.addActuator(motorName, "Right Motor 2", rightMotorFront);
		LiveWindow.addActuator(motorName, "Right Motor 3", rightMotorBack);
		//LiveWindow.addActuator(motorName, "Left Motor 1", leftMotorTop);
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
		
//		LOG.logMessage("right speed = " + rightCurrentSpeed);
//		LOG.logMessage("left speed = " + leftCurrentSpeed);
		if(!previousDriveState.equals(currentDriveState)){
			if(currentDriveState.equals(DriveState.DISABLED)){
				//rightMotorTop.enableBrakeMode(false);
				rightMotorFront.enableBrakeMode(false);
				rightMotorBack.enableBrakeMode(false);
				//leftMotorTop.enableBrakeMode(false);
				leftMotorFront.enableBrakeMode(false);
				leftMotorBack.enableBrakeMode(false);
			}else if(previousDriveState.equals(DriveState.DISABLED)){
				//rightMotorTop.enableBrakeMode(true);
				rightMotorFront.enableBrakeMode(true);
				rightMotorBack.enableBrakeMode(true);
				//leftMotorTop.enableBrakeMode(true);
				leftMotorFront.enableBrakeMode(true);
				leftMotorBack.enableBrakeMode(true);
			}
		}		
		
		if(gyro.equals(null)){
			gyro = new AHRS(SerialPort.Port.kUSB);
			LOG.logMessage("Recreating NavX");
		}
		
//		if (!gyro.isConnected())
//			LOG.logMessage("NavX not connected!");

		rightEncoderData.calculateSpeed();
		leftEncoderData.calculateSpeed();
		rightCurrentSpeed = rightEncoderData.getSpeed();
		leftCurrentSpeed = leftEncoderData.getSpeed();
		averageSpeed = (rightCurrentSpeed + leftCurrentSpeed) / 2;
		currentAngle = gyro.getAngle() % 360;
		rightCurrentDistance = rightEncoderData.getDistance();
		leftCurrentDistance = leftEncoderData.getDistance();
//		LOG.logMessage("right distance = " + rightCurrentDistance);
//		LOG.logMessage("left distane = " + leftCurrentDistance);
//		LOG.logMessage("right ticks " + rightEncoder.getRaw());
//		LOG.logMessage("left ticks " + leftEncoder.getRaw());
		averageDistance = ((rightCurrentDistance - rightPreviousDistance) + (leftCurrentDistance - leftPreviousDistance))/2;
		currentX += Math.sin(Math.toRadians(currentAngle)) * averageDistance;
		currentY += Math.cos(Math.toRadians(currentAngle)) * averageDistance;

		if(dsc.isEnabled()){
			//LOG.logMessage(18, 50, "(X, Y) position: ( " + currentX + ", " + currentY + ") " + gyro.getAngle());
		}

		// Allow for zeroing gyro/encoders from the the joystick as long as we are not in autonomous
		
		if(!dsc.isAutonomous() && dsc.getButtonRising(DriverStationControls.LEFT_JOY_RIGHT)){
			rightEncoder.reset();
			rightEncoderData.reset();
			leftEncoder.reset();
			leftEncoderData.reset();
			gyro.zeroYaw();
			LOG.logMessage("Zeroing Gyro");
			currentX = 0;
			currentY = 0;
			previousX = 0;
			previousY = 0;
			previousAngle = 0;
			leftPreviousDistance = 0;
			rightPreviousDistance = 0;
		}
		
		switch(currentDriveState){
			
		case STANDBY:
			autoReady = true;
			if(dsc.isDisabled()){
				currentDriveState = DriveState.DISABLED;
			}else if(dsc.isOperatorControl()){
				currentDriveState = DriveState.TELEOP;
			}
			break;
			
		case AUTO_DRIVE_DISTANCE:
			autoReady = false;
			driveDistance();			
			break;
			
		case AUTO_DRIVE_POINT:
			autoReady = false;
			drivePoint();			
			break;
			
		case AUTO_TURN:
			autoReady = false;
			turn();			
			break;
			
		case AUTO_HOLD:
			autoReady = false;
			hold();			

			break;
			
		case AUTO_ABORT:
			abort();
			currentDriveState = DriveState.STANDBY;
			break;
			
		case AUTO_STOP:
			stopDrives();
			break;
			
		case TELEOP:
			if(dsc.getButtonRising(DriverStationControls.LEFT_JOY_TRIGGER)){
				LOG.logMessage("Turning");
				autoTurnToHeading(90, 40);
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
			if(dsc.getButtonRising(DriverStationControls.RIGHT_JOY_MIDDLE)){
				autoDrivePoint(144, 50);
			}
			
			setTankSpeed(dsc.getAxis(IO.RIGHT_JOY_Y), dsc.getAxis(IO.LEFT_JOY_Y), isInverse);
			//setArcadeSpeed(dsc.getAxis(IO.RIGHT_JOY_X), dsc.getAxis(IO.RIGHT_JOY_Y), isInverse);					
			break;
			
		case DISABLED:
			rightWantedSpeed = 0;
			leftWantedSpeed = 0;
			currentDriveState = DriveState.STANDBY;
			break;
			
		default:
			LOG.logError("Error :( Current Drive State: " + currentDriveState);
		}
	
		if(!isDiagnostic){
			
			if(dsc.isOperatorControl()){
				rightSetPower = rightWantedSpeed/MAX_SPEED;
				leftSetPower = leftWantedSpeed/MAX_SPEED;
			}else{
				rightSetPower = rightPID.loop(rightCurrentSpeed, rightWantedSpeed);
				leftSetPower = leftPID.loop(leftCurrentSpeed, leftWantedSpeed);
			}
		
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
			
			//rightMotorTop.set(rightSetPower);
			rightMotorFront.set(rightSetPower);
			rightMotorBack.set(rightSetPower);
			//leftMotorTop.set(leftSetPower);
			leftMotorFront.set(leftSetPower);
			leftMotorBack.set(leftSetPower);
		}

		SharedData.x = currentX;
		SharedData.y = currentY;
		SharedData.heading = currentAngle;
		SharedData.leftSpeed = leftCurrentSpeed;
		SharedData.rightSpeed = rightCurrentSpeed;
		SharedData.avgSpeed = averageSpeed;
		previousDriveState = currentDriveState;
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
		if(dsc.isEnabled()){
			//LOG.logMessage(0, 2, "(X, Y) position: ( " + currentX + ", " + currentY + ") " + gyro.getAngle());
			//LOG.logMessage(1, 2, "(Right, Left) Current Speeds: (" + rightCurrentSpeed + ", " + leftCurrentSpeed + ")");
			//LOG.logMessage(2, 2, "(Right, Left) Wanted Speeds : (" + rightWantedSpeed + ", " + leftWantedSpeed + ")");
			//LOG.logMessage(3, 2, "(Right, Left) Set Powers : (" + rightSetPower + ", " + leftSetPower + ")");
			//LOG.logMessage(4, 2, "Current Angle: " + currentAngle);
			//LOG.logMessage(5, 2, "Wanted Angle: " + wantedAngle);
			//LOG.logMessage(6, 2, "Previous Distances (Right,Left): (" + rightPreviousDistance + "," + leftCurrentDistance + ")");
			//LOG.logMessage(7, 2, "Current Distances (Right, Left): (" + rightCurrentDistance + "," + leftCurrentDistance + ")");
		}

	}

	/**
	 * sets the speed based on the power of the joysticks for tank drive
	 * @param right the power from the right joystick
	 * @param left the power from the left joystick
	 * @param isInverted if the drives are inverted
	 */
	public void setTankSpeed(double right, double left, boolean isInverted){
		if(!isInverted){
			rightWantedSpeed = -right * MAX_SPEED;
			leftWantedSpeed = -left * MAX_SPEED;
		}else{
			rightWantedSpeed = (left * MAX_SPEED);
			leftWantedSpeed = (right * MAX_SPEED);
		}
		//LOG.logMessage("right: " + rightWantedSpeed);
		//LOG.logMessage("left: " + leftWantedSpeed);
	}

	/**
	 * sets the speed based on the power of the joystick for arcade dribe
	 * @param xAxis value from the xAxis on the joystick
	 * @param yAxis value from the yAxis on the joystick
	 * @param isInverted if the drives are inverted
	 */
	public void setArcadeSpeed(double xAxis, double yAxis, boolean isInverted){
		if(!isInverted){
			rightWantedSpeed = -(yAxis + xAxis/X_SENSITIVITY) * MAX_SPEED;
			leftWantedSpeed = -(yAxis - xAxis/X_SENSITIVITY) * MAX_SPEED;
		}else{
			rightWantedSpeed = (yAxis - xAxis/X_SENSITIVITY) * MAX_SPEED;
			leftWantedSpeed = (yAxis + xAxis/X_SENSITIVITY) * MAX_SPEED;
		}
	}
	
	/**
	 * Drives the robot in auto
	 * @param distance the distance the robot will go, if distance > 0 then forward
	 * if distance < 0 then backward 
	 * @param speed the speed at which the robot should drive
	 * @return true if the robot is ready to go, false otherwise
	 */
	public boolean autoDriveDistance(double distance, double speed){
		LOG.logMessage("AutoDriveDistance (" + distance + ", " + speed + ")");
		LOG.logMessage("AutoDrive (X,Y) start: (" + currentX + "," + currentY + ")");
		
		if(!isAutoDone()){
			return false;
		}
		driveDone = false;
		wantedDistance = distance;
		initialHeading = gyro.getAngle();
		if(distance < 0){
			wantedSpeed = -speed;
		}else{
			wantedSpeed = speed;
		}
		currentDriveState = DriveState.AUTO_DRIVE_DISTANCE;
		return true;
	}
	
	/**
	 * Drives the robot  a distance by calculating the end point
	 * @param distance the distance to go
	 * @param speed the speed to drive
	 * @return true if the robot is ready to go, false otherwise
	 */
	public boolean autoDrivePoint(double distance, double speed){
		LOG.logMessage("AutoDrivePoint (" + distance + ", " + speed + ")");
		LOG.logMessage("AutoDrivePoint (X,Y) start: (" + currentX + "," + currentY + ")");

		if(!isAutoDone()){
			return false;
		}
		driveDone = false;
		wantedDistance = distance;
		initialHeading = gyro.getAngle();
		endY = currentY + distance*Math.cos(Math.toRadians(initialHeading));
		endX = currentX + distance*Math.sin(Math.toRadians(initialHeading));
		if(distance < 0){
			wantedSpeed = -speed;
		}else{
			wantedSpeed = speed;
		}
		currentDriveState = DriveState.AUTO_DRIVE_POINT;
		return true;
	}
	
	/**
	 * Drives the robot to a certain coordinate
	 * @param x the x value if the end coordinate
	 * @param y the y value of the end coordinate
	 * @param speed the speed to drive 
	 * @return true if the robot is ready to go, false otherwise
	 */
	public boolean autoDriveCoordinate(double x, double y, double speed){
		if(!isAutoDone()){
			LOG.logMessage("Error: AutoDrive Coordinate isAutoDone is false");
			return false;
		}
		LOG.logMessage("AutoDriveCoord (X,Y) start: (" + currentX + "," + currentY + ")");
		driveDone = false;
		autoReady = false;
		initialHeading = Math.toDegrees(Math.atan2(x-currentX, y-currentY));
		
		if (speed < 0) {
			initialHeading += 180;
			
			if (initialHeading >= 360)
				initialHeading -= 360;
		}
		
		endY = y;
		endX = x;
		
		
		LOG.logMessage("AutoDriveCoordinate (" + x + ", " + y + ", " + speed + ") Ang " + initialHeading);
		wantedSpeed = speed;
		currentDriveState = DriveState.AUTO_DRIVE_POINT;
		return true;
	}
	
	/**
	 * drives the robot to the lift
	 * @param speed the speed to drive
	 * @return true if the robot is ready to go, false otherwise
	 */
	public boolean moveToLift(double speed){
		if(!isAutoDone()){
			LOG.logMessage("Error: moveToLit isAutoDone is false");
			return false;
		}
		LOG.logMessage("MoveToLift (X,Y) start: (" + currentX + "," + currentY + ")");
		driveDone = false;
		autoReady = false;
		initialHeading = gyro.getAngle();
		wantedSpeed = speed;
		wantedDistance = SharedData.distanceToLift - LIFT_TAPE_GEAR_DISTANCE;
		wantedAngle = SharedData.angleToLift;
		LOG.logMessage("distance to lift from camera: " + SharedData.distanceToLift);
		LOG.logMessage("distance to drive to lift: " + wantedDistance);
		LOG.logMessage("angle to Lift: " + wantedAngle);
		LOG.logMessage("moveToLift(" + speed + ") Ang " + initialHeading);
		currentDriveState = DriveState.AUTO_DRIVE_DISTANCE;
		//currentDriveState = DriveState.STANDBY;
		return true;
	}
	/**
	 * Helper method for auto drive
	 */
	private void driveDistance(){
		autoReady = false;
		calculatedDistance = wantedDistance;
		//LOG.logMessage("wanted distance: " + wantedDistance);
		straightCorrection = gyro.getAngle() - initialHeading;
		averageDistance = (Math.abs(rightEncoderData.getDistance()) + Math.abs(leftEncoderData.getDistance()))/2;
		if(Math.abs(averageSpeed) > 16){
			calculatedDistance -= ((Math.abs(averageSpeed) - 12) * .25 +.5);
		}
		distanceToGo = calculatedDistance - averageDistance;
		//LOG.logMessage("Distance to go: " + distanceToGo);
		if(wantedSpeed > distanceToGo + 40){
			wantedSpeed = distanceToGo + 40;
		}
		//LOG.logMessage("wanted Speed: " + wantedSpeed);
		rightWantedSpeed = straightCorrection/4 + wantedSpeed;
		leftWantedSpeed = wantedSpeed - straightCorrection/4;
		if(averageDistance >= Math.abs(calculatedDistance - .5)){
			rightWantedSpeed = 0;
			leftWantedSpeed = 0;
			straightCorrection = 0;
			//LOG.logMessage("Distance Traveled: " + averageDistance);
			//LOG.logMessage("Gryo Angle: " + gyro.getAngle());
			currentDriveState = DriveState.STANDBY;
			LOG.logMessage("DriveDistance (X,Y) end: (" + currentX + "," + currentY + ")");
			driveDone = true;
			autoReady = true;
			return;
		}
	}
	
	/**
	 * Helper method for auto driving to a coordinate
	 */
	private void drivePoint(){
		autoReady = false;
		double xChange = endX - currentX;
			//LOG.logMessage("change in x: " + xChange);
		double yChange = endY - currentY;
		distanceToPoint = Math.sqrt((xChange * xChange) + (yChange * yChange));
			//LOG.logMessage("Distance to point: " + distanceToPoint);
		angleToEnd = Math.IEEEremainder(Math.atan2(xChange, yChange) - Math.toRadians(currentAngle), Math.PI * 2.0);
			//LOG.logMessage("Angle offset to end: " + angleToEnd);
		offsetCorrection = Math.sin(angleToEnd) * distanceToPoint;
			//LOG.logMessage("Offset correction: " + offsetCorrection);
			//LOG.logMessage("wanted distance: " + wantedDistance);
		straightCorrection = Math.IEEEremainder(gyro.getAngle() - initialHeading, 360);
		averageDistance = (Math.abs(rightEncoderData.getDistance()) + Math.abs(leftEncoderData.getDistance()))/2;
		if(Math.abs(averageSpeed) > 16){
			distanceToPoint -= ((Math.abs(averageSpeed) - 12) * .32 +.5);
		}
		if(wantedSpeed > distanceToPoint + 40){
			wantedSpeed = distanceToPoint + 40;
		}
		//LOG.logMessage("wanted Speed: " + wantedSpeed);
		rightWantedSpeed = wantedSpeed + straightCorrection/4 - offsetCorrection/2;
		leftWantedSpeed = wantedSpeed - straightCorrection/4 + offsetCorrection/2;
		if((distanceToPoint < .5) || (Math.abs(angleToEnd) > Math.toRadians(90))){
			rightWantedSpeed = 0;
			leftWantedSpeed = 0;
			straightCorrection = 0;
			offsetCorrection = 0;
			//LOG.logMessage("Distance Traveled: " + averageDistance);
			//LOG.logMessage("Gryo Angle: " + gyro.getAngle());
			currentDriveState = DriveState.STANDBY;
			LOG.logMessage("DrivePoint (X,Y) end: (" + currentX + "," + currentY + ")");
			LOG.logMessage("Ending drive to a coordinate.  angToEnd " + angleToEnd + "  distToPt " + distanceToPoint);
			driveDone = true;
			autoReady = true;
			return;
		}
	}
	
	/**
	 * Turns the robot to a specific heading, relative to the angle/heading the robot started at
	 * @param angle the angle the robot needs to turn
	 * @param speed the speed at which the robot should turn
	 * @return true if the robot is ready to go, false otherwise
	 */
	public boolean autoTurnToHeading(double angle, double speed){
		LOG.logMessage("AutoTurnHeading (" + angle + ", " + speed + ")");
		LOG.logMessage("AutoTurnHeading (X,Y) start: (" + currentX + "," + currentY + ")");
		if(!isAutoDone()){
			return false;
		}
		autoReady = false;
		turnDone = false;
		wantedAngle = angle;
		wantedSpeed = speed;
		currentDriveState = DriveState.AUTO_TURN;
		return true;
	}
	
	/**
	 * Turns the robot angle degrees from its current angle
	 * @param angle the angle the robot needs to turn
	 * @param speed the speed at which the robot should turn
	 * @return true if the robot is ready to go, false otherwise
	 */
	public boolean autoTurnToAngle(double angle, double speed){
		LOG.logMessage("AutoTurnAngle (" + angle + ", " + speed + ")");
		LOG.logMessage("AutoTurnAngle (X,Y) start: (" + currentX + "," + currentY + ")");
		if(!isAutoDone()){
			return false;
		}
		turnDone = false;
		autoReady = false;
		wantedAngle = currentAngle + angle;
		wantedSpeed = speed;
		currentDriveState = DriveState.AUTO_TURN;
		return true;
	}
	
	/**
	 * Turns the robot angle degrees to the lift
	 * @param speed the speed at which the robot should turn
	 * @return true if the robot is ready to go, false otherwise
	 */
	public boolean autoTurnLift(double speed){
		LOG.logMessage("AutoTurnLift (" + speed + ")");
		LOG.logMessage("AutoTurnLift (X,Y) start: (" + currentX + "," + currentY + ")");
		if(!isAutoDone()){
			return false;
		}
		turnDone = false;
		wantedAngle = SharedData.angleToLift;
		wantedSpeed = speed;
		currentDriveState = DriveState.AUTO_TURN;
		return true;
	}
	
	/**
	 * Helper method for auto turn
	 */
	private void turn(){
		autoReady = false;
		angleOffset = Math.IEEEremainder(wantedAngle - currentAngle, 360);
//		LOG.logMessage(20, 20, "Current Angle: " + currentAngle);
//		LOG.logMessage(21, 20, "angleOffset: " + angleOffset);
		double averageTurningSpeed = (Math.abs(rightCurrentSpeed)+ Math.abs(leftCurrentSpeed))/2;
		if(Math.abs(angleOffset)-((averageTurningSpeed-9)*.5) < 3){
			rightWantedSpeed = 0;
			leftWantedSpeed = 0;
			currentDriveState = DriveState.STANDBY;
			LOG.logMessage("Ending Auto turn");
			LOG.logMessage("Current Angle: " + currentAngle);
			turnDone = true;
			autoReady = true;
			LOG.logMessage("Turn (X,Y) end: (" + currentX + "," + currentY + ")");
			return;
		}
		if(wantedSpeed > 12 + Math.abs(angleOffset)/2){
			wantedSpeed = 12 + Math.abs(angleOffset)/2;
		}
		if(angleOffset > 0){
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
		LOG.logMessage("Auto Abort");;
		currentDriveState = DriveState.AUTO_ABORT;
	}
	
	/**
	 * Aborts the auto drives
	 */
	public void abort(){
		//rightMotorTop.set(STOP_MOTOR_POWER_SPEED);
		rightMotorFront.set(STOP_MOTOR_POWER_SPEED);
		rightMotorBack.set(STOP_MOTOR_POWER_SPEED);
		//leftMotorTop.set(STOP_MOTOR_POWER_SPEED);
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
		LOG.logMessage("StopDrives()");
		autoReady = false;
		currentDriveState = DriveState.AUTO_STOP;
		wantedSpeed = STOP_MOTOR_POWER_SPEED;
		rightWantedSpeed = STOP_MOTOR_POWER_SPEED;
		leftWantedSpeed = STOP_MOTOR_POWER_SPEED;
		rightSetPower = STOP_MOTOR_POWER_SPEED;
		leftSetPower = STOP_MOTOR_POWER_SPEED;
		if(Math.abs(leftCurrentSpeed) < .1 && Math.abs(rightCurrentSpeed) < .1){
			currentDriveState = DriveState.STANDBY;
			autoReady = true;
			LOG.logMessage("StopDrives (X,Y) end: (" + currentX + "," + currentY + ")");
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * holds the drives a certain position
	 * @return true if the robot is ready to go, false otherwise
	 */
	public boolean holdDrives(){
		if(!isAutoDone()){
			return false;
		}
		currentDriveState = DriveState.AUTO_HOLD;
		return true;
	}
	
	/**
	 * holds the drives at a specific y coordinate and angle...nothing can be done to 
	 * hold the x since we don't have lateral movement
	 */
	public void hold(){
		autoReady = false;
		double changeX = currentX - previousX;
		double changeY = currentY - previousY;
		double changeAngle = currentAngle - previousAngle;
		if (Math.abs(changeX) > 3){
			LOG.logMessage("We have been pushed off course! Lateral Change: " + changeX);
		}
		if(Math.abs(changeY) > 3){
			autoDriveDistance(changeY, HOLDING_DRIVE_SPEED);
		}
		if((Math.abs(changeAngle) > 3) && driveDone){
			autoTurnToHeading(previousAngle, HOLDING_TURN_SPEED);
		}
		if(turnDone){
			currentDriveState = DriveState.STANDBY;
			autoReady = true;
			
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
		gyro.zeroYaw();
	}
	
	/**
	 * Moves the robot to a specific coordinate
	 * @param xValue the x the robot needs to travel to 
	 * @param yValue the y value the robot needs to travel to
	 * @param driveSpeed the speed at which the robot needs to drive
	 * @param turnSpeed the speed at which the robot needs to turn
	 * @return true if the robot is in standby and , false otherwise
	 */
	public boolean travelToCoordinate(double xValue, double yValue, double driveSpeed, double turnSpeed){
		if(!isAutoDone()){
			return false;
		}
		trig(xValue,yValue);
		autoTurnToAngle(wantedAngle, turnSpeed);
		if(turnDone){;
			autoDriveDistance(wantedDistance, driveSpeed);
		}
		if(driveDone){
			currentDriveState = DriveState.STANDBY;
			autoReady = true;
		}
		return true;
	}
	
	/**
	 * returns if the auto is done
	 */
	public boolean isAutoDone(){
		if(currentDriveState.equals(DriveState.STANDBY) || autoReady){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Checks all the motors
	 */
	public void diagnostics(){
		switch(currentDiagnosticState){
		case DONE:
			//rightMotorTop.set(STOP_MOTOR_POWER_SPEED);
			rightMotorFront.set(STOP_MOTOR_POWER_SPEED);
			rightMotorBack.set(STOP_MOTOR_POWER_SPEED);
			//leftMotorTop.set(STOP_MOTOR_POWER_SPEED);
			leftMotorFront.set(STOP_MOTOR_POWER_SPEED);
			leftMotorBack.set(STOP_MOTOR_POWER_SPEED);
			return;
		case TOP:
			//rightMotorTop.set(CHECK_POWER);
			//leftMotorTop.set(CHECK_POWER);
			diagnosticTime = System.currentTimeMillis();
			currentDiagnosticState = DiagnosticState.TOP_WAIT;
			break;
		case TOP_WAIT:
			if(System.currentTimeMillis() < diagnosticTime + 500){
				return;
			}else{
				//check("Top Right", rightCurrentSpeed, CHECK_POWER);
				//check("Top Left", leftCurrentSpeed, CHECK_POWER);
				//rightMotorTop.set(STOP_MOTOR_POWER_SPEED);
				//leftMotorTop.set(STOP_MOTOR_POWER_SPEED);
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
		AUTO_DRIVE_DISTANCE,
		AUTO_DRIVE_POINT,
		AUTO_TURN,
		AUTO_HOLD,
		AUTO_ABORT,
		AUTO_STOP,
		TELEOP,
		DISABLED;	
	}
	
	/**
	 * Cases for diagnostics
	 */
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

