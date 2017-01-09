package org.gosparx.team1126.robot.subsystem;

import java.security.InvalidParameterException;

import org.gosparx.team1126.robot.util.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is designed to be the base class of all the subsystems on the
 * robot. For every different subsystem, a new Generic Subsystem must be
 * created.
 * 
 * @author Justin Bassett
 *
 */
public abstract class GenericSubsystem extends Thread {

	/**
	 * This is the logger for the specific subsystem.
	 */
	protected Logger LOG;

	/**
	 * An instance of driverstation
	 */
	protected DriverStation ds;

	/**
	 * This constructs a new subsystem with the given name and priority.
	 * 
	 * @param name
	 *            the name of the subsystem
	 * @param priority
	 *            the Thread priority that it should execute at.
	 */
	public GenericSubsystem(String name, int priority){
		super(name);
		if(priority != Thread.MIN_PRIORITY && priority != Thread.NORM_PRIORITY && priority != MAX_PRIORITY)
			throw new InvalidParameterException();
		setPriority(priority);
		if(name != "LogWriter"){
			LOG = new Logger(name);
		}
		ds = DriverStation.getInstance();
	}

	/**
	 * This is called one time after start is called.
	 * 
	 * @return true if we have successfully inited, false otherwise.
	 */
	abstract protected boolean init();

	/**
	 * This is used to create a liveWindow setup
	 */
	abstract protected void liveWindow();

	/**
	 * Once start is called, this method is called until it returns true.
	 * 
	 * @return true if execution has complete and we do not need the method
	 *         restarted, false otherwise.
	 */
	abstract protected boolean execute();

	/**
	 * The amount of time you want to sleep for after a cycle.
	 * 
	 * @return the number of milliseconds you want to sleep after a cycle.
	 */
	abstract protected long sleepTime();

	/**
	 * The amount of time between calling writeLog().
	 * 
	 * @return the number of seconds you want between writeLog() calls.
	 */
	protected double logTime() { return 5; }

	/**
	 * Logs all info appropriate to the subsystem.
	 */
	abstract protected void writeLog();

	public boolean isWorking = true;
	/**
	 * Is the subsystem Working?
	 */
	private void updateSmartStatus(){
		SmartDashboard.putBoolean(getName(), isWorking);
	}

	/**
	 * Runs and loops the execute() until execute returns false, logging ever logTime() seconds.
	 */
	@Override
	public void run(){
		boolean retVal = false;
		double lastLogged = 0;
		if(LOG != null)
			LOG.logMessage("***Starting: " + getName());
		init();
		liveWindow();
		if(LOG != null)
			LOG.logMessage("***Executing: " + getName());
		do{
			if(!ds.isTest()){
				try{
					retVal = execute();
					updateSmartStatus();
				}catch(Exception e){
					if(LOG != null)
						LOG.logError("Uncaught Exception! " + e.getMessage());
					e.printStackTrace(System.err);
				}
				if(Timer.getFPGATimestamp() >= lastLogged + logTime()){
					writeLog();
					lastLogged = Timer.getFPGATimestamp();
				}
				try {
					Thread.sleep(sleepTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				retVal = false;
			}
		}while(!retVal);
		if(LOG != null)
			LOG.logMessage("Completing thread: " + getName());
	}

	/**
	 * returns a String version of the class
	 */
	@Override
	public String toString(){
		return  this.getName();
	}
}
