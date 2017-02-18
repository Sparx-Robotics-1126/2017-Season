package org.gosparx.team1126.robot;

/**
 * Stores all of the IO information
 */

/** Subject to change */

public class IO {

	/**************************************PWM*****************************************/
	
	public static final int PWM_BALLACQ_SERVO_AGITATOR						= 4;
	
	public static final int PWM_BALLACQ_SERVO_FUELGATE  					= 5;
	
	/************************************DIO*******************************************/
	
	public static final int DIO_RIGHT_DRIVES_ENC_A                          = 10;

	public static final int DIO_RIGHT_DRIVES_ENC_B                          = 11;
	
	public static final int DIO_LEFT_DRIVES_ENC_A                           = 12;

	public static final int DIO_LEFT_DRIVES_ENC_B	                        = 13;
	
	public static final int DIO_GEARACQ_ENC									= 14;
	
	public static final int DIO_SHOOTER_ABS_ENC								= 15;
	
	public static final int DIO_CLIMBING_LIMITSWITCH						= 16;
	
	public static final int DIO_SHOOTER_ENC_A                               = 22;
	
	public static final int DIO_SHOOTER_ENC_B								= 23;
	
	/**********************************CAN********************************************/

	/*
	//CAN 2016 - Redefined for execution on the 2016 robot base
	
	public static final int CAN_DRIVES_LEFT_FRONT                           = 1;
	public static final int CAN_DRIVES_LEFT_BACK                            = 2;
	public static final int CAN_DRIVES_LEFT_TOP								= 4;	// Old Acquisition Shoulder
	public static final int CAN_DRIVES_RIGHT_FRONT                          = 9;
	public static final int CAN_DRIVES_RIGHT_BACK                           = 8;
	public static final int CAN_DRIVES_RIGHT_TOP							= 5;	// Old Acquisition Shoulder 
	 */
	
	// CAN 2017 - New definitions for 2017 robot
	
	public static final int CAN_BALLACQ_RIGHT								= 8;
	
	public static final int CAN_BALLACQ_LEFT								= 2;
	
	public static final int CAN_CLIMBING_WINCH	   							= 3;
	
	public static final int CAN_DRIVES_RIGHT_BACK                           = 4;
	
	public static final int CAN_DRIVES_RIGHT_FRONT                          = 5;
	
	public static final int CAN_DRIVES_RIGHT_TOP							= 6;
	
	public static final int CAN_SHOOTER_INTAKE_FEEDER						= 7;
	
	public static final int CAN_SHOOTER_TURRET								= 1;
	
	public static final int CAN_SHOOTER_FLYWHEEL                            = 9;
	
	public static final int CAN_DRIVES_LEFT_BACK                            = 10;
	
	public static final int CAN_DRIVES_LEFT_FRONT                           = 11;
	
	public static final int CAN_DRIVES_LEFT_TOP								= 12;

	/**********************************USB********************************************/
	
	public static final int USB_DRIVER_LEFT                                 = 0;
	
	public static final int USB_DRIVER_RIGHT                                = 1;
	
	public static final int USB_OPERATOR                                    = 2;
	
	/*********************************CAMS********************************************/
	
	public static final String[] CAMS                                       = {
			                                                                "cam0",
			                                                                "cam1"
	                                                                        };
	
	/***********************BUTTONS/JOYSTICK******************************************/
	
	public static final int ABORT_AUTO_DRIVES								= 1;
	
	public static final int INVERT_DRIVES_BUTTON							= 4;
	
	public static final int HOLD_DRIVES										= 6;
	
	public static final int BUTTON_SHOOTING_SYSTEM_ON						= 8;
	
	public static final int BUTTON_FIRE										= 11;
	
	public static final int SCALING_RT										= 9;
	
	public static final int RIGHT_JOY_X										= 2;
	
	public static final int RIGHT_JOY_Y										= 3;
	
	public static final int LEFT_JOY_X										= 0;
	
	public static final int LEFT_JOY_Y										= 1;
	
	public static final int DIAGNOSTICS										= 14;
	
	public static final int ACQ_ON											= 0;
	
	public static final int ACQ_OFF											= 4;
	
	public static final int ACQ_FOWARD										= 2;
	
	public static final int ACQ_BACKWARD									= 6;
	
}