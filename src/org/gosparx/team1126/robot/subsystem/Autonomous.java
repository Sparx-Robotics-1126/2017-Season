package org.gosparx.team1126.robot.subsystem;

import java.awt.List;
import java.util.Arrays;

import org.gosparx.team1126.robot.util.CSVReader;
import org.gosparx.team1126.robot.util.SharedData.Target;

import edu.wpi.first.wpilibj.Timer;
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
	private Shooter shooter;
	private BallAcq ballacq;
	private boolean firstRun = true;
	private boolean runAuto;
	
	// All genericSubsystems that the Autonomous system needs to interface with will need to be defined.	

	// private Drives drives;									// An instance of drives

	private static final int DRIVES_FORWARD = 1;				// Drive Straight(inches, speed)
	private static final int DRIVES_TURN = 2;					// Turn (angle, absolute = true)
	private static final int DRIVES_MOVE = 3;					// Drive To (x, y, speed)
	private static final int DRIVES_LIFT = 4;
	private static final int DRIVES_SETCOORDS = 5;
	private static final int DRIVES_STOP = 6;					// Stop the Drives
	private static final int BALLACQ_TOGGLE = 7;
	private static final int SHOOTER_TOGGLE = 8;
	private static final int SHOOTER_SETUP = 9;
	private static final int SHOOTER_SERVO = 10;
	private static final int DELAY = 95;						// Wait (seconds)
	private static final int SETCRITSTEP = 96;					// Set Critical Timeout Step
	private static final int DRIVES_DONE = 97;					// DO NOT USE - Wait For Drives Command is Done
	private static final int WAITING = 98;						// DO NOT USE - Used by Wait command
	public static final int AUTOEND = 99;						// End Autonomous Mode

	// Lookup table to identify the index in the commandName array
	
	private static final int[] commandNumber = {
			DRIVES_FORWARD,										// Distance, Speed
			DRIVES_TURN,										// Degrees, Absolute/Relative
			DRIVES_MOVE,										// X, Y, Speed
			DRIVES_LIFT,
			DRIVES_SETCOORDS,
			DRIVES_STOP,
			BALLACQ_TOGGLE,
			SHOOTER_TOGGLE,
			SHOOTER_SERVO,
			SHOOTER_SETUP,
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
			"Drives_Lift",
			"Drives_SetCoords",
			"Ballacq_Toggle",
			"Shooter_Toggle",
			"Shooter_Servo",
			"Shooter_Setup",
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
		shooter = Shooter.getInstance();
		ballacq = BallAcq.getInstance();
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
			getCurrentAuto();//(int[][]) chooser.getSelected();
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
//			LOG.logMessage("Auto imported");
			currentAuto = reader.readIntCSV("/home/lvuser/Auto");
//			LOG.logMessage(Arrays.deepToString(currentAuto));
			return currentAuto;
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
					//1,<distance>,<speed>
					drives.autoDriveDistance(currentAuto[currStep][1], currentAuto[currStep][2]);
					currCommand = DRIVES_DONE;
					break;
					
				case DRIVES_TURN:
					//2,<angle>,<speed>,<mode (1 == absolute, 2 == relative)>
					if(currentAuto[currStep][3] == 1){
						drives.autoTurnToHeading(currentAuto[currStep][1], currentAuto[currStep][2]);
					} else if(currentAuto[currStep][3] == 2){
						drives.autoTurnToAngle(currentAuto[currStep][1], currentAuto[currStep][2]);
					} else {
						LOG.logMessage("DRIVES_TURN: invalid/missing parameter, defaulting to absolute - are you using an outdated auto?");
						drives.autoTurnToHeading(currentAuto[currStep][1], currentAuto[currStep][2]);
					}
					currCommand = DRIVES_DONE;
					break;
						
				case DRIVES_MOVE:
					//3,<x coord>,<y coord>,<speed>
					drives.autoDriveCoordinate(currentAuto[currStep][1], currentAuto[currStep][2], currentAuto[currStep][3]);
					currCommand = DRIVES_DONE;
					break;
					
				case DRIVES_LIFT:
					//4,<speed>
					dsc.sharedData.targetType = Target.LIFT;
					Timer.delay(3);	//if delay is now implemented in drives remove this else keep
					drives.moveToLift(currentAuto[currStep][1]);
					currCommand = DRIVES_DONE;
					break;
					
				case DRIVES_SETCOORDS:
					//5,<x coord>,<y coord>
					drives.setStartingCoordinate(currentAuto[currStep][1], currentAuto[currStep][2]);
					incStep = true;
					break;
					
				case DRIVES_STOP:
					//6
					if(drives.stopDrives()){
						incStep = true;
					}
					break;
					
				case BALLACQ_TOGGLE:
					//7,<0/1/2 (off/left/right)>,<thingy>
					ballacq.autoChanger(currentAuto[currStep][1],currentAuto[currStep][2]);
					incStep = true;
					break;
					
				case SHOOTER_TOGGLE:
					//8,<1/0 (on/off)>,<speed>
					shooter.shooterSystemState(currentAuto[currStep][1],currentAuto[currStep][2],currentAuto[currStep][3]);
					incStep = true;
					break;
					
				case SHOOTER_SETUP:
					//9,<0/1 (off/on)>
					shooter.shooterSystemFire(currentAuto[currStep][1]);
					incStep = true;
					break;
				
				case SHOOTER_SERVO:
					//10,<0/1/2 (off/forward/backward)>
					shooter.shooterShroud(currentAuto[currStep][1]);
					incStep = true;
					break;
					
				case DELAY:
					//95,<time in milliseconds>
					waitTime = System.currentTimeMillis() + currentAuto[currStep][1];
					currCommand = WAITING;
					break;
					
				case DRIVES_DONE:
					//97
					if(drives.isAutoDone()){
						incStep = true;
					}
					break;

				case WAITING:
					//98
					if (System.currentTimeMillis() >= waitTime){
						incStep = true;
					}
					break;
				
				case SETCRITSTEP:
					//96
					critStep = currentAuto[currStep][1];
					critTime = autoStartTime + currentAuto[currStep][2];
					incStep = true;
					break;

				case AUTOEND:
					//99
					abortCommands(); 
					incStep = false;
					dsc.sharedData.targetType = Target.NONE;
					break;
				
				default:
					incStep = false;
					currCommand = AUTOEND;
					dsc.sharedData.targetType = Target.NONE;
					LOG.logError("Unknown auto command: " + currentAuto[currStep]);
					break;
			}			
		}
	}

	/*
	 * Example auto:

0 - tells that auto file was read correctly, will be changed by reader if there is an issue in background
5,0,0 - tells drives to set current coordinates to 0,0
4,20 - tells drives to move forward at a speed of 20, waits in background for drives to go to standby
6 - tells auto to stop drives (probably unneeded)
99 - tells auto that it is done and to abort all running commands

0 - tells that auto file was read correctly, will be changed by reader if there is an issue in background
5,0,0 - tells drives to set current coordinates to 0,0
3,0,144,60 - tells drives to drive to coords (0,144) with a speed of 60
2,90,40 - tells drives to rotate to 90 degrees from initial heading with a speed of 40
3,96,144,60 - tells drives to drive to coords (96,144) with a speed of 60
2,180,40 - tells drives to rotate to 180 degrees from starting point (initial heading) with a speed of 40
3,96,0,60 - tells drives to drive to coords (96,0) with a speed of 60
2,270,40 - tells drives to rotate to 270 degrees from initial heading with a speed of 40
3,0,0,60 - tells drives to drive to coords
2,0,40 - tells drives to rotate to 0 degrees from initial heading with a speed of 40
99 - tells auto that it is done and to abort all running commands

	 */
	/* auto for shooting !check the logic on this movement
0
5,0,0 - change for actual coords? (where is the gyro on the robot?)
1,-52,40 - moves backwards 52" at a speed of 40
2,-90,40,1 - turns 90* CCW using absolute (2,-90,40,2 for relative)
1,-60,40 - moves backwards 60" at a speed of 40
2,-45,40,1 - turns 45* CW using absolute (2,45,40,2 for relative)
8,1 - gets shooter ready with vision and everything
9,1 - starts servo movement
95,250 - wait 250 ms for servo movement?
9,0 - ends servo movement
10,1 - tells shooter to start shooting when ready!
	 */
	/* auto for driving forward
0
5,0,0
1,86,40
99
	 */

	
	/*************************************************************************************************
	// Aborts active commands in other subsystems
	************************************************************************************************/
	void abortCommands(){
		drives.abortAuto();
		shooter.shooterSystemFire(0);
		shooter.shooterSystemState(0,1450,1);
		//get aborts from other subsystems, maybe a direct command?
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
		if(!runAuto)
			abortCommands();
	}
}