package EventManagment;

public class SpawnEvent extends Event {

	private static final long serialVersionUID = -8497052124298143251L;
	
	private int newX;
	private int newY;
	
	public SpawnEvent(long timeStamp, int priority, int objectID, int newX, int newY) {
		super(timeStamp, priority, objectID);
		eventType = EVENT_TYPE_CHARACTER_SPAWN;
		this.newX = newX;
		this.newY = newY;
	}
	
	public int getNewX() {
		return newX;
	}
	
	public int getNewY() {
		return newY;
	}
	
	public int getObjectID() {
		return objectID;
	}

}
