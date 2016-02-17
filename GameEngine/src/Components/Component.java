package Components;
import java.io.Serializable;

import GameObjects.GameObject;


public abstract class Component implements Serializable {
	
	private static final long serialVersionUID = -5281287236014264559L;
	// Object that this component belongs too
	GameObject o;
	
	// Constructor
	public Component(GameObject o) {
		this.o = o;
	}
	
	// Update the references to other components to most up to date ones.
	public abstract void updateReferences();
	
	// Abstract update method that will update this component.
	public abstract void update();
}
