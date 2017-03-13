package org.gosparx.team1126.robot.util;

public class SharedData {

	public static final double INVALIDTARGETTYPE = 99999;
	
	// Drives Subsystem Shared Data

	public static double x;
	public static double y;
	public static double heading;
	public static double leftSpeed;
	public static double rightSpeed;
	public static double avgSpeed;


	// Shooter & Turret Subsystem Shared Data

	public static double turretAngle;
	public static double shooterSpeed;
	public static boolean systemReady;
	public static boolean shootingOn;


	// Camera SubSystem Shared Data

	public static enum Target { NONE, BOILER, LIFT };
	public static Target targetType = Target.NONE;

	public static double distanceToBoiler, distanceToLift;
	public static double angleToBoiler, angleToLift;
	private static double targetXBoiler, targetYBoiler; 			// (X, Y) at image time
	private static double targetXLift, targetYLift;
	private static long liftImageTime = 0, boilerImageTime = 0;		// Time of Image

	private long newLiftImage = 0, newBoilerImage = 0;
	
	// Camera routine to set the location of the found target.  Data passed is the target
	// type, distance and angle to the target from a fixed reference point on the robot
	
	public static void setTarget (Target type, double distance, double angle){
		if(type == Target.BOILER){
			distanceToBoiler = distance;
			angleToBoiler = angle;
			targetXBoiler = x + Math.sin(Math.toRadians(heading + angleToBoiler)) * distanceToBoiler;
			targetYBoiler = y + Math.cos(Math.toRadians(heading + angleToBoiler)) * distanceToBoiler;
			boilerImageTime = System.currentTimeMillis();
		}
		else if (type == Target.LIFT)
		{
			angleToLift = angle;
			liftImageTime = System.currentTimeMillis();
			distanceToLift = distance;
			targetXLift = x + Math.sin(Math.toRadians(heading + angleToLift)) * distanceToLift;
			targetYLift = y + Math.cos(Math.toRadians(heading + angleToLift)) * distanceToLift;
		}
	}

	// Returns the angle to the desired target based on the last camera image data and compensated
	// by the movement of the robot.
	
	public static double getCorrectedTargetAngle(Target type){
		if (type == Target.BOILER)
			return(Math.IEEEremainder((Math.atan2(targetXBoiler - x, targetYBoiler - y) - heading), 360.0));
		else if (type == Target.LIFT)
			return(Math.IEEEremainder((Math.atan2(targetXLift - x, targetYLift - y) - heading), 360.0));		
		else
		  return INVALIDTARGETTYPE;
	}

	// Returns the distance to the desired target based on the last camera image data and compensated
	// by the movement of the robot.
	
	public static double getCorrectedTargetDistance(Target type){
		if (type == Target.BOILER)
			return (Math.sqrt(Math.pow(targetXBoiler - x, 2) + Math.pow(targetYBoiler - y, 2)));
		else if (type == Target.LIFT)
			return (Math.sqrt(Math.pow(targetXLift - x, 2) + Math.pow(targetYLift - y, 2)));			
		else
			return INVALIDTARGETTYPE;
	}
	
	// Return the amount of time since the last image of the specified type was received (in seconds)
	
	public static double getImageTime(Target type){
		if (type == Target.BOILER)
			return (System.currentTimeMillis() - boilerImageTime) / 1000.0;
		else if (type == Target.LIFT)
			return (System.currentTimeMillis() - liftImageTime) / 1000.0;
		else
			return INVALIDTARGETTYPE;
	}
	
	// Determine if new data has arrived since last called.  Could be useful when initially turning on
	// the camera to look for new image.
	
	public boolean newImage(Target type){
		if ((type == Target.BOILER) && (boilerImageTime > newBoilerImage)){
			newBoilerImage = boilerImageTime;
			return true;
		}
		
		if ((type == Target.LIFT) && (liftImageTime > newLiftImage)){
			newLiftImage = liftImageTime;
			return true;
		}
		
		return false;
	}
	
	public void clearImageData ( Target type ){
		if (type == Target.BOILER)
			boilerImageTime = 0;
		else if (type == Target.LIFT)
			liftImageTime = 0;
	}
}
