package org.gosparx.team1126.robot;

import org.gosparx.team1126.robot.subsystem.GenericSubsystem;
import org.gosparx.team1126.robot.subsystem.Shooter;
import org.gosparx.team1126.robot.util.DriverStationControls;
import org.gosparx.team1126.robot.util.LogWriter;
import org.gosparx.team1126.robot.util.VisionNetworkTable;

import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot{

	protected VisionNetworkTable vision;
	
	/**
	 * An array of all of the subsystems on the robot
	 */
	private GenericSubsystem[] subsystems;
	
	// Create a DriverStationControls object in Robot to be able to reset the internal object.
	// Deploying without rebooting seems to not distroy all the objects in dsc.
	
	protected DriverStationControls dsc;

	/**
	 * Called once every time the robot is powered on
	 */
	public Robot() {
		dsc = new DriverStationControls(true);
		subsystems = new GenericSubsystem[]{
//        	Drives.getInstance(),
//			Autonomous.getInstance(),
//        	BallAcqNew.getInstance(),
//			CameraController.getInstance(), 
			Shooter.getInstance(),
//			LogWriter.getInstance(),
		};

		for(GenericSubsystem system: subsystems){
			system.start();
			System.out.println(system.getName());
		}
		vision = new VisionNetworkTable();
	}

	/**
	 *  Called one time when the robot enters autonomous
	 */
	public void autonomous() {
		System.out.println("Auto Started");
	}

	/**
	 *  Called one time when the robot enters teleop
	 */
	public void operatorControl() {
	}

	/**
	 *  Called one time when the robot enters test
	 */
	public void test() {
	}
	
	
	@Override
	public void disabled(){
	}
}
