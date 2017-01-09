package org.gosparx.team1126.robot.sensors;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * A class for easily interfacing with the REV-11-1107
 * @author Alex Mechler {amechler1998@gmail.com}
 */
public class PressureSensor {

	/**
	 * The analog input that the sensor is on
	 */
	private AnalogInput input;
	
	/**
	 * The input voltage provided to the sensor
	 */
	private double inputVoltage;
	
	/**
	 * What to use if we are no provided with another input voltage
	 */
	private final static double DEFAULT_VOLTS = 5.0;
	
	/**
	 * The slope of the conversion of the return volts to pressure. From documentation.
	 */
	private final int SLOPE = 250;
	
	/**
	 * The Y intercept of the conversion of the return volts to pressure. From the docs. 
	 */
	private final int Y_INTERCEPT = -25;
	
	/**
	 * Creates a new PressureSensor
	 * @param in The port the sensor is on
	 * @param volts The voltage we are providing to the sensor
	 */
	public PressureSensor(int in, double volts){
		input = new AnalogInput(in);
		inputVoltage = volts;
	}
	
	/**
	 * Creates a new PressureSensor with an input voltage of DEFAULT_VOLTS
	 * @param in The port the sensor is on
	 */
	public PressureSensor(int in){
		this(in, DEFAULT_VOLTS);
	}
	
	/**
	 * @return the analog volts the sensor is returning
	 */
	public double getVolts(){
		return input.getVoltage();
	}
	
	/**
	 * Formula comes from the official documentation
	 * @return The pressure in PSI the sensor is reading
	 */
	public double getPressure(){
		return  SLOPE * (input.getVoltage()/inputVoltage) + Y_INTERCEPT;
	}
}
