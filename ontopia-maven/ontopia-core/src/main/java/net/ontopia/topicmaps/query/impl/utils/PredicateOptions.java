
// $Id: PredicateOptions.java,v 1.2 2005/04/05 11:38:58 larsga Exp $

package net.ontopia.topicmaps.query.impl.utils;

/**
 * INTERNAL: Used as a special, "magic", argument to predicates,
 * inserted by the query optimizer to tell them to behave differently.
 * The meaning of this argument differs depending on the predicate in
 * question.
 */
public class PredicateOptions {
  private String value;  // the meaning of this depends on the user
  private Object column; // the meaning of this depends on the user
  
  public PredicateOptions(String value) {
    this.value = value;
  }
  
  public PredicateOptions(Object column) {
    this.column = column;
  }

  public String getValue() {
    return value;
  }

  public Object getColumn() {
    return column;
  }
  
  public String toString() {
    return "<<<PredicateOptions>>>";
  }
  
}
