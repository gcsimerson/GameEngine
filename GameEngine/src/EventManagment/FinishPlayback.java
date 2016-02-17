package EventManagment;

public class FinishPlayback extends Event {

	private static final long serialVersionUID = -8923439741379821867L;

	public FinishPlayback(long timeStamp, int priority, int objectID) {
		super(timeStamp, priority, objectID);
		eventType = EVENT_TYPE_FINISH_PLAYBACK;
	}

}
