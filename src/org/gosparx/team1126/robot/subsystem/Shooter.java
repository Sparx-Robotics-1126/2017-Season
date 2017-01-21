package org.gosparx.team1126.robot.subsystem;

/**
 * 
 * @Author - Nathan Hunt
 */

import edu.wpi.first.wpilibj.Encoder;
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
	
//******************************************Objects**************************************\\
	
	private static Shooter shoot;
	
	private Encoder encoder;
	
	private EncoderData encoderData;
	
	private AbsoluteEncoderData turretSensor;
	
//*****************************************Constants*************************************\\
	/**
	 * the speed wanted for the first agitator 
	 */
	private final double firstAgitatorSpeed = 0;
	
	/**
	 * The speed wanted for the second agitator;
	 */
	private final double secondAgitatorSpeed = 0;
	
	/**
	 * The speed wanted for the conveyer belt
	 */
	private final double conveyerBeltSpeed = 0;
	
	/**
	 * the case number for shooting
	 */
	private final int caseShooting = 1;
	
	/**
	 * the case number for the turret
	 */
	private final int caseTurret = 2;
	
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
		encoder = new encoder();
		encoderData = new encoderData(); 
		turretSensor = new AbsoluteEncoderData();
		firstAgitatorMoving = false;
		secondAgitatorMoving = false;
		conveyerBeltMoving = false;
		turretMotorMoving  = false;
		shootingSpeedCurrent = 0;
		turretDegree = 0;
		turretMotorCurrent = 0;
		return true;
	}

	@Override
	protected void liveWindow() {
		
	}

	@Override
	protected boolean execute() {
		int option = 1;
		
		
		switch(option){
		case caseShooting:
			if(shooting == false){
				shooting = true;
			}	
			else if(shootingSpeedCurrent < shootingSpeed){
				
			}
			break;
		case caseTurret:
			if(turretDegree > 360){
				turretDegree -= 360;
			}else if(turretDegree < 0){
				turretDegree += 360;
			}
			if(turretMotorMoving == false){
				turretMotorMoving = true;
			}
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

}
