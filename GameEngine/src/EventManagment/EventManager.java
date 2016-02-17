package EventManagment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import GameObjects.GameObject;
import MainGameLoop.Server;

public class EventManager implements Serializable{
	
	private static final long serialVersionUID = 6524145177239272896L;
	
	Comparator<Event> comparator;
	PriorityQueue<Event> queue;
	ArrayList<Handler> componentHandlers;
	
	ArrayList<ServerHandler> serverHandler;
	
	public EventManager() {
        comparator = new EventComparator();
        queue = new PriorityQueue<Event>(1000, comparator);
        componentHandlers = new ArrayList<Handler>();
        serverHandler = new ArrayList<ServerHandler>();
	}
	
	public void registerHandler(GameObject o, int eventType) {
		componentHandlers.add(new Handler(o, eventType));
	}
	
	public void registerHandler(Server server, int eventTypeStartRecord) {
		serverHandler.add(new ServerHandler(server, eventTypeStartRecord));
		
	}
	
	public void queueEvent(Event e) {
		queue.add(e);
	}
	
	public void dispatchEvents(long timeStamp) {
		Event event = queue.peek();
		
		while (event != null && event.getTimeStamp() <= Server.globalTime.getTotalTimeElapsed()) {
			queue.remove();
			
			for(int i = 0 ; i < serverHandler.size() ; i++)
				if(serverHandler.get(i).getType() == event.getEventType()) {
					serverHandler.get(i).getObject().handleEvents(event);
				}
			
			for(int i = 0 ; i < componentHandlers.size() ; i++) {
				if(componentHandlers.get(i).getType() == event.getEventType()) {
					componentHandlers.get(i).getObject().handleEvent(event);
				}
			}
			
			event = queue.peek();
		}
		
	}
	
	private class Handler {
		
		private GameObject o;
		private int type;
		
		public Handler(GameObject o, int type) {
			this.o = o;
			this.type = type;
		}
		
		public GameObject getObject() {
			return o;
		}
		
		public int getType() {
			return type;
		}
		
	}
	
	private class ServerHandler {
		
		private Server o;
		private int type;
		
		public ServerHandler(Server o, int type) {
			this.o = o;
			this.type = type;
		}
		
		public Server getObject() {
			return o;
		}
		
		public int getType() {
			return type;
		}
		
	}
	
	private class EventComparator implements Comparator<Event> {

		@Override
		public int compare(Event e1, Event e2) {
			if(e1.getTimeStamp() < e2.getTimeStamp()) {
				return -1;
			}
			else if(e1.getTimeStamp() == e2.getTimeStamp()) {
				if(e1.getPriority() < e2.getPriority()) {
					return -1;
				}
				else if(e1.getPriority() > e2.getPriority()) {
					return 1;
				}
				else {
					return 0;
				}
			}
			return 1;
		}
		
	}

}
