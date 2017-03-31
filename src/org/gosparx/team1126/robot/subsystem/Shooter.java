package org.gosparx.team1126.robot.subsystem;
/**
 * 
 * @Author - nphto
 */

//*****************************************Imports***************************************\\

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.CANTalon;
import org.gosparx.team1126.robot.IO;
import org.gosparx.team1126.robot.sensors.AbsoluteEncoderData;
import org.gosparx.team1126.robot.sensors.EncoderData;
import org.gosparx.team1126.robot.subsystem.GenericSubsystem;
import org.gosparx.team1126.robot.util.SharedData;

public class Shooter extends GenericSubsystem{

//*****************************************Variables*************************************\\
	
	/**
	 * current shooting speed & wanted speed
	 */
	private double shootingSpeedCurrent;
	private double shootingSpeed;
	private double shroudOutput;
	private double targetDistance;
	
	/**
	 * current turret degree - 2/3 rotation per 30 degrees & turret motor output
	 */
	private double turretDegreeCurrent;
	private double turretOutput;
		
	/**
	 * the local variable to see if the button is being pressed
	 */
	private boolean isPressed;
    private boolean lastPressed;
    
    private boolean manualControl;

	/**
	 * the local variable to see if the system should fire
	 */
	private boolean fireWhenReady;
	
	/**
	 * the degree the turret is off by
	 */
	private double degreeOff;
	
	/**
	 * if the system is ready to fire
	 */
	private boolean ready;
	
	/**
	 * speed for the shooter(temporary)
	 */
	private double speed;
		
	private long currentTime;									// For Diagnostics
	private double startingTurret;								// For Diagnostics
	
//*****************************************Objects***************************************\\
	
	private static Shooter shoot;	
	private Encoder encoder;
	private EncoderData encoderData;
	private AbsoluteEncoderData turretSensor;
	private Servo shroud;
	private CANTalon flyWheel;
	private CANTalon feeder;
	private CANTalon turret;
	private DiagnosticsEnuuum currentEnum;
	private DigitalInput limitSwitchLeft;
	private DigitalInput limitSwitchRight;
	
//*****************************************Constants*************************************\\
	
	/**
	 * used for the turret absolute encoder
	 */
	private final double DEGREE_PER_VOLT = 9.828;
	
	/**
	 * the value that the speed of the motor is  allowed to be off by.
	 */
	private final int SPEED_ALLOWED_OFF = 15;
	
	/**
	 * the max speed for the fly wheel
	 */
	private final double FLYWHEEL_MAX = 1;
	
	/**
	 * Initial wheel speed
	 */
	private final double INITIAL_SPEED = 1400;
	
	/**
	 * the speed that slowly decreases the fly wheel speed
	 */
	private final double FLYWHEEL_DECAY = 0.25;
	
	/**
	 * the speed that allows the balls to go into the fly wheel 
	 */
	private final double INTAKE_BALL_SPEED = 1; 
	
	/**
	 * the difference in fly wheel speeds allowed
	 */
	private final double FlYWHEEL_DEADBAND = 100;
	
	/**
	 * used for the encoder data
	 */
	private final double DIST_PER_TICK = 0.154194079; //0.146484375; //(1.0/256.0)*60.0;
	
	/**
	 * turret center position in volts
	 */
	private final double ZERO_VOLTAGE = 2.64;
	
	private BallAcq ballAcq;	
	
	private boolean visionOff;
//*****************************************Methods***************************************\\	
	/**
	 * Constructs a shooter object
	 */
	public Shooter(){
		super("Shooter", Thread.NORM_PRIORITY);
	}
	

	/**
	 * makes sure there is only one instance of shooter
	 * @return - a shooter object
	 */
	public static synchronized Shooter getInstance(){
		if(shoot == null){
			shoot = new Shooter();
		}
		return shoot;
	}
	
//-----------------------------------------------------------------------------------------
	/**
	 * instantiates all the objects and gives data to the variables
	 */
	@Override
	protected boolean init(){
		ballAcq = BallAcq.getInstance();
		encoder = new Encoder(IO.DIO_SHOOTER_ENC_A, IO.DIO_SHOOTER_ENC_B);
		encoderData = new EncoderData(encoder, DIST_PER_TICK); 
		turretSensor = new AbsoluteEncoderData(IO.ANALOG_SHOOTER_ABS_ENC, DEGREE_PER_VOLT);
		turretSensor.setZero(ZERO_VOLTAGE);
		shroud = new Servo(IO.PWM_SHOOTER_SHROUD);
		flyWheel = new CANTalon(IO.CAN_SHOOTER_FLYWHEEL);
		feeder = new CANTalon(IO.CAN_SHOOTER_INTAKE_FEEDER);
		turret = new CANTalon(IO.CAN_SHOOTER_TURRET);
	 	limitSwitchLeft = new DigitalInput(IO.DIO_SHOOTER_LIMITSWITCH_LEFT);
	 	limitSwitchRight = new DigitalInput(IO.DIO_SHOOTER_LIMITSWITCH_RIGHT);
		currentEnum = DiagnosticsEnuuum.DONE;
		shootingSpeedCurrent = 0;
		isPressed = false;
		degreeOff = 0;
		ready = false;
		manualControl = false;
		speed = INITIAL_SPEED;
		shroudOutput = 0;
		dsc.setAxisDeadband(IO.TURRET_JOY_X, .1); //added for manual turret control
		
		return true;
	}

//-----------------------------------------------------------------------------------------
	/**
	 * used to set data during testing mode
	 */
	@Override
	protected void liveWindow(){
		String subsystemName = "Shooter";
		LiveWindow.addActuator(subsystemName, "Turret Motor", turret);
		LiveWindow.addActuator(subsystemName, "FlyWheel", flyWheel);
		LiveWindow.addActuator(subsystemName, "FeederWheel", feeder);
		LiveWindow.addActuator(subsystemName, "Encoder", encoder);
		LiveWindow.addSensor(subsystemName, "Right Limit Switch", limitSwitchRight);
		LiveWindow.addSensor(subsystemName, "Left Limit Switch", limitSwitchLeft);
	}

//-----------------------------------------------------------------------------------------
	/**
	 * makes the robot shoot and turn its turret and stuff
	 * @return - does not mean anything
	 */
	@Override  
	protected boolean execute(){
		boolean turretReady;
		boolean shooterReady;
		boolean fireOverride = false;
		boolean currentManualControl = false;

		encoderData.calculateSpeed();
		shootingSpeedCurrent = encoderData.getSpeed();
		turretDegreeCurrent = turretSensor.relDegrees();

		if(dsc.isPressed(IO.DIAGNOSTICS))
			diagnostics();
		else
			currentEnum = DiagnosticsEnuuum.FLYWHEEL;

		// Get Shooting System Command - If Operator Control, then get the input from the joystick,
		// otherwise Autonomous will automatically set/clear isPressed by calling the routine
		// "shooterSystemState".  Otherwise make sure isPressed is false.
		
		if (!dsc.isAutonomous()){
			if (dsc.isOperatorControl()){
				visionOff = false;
				isPressed = dsc.isPressed(IO.FLIP_SHOOTING_SYSTEM_ON);
				fireWhenReady = dsc.isPressed(IO.BUTTON_FIRE);
				currentManualControl = dsc.isPressed(IO.FLIP_BOILER_SHOT);
				fireOverride = dsc.isPressed(IO.FIRE_OVERRIDE);

				if ((currentManualControl == true) && (manualControl == false)){
					speed = INITIAL_SPEED;
					shroudOutput = 0;
					degreeOff = 0;
				}			
				
				manualControl = currentManualControl;
			} else {
				isPressed = false;
				fireWhenReady = false;
				manualControl = false;
			}

//			LOG.logMessage(35,50,"Deg/Corr :" + degreeOff + "/" + (SharedData.getCorrectedTargetAngle(SharedData.Target.BOILER)));
//			LOG.logMessage(36,50,"Boiler (X,Y):"+ SharedData.targetXBoiler+","+SharedData.targetYBoiler);
//			LOG.logMessage(37,50,"Heading:" + SharedData.heading + " (" + SharedData.x + "," + SharedData.y+")");
		}
		
		
		if ((manualControl == false) && (visionOff == false)){
			targetDistance = SharedData.distanceToBoiler;
//			degreeOff = -SharedData.getCorrectedTargetAngle(SharedData.Target.BOILER) - 4;
			degreeOff = SharedData.angleToBoiler - 4.0;
		}
		
		// Check to see if the system state has changed
		
		if (lastPressed != isPressed){
			if (isPressed){
				SharedData.targetType = SharedData.Target.BOILER;

				if (manualControl == false)
					speed = INITIAL_SPEED;
			}
			else
				SharedData.targetType = SharedData.Target.NONE;

			lastPressed = isPressed;
		}

		// fireCtrl checks to see if target and wheel is ready to fire.  Turret on Target, Wheel @ speed
		
		turretReady = turretCtrl();
		shooterReady = speedCtrl();		
		ready = turretReady & shooterReady;
		
		if((ready && fireWhenReady) || fireOverride){
			feeder.set(INTAKE_BALL_SPEED);
		}else{
			feeder.set(0);
		}
		
		turret.set(turretOutput);
		shroud.set(shroudOutput);;
		
		SharedData.systemReady = ready;
		SharedData.turretAngle = turretDegreeCurrent;
		SharedData.shooterSpeed = shootingSpeedCurrent;		
		SmartDashboard.putBoolean("Turret Limit Switch Left", limitSwitchLeft.get());
		SmartDashboard.putBoolean("Turret Limit Switch Right", limitSwitchRight.get());
		SmartDashboard.putNumber("Turret Angle", turretDegreeCurrent);
		SmartDashboard.putNumber("Shooter Speed", speed);
		SmartDashboard.putBoolean("Flywheel On", isPressed);
		SmartDashboard.putBoolean("Feederwheel On", fireWhenReady);
		SmartDashboard.putBoolean("Fire Feeder Override", fireOverride);
		SmartDashboard.putNumber("Shroud Setting", shroudOutput);
		return false;
	}

//-----------------------------------------------------------------------------------------
	/**
	 * time to rest the system between loops 
	 */
	@Override
	protected long sleepTime(){
		return 15;
	}

//-----------------------------------------------------------------------------------------
	/**
	 * for logging messages
	 */
	@Override
	protected void writeLog(){
		LOG.logMessage("Flywheel SP/Spd: " + (int) speed +"/" + (int) shootingSpeedCurrent);
		LOG.logMessage("Turret Angle/Volt/Degrees Off: "+ turretDegreeCurrent + " / " + 
				turretSensor.getVoltage() + " / " + degreeOff);
		LOG.logMessage("Distance Away: " + targetDistance);
	}
	
//-----------------------------------------------------------------------------------------
	/**
	 * calculates the required speed needed for shooting 
	 * @return - the required speed
	 */
	private double distanceToSpeed(){
		double speed;
		
		if (targetDistance < 35)
			speed = 1400;
		else if (targetDistance < 85)
			speed = 1400 + (targetDistance - 35.0) * 1.5;
		else
			speed = 1450 + (targetDistance - 85.0) * 7.6;
		
		return speed;
	}

	private double distanceToShroud(){
		double shroudOutput;
		
//		0 @ boiler, 60% @ 6'6" (center of boiler)
		if (targetDistance < 35)
			shroudOutput = 0;
		else if (targetDistance > 90)
			shroudOutput = .50;
		else
			shroudOutput = (targetDistance - 35.0) * 0.009; 

		return shroudOutput;
	}
	
//-----------------------------------------------------------------------------------------
	/**
	 * checks if the motors are ready and are at a correct speed9999
	 * @param button - if the button is pressed 
	 * @return - if this system is ready
	 */
	private boolean speedCtrl(){
		if(!isPressed){
			flyWheel.set(0);
			return false;
		}
		
		if(visionOff || manualControl){			// Manual control of the flywheel and shroud
			shroudOutput -= (dsc.getAxis(IO.SHROUD_MANUAL_AXIS) / 100.0);
			
			if (shroudOutput > .50)
				shroudOutput = .50;
			else if (shroudOutput < 0)
				shroudOutput = 0;
			
			if(dsc.getButtonRising(IO.FLYWHEEL_INCREASE)){
				speed += 25;
			}else if(dsc.getButtonRising(IO.FLYWHEEL_DECREASE)){
				speed -= 25;
			}

			shootingSpeed = speed;
		} else {
			shootingSpeed = distanceToSpeed();
			shroudOutput = distanceToShroud();
		}
		
		if(shootingSpeedCurrent < shootingSpeed - SPEED_ALLOWED_OFF){
			flyWheel.set(FLYWHEEL_MAX);
		}else if(shootingSpeedCurrent + SPEED_ALLOWED_OFF > shootingSpeed){
			flyWheel.set(FLYWHEEL_DECAY + (shootingSpeed * 0.0001));
		}
		
		if(Math.abs(shootingSpeedCurrent - shootingSpeed) > FlYWHEEL_DEADBAND)
				return false;
		
		return true;	 
	}
		
//-----------------------------------------------------------------------------------------
	/**
	 * checks if the turret is ready to fire(correct angle to fire)
	 * @return - if this system is ready
	 */
	private boolean turretCtrl(){
		boolean turretReady = false;
		
		turretOutput = 0;
		
		if(visionOff || manualControl){					// Manual control of turret.
			if(dsc.getAxis(IO.TURRET_JOY_X) < -0.25)
				turretOutput = .2;
			else if (dsc.getAxis(IO.TURRET_JOY_X) > 0.25)
				turretOutput = -.2;

			turretReady = true;
		}else if(dsc.isPressed(IO.DIAGNOSTICS) || (isPressed)){
			// Only control if system is ON and we have a boiler image
			
			if(turretDegreeCurrent < degreeOff - .5)
				turretOutput = .50;
			else if(turretDegreeCurrent > degreeOff + 0.5)
				turretOutput = -.50;
			else
				turretReady = true;

			turretOutput *= (.2 + (Math.abs(turretDegreeCurrent - degreeOff) * 0.25));
		}
		
		// Turret Limit Protection and output to turret motor.
		
		if ((limitSwitchRight.get() && turretOutput < 0) ||
				(limitSwitchLeft.get() && turretOutput > 0))
			turretOutput = 0;
		
		return turretReady;
	}
		
//-----------------------------------------------------------------------------------------
	/**
	 * Sets the shooting system on for auto
	 * @param isOn - auto sends 1 to shoot
	 */
	public void shooterSystemState(int isOn, int speeds, int vision, int shroudPos){
		if ((isOn == 1) && dsc.isAutonomous()){
			speed = speeds;
			shroud.set(((double) shroudPos) /100.0);
			isPressed = true;
		}else{
			isPressed = false;
		}
		if(vision == 1){
			visionOff = true;
		}else
			visionOff = false;
	}
	
	public void shooterSystemFire(int fire) {
		if ((fire == 1) && dsc.isAutonomous()){
			fireWhenReady = true;
			ballAcq.transport(true);
		}else{
			fireWhenReady = false;
			ballAcq.transport(false);
		}
	}
	
	public void shooterSetTurret(int turretAngle){
		degreeOff = turretAngle;
	}
	
//-----------------------------------------------------------------------------------------
	/**
	 * Diagnostics
	 */
	private enum DiagnosticsEnuuum{
		DONE,
		FLYWHEEL,
		FLYWHEEL_WAIT,
		TURRET_WAIT1,
		TURRET_WAIT2
	}
	
	private void diagnostics(){
		switch(currentEnum){
		case DONE:
			flyWheel.set(0);
			degreeOff = turretDegreeCurrent;
			break;

		case FLYWHEEL:
			flyWheel.set(0.5);
			currentTime = System.currentTimeMillis();
			currentEnum = DiagnosticsEnuuum.FLYWHEEL_WAIT;
			break;

		case FLYWHEEL_WAIT:
			degreeOff = 0.0;

			if(System.currentTimeMillis() < currentTime + 1000)
				break;

			if(shootingSpeedCurrent > 10)
				LOG.logMessage("Flywheel/Encoder OK");
			else if(shootingSpeedCurrent < -10)
				LOG.logMessage("Flywheel Motor or Encoder is backwards");
			else
				LOG.logMessage("Flywheel not turning, or encoder not reading");

			flyWheel.set(0);

			// setup Turret
			startingTurret = turretDegreeCurrent;
			currentTime = System.currentTimeMillis();
			currentEnum = DiagnosticsEnuuum.TURRET_WAIT1;
			break;

		case TURRET_WAIT1:
			degreeOff = 5.0;

			if(System.currentTimeMillis() < currentTime + 1000)
				break;

			if (Math.abs(turretDegreeCurrent - 5.0) < 1.0)
				LOG.logMessage("Turret 5 degrees OK");
			else if (Math.abs(turretDegreeCurrent - startingTurret) < 0.2)
				LOG.logMessage("Turret Not Turning, or Encoder not Reading");
			else
				LOG.logMessage("Turret or Absolute Encoder Error");
			
			currentTime = System.currentTimeMillis();
			currentEnum = DiagnosticsEnuuum.TURRET_WAIT2;
			break;

		case TURRET_WAIT2:
			degreeOff = -5.0;

			if(System.currentTimeMillis() < currentTime + 1000)
				break;

			if (Math.abs(turretDegreeCurrent + 5.0) < 1.0)
				LOG.logMessage("Turret -5 degrees OK");
			else if (Math.abs(turretDegreeCurrent - startingTurret) < 0.2)
				LOG.logMessage("Turret Not Turning, or Encoder not Reading");
			else
				LOG.logMessage("Turret or Absolute Encoder Error");

			currentEnum = DiagnosticsEnuuum.DONE;
		}
	}
}