package org.gosparx.team1126.robot;

/**
 * Stores all of the IO information
 */

/** Subject to change */

public class IO {

	/**************************************PWM*****************************************/
	
//	public static final int PWM_DRIVES_LEFT_FRONT		= 0;
//	
//	public static final int PWM_DRIVES_LEFT_REAR		= 1;
//	
//	public static final int PWM_DRIVES_RIGHT_FRONT		= 3;
//	
//	public static final int PWM_DRIVES_RIGHT_REAR		= 2;
	
	/************************************DIO*******************************************/

	public static final int DIO_LEFT_DRIVES_ENC_A                           = 10;

	public static final int DIO_LEFT_DRIVES_ENC_B                           = 11;

	public static final int DIO_RIGHT_DRIVES_ENC_A                          = 22;

	public static final int DIO_RIGHT_DRIVES_ENC_B                          = 23;
	
	public static final int DIO_SHOOTER_ENC                                 = 0;

	public static final int DIO_BALLACQ_ENC_GATE_A                          = 0;
	
	public static final int DIO_BALLACQ_ENC_GATE_B                          = 0;
	
	/**********************************CAN********************************************/

	public static final int CAN_DRIVES_LEFT_FRONT                           = 1;

	public static final int CAN_DRIVES_LEFT_BACK                            = 2;

	public static final int CAN_DRIVES_RIGHT_FRONT                          = 9;
	
	public static final int CAN_DRIVES_RIGHT_BACK                           = 8;
	
	public static final int CAN_SHOOTER_A                                   = 0;
	
	public static final int CAN_SHOOTER_B                                   = 0;

	/**********************************USB********************************************/
	
	public static final int USB_DRIVER_LEFT                                 = 0;
	
	public static final int USB_DRIVER_RIGHT                                = 1;
	
	public static final int USB_OPERATOR                                    = 2;
	
	/*********************************CAMS********************************************/
	
	public static final String[] CAMS                                       = {
			                                                                "cam0",
			                                                                "cam1"
	                                                                        };
}