package org.gosparx.team1126.robot;

import org.gosparx.team1126.robot.subsystem.BallAcq;
import org.gosparx.team1126.robot.subsystem.Drives;
import org.gosparx.team1126.robot.subsystem.GenericSubsystem;
import org.gosparx.team1126.robot.subsystem.Scaling;
import org.gosparx.team1126.robot.subsystem.Shooter;
import org.gosparx.team1126.robot.subsystem.Vision;
import org.gosparx.team1126.robot.util.DriverStationControls;
import org.gosparx.team1126.robot.util.LogWriter;

import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot{

	/**
	 * An array of all of the subsystems on the robot
	 */
	private GenericSubsystem[] subsystems;
		
	// Create a DriverStationControls object in Robot to be able to reset the internal object.
	// Deploying without rebooting seems to not destroy all the objects in dsc.
	
	protected DriverStationControls dsc;

	/**
	 * Called once every time the robot is powered on
	 */
	public Robot() {
		dsc = new DriverStationControls(true);
		
		subsystems = new GenericSubsystem[]{
			BallAcq.getInstance(),
			Drives.getInstance(),
			Scaling.getInstance(),
			Shooter.getInstance(),
			Vision.getInstance(),
			LogWriter.getInstance(),
			Autonomous.getInstance(),
		};

		for(GenericSubsystem system: subsystems){
			system.start();
			System.out.println(system.getName());
		}
	}

	/**
	 *  Called one time when the robot enters autonomous
	 */
	public void autonomous() {
		Autonomous.getInstance().setRunAuto(true);
		System.out.println("Auto Started");
	}

	/**
	 *  Called one time when the robot enters teleop
	 */
	public void operatorControl() {
		Autonomous.getInstance().setRunAuto(false);
		//Drives.getInstance().abortAuto();
	}

	/**
	 *  Called one time when the robot enters test
	 */
	public void test() {
		Autonomous.getInstance().setRunAuto(false);
	}
	
	
	@Override
	public void disabled(){
		Autonomous.getInstance().setRunAuto(false);
	}
}
