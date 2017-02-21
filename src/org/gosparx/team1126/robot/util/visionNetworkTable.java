package org.gosparx.team1126.robot.util;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.util.Scanner;

//import edu.wpi.first.wpilibj.vision.VisionRunner.Listener;
/*########################################################################*/	
public class visionNetworkTable implements ITableListener{
	/*########################################################################*/	
	private static NetworkTable client;
	
	private String IP = "10.11.26.103"; //Jetsons boards IP
	//private int port = 1735; //Jenson's board port (might not need)
	private String serverKey = "mode"; //For sending mode
	private static String clientLift = "peg"; //For reciving angle and distance for lift
	private static String clientHighGoal = "highGoal"; ////For reciving angle and distance for HighGoal 
	private SharedData.Target currentMode; //BOILER or LIFT //0 is off, 1 is Highgoal, 2 is lift
	double[] arrTargetData;
	/*########################################################################*/	
	public static void main(String[] args)
	{
		int inputNum;
		Scanner read=new Scanner(System.in);
		visionNetworkTable instance = new visionNetworkTable();
		while (true) 
		{
			if(read.hasNextInt()){
				inputNum=read.nextInt();
				if(inputNum==0)
				{
					SharedData.targetType = SharedData.Target.BOILER;
				}

				else if(inputNum==1)
				{
					SharedData.targetType = SharedData.Target.LIFT;
				}
				else if(inputNum==1126||inputNum==42)
				{
					System.out.println("This is the meaning of life!!!!!");
				}
				else
				{
					System.out.println("Should be 0 or 1.");
				}

				instance.serverUpdate();
			}
			else
			{
				read.next();
				System.out.println("Value should be 0 (Boiler) or 1 (Lift).");
			}
		}
		//double[] d = {2.0,2.0};
		//instance.valueChanged(client, clientHighGoal, d, true);
		//	
	}
	/*########################################################################*/
	public visionNetworkTable() //Constructor
	{
		currentMode = SharedData.Target.BOILER;
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress(IP); //Sets Ip address
		client = NetworkTable.getTable("targetData"); //Gets client table 

		//Adds the listener to see if value changed
		client.addTableListener(this, true);
	}

	/*#####################################################################################*/	
	private void serverUpdate() //For sending mode
	{
		try{	 
			currentMode = SharedData.targetType;		
			client.putValue(serverKey, new Boolean(currentMode == SharedData.Target.LIFT)); //Puts the mode in table
			System.out.println("Mode:"+currentMode); //Prints the mode for debug

			try{Thread.sleep(5);} //Pauses for 5 mili
			catch(Exception e)
			{
				System.out.println("\n"+"Sleep problem??"); //If somthing went wrong
				System.out.println(e.getMessage());
			}}catch(Exception e){System.out.print("connection error");}
	}
	/*#####################################################################################*/	
	@Override //For listener 
	public void valueChanged(ITable itable, String Values_Key, Object val, boolean bln)
	{
		System.out.println("Change detected: "+val.getClass()+" at "+Values_Key+"\n Itable"+itable);
		try //Catch exection e
		{
//			arrTargetData = (double[]) val; 
			if(Values_Key.equals(clientLift) || Values_Key.equals(clientHighGoal)) 
			{
				arrTargetData = (double[]) val; 
				SharedData.setTarget(currentMode, arrTargetData[1], arrTargetData[0]);
				System.out.println("Angle and distance: "+arrTargetData[0]+", "+arrTargetData[1]+"\n");	

				try{Thread.sleep(5); System.out.println("Sleeping");} //Pauses	
				catch(InterruptedException e)
				{
					System.out.println("\n"+"InterruptedException exception during sleep"+"\n");
					System.out.println(e.getMessage()+"\n");
				}

				//System.out.print(val); //Prints info
			}
			else
			{
				System.out.println("Invalid key taken");
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
}	
