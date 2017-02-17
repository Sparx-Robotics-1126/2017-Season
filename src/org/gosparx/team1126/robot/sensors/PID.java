package org.gosparx.team1126.robot.sensors;

/**
 * Logic of a Proportional Integral Derivative loop. Must be constructed first, then it must receive
 * periodic calls to the "loop" function in order to calculate the appropriate output values.
 * 
 * 	Derivative calculation not yet available
 */

public class PID {
	
	private double kp = 0;						        // Proportional Constant
	private double ki = 0;					 	        // Integral Constant
	private double kf = 0;						        // Linear Feedforward Constant
	private double integral = 0;					    // Integral portion of the output
	private double proportional = 0;				    // Proportional portion of the output
	private double feedForward = 0;				        // Feedforward portion of the output
	private double totalizer = 0;				        // Cumulative sum of the error for integral calc
	private double outMax = 1.0;						// Default Maximum Output
	private double outMin = -1.0;						// Default Minimum Output
	private double pastTime = 0;					    // Last time the loop was run (in seconds)
	private double spRampLimit = 0;						// Setpoint Ramp Limit
	private double internalSP = 0;						// Internal Setpoint
	private double loopTimeLimit = 0.1;					// Maximum limit for loop timing
	private boolean spRamping = false;					// Setpoint ramping
	private boolean outMaxMinEna = false;				// Output Max/Min Enabled
	private boolean stopFunction = false;		        // Use speed controller brake mode flag

	/* PID Constructors */
	
	public PID(){
	}
	
	public PID(double kP){
		kp = kP;
	}

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

		double currentTime;						        // Current time (in seconds)
		double elapsedTime;								// Elapsed time since last call
		double error;									// Error between SP & PV
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
		// use an old I component as well as update the internal setpoint immediately.
		
		if((setPoint == 0) && (stopFunction == true))
		{
			totalizer = 0;
			internalSP = 0;
			proportional = 0;							// Clear internal variables in case an external
			integral = 0;								//	call is made to print/record these values.
			feedForward = 0;
			return 0;
		}
		
		if(elapsedTime > loopTimeLimit)					// Limit the maximum elapsed time
			elapsedTime = loopTimeLimit;

		if ((spRamping == true) &&						// Check for SP Ramping enabled,
			(Math.abs(setPoint - internalSP) >	 		// and over the limit
			(spRampLimit * elapsedTime)))
		{
			if (setPoint - internalSP > 0)				// Are we ramping UP?
				internalSP += spRampLimit * elapsedTime;// Add the maximum ramp limit
			else
				internalSP -= spRampLimit * elapsedTime;// Else subtract the maximum limit
		}
		else
			internalSP = setPoint;
		
		error = internalSP - speed;						// Calculate the error between the setpoint and PV
		proportional = error * kp;						// Proportional calculation
		totalizer += error * (elapsedTime);				// Add the error to the cumulative error sum
		integral = totalizer * ki;						// Integral calculation
		feedForward = kf * setPoint;					// Linear feedforward calculation
		
		output = proportional + integral + feedForward;	// Calculate output valule
		
		if (outMaxMinEna == true)						// Is Min/Max limiting enabled?
		{
			if (output > outMax)						// Are we above the maximum output value?
			{
				if (integral > output - outMax)			// Is compensating with i term possible?
					totalizer -= (output - outMax) / ki;// Adjust i term to the maximum limit;
				else if (integral > 0)					// Otherwise the i term must be 0 if positive
					totalizer = 0;
				
				output = outMax;						// Set output to max limit
				integral = ki * totalizer;				// Correct integral component for retrieval
			}
			if (output < outMin)						// Are we less than the minimum value?
			{
				if (integral < output - outMin)			// Is compensating with the i term possible?
					totalizer -= (output - outMin) / ki;// Adjust i term to maximum (negative) value
				else if (integral < 0)					// Otherwise the i term must be 0 if negative
					totalizer = 0;
				
				output = outMin;
				integral = ki * totalizer;				// Correct integral component for retrieval
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
		spRamping = true;
		spRampLimit = ramp;
	}
	
	/* Enable the changing of the maximum time between loop executions.  By default, this is set to 0.1 sec.
	 * so that any single loop won't have a dramatic effect on the output should the thread get dealyed by
	 * something else occurring in the processor (blocking).
	 */
	
	public void setMaxLoopTime ( double maxTime ){
		loopTimeLimit = maxTime;
	}
	
	/* Routine to allow for changes/updates to the PID constants */
	
    public void pidconstants (double newkp, double newki, double newkf){
    	kp = newkp;
    	ki = newki;
    	kf = newkf;
    }
 
    /* Routines to retrieve internal variables for logging/plotting/tracking/printing */
    
    public double getProportional(){
    	return proportional;
    }
    
    public double getIntegral(){
    	return integral;
    }
    
    public double getFeedforward(){
    	return feedForward;
    }
    
    public double getInternalSP(){
    	return internalSP;
    }
    
    public double getki(){
    	return ki;
    }
    
    public double getkp(){
    	return kp;
    }
    
    public double getkf(){
    	return kf;
    }
}