package org.gosparx.team1126.robot.subsystem;

import edu.wpi.first.wpilibj.DigitalInput; 
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.EncoderData;

import com.ctre.CANTalon;

public class BallAcq extends GenericSubsystem{

	private static final double ROLLER_SPIN_FOWARD = 1.0;

	private static final double ROLLER_SPIN_BACKWARD = 1.0;
	
	private static final double ROLLER_STOP = 0;
	
	private static final double CONVEYOR_SPIN_FOWARD = 1.0;
	
	private static final double CONVEYOR_SPIN_BACKWARD = 1.0;
	
	private static final double CONVEYOR_STOP = 0;

	private double wantedRollerSpeed;
	
	private double wantedConveyorSpeed;
	
	private static State currentAcqstatus;

	public static BallAcq ballacq;

	private static CANTalon roller;

	private static CANTalon conveyor;

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
		LOG.logMessage("Acqusition Status" + currentAcqstatus);
	}
	public enum State{
		STANDBY,
		FORWARD,
		BACKWARD;
	
		@Override
		public String toString(){
			switch(this){
			case STANDBY:
				return "Ready to acquire";
			case FORWARD:
				return "BallAcq foward";
			case BACKWARD:
				return "BallAcq backward";	
			default:
				return "Acquiring Status Unknown";
			}
		}
	protected boolean init() {
			roller = new CANTalon(IO.CAN_BALLACQ_ROLLER);
			conveyor = new CANTalon(IO.CAN_BALLACQ_CONVEYOR);
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
		switch(currentAcqstatus){
		case STANDBY:{
			wantedRollerSpeed = ROLLER_STOP;
			wantedConveyorSpeed = CONVEYOR_STOP;
			break;
		}
		case FORWARD:{
			wantedRollerSpeed = ROLLER_SPIN_FOWARD;
			wantedConveyorSpeed = CONVEYOR_SPIN_BACKWARD;
				break;
			}
		case BACKWARD:{
			wantedRollerSpeed = ROLLER_SPIN_BACKWARD;
			wantedConveyorSpeed =  CONVEYOR_SPIN_BACKWARD;
				break;
			}
		default:
			break;
		}

		roller.set(wantedRollerSpeed);
		conveyor.set(wantedConveyorSpeed);
		
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
					currentAcqstatus = State.FORWARD;
				}
				break;
			case 90:
				if(dsc.getPOVRising(2)){
					currentAcqstatus = State.FORWARD;
				}
				break;
			case 180:
				if(dsc.getPOVRising(4)){
					currentAcqstatus = State.STANDBY;
				}
				break;
			case 270:
				if(dsc.getPOVRising(6)){
					currentAcqstatus = State.BACKWARD;
				}
				break;
				
			}
			
		} else {
			currentAcqstatus = State.STANDBY;
		}
	}
	
	@Override
	protected boolean init() {
		GearAcqSensor = new DigitalInput(IO.DIO_GEARACQ_ENC);
		roller = new CANTalon(IO.CAN_BALLACQ_ROLLER);
		conveyor = new CANTalon (IO.CAN_BALLACQ_CONVEYOR);
		return false;
	}

}
