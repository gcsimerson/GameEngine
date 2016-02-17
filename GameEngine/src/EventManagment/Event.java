package EventManagment;

import java.io.Serializable;

public abstract class Event implements Serializable{
	
	private static final long serialVersionUID = 1708296706495047351L;
	
	public static final int EVENT_TYPE_COLLISION = 0;
	public static final int EVENT_TYPE_CHARACTER_DEATH = 1;
	public static final int EVENT_TYPE_CHARACTER_SPAWN = 2;
	public static final int EVENT_TYPE_USER_INPUT = 3;
	public static final int EVENT_TYPE_START_RECORD = 4;
	public static final int EVENT_TYPE_STOP_RECORD = 5;
	public static final int EVENT_TYPE_FINISH_PLAYBACK = 6;
	public static final int EVENT_TYPE_ADJUST_SPEED = 7;
	
	
	protected int eventType;
	protected long timeStamp;
	protected int priority;
	protected int objectID;

	public Event(long timeStamp, int priority, int objectID) {
		this.timeStamp = timeStamp;
		this.priority = priority;
		this.objectID = objectID;
	}
	
	public int getEventType() {
		return eventType;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	

}
