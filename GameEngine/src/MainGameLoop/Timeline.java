package MainGameLoop;

public class Timeline {
	
	// Beginning of frame
	private long totalTime;
	// Time elapsed since frame
	private long timeElapsed;
	// Time elapsed since first frame
	private long totalTimeElapsed;
	// Tic size to adjust other timelines
	private double ticSize;

	public Timeline() {
		totalTime = System.currentTimeMillis();
		ticSize = 1;
	}
	
	public Timeline(long totalTimeElapsed) {
		totalTime = System.currentTimeMillis();
		this.totalTimeElapsed = totalTimeElapsed;
	}
	
	// Update the time elapsed and adjust it by the tic size
	public void updateTime() {
		timeElapsed = System.currentTimeMillis() - totalTime;
		totalTimeElapsed += timeElapsed * ticSize;
		totalTime = System.currentTimeMillis();
	}
	
	// Get current time in seconds
	public long getTotalTimeElapsed() {
		return totalTimeElapsed;
	}
	
	public double getTicSize() {
		return ticSize;
	}
	
	public long getTimeElapsed() {
		return timeElapsed;
	}
	
	public void	adjustTicSize(double ticSize) {
		this.ticSize = ticSize;
	}
	
	

}
