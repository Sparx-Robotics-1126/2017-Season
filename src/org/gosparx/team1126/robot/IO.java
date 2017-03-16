package org.gosparx.team1126.robot;

import org.gosparx.team1126.robot.util.DriverStationControls;

/**
 * Stores all of the IO information
 */

/** Subject to change */

public class IO {

	/**************************************PWM*****************************************/
	
	public static final int PWM_BALLACQ_SERVO_AGITATOR					= 14;
	public static final int PWM_BALLACQ_SERVO_FUELGATE  				= 15;
	
	/************************************DIO*******************************************/
	
	public static final int DIO_RIGHT_DRIVES_ENC_A                      = 0;
	public static final int DIO_RIGHT_DRIVES_ENC_B                      = 1;
	public static final int DIO_LEFT_DRIVES_ENC_A                       = 12;
	public static final int DIO_LEFT_DRIVES_ENC_B	                    = 13;
	public static final int DIO_GEARACQ_SENSOR							= 14;
	public static final int ANALOG_SHOOTER_ABS_ENC						= 7;
	public static final int DIO_CLIMBING_LIMITSWITCH					= 16;
	public static final int DIO_JETSON_RESET							= 17;
	public static final int DIO_SHOOTER_LIMITSWITCH_RIGHT				= 20;
	public static final int DIO_SHOOTER_LIMITSWITCH_LEFT				= 21;
	public static final int DIO_SHOOTER_ENC_A                           = 22;
	public static final int DIO_SHOOTER_ENC_B							= 23;
	public static final int DIO_SHOOTER_SHROUD							= 18;
	
	/**********************************CAN********************************************/
	
	//public static final int CAN_BALLACQ_RIGHT							= 1;
	public static final int CAN_SHOOTER_TURRET							= 2;
	public static final int CAN_CLIMBING_WINCH	   						= 3;
	public static final int CAN_DRIVES_RIGHT_BACK                       = 4;
	public static final int CAN_DRIVES_RIGHT_FRONT                      = 5;
	//public static final int CAN_DRIVES_RIGHT_TOP						= 6;
	public static final int CAN_SHOOTER_INTAKE_FEEDER					= 7;
	public static final int CAN_BALLACQ_LEFT							= 8;
	public static final int CAN_SHOOTER_FLYWHEEL                        = 9;
	public static final int CAN_DRIVES_LEFT_BACK                        = 10;
	public static final int CAN_DRIVES_LEFT_FRONT                       = 11;
	//public static final int CAN_DRIVES_LEFT_TOP						= 12;
	public static final int CAN_HOPPER_HORIZONTAL_BELT                  = 12;

	/**********************************USB Joysticks**********************************/
	
	public static final int USB_DRIVER_LEFT                             = 0;
	public static final int USB_DRIVER_RIGHT                            = 1;
	public static final int USB_OPERATOR                                = 2;
	
	/*********************************CAMS********************************************/
	
	public static final String[] CAMS = {"cam0", "cam1"};
	
	/***********************BUTTONS/JOYSTICK******************************************/
	
	// Drives
	
	public static final int AUTO_TURN									= DriverStationControls.LEFT_JOY_TRIGGER;
	public static final int AUTO_DRIVE									= DriverStationControls.RIGHT_JOY_LEFT;
	public static final int RESET_ENCODER								= DriverStationControls.LEFT_JOY_RIGHT;
	public static final int ABORT_AUTO_DRIVES							= DriverStationControls.LEFT_JOY_LEFT;
	public static final int INVERT_DRIVES_BUTTON						= DriverStationControls.RIGHT_JOY_TRIGGER;
	public static final int HOLD_DRIVES									= DriverStationControls.RIGHT_JOY_RIGHT;

	// Shooter
	
	public static final int FLIP_SHOOTING_SYSTEM_ON						= DriverStationControls.LEFT_JOY_MIDDLE;
	public static final int FLIP_TARGET_LIFT							= DriverStationControls.OP_XBOX_L2;
	public static final int BUTTON_FIRE									= DriverStationControls.OP_XBOX_A;
	public static final int FIRE_OVERRIDE								= DriverStationControls.OP_XBOX_Y;
	public static final int SCALING_RT									= DriverStationControls.OP_XBOX_R2;
	public static final int RIGHT_JOY_X									= DriverStationControls.RIGHT_JOY_X_AXIS;
	public static final int RIGHT_JOY_Y									= DriverStationControls.RIGHT_JOY_Y_AXIS;
	public static final int LEFT_JOY_X									= DriverStationControls.LEFT_JOY_X_AXIS;
	public static final int LEFT_JOY_Y									= DriverStationControls.LEFT_JOY_Y_AXIS;
	public static final int TURRET_JOY_Y								= DriverStationControls.OP_XBOX_LEFT_X;
	public static final int DIAGNOSTICS									= DriverStationControls.OP_XBOX_BACK;
	public static final int FLYWHEEL_INCREASE						    = DriverStationControls.OP_XBOX_B;
	public static final int FLYWHEEL_DECREASE 							= DriverStationControls.OP_XBOX_X;

	// Acquisition & Shooting Belts
	
	public static final int ACQ_ON										= DriverStationControls.OP_XBOX_POV_UP;
	public static final int ACQ_OFF										= DriverStationControls.OP_XBOX_POV_DOWN;
	public static final int ACQ_FEED_RIGHT								= DriverStationControls.OP_XBOX_POV_RIGHT;
	public static final int ACQ_FEED_LEFT								= DriverStationControls.OP_XBOX_POV_LEFT;
	
}