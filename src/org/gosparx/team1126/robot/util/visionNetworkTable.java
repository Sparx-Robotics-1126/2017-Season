package org.gosparx.team1126.robot.util;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
//import edu.wpi.first.wpilibj.vision.VisionRunner.Listener;

public class visionNetworkTable implements ITableListener {
/*########################################################################*/	
private NetworkTable client;
private NetworkTable server;
private String IP = "8.8.8.8";
private int port = 1735;
private String serverKey = "mode";
private String clientLift = "targetData";
private String clientHighGoal = "targetData";
private double angle = 0;
private double distance = 0;
private int currentMode = 0; //0 is off, 1 is Highgoal, 2 is lift
private int lastMode = 1; //Has to be different than current mode in order to setup table
/*########################################################################*/
public visionNetworkTable()
{
	
	NetworkTable.setIPAddress(IP); //Sets Ip address 
	NetworkTable.setPort(port); //Sets Port
	client = NetworkTable.getTable("targetData"); //Gets client table 

	//Adds the listener see value changed
	client.addTableListener(this);
	
	NetworkTable.setServerMode(); //Sets Server mode  
	server = NetworkTable.getTable(serverKey); //Gets server table
}
/*#####################################################################################*/	
public void serverRun() //For sending mode
{
	if(currentMode!=lastMode) //If I already sent current mode then it does not send
	{
		server.putValue(serverKey, currentMode);
		System.out.println(currentMode);
	}
	else
	{
		System.out.println("nothing changed");
	}
	
	try{Thread.sleep(5);}
	catch(Exception e)
	{
		System.out.println("\n"+"InterruptedException exception at run");
		System.out.println(e.getMessage()+"\n");
	}
}
/*#####################################################################################*/	
@Override
public void valueChanged(ITable itable, String Values_Key, Object val, boolean bln)
{
	try
	{
		if (Values_Key == clientHighGoal && currentMode == 0) //If it has the right key
		{

			double[] arr = (double[]) val;
			angle = arr[0];
			distance = arr[1];
			System.out.println(angle+" "+distance+"\n");	
			///Shooter.update(angle,distance);
			
			try{Thread.sleep(5); System.out.println("Sleeping");}	
		    catch(InterruptedException e)
			{
			 System.out.println("\n"+"InterruptedException exception at run"+"\n");
			 System.out.println(e.getMessage()+"\n");
			}
		}
		else if(Values_Key == clientLift) 
		{
			double[] arr = (double[]) val;
			angle = arr[0];
			distance = arr[1];
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

