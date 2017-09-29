package gvs.ui.graph.layout.ticker;

/**
 * Helperclass for layouting ticker
 * @author aegli
 *
 */
public class HitsPerSecond {
	
	private long [] time;
	private long init;
	private int size;
	private int first, last;
	
	public HitsPerSecond(int size) {
		init = System.currentTimeMillis();
		
		time = new long[size];
		for (int i=0; i<size; i++) { time[i] = init; }
		this.size = size;
		first = 0; 
		last = 0;
	}
	
	private void insert(long l) {
		time[first] = l;
		last = first;
		first = (first + 1) % size;

	}
	
	public void doHit() {
		insert(System.currentTimeMillis());
	}
	
	public double getHitsPerSecond() {
		long td = time[last] - time[first];
		return 1000.0 * size / td;
	}
}

