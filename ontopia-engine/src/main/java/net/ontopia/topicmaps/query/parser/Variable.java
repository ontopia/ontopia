
package net.ontopia.topicmaps.query.parser;

/**
 * INTERNAL: Used to represent variable references in tolog queries.
 */
public class Variable {
  protected String name; // does NOT include the initial '$'
  
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
