package GameObjects;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.LinkedList;
import Components.*;
import EventManagment.Event;
import EventManagment.EventManager;
import MainGameLoop.Server;

/**Game object that will always be movable, renderable, and collidable*/
public class GameObject extends Rectangle implements Serializable {
	
	private static final long serialVersionUID = -6255150047310690724L;
	// List of components
	protected LinkedList<Component> components;
	
	private int objectID;
	
	// GameObject constructor.
	public GameObject(int objectID){
		
		this.objectID = objectID;
		
		// Initialize LinkedList
		components = new LinkedList<Component>();
	}
	
	// Add a component to the list of components
	public void addComponent(Component c) {
		components.add(c);
	}
	
	// Finds if the type of component exists in list of components and returns index if it exists
	public int findComponent (Component c) {
		for(int i = 0 ; i < components.size() ; i++) {
			if(components.get(i).getClass().equals(c.getClass())) {
				return i;
			}
		}
		return -1;
	}
	
	public EventManager getEventManager() {
		return Server.eventManager;
	}
	
	public Component getComponent(int index) {
		return components.get(index);
	}
	
	// Update all components
	public void updateComponents(){
		for(int i = 0 ; i < components.size() ; i++) {
			components.get(i).updateReferences();
			components.get(i).update();
		}
	}
	
	public int getObjectID() {
		return objectID;
	}
	
	public void register(int eventType) {
		getEventManager().registerHandler(this, eventType);
	}
	
	public void handleEvent(Event e) {
		switch (e.getEventType()) {
			default:
				break;
		}
	}
	
	public LinkedList<Component> getComponentList() {
		return components;
	}
}
