package org.gosparx.team1126.robot.sensors;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Utility;

/**
 * Class for obtaining more reliable data from an encoder.
 * @author Mike Bortfeldt
 * @author Solis Knight
 * @version 1.5 Pre 2012
 */
public class EncoderData {
    private Encoder controlled;
    private Counter counter;
    private double distPerTick;
    private long lastTime;
    private long lastEncoderCount;
    private double speed;
    
    private boolean USE_COUNTER;
    
    /**
     * Constructor for the EncoderData.
     * @param controlled - the Encoder we wish to obtain accurate speeds for.
     * @param distPerTick - the distance per tick of this particular encoder.
     */
    public EncoderData(Encoder controlled, double distPerTick){
        this.controlled = controlled;
        controlled.setDistancePerPulse(distPerTick);
        this.distPerTick = distPerTick;
        lastTime = Utility.getFPGATime();
        USE_COUNTER = false;
    }
    
    public EncoderData(Counter controlled, double distPerTick){
        this.counter = controlled;
        this.distPerTick = distPerTick;
        lastTime = Utility.getFPGATime();
        USE_COUNTER = true;
    }
    
    /**
     * Method to accurately calculate speeds based on an encoder.  This routine
     * should be run with a minimum of 20 milliseconds between executions
     * to allow for enough time to get an accurate speed calculation.
     */
    public void calculateSpeed() {
        long currentTime = Utility.getFPGATime();
        long encoderCount = USE_COUNTER ? counter.get() : controlled.get();
        long elapsedTime = currentTime - lastTime;
        long deltaCount;
        if (elapsedTime < 20000)
            return;
        deltaCount = encoderCount - lastEncoderCount;
        lastTime = currentTime;
        lastEncoderCount = encoderCount;
        speed = ((double) deltaCount * distPerTick) / (elapsedTime / 1000000.0);
    }
    
    /**
     * Getter method for the calculated speed.
     * @return speed - the 'speed' the tracked object is moving based on the encoder.
     */
    public double getSpeed(){
        return speed;
    }
    
    /**
     * Get the distance the robot has driven since the last reset.
     *
     * @return The distance driven since the last reset as scaled by the value from setDistancePerPulse().
     */
    public double getDistance() {
        return USE_COUNTER ? counter.get() * distPerTick : controlled.getDistance();
    }

    /**
     * Resets the encoder data
     */
    public void reset(){
        if (USE_COUNTER){
            counter.reset();
        } else {
            controlled.reset();
        }
        lastTime = Utility.getFPGATime();
        lastEncoderCount =  USE_COUNTER ? counter.get() : controlled.get();
    }
    
    /**
     * @return the time (in seconds) that the encoder has updated its values 
     */
    public double getLastReadingTime(){
        return Utility.getFPGATime() - lastTime;
    }
}
