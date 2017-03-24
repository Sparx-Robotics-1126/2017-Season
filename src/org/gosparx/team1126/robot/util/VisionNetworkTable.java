package org.gosparx.team1126.robot.util;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

//import edu.wpi.first.wpilibj.vision.VisionRunner.Listener;
/*########################################################################*/	
public class VisionNetworkTable implements ITableListener{
	/*########################################################################*/	
	private static NetworkTable client;

	private static final String serverKey = "mode"; //For sending mode
	private static final  String clientLift = "peg"; //For reciving angle and distance for lift
	private static final String clientHighGoal = "highGoal"; ////For reciving angle and distance for HighGoal 
	
	private double liftAngle = -180.0;
	private double liftDistance = -1.0;
	private double boilerAngle = -180.0;
	private double boilerDistance = -1.0;
	
	private static final double xOffsetBoiler = 13.75;
	private static final double yOffsetBoiler = 13.5;
	private static final double xOffsetPeg = 13.75;
	private static final double yOffsetPeg = 12.5;
	
	public VisionNetworkTable() //Constructor
	{
		client = NetworkTable.getTable("targetData"); //Gets client table 

		//Adds the listener to see if value changed
		client.addTableListener(this, true);
	}

	/*#####################################################################################*/	
	public void serverUpdate() //For sending mode
	{
		try
		{
				client.putValue(serverKey, new Boolean(SharedData.targetType == SharedData.Target.LIFT)); //Puts the mode in table
		}
		catch(Exception e)
		{
			System.out.print("Connection error with Jetson board");
		}

	}
	/*#####################################################################################*/	
	@Override //For listener 
	public void valueChanged(ITable itable, String Values_Key, Object val, boolean bln)
	{
			try //Catch exection e
			{
				if(Values_Key.equals(clientLift))
				{
					double[] doubleArray = (double[])val;
					
					double inputAngle = doubleArray[0];
					double inputDistance = doubleArray[1];
					
					if (inputAngle != -180.0 && inputDistance != -1.0)
					{
						double dy = inputDistance;
						double dx = dy * Math.tan(Math.toRadians(inputAngle));
						dx += xOffsetPeg;
						dy += yOffsetPeg;
						
						liftDistance = dy;
						liftAngle = Math.toDegrees(Math.atan(dx/dy));					
						
						SharedData.setTarget(SharedData.Target.LIFT, liftDistance, liftAngle);
						
						//System.out.format("%f, %f\n", liftDistance.doubleValue(), liftAngle.doubleValue());
					}
				}
				else if (Values_Key.equals(clientHighGoal))
				{
					double[] doubleArray = (double[])val;
					
					double inputAngle = doubleArray[0];
					double inputDistance = doubleArray[1];
					
					if (inputAngle != -180.0 && inputDistance != -1.0)
					{
						double dy = inputDistance;
						double dx = dy * Math.tan(Math.toRadians(inputAngle));
						dx += xOffsetBoiler;
						dy += yOffsetBoiler;
						
						boilerDistance = dy;
						boilerAngle = Math.toDegrees(Math.atan(dx/dy));
						
						SharedData.setTarget(SharedData.Target.BOILER, boilerDistance, boilerAngle);
						
						//System.out.format("%f, %f\n", boilerDistance.doubleValue(), boilerAngle.doubleValue());
					}
				}
				else
				{
					System.out.println("Invalid key taken (from Jetson board)");
				} 
			}
			catch (Exception e)
			{
				System.out.println("Jetson error, value changed: "+ Values_Key + " " + bln + " " + val);
				System.out.println(e.getMessage());
			}
	}
	/*#####################################################################################*/
}