package GameObjects;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import EventManagment.Event;
import EventManagment.FinishPlayback;
import MainGameLoop.Server;

public class Replay extends GameObject {
	
	private static final long serialVersionUID = -3182174055957674020L;
	
	private boolean canRecord;
	private long startReplay;
	private long endReplay;
	
	Comparator<Event> comparator;
	LinkedList<Event> queue;

	public Replay(int objectID) {
		super(objectID);
        queue = new LinkedList<Event>();
        canRecord = false;
	}
	
	public void play() {
		
		Event e = null;
		
		while(queue.size() > 0) {
			e = queue.remove();
			e.setTimeStamp(endReplay + e.getTimeStamp() - startReplay);
			getEventManager().queueEvent(e);
		}
		
		getEventManager().queueEvent(new FinishPlayback(endReplay + e.getTimeStamp() - startReplay, 4, this.getObjectID()));
		
	}
	
	@Override
	public void handleEvent(Event e) {
		switch (e.getEventType()) {
		case Event.EVENT_TYPE_START_RECORD: {
			startReplay = Server.globalTime.getTotalTimeElapsed();
			canRecord = true;
			break;
		}
		case Event.EVENT_TYPE_STOP_RECORD: {
			endReplay = Server.globalTime.getTotalTimeElapsed();
			canRecord = false;
			play();
			break;
		}
		default:
			if(canRecord) {
				queue.add(e);
			}
			break;
		}
	}

}
