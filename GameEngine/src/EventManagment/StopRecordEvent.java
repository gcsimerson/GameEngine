package EventManagment;

public class StopRecordEvent extends Event {

	private static final long serialVersionUID = 4246888234104234626L;

	public StopRecordEvent(long timeStamp, int priority, int objectID) {
		super(timeStamp, priority, objectID);
		eventType = EVENT_TYPE_STOP_RECORD;
	}

}
