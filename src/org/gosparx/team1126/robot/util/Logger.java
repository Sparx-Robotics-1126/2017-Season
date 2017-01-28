package org.gosparx.team1126.robot.util;

import java.text.DecimalFormat;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

/**
 * Used to log messages to files. This is the non singleton Logger that communicates with the singleton LogWriter.
 * @author Alex Mechler {amechler1998@gmail.com}
 */
public class Logger{

	private int counters[] = new int[26];
	
	/**
	 * A LogWriter to log our messages
	 */
	private LogWriter writer;

	/**
	 * Used to get field times and robot status
	 */
	private DriverStation ds;

	/**
	 * The name of the subsystem
	 */
	private String subsystemName;

	/**
	 * The DecimalFormatter to properly format a decimal
	 */
	private DecimalFormat formatter;

	/**
	 * Creates a new Logger
	 * @param subsystem The name of the subsystem
	 */
	public Logger(String subsystem){
		try{
			subsystemName = subsystem;
			ds = DriverStation.getInstance();
			writer = LogWriter.getInstance();
			formatter = new DecimalFormat("0000.0000");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a log message to the Writer with the format DEBUG[status]{subsystem}(time):message 
	 * @param message - the message to send
	 */
	public void logMessage(String message){
		logMessage(message, false);
	}

	/**
	 * Sends a log message to the Writer with the format ERROR[status]{subsystem}(time):message 
	 * @param message - the message to send
	 */
	public void logError(String message){
		logMessage(message, true);
	}

	// Log every Xth message to reduce logging.  Each unique message must have an separate ID from 0-24
	
	public void logMessage(int id, int frequency, String message){
		if ((id > 0) && (id <= 24)){
			counters[id]++;
			
			if (counters[id] < frequency)
				return;
			
			counters[id] = 0;
		}
		
		logMessage(message);
	}
	
	/**
	 * Sends the message to the LogWriter with the proper formatting
	 * @param message - the message to log
	 * @param error - if the message is considered an error
	 */
	private void logMessage(String message, boolean error){
		String status = "";
		if(ds.isDisabled()){
			status = "Dis";
		}else if(ds.isAutonomous()){
			status = "Aut";
		}else if(ds.isEnabled()){
			status = "Tel";
		}
		String timeFormatted = formatter.format(Timer.getFPGATimestamp());
		String toLog = (error ? ("ERROR") : ("DEBUG"))+ "[" + status + "]{" + subsystemName + "}(" + timeFormatted + "):" + message+ "\n";
		writer.logString(toLog);//TODO:UNCOMMENT TO LOG
		System.out.print(toLog);
	}
}
