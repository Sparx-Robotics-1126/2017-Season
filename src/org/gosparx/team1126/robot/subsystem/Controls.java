package org.gosparx.team1126.robot.subsystem;

import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.util.AdvancedJoystick;
import org.gosparx.team1126.robot.util.AdvancedJoystick.ButtonEvent;
import org.gosparx.team1126.robot.util.AdvancedJoystick.JoystickListener;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * A class for controlling the inputs from controls.
 */
public class Controls extends GenericSubsystem implements JoystickListener{

	/**
	 * declares a Drives object named drives
	 */
	//private static Drives drives;

	/**
	 * the input from the left joystick
	 */
	private double leftPower;

	/**
	 * the input from the right joystick
	 */
	private double rightPower;

	/**
	 * the deadband on the joystick of which we don't want it to move
	 */
	private static final double DEADBAND = 0.05;

	/**
	 * The instance of driver station
	 */
	private static DriverStation ds;

	/**
	 * Support for singleton
	 */
	private static Controls controls;

	/**
	 * The advanced joystick for the right driver stick
	 */
	private AdvancedJoystick driverRight;

	/**
	 * The advanced joystick for the left driver stick
	 */
	private AdvancedJoystick driverLeft;

	/**
	 * The advanced joystick for the operator
	 */
	private AdvancedJoystick opJoy;

	//xbox mapping
	private static final int XBOX_A = 1;
	private static final int XBOX_B = 2;
	private static final int XBOX_X = 3;
	private static final int XBOX_Y = 4;
	private static final int XBOX_L1 = 5;
	private static final int XBOX_R1 = 6;
	private static final int XBOX_BACK = 7;
	private static final int XBOX_START = 8;
	private static final int XBOX_L3 = 9;
	private static final int XBOX_R3 = 10;
	private static final int XBOX_LEFT_X = 0;
	private static final int XBOX_LEFT_Y = 1;
	private static final int XBOX_L2 = 2;
	private static final int XBOX_R2 = 3;
	private static final int XBOX_RIGHT_X = 4;
	private static final int XBOX_RIGHT_Y = 5;
	private static final int XBOX_POV = 0;

	/**
	 * The outputs for the joysticks 
	 */
	private static final int NEW_JOY_X_AXIS = 0;
	private static final int NEW_JOY_Y_AXIS = 1;
	private static final int NEW_JOY_TRIGGER = 1;//TRIGGEr
	private static final int NEW_JOY_LEFT = 2;//LEFT
	private static final int NEW_JOY_RIGHT = 3;//RIGHT
	private static final int NEW_JOY_MIDDLE = 4;

	private int lastPOV;

	/**
	 * @return the only instance of Controls ever.
	 */
	public static synchronized Controls getInstance(){
		if(controls == null){
			controls = new Controls();
		}
		return controls;
	}

	/**
	 * Creates a new controls
	 */
	private Controls() {
		super("Controls", Thread.MAX_PRIORITY);
	}

	/**
	 * Sets everything up.
	 */
	@Override
	protected boolean init() {
		driverLeft = new AdvancedJoystick("Driver Left", IO.USB_DRIVER_LEFT,4,DEADBAND);
		driverLeft.addActionListener(this);
		driverLeft.addButton(NEW_JOY_LEFT);
		driverLeft.addButton(NEW_JOY_TRIGGER);
		driverLeft.addButton(NEW_JOY_RIGHT);
		driverLeft.addButton(NEW_JOY_MIDDLE);
		driverLeft.start();

		driverRight = new AdvancedJoystick("Driver Right", IO.USB_DRIVER_RIGHT,4,DEADBAND);
		driverRight.addActionListener(this);
		driverRight.addButton(NEW_JOY_LEFT);
		driverRight.addButton(NEW_JOY_MIDDLE);
		driverRight.addButton(NEW_JOY_RIGHT);
		driverRight.addButton(NEW_JOY_TRIGGER);
		driverRight.start();

		opJoy = new AdvancedJoystick("Operator Joy", IO.USB_OPERATOR, 10, 0.25);
		opJoy.addActionListener(this);
		opJoy.addButton(XBOX_Y);
		opJoy.addButton(XBOX_R1);
		opJoy.addButton(XBOX_BACK);
		opJoy.addButton(XBOX_L1);
		opJoy.addButton(XBOX_L2);
		opJoy.addButton(XBOX_START);
		opJoy.addButton(XBOX_B);
		opJoy.addButton(XBOX_A);
		opJoy.addButton(XBOX_X);
		opJoy.addButton(XBOX_Y);
		opJoy.start();

		leftPower = 0;
		rightPower = 0;
		ds = DriverStation.getInstance();

		return true;
	}

	/**
	 * Pointless in this class
	 */
	@Override
	protected void liveWindow() {

	}

	/**
	 * Loops, controls drives
	 */
	@Override
	protected boolean execute() {
		if(ds.isOperatorControl()){
			leftPower = driverLeft.getAxis(NEW_JOY_Y_AXIS);
			rightPower = driverRight.getAxis(NEW_JOY_Y_AXIS);
			//drives.setPower(leftPower, rightPower);
			
			if(opJoy.getPOV(XBOX_POV) == 0 && lastPOV != 0){
			}else if(opJoy.getPOV(XBOX_POV) == 180 && lastPOV != 180){
			}else if(opJoy.getPOV(XBOX_POV) == 270 && lastPOV != 270){
			}else if(opJoy.getPOV(XBOX_POV) == 90 && lastPOV != 90){
			}
			
			if(opJoy.getAxis(XBOX_R2) > .33){
			}
			
			lastPOV = (int) opJoy.getPOV(XBOX_POV);
			
		}
		return false;
	}

	/**
	 * Small sleeps for accurate control
	 */
	@Override
	protected long sleepTime() {
		return 20;
	}

	/**
	 * Writes info to a log every 5 seconds.
	 */
	@Override
	protected void writeLog() {

	}

	public void actionPerformed(ButtonEvent e) {
		if(ds.isOperatorControl()){
			switch(e.getPort()){
			case IO.USB_OPERATOR:
				switch(e.getID()){
				case XBOX_A:
					if(e.isRising()){
					}
					break;
				case XBOX_B:
					if(e.isRising()){
					}
					break;
				case XBOX_Y:
					if(e.isRising()){
					}
					break;
				case XBOX_X:
					if(e.isRising()){
					}
					break;
				case XBOX_L1:
					if(e.isRising()){
					}
					break;
				case XBOX_R1:
					if(e.isRising()){
					}
					break;
				default:
					LOG.logMessage("Bad button id" + e.getID());
				}
			case IO.USB_DRIVER_LEFT:
				switch(e.getID()){
				case NEW_JOY_TRIGGER:
					if(e.isRising()){
					}
					break;
				case NEW_JOY_LEFT:
					if(e.isRising()){
					}
					break;
				case NEW_JOY_RIGHT:
					if(e.isRising()){
					}
					break;
				case NEW_JOY_MIDDLE:
					break;
				}
				break;
			case IO.USB_DRIVER_RIGHT:
				switch(e.getID()){
				case NEW_JOY_MIDDLE:
					if(e.isRising())
					break;
				case NEW_JOY_LEFT:
					if(e.isRising()){
					}
					break;
				case NEW_JOY_RIGHT:
					if(e.isRising())
					break;
				case NEW_JOY_TRIGGER:
					if(e.isRising()){
					}
					break;
				}
				break;
			}
		}
	}
}

