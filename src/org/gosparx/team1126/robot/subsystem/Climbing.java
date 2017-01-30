package org.gosparx.team1126.robot.subsystem;

import java.security.InvalidParameterException;

import org.gosparx.team1126.robot.util.Logger;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.DriverStation;

import org.gosparx.team1126.robot.IO;

import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//import org.gosparx.team1126.robot.subsystem.Drives;


public class Climbing extends GenericSubsystem {

	private State currentClimbingStatus;
	
	private static Climbing climbing;
	
	private static final double MOTOR_SPIN = 1.0;
	
	private static final double MOTOR_STOP = 0;
	
	private static final double MOTOR_ATTATCH = 0.5;
	
	private DigitalInput ClimbingSensor;
	
	private CANTalon ClimbingMotor;
	
	private static int MotorSpeed = 0;
	
	//private Drives drives;
	
	public Climbing(String name, int priority){
		super(name, priority);
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
			if(ClimbingSensor.get() == true){
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
		
		ClimbingMotor = new CANTalon(IO.CAN_CLIMBING);
		return false;
	}

	@Override
	protected void liveWindow() {
		// TODO Auto-generated method stub
		
	}



	@Override
	protected long sleepTime() {
		// TODO Auto-generated method stub
		return 0;
	}
}