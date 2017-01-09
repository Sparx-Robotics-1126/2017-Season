package org.gosparx.team1126.robot.sensors;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/* This returns the color 
 * Version 1.0 Season 2015
 */

public class ColorSensor {
	
	/**
	 * State machine for Color
	 *
	 */
	public enum Color {
		UNKNOWN,
		WHITE,
		BLACK,
		RED,
		GREEN,
		BLUE 
	}

	/**
	 * Red input
	 */
	private AnalogInput redAnalogInput;
	
	/**
	 * Green input
	 */
	private AnalogInput greenAnalogInput;
	
	/**
	 * Blue input
	 */
	private AnalogInput blueAnalogInput;
	
	/**
	 * LED ouput
	 */
	private DigitalOutput lightLED;
	
	/**
	 * we treat white as 70 % of light returned
	 */
	static final double WHITE_THRESHOLD = 70;//(255 * 3) * .8; 
	
	/**
	 * we treat black as 20% of the addition of all of the colors and below
	 */
	static final double BLACK_THRESHOLD = (255 * 3) * .2;
	
	/**
	 * Determines if green analog input is used
	 */
	private final boolean useGreen;

	/**
	 * Constructs a colorSenosr object
	 * @param redChannel - red analog channel
	 * @param greenChannel - green analog channel
	 * @param blueChannel - blue analog channel
	 * @param ledChannel - DIO LED channel
	 */
	public ColorSensor(int redChannel, int greenChannel, int blueChannel, int ledChannel, String liveSubsystem, String liveName){
		redAnalogInput = new AnalogInput(redChannel);
		greenAnalogInput = new AnalogInput(greenChannel);
		blueAnalogInput = new AnalogInput(blueChannel);
		lightLED = new DigitalOutput(ledChannel);
		lightLED.set(true);
		useGreen = true;
		liveWindow(true, liveSubsystem, liveName);
	}
	
	/**
	 * Constructs a colorSensor object without a green input
	 * @param redChannel - red analog channel
	 * @param blueChannel - blue analog channel
	 * @param ledChannel - DIO LED channel
	 */
	public ColorSensor(int redChannel, int blueChannel, int ledChannel, String liveSubsystem, String liveName){
		redAnalogInput = new AnalogInput(redChannel);
		blueAnalogInput = new AnalogInput(blueChannel);
		lightLED = new DigitalOutput(ledChannel);
		lightLED.set(true);
		useGreen = false;
		liveWindow(false, liveSubsystem, liveName);
	}

	/**
	 * @return value of red (0 - 255) values may be bigger than 255
	 */
	public int getRed(){
		return redAnalogInput.getValue();
	}
	
	/**
	 * @return value of green (0 - 255) values may be bigger than 255
	 */
	private int getGreen(){
		if(useGreen){
			return greenAnalogInput.getValue();
		}else{
			return 0;
		}
	}
	
	/**
	 * @return value of blue (0 - 255) values may be bigger than 255
	 */
	public int getBlue(){
		return blueAnalogInput.getValue();
	}
	
	/**
	 * @param on - true if on, false is off
	 */
	public void setLED(boolean on){
		lightLED.set(on);
	}
	
	/**
	 * returns the color ID
	 */
	public Color getColor(){
		int redValue = getRed();
		int greenValue = getGreen();
		int blueValue = getBlue();
		int totalValue = redValue + greenValue + blueValue;
		
		if (totalValue >= WHITE_THRESHOLD){
			return Color.WHITE;
		}
		else if (totalValue <= BLACK_THRESHOLD){
			return Color.BLACK;
		}
		else if (blueValue >= redValue && blueValue >= greenValue){
			return Color.BLUE;
		}
		else if (greenValue >= redValue && greenValue >= blueValue){
			return Color.GREEN;
		}
		else if (redValue >= blueValue && redValue >= greenValue){
			return Color.RED;
		}
		return Color.UNKNOWN;
	}
	
	/**
	 * 
	 * @param color - color wanted from Color.
	 * @return the string value for the color
	 */
	public String colorToString(Color color){
		switch (color){
			case WHITE:
				return "White";
			case BLACK:
				return "Black";
			case RED:
				return "Red";
			case GREEN:
				return "Green";
			case BLUE:
				return "Blue";
			case UNKNOWN:
				return "Unknown";
			default:
				return "Invalid";
			
		}
	}
	
	/**
	 * 
	 * @param color - color wanted to see
	 * @return - if the color seen by the sensor is the same as the color wanted
	 */
	public boolean isColor(Color color) {
		return(getColor() == color);
	}
	
	private void liveWindow(boolean hasGreen, String subsystem, String name){
		if(hasGreen){
			LiveWindow.addSensor(subsystem, name + " Green Color", greenAnalogInput);
		}
		LiveWindow.addSensor(subsystem, name + "Red Color", redAnalogInput);
		LiveWindow.addSensor(subsystem, name + " Blue Color", blueAnalogInput);
	}
}
