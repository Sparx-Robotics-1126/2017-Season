package org.gosparx.team1126.robot.subsystem;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.gosparx.team1126.robot.IO;

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
			isStarted = false;
			break;
		}
		case ATTATCHING:{
			ScalingMotor.set(dsc.getRawAxis(IO.SCALING_AXIS));
			currentScalingStatus = State.SCALING;
			isStarted = true;
			break;
		}
		case SCALING:	
			ScalingMotor.set(dsc.getRawAxis(IO.SCALING_AXIS));
			isStarted = true;
			if(ScalingSensor.get()){
				LOG.logMessage("Rope Has Been Climbed");
				currentScalingStatus = State.STANDBY;
			}
			break;
		default:
			break;
			}
		SmartDashboard.putBoolean("Climbing?", isStarted);
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
		//for using the right trigger
		
//		if(dsc.getRawAxis(IO.SCALING_RT) >= 0.5)
//			currentScalingStatus = State.ATTATCHING;
//		else
//			currentScalingStatus = State.STANDBY;
		
		//for using the right y-axis on xbox controller
		if(dsc.getRawAxis(IO.SCALING_AXIS) < .25){
			currentScalingStatus = State.STANDBY;
		}else{
			currentScalingStatus = State.ATTATCHING;
		}
	}
	

	@Override
	protected long sleepTime() {
		
		return 20;
	}
}