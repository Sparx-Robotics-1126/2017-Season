package org.gosparx.team1126.robot.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Joystick;

public class DriverStationControls {

	// General Joystick Data
	
	private static final int maxJoysticks = 3;
	private static final int maxButtons = 18;
	private static final int maxAxes = 14;
	private static final int maxPOVs = 8;
	private static final int leftJoystickButtons = -1;
	private static final int rightJoystickButtons = 3;
	private static final int xboxControllerButtons = 7;
	private static final int leftJoystickAxis = 0;
	private static final int rightJoystickAxis = 2;
	private static final int xboxControllerAxis = 4;

	// Generic Joystick Mapping
	
	public static final int JOY_X_AXIS = 0;
	public static final int JOY_Y_AXIS = 1;
	public static final int JOY_Z_AXIS = 2;
	public static final int JOY_W_AXIS = 0;

	public static final int JOY_TRIGGER = 1;
	public static final int JOY_LEFT = 2;
	public static final int JOY_RIGHT = 3;
	public static final int JOY_MIDDLE = 4;

	// Specific Joystick Mapping
	
	public static final int LEFT_JOY_X_AXIS = leftJoystickAxis + JOY_X_AXIS;
	public static final int LEFT_JOY_Y_AXIS = leftJoystickAxis + JOY_Y_AXIS;
	public static final int LEFT_JOY_Z_AXIS = leftJoystickAxis + JOY_Z_AXIS;
	public static final int LEFT_JOY_W_AXIS = leftJoystickAxis + JOY_W_AXIS;

	public static final int LEFT_JOY_TRIGGER = leftJoystickButtons + JOY_TRIGGER;
	public static final int LEFT_JOY_LEFT = leftJoystickButtons + JOY_LEFT;
	public static final int LEFT_JOY_RIGHT = leftJoystickButtons + JOY_RIGHT;
	public static final int LEFT_JOY_MIDDLE = leftJoystickButtons + JOY_MIDDLE;
	
	public static final int RIGHT_JOY_X_AXIS = rightJoystickAxis + JOY_X_AXIS;
	public static final int RIGHT_JOY_Y_AXIS = rightJoystickAxis + JOY_Y_AXIS;
	public static final int RIGHT_JOY_Z_AXIS = rightJoystickAxis + JOY_Z_AXIS;
	public static final int RIGHT_JOY_W_AXIS = rightJoystickAxis + JOY_W_AXIS;

	public static final int RIGHT_JOY_TRIGGER = rightJoystickButtons + JOY_TRIGGER;
	public static final int RIGHT_JOY_LEFT = rightJoystickButtons + JOY_LEFT;
	public static final int RIGHT_JOY_RIGHT = rightJoystickButtons + JOY_RIGHT;
	public static final int RIGHT_JOY_MIDDLE = rightJoystickButtons + JOY_MIDDLE;

	// Generic XBox Mapping

	public static final int XBOX_LEFT_X = 0;
	public static final int XBOX_LEFT_Y = 1;
	public static final int XBOX_RIGHT_X = 2;
	public static final int XBOX_RIGHT_Y = 3;
	public static final int XBOX_L2 = 4;
	public static final int XBOX_R2 = 5;
	
	public static final int XBOX_POV = 0;
	public static final int XBOX_A = 1;
	public static final int XBOX_B = 2;
	public static final int XBOX_X = 3;
	public static final int XBOX_Y = 4;
	public static final int XBOX_L1 = 5;
	public static final int XBOX_R1 = 6;
	public static final int XBOX_BACK = 7;
	public static final int XBOX_START = 8;
	public static final int XBOX_L3 = 9;
	public static final int XBOX_R3 = 10;

	// XBox Mapping
	
	public static final int OP_XBOX_LEFT_X = xboxControllerAxis + 0;
	public static final int OP_XBOX_LEFT_Y = xboxControllerAxis + 1;
	public static final int OP_XBOX_L2 = xboxControllerAxis + 2;
	public static final int OP_XBOX_R2 = xboxControllerAxis + 3;
	public static final int OP_XBOX_RIGHT_X = xboxControllerAxis + 4;
	public static final int OP_XBOX_RIGHT_Y = xboxControllerAxis + 5;
	
	public static final int OP_XBOX_POV = xboxControllerButtons + 0;
	public static final int OP_XBOX_A = xboxControllerButtons + XBOX_A;
	public static final int OP_XBOX_B = xboxControllerButtons + XBOX_B;
	public static final int OP_XBOX_X = xboxControllerButtons + XBOX_X;
	public static final int OP_XBOX_Y = xboxControllerButtons + XBOX_Y;
	public static final int OP_XBOX_L1 = xboxControllerButtons + XBOX_L1;
	public static final int OP_XBOX_R1 = xboxControllerButtons + XBOX_R1;
	public static final int OP_XBOX_BACK = xboxControllerButtons + XBOX_BACK;
	public static final int OP_XBOX_START = xboxControllerButtons + XBOX_START;
	public static final int OP_XBOX_L3 = xboxControllerButtons + XBOX_L3;
	public static final int OP_XBOX_R3 = xboxControllerButtons + XBOX_R3;
		
	// Internal private variables (static - Global for all objects)
	
	private static DriverStation ds;
	private static Joystick joysticks[] = new Joystick[3];
	public static SharedData sharedData;

	// Joystick button lookup table (0, 1 = Standard Joystick, 2 = XBox Controller)
	
	private static final int[][] buttons = {							// {Joystick #, Raw Button #}
			{0, JOY_TRIGGER},											// Index 0 - Start of Joystick #1
			{0, JOY_LEFT},
			{0, JOY_RIGHT},
			{0, JOY_MIDDLE},
			{1, JOY_TRIGGER},											// Index 4 - Start of Joystick #2
			{1, JOY_LEFT},
			{1, JOY_RIGHT},
			{1, JOY_MIDDLE},
			{2, XBOX_A},												// Index 8 - Start of Joystick #3 (XBox)
			{2, XBOX_B},
			{2, XBOX_X},
			{2, XBOX_Y},
			{2, XBOX_L1},
			{2, XBOX_R1},
			{2, XBOX_BACK},
			{2, XBOX_START},
			{2, XBOX_L3},
			{2, XBOX_R3}
	};
	
	// Time (in milliseconds - from the system.CurrentTimeMillis()) that the last press or release of a button occurred
	//	 There is a static version that is continually updated, and a local version used for each object and is updated
	//	 when called the appropriate routine.
	
	private static long[][]POVData = {
			{0,0},														// Stores last POV from update()
			{0,0},														// Stores rising edge information (right is from update())
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0}								// Stores falling edge information (right is from update())
	};
	
	private static long[][]POVDataGlobal = {
			{0,0},														// Stores last POV from update()
			{0,0},														// Stores rising edge information (right is from update())
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0}								// Stores falling edge information (right is from update())
	};

	private static boolean[] POVLastValues = {
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false
	};
	
	private static long[][]buttonDataGlobal = {							// {Rising Edge, Falling Edge})
			{0,0},														// Joystick 0 (Standard)
			{0,0},
			{0,0},
			{0,0},
			{0,0},														// Joystick 1 (Standard)
			{0,0},
			{0,0},
			{0,0},
			{0,0},														// Joystick 2 (XBox)
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0}
	};
	
	private long[][]buttonData = {										// {Rising Edge, Falling Edge}
			{0,0},														// Joystick 0 (Standard)
			{0,0},
			{0,0},
			{0,0},
			{0,0},														// Joystick 1 (Standard)
			{0,0},
			{0,0},
			{0,0},
			{0,0},														// Joystick 2 (XBox)
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0},
			{0,0}
	};

	// Last button values - Static - Used to determine when the button value has changed by looking at the value the last
	//	 time the routine was run.  The initial state is "false" (not pressed).
	
	private static boolean[] buttonLastValues = {						// { Last Button Value }
			false,														// Joystick 0 (Standard)
			false,
			false,
			false,
			false,														// Joystick 1 (Standard)
			false,
			false,
			false,
			false,														// Joystick 2 (XBox)
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false
	};
	
	// Joystick Axis Look-up Table
	
	private static final int[][] axes = {								// { Joystick #, Axis # }
			{0, JOY_X_AXIS},											// Joystick 0 (Standard)
			{0, JOY_Y_AXIS},
			{0, JOY_Z_AXIS},
			{0, JOY_W_AXIS},
			{1, JOY_X_AXIS},											// Joystick 1 (Standard)
			{1, JOY_Y_AXIS},
			{1, JOY_Z_AXIS},
			{1, JOY_W_AXIS},
			{2, XBOX_LEFT_X},											// Joystick 2 (Standard)
			{2, XBOX_LEFT_Y},
			{2, XBOX_RIGHT_X},
			{2, XBOX_RIGHT_Y},
			{2, XBOX_L2},
			{2, XBOX_R2}
	};
	
	private static final int[][] povs = {
			{2, 0},
			{2, 45},
			{2, 90},
			{2, 135},
			{2, 180},
			{2, 225},
			{2, 270},
			{2, 315}
	};
	
	private double[][] axesData = {										// { Deadband, axis invert }
			{0.05,1.0},													// Joystick 0 (Standard)
			{0.05,1.0},
			{0.05,1.0},
			{0.05,1.0},
			{0.05,1.0},													// Joystick 1 (Standard)
			{0.05,1.0},
			{0.05,1.0},
			{0.05,1.0},
			{0.05,1.0},													// Joystick 2 (XBox)
			{0.05,1.0},
			{0.05,1.0},
			{0.05,1.0},
			{0.05,1.0},
			{0.05,1.0}
	};
	
	//-----------------------------------------------------------------------------------------------------------
	// Constructors - If the first one created, then create the static objects, get a pointer to the driver
	//	station object and initialize variables
	//-----------------------------------------------------------------------------------------------------------
	
	public DriverStationControls(boolean reset)
	{
		int i;
		
		createObjects(reset);

		for (i=0; i< maxButtons; i++){									// Set buttons to the current values
			buttonLastValues[i] = getButton(i);
		}
	}
	
	public DriverStationControls()
	{
		int i;
		
		createObjects(false);

		for (i=0; i< maxButtons; i++){									// Set buttons to the current values
			buttonLastValues[i] = getButton(i);
		}
	}
	
	//-----------------------------------------------------------------------------------------------------------
	// Return if the specified POV is pressed
	//-----------------------------------------------------------------------------------------------------------
		
	public boolean getPOV(int POVNumber)
	{
		if ((POVNumber >= 0) &&(POVNumber < maxPOVs))
			if(joysticks[2].getPOV() == povs[POVNumber][1]){
				return true;
			}
		return false;
	}
	
	//-----------------------------------------------------------------------------------------------------------
	// Return the current value of the specified POV
	//-----------------------------------------------------------------------------------------------------------
		
	public int getRawPOV(){
		return joysticks[2].getPOV();
	}
		//-----------------------------------------------------------------------------------------------------------
	// Return if the POV is pressed (rising edge) since the last time this method was called by the
	// owner (subsystem).
	//-----------------------------------------------------------------------------------------------------------
	
	public boolean getPOVRising(int POVNumber){
		
		boolean rising = false;
		
		if ((POVNumber >= 0) && (POVNumber < maxPOVs)){
			if (POVData[POVNumber][0] < POVDataGlobal[POVNumber][0]){
				if (POVData[POVNumber][0] > 0)
				{
					rising = true;
				}
				POVData[POVNumber][0] = POVDataGlobal[POVNumber][0];
			}
		}
			return rising;
	}
	
	//-----------------------------------------------------------------------------------------------------------
	// Return if the POV is released (falling edge) since the last time this method was called by the
	// owner (subsystem).
	//-----------------------------------------------------------------------------------------------------------
	
	public boolean getPOVFalling(int POVNumber){
		
		boolean falling = false;
				
		if ((POVNumber >= 0) && (POVNumber < maxPOVs)){
			if (POVData[POVNumber][1] < POVDataGlobal[POVNumber][1]){
				if (POVData[POVNumber][1] > 0)
				{
					falling = true;
				}
				POVData[POVNumber][1] = POVDataGlobal[POVNumber][1];
			}
		}
		return falling;
	}
	
	//-----------------------------------------------------------------------------------------------------------
	// Create the objects if they haven't already been created.
	//-----------------------------------------------------------------------------------------------------------
	
	private void createObjects(boolean reset)
	{
		if ((ds == null) || (reset == true))							// Get Instance of driver station
			ds = DriverStation.getInstance();
		
		if ((joysticks[0] == null) || (reset == true))
			joysticks[0] = new Joystick(0);								// Create the Left driver Joystick
		
		if ((joysticks[1] == null) || (reset == true))
			joysticks[1] = new Joystick(1);								// Create the Right Driver Joystick
		
		if ((joysticks[2] == null) || (reset == true))
			joysticks[2] = new Joystick(2);								// Create the XBox Controller #2
			
		if (sharedData == null)											// Create the vehicle to share data
			sharedData = new SharedData();								//  between subsystems.
	}
	
	
	//-----------------------------------------------------------------------------------------------------------
	// Return the current value of the specified button
	//-----------------------------------------------------------------------------------------------------------
	
	public boolean getButton(int buttonNumber)
	{
		if ((buttonNumber >= 0) && (buttonNumber < maxButtons))			// Check for valid button number
			return joysticks[buttons[buttonNumber][0]].					// Return current value
					getRawButton(buttons[buttonNumber][1]);

		return false;													// Invalid button number value
	}

	//-----------------------------------------------------------------------------------------------------------
	// Returns true if the passed button number is pressed. 
	//-----------------------------------------------------------------------------------------------------------

	public boolean isPressed(int buttonNumber)
	{
		if ((buttonNumber >= 0) && (buttonNumber < maxButtons))			// Check for valid button number
			return (getButton(buttonNumber));							// Return True if pressed

		return false;
	}


	//-----------------------------------------------------------------------------------------------------------
	// Returns true if the passed button number is NOT pressed.
	//-----------------------------------------------------------------------------------------------------------

	public boolean isReleased(int buttonNumber)
	{
		if ((buttonNumber >= 0) && (buttonNumber < maxButtons))			// Check for valid button number
			return (!getButton(buttonNumber));							// Return True if not pressed

		return false;
	}


	//-----------------------------------------------------------------------------------------------------------
	// Pass-thru of the joystick getRawButton method 
	//-----------------------------------------------------------------------------------------------------------

	public boolean getRawButton(int joy, int button)
	{
		if ((joy >= 0) && (joy < maxJoysticks) && (button >= 0) && 
				(button < maxButtons))
			return joysticks[joy].getRawButton(button);					// Return current value

		return false;
	}

	
	//-----------------------------------------------------------------------------------------------------------
	// Return if there was a button pressed (rising edge) since the last time this method was called by the
	// owner (subsystem).
	//-----------------------------------------------------------------------------------------------------------

	public boolean getButtonRising(int buttonNumber)
	{
		boolean rising = false;											// Default rising edge status is FALSE
				
		if (buttonData[buttonNumber][0] <  								// If a more recent (timewise) button
				buttonDataGlobal[buttonNumber][0])						//   press has occurred...
		{
			if (buttonDataGlobal[buttonNumber][0] > 0)						// and this is not the first time this
				rising = true;											//	routine has been called, then TRUE

			buttonData[buttonNumber][0] =  								// Store the falling edge time
					buttonDataGlobal[buttonNumber][0];
		}
		return rising;													// Return the rising edge button status
	}
	
	//-----------------------------------------------------------------------------------------------------------
	// Return if there was a button released (falling edge) since the last time this method was called by the
	// owner (subsystem).
	//-----------------------------------------------------------------------------------------------------------
	
	public boolean getButtonFalling(int buttonNumber)
	{
		boolean falling = false;										// Default falling edge status is FALSE
		
		if (buttonData[buttonNumber][1] < 								// If a more recent (timewise) button
				buttonDataGlobal[buttonNumber][1])						//   release has occurred...
		{
			if (buttonDataGlobal[buttonNumber][1] > 0)						// and this is not the first time this
				falling = true;											//	routine has been called, then TRUE
			
			buttonData[buttonNumber][1] = 								// Store the falling edge time
					buttonDataGlobal[buttonNumber][1];
		}
		return falling;													// Return the falling edge button status
	}

	//-----------------------------------------------------------------------------------------------------------
	// Return the joystick axis value modified by the current deadband
	//-----------------------------------------------------------------------------------------------------------

	public double getAxis(int axisNumber)
	{
		double rawAxis, deadband;
		
		if ((axisNumber >= 0) && (axisNumber < maxAxes))				// Check for Valid Axis Number
		{
			rawAxis = getRawAxis(axisNumber);							// Get Axis Value
			deadband = axesData[axisNumber][0];							// Local variable used for readability
			
			if (Math.abs(rawAxis) > deadband)							// Is it outside the deadband?
				return (rawAxis - (rawAxis > 0 ? deadband :				// Rescale value to include the the full
					-deadband)) / (1.0 - deadband);						//   range between 0 and 1.0
		}
		
		return (0.0);													// Invalid axis number return value
	}
	
	//-----------------------------------------------------------------------------------------------------------
	// Get the Raw Joystick Value without modifying it with the deadband
	//-----------------------------------------------------------------------------------------------------------

	public double getRawAxis(int axisNumber)
	{
		if ((axisNumber >= 0) && (axisNumber < maxAxes))				// Check for Valid Axis Number
			return joysticks[axes[axisNumber][0]].						// Return raw value
					getRawAxis(axes[axisNumber][1]);

		return (0.0);													// Invalid Axis Number Value
	}

	public double getRawAxis(int joystick, int axisNumber)
	{
		return joysticks[joystick].getRawAxis(axes[axisNumber][1]);		// Return Raw Value
	}

	//-----------------------------------------------------------------------------------------------------------
	// Change the specified joystick deadband to the passed value.  This routine only affects the local
	//	DriverStationControl object. 
	//-----------------------------------------------------------------------------------------------------------
	
	public boolean setAxisDeadband(int axisNumber, double deadband)
	{
		if ((axisNumber >= 0) && (axisNumber < maxAxes) &&				// Check for valid Axis Number
			(deadband >= 0) && (deadband <= 1.0))						//	 and valid deadband (0.0 - 1.0)
		{
			axesData[axisNumber][0] = deadband;							// Update deadband
			return true;												// Success
		}
		return false;													// Failure
	}
	
	
	//-----------------------------------------------------------------------------------------------------------
	// Inverts the joystick axis if the boolean passed in is true  
	//-----------------------------------------------------------------------------------------------------------
		
	public boolean setInverted(int axisNumber, boolean isInverted)
	{
		if ((axisNumber >= 0) && (axisNumber < maxAxes))				// Check for valid Axis Number
		{			
			axesData[axisNumber][1] = isInverted ? -1.0 : 1.0;			// Update deadband
			return true;												// Success
		}
		return false;													// Failure
	}

	
	//-----------------------------------------------------------------------------------------------------------
	// Run System Diagnostics while button is Pressed 
	//-----------------------------------------------------------------------------------------------------------
	
	public boolean runDiagnostics(){
		if ((isFMSAttached() == false) && isPressed(OP_XBOX_BACK))
			return true;

		return false;
	}
	
	
	//-----------------------------------------------------------------------------------------------------------
	// Pass-Thru of driver station methods - Note: isNewControlData is purposely omitted
	//-----------------------------------------------------------------------------------------------------------

	public boolean isAutonomous()			{	return ds.isAutonomous();			}
	public boolean isOperatorControl()		{	return ds.isOperatorControl();		}
	public boolean isDisabled()				{	return ds.isDisabled();				}
	public boolean isEnabled()				{	return ds.isEnabled();				}
	public boolean isTest()					{	return ds.isTest();					}
	public boolean isBrownedOut()			{	return ds.isBrownedOut();			}
	public boolean isDSAttached()			{	return ds.isDSAttached();			}
	public boolean isFMSAttached()			{	return ds.isFMSAttached();			}
	public int getLocation()				{	return ds.getLocation();			}
	public int getJoystickType(int st) 		{	return ds.getJoystickType(st);		}
	public int kJoystickPorts()				{	return ds.kJoystickPorts;			}
	public boolean getJoystickIsXbox(int st){	return ds.getJoystickIsXbox(st);	}
	public double getMatchTime()			{	return ds.getMatchTime();			}
	public double getBatteryVoltage()		{	return ds.getBatteryVoltage();		}
	public String getJoystickName(int st)	{	return ds.getJoystickName(st);		}
	public Alliance getAlliance()			{	return ds.getAlliance();			}
	
	//-----------------------------------------------------------------------------------------------------------
	// General update routine called by each subsystem at the beginning of each loop.  This routine updates the
	//	rising/falling edge button data.
	//-----------------------------------------------------------------------------------------------------------

	public void update()
	{
		int i;															// FOR loop counter
		boolean bValue;													// current button value
		boolean POVValue;
		
		createObjects(false);											// Ensure all objects have been created.
		
		if (ds.isNewControlData())										// Has new data been received by the ds?
		{
			for (i=0; i<maxButtons; i++){								// Cycle through each button
				bValue = getButton(i);									// Get current button value
				
				if (bValue != buttonLastValues[i]){						// Has the button value changed?
					if (bValue)											// It's been pressed
						buttonDataGlobal[i][0] =						// Update rising edge time
							System.currentTimeMillis();
					else												// It's been released 
						buttonDataGlobal[i][1] =						// Update the falling edge time
							System.currentTimeMillis();
					
					buttonLastValues[i] = bValue;						// Store updated button value
				}
			}
			for (i=0; i<maxPOVs; i++){
				POVValue = getPOV(i);
				if (POVValue != POVLastValues[i]){
					if(POVValue)
						POVDataGlobal[i][0] = System.currentTimeMillis();
					else
						POVDataGlobal[i][1] = System.currentTimeMillis();
					POVLastValues[i] = POVValue;
				}
			}
		}
	}
}
