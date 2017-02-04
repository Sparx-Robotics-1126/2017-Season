package org.gosparx.team1126.robot;

import org.gosparx.team1126.robot.subsystem.GenericSubsystem;
import org.gosparx.team1126.robot.util.LogWriter;

import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot{

	/**
	 * An array of all of the subsystems on the robot
	 */
	private GenericSubsystem[] subsystems;

	/**
	 * Called once every time the robot is powered on
	 */
	public Robot() {
		subsystems = new GenericSubsystem[]{
//        	Drives.getInstance(),
//			Autonomous.getInstance(),
//        	BallAcqNew.getInstance(),
//			CameraController.getInstance(), 
			LogWriter.getInstance(),
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
