package org.gosparx.team1126.robot.subsystem;

import edu.wpi.first.wpilibj.Encoder;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.EncoderData;

import com.ctre.CANTalon;

public class BallAcq extends GenericSubsystem{

	private static final double ROLLER_SPIN = 1.0;

	private static final double ROLLER_STOP = 0;

	private static State currentAcqstatus;

	public static BallAcq ballacq;

	private static Encoder encoder;

	private static  EncoderData encoderData;

	private static CANTalon roller;

	private static CANTalon conveyor;

	private static double distPerTick;

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
				return "Acqing Status Unknown";
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
		// TODO Auto-generated method stub

	}

	protected boolean execute() {
		switch(currentAcqstatus){
		case STANDBY:{
			roller.set(ROLLER_STOP);
			break;
		}
		case ACQING:{
			roller.set(ROLLER_SPIN);
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
		// TODO Auto-generated method stub
		return false;
	}

}
