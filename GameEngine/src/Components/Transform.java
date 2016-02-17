package Components;
import GameObjects.GameObject;

public class Transform extends Component {
	
	private static final long serialVersionUID = -489721358209414936L;
	
	// X and Y location of object
	public int x;
	public int y;
	
	// Dimensions of Object
	public int width;
	public int height;

	// Constructor
	public Transform(GameObject o) {
		super(o);
	}
	
	// Constructor
	public Transform(GameObject o, int x, int y, int width, int height) {
		super(o);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	// change size
	public void updateSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void updateReferences() {
		return;
		
	}

	@Override
	public void update() {
		o.x = x;
		o.y = y;
		o.width = width;
		o.height = height;
		return;
	}

}
