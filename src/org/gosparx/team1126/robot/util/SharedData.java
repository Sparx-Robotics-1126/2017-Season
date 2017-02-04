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

}
