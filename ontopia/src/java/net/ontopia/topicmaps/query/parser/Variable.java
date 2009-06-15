
// $Id: Variable.java,v 1.7 2003/12/15 10:30:37 larsga Exp $

package net.ontopia.topicmaps.query.parser;

/**
 * INTERNAL: Used to represent variable references in tolog queries.
 */
public class Variable {
  protected String name;
  
  public Variable(String name) {
    this.name = name.substring(1);
  }

  public String getName() {
    return name;
  }

  /// Object

  public String toString() {
    return "$" + name;
  }

  public boolean equals(Object obj) {
    return obj instanceof Variable &&
      name.equals(((Variable) obj).name);
  }

  public int hashCode() {
    return name.hashCode();
  }
  
}
