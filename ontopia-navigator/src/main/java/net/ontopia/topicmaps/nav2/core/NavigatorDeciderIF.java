
package net.ontopia.topicmaps.nav2.core;

/**
 * INTERNAL: interface for classes which
 * implement some calculation on an object
 * an come to a binary decision.
 * Used by the Deciders in the if and filter tag.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.IfTag
 * @see net.ontopia.topicmaps.nav2.taglibs.TMvalue.FilterTag
 */
public interface NavigatorDeciderIF {

  /**
   * INTERNAL: if implemented criteria are matched: deliver true,
   * otherwise false.
   */
  public boolean ok(NavigatorPageIF contextTag, Object obj);
  
}





