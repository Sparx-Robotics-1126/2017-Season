package org.gosparx.team1126.robot.sensors;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * A class for interpreting the data from Magnetic Limit Switch
 * @author Alex Mechler {amechler1998@gmail.com}
 */
public class MagnetSensor {

	/**
	 * The digital input for the Magnetic Sensor
	 */
	public DigitalInput in;
	
	/**
	 * Is the output inversed
	 */
	private boolean inversed;
	/**
	 * Creates a new magnetic sensor
	 * @param dio - The digitalinput the sensor is in
	 * @param inverse - do we inverse the output
	 */
	public MagnetSensor(DigitalInput dio, boolean inverse){
		in = dio;
		inversed = inverse;
	}

	/**
	 * Creates a new magnetic sensor
	 * @param port - the port the sensor is in
	 * @param inverse - do we inverse the output
	 */
	public MagnetSensor(int port, boolean inverse){
		this(new DigitalInput(port), inverse);
	}

	/**
	 * @return if the sensor is tripped.
	 */
	public boolean isTripped(){
		if(inversed)
			return !in.get();
		return in.get();
	}
}
