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

	private State currentAcqStatus;

	public static BallAcq ballacq;

	private CANTalon leftMotor;

	private CANTalon rightMotor;

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
	protected boolean init(){
		wantedLeftSpeed = 0;
		wantedRightSpeed = 0;
		currentAcqStatus = State.STANDBY;
		ballacq = new BallAcq();
		leftMotor = new CANTalon(IO.CAN_BALLACQ_LEFT);
		rightMotor = new CANTalon(IO.CAN_BALLACQ_RIGHT);
		GearAcqSensor = new DigitalInput (IO.DIO_GEARACQ_ENC);
		
		return true;
	}
	
	@Override 
	protected void writeLog() {
		LOG.logMessage("Acqusition Status" + currentAcqStatus);
	}
	
	public enum State{
		STANDBY,
		FORWARD,
		BACKWARD;

		@Override
		public String toString(){
			switch(this){
			case STANDBY:
				return "BallAcq standby";
			case FORWARD:
				return "BallAcq foward";
			case BACKWARD:
				return "BallAcq backward";	
			default:
				return "Acquiring Status Unknown";
			}
		}
	}

	@Override
	protected void liveWindow() {
		String subsystemName = "Gear Acq";
		LiveWindow.addSensor(subsystemName, "Gear Acq Sensor", GearAcqSensor);

	}

	@Override
	protected boolean execute() {
		setAcqState();
		switch(currentAcqStatus){
		case STANDBY:{
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

		rightMotor.set(wantedRightSpeed);
		leftMotor.set(wantedLeftSpeed);

		return false;
	}
	
	@Override
	protected long sleepTime() {
		return 20;
	}

	private void setAcqState(){
		if(dsc.isAutonomous()){
			currentAcqStatus = State.STANDBY;
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
					currentAcqStatus = State.STANDBY;
				}
				break;
			case 270:
				if(dsc.getPOVRising(6)){
					currentAcqStatus = State.BACKWARD;
				}
				break;
			default:
				break;
			}
		} else {
			currentAcqStatus = State.STANDBY;
		}
	}

}
