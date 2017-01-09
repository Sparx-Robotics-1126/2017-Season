package org.gosparx.team1126.robot.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.gosparx.team1126.robot.subsystem.GenericSubsystem;

import edu.wpi.first.wpilibj.DriverStation.Alliance;

/**
 * Used to log messages to files. This is the singleton LogWriter that writes to the files.
 * @author Alex Mechler {amechler1998@gmail.com}
 */
public class LogWriter extends GenericSubsystem{

	/**
	 * The file path to store the logs in. /mnt/sda1 is the USB port.
	 */
	private static final String FILE_PATH = "/U/sda1/";

	/**
	 * The name of the log
	 */
	private String logName;

	/**
	 * A file for checking if the current log exists
	 */
	private File file;

	/**
	 * The FileOutputStream for accessing the log
	 */
	private FileOutputStream dos;

	/**
	 * Support for the singleton model
	 */
	private static LogWriter lw;

	private boolean loggerWorking = true;

	/**
	 * A queue of log messages we need to write so that they always appear in chronological order.
	 */
	private LinkedBlockingQueue<String> toLog;

	/**
	 * Supports singleton model.
	 * @return - the long writer
	 */
	public static synchronized LogWriter getInstance(){
		if(lw == null){
			lw = new LogWriter();
		}
		return lw;
	}

	/**
	 * Creates a LogWriter
	 */
	private LogWriter(){
		super("LogWriter", Thread.NORM_PRIORITY);
		toLog = new LinkedBlockingQueue<String>();
	}

	/**
	 * Makes sure the file exists and if the directories for it exist
	 */
	@Override
	protected boolean init() {
		try {
			Calendar cal = Calendar.getInstance();
			logName = "log" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DATE) + "-" + cal.get(Calendar.YEAR) + "(" + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) + ") " + (ds.isFMSAttached() ? (ds.getAlliance() == Alliance.Red ? "Red" : "Blue") : "Practice") + ".txt";
			file = new File(FILE_PATH + logName);
			file.mkdirs();
			file.setWritable(true, false);
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("********************************************" + file.exists() + " " + file.isHidden() + " " + file.getAbsolutePath());
		return true;
	}

	/**
	 * Loops and sleeps until toLog is no longer empty, and then writes the information to the log file.
	 */
	@Override
	protected boolean execute(){
		String toWrite = null;
		while(toWrite == null){
			try {
				toWrite = toLog.poll(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		write(toWrite.getBytes());
		System.out.print(toWrite);
		return false;
	}

	/**
	 * The amount of time for the sleeping between loops of execute()
	 */
	@Override
	protected long sleepTime() {
		return 50;
	}

	/**
	 *	Logs info about the subsystem
	 */
	@Override
	protected void writeLog() {}

	/**
	 * writes the passed byte array to the file and then closes the output stream
	 * @param bytes - the array of bytes to write
	 */
	private synchronized void write(byte[] bytes) {
		if(loggerWorking){
			try {
				dos = new FileOutputStream(file,true);
				dos.write(bytes);
				dos.flush();
				dos.close();
				dos = null;
			} catch (Exception e) {
				//			e.printStackTrace();
				loggerWorking = false;
				System.out.println("LOGGER FAILED******************************************************");
			}
		}
	}

	/**
	 * Adds a message to the queue
	 * @param message - the message to add to the queue
	 */
	public void logString(String message){
		toLog.add(message);
	}

	@Override
	protected void liveWindow() {
		// TODO Auto-generated method stub

	}
}
