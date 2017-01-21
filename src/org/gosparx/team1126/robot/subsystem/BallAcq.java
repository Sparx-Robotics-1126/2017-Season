package org.gosparx.team1126.robot.subsystem;

public class BallAcq extends GenericSubsystem{

	public static BallAcq ballacq;
	
	private BallAcq(){
		super("BallAcq", Thread.NORM_PRIORITY);
	}

	/**
	 * This creates a drives object with a name and its priority
	 */
	public static synchronized BallAcq getInstance() {
		if(ballacq == null){
			ballacq = new BallAcq();
		}
		return ballacq;
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
		return 20;
	}

	@Override
	protected void writeLog() {
		// TODO Auto-generated method stub
		
	}

	
	
}
