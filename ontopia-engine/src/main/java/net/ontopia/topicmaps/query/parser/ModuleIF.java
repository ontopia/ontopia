
package net.ontopia.topicmaps.query.parser;

/**
 * INTERNAL: Represents a tolog module.
 */
public interface ModuleIF {

  /**
   * INTERNAL: Returns the predicate with the given local name inside
   * the module.
   * @param name The local name of the predicate; that is the part of
   * the QName <em>after</em> the colon.
   */
  public PredicateIF getPredicate(String name);
  
}
