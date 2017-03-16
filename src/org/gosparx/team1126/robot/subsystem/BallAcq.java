package org.gosparx.team1126.robot.subsystem;

import edu.wpi.first.wpilibj.DigitalInput; 
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.subsystem.Drives.DiagnosticState;
import org.gosparx.team1126.robot.util.DriverStationControls;

import com.ctre.CANTalon;

public class BallAcq extends GenericSubsystem{

	private static final double MOTOR_SPIN_FOWARD = 1.0;
	private static final double MOTOR_SPIN_BACKWARD = -1.0;
	private static final double MOTOR_STOP = 0;
	private static final double BELT_RAMP = .1;

	public static BallAcq ballacq;

	private CANTalon acqMotor;
	private CANTalon horizontalBeltMotor;
	private DigitalInput GearAcqSensor;

	private State currentAcqStatus;
	private BeltState currentBeltState;	
	private HorizontalBeltState currentHorizontalBeltState;

	private long startBeltTime;
	private double wantedSpeed;
	private double wantedBeltSpeed; 
	private boolean acqIsAcquiring;
	private String horBeltDir;
	/**
	 * Constructor
	 */
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
		acqMotor = new CANTalon(IO.CAN_BALLACQ_LEFT);
		horizontalBeltMotor = new CANTalon(IO.CAN_HOPPER_HORIZONTAL_BELT);
		GearAcqSensor = new DigitalInput (IO.DIO_GEARACQ_SENSOR);
		acqIsAcquiring = false;
		currentAcqStatus = State.STANDBY;
		currentBeltState = BeltState.STANDBY;
		currentHorizontalBeltState = HorizontalBeltState.STANDBY;
		horBeltDir = "Off";
		
		return true;
	}

	@Override 
	protected void writeLog() {
		//		LOG.logMessage("Acqusition Status" + currentAcqStatus);
	}

	/**
	 * State of Hopper vertical belts
	 */
	public enum BeltState{
		FORWARD,
		FORWARD_WAIT,
		REVERSE,
		REVERSE_WAIT,
		STANDBY;
	}
	
	/**
	 * State of the hopper horizontal belts 
	 */
	public enum HorizontalBeltState{
		RIGHT,
		LEFT,
		STANDBY;
	}

	public enum State{
		STANDBY,
		FORWARD,
		BACKWARD,
		SHOOTING;

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
			wantedSpeed = MOTOR_STOP;
			acqIsAcquiring= false;
			break;
		}
		case FORWARD:{
			wantedSpeed = MOTOR_SPIN_FOWARD;
			acqIsAcquiring = true;
			break;
		}
		case BACKWARD:{
			wantedSpeed = MOTOR_SPIN_BACKWARD;
			acqIsAcquiring = false;
			break;
		}
		case SHOOTING:{
			acqIsAcquiring = false;
			switch(currentBeltState){
			case FORWARD:
				startBeltTime = System.currentTimeMillis();
				wantedSpeed = -1.0;
				currentBeltState = BeltState.FORWARD_WAIT;
				break;
			case FORWARD_WAIT:
				if(System.currentTimeMillis() >= startBeltTime + 1500)
					currentBeltState = BeltState.REVERSE;
				wantedSpeed = -1.0;
				break;
			case REVERSE:
				startBeltTime = System.currentTimeMillis();
				wantedSpeed = 1.0;
				currentBeltState = BeltState.REVERSE_WAIT;
				break;
			case REVERSE_WAIT:
				if(System.currentTimeMillis() >= startBeltTime + 200)
					currentBeltState = BeltState.FORWARD;
				wantedSpeed = 1.0;
				break;
			case STANDBY:
				wantedSpeed = 0;
				break;
			default:
				break;
			}
			break;
		}
		default:
			break;
		}
		
		switch(currentHorizontalBeltState){
		case STANDBY:
			wantedBeltSpeed = 0;
			horBeltDir = "Off";
			break;
		case RIGHT:
			horBeltDir = "Empty Left Bin";
			if(wantedBeltSpeed > -1)
				wantedBeltSpeed -= BELT_RAMP;
			else
				wantedBeltSpeed = -1.0;
			break;
		case LEFT:
			horBeltDir = "Empty Right Bin";
			if(wantedBeltSpeed < 1)
				wantedBeltSpeed += BELT_RAMP;
			else
				wantedBeltSpeed = 1.0;
			break;
		default:
			break;
		}

		acqMotor.set(wantedSpeed);
		horizontalBeltMotor.set(wantedBeltSpeed);
		SmartDashboard.putBoolean("Acquiring?", acqIsAcquiring);
		SmartDashboard.putString("Horizontal Belt Direction: ", horBeltDir);
		return false;
	}

	@Override
	protected long sleepTime() {
		return 20;
	}

	private void setAcqState(){
		if (dsc.isDisabled()){											// Robot Disabled
			currentAcqStatus = State.STANDBY;
			currentBeltState = BeltState.STANDBY;
			currentHorizontalBeltState =  HorizontalBeltState.STANDBY;		
		}else if(dsc.isOperatorControl()){								// Operator Control
			if(dsc.getPOVRising(IO.ACQ_ON)){							// Acquisition System ON
				currentAcqStatus = State.FORWARD;
				currentBeltState = BeltState.STANDBY;
			}else if (dsc.getPOVRising(IO.ACQ_FEED_RIGHT)){				// Shooting Feed System LEFT BIN
				currentAcqStatus = State.SHOOTING;
				currentBeltState = BeltState.FORWARD;
				currentHorizontalBeltState = HorizontalBeltState.RIGHT;
			}else if(dsc.getPOVRising(IO.ACQ_OFF)){						// Acquisition and Shooting Feed System OFF
				currentAcqStatus = State.STANDBY;
				currentBeltState = BeltState.STANDBY;
				currentHorizontalBeltState = HorizontalBeltState.STANDBY;
			}else if(dsc.getPOVRising(IO.ACQ_FEED_LEFT)){				// Shooting Feed System RIGHT BIN
				currentAcqStatus = State.SHOOTING;
				currentBeltState = BeltState.REVERSE;
				currentHorizontalBeltState = HorizontalBeltState.LEFT;
			}
		}	
	}

	public void transport(boolean isShooting){
		if(isShooting){
			currentAcqStatus = State.SHOOTING;
			currentBeltState = BeltState.FORWARD;
			currentHorizontalBeltState = HorizontalBeltState.LEFT;
		}else{
			currentAcqStatus = State.STANDBY;
			currentBeltState = BeltState.STANDBY;
			currentHorizontalBeltState = HorizontalBeltState.STANDBY;
		}
	}
	
	public void autoStuff(int i){
		if(dsc.isAutonomous()){
			switch(i){
			case 1:
				currentHorizontalBeltState = HorizontalBeltState.LEFT;
				break;
			case 2:
				currentHorizontalBeltState = HorizontalBeltState.RIGHT;
				break;
			case 3:
				currentHorizontalBeltState = HorizontalBeltState.STANDBY;
				break;
			default:
				break;
			}
			
		}
	}
	//5s left
	//10s right
}
