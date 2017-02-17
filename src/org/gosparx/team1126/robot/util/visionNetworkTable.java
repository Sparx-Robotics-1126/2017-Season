package org.gosparx.team1126.robot.util;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class visionNetworkTable implements ITableListener {
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
	private int currentMode = 0; //0 is off, 1 is Highgoal, 2 is lift
	private int lastMode = 1; //Has to be different than current mode in order to setup table
	/*########################################################################*/
	public visionNetworkTable() //Constructor
	{

		NetworkTable.setIPAddress(IP); //Sets Ip address 
		NetworkTable.setPort(port); //Sets Port
		client = NetworkTable.getTable("targetData"); //Gets client table 

		//Adds the listener to see if value changed
		client.addTableListener(this);

		NetworkTable.setServerMode(); //Sets to Server mode  
		server = NetworkTable.getTable(serverKey); //Makes server table
		serverRun(); //To make sure the Table has a value
	}
	/*#####################################################################################*/	
	private void serverRun() //For sending mode
	{
		if(currentMode!=lastMode) //If I already sent current mode then it does not send
		{
			server.putValue(serverKey, currentMode); //Puts the mode
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
			else if(Values_Key == clientLift  && currentMode == 1) 
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
					System.out.println("Message:"+e.getMessage()+"\n");
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
	public void update(int mode) //From nate
	{
		if(mode>=0||mode<=3) //If 1-3
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

