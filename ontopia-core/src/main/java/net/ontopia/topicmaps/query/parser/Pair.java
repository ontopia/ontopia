
// $Id: Pair.java,v 1.5 2003/01/10 13:16:15 larsga Exp $

package net.ontopia.topicmaps.query.parser;

/**
 * INTERNAL: Used to represent a : b pairs in tolog queries.
 */
public class Pair {
  protected Object first;
  protected Object second;
  
  public Pair(Object first, Object second) {
    this.first = first;
    this.second = second;
  }

  public Object getFirst() {
    return first;
  }

  public Object getSecond() {
    return second;
  }
  
  public String toString() {
    return first + " : " + second;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof Pair))
      return false;

    Pair pair = (Pair)obj;
    return (first.equals(pair.first) &&
            second.equals(pair.first));
  }

  public int hashCode() {
    return first.hashCode() + second.hashCode();
  }
  
}
