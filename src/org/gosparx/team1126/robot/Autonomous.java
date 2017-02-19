package org.gosparx.team1126.robot;

import java.awt.List;

import org.gosparx.team1126.robot.subsystem.Drives;
import org.gosparx.team1126.robot.subsystem.GenericSubsystem;
import org.gosparx.team1126.robot.util.CSVReader;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*****************************************************************************************************
 * A class for handling the autonomous functions of the robot
****************************************************************************************************/
public class Autonomous extends GenericSubsystem{

	private static Autonomous auto;								// Support for singleton
	private SendableChooser<int[][]> chooser;					// Selector for AutoMode
	private int[][] currentAuto;								// Current autonomous mode data
	private int currStep = 0;									// The current step of the auto we are performing
	private int currCommand = 0;								// Current command
	private int critStep = 0;									// The "critical" step of auto, what must happen if all else fails.
	private boolean incStep = true;								// Should we move to the next step?
	private long waitTime = 0;									// When we should stop waiting
	private long critTime = 0;									// When we need to do this step by
	private long autoStartTime;									// When we started auto
	private boolean fromFile = true;							// Check if Autonomous mode should be read from local file.
	private long lastRead = 0;
	private CSVReader reader;
	private Drives drives;
	private boolean firstRun = true;
	private boolean runAuto;
	
	// All genericSubsystems that the Autonomous system needs to interface with will need to be defined.	

	// private Drives drives;									// An instance of drives

	private static final int DRIVES_FORWARD = 1;				// Drive Straight(inches, speed)
	private static final int DRIVES_TURN = 2;					// Turn (angle, absolute = true)
	private static final int DRIVES_MOVE = 3;					// Drive To (x, y, speed)
	private static final int DRIVES_SETCOORDS = 4;
	private static final int DRIVES_STOP = 5;					// Stop the Drives
	private static final int DELAY = 6;							// Wait (seconds)
	private static final int SETCRITSTEP = 7;					// Set Critical Timeout Step
	private static final int DRIVES_DONE = 97;					// DO NOT USE - Wait For Drives Command is Done
	private static final int WAITING = 98;						// DO NOT USE - Used by Wait command
	public static final int AUTOEND = 99;						// End Autonomous Mode

	// Lookup table to identify the index in the commandName array
	
	private static final int[] commandNumber = {
			DRIVES_FORWARD,										// Distance, Speed
			DRIVES_TURN,										// Degrees, Absolute/Relative
			DRIVES_MOVE,										// X, Y, Speed
			DRIVES_SETCOORDS,
			DRIVES_STOP,
			DRIVES_DONE,
			SETCRITSTEP,										// Crit Step #, Time (msec) 
			DELAY,												// Time (msec)
			WAITING,
			AUTOEND
	};
	
	private static final String[] commandName = {
			"Drives_Forward",
			"Drives_Turn",
			"Drives_Move",
			"Drives_SetCoords",
			"Drives_Stop",
			"Drives_Done - DO NOT USE",
			"Set Critical Step",
			"Delay",
			"Waiting - DO NOT USE",
			"End"
	};
	
	/*************************************************************************************************
	 * Start Preset Arrays
	************************************************************************************************/
	// Examples from 2016
	
	private final int[][] LOW_BAR_GOAL = {
			{DRIVES_FORWARD, 94},
			{AUTOEND}
	};

	private final int[][] REACH_DEF = {
			{DRIVES_FORWARD, 50},
			{AUTOEND}
	};

	private final int[][] EMPTY = {
			{AUTOEND}
	};
		
	/*************************************************************************************************
	 * Private constructor to aid in singleton.
	************************************************************************************************/
	private Autonomous() {
		super("Autonomous", Thread.NORM_PRIORITY);
	}

	/*************************************************************************************************
	 * @return The only instance of Autonomous
	************************************************************************************************/
	public static Autonomous getInstance(){
		if(auto == null)
			auto  = new Autonomous();

		return auto;
	}

	/*************************************************************************************************
	 * Performed once when the subsystems .start() method is called.
	************************************************************************************************/
	@Override
	protected boolean init() {

		drives = Drives.getInstance();
		reader = new CSVReader();
		
		chooser = new SendableChooser<int[][]>();
		chooser.addDefault("Do Nothing", EMPTY);
		chooser.addObject("Reach Defense", LOW_BAR_GOAL);
		chooser.addObject("Low Bar - Score", REACH_DEF);

		SmartDashboard.putData("Auto Chooser", chooser);
		return true;
	}

	/*************************************************************************************************
	 * Loops after .start() is called.
	************************************************************************************************/
	@Override
	protected boolean execute() {
		if(dsc.isEnabled() && dsc.isAutonomous() && runAuto){
			runAuto();
		}else if (dsc.isDisabled() && dsc.isAutonomous()){
			currentAuto = getCurrentAuto();//(int[][]) chooser.getSelected();
			currStep = 0;
			autoStartTime = System.currentTimeMillis();
			incStep = true;
			critTime = 0;
			critStep = 0;
			//abortCommands();
		}
		
		return false;
	}
	
	private int[][] getCurrentAuto(){
		if(fromFile && System.currentTimeMillis() > lastRead + 10000 || fromFile && firstRun){
			firstRun = false;
			lastRead = System.currentTimeMillis();
			LOG.logMessage("Auto imported");
			return reader.readIntCSV("/home/lvuser/Auto");
		} else if (fromFile){
		} else {
			return chooser.getSelected();
		}
		return currentAuto;
	}
	
	/*************************************************************************************************
	 * Actually loops through auto commands
	************************************************************************************************/
	private void runAuto(){
		if(dsc.isEnabled() && dsc.isAutonomous()){
			if ((System.currentTimeMillis() >= critTime) && 			// Check for Critical Time
					(currStep < critStep) && (critTime > 0)){
				incStep = true;											// Inform system we are changing steps
				currStep = critStep - 1;								// Set step number (incStep adds +1)
				critTime = 0;											// Clear the critical time
				LOG.logMessage("Crit Step Triggered: " + critStep);
			}
		
			if (incStep == true){										// Previous command done?
				currStep++;												// Increment to next step
				currCommand = currentAuto[currStep][0];					// Get the "next step" command #
				incStep = false;
				LOG.logMessage("runAuto step: " + 
						lookupCommandName(currCommand));				// Log the "next step" Name
			}

			if ((currStep < 0) || (currStep >= currentAuto.length)){	// Check for invalid command step
				currCommand = AUTOEND;									// Default Step is to END
				LOG.logMessage("runAuto invalid step #" + currStep);
			}
			
			if (currentAuto[0][0] < 0){
				currCommand = AUTOEND;
				LOG.logMessage("Failed to read file");
			}
			
			switch(currCommand){
				case DRIVES_FORWARD:
					drives.autoDriveDistance(currentAuto[currStep][1], currentAuto[currStep][2]);
					if(drives.isAutoDone())
					{
						incStep = true;
					}
					break;
					
				case DRIVES_TURN:
					drives.autoTurn(currentAuto[currStep][1], currentAuto[currStep][2]);
					if(drives.isAutoDone())
					{
						incStep = true;
					}
					break;
						
				case DRIVES_MOVE:
					drives.autoDriveCoordinate(currentAuto[currStep][1], currentAuto[currStep][2], currentAuto[currStep][3]);
					if(drives.isAutoDone())
					{
						incStep = true;
					}
					break;
					
				case DRIVES_SETCOORDS:
					drives.setStartingCoordinate(currentAuto[currStep][1], currentAuto[currStep][2]);
					incStep = true;
					break;
					
				case DRIVES_STOP:
					if(drives.stopDrives()){
						incStep = true;
					}
					break;
				
				case DRIVES_DONE:
					if(drives.isAutoDone()){
						incStep = true;
					}
					break;
				
				case DELAY:
					waitTime = System.currentTimeMillis() + currentAuto[currStep][1];
					currCommand = WAITING;
					break;

				case WAITING:
					if (System.currentTimeMillis() >= waitTime){
						incStep = true;
					}
					break;
				
				case SETCRITSTEP:
					critStep = currentAuto[currStep][1];
					critTime = autoStartTime + currentAuto[currStep][2];
					incStep = true;
					break;

				case AUTOEND:
					abortCommands(); 
					incStep = false;
					break;
				
				default:
					incStep = false;
					currCommand = AUTOEND;
					LOG.logError("Unknown auto command: " + currentAuto[currStep]);
					break;
			}			
		}
	}

	/*************************************************************************************************
	// Aborts active commands in other subsystems
	************************************************************************************************/
	void abortCommands(){
		drives.abortAuto();
	}
	
	/*************************************************************************************************
	// Looks up the command name based on the command number.  Used for logging.
	************************************************************************************************/
	String lookupCommandName(int command){
		int i;
		
		for(i=0; i < commandNumber.length; i++){
			if (commandNumber[i] == command)
				return commandName[i];
		}
		return "Unknown Command";
	}
	
	/*************************************************************************************************
	*  Updates the livewindow functions
	************************************************************************************************/
	@Override
	protected void liveWindow() {
	}	

	/*************************************************************************************************
	 * @return How long to sleep between loops
	************************************************************************************************/
	@Override
	protected long sleepTime() {
		return 20;
	}

	/*************************************************************************************************
	 * Writes to the log file every 5 seconds.
	************************************************************************************************/
	@Override
	protected void writeLog() {
	}
	
	public void setRunAuto(boolean auto){
		runAuto = auto;
	}
}