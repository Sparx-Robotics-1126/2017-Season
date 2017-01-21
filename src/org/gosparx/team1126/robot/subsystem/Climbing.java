package org.gosparx.team1126.robot.subsystem;

import java.security.InvalidParameterException;

import org.gosparx.team1126.robot.util.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import org.gosparx.team1126.robot.IO;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Climbing extends GenericSubsystem {

	private State currentClimbingStatus;
	
	public Climbing(String name, int priority){
		super(name, priority);
	}
	
	@Override
	protected void writeLog() {
		LOG.logMessage("Climbing Status" + currentClimbingStatus);
	}
	
	public enum State{
		STANDBY,
		ATTATCHING,
		CLIMBING;	

		@Override
		public String toString(){
			switch(this){
				case STANDBY:
					return "Scaling standby";
				case ATTATCHING:
					return "Attatching to rope";
				case CLIMBING:
					return "Climbing";
					default:
				return "Climbing Status Unknown";
			}
	    }
    }

	@Override
	protected boolean init() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void liveWindow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean execute() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected long sleepTime() {
		// TODO Auto-generated method stub
		return 0;
	}
}