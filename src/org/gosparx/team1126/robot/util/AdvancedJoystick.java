package org.gosparx.team1126.robot.util;

import java.util.ArrayList;

import org.gosparx.team1126.robot.subsystem.GenericSubsystem;

import edu.wpi.first.wpilibj.Joystick;

/**
 * A class for more advanced data collection from the joysticks.
 * @author Alex Mechler {amechler1998@gmail.com}
 */
public class AdvancedJoystick extends GenericSubsystem{

	/**
	 * The joystick we are gathering data from
	 */
	private Joystick joy;

	/**
	 * The previous value of the joystick buttons
	 */
	private boolean[] prevValues;

	/**
	 * The list of listeners that are listening for the rising/falling edges
	 */
	private ArrayList<JoystickListener> listeners;

	/**
	 * the port the joystick is in
	 */
	private int port;

	/**
	 * The list of button ids to listen for
	 */
	private ArrayList<Integer> buttons;

	/**
	 * The list of multibuttons to track
	 */
	private ArrayList<Multibutton> multibuttons;

	/**
	 * Any axis value under this will be treated as 0
	 */
	private static double DEADBAND = .04;

	/**
	 * Creates a new advanced joystick - INSTABLE IF JOYSTICKS ARE NOT ALWAYS PLUGGED IN
	 * @param name - The name of the thread
	 * @param joyPort - the port the joystick is in
	 */
	public AdvancedJoystick(String name, int joyPort) {
		super(name, Thread.NORM_PRIORITY);
		joy = new Joystick(joyPort);
		port = joyPort;
		buttons = new ArrayList<Integer>();
		multibuttons = new ArrayList<Multibutton>();
		listeners = new ArrayList<JoystickListener>();
		prevValues = new boolean[joy.getButtonCount() + 1];
	}
	
	/**
	 * Creates a new advanced joystick
	 * @param name - The name of the thread
	 * @param joyPort - the port the joystick is in
	 * @param numButtons - The number of buttons on the joystick
	 */
	public AdvancedJoystick(String name, int joyPort, int numButtons){
		this(name, joyPort);
		prevValues = new boolean[numButtons+1];
	}
	
	/**
	 * Creates a new advanced joystick
	 * @param name - The name of the thread
	 * @param joyPort - the port the joystick is in
	 * @param numButtons - The number of buttons on the joystick
	 * @param deadBand - The +/- deadband
	 */
	public AdvancedJoystick(String name, int joyPort, int numButtons, double deadBand) {
		this(name, joyPort);
		prevValues = new boolean[numButtons+1];
		DEADBAND = deadBand;
	}

	/**
	 * Initializes things.
	 */
	@Override
	protected boolean init() {
		return true;
	}

	/**
	 * Uses livewindow
	 */
	@Override
	protected void liveWindow() {

	}

	/**
	 * loops, updates and compares values
	 */
	@Override
	protected boolean execute() {
		for(Multibutton m: multibuttons){
			m.update();
		}
		for(Integer i: buttons){
			hasChanged(i);
			prevValues[i] = joy.getRawButton(i);
		}

		return false;
	}

	/**
	 * How long to sleep in ms
	 */
	@Override
	protected long sleepTime() {
		return 30;
	}

	/**
	 * writes log info
	 */
	@Override
	protected void writeLog() {

	}

	private void notifyAllListeners(ButtonEvent e){
		for(JoystickListener j: listeners){
			if(j != null){
				j.actionPerformed(e);
			}
		}
	}

	/**
	 * checks to see if the button has had a rising/falling edge and will notify the listener if it is
	 * @param button - the id of the button
	 */
	private void hasChanged(int button){
		for(Multibutton m: multibuttons){
			if((m.getButton1() == button || m.getButton2() == button) && m.getUpdated()){
				return;
			}
		}
		if(joy.getRawButton(button) != prevValues[button]){
			notifyAllListeners(new ButtonEvent(button, joy.getRawButton(button)));
		}
	}

	/**
	 * Adds the specified button to the list of buttons to listen too
	 * @param id - The button to listen too
	 */
	public void addButton(int id){
		buttons.add(id);
	}

	/**
	 * Adds the specified button to the list of buttons to listen too
	 * @param id - The button to listen too
	 */
	public void addMultibutton(int b1, int b2){
		multibuttons.add(new Multibutton(b1, b2));
	}

	/**
	 * Get the axis value accounting for the deadband
	 * @param axis - the int of the axis to get
	 * @return the modified axis value
	 */
	public double getAxis(int axis){
		return (Math.abs(joy.getRawAxis(axis)) > DEADBAND) ? joy.getRawAxis(axis) : 0;
	}
	
	public double getPOV(int axis){
		return joy.getPOV(axis);
	}

	/**
	 * Adds a new ActionListener
	 * @param listener - the listener
	 */
	public void addActionListener(JoystickListener listener){
		listeners.add(listener);
	}

	/**
	 * A class that represents all of the button events that could happen
	 * @author Alex Mechler {amechler1998@gmail.com}
	 */
	public class ButtonEvent{

		/**
		 * True if this is a rising edge, false if its a falling edge
		 */
		private boolean risingEdge;

		/**
		 * The button id that triggered the event
		 */
		private int buttonID;

		/**
		 * Creates a new ButtonEvent
		 * @param port - the port of the joystick
		 * @param buttonID - the button that created the event
		 * @param risingEdge - if this is a rising edge or falling edge
		 */
		public ButtonEvent(int buttonID, boolean risingEdge){
			this.buttonID = buttonID;
			this.risingEdge = risingEdge;
		}

		/**
		 * @return true - if rising edge
		 * 		   false - if falling edge
		 */
		public boolean isRising(){
			return risingEdge;
		}

		/**
		 * @return the button id that the event was triggered from 
		 */
		public int getID(){
			return buttonID;
		}

		/**
		 * @return The port of the joystick that triggered the event
		 */
		public int getPort(){
			return port;
		}
	}

	/**
	 * A class to represent a combo of buttons/multibutton
	 * @author Alex Mechler {amechler1998@gmail.com}
	 */
	public class Multibutton{

		/**
		 * The ID of the first button
		 */
		private int button1;

		/**
		 * The ID of the second button
		 */
		private int button2;

		/**
		 * The previous value of the multibutton
		 */
		private boolean last;

		/**
		 * Can we check the individual buttons
		 */
		private boolean updated;

		/**
		 * Creates a new Multibutton
		 * @param b1 The first button to check
		 * @param b2 The second button to check
		 */
		public Multibutton(int b1, int b2){
			button1 = b1;
			button2 = b2;
		}

		/**
		 * Checks the multibutton combo and if it has changed creates a new event for it
		 */
		public void update(){
			updated = false;
			if(last != (joy.getRawButton(button1) && joy.getRawButton(button2))){
				last = (joy.getRawButton(button1) && joy.getRawButton(button2));
				notifyAllListeners(new MultibuttonEvent(button1, button2, last));
				updated = true;
			}
		}

		/**
		 * @return if the button is pressed
		 */
		public boolean getLast(){
			return last;
		}

		/**
		 * @return if we can check the individuals
		 */
		public boolean getUpdated(){
			return updated;
		}

		/**
		 * @return The first button in the combo
		 */
		public int getButton1(){
			return button1;
		}

		/**
		 * @return The second button in the combo
		 */
		public int getButton2(){
			return button2;
		}
	}

	/**
	 * A class to represent the multibutton events
	 * @author Alex Mechler {amechler1998@gmail.com}
	 */
	public class MultibuttonEvent extends ButtonEvent{

		/**
		 * The ID of the second button in the combo
		 */
		private int button2;

		/**
		 * Creates a new Multibutton Event
		 * @param button1 The first button in the combo
		 * @param button2 The second button in the combo
		 * @param risingEdge If this is a rising or falling edge
		 */
		public MultibuttonEvent(int button1, int button2, boolean risingEdge){
			super(button1, risingEdge);
			this.button2 = button2;
		}

		/**
		 * @return the second button
		 */
		public int getButton2(){
			return button2;
		}
	}

	/**
	 * An interface that allows for listening to the advanced joystick.
	 * @author Alex Mechler {amechler1998@gmail.com}
	 */
	public interface JoystickListener{
		public void actionPerformed(ButtonEvent e);
	}
}
