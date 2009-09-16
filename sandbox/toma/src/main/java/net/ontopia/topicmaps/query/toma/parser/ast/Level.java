package net.ontopia.topicmaps.query.toma.parser.ast;

/**
 * INTERNAL: Level parameter for certain path expression elements.
 */
public class Level 
{
  private int start;
  private int end;
  
  public Level(int level) {
    this(level, level);
  }
  
  public Level(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    this.end = end;
  }
}
