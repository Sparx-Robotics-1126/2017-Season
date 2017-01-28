package org.gosparx.team1126.robot;

import org.gosparx.team1126.robot.subsystem.GenericSubsystem;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A class for handling the autonomous functions of the robot
 * @author Alex Mechler {amechler1998@gmail.com}
 */
public class Autonomous extends GenericSubsystem{

	/**
	 * Support for singleton
	 */
	private static Autonomous auto;

	/**
	 * The selector for the AutoMode
	 */
	private SendableChooser chooser;

	private int[][] currentAuto;								// Stores the current autonomous
	private int currStep = 0;									// The current step of the auto we are performing
	private boolean incStep = true;								// Should we move to the next step?
	private boolean runAuto = false;							// Are we running auto?
	private boolean waiting = false;							// Are we waiting before moving to the next step?
	private double waitTime = 0.0;								// When we should stop waiting
	private int critStep = 0;									// The "critical" step of auto, what must happen if all else fails.
	private double critTime = 0.0;								// When we need to do this step by
	private double autoStartTime;								// When we started auto
	private boolean checkTime = false;							// Are we checking the time for a critical step?
//	private Drives drives;										// An instance of drives

	/**
	 * START PRESET ARRAYS
	 */

	// Examples from 2016
	
	private final String LOW_BAR_GOAL_NAME = "Low bar to low goal";
	private final Integer LOW_BAR_GOAL_NUM = 0;
	private final int[][] LOW_BAR_GOAL = {
			{AutoCommand.DRIVES_FORWARD.toId(), 168},
			{AutoCommand.DRIVES_FORWARD.toId(), 72},
			{AutoCommand.DRIVES_FORWARD.toId(), 140},
			{AutoCommand.DRIVES_STOP.toId()},
			{AutoCommand.END.toId()}
	};

	private final String REACH_DEF_NAME = "Reach a Defense";
	private final Integer REACH_DEF_NUM = 1;
	private final int[][] REACH_DEF = {
			{AutoCommand.DRIVES_FORWARD.toId(), 50},
			{AutoCommand.END.toId()}
	};

	private final String EMPTY_NAME = "La tortuga (Do nothing)";
	private final Integer EMPTY_NUM = 99;
	private final int[][] EMPTY = {
			{AutoCommand.END.toId()}
	};

	/**
	 * Enum of all possible autocommands
	 */
	public enum AutoCommand{
		DRIVES_FORWARD(1),										// Drive Straight(inches, speed)
		DRIVES_TURN(2),											// Turn (angle, absolute = true)
		DRIVES_MOVE(3),											// Drive To (x, y, speed)
		DRIVES_STOP(4),											// Stop the Drives
		DRIVES_DONE(5),											// Wait Until Drives Command is Done
		DELAY(98),												// Wait (seconds)
		END(99);												// End Autonomous Mode

		private int id;											// The ID of the autocommand
		
		/**
		 * Creates a new AutoCommand
		 * @param id The autoCommand ID
		 */
		private AutoCommand(int id){
			this.id = id;
		}

		// return The ID of this AutoCommand
		
		public int toId(){
			return id;
		}

		/**
		 * @param id The desired autocommands id
		 * @return An autocommand with the matching ID
		 */
		public static AutoCommand fromId(int id){
			for(AutoCommand ac: AutoCommand.values()){
				if(ac.toId() == id)
					return ac;
			}
			throw new RuntimeException("No auto exists for ID " + id);
		}

		/**
		 * Gets the name of the state
		 */
		@Override
		public String toString(){
			switch(this){
			case DRIVES_FORWARD:
				return "DRIVES_FORWARD";
			case DRIVES_TURN:
				return "DRIVES_TURN";
			case DRIVES_MOVE:
				return "DRIVES_MOVE";
			case DRIVES_STOP:
				return "DRIVES_STOP";
			case DELAY:
				return "DELAY";
			case END:
				return "END";

			default:
				return "Error :( Auto in " + this;
			}
		}
	}

	/**
	 * Private constructor to aid in singleton.
	 */
	private Autonomous() {
		super("Autonomous", Thread.NORM_PRIORITY);
	}

	/**
	 * @return The only instance of Autonomous
	 */
	public static Autonomous getInstance(){
		if(auto == null){
			auto  = new Autonomous();
		}
		return auto;
	}

	/**
	 * Performed once when the subsystems .start() method is called.
	 */
	@Override
	protected boolean init() {

//		drives = Drives.getInstance();

		chooser = new SendableChooser();
		chooser.addDefault(EMPTY_NAME, EMPTY_NUM);
		chooser.addObject(REACH_DEF_NAME, REACH_DEF_NUM);
		chooser.addObject(LOW_BAR_GOAL_NAME, LOW_BAR_GOAL_NUM);

		SmartDashboard.putData("Auto Chooser", chooser);
		return true;
	}

	/**
	 * Loops after .start() is called.
	 */
	@Override
	protected boolean execute() {
		if(runAuto && dsc.isEnabled()){
			runAuto();
		}else{
			currStep = 0;
			autoStartTime = Timer.getFPGATimestamp();
		}
		return false;
	}

	/**
	 * Actually loops through auto commands
	 */
	private void runAuto(){
		incStep = true;
		
		if(dsc.isEnabled() && dsc.isAutonomous() && (currStep < currentAuto.length)){
			switch(AutoCommand.fromId(currentAuto[currStep][0])){
			case DRIVES_FORWARD:
				LOG.logMessage("runAuto step: " + AutoCommand.fromId(currentAuto[currStep][0]).toString());
//				Place Drives Command Here
				break;
				
			case DRIVES_STOP:
				LOG.logMessage("runAuto step: " + AutoCommand.fromId(currentAuto[currStep][0]).toString());
//				Place Drives Command Here;
				break;
				
			case DELAY:
				if(!waiting){
					waiting = true;
					waitTime = Timer.getFPGATimestamp() + currentAuto[currStep][1];
				}
				break;
				
			case END:
				break;
				
			default:
				incStep = false;
				LOG.logError("Unknown auto command: " + currentAuto[currStep]);
				break;
			}
			
			if(waiting && waitTime < Timer.getFPGATimestamp()){
				waiting = false;
				waitTime = Double.MAX_VALUE;
				incStep = true;
			}else if(waiting){
				incStep = false;
			}

			if(incStep){
				currStep++;
			}

			if(checkTime && Timer.getFPGATimestamp() - autoStartTime >= critTime && currStep < critStep){
				checkTime = false;
				currStep = critStep;
				LOG.logMessage("Jumping to crit step: " + critStep);
			}
		}
	}

	/**
	 * Updates the livewindow functions
	 */
	@Override
	protected void liveWindow() {
	}	

	/**
	 * @return How long to sleep between loops
	 */
	@Override
	protected long sleepTime() {
		return 20;
	}

	/**
	 * Writes to the log file every 5 seconds.
	 */
	@Override
	protected void writeLog() {

	}

	public void setRunAuto(boolean n){
		runAuto = n;
		//LOG.logMessage("runAuto: " + n + " auto " + SmartDashboard.getString("Auto Name: ", "error"));
	}
}
