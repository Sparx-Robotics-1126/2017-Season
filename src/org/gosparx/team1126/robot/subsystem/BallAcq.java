package org.gosparx.team1126.robot.subsystem;

import edu.wpi.first.wpilibj.DigitalInput; 
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.EncoderData;

import com.ctre.CANTalon;

public class BallAcq extends GenericSubsystem{
	
	private static final double LEFT_MOTOR_SPIN_FOWARD = 1.0;
	
	private static final double LEFT_MOTOR_SPIN_BACKWARD = -1.0;
	
	private static final double LEFT_MOTOR_STOP = 0;
	
	private static final double RIGHT_MOTOR_SPIN_FOWARD = 1.0;
	
	private static final double RIGHT_MOTOR_SPIN_BACKWARD = -1.0;
	
	private static final double RIGHT_MOTOR_STOP = 0;

	private double wantedLeftSpeed;
	
	private double wantedRightSpeed;
	
	private static State currentAcqStatus;

	public static BallAcq ballacq;

	private static CANTalon leftMotor;
	
	private static CANTalon rightMotor;
	
	private static Encoder encoder;
	
	private static EncoderData encoderData;
	
	private DigitalInput GearAcqSensor;

	private BallAcq(){
		super("BallAcq", Thread.NORM_PRIORITY);
	}

	/**
	 * This creates a drives object with a name and its priority
	 */
	public static synchronized BallAcq getInstance() {
		if(ballacq == null){
			ballacq = new BallAcq();
		}
		return ballacq;
	}
	@Override 
	protected void writeLog() {
		LOG.logMessage("Acqusition Status" + currentAcqStatus);
	}
	public enum State{
		BALLACQON,
		FORWARD,
		BACKWARD;
	
		@Override
		public String toString(){
			switch(this){
			case BALLACQON:
				return "BallAcq on";
			case FORWARD:
				return "BallAcq foward";
			case BACKWARD:
				return "BallAcq backward";	
			default:
				return "Acquiring Status Unknown";
			}
		}
	protected boolean init() {
			leftMotor = new CANTalon(IO.CAN_BALLACQ_LEFT);
			rightMotor = new CANTalon(IO.CAN_BALLACQ_RIGHT);
			return false;
		}
	}

	protected void liveWindow() {
		String subsystemName = "Gear Acq";
		LiveWindow.addSensor(subsystemName, "Gear Acq Sensor", GearAcqSensor);
		
	}

	protected boolean execute() {
		dsc.update();
		setAcqState();
		switch(currentAcqStatus){
		case BALLACQON:{
			wantedLeftSpeed = LEFT_MOTOR_STOP;
			wantedRightSpeed = RIGHT_MOTOR_STOP;
			break;
		}
		case FORWARD:{
			wantedLeftSpeed = LEFT_MOTOR_SPIN_FOWARD;
			wantedRightSpeed = RIGHT_MOTOR_SPIN_FOWARD;
				break;
			}
		case BACKWARD:{
			wantedLeftSpeed = LEFT_MOTOR_SPIN_BACKWARD;
			wantedRightSpeed =  RIGHT_MOTOR_SPIN_BACKWARD;
				break;
			}
		default:
			break;
		}

		rightMotor.set(wantedLeftSpeed);
		leftMotor.set(wantedRightSpeed);
		
		return false;
	}
	protected long sleepTime() {
		// TODO Auto-generated method stub
		return 20;
	}

	private void setAcqState(){
		if(dsc.isAutonomous()){
			//
		}
		if(dsc.isOperatorControl()){
			switch(dsc.getRawPOV()){
			case 0:
				if(dsc.getPOVRising(0)){
					currentAcqStatus = State.FORWARD;
				}
				break;
			case 90:
				if(dsc.getPOVRising(2)){
					currentAcqStatus = State.FORWARD;
				}
				break;
			case 180:
				if(dsc.getPOVRising(4)){
					currentAcqStatus = State.BALLACQON;
				}
				break;
			case 270:
				if(dsc.getPOVRising(6)){
					currentAcqStatus = State.BACKWARD;
				}
				break;
				
			}
			
		} else {
			currentAcqStatus = State.BALLACQON;
		}
	}
	
	@Override
	protected boolean init() {
		GearAcqSensor = new DigitalInput(IO.DIO_GEARACQ_ENC);
		rightMotor = new CANTalon(IO.CAN_BALLACQ_RIGHT);
		leftMotor = new CANTalon (IO.CAN_BALLACQ_LEFT);
		return false;
	}

}
