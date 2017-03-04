package org.gosparx.team1126.robot.util;

public class SharedData {

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


	// Camera SubSystem Shared Data

	public enum Target { BOILER, LIFT };

	public static double distanceToBoiler, distanceToLift;
	public static double angleToBoiler, angleToLift;
	//public static double distanceToLift; 							//For if there is no ultra sonic sound sensor 
	public static Target targetType;
	private static double targetXBoiler, targetYBoiler; 			// (X, Y) at image time
	private static double targetXLift, targetYLift;
	private static long liftImageTime, boilerImageTime;				// Time of Image
	
	public static void setTarget (Target type, double distance, double angle){
		if(type==Target.BOILER){
			distanceToBoiler = distance;
			angleToBoiler = angle;
			targetXBoiler = x + Math.sin(Math.toRadians(heading + angleToBoiler)) * distanceToBoiler;
			targetYBoiler = y + Math.cos(Math.toRadians(heading + angleToBoiler)) * distanceToBoiler;
			boilerImageTime = System.currentTimeMillis();
		}
		else
		{
			angleToLift = angle;
			liftImageTime = System.currentTimeMillis();
			distanceToLift = distance;
			targetXLift = x + Math.sin(Math.toRadians(heading + angleToLift)) * distanceToLift;
			targetYLift = y + Math.cos(Math.toRadians(heading + angleToLift)) * distanceToLift;
		}
	}

	public static double getCorrectedTargetAngle(Target type){
		if (type == Target.BOILER)
			return(Math.IEEEremainder((Math.atan2(targetXBoiler - x, targetYBoiler - y) - heading), 360.0));
		else
			return(Math.IEEEremainder((Math.atan2(targetXLift - x, targetYLift - y) - heading), 360.0));		
	}

	public static double getCorrectedTargetDistance(Target type){
		if (type == Target.BOILER)
			return (Math.sqrt(Math.pow(targetXBoiler - x, 2) + Math.pow(targetYBoiler - y, 2)));
		else
			return (Math.sqrt(Math.pow(targetXLift - x, 2) + Math.pow(targetYLift - y, 2)));			
	}
	
	public static double getBoilerImageTime(){
		return boilerImageTime;
	}
	
	public static double getLiftImageTime(){
		return liftImageTime;
	}
}