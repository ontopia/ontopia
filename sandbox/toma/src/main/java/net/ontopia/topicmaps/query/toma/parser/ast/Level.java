package net.ontopia.topicmaps.query.toma.parser.ast;

/**
 * INTERNAL: Indicates the level parameter for several path element expression,
 * e.g. type, instance, super, sub. A Level has a start and end value indicating
 * the range for this operations.
 */
public class Level {
  private int start;
  private int end;

  /**
   * Creates a new range in the form [level, level].
   * 
   * @param level the specified level.
   */
  public Level(int level) {
    this(level, level);
  }

  /**
   * Creates a new range in the form [start, end].
   * 
   * @param start the start of the range.
   * @param end the end of the range.
   */
  public Level(int start, int end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Get the start value.
   * 
   * @return the start value.
   */
  public int getStart() {
    return start;
  }

  /**
   * Set the start value.
   * 
   * @param start the start value to be set.
   */
  public void setStart(int start) {
    this.start = start;
  }

  /**
   * Get the end value.
   * 
   * @return the end value.
   */
  public int getEnd() {
    return end;
  }

  /**
   * Set the end value.
   * 
   * @param end the end value to be set.
   */
  public void setEnd(int end) {
    this.end = end;
  }
}
