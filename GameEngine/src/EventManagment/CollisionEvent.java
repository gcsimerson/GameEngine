package EventManagment;

public class CollisionEvent extends Event {

	private static final long serialVersionUID = 2328234029306311224L;
	
	private int newLocation;

	public CollisionEvent(long timeStamp, int priority, int objectID, int newLocation) {
		super(timeStamp, priority, objectID);
		eventType = EVENT_TYPE_COLLISION;
		this.newLocation =  newLocation;
	}
	
	public int getLocation() {
		return newLocation;
	}
	
	public int getObjectID() {
		return objectID;
	}

}
