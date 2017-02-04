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
	
	
	// Camera SubSystem Shared Data
	
	public enum Target { BOILER, LIFT };
	
	public static double distanceToTarget;
	public static double angleToTarget;
	public static Target targetType;
	private static double targetX, targetY;					// (X, Y) at image time
	private static long imageTime;							// Time of Image
	
	public static void setTarget (Target type, double distance, double angle){
		distanceToTarget = distance;
		angleToTarget = angle;
		targetX = x + Math.sin(Math.toRadians(heading + angleToTarget)) * distanceToTarget;
		targetY = y + Math.cos(Math.toRadians(heading + angleToTarget)) * distanceToTarget;
		imageTime = System.currentTimeMillis();
	}
	
	public static double getCorrectedTargetAngle(){
		return(Math.IEEEremainder((Math.atan2(targetX - x, targetY - y) - heading), 360.0));
	}
	
	public static double getCorrectedTargetDistance(){
		return (Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2)));
	}
}
