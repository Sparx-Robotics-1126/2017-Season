package org.gosparx.team1126.robot.subsystem;

public class TestSubsystem extends GenericSubsystem {
	
	private static TestSubsystem test;
	private int i;
	private int j = 25;
	/**
	 * Constructors a drives object with normal priority
	 */
	private TestSubsystem(){
		super("Test",Thread.NORM_PRIORITY);
	}

	/**
	 * ensures that there is only one instance of drives		
	 * @return the instance of drives 
	 */
	public static synchronized TestSubsystem getInstance(){
		if(test == null){
			test = new TestSubsystem();
		}
		return test;											
	}

	@Override
	protected void liveWindow() {
	}

	@Override
	protected boolean execute() {
		for (i=0; i<j; i++)
			LOG.logMessage("message " + j + " " + i);

//		j++;
		
		return false;
	}

	/**
	 * Instantiates all the objects and initializes the variables  
	 * @return true if it runs once, false if it continues, should return true
	 */
	@Override
	protected boolean init(){
		return true;
	}
	/**
	 * the time the system "sleeps" until it is called again
	 * @return the time in milliseconds
	 */
	@Override
	protected long sleepTime() {
		return 50;
	}

	/**
	 * Writes logs to the console every 5 seconds
	 */
	@Override
	protected void writeLog() {
	}
}
