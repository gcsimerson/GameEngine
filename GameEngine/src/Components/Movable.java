package Components;
import GameObjects.GameObject;
import MainGameLoop.Server;

public class Movable extends Component {
	
	private static final long serialVersionUID = 8732031778365208659L;
	
	// Velocity in X and Y direction
	public float vx;
	public float vy;
	
	private long totalSeconds;
	private long secondsElapsedSinceLastFrame;
	
	Transform transform;
	
	// Constructor
	public Movable(GameObject o) {
		super(o);
		vx = 0;
		vy = 0;
		totalSeconds = Server.globalTime.getTotalTimeElapsed();
		
		updateReferences();
	}

	// Constructor if you want to initialize the velocity to a non-zero value
	public Movable(GameObject o, float vx, float vy) {
		super(o);
		this.vx = vx;
		this.vy = vy;
		totalSeconds = Server.globalTime.getTotalTimeElapsed();
		
		updateReferences();
	}
	
	@Override
	public void updateReferences() {
		// Index of Transform component if it exists
		int indexOfTransform = o.findComponent(new Transform(o));
		
		// If it does, store the component in transform
		// If it doesn't just create a default transform
		if(indexOfTransform >= 0) {
			transform = (Transform) o.getComponent(indexOfTransform);
		}
		else {
			transform = new Transform(o);
		}
		
	}
	
	public void updateTime() {
		secondsElapsedSinceLastFrame = Server.globalTime.getTotalTimeElapsed() - totalSeconds;
		totalSeconds = Server.globalTime.getTotalTimeElapsed();
	}
	
	public void stepX() {
		transform.x += vx * secondsElapsedSinceLastFrame;
	}
	
	public void stepY() {
		transform.y += vy * secondsElapsedSinceLastFrame;
	}
	
	public void stepBackX() {
		transform.x -= vx * secondsElapsedSinceLastFrame;
	}
	
	public void stepBackY() {
		transform.y -= vy * secondsElapsedSinceLastFrame;
	}
	
	@Override
	public void update() {
		updateTime();
		stepX();
		stepY();
	}

}
