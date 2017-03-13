package org.gosparx.team1126.robot.subsystem;

/**
 * 
 * @Author - nphto
 */

//*****************************************Imports***************************************\\

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.CANTalon;
import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.AbsoluteEncoderData;
import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.subsystem.GenericSubsystem;
import org.gosparx.team1126.robot.util.SharedData;

public class Shooter extends GenericSubsystem{

//*****************************************Variables*************************************\\
	
	/**
	 * current shooting speed & wanted speed
	 */
	private double shootingSpeedCurrent;
	private double shootingSpeed;
	
	/**
	 * current turret degree - 2/3 rotation per 30 degrees & turret motor output
	 */
	private double turretDegreeCurrent;
	private double turretOutput;
		
	/**+
	 * the local variable to see if the button is being pressed
	 */
	private boolean isPressed;
    private boolean lastPressed;
    
	/**
	 * the local variable to see if the system should fire (when ready)
	 */
	private boolean fireNow;
	
	/**
	 * the degree the turret is off by
	 */
	private double degreeOff;
	
	/**
	 * the distance from the target 
	 */
	private double distance;
	
	/**
	 * if the system is ready to fire
	 */
	private boolean ready;
	
	/**
	 * speed for the shooter(temporary)
	 */
	private double speed;
		
	/**
	 * the min/max encoder speed read during currentTime
	 */
	private double max;
	private double min; 
	
	
//*****************************************Objects***************************************\\
	
	private static Shooter shoot;
	
	private Encoder encoder;
	
	private EncoderData encoderData;
	
	private AbsoluteEncoderData turretSensor;
	
	private CANTalon flyWheel;
	
	private CANTalon feeder;
	
	private CANTalon turret;
	
	private DiagnosticsEnuuum currentEnum;
	
	private Servo servo;
	
	private DigitalInput limitSwitchLeft;
	
	private DigitalInput limitSwitchRight;
	
//*****************************************Constants*************************************\\
	
	/**
	 * used for the turret absolute encoder
	 */
	private final double DEGREE_PER_VOLT = 9.828;
	
	/**
	 * the value that the speed of the motor is  allowed to be off by.
	 */
	private final int SPEED_ALLOWED_OFF = 15;
	
	/**
	 * the max speed for the fly wheel
	 */
	private final double FLYWHEEL_MAX = 1;
	
	/**
	 * Inital wheel speed
	 */
	private final double INITIAL_SPEED = 1450;

	/**
	 * the speed that slowly decreases the fly wheel speed
	 */
	private final double FLYWHEEL_DECAY = 0.25;
	
	/**
	 * the speed that allows the balls to go into the fly wheel 
	 */
	private final double INTAKE_BALL_SPEED = 0.35; //KEEP THIS VALUE 0.35
	
	/**
	 * the difference in fly wheel speeds allowed
	 */
	private final double FlYWHEEL_DEADBAND = 100;
	
	/**
	 * used for the encoder data
	 */
	private final double DIST_PER_TICK = 0.154194079; //0.146484375; //(1.0/256.0)*60.0;
	
	/**
	 * turret center position in volts
	 */
	private final double ZERO_VOLTAGE = 2.4;
	
	private BallAcq ballAcq;	
	
//*****************************************Methods***************************************\\	
	
	//done
	/**
	 * Constructs a shooter object
	 */
	public Shooter(){
		super("Shooter", Thread.NORM_PRIORITY);
	}
	
	//done
	/**
	 * makes sure there is only one instance of shooter
	 * @return - a shooter object
	 */
	public static synchronized Shooter getInstance(){
		if(shoot == null){
			shoot = new Shooter();
		}
		return shoot;
	}
	
	//done 
	/**
	 * instantiates all the objects and gives data to the variables
	 */
	@Override
	protected boolean init(){
		ballAcq = BallAcq.getInstance();
		encoder = new Encoder(IO.DIO_SHOOTER_ENC_A, IO.DIO_SHOOTER_ENC_B);
		encoderData = new EncoderData(encoder, DIST_PER_TICK); 
		turretSensor = new AbsoluteEncoderData(IO.ANALOG_SHOOTER_ABS_ENC, DEGREE_PER_VOLT);
		turretSensor.setZero(ZERO_VOLTAGE);
		flyWheel = new CANTalon(IO.CAN_SHOOTER_FLYWHEEL);
		feeder = new CANTalon(IO.CAN_SHOOTER_INTAKE_FEEDER);
		turret = new CANTalon(IO.CAN_SHOOTER_TURRET);
	 	servo = new Servo(IO.PWM_BALLACQ_SERVO_AGITATOR);
	 	limitSwitchLeft = new DigitalInput(IO.DIO_SHOOTER_LIMITSWITCH_LEFT);
	 	limitSwitchRight = new DigitalInput(IO.DIO_SHOOTER_LIMITSWITCH_RIGHT);
		currentEnum = DiagnosticsEnuuum.DONE;
		shootingSpeedCurrent = 0;
		isPressed = false;
		degreeOff = 0;
		distance =  100;
		ready = false;
		speed = INITIAL_SPEED;
		max = 0;
		min = 10000;
		dsc.setAxisDeadband(IO.TURRET_JOY_Y, .1); //added for manual turret control
		return true;
	}

	//done
	/**
	 * used to set data during testing mode
	 */
	@Override
	protected void liveWindow(){
		String subsystemName = "Shooter";
		LiveWindow.addActuator(subsystemName, "Turret Motor", turret);
		LiveWindow.addActuator(subsystemName, "FlyWheel", flyWheel);
		LiveWindow.addActuator(subsystemName, "FeederWheel", feeder);
		LiveWindow.addActuator(subsystemName, "Encoder", encoder);
		LiveWindow.addSensor(subsystemName, "Right Limit Switch", limitSwitchRight);
		LiveWindow.addSensor(subsystemName, "Left Limit Switch", limitSwitchLeft);
		SmartDashboard.putBoolean("Turret Limit Switch Left", limitSwitchLeft.get());
		SmartDashboard.putBoolean("Turret Limit Switch Right", limitSwitchRight.get());
	}

	//done
	/**
	 * makes the robot shoot and turn its turret and stuff
	 * @return - does not mean anything //turretDegreeCurrent = turretSensor.relDegrees();
	 */
	@Override  
	protected boolean execute(){
		boolean turretReady;
		boolean shooterReady;
		
		encoderData.calculateSpeed();
		shootingSpeedCurrent = encoderData.getSpeed();
		turretDegreeCurrent = turretSensor.relDegrees();

		//LOG.logMessage(36, 300,"Flywheel speed Wanted/Actual: " + shootingSpeedCurrent + " " + speed);

		// printing min/max for testing wheel speed control
/*		
		if (System.currentTimeMillis() - time > 1000){
			LOG.logMessage("Max: " + max + " Min: " + min);
			max = 0;
			min = 10000;
			time = System.currentTimeMillis();
		}
			
		if (shootingSpeedCurrent > max)
			max = shootingSpeedCurrent;
		
		if (shootingSpeedCurrent < min)
			min = shootingSpeedCurrent;
*/

		// Get Shooting System Command - If Operator Control, then get the input from the joystick,
		// otherwise Autonomous will automatically set/clear isPressed by calling the routine
		// "shooterSystemState".  Otherwise make sure isPressed is false.
		
		if (!dsc.isAutonomous()){
			isPressed = false;
			fireNow = false;
		}
		
		if (dsc.isOperatorControl()){
			isPressed = dsc.isPressed(IO.FLIP_SHOOTING_SYSTEM_ON);
			fireNow = dsc.isPressed(IO.BUTTON_FIRE);
		}

		// Check to see if the system state has changed
		
		if (lastPressed != isPressed){
			if (isPressed){
				dsc.sharedData.targetType = SharedData.Target.BOILER;
				speed = INITIAL_SPEED;
			}
			else
				dsc.sharedData.targetType = SharedData.Target.NONE;

			lastPressed = isPressed;
		}
		
		// Manual control of the flywheel.  This will need to be changed to the analog dial on joystick 4 
		
		if(dsc.getButtonRising(IO.FLYWHEEL_INCREASE)){
			speed += 25;
		}else if(dsc.getButtonRising(IO.FLYWHEEL_DECREASE)){
			speed -= 25;
		}

		// fireCtrl checks to see if target and wheel is ready to fire.  Turret on Target, Wheel @ speed
		
		turretReady = turretCtrl();
		shooterReady = speedCtrl();		
		ready = turretReady & shooterReady;
		
		if(ready && fireNow)
			feeder.set(INTAKE_BALL_SPEED);
		else
			feeder.set(0);		

		if(dsc.getAxis(IO.TURRET_JOY_Y) < -0.25){
			turretOutput = -.2;
		}else if (dsc.getAxis(IO.TURRET_JOY_Y) > 0.25){
			turretOutput = .2;
		}
		
		// Turret Limit Protection
		
		if (limitSwitchRight.get() && turretOutput < 0){
//			LOG.logMessage("Limit Right");
			turretOutput = 0;
		}else if (limitSwitchLeft.get() && turretOutput > 0){
//			LOG.logMessage("Limit Left");;
			turretOutput = 0;
		}
		
		turret.set(turretOutput);
		
		//LOG.logMessage("turrent angle: " + turretSensor.getDegrees());
		//LOG.logMessage("relative angle: " + turretSensor.relDegrees());
		//LOG.logMessage("Voltage: " + turretSensor.getVoltage());
		
		if(dsc.isPressed(IO.DIAGNOSTICS))
			diagnostics();
		else
			currentEnum = DiagnosticsEnuuum.FLYWHEEL;

		SharedData.systemReady = ready;
		SharedData.turretAngle = turretDegreeCurrent;
		SharedData.shooterSpeed = shootingSpeedCurrent;		
		return false;
	}

	//done
	/**
	 * time to rest the system between loops 
	 */
	@Override
	protected long sleepTime(){
		return 10;
	}

	//done
	/**
	 * for logging messages
	 */
	@Override
	protected void writeLog(){
		LOG.logMessage("Flywheel SP/Spd: " + (int) speed +"/" + (int) shootingSpeedCurrent);
		LOG.logMessage("Turret Voltage: " + turretSensor.getVoltage());
		LOG.logMessage("Turret Angle: "+ turretDegreeCurrent);
		//		LOG.logMessage("Turret Degree Off: " + degreeOff);
//		LOG.logMessage("Distance Away: " + distance);
//		LOG.logMessage("IsPressed: " + isPressed
	}
	
	//framework done
	/**
	 * calculates the required speed needed for shooting 
	 * @return - the required speed
	 */
	private double distanceToSpeed(){
// 		speed = dsc.sharedData.distanceToTarget * someFormula
		return speed;

	}
	
	
	/**
	 * checks if the motors are ready and are at a correct speed9999
	 * @param button - if the button is pressed 
	 * @return - if this system is ready
	 */
	private boolean speedCtrl(){
		if(!isPressed){
			flyWheel.set(0);
			return false;
		}
		
		shootingSpeed = distanceToSpeed();

		if(shootingSpeedCurrent < shootingSpeed - SPEED_ALLOWED_OFF){
			flyWheel.set(FLYWHEEL_MAX);
		}else if(shootingSpeedCurrent + SPEED_ALLOWED_OFF > shootingSpeed){
			flyWheel.set(FLYWHEEL_DECAY + (shootingSpeed * 0.0001));
		}
		
		if(Math.abs(shootingSpeedCurrent - shootingSpeed) > FlYWHEEL_DEADBAND)
				return false;
		return true;	 
	}
	
	
	//done (until tested)
	/**
	 * checks if the turret is ready to fire(correct angle to fire)
	 * @return - if this system is ready
	 */
	private boolean turretCtrl(){
		turretOutput = 0;									// Initialize Turret Output to 0

		// Only control if system is ON and we have a boiler image less than 2 seconds old.
		
		if(!isPressed || SharedData.getImageTime(SharedData.Target.BOILER) > 2.0){
			return false;
		}

		degreeOff = SharedData.angleToBoiler;
		
		if(turretDegreeCurrent < degreeOff - .5){
			turretOutput = -.50;
		}else if(turretDegreeCurrent > degreeOff + 0.5){
			turretOutput = .50;
		} else
			return true;
		
		turretOutput *= (.2 + (Math.abs(turretDegreeCurrent - degreeOff) * 0.1));
		
		return false;	
	}
		
	//done
	/**
	 * Sets the shooting system on for auto
	 * @param isOn - auto sends 1 to shoot
	 */
	public void shooterSystemState(int isOn){
		if ((isOn == 1) && dsc.isAutonomous())
			isPressed = true;
		else 
			isPressed = false;
	}
	
	public void shooterSystemFire(int fire) {
		if ((fire == 1) && dsc.isAutonomous()){
			fireNow = true;
			ballAcq.transport(true);
		} else {
			fireNow = false;
			ballAcq.transport(false);
		}
	}
	
	//done
	/**
	 * Updates the degree and distance from vision
	 * @param degreeOff - visions degree off from center
	 * @param distance - the distance from the target
	 */
	public void visionUpdate(double degreeOff, double distance){
		this.degreeOff = degreeOff;
		this.distance = distance;
	}
	
	public void feederOff()
	{
		feeder.set(0);
	}
	
	//done
	/**
	 * checks all the motors
	 */
	public void diagnostics(){
/*		switch(currentEnum){
		case DONE:
			flyWheel.set(0);
			feeder.set(0);
			turret.set(0);
			break;
		case FLYWHEEL:
			flyWheel.set(0.5);
			currentTime = System.currentTimeMillis();
			currentEnum = DiagnosticsEnuuum.FLYWHEEL_WAIT;
			break;
		case FLYWHEEL_WAIT:
			if(System.currentTimeMillis() < currentTime + 500)
				return;
			else{
				check("Flywheel", shootingSpeedCurrent);
				flyWheel.set(0);
				currentEnum = DiagnosticsEnuuum.TURRET;
			}
			break;
		case TURRET:
			turret.set(0.5);
			currentTime = System.currentTimeMillis();
			currentEnum = DiagnosticsEnuuum.TURRET_WAIT;
			break;
		case TURRET_WAIT:
			if(System.currentTimeMillis() < currentTime + 500)
				return;
			else{
				check("Turret", turretDegreeCurrent);
				turret.set(0);
				currentEnum = DiagnosticsEnuuum.DONE;
			}
			break;
		}*/
	}
	
	//done
	/**
	 * creates the enums cases
	 */
	public enum DiagnosticsEnuuum{
		DONE,
		FLYWHEEL,
		FLYWHEEL_WAIT,
		TURRET,
		TURRET_WAIT;
	}
	
	//done
	/**
	 * checks to make sure the encoder is working correctly
	 * @param name- the name of the motor
	 * @param encoderSpeed - the speed the encoder is reading
	 * power is only going to be positive
	 */
	public void check(String name, double encoderSpeed){
		//LOG.logMessage(name + " speed: " + encoderSpeed);
		if(encoderSpeed > 5)
			LOG.logMessage(name + " is going forward and the encoder is reading correctly");
		else if(encoderSpeed < -5)
			LOG.logMessage(name + " is going forward but the encoder is reading backwards");
		else
			LOG.logMessage(name + " is going forward but the encoder is reading 0");
	}
	
	//couldn't figure out how to invert limit switch so....
	public boolean getPressed (DigitalInput limit){
		if(limit.get()){
			return false;
		}else{
			return true;
		}
	}
}
