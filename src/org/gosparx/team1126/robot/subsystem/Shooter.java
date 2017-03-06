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
import com.ctre.CANTalon;
import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.AbsoluteEncoderData;
import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.subsystem.GenericSubsystem;
import org.gosparx.team1126.robot.util.SharedData;
import org.gosparx.team1126.robot.util.SharedData.Target;

public class Shooter extends GenericSubsystem{

//*****************************************Variables*************************************\\
	
	/**
	 * current shooting speed;
	 */
	private double shootingSpeedCurrent;
	
	/**
	 * the speed wanted before shooting
	 */
	private double shootingSpeed;
	
	/**
	 * current turret degree - 2/3 rotation per 30 degrees
	 */
	private double turretDegreeCurrent;
	
	/**
	 * current turret speed
	 */
	private double turretSpeedCurrent;
		
	/**
	 * if the shooter subsystem(speed method) is ready 
	 */
	private boolean speedButton;
	
	/**
	 * if the shooter subsystem(turret method) is ready
	 */
	private boolean turretButton;
	
	/**+
	 * the local variable to see if the button is being pressed
	 */
	private boolean isPressed;
	
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
	private int speed;
	
	/**
	 * the current time for logging messages every 1 second
	 */
	private long currentTime;
	
	/**
	 * the max encoder speed read during currentTime
	 */
	private double max;
	
	/**
	 * the minimum encoder speed during currentTime
	 */
	private double min; 
	
	/**
	 * also used for logging messages with the currentTime
	 */
	private double time;
	
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
	private final double DEGREE_PER_VOLT = 0.1;
	
	/**
	 * the value that the speed of the motor is  allowed to be off by.
	 */
	private final int SPEED_ALLOWED_OFF = 25;
	
	/**
	 * the max speed for the fly wheel
	 */
	private final double FLYWHEEL_MAX = 1;
	
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
	private final double ZERO_VOLTAGE = 2.5;
	
	
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
		encoder = new Encoder(IO.DIO_SHOOTER_ENC_A, IO.DIO_SHOOTER_ENC_B);
		encoderData = new EncoderData(encoder, DIST_PER_TICK); 
		//turretSensor = new AbsoluteEncoderData(IO.CAN_SHOOTER_TURRET, DEGREE_PER_VOLT);
		//turretSensor.setZero(ZERO_VOLTAGE);
		flyWheel = new CANTalon(IO.CAN_SHOOTER_FLYWHEEL);
		feeder = new CANTalon(IO.CAN_SHOOTER_INTAKE_FEEDER);
		turret = new CANTalon(IO.CAN_SHOOTER_TURRET);
	 	servo = new Servo(IO.PWM_BALLACQ_SERVO_AGITATOR);
		currentEnum = DiagnosticsEnuuum.DONE;
		shootingSpeedCurrent = 0;
		turretSpeedCurrent = 0;
		speedButton = false;
		turretButton = false;
		isPressed = false;
		degreeOff = 0;
		distance =  100;
		ready = false;
		speed = 1450;
		currentTime = 0;
		max = 0;
		min = 10000;
		time = 0;
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
	}

	//done
	/**
	 * makes the robot shoot and turn its turret and stuff
	 * @return - does not mean anything //turretDegreeCurrent = turretSensor.relDegrees();
	 */
	@Override  
	protected boolean execute(){
		encoderData.calculateSpeed();
		shootingSpeedCurrent = encoderData.getSpeed();
		//LOG.logMessage(36, 300,"Flywheel speed: " + shootingSpeedCurrent);
		//LOG.logMessage(37, 300, "Wanted Flywheel speed: " + speed);
	//	turretDegreeCurrent = turretSensor.getDegrees();
		if (System.currentTimeMillis()/1000.0 - time >1.0){
		//	LOG.logMessage("Max: " + max + " Min: " + min);
			max = 0;
			min = 10000;
			time = System.currentTimeMillis()/1000.0;
		}
			
		if (shootingSpeedCurrent > max)
			max = shootingSpeedCurrent;
		
		if (shootingSpeedCurrent < min)
			min = shootingSpeedCurrent;
		//LOG.logMessage(1,25,"Flywheel speed: " + shootingSpeedCurrent);
		
//		if(dsc.isOperatorControl())	
//			isPressed = dsc.isPressed(IO.BUTTON_SHOOTING_SYSTEM_ON);
//		if(isPressed){
//			speedButton = true;
//			turretButton = true;                       old way to turn on system using y button
//		}else{
//			speedButton = false;
//			turretButton = false;
//		}
		
//		if(dsc.getButtonRising(IO.BUTTON_SHOOTING_SYSTEM_ON)||isPressed){
//			if(speedButton == true){
//				speedButton = false;
//				turretButton = false;				alternate way to turn on shooter using y button
//				isPressed = false;
//			}else{
//				speedButton = true;
//				turretButton = true;
//				isPressed = false;
//			}
//		}
		
		if((dsc.isPressed(IO.FLIP_SHOOTING_SYSTEM_ON)&&(speedButton == false) && dsc.isOperatorControl())
				||(!(dsc.isPressed(IO.FLIP_SHOOTING_SYSTEM_ON))&&(speedButton == true)&&dsc.isOperatorControl())){
			if(speedButton == true){
				//LOG.logMessage("off,speed");
				speedButton = false;
				turretButton = false;
			}else{
			//	LOG.logMessage("on,speed");
				speedButton = true;
				turretButton = true;
			}
		}
//		}else if(isPressed){
//			speedButton = true;
//			turretButton = true;
//		}else if(!isPressed){
//			speedButton = false;
//			turretButton = false;
//		}
		if(dsc.getButtonRising(IO.FLYWHEEL_INCREASE)){
			speed += 25;
			//LOG.logMessage("up");
		}else if(dsc.getButtonRising(IO.FLYWHEEL_DECREASE)){
			//LOG.logMessage("Down");
			speed -= 25;
		}
		if(dsc.getButtonRising(IO.AGITATOR_SERVO)){
			servo.set(1);
			//LOG.logMessage("Servo is pressed");
		}	
		if(fireCtrl()){
			ready = true;
			if(dsc.isPressed(IO.BUTTON_FIRE))
				feeder.set(INTAKE_BALL_SPEED);
			else
				feeder.set(0);
		}else{
			ready = false;
			feeder.set(0);
		}
		
		if(dsc.isPressed(IO.DIAGNOSTICS))
			diagnostics();
		else
			currentEnum = DiagnosticsEnuuum.FLYWHEEL;

		dsc.sharedData.systemReady = ready;
		dsc.sharedData.turretAngle = turretDegreeCurrent;
		dsc.sharedData.shooterSpeed = shootingSpeedCurrent;		
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
//		LOG.logMessage("Flywheel speed: " + shootingSpeedCurrent);
//		//LOG.logMessage("Turret degree: " + turretDegreeCurrent);
//		//LOG.logMessage("Turret Degree Off: " + degreeOff);
//		LOG.logMessage("Distance Away: " + distance);
//		LOG.logMessage("IsPressed: " + isPressed);
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
	
	//done
	/**
	 * calculates the direction to turn
	 * @return - the degree and direction(positive or negative)
	 */
	private double turretSettings(){
		//degreeOff = dsc.sharedData.angleToTarget;
		return degreeOff;
	}
	
	//done 
	/**
	 * checks if the motors are ready and are at a correct speed9999
	 * @param button - if the button is pressed 
	 * @return - if this system is ready
	 */
	private boolean speedCtrl(){
		if(!speedButton){
			flyWheel.set(0);
			//LOG.logMessage("off");
			return false;
		}
		shootingSpeed = distanceToSpeed();
		if(shootingSpeedCurrent < shootingSpeed - SPEED_ALLOWED_OFF){
			flyWheel.set(FLYWHEEL_MAX);
			//LOG.logMessage("on");
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
//		if(!turretButton){
//			turret.set(0);
//			return false;
//		}
//		degreeOff = turretSettings();
//		LOG.logMessage("Degree: " + degreeOff);
//		if(limitSwitchRight.get()){
//			turret.set(-0.01);
//		}else if(limitSwitchLeft.get()){
//			turret.set(0.01);
//		}else{
//			if(turretDegreeCurrent < degreeOff-1){
//				turret.set(-.10);
//			}else if(turretDegreeCurrent > degreeOff+1){
//				turret.set(.10);
//			}else{
//				turret.set(0);
//				return true;
//			}
//		}
		return true;
	}
	
	//done
	/**
	 * checks if the turret is locked on and the shooter is at speed
	 * @return - if this system is ready
	 */
	private boolean fireCtrl(){
		//if(dsc.sharedData.targetType == SharedData.Target.BOILER)
			if(speedCtrl() && turretCtrl()){
				return true;
			}
		return false;
	}
	
	//done
	/**
	 * Sets the shooting system on for auto
	 * @param isOn - auto sends 1 to shoot
	 */
	public void shooterSystemState(int isOn){
		if(isOn == 1)
			isPressed = true;
		else if(isOn == 0)
			isPressed = false;
		else 
			isPressed = false;
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
}
