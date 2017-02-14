package org.gosparx.team1126.robot.sensors;

import edu.wpi.first.wpilibj.DriverStation;


/**
 * Logic of a Proportional Integral Derivative loop. Must be constructed first,
 * then it must receive continual updates in order to receive accurate output
 * values.
 */

public class PID {
	
	private double kp;							        // Proportional Constant
	private double ki;						 	        // Integral Constant
	private double kf = 0;						        // Linear Feedforward Constant
	private double error = 0;					        // Delta between setpoint and PV
	private double integral = 0;					    // Integral portion of the output
	private double proportional = 0;				    // Proportional portion of the output
	private double feedForward = 0;				        // Feedforward portion of the output
	private double totalizer = 0;				        // Cumulative sum of the error for integral calc
	private double currentTime = 0;				        // Current time (in seconds)
	private double outMax = 1.0;						// Default Maximum Output
	private double outMin = -1.0;						// Default Minimum Output
	private double pastTime = 0;					    // Last time the loop was run (in seconds)
	private double spRampLimit = 0;						// Setpoint Ramp Limit
	private double internalSP = 0;						// Internal Setpoint
	private boolean spRamping = false;					// Setpoint ramping
	private boolean outMaxMinEna = false;				// Output Max/Min Enabled
	private boolean stopFunction = false;		        // Use speed controller brake mode flag

	/* PID Constructors */
	
	public PID(double kI, double kP){
		ki = kI;
		kp = kP;
	}
	
	public PID(double kI, double kP, double ff){
		ki = kI;
		kp = kP;
		kf = ff;
	}
    
	/* Main PID loop routine - This routine should be called periodically to update the output
	 * based on the current feedback from the sensor.  The maximum interval is currently 0.1
	 * seconds, which is hardcoded into the loop.  This value was chosen so in the event that
	 * the loop call is significantly delayed, a large value is not added to the cumulative sum
	 * used for the integral.
	 */
	
	public double loop(double speed, double setPoint){

		double elapsedTime;								// Elapsed time since last call
		double output;									// Calculated Output
		
		// Acquire the current time (in milliseconds), convert to seconds and calculate the
		// elapsed time since the last call, and store the current time for the next loop.
		// The elapsed time is used in the I (and D) terms to make the calculation time
		// independent.  That is, not influenced by the execution frequency of the PID loop.
		
		currentTime = (double)(System.currentTimeMillis()) / 1000;
		elapsedTime = currentTime - pastTime;
		pastTime = currentTime;						
		
		// Check to see if the speed control brake mode is to be used.  If so, as determined by the
		// stopFunction flag, and the desired setpoint is zero, then immediately return a zero output.
		// Setting the speed controller to zero (0), will cause it to enter brake mode if set.  Zero
		// the cumulative error term so that the next time a non-zero setpoint is used, the loop doesn't
		// use an old I component.
		
		if((setPoint == 0) && (stopFunction == true)){
			totalizer = 0;
			return 0;
		}
		
		if(elapsedTime > .1)							// Limit the maximum elapsed time to 0.1 sec
			elapsedTime = .1;

		if ((spRamping == true) &&						// Check for SP Ramping enabled,
				(Math.abs(setPoint - internalSP) > 		// and over the limit
				(spRampLimit * elapsedTime)))
		{
			if (setPoint - internalSP > 0)				// Are we ramping UP?
				internalSP += spRampLimit * elapsedTime;// Add the maximum ramp limit
			else
				internalSP -= spRampLimit * elapsedTime;// Else subtract the maximum limit
		}else
			internalSP = setPoint;
		
		error = internalSP - speed;						// Calculate the error between the setpoint and PV
		proportional = error * kp;						// Proportional calculation
		totalizer += error * (elapsedTime);				// Add the error to the cumulative error sum
		integral = totalizer * ki;						// Integral calculation
		feedForward = kf * setPoint;					// Linear feedforward calculation
		
		output = proportional + integral + feedForward;	// Calculate output valule
		
		if (outMaxMinEna == true){						// Is Min/Max limiting enabled?
			if (output > outMax){						// Are we above the maximum output value?
				if (integral > output - outMax)			// Is compensating with i term possible?
					totalizer -= (output - outMax) / ki;// Adjust i term to the maximum limit;
				else if (integral > 0)					// Otherwise the i term must be 0 if positive
					totalizer = 0;
				
				output = outMax;						// Set output to max limit
			}
			if (output < outMin){						// Are we less than the minimum value?
				if (integral < output - outMin)			// Is compensating with the i term possible?
					totalizer -= (output - outMin) / ki;// Adjust i term to maximum (negative) value
				else if (integral < 0)					// Otherwise the i term must be 0 if negative
					totalizer = 0;
				
				output = outMin;
			}
		}		
		return (output);								// returns output power
	}
	
	/* Set the speed controller brake mode flag.  When TRUE, the PID loop will return a zero (0) output
	 * when the setpoint is zero (0), otherwise the loop will try to control to a zero (0) setpoint.
	 */
	
	public void breakMode(boolean condition){
		stopFunction = condition;
	}
   
	// Output Maximum/Minimum limiting routines.  The output max/min places limits on the output so that it does
	// not exceed the specified value.  This feature also limits the 'i' term.
	
	public void maxMinMode(boolean enabled){
		outMaxMinEna = enabled;
	}
	
	public void setMaxMin(double min, double max){
		outMin = min;
		outMax = max;
	}
	
	// Enable/Disable set point ramping.  SP ramping can be useful to limit the acceleration on the robot.  When
	// both drives are saturated (at 100%), it is not possible to correct for heading variation and ramping the
	// setpoint when a large change is desired is one way to compensate for this.  The limit is in EU/sec.
	
	public void spRampMode(boolean enabled){
		spRamping = enabled;
	}
	
	public void setSPRamp(double ramp){
		spRampLimit = ramp;
	}
	
	/* Routine to allow for changes/updates to the PID constants */
	
    public void pidconstants (double newkp, double newki){
    	kp = newkp;
    	ki = newki;
    }
}