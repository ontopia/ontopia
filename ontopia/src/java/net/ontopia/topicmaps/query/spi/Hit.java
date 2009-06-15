
// $Id: Hit.java,v 1.1 2007/12/14 12:43:33 geir.gronmo Exp $

package net.ontopia.topicmaps.query.spi;

/**
 * INTERNAL: Class that holds search hit data.<p>
 */
public class Hit {

  private Object value;
  private float score;
  
  public Hit(Object value, float score) {
    this.value = value;
    this.score = score;
  }
  
  /**
   * INTERNAL: Gets the result value.
   */
  public Object getValue() {
    return value;
  }

  /**
   * INTERNAL: Gets the score ;
   */
  public float getScore() {
    return score;
  }

  public String toString() {
    return "[Hit " + getValue() + ", " + getScore() + "]";
  }
  
}





