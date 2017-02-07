package org.gosparx.team1126.robot.subsystem;

import edu.wpi.first.wpilibj.DigitalInput; 
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.EncoderData;

import com.ctre.CANTalon;

public class BallAcq extends GenericSubsystem{
//change enum's to booleans today!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	private static final double ROLLER_SPIN = 1.0;

	private static final double ROLLER_STOP = 0;
	
	private static final double CONVEYOR_SPIN = 1.0;
	
	private static final double CONVEYOR_STOP = 0;

	private static State currentAcqstatus;

	public static BallAcq ballacq;

	private static Encoder encoder;

	private static  EncoderData encoderData;

	private static CANTalon roller;

	private static CANTalon conveyor;

	private static double distPerTick;
	
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
		ACQING;
	
		@Override
		public String toString(){
			switch(this){
			case STANDBY:
				return "Ready to acquire";
			case ACQING:
				return "Acquiring balls";
			default:
				return "Acquiring Status Unknown";
			}
		}
		protected boolean init() {
			encoderData = new EncoderData(encoder, distPerTick);
			roller = new CANTalon(IO.CAN_BALLACQ_ROLLER);
			conveyor = new CANTalon (IO.CAN_BALLACQ_CONVEYOR);
			return false;
		}
	}

	protected void liveWindow() {
		String subsystemName = "Gear Acq";
		LiveWindow.addSensor(subsystemName, "Gear Acq Sensor", GearAcqSensor);
		

	}

	protected boolean execute() {
		switch(currentAcqstatus){
		case STANDBY:{
			roller.set(ROLLER_STOP);
			conveyor.set(CONVEYOR_STOP);
			currentAcqstatus = State.STANDBY;
			break;
		}
		case ACQING:{
			roller.set(ROLLER_SPIN);
			currentAcqstatus = State.ACQING;
			conveyor.set(CONVEYOR_SPIN);
			currentAcqstatus = State.ACQING;
				break;
			}
		
		default:
			break;
		}

		return false;
	}
	protected long sleepTime() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	protected boolean init() {
		GearAcqSensor = new DigitalInput(IO.DIO_GEARACQ_ENC);
		encoderData = new EncoderData(encoder, distPerTick);
		roller = new CANTalon(IO.CAN_BALLACQ_ROLLER);
		conveyor = new CANTalon (IO.CAN_BALLACQ_CONVEYOR);
		return false;
	}

}
