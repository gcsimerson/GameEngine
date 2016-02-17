package GameObjects;

import Components.Movable;
import Components.Transform;
import EventManagment.CollisionEvent;
import EventManagment.DeathEvent;
import EventManagment.Event;
import EventManagment.InputEvent;
import EventManagment.SpawnEvent;

public class Player extends GameObject {
	
	private static final long serialVersionUID = -5488835847211396177L;

	public Player(int objectID) {
		super(objectID);
	}
	
	@Override
	public void handleEvent(Event e) {
		
		// Transform of this object
		Transform transform;
		
		// Movable of this object
		Movable movable;
		
		// Index of Transform component if it exists
		int indexOfTransform = findComponent(new Transform(this));
		
		// If it does, store the component in transform
		// If it doesn't just create a default transform
		if(indexOfTransform >= 0) {
			transform = (Transform) getComponent(indexOfTransform);
		}
		else {
			transform = new Transform(this);
		}	
		
		// Index of Transform component if it exists
		int indexOfMovable = this.findComponent(new Movable(this));
		
		// If it does, store the component in transform
		// If it doesn't just create a default transform
		if(indexOfMovable >= 0) {
			movable = (Movable) this.getComponent(indexOfMovable);
		}
		else {
			movable = new Movable(this);
		}
		
		switch (e.getEventType()) {
		case Event.EVENT_TYPE_COLLISION:
			CollisionEvent collisionEvent = (CollisionEvent) e;
			
			if(collisionEvent.getObjectID() != getObjectID()) {
				break;
			}
			
			transform.y = collisionEvent.getLocation();
			movable.vy = 0f;
			break;
		case Event.EVENT_TYPE_USER_INPUT:
			
			InputEvent inputEvent = (InputEvent) e;
			
			if(inputEvent.getPlayer() != getObjectID()) {
				break;
			}
			
			movable.vx = inputEvent.getVX();
			if(inputEvent.getJump()) {
				movable.vy += inputEvent.getVY();
			}
			break;
		case Event.EVENT_TYPE_CHARACTER_DEATH:
			
			DeathEvent deathEvent = (DeathEvent) e;
			
			if(deathEvent.getObjectID() != getObjectID()) {
				break;
			}
			
			System.out.println("You died.");
			
			break;
		case Event.EVENT_TYPE_CHARACTER_SPAWN:
			
			SpawnEvent spawnEvent = (SpawnEvent) e;
			
			if(spawnEvent.getObjectID() != getObjectID()) {
				break;
			}
			
			System.out.println("Respawning...");
			
			transform.x = spawnEvent.getNewX();
			transform.y = spawnEvent.getNewY();
		default:
			break;
		}
	}

}
