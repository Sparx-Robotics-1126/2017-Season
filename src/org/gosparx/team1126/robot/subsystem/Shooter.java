package org.gosparx.team1126.robot.subsystem;

/**
 * 
 * @Author - Nathan Hunt
 */

import edu.wpi.first.wpilibj.Encoder;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.AbsoluteEncoderData;
import org.gosparx.team1126.robot.sensors.EncoderData;

public class Shooter extends GenericSubsystem{

//*****************************************Variables*************************************\\
	
	/**
	 * true if first agitator is moving
	 */
	private boolean firstAgitatorMoving;
	
	/**
	 * true if second agitator is moving
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
	
//******************************************Objects**************************************\\
	
	private static Shooter shoot;
	
	private Encoder encoder;
	
	private EncoderData encoderData;
	
	private AbsoluteEncoderData turretSensor;
	
//*****************************************Constants*************************************\\
	/**
	 * the speed wanted for the first agitator 
	 */
	private final double FIRST_AGITATOR_SPEED = 0;
	
	/**
	 * The speed wanted for the second agitator;
	 */
	private final double SECOND_AGITATOR_SPEED = 0;
	
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
		turretSensor = new AbsoluteEncoderData(IO.CAN_TURRET_PORT, DEGREE_PER_VOLT);
		firstAgitatorMoving = false;
		secondAgitatorMoving = false;
		conveyerBeltMoving = false;
		turretMotorMoving  = false;
		shootingSpeedCurrent = 0;
		turretDegree = 0;
		turretMotorCurrent = 0;
		distPerTick = 0;
		return true;
	}

	@Override
	protected void liveWindow() {
		
	}

	@Override
	protected boolean execute() {
		if(dsc.isPressed(IO.SHOOTING_SYSTEM_ON)){
			shooting = true;
		}else if(shootingSpeedCurrent < shootingSpeed - SPEED_ALLOWED_OFF){
			//sets power = 100%
		}else if(shootingSpeedCurrent + SPEED_ALLOWED_OFF > shootingSpeed){
			//sets power = to some value that keeps the wheel turning but isn't increasing the speed
		}
		
		if(turretDegree > 360){
			turretDegree -= 360;
		}else if(turretDegree < 0){
			turretDegree += 360;
		}
		if(turretMotorMoving == false){
			turretMotorMoving = true;
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
	
	
	private double distanceToSpeed(){
		return distanceSetPoint * 10;

	}

}
