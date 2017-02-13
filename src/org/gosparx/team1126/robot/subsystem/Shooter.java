package org.gosparx.team1126.robot.subsystem;

/**
 * 
 * @Author - nphto
 */

//*****************************************Imports***************************************\\

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import com.ctre.CANTalon;
import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.AbsoluteEncoderData;
import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.subsystem.GenericSubsystem;

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
	 * if the shooter subsystem(speed method) is ready 
	 */
	private boolean speedButton;
	
	/**
	 * if the shooter subsystem(turret method) is ready
	 */
	private boolean turretButton;
	
	/**
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
	
	private int speed;
	
//*****************************************Objects***************************************\\
	
	private static Shooter shoot;
	
	private Encoder encoder;
	
	private EncoderData encoderData;
	
	//private AbsoluteEncoderData turretSensor;
	
	private CANTalon flyWheel;
	
	private CANTalon intake;
	
	private CANTalon turret;
	
	//private DigitalInput A;
	
	//private DigitalInput B;
	
//*****************************************Constants*************************************\\
	
	/**
	 * 
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
	private final double INTAKE_BALL_SPEED = 0.5; //we need to change this maybe
	
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
		//turretSensor = new AbsoluteEncoderData(IO.CAN_SHOOTER_TURNING, DEGREE_PER_VOLT);
		//turretSensor.setZero(ZERO_VOLTAGE);
		//A = new DigitalInput(IO.DIO_SHOOTER_ENC_A);
		//B = new DigitalInput(IO.DIO_SHOOTER_ENC_B);
		flyWheel = new CANTalon(IO.CAN_SHOOTER_FLYWHEEL);
		intake = new CANTalon(IO.CAN_BALLACQ_INTAKE);
		turret = new CANTalon(IO.CAN_SHOOTER_TURNING);
		shootingSpeedCurrent = 0;
		speedButton = false;
		turretButton = false;
		isPressed = false;
		degreeOff = 0;
		distance =  100;
		ready = false;
		speed = 2500;
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
		LiveWindow.addActuator(subsystemName, "Flywheel", flyWheel);
		LiveWindow.addActuator(subsystemName, "Intake", intake);
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
		LOG.logMessage(1,25,"Flywheel speed: " + shootingSpeedCurrent);
		if(dsc.isOperatorControl())
			isPressed = dsc.isPressed(IO.BUTTON_SHOOTING_SYSTEM_ON);
		if(isPressed){
			speedButton = true;
			turretButton = true;
		}else{
			speedButton = false;
			turretButton = false;
		}
		if(dsc.getButtonRising(IO.FLYWHEEL_INCREASE)){
			speed += 50;
			LOG.logMessage("up");
		}else if(dsc.getButtonRising(IO.FLYWHEEL_DECREASE)){
			LOG.logMessage("Down");
			speed -= 50;
		}
		if(fireCtrl()){
			ready = true;
			if(dsc.isPressed(IO.BUTTON_FIRE))
				intake.set(INTAKE_BALL_SPEED);
			else
				intake.set(0);
		}else{
			ready = false;
			intake.set(0);
		}
//		dsc.sharedData.systemReady = ready;
//		dsc.sharedData.turretAngle = turretDegreeCurrent;
//		dsc.sharedData.shooterSpeed = shootingSpeedCurrent;
		//System.out.println("t");
		return false;
	}

	//done
	/**
	 * time to rest the system between loops 
	 */
	@Override
	protected long sleepTime(){
		return 25;
	}

	//done
	/**
	 * for logging messages
	 */
	@Override
	protected void writeLog(){
		LOG.logMessage("Flywheel speed: " + shootingSpeedCurrent);
		//LOG.logMessage("Turret degree: " + turretDegreeCurrent);
		//LOG.logMessage("Turret Degree Off: " + degreeOff);
		LOG.logMessage("Distance Away: " + distance);
		LOG.logMessage("IsPressed: " + isPressed);
	}
	
	//framework done
	/**
	 * calculates the required speed needed for shooting 
	 * @return - the required speed
	 */
	private double distanceToSpeed(){
		return speed;

	}
	
	//framework done
	/**
	 * calculates the direction to turn
	 * @return - the degree and direction
	 */
	private double turretSettings(){
		return degreeOff;
	}
	
	//done 
	/**
	 * checks if the motors are ready and are at a correct speed
	 * @param button - if the button is pressed 
	 * @return - if this system is ready
	 */
	private boolean speedCtrl(){
		if(!speedButton){
			flyWheel.set(0);
			return false;
		}
		shootingSpeed = distanceToSpeed();
		if(shootingSpeedCurrent < shootingSpeed - SPEED_ALLOWED_OFF){
			flyWheel.set(FLYWHEEL_MAX);
		}else if(shootingSpeedCurrent + SPEED_ALLOWED_OFF > shootingSpeed){
			flyWheel.set(FLYWHEEL_DECAY);
		}
		if(Math.abs(shootingSpeedCurrent - shootingSpeed) < FlYWHEEL_DEADBAND)
				return true;
		return true;	 
	}
	
	//done
	/**
	 * checks if the turret is ready to fire(correct angle to fire)
	 * @param button - if the button is pressed
	 * @return - if this system is ready
	 */
	private boolean turretCtrl(){
/*		if(!turretButton){
			turret.set(0);
			return false;
		}
		if(turretDegreeCurrent < turretSettings()-1){
			turret.set(-.5);
		}else if(turretDegreeCurrent > turretSettings()+1){
			turret.set(.5);
		}else{
			turret.set(0);
			return true;
		}*/
		return true;
	}
	
	//done
	/**
	 * checks if the turret is locked on and the shooter is at speed
	 * @return - if this system is ready
	 */
	private boolean fireCtrl(){
		if(speedCtrl() && turretCtrl()){
			return true;
		}
		return false;
	}
	
	//done
	/**
	 * Sets the shooting system on for auto
	 * @param isOn - if auto is ready to shoot
	 * @return - true
	 */
	public boolean setSystemState(boolean isOn){
		isPressed = isOn;
		return true;
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

}
