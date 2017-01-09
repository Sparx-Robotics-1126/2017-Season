package org.gosparx.team1126.robot.sensors;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * A class for interpreting data from an absolute encoder
 * @author Alex Mechler {amechler1998@gmail.com}
 */
public class AbsoluteEncoderData {

	/**
	 * The analog input of the absolute encoder
	 */
	private AnalogInput input;

	/**
	 * How many degrees do we go per volt change?
	 */
	private double degreesPerVolt;
	
	/**
	 * The voltage to count as 0
	 */
	private double zeroPos;

	/**
	 * The lowest possible return value, used for changing volts to degrees 
	 */
	private final double LOWER_LIMIT = 0.2;
	
	/**
	 * Creates a new absolute encoder data
	 * @param port The analog port of the input
	 * @param degPerVolt How many degrees we have turned per volt
	 */
	public AbsoluteEncoderData(int port, double degPerVolt){
		input = new AnalogInput(port);
		degreesPerVolt = degPerVolt; 
	}

	/**
	 * @return The current position of the data, corrected for zero pos and wrapping
	 */
	public double getDegrees(){
		double deg = (input.getVoltage() - zeroPos) * degreesPerVolt - (LOWER_LIMIT * degreesPerVolt);
		deg = deg >= 360 ? deg - 360 : deg;
		deg = deg <= 0 ? deg+360: deg;
		return deg;
	}

	/**
	 * @return The current voltage of the enc
	 */
	public double getVoltage(){
		return input.getVoltage();
	}
	
	/**
	 * Sets the zero position. 
	 */
	public void reset(){
		zeroPos = input.getVoltage();
	}
}
