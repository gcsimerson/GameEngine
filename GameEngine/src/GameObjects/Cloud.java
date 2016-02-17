package GameObjects;

import Components.Color;
import Components.Movable;
import Components.Transform;
import EventManagment.DeathEvent;
import EventManagment.Event;
import Scripting.ScriptManager;

public class Cloud extends GameObject {
	
	private static final long serialVersionUID = -7673221608818686874L;

	public Cloud(int objectID) {
		super(objectID);
		addComponent(new Transform(this, 0, 200, 100, 25));
		addComponent(new Movable(this, 0, 0));
		addComponent(new Color(this, 255, 255, 255));
	}
	
	public void move(float x, float y) {
		
		// Movable of this object
		Movable movable;
		
		// Index of Transform component if it exists
		int indexOfMovable = this.findComponent(new Movable(this));
		
		// If it does, store the component in transform
		// If it doesn't just create a default transform
		if(indexOfMovable >= 0) {
			movable = (Movable) this.getComponent(indexOfMovable);
		}
		else {
			movable = new Movable(this);
		}
		
		movable.vx = x;
		movable.vy = y;
		
	}
	
	public void changeColor(int red, int green, int blue) {
		// Movable of this object
		Color color;
		
		// Index of Transform component if it exists
		int indexOfColor = this.findComponent(new Color(this));
		
		// If it does, store the component in transform
		// If it doesn't just create a default transform
		if(indexOfColor >= 0) {
			color = (Color) this.getComponent(indexOfColor);
		}
		else {
			color = new Color(this);
		}
		
		color.red = red;
		color.green = green;
		color.blue = blue;
	}
	
	@Override
	public void handleEvent(Event e) {
		switch(e.getEventType()) {
			case Event.EVENT_TYPE_CHARACTER_DEATH:
				
				ScriptManager.loadScript("scripts/cloud_event_script.js");

				ScriptManager.bindArgument("cloud", this);
				ScriptManager.executeScript();
				
				break;
			default:
				break;
				
		}
	}

}
