package Components;

import GameObjects.GameObject;

public class Color extends Component {
	

	private static final long serialVersionUID = 2951670153770856701L;
	
	public float red;
	public float green;
	public float blue;
	
	public Color(GameObject o) {
		super(o);
	}

	public Color(GameObject o, float red, float green, float blue) {
		super(o);
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	// Change the color
	public void updateColor(float red, float green, float blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	@Override
	public void updateReferences() {
		return;
		
	}

	@Override
	public void update() {
		return;

	}

}
