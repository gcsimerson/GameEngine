package EventManagment;

public class StartRecordEvent extends Event {

	private static final long serialVersionUID = -2960039672249178793L;

	public StartRecordEvent(long timeStamp, int priority, int objectID) {
		super(timeStamp, priority, objectID);
		eventType = EVENT_TYPE_START_RECORD;
	}

}
