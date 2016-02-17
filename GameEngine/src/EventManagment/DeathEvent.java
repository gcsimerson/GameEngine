package EventManagment;

public class DeathEvent extends Event {

	private static final long serialVersionUID = 8010526522219703560L;
	private int newX;
	private int newY;
	
	public DeathEvent(long timeStamp, int priority, int objectID, int newX, int newY) {
		super(timeStamp, priority, objectID);
		eventType = EVENT_TYPE_CHARACTER_DEATH;
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
