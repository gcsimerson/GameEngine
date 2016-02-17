package MainGameLoop;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import Components.Color;
import Components.Movable;
import Components.RigidBody;
import Components.Transform;
import EventManagment.AdjustPlaybackSpeed;
import EventManagment.DeathEvent;
import EventManagment.Event;
import EventManagment.EventManager;
import EventManagment.SpawnEvent;
import GameObjects.Cloud;
import GameObjects.GameObject;
import GameObjects.Player;
import GameObjects.Replay;
import Scripting.ScriptManager;

// Creates a server that asks for a user name and asks for a message from the user then repeats the message back until the user says exit
// Code was based on a previous assignment that I did in CSC 246
public class Server {
	
	private static Semaphore lock = new Semaphore(1);
	
	/** Port number used by the server */
	private static final int PORT_NUMBER = 26136;
	private static final int PLAYER_SIZE = 50;
	
	// Initialize Linked List of game objects.
	private LinkedList<GameObject> gameObjects = new LinkedList<GameObject>();
	private LinkedList<UserRec> players = new LinkedList<UserRec>();
	private LinkedList<GameObject> playerObjects = new LinkedList<GameObject>();
	
	private LinkedList<GameObject> tempGameObjects = new LinkedList<GameObject>();
	private LinkedList<GameObject> tempPlayerObjects = new LinkedList<GameObject>();
	
	ArrayList<Transform> transforms;
	
	private GameObject spawn;
	private GameObject deathZone;
	private int totalObjects;
	
	public static EventManager eventManager;
	
	private Replay replay;
	private boolean playback;
	
	private boolean threadWaiting = false;
	
	public static Timeline globalTime;
	
	/** Record for an individual user. */
	public static class UserRec{
		// Input/output streams for this thread.
		ObjectOutputStream userOutput;
		ObjectInputStream userInput;
	}
	
	class MainGameLoopThread extends Thread {
		
		public void run() {
			
			while (true) {
				
		    	if(threadWaiting) {
			    	try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		    	}
				
				// Make sure no one else can access the LinkedList of players
				lock.acquireUninterruptibly();
				
				// If moving platform goes off screen loop it back to the edge of the screen.
				Transform transform = (Transform) gameObjects.get(5).getComponent(0);
				if(transform.x > Client.WIDTH) {
					transform.x = 0 - transform.width;
				}
				
				// If moving platform goes off screen loop it back to the edge of the screen.
				transform = (Transform) gameObjects.get(8).getComponent(0);
				if(transform.x > Client.WIDTH) {
					transform.x = 0 - transform.width;
				}
				
				ScriptManager.loadScript("scripts/cloud_script.js");

				ScriptManager.bindArgument("cloud", gameObjects.get(8));
				ScriptManager.executeScript();
				
				// Loop over list of objects and update the components
				// If there is a RigidBody, give it a fresh list of gameObjects
				for(int i = 0 ; i < gameObjects.size() ; i++) {
					
					RigidBody rigidBody;
					
					// Index of RigidBody component if it exists
					int indexOfRB = gameObjects.get(i).findComponent(new RigidBody(gameObjects.get(i)));
					
					// If it does, store the component in RigidBody
					// If it doesn't just create a default RigidBody
					if(indexOfRB >= 0) {
						rigidBody = (RigidBody) gameObjects.get(i).getComponent(indexOfRB);
					}
					else {
						rigidBody = new RigidBody(gameObjects.get(i));
					}
					
					rigidBody.updateList(gameObjects);
					
					gameObjects.get(i).updateComponents();
					
				}
				
				// Loop over list of objects and update the components
				// If there is a RigidBody, give it a fresh list of gameObjects
				for(int i = 0 ; i < playerObjects.size() ; i++) {
					
					RigidBody rigidBody;
					
					// Index of RigidBody component if it exists
					int indexOfRB = playerObjects.get(i).findComponent(new RigidBody(playerObjects.get(i)));
					
					// If it does, store the component in RigidBody
					// If it doesn't just create a default RigidBody
					if(indexOfRB >= 0) {
						rigidBody = (RigidBody) playerObjects.get(i).getComponent(indexOfRB);
					}
					else {
						rigidBody = new RigidBody(playerObjects.get(i));
					}
					
					rigidBody.updateList(gameObjects);
					
					playerObjects.get(i).updateComponents();
					
					if(playerObjects.get(i).intersects(deathZone)) {
						
						Transform spawnTransform;
						
						// Index of Transform component if it exists
						int indexOfTransformSpawn = spawn.findComponent(new Transform(spawn));
						
						// If it does, store the component in transform
						// If it doesn't just create a default transform
						if(indexOfTransformSpawn >= 0) {
							spawnTransform = (Transform) spawn.getComponent(indexOfTransformSpawn);
						}
						else {
							spawnTransform = new Transform(spawn);
						}
						
						eventManager.queueEvent( new DeathEvent(Server.globalTime.getTotalTimeElapsed(), 0, playerObjects.get(i).getObjectID(), spawnTransform.x, spawnTransform.y));
						eventManager.queueEvent(new SpawnEvent(Server.globalTime.getTotalTimeElapsed(), 1, playerObjects.get(i).getObjectID(), spawnTransform.x,spawnTransform.y));
					}
					
				}
				
				for(int i = 0 ; i < players.size() ; i++) {
					// Send Object to Client
					try {
						
						players.get(i).userOutput.reset();
						players.get(i).userOutput.writeObject(gameObjects);
						players.get(i).userOutput.flush();
						
						players.get(i).userOutput.reset();
						players.get(i).userOutput.writeObject(playerObjects);
						players.get(i).userOutput.flush();			
						
						players.get(i).userOutput.reset();
						players.get(i).userOutput.writeObject(globalTime.getTotalTimeElapsed());
						players.get(i).userOutput.flush();
						
						Event e = (Event) players.get(i).userInput.readObject();
						if(e != null) {
							e.setTimeStamp(globalTime.getTotalTimeElapsed());
							eventManager.queueEvent(e);
						}

					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
				eventManager.dispatchEvents(globalTime.getTotalTimeElapsed());
				
				globalTime.updateTime();
				
				// Allow other players to access linked lists
		    	lock.release();
			}
		}
	}
	
	/** Handle interaction with a single client. */
	class HandleClient extends Thread {

		Socket mySock;

		public HandleClient(Socket sock) {
			mySock = sock;
		}

		public void run() {
			try {
				
				// Create a new user record
				UserRec rec = new UserRec();
				
				threadWaiting = true;
				// Make sure no one else can access the LinkedList of players
				lock.acquireUninterruptibly();
				System.out.println("Handling new client");
				
				// Get formatted input/output streams for this thread.
				rec.userOutput = new ObjectOutputStream(mySock.getOutputStream());
				rec.userInput = new ObjectInputStream(mySock.getInputStream());
				
				// Create a new player to store in the playerlist
				Player player = new Player(totalObjects);
				
				rec.userOutput.writeInt(totalObjects);
				
				// Create a temporary new transform to store spawn point location in.
				// We know it is at index 0 because we created the spawn point object first.
				Transform spawnTransform = (Transform) spawn.getComponent(0);
				player.addComponent(new Color(player, 203, 30, 49));
				player.addComponent(new Movable(player, 0, 0));
				player.addComponent(new Transform(player, spawnTransform.x, spawnTransform.y, PLAYER_SIZE, PLAYER_SIZE));
				player.addComponent(new RigidBody(player, false, true, gameObjects));
				
				eventManager.registerHandler(player, Event.EVENT_TYPE_USER_INPUT);
				eventManager.registerHandler(player, Event.EVENT_TYPE_COLLISION);
				eventManager.registerHandler(player, Event.EVENT_TYPE_CHARACTER_DEATH);
				eventManager.registerHandler(player, Event.EVENT_TYPE_CHARACTER_SPAWN);
				
				// Store in Linked list of players
				players.add(rec);
				playerObjects.add(player);
				totalObjects++;
				
		    	// Verify that the user connected
				System.out.println("Player has logged on at " + globalTime.getTotalTimeElapsed() * .001 + " seconds");
				
				// Allow other players to access linked lists
				threadWaiting = false;
		    	lock.release();
				System.out.println("Handled new client");
				
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Essentially, the main method for our server. */
	private void run() {
		
		ServerSocket serverSocket = null;
		
		eventManager = new EventManager();
		
		globalTime = new Timeline();
		
		playback = false;
		
		// Register server to receive events
		eventManager.registerHandler(this, Event.EVENT_TYPE_START_RECORD);
		eventManager.registerHandler(this, Event.EVENT_TYPE_STOP_RECORD);
		eventManager.registerHandler(this, Event.EVENT_TYPE_FINISH_PLAYBACK);
		eventManager.registerHandler(this, Event.EVENT_TYPE_ADJUST_SPEED);
		
		// Replay object 0
		replay = new Replay(totalObjects);
		eventManager.registerHandler(replay, Event.EVENT_TYPE_USER_INPUT);
		eventManager.registerHandler(replay, Event.EVENT_TYPE_START_RECORD);
		eventManager.registerHandler(replay, Event.EVENT_TYPE_STOP_RECORD);
		totalObjects++;
		
		// Spawn Point 1
		gameObjects.add(spawn = new GameObject(totalObjects));
		spawn.addComponent(new Transform(spawn, 75, 700, 0, 0));
		totalObjects++;
		
		// Death Zone 2
		gameObjects.add(deathZone = new GameObject(totalObjects));
		deathZone.addComponent(new Transform(deathZone, -1200, 1300, 3600, 100));
		totalObjects++;
		
		// Platform 3
		gameObjects.add(new GameObject(totalObjects));
		gameObjects.getLast().addComponent(new Transform(gameObjects.getLast(), 500, 700, 200, 25));
		gameObjects.getLast().addComponent(new Color(gameObjects.getLast(), 231, 81, 119));
		totalObjects++;
		
		// Platform 4
		gameObjects.add(new GameObject(totalObjects));
		gameObjects.getLast().addComponent(new Transform(gameObjects.getLast(), 200, 500, 200, 25));
		gameObjects.getLast().addComponent(new Color(gameObjects.getLast(), 146, 69, 101));
		totalObjects++;
		
		// Platform 5
		gameObjects.add(new GameObject(totalObjects));
		gameObjects.getLast().addComponent(new Transform(gameObjects.getLast(), 800, 500, 200, 25));
		gameObjects.getLast().addComponent(new Color(gameObjects.getLast(), 146, 69, 101));
		totalObjects++;
		
		// Moving Platform 6
		gameObjects.add(new GameObject(totalObjects));
		gameObjects.getLast().addComponent(new Transform(gameObjects.getLast(), 500, 300, 200, 25));
		gameObjects.getLast().addComponent(new Movable(gameObjects.getLast(), .25f, 0));
		gameObjects.getLast().addComponent(new Color(gameObjects.getLast(), 231, 81, 119));
		totalObjects++;
		
		// Ground (Platform) 7
		gameObjects.add(new GameObject(totalObjects));
		gameObjects.getLast().addComponent(new Transform(gameObjects.getLast(), 0, 790, 400, 500));
		gameObjects.getLast().addComponent(new Color(gameObjects.getLast(), 153, 150, 71));
		totalObjects++;
		
		// Ground (Platform) 8
		gameObjects.add(new GameObject(totalObjects));
		gameObjects.getLast().addComponent(new Transform(gameObjects.getLast(), 800, 790, 400, 500));
		gameObjects.getLast().addComponent(new Color(gameObjects.getLast(), 153, 150, 71));
		totalObjects++;
		
		// Cloud Object 9
		// Can script behavior
		gameObjects.add(new Cloud(totalObjects));
		totalObjects++;
		eventManager.registerHandler(gameObjects.getLast(), Event.EVENT_TYPE_CHARACTER_DEATH);
		
		MainGameLoopThread mainGameLoop = new MainGameLoopThread();
		mainGameLoop.start();

		// One-time setup.
		try {
			// Open a socket for listening.
			serverSocket = new ServerSocket(PORT_NUMBER);
			System.out.println("Server created");
		} catch (Exception e) {
			System.err.println("Can't initialize server: " + e);
			System.exit(-1);
		}		// Keep trying to accept new connections and serve them.
		while (true) {
			try {
				// Try to get a new client connection.
				Socket sock = serverSocket.accept();
				System.out.println("Accepted");

				// Interact with this client.
				HandleClient worker = new HandleClient(sock);
				worker.start();
				
			} catch (IOException e) {
				System.err.println("Failure accepting client " + e);
			}
		}
	}
	
	public void handleEvents(Event e){
		switch (e.getEventType()) {
		case Event.EVENT_TYPE_START_RECORD: {
			System.out.println("Recording");
			
			transforms = new ArrayList<Transform>();
			for(int i = 0 ; i < gameObjects.size() ; i++) {
				transforms.add(new Transform(gameObjects.get(i), gameObjects.get(i).x, gameObjects.get(i).y, gameObjects.get(i).width, gameObjects.get(i).height));
			}
			for(int i = 0 ; i < playerObjects.size() ; i++) {
				transforms.add(new Transform(playerObjects.get(i), playerObjects.get(i).x, playerObjects.get(i).y, playerObjects.get(i).width, playerObjects.get(i).height));
			}
			
			break;
		}
		case Event.EVENT_TYPE_STOP_RECORD: {
			System.out.println("Playing");
			
			for(int i = 0 ; i < gameObjects.size() ; i++) {
				int index = gameObjects.get(i).findComponent(new Transform(gameObjects.get(i)));
				gameObjects.get(i).getComponentList().set(index, transforms.get(0));
				transforms.remove(0);
			}
			for(int i = 0 ; i < playerObjects.size() ; i++) {
				int index = playerObjects.get(i).findComponent(new Transform(playerObjects.get(i)));
				playerObjects.get(i).getComponentList().set(index, transforms.get(0));
				transforms.remove(0);
			}
			
			playback = true;
			break;
		}
		case Event.EVENT_TYPE_FINISH_PLAYBACK: {
			System.out.println("Finished Playback");
			playback = false;
			globalTime.adjustTicSize(1);
			break;
		}
		case Event.EVENT_TYPE_ADJUST_SPEED: {
			if(playback) {
				AdjustPlaybackSpeed newEvent = (AdjustPlaybackSpeed) e;
				globalTime.adjustTicSize(newEvent.getSpeed());
			}
			break;
		}
		default:
			break;
		}
	}

	public static void main(String[] args) {
		// Make a server object, so we can have non-static fields.
		Server server = new Server();
		server.run();
	}
}
