package org.gosparx.team1126.robot.sensors;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Logic of a Proportional Integral Derivative loop. Must be constructed first,
 * then it must receive continual updates in order to receive accurate output
 * values.
 */

public class PID {
	
	private double kp;							        // Proportional Constant
	private double ki;							        // Integral Constant
	private double kf = 0;						        // Linear Feedforward Constant
	private double error = 0;					        // Delta between setpoint and PV
	private double integral = 0;					    // Integral portion of the output
	private double proportional = 0;				    // Proportional portion of the output
	private double feedForward = 0;				        // Feedforward portion of the output
	private double totalizer = 0;				        // Cumulative sum of the error for integral calc
	private double currentTime = 0;				        // Current time (in seconds)
	private double pastTime = 0;					    // Last time the loop was run (in seconds)
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
		
		// Acquire the current time (in milliseconds), convert to seconds and calculate the
		// elapsed time since the last call, and store the current time for the next loop.
		// The elapsed time is used in the I (and D) terms to make the calculation time
		// independent.  That is, not influenced by the execution frequency of the PID loop.
		
		currentTime = (double)(System.currentTimeMillis()) / 1000;
		elapsedTime = currentTime - pastTime;
		pastTime = currentTime;						

		if(elapsedTime > .1)							// Limit the maximum elapsed time to 0.1 sec
			elapsedTime = .1;
		
		error = setPoint - speed;						// Calculate the error between the setpoint and PV
		proportional = error * kp;						// Proportional calculation
		totalizer += error * (elapsedTime);				// Add the error to the cumulative error sum
		integral = totalizer * ki;						// Integral calculation
		
		if(integral > 1){								// Limit the I term to a maximum 1.0 (prevent windup)
			totalizer = 1.0/ki;
		}else if(integral < -1){						// Limit the I term to a minimum of -1.0
			totalizer = -1.0/ki;
		}
		
		feedForward = kf * setPoint;					// Linear feedforward calculation
		
		// Check to see if the speed control brake mode is to be used.  If so, as determined by the
		// stopFunction flag, and the desired setpoint is zero, then immediately return a zero output.
		// Setting the speed controller to zero (0), will cause it to enter brake mode if set.  Zero
		// the cumulative error term so that the next time a non-zero setpoint is used, the loop doesn't
		// use an old I component.
		
		if((setPoint == 0) && (stopFunction == true)){
			totalizer = 0;
			return 0;
		}
		
		return (proportional + integral + feedForward);	// Sum the components and return the total, returns power
	}
	
	/* Set the speed controller brake mode flag.  When TRUE, the PID loop will return a zero (0) output
	 * when the setpoint is zero (0), otherwise the loop will try to control to a zero (0) setpoint.
	 */
	
	public void breakMode(boolean condition){
		stopFunction = condition;
	}
   
	/* Routine to allow for changes/updates to the PID constants */
	
    public void pidconstants (double newkp, double newki){
    	kp = newkp;
    	ki = newki;
    }
}