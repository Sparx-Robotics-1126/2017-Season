package org.gosparx.team1126.robot.util;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;


//import edu.wpi.first.wpilibj.vision.VisionRunner.Listener;
/*########################################################################*/	
public class visionNetworkTable implements ITableListener{
/*########################################################################*/	
private static NetworkTable client;
private NetworkTable server;
private String IP = "10.11.26.20"; //Jetsons boards IP
//private int port = 1735; //Jenson's board port
private String serverKey = "mode"; //For sending mode
private String clientLift = "peg"; //For reciving angle and distance for lift
private String clientHighGoal = "highGoal"; ////For reciving angle and distance for HighGoal 
private double angle; //To hold angle
private double distance; //To hold distance
private SharedData.Target currentMode; //BOILER or LIFT //0 is off, 1 is Highgoal, 2 is lift
double[] arrGoalLift;

/*########################################################################*/	
//For testing (may need to make some variables static)
//public static void main(String[] args)
//{
//	
//	visionNetworkTable instance = new visionNetworkTable();
//	while (true)
//	{
//	instance.serverUpdate();
//	break;
//	}
//	//double[] d = {2.0,2.0};
//	//instance.valueChanged(client, clientHighGoal, d, true);
//	
//	}
/*########################################################################*/
public visionNetworkTable() //Constructor
{
	NetworkTable.setClientMode();
	NetworkTable.setIPAddress(IP); //Sets Ip address
	client = NetworkTable.getTable("targetData"); //Gets client table 

	//Adds the listener to see if value changed
	client.addTableListener(this, true);
	
	//NetworkTable.setServerMode(); //Sets to Server mode  
	server = NetworkTable.getTable(serverKey); //Makes server table
	//serverRun(); //To make sure the Table has a value
}

/*#####################################################################################*/	
private void serverUpdate() //For sending mode
{
	currentMode = SharedData.Target.BOILER;
//	if((currentMode!=lastMode)) //If I already sent current mode then it does not send
//	{
//		
		server.putValue(serverKey, new Boolean(currentMode == SharedData.Target.LIFT)); //Puts the mode in table
//		lastMode = currentMode;
		//System.out.println("Mode:"+currentMode); //Prints the mode for debug
//	}
	try{Thread.sleep(5);} //Pauses for 5 mili
	catch(Exception e)
	{
		System.out.println("\n"+"InterruptedException exception at run"); //If somthing went wrong
		System.out.println(e.getMessage());
	}
}
/*#####################################################################################*/	
@Override //For listner 
public void valueChanged(ITable itable, String Values_Key, Object val, boolean bln)
{
	try
	{
		arrGoalLift = (double[]) val; 
		if (Values_Key.equals(clientHighGoal) && currentMode == SharedData.Target.BOILER) //If it has the right key
		{
			//Gets the angle and distance for HighGoal
			 
			angle = arrGoalLift[0];
			distance = arrGoalLift[1];
			System.out.println("angle and distance: "+angle+" "+distance+"\n");	
			SharedData.angleToTarget = angle;
			SharedData.distanceToTarget = distance;
			
			try{Thread.sleep(5); System.out.println("Sleeping");}	
		    catch(InterruptedException e)
			{
			 System.out.println("\n"+"InterruptedException exception at run"+"\n");
			 System.out.println(e.getMessage()+"\n");
			}
		}
		else if(Values_Key == clientLift  && currentMode == SharedData.Target.LIFT) 
		{
			//Finds angle and distance for Lift
			
			
			angle = arrGoalLift[0];
			distance = arrGoalLift[1];
			System.out.println(angle+" "+distance+"\n");	
			SharedData.setTarget(currentMode, distance, angle);
			SharedData.angleToTarget = angle;
			SharedData.distanceToTarget = distance;
			
			///Shooter.update(angle,distance);
			
			try{Thread.sleep(5); System.out.println("Sleeping");}	
		    catch(InterruptedException e)
			{
			 System.out.println("\n"+"InterruptedException exception at run"+"\n");
			 System.out.println(e.getMessage()+"\n");
			}
	      
			//System.out.print(val); //Prints info
		}
		else
		{
			System.out.println("Somthing Wrong");
		} 
	}
	catch (Exception e)
	{
		System.out.println("\n"+"Error, value changed: "+Values_Key+" "+bln+" "+val+"\n");
		System.out.println(e.getMessage());
		// Print an error.
	}
}	
/*#####################################################################################*/	
//public void update(Boolean mode) //From nate
//{
//	if(mode!=null) //If true/false
//		{
//		mode = currentMode;
//		serverUpdate(); //Sends the value
//	}
//}

}	




