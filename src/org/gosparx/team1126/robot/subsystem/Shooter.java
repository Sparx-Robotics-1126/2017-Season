package org.gosparx.team1126.robot.subsystem;

/**
 * 
 * @Author - nphto
 */

import edu.wpi.first.wpilibj.Encoder;

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
	 * current turret degree
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
	
	
	private boolean speedButton;
	
	private boolean turretButton;
	
	private double pot;
	
//******************************************Objects**************************************\\
	
	private static Shooter shoot;
	
	private Encoder encoder;
	
	private EncoderData encoderData;
	
	private AbsoluteEncoderData turretSensor;
	
//*****************************************Constants*************************************\\
	/**
	 * the speed wanted for the agitator 
	 */
	private final double AGITATOR_SPEED = 0;
	
	/**
	 * The speed wanted for the conveyer belt
	 */
	private final double CONVEYER_BELT_SPEED = 0;
	
	/**
	 * the case number for shooting
	 */
	private final int CASE_SHOOTING = 1;
	
	/**
	 * the case number for the turret
	 */
	private final int CASE_TURRET = 2;
	
	
	private final double DEGREE_PER_VOLT = 0;
	
	/**
	 * the value that the speed of the motor is  allowed to be off by.
	 */
	private final int SPEED_ALLOWED_OFF = 25;
	
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

	//need to figure out what else should be updated and stuff
	@Override  
	protected boolean execute() {
		//turretButton = isButtonPressed
		//speedButton = isButtonPressed
		if(fireCtrl()){
			//FIRE
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
	
	//framework done
	/**
	 * calculates the required speed needed for shooting 
	 * @return - the required speed
	 */
	private double distanceToSpeed(){
		return distanceSetPoint * 10;

	}
	
	//framework done
	/**
	 * checks if the motors are ready and are at a correct speed
	 * @param button - if the button is pressed 
	 * @return - if this system is ready
	 */
	private boolean speedCtrl(boolean button){
		shootingSpeedCurrent = distanceToSpeed();
		if(dsc.isPressed(IO.BUTTON_SHOOTING_SYSTEM_ON)){
			shooting = true;
		}else if(shootingSpeedCurrent < shootingSpeed - SPEED_ALLOWED_OFF){
			//sets power = 100%
		}else if(shootingSpeedCurrent + SPEED_ALLOWED_OFF > shootingSpeed){
			//sets power = to some value that keeps the wheel turning but isn't increasing the speed
		}else{
			return true;
		}
		return false;	
	}
	
	//need to have the distance and angle figured out
	/**
	 * checks if the turret is ready to fire(correct distance and angle to fire)
	 * @param button - if the button is pressed
	 * @return - if this system is ready
	 */
	private boolean turretCtrl(boolean button){
		
		return true;
	}
	
	//framework done
	/**
	 * checks if the turret is locked on and the shooter is at speed
	 * @param fire - if turret and speed ctrl. are ready to fire
	 * @return - if this system is ready
	 */
	private boolean fireCtrl(){
		if(speedCtrl(speedButton) && turretCtrl(turretButton)){
			return true;
		}
		return false;
	}

}
