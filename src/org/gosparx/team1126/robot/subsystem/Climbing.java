package org.gosparx.team1126.robot.subsystem;

import java.security.InvalidParameterException;

import org.gosparx.team1126.robot.util.Logger;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.DriverStation;

import org.gosparx.team1126.robot.IO;

import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//hello Logan, are you having a nice day? Yes.

public class Climbing extends GenericSubsystem {

	private State currentClimbingStatus;
	
	private static Climbing climbing;
	
	private static final double MOTOR_SPIN = 1.0;
	
	private static final double MOTOR_STOP = 0;
	
	private static final double MOTOR_ATTATCH = 0.5;
	
	private DigitalInput ClimbingSensor;
	
	private CANTalon ClimbingMotor;
	
	private int MotorSpeed;
	
	private boolean isStarted = false;
	
	
	public Climbing(){
		super("Climbing", Thread.NORM_PRIORITY);
	}
	
	/**
	 * This creates a drives object with a name and its priority
	 */
	public static synchronized Climbing getInstance() {
		if(climbing == null){
			climbing = new Climbing();
		}
		return climbing;
	}


	@Override
	protected void writeLog() {
		LOG.logMessage("Climbing Status" + currentClimbingStatus);
	}
	
	public enum State{
		STANDBY,
		ATTATCHING,
		CLIMBING;	

		@Override
		public String toString(){
			switch(this){
				case STANDBY:
					return "Scaling standby";
				case ATTATCHING:
					return "Attatching to rope";
				case CLIMBING:
					return "Climbing";
					default:
				return "Climbing Status Unknown";
			}
	    }
    }

	@Override
	protected boolean execute() {
		readControls();
		switch(currentClimbingStatus){
		case STANDBY:{
			ClimbingMotor.set(MOTOR_STOP);
			
			break;
		}
		case ATTATCHING:{
			ClimbingMotor.set(MOTOR_ATTATCH);
			currentClimbingStatus = State.CLIMBING;
			
			break;
		}
		case CLIMBING:	
			ClimbingMotor.set(MOTOR_SPIN);
			if(ClimbingSensor.get()){
				LOG.logMessage("Rope Has Been Climbed");
				currentClimbingStatus = State.STANDBY;
			}
			break;
		default:
			break;
			}
		return false;		
	}
	
	
	@Override
	protected boolean init() {
		ClimbingSensor = new DigitalInput(IO.DIO_CLIMBING_LIMITSWITCH);
		currentClimbingStatus = State.STANDBY;
		ClimbingMotor = new CANTalon(IO.CAN_CLIMBING);
		return true;
	}

	@Override
	protected void liveWindow() {
		// TODO Auto-generated method stub
		
	}

	private void readControls(){
		if(dsc.getRawAxis(10) >= 0.5 && !isStarted){
			isStarted = true;
			currentClimbingStatus = State.ATTATCHING;
		}
	}
	

	@Override
	protected long sleepTime() {
		// TODO Auto-generated method stub
		return 0;
	}
}