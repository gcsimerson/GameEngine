package EventManagment;

public class AdjustPlaybackSpeed extends Event {

	private static final long serialVersionUID = -8714463043181475913L;
	
	private double speed;
	
	public AdjustPlaybackSpeed(long timeStamp, int priority, int objectID, double speed) {
		super(timeStamp, priority, objectID);
		this.speed = speed;
		eventType = EVENT_TYPE_ADJUST_SPEED;
	}
	
	public double getSpeed() {
		return speed;
	}

}
