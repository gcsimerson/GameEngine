package Components;

import java.util.LinkedList;

import EventManagment.CollisionEvent;
import GameObjects.GameObject;
import MainGameLoop.Server;

public class RigidBody extends Component {
	
	private static final long serialVersionUID = 926422926506756359L;
	
	LinkedList<GameObject> gameObjects;

	// Location of object
	Movable movable;
	
	Transform transform;
	
	// Is object not affected by physics?
	boolean isKinematic;
	
	// Affect by gravity?
	boolean useGravity;
	
	private static final float GRAVITY_CONST = 0.1f;
	private static final float TERMINAL_VELOCITY = 5.0f;

	public RigidBody(GameObject o) {
		super(o);
		updateReferences();
	}
	
	public RigidBody(GameObject o, boolean isKinematic, boolean useGravity, LinkedList<GameObject> gameObjects) {
		super(o);
		
		this.isKinematic = isKinematic;
		this.useGravity = useGravity;
		this.gameObjects = gameObjects;
		
		updateReferences();
	}

	@Override
	public void updateReferences() {
		
		// Index of Transform component if it exists
		int indexOfMovable = o.findComponent(new Movable(o));
		
		// If it does, store the component in transform
		// If it doesn't just create a default transform
		if(indexOfMovable >= 0) {
			movable = (Movable) o.getComponent(indexOfMovable);
		}
		else {
			movable = new Movable(o);
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

	}
	
	public void updateList(LinkedList<GameObject> gameObjects) {
		this.gameObjects = gameObjects;
	}
	
	public boolean willCollideX(GameObject o) {
		if(this.o.intersects(o)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean willCollideY(GameObject o) {
		if(this.o.intersects(o)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void update() {
		boolean collided = false;
		
		Transform newTransform;
		
		if(!isKinematic) {
			if(useGravity) {
				for(int i = 0 ; i < gameObjects.size() ; i++) {
					if(willCollideY(gameObjects.get(i))) {
						collided = true;
						
						// Index of Transform component if it exists
						int indexOfTransform = gameObjects.get(i).findComponent(new Transform(o));
						
						// If it does, store the component in transform
						// If it doesn't just create a default transform
						if(indexOfTransform >= 0) {
							newTransform = (Transform) gameObjects.get(i).getComponent(indexOfTransform);
						}
						else {
							newTransform = new Transform(gameObjects.get(i));
						}
						newTransform = (Transform) gameObjects.get(i).getComponent(indexOfTransform);
						
						Server.eventManager.queueEvent(new CollisionEvent(Server.globalTime.getTotalTimeElapsed(), 2, o.getObjectID(), newTransform.y - transform.height));
						//transform.y = newTransform.y - transform.height;
					}
				}
				if(!collided) {
					if(movable.vy < TERMINAL_VELOCITY) {
						movable.vy += GRAVITY_CONST;
					}
				}
			}
		}

	}

}
