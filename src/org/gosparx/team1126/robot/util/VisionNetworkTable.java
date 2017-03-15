package org.gosparx.team1126.robot.util;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

//import edu.wpi.first.wpilibj.vision.VisionRunner.Listener;
/*########################################################################*/	
public class VisionNetworkTable implements ITableListener{
	/*########################################################################*/	
	private static NetworkTable client;

	private String IP = "10.11.26.60"; //Jetsons boards IP
	//private int port = 1735; //Jenson's board port (might not need)
	private String serverKey = "mode"; //For sending mode
	private static String clientLift = "peg"; //For reciving angle and distance for lift
	private static String clientHighGoal = "highGoal"; ////For reciving angle and distance for HighGoal 
	private SharedData.Target currentMode; //BOILER or LIFT //0 is off, 1 is Highgoal, 2 is lift
	private SharedData.Target lastMode;
	double[] arrTargetData;
	private double xOffset=0.0;
	private double yOffset=0.0;
	private double dy;
	private double dx;
	
	public VisionNetworkTable() //Constructor
	{
		currentMode = SharedData.Target.BOILER;
		lastMode = currentMode;
		NetworkTable.setIPAddress(IP); //Sets Ip address
		client = NetworkTable.getTable("targetData"); //Gets client table 

		//Adds the listener to see if value changed
		client.addTableListener(this, true);
	}

	/*#####################################################################################*/	
	public void serverUpdate() //For sending mode
	{
		try{	 
			if(!lastMode.equals(SharedData.targetType))
			{
				currentMode = SharedData.targetType;
				client.putValue(serverKey, new Boolean(currentMode == SharedData.Target.LIFT)); //Puts the mode in table
				lastMode=currentMode;
			}
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
		//System.out.println("Change detected: "+val.getClass()+" at "+Values_Key+"\n Itable"+itable);
		if(SharedData.targetType!=SharedData.Target.NONE){
			try //Catch exection e
			{ 
				if(Values_Key.equals(clientLift) || Values_Key.equals(clientHighGoal)) 
				{
					arrTargetData = (double[]) val; 
					if(arrTargetData[0] !=-180){
						dy=arrTargetData[1];
						dx=dy*Math.tan(Math.toRadians(arrTargetData[0]));
						dx+=xOffset;
						dy+=yOffset;
						SharedData.setTarget(currentMode, dy, Math.toDegrees(Math.atan(dx/dy)));
//						System.out.println("Angle and distance: "+arrTargetData[0]+", "+arrTargetData[1]+"\n");	
						
					}
					try{	 //Pauses
						Thread.sleep(5); 
						System.out.println("Sleeping");
					}	
					catch(InterruptedException e)
					{
						System.out.println("\n"+"InterruptedException exception during sleep"+"\n");
						System.out.println(e.getMessage()+"\n");
					}

				}
				else
				{
					System.out.println("Invalid key taken (from Jetson board)");
				} 
			}

			catch (Exception e)
			{
				System.out.println("Jetson error, value changed: "+Values_Key+" "+bln+" "+val);
				System.out.println(e.getMessage());
				// Print an error.
			}
		}	
	}
	/*#####################################################################################*/	
}