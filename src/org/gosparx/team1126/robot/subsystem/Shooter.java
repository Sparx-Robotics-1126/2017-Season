package org.gosparx.team1126.robot.subsystem;

public class Shooter extends GenericSubsystem{

	/**
	 * the instance of a Shooter object
	 */
	private static Shooter shoot;
	
	/**
	 * Constructs a shooter object
	 */
	public Shooter(){
		super("Shooter", Thread.NORM_PRIORITY);
	}
	
	public static synchronized Shooter getInstance(){
		if(shoot == null){
			shoot = new Shooter();
		}
		return shoot;
	}
	
	@Override
	protected boolean init() {
		
		return false;
	}

	@Override
	protected void liveWindow() {
		
	}

	@Override
	protected boolean execute() {
		
		return false;
	}

	@Override
	protected long sleepTime() {
		
		return 0;
	}

	@Override
	protected void writeLog() {
		
		
	}

}
