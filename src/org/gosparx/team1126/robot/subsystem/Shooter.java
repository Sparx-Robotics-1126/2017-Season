package org.gosparx.team1126.robot.subsystem;

/**
 * 
 * @Author - nphto
 */

import edu.wpi.first.wpilibj.Encoder;
import com.ctre.CANTalon;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.AbsoluteEncoderData;
import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.subsystem.GenericSubsystem;

public class Shooter extends GenericSubsystem{

//*****************************************Variables*************************************\\
	
	/**
	 * true if the first agitator is moving
	 */
	private boolean firstAgitatorMoving;
	
	/**
	 * true if the second agitator is moving
	 */
	private boolean secondAgitatorMoving;
	
	/**
	 * true if conveyer belt is moving
	 */
	private boolean conveyerBeltMoving;
	
	/**
	 * true if shooting
	 */
	private boolean shooting;
	
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
	 * the angle the robot is from the target 
	 */
	private double turretDegree;
	
	/**
	 * if the turret motor is moving 
	 */
	private boolean turretMotorMoving;
	
	/**
	 * current motor speed 
	 */
	private double turretMotorCurrent;
	
	
	private double distPerTick;
	
	/**
	 * distance to boiler
	 */
	private double distanceSetPoint;
	
	/**
	 * if the shooter subsystem(speed method) is ready 
	 */
	private boolean speedButton;
	
	/**
	 * if the shooter subsystem(turret method) is ready
	 */
	private boolean turretButton;
	
	/**
	 * the pentiameter
	 */
	private double pot;
	
//******************************************Objects**************************************\\
	
	private static Shooter shoot;
	
	private Encoder encoder;
	
	private EncoderData encoderData;
	
	private AbsoluteEncoderData turretSensor;
	
	private CANTalon flyWheel;
	
	private CANTalon conveyor;
	
	private CANTalon turret;
	
	
//*****************************************Constants*************************************\\
	/**
	 * the speed wanted for the agitator 
	 */
	private final double AGITATOR_SPEED = 0;
	
	/**
	 * The speed wanted for the conveyer belt
	 */
	private final double CONVEYER_BELT_SPEED = 0;
	
	private final double DEGREE_PER_VOLT = 0;
	
	/**
	 * the value that the speed of the motor is  allowed to be off by.
	 */
	private final int SPEED_ALLOWED_OFF = 25;
	
	/**
	 * the max speed for the fly wheel
	 */
	private final double FLYWHEEL_MAX = 1;
	
	/**
	 * the speed that slowly decreases the flywheel speed
	 */
	private final double FLYWHEEL_DECAY = 0.25;
	
	/**
	 * the speed that allows the balls to go into the flywheel 
	 */
	private final double CONVEYOR_BALL_SPEED = 1; //we need to change this maybe
	
	/**
	 * the speed that will turn the turret until it hits the correct degree
	 */
	private final double TURRET_SPEED = 0.25;
	
//***************************************************************************************\\	
	
	
	
	
	/**
	 * Constructs a shooter object
	 */
	public Shooter(){
		super("Shooter", Thread.NORM_PRIORITY);
	}
	
	public static synchronized Shooter getInstance(){
		if(shoot == null){
			shoot = new Shooter();
		}
		return shoot;
	}
	
	//framework done(updated whenever instance values are added) 
	@Override
	protected boolean init() {
		encoderData = new EncoderData(encoder, distPerTick); 
		turretSensor = new AbsoluteEncoderData(IO.CAN_SHOOTER_TURNING, DEGREE_PER_VOLT);
		flyWheel = new CANTalon(IO.CAN_SHOOTER_FLYWHEEL);
		conveyor = new CANTalon(IO.CAN_BALLACQ_CONVEYOR);
		turret = new CANTalon(IO.CAN_SHOOTER_TURNING);
		firstAgitatorMoving = false;
		secondAgitatorMoving = false;
		conveyerBeltMoving = false;
		turretMotorMoving  = false;
		shootingSpeedCurrent = 0;
		turretDegree = 0;
		turretMotorCurrent = 0;
		distPerTick = 0;
		speedButton = false;
		turretButton = false;
		pot = 0;
		return true;
	}

	//needs to be started
	@Override
	protected void liveWindow() {
		
	}

	//need to figure out what else should be updated and stuff, no where near finished really
	@Override  
	protected boolean execute() {
		if(dsc.isPressed(IO.BUTTON_SHOOTING_SYSTEM_ON)){
			speedButton = true;
			turretButton = true;
		}else{
			speedButton = false;
			turretButton = false;
		}
		if(fireCtrl()){
			conveyor.set(CONVEYOR_BALL_SPEED);
			return true;
		}
		return false;
	}

	//needs to be started
	@Override
	protected long sleepTime() {
		
		return 0;
	}

	//needs to be started
	@Override
	protected void writeLog() {
		
		
	}
	
	//done
	/**
	 * sets the distance variable to the new distance
	 * @param distance - the new distance from the boiler
	 */
	public void setDistance(int distance){
		distanceSetPoint = distance;
	}
	
	//framework done(need to create some equation that calc. the speed needed using the degree and distance from the target)
	/**
	 * calculates the required speed needed for shooting 
	 * @return - the required speed
	 */
	private double distanceToSpeed(){
		return distanceSetPoint * 10;

	}
	
	//done 
	/**
	 * checks if the motors are ready and are at a correct speed
	 * @param button - if the button is pressed 
	 * @return - if this system is ready
	 */
	private boolean speedCtrl(){
		if(!speedButton){
			return false;
		}
		shootingSpeedCurrent = distanceToSpeed();
		if(shootingSpeedCurrent < shootingSpeed - SPEED_ALLOWED_OFF){
			flyWheel.set(FLYWHEEL_MAX);
		}else if(shootingSpeedCurrent + SPEED_ALLOWED_OFF > shootingSpeed){
			flyWheel.set(FLYWHEEL_DECAY);
		}else{
			return true;
		}
		return false;	 
	}
	
	//need to have the distance and angle figured out
	/**
	 * checks if the turret is ready to fire(correct angle to fire)
	 * @param button - if the button is pressed
	 * @return - if this system is ready
	 */
	private boolean turretCtrl(){
		if(!turretButton){
			return false;
		}else if(turretDegreeCurrent != turretDegree){
			turret.set(arg0);
		}
		
		return true;
	}
	
	//done
	/**
	 * checks if the turret is locked on and the shooter is at speed
	 * @param fire - if turret and speed ctrl. are ready to fire
	 * @return - if this system is ready
	 */
	private boolean fireCtrl(){
		if(speedCtrl() && turretCtrl()){
			return true;
		}
		return false;
	}

}
