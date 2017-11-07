package gvs.business.logic.physics.ticker;

/**
 * Helperclass for layouting ticker
 * 
 * @author aegli
 *
 */
public class HitsPerSecond {

  private static final double DEFAULT_FACTOR = 1000.0;
  private long[] time;
  private long init;
  private int size;
  private int first, last;

  /**
   * Constructor
   * 
   * @param size
   *          size
   */
  public HitsPerSecond(int size) {
    init = System.currentTimeMillis();

    time = new long[size];
    for (int i = 0; i < size; i++) {
      time[i] = init;
    }
    this.size = size;
    first = 0;
    last = 0;
  }

  /**
   * Insert long
   * 
   * @param l
   *          time
   */
  private void insert(long l) {
    time[first] = l;
    last = first;
    first = (first + 1) % size;
  }

  /**
   * Insert current time in miliseconds.
   */
  public void insertCurrentTimestamp() {
    insert(System.currentTimeMillis());
  }

  /**
   * Hits per seconds.
   * 
   * @return size / time difference * 1000
   */
  public double getHitsPerSecond() {
    long td = time[last] - time[first];
    return DEFAULT_FACTOR * size / td;
  }
}
