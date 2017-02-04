package org.gosparx.team1126.robot.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.stream.Stream;

public class CSVReader {
//DOESNT WORK
	String line = ",";
	
	String csvSplit = "";
	
	BufferedReader br = null;
	
	public int[] readIntCSV(String CSVFile) {
        String[] csvOutput = null;
		try{
			br = new BufferedReader(new FileReader(CSVFile));
			while ((line = br.readLine()) != null) {
				csvOutput = line.split(csvSplit);
			}
			} catch(IOException ie) {
				ie.printStackTrace();
			}   
		int[] intOutput = Stream.of(csvOutput).mapToInt(Integer::parseInt).toArray();
		return intOutput;
	}
	public int[][] read2DIntCSV(String CSVFile){
	    BufferedReader br = new BufferedReader(new FileReader());
	    String line = "";

	    ArrayList<String[]> t = new ArrayList<String[]>();

	    while((line = br.readLine()) != null) {
	        StringTokenizer st = new StringTokenizer(line, ",");
	        String[] card = new String[8]; 
	        for(int i = 0; i < 8; i++) { 
	            String value = st.nextToken(); 
	            card[i] = value;
	        }
	        t.add(card);
	    }

	    for(int i = 0; i < t.size(); i++) {
	        for(int x = 0; x < t.get(i).length; x++) {
	            System.out.printf("card[%d][%d]: ", i, x);
	            System.out.println(t.get(i)[x]);
	        }
	    }
	}
}
