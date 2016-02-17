package EventManagment;

public class InputEvent extends Event {

	private static final long serialVersionUID = 953263011333010426L;
	private float vx;
	private float vy;
	private boolean jump;
	
	public InputEvent(long timeStamp, int priority, float vx, boolean jump, float vy, int player) {
		super(timeStamp, priority, player);
		eventType = EVENT_TYPE_USER_INPUT;
		this.vx = vx;
		this.vy = vy;
		this.jump = jump;
	}
	
	public float getVX() {
		return vx;
	}
	
	public float getVY() {
		return vy;
	}
	
	public boolean getJump() {
		return jump;
	}
	
	public int getPlayer() {
		return objectID;
	}

}
