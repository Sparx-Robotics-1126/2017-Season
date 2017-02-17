package org.gosparx.team1126.robot.util;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

//import edu.wpi.first.wpilibj.vision.VisionRunner.Listener;
/*########################################################################*/	
public class visionNetworkTableRobot implements ITableListener {
/*########################################################################*/	
private NetworkTable client;
private NetworkTable server;
private String IP = "8.8.8.8"; //Jetsons boards IP
private int port = 1735; //Jenson's board port
private String serverKey = "mode"; //For sending mode
private String clientLift = "targetData"; //For reciving angle and distance for lift
private String clientHighGoal = "targetData"; ////For reciving angle and distance for HighGoal 
private double angle = 0; //To hold angle
private double distance = 0; //To hold distance
private Boolean currentMode = true; //0 is off, 1 is Highgoal, 2 is lift
private Boolean lastMode = false; //Has to be different than current mode in order to setup table
double[] arrLift;
double[] arrGoal;
//static{System.load("C:\\Users\\Master\\Desktop\\SparX 2017\\visionNetworkTabe\\libntcore.so");}
/*########################################################################*/	
public static void main(String[] args)
{
	visionNetworkTableRobot goodBYE = new visionNetworkTableRobot(); 
	goodBYE.serverRun();
	goodBYE.serverRun();
	goodBYE.serverRun();
}
/*########################################################################*/
public visionNetworkTableRobot() //Constructor
{
	
	NetworkTable.setIPAddress(IP); //Sets Ip address 
	NetworkTable.setPort(port); //Sets Port
	client = NetworkTable.getTable("targetData"); //Gets client table 

	//Adds the listener to see if value changed
	client.addTableListener(this);
	
	NetworkTable.setServerMode(); //Sets to Server mode  
	server = NetworkTable.getTable(serverKey); //Makes server table
	//serverRun(); //To make sure the Table has a value
}
/*#####################################################################################*/	
private void serverRun() //For sending mode
{
	if((currentMode!=lastMode)) //If I already sent current mode then it does not send
	{
		server.putValue("X", new Boolean(true)); //(serverKey, currentMode); //Puts the mode
		lastMode = currentMode;
		System.out.println("Mode:"+currentMode); //Prints the mode for debug
	}
	else
	{
		System.out.println("Mode: nothing changed"); //For debug
	}
	
	try{Thread.sleep(5);} //Pauses for 5 mili
	catch(Exception e)
	{
		System.out.println("\n"+"InterruptedException exception at run"); //If somthing went wrong
		System.out.println(e.getMessage()+"\n");
	}
}
/*#####################################################################################*/	
@Override //For listner 
public void valueChanged(ITable itable, String Values_Key, Object val, boolean bln)
{
	try
	{
		if (Values_Key == clientHighGoal && currentMode == true) //If it has the right key
		{
			//Gets the angle and distance for HighGoal
			arrGoal = (double[]) val; 
			arrGoal[0] = angle;
			arrGoal[1] = distance;
			System.out.println(angle+" "+distance+"\n");	
			///Shooter.update(angle,distance);
			
			try{Thread.sleep(5); System.out.println("Sleeping");}	
		    catch(InterruptedException e)
			{
			 System.out.println("\n"+"InterruptedException exception at run"+"\n");
			 System.out.println(e.getMessage()+"\n");
			}
		}
		else if(Values_Key == clientLift  && currentMode == false) 
		{
			//Finds angle and distance for Lift
			arrLift[0] = angle;
			arrLift[1] = distance;
			System.out.println(angle+" "+distance+"\n");	
			///Shooter.update(angle,distance);
			
			try{Thread.sleep(5); System.out.println("Sleeping");}	
		    catch(InterruptedException e)
			{
			 System.out.println("\n"+"InterruptedException exception at run"+"\n");
			 System.out.println(e.getMessage()+"\n");
			}
	      
			//System.out.print(val); //Prints info
		}
		else{System.out.println("Somthing Wrong");} 
	}
	catch (Exception e)
	{
		System.out.println("\n"+"Error, value changed: "+Values_Key+" "+bln+" "+val+"\n");
		System.out.println(e.getMessage());
		// Print an error.
	}
}	
/*#####################################################################################*/	
public void update(Boolean mode) //From nate
{
	if(mode!=null) //If 1-3
	{
		mode = currentMode;
		serverRun(); //Sends the value
	}
}

}	

//Without listner 

//	public static void main(String[] args){
//		System.out.print("hi");
//		NetworkTable table = run("datatable",IP);
////while(true)
////{
////	double[] hi = null;
////	table.getNumberArray("hi", hi);
////	
////		table.getNumberArray("key",Double[] arg1);
////}
//		
//		
// }


