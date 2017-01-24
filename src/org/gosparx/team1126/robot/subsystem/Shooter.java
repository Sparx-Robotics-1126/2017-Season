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
	 * true if the agitator is moving
	 */
	private boolean agitatorMoving;
	
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
	
	@Override
	protected boolean init() {
		encoderData = new EncoderData(encoder, distPerTick); 
		turretSensor = new AbsoluteEncoderData(IO.CAN_SHOOTER_TURNING, DEGREE_PER_VOLT);
		agitatorMoving = false;
		conveyerBeltMoving = false;
		turretMotorMoving  = false;
		shootingSpeedCurrent = 0;
		turretDegree = 0;
		turretMotorCurrent = 0;
		distPerTick = 0;
		speedButton = false;
		turretButton = false;
		return true;
	}

	@Override
	protected void liveWindow() {
		
	}

	@Override
	protected boolean execute() {
		if(fireCtrl(speedCtrl(speedButton) && turretCtrl(turretButton))){
			//FIRE
			return true;
		}
		return false;
	}

	@Override
	protected long sleepTime() {
		
		return 0;
	}

	@Override
	protected void writeLog() {
		
		
	}
	
	/**
	 * sets the distance variable to the new distance
	 * @param distance - the new distance from the boiler
	 */
	public void setDistance(int distance){
		distanceSetPoint = distance;
	}
	
	/**
	 * calculates the required speed needed for the 
	 * @return - the required speed
	 */
	private double distanceToSpeed(){
		return distanceSetPoint * 10;

	}
	
	/**
	 * checks if the motors are ready and are at a correct speed
	 * @param button - if the button is pressed 
	 * @return - if this system is ready
	 */
	private boolean speedCtrl(boolean button){
		if(dsc.isPressed(IO.BUTTON_SHOOTING_SYSTEM_ON)){
			shooting = true;
		}else if(shootingSpeedCurrent < shootingSpeed - SPEED_ALLOWED_OFF){
			//sets power = 100%
		}else if(shootingSpeedCurrent + SPEED_ALLOWED_OFF > shootingSpeed){
			//sets power = to some value that keeps the wheel turning but isn't increasing the speed
		}
		return true;
	}
	
	/**
	 * checks if the turret is ready to fire(correct distance and angle to fire)
	 * @param button - if the button is pressed
	 * @return - if this system is ready
	 */
	private boolean turretCtrl(boolean button){
		return true;
	}
	
	/**
	 * checks if the turret is locked on and the shooter is at speed
	 * @param fire - if turret and speed ctrl. are ready to fire
	 * @return - if this system is ready
	 */
	private boolean fireCtrl(boolean fire){
		return true;
	}

}
