package org.gosparx.team1126.robot.util;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.util.Scanner;

import org.gosparx.team1126.robot.util.SharedData.Target;

//import edu.wpi.first.wpilibj.vision.VisionRunner.Listener;
/*########################################################################*/	
public class visionNetworkTable implements ITableListener{
	/*########################################################################*/	
	private static NetworkTable client;
	private DriverStationControls dsc;
	private String IP = "10.11.26.103"; //Jetsons boards IP
	private String serverKey = "mode"; //For sending mode
	private static String clientLift = "peg"; //For reciving angle and distance for lift
	private static String clientHighGoal = "highGoal"; ////For reciving angle and distance for HighGoal 
	private SharedData.Target currentMode; //BOILER or LIFT //0 is off, 1 is Highgoal, 2 is lift
	double[] arrTargetData;

	/*########################################################################*/
	public void networkTable() //Constructor
	{
		currentMode = SharedData.Target.BOILER;
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress(IP); 
		client = NetworkTable.getTable("targetData");  
		client.addTableListener(this, true);
	}

	/*#####################################################################################*/	
	public void serverUpdate() //For sending mode
	{
		try{	 
			currentMode = SharedData.targetType;		
			client.putValue(serverKey, new Boolean(currentMode == SharedData.Target.BOILER)); //Puts the mode in table
		} catch(Exception e){
		}finally{
			
		}
	}
	/*#####################################################################################*/	
	@Override //For listener 
	public void valueChanged(ITable itable, String Values_Key, Object val, boolean bln)
	{
		try //Catch exection e
		{
//			arrTargetData = (double[]) val; 
			if(Values_Key.equals(clientLift) || Values_Key.equals(clientHighGoal)) 
			{
				arrTargetData = (double[]) val; 
				SharedData.setTarget(currentMode, arrTargetData[1], arrTargetData[0]);

				try{Thread.sleep(5);} //Pauses	
				catch(InterruptedException e)
				{
				}
			}
			else
			{
				System.out.println("Invalid key taken");
			} 
		}

		catch (Exception e)
		{

		}
	}	
}	
