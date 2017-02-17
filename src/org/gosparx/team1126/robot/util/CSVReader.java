package org.gosparx.team1126.robot.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class CSVReader {
	
	/**
	 * A class for converting CSV files into 2D int arrays.
	 * @author Jensen Li
	 */
	
	public int[][] readIntCSV(String CSVFile) {

		int[][] currentAuto = null; //This holds the currentAuto that will be sent to Autonomous
		
		int height = 0; //This holds the "height" (vertical length) of the CSV.

		String delimiter = ","; //Holds the delimiter (ooh fancy word, it means boundaries and stuff), just in case we want to switch off commas.
		
		try{ 
		
		BufferedReader heightReader = new BufferedReader(new FileReader(CSVFile)); //Used to read the CSV file
		while (heightReader.readLine() != null) height++;					
		//Gets the height of the CSV.
		heightReader.close();												 //Closes the BufferedReader
																			 //, releasing used resources.
		currentAuto = new int[height][];									 //Adds the columns to the returned info.
		BufferedReader reader2 = new BufferedReader(new FileReader(CSVFile));//Used to read the CSV file (could mark the location at the start and go back to the beginning but it has been known to have downsides if the file is too big (although that should never happen in this case).
		for(int i = 0; i < height; i++){
			String line = reader2.readLine();							     //Reads the line and puts it into a string
			String[] splitLine = line.split(delimiter);
			ArrayList<String> lineList = new ArrayList<String>();
			for(String value : lineList){
				lineList.add(value);
			}
			currentAuto[i] = new int[lineList.size()];
			for(int x = 0; x < lineList.size(); x++){
				currentAuto[i][x] = Integer.parseInt(lineList.get(x));
			}
		}
		reader2.close();
		} catch (FileNotFoundException e) {  //These return exceptions if something has gone wrong 
			currentAuto[0][0] = -1;			 //Attempts to return a different value depending on the
			return currentAuto;				 //error to hopefully log it using Logger in the Auto
		} catch (NumberFormatException e) {  //class.
			currentAuto[0][0] = -2;			 //***TODO: Add potentially missing exceptions to log in Auto
			return currentAuto;				 //***Check for any potential failures in the code that would
		} catch (Exception e) {				 //***result in false information.
			currentAuto[0][0] = -99;
			return currentAuto;
		} finally {
			
		}
		return currentAuto;
	}
		
}
