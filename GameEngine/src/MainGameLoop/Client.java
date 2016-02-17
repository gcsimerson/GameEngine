package MainGameLoop;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import Components.*;
import EventManagment.AdjustPlaybackSpeed;
import EventManagment.InputEvent;
import EventManagment.StartRecordEvent;
import EventManagment.StopRecordEvent;
import GameObjects.*;
import processing.core.*;

public class Client extends PApplet implements Serializable {

	private static final long serialVersionUID = 495595718184883043L;
	
	/** Port number and host used by the server */
	private static final int PORT_NUMBER = 26136;
	private static final String localhost = "127.0.0.1";
	private static final float SPEED = 1f;
	
	private static final int DEFAULT = -1;
	private static final int INPUT = 0;
	private static final int START_REC = 1;
	private static final int STOP_REC = 2;
	private static final int ADJUST_SPEED = 3;
	
	// Dimensions of the screen
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	
	// Default background color
	public static final int RED = 98;
	public static final int GREEN = 192;
	public static final int BLUE = 220;
	
	// Priority of event
	public static final int PRIORITY = 3;
	
	// Jump value;
	private static final float VY = -2f;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private long totalTimeElapsed;
	private int playerID;
	private boolean canJump;
	private int state;
	
	private double speed;
	
	private float vx;
	
	private LinkedList<GameObject> gameObjects = new LinkedList<GameObject>();
	private LinkedList<GameObject> playerObjects = new LinkedList<GameObject>();

	private boolean released = true;

	// Setup before main game loop runs
	public void setup() {
		
		// Create window
		size(WIDTH, HEIGHT);
		
		canJump = false;
		
		state = -1;
		
		try {
			System.out.println("Connecting");
			
			// Try to create a socket connection to the server.
			Socket sock = new Socket(localhost, PORT_NUMBER);
			
			output = new ObjectOutputStream(sock.getOutputStream());
			input = new ObjectInputStream(sock.getInputStream());
			
			playerID = (int) input.readInt();
			
			System.out.println("Connected");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void draw() {
		
		// Set background color of window
		background(RED, GREEN ,BLUE);
		
		try {
			gameObjects = (LinkedList<GameObject>) input.readObject();
			playerObjects = (LinkedList<GameObject>) input.readObject();
			totalTimeElapsed = (long) input.readObject();

		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		for(int i = 0 ; i < gameObjects.size() ; i++) {
			render(gameObjects.get(i));
		}
		for(int i = 0 ; i < playerObjects.size() ; i++) {
			render(playerObjects.get(i));
		}
		
		try {
			
			output.reset();
			
			switch(state) {
				case INPUT: {
					output.writeObject(new InputEvent(totalTimeElapsed, PRIORITY, vx, canJump, VY, playerID));
					state = DEFAULT;
					break;
				}
				case START_REC: {
					output.writeObject(new StartRecordEvent(totalTimeElapsed, PRIORITY, playerID));
					state = DEFAULT;
					break;
				}
				case STOP_REC: {
					output.writeObject(new StopRecordEvent(totalTimeElapsed, PRIORITY, playerID));
					state = DEFAULT;
					break;
				} case ADJUST_SPEED: {
					output.writeObject(new AdjustPlaybackSpeed(totalTimeElapsed, 4, playerID, speed));
					state = DEFAULT;
				}
				default:
					output.writeObject(null);
					break;
			}

			output.flush();
			canJump = false;
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	public void render(GameObject o) {
		// Color of object
		Color color;
		
		// Location of object
		Transform transform;
		
		// Index of color component if it exists
		int indexOfColor = o.findComponent(new Color(o));
		
		// If it does, store the component in color
		// If it doesn't just create a default Color
		if(indexOfColor >= 0) {
			color = (Color) o.getComponent(indexOfColor);
		}
		else {
			color = new Color(o);
		}
		
		// Index of Transform component if it exists
		int indexOfTransform = o.findComponent(new Transform(o));
		
		// If it does, store the component in transform
		// If it doesn't just create a default transform
		if(indexOfTransform >= 0) {
			transform = (Transform) o.getComponent(indexOfTransform);
		}
		else {
			transform = new Transform(o);
		}
		
		fill(color.red, color.green, color.blue);
		rect(transform.x, transform.y, transform.width, transform.height);
	}

	public void keyPressed() {
	  
		if(keyCode == LEFT && released ) {
			vx = -1 * SPEED;
			state = INPUT;
			released = false;
		} else if(keyCode == RIGHT && released) {
			vx = SPEED;
			state = INPUT;
			released = false;
		} else if(key == ' ') {
			canJump = true;
			state = INPUT;
		} else if(key == 'r') {
			state = START_REC;
		} else if (key == 'p') {
			state = STOP_REC;
		} else if (key == '1') {
			speed = .5;
			state = ADJUST_SPEED;
		} else if (key == '2') {
			speed = 1;
			state = ADJUST_SPEED;
		} else if (key == '3') {
			speed = 2;
			state = ADJUST_SPEED;
		}
		

	}

	public void keyReleased() {
		
		if(keyCode == RIGHT || keyCode == LEFT ) {
			vx = 0;
			state = INPUT;
			released = true;
		}

  }
  
}