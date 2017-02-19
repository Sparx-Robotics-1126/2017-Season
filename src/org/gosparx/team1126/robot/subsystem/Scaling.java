package org.gosparx.team1126.robot.subsystem;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;

import org.gosparx.team1126.robot.IO;

//hello Logan, are you having a nice day? Yes.

public class Scaling extends GenericSubsystem {

	private State currentScalingStatus;
	
	private static Scaling scaling;
	
	private static final double MOTOR_SPIN = 1.1;
	
	private static final double MOTOR_STOP = 0;
	
	private static final double MOTOR_ATTATCH = 1.1;
	
	private DigitalInput ScalingSensor;
	
	private CANTalon ScalingMotor;
	
	private int MotorSpeed;
	
	private boolean isStarted = false;
	
	
	public Scaling(){
		super("Scaling", Thread.NORM_PRIORITY);
	}
	
	/**
	 * This creates a drives object with a name and its priority
	 */
	public static synchronized Scaling getInstance() {
		if(scaling == null){
			scaling = new Scaling();
		}
		return scaling;
	}


	@Override
	protected void writeLog() {
//		LOG.logMessage("Scaling Status" + currentScalingStatus);
	}
	
	public enum State{
		STANDBY,
		ATTATCHING,
		SCALING;	

		@Override
		public String toString(){
			switch(this){
				case STANDBY:
					return "Scaling standby";
				case ATTATCHING:
					return "Attatching to rope";
				case SCALING:
					return "Scaling";
					default:
				return "Scaling Status Unknown";
			}
	    }
    }

	@Override
	protected boolean execute() {
		readControls();
		switch(currentScalingStatus){
		case STANDBY:{
			ScalingMotor.set(MOTOR_STOP);
			
			break;
		}
		case ATTATCHING:{
			ScalingMotor.set(MOTOR_ATTATCH);
			currentScalingStatus = State.SCALING;
			
			break;
		}
		case SCALING:	
			ScalingMotor.set(MOTOR_SPIN);
			if(ScalingSensor.get()){
				LOG.logMessage("Rope Has Been Climbed");
				currentScalingStatus = State.STANDBY;
			}
			break;
		default:
			break;
			}
		return false;		
	}
	
	@Override
	protected boolean init() {
		ScalingSensor = new DigitalInput(IO.DIO_CLIMBING_LIMITSWITCH);
		currentScalingStatus = State.STANDBY;
		ScalingMotor = new CANTalon(IO.CAN_CLIMBING_WINCH);
		return true;
	}

	@Override
	protected void liveWindow() {
		
		
	}

	private void readControls(){
		if(dsc.getRawAxis(dsc.OP_XBOX_R2) >= 0.5)
			currentScalingStatus = State.ATTATCHING;
		else
			currentScalingStatus = State.STANDBY;
	}
	

	@Override
	protected long sleepTime() {
		
		return 20;
	}
}