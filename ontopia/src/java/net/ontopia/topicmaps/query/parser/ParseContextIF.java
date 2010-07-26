
// $Id: ParseContextIF.java,v 1.9 2008/06/23 09:31:25 lars.garshol Exp $

package net.ontopia.topicmaps.query.parser;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Represents an interpretation context for tolog queries.
 */
public interface ParseContextIF {

  // --- Constants for prefix bindings
  
  static final int SUBJECT_IDENTIFIER = 1;
  static final int SUBJECT_LOCATOR    = 2;
  static final int ITEM_IDENTIFIER    = 3;
  static final int MODULE             = 4;

  /**
   * INTERNAL: Returns the topic map being parsed against.
   */
  public TopicMapIF getTopicMap();

  /**
   * INTERNAL: Returns the full locator for the given QName, or
   * reports an error if the prefix is unbound, or if the prefix is
   * bound to something other than a subject identifier namespace
   * (since this is used for the CTM part of tolog INSERT only).
   * FIXME: what exception to throw?
   */
  public LocatorIF resolveQName(QName qname);
  
  /**
   * INTERNAL: Adds a prefix binding to the context.
   * @param prefix The prefix whose binding is being defined.
   * @param uri The (possibly relative) URI reference to which the prefix is bound.
   * @param qualification The interpretation of the URI. (Defined
   * using local constants.)
   */
  public void addPrefixBinding(String prefix, String uri, int qualification)
    throws AntlrWrapException;

  /**
   * INTERNAL: Adds a new predicate to the context.
   */
  public void addPredicate(PredicateIF predicate) throws AntlrWrapException;

  /**
   * INTERNAL: Interprets the given prefix and localname as a topic,
   * returning null if none is found.
   */
  public TopicIF getTopic(QName qname) throws AntlrWrapException;

  public TMObjectIF getObject(QName qname) throws AntlrWrapException;
  
  /**
   * INTERNAL: Returns the named predicate, or null if it does not
   * exist.
   */
  public PredicateIF getPredicate(QName qname, boolean assoc)
    throws AntlrWrapException;
  
  public PredicateIF getPredicate(TopicIF topic, boolean assoc);

  public PredicateIF getPredicate(ParsedRule rule);

  public ModuleIF getModule(String uri);

  public LocatorIF absolutify(String uriref) throws AntlrWrapException ;
  
  public TopicIF getTopicBySubjectIdentifier(String uri) throws AntlrWrapException;

  public TopicIF getTopicBySubjectLocator(String uri) throws AntlrWrapException;

  public TMObjectIF getObjectByItemId(String uri) throws AntlrWrapException;

  public TMObjectIF getObjectByObjectId(String id) throws AntlrWrapException;

  /**
   * INTERNAL: Returns true if the uri is being loaded or has already
   * been loaded. This method is used to make sure that there are no
   * infinite recursion when importing modules.
   */
  public boolean isLoading(String uri);

  /**
   * INTERNAL: Used to find out if the given predicate name is the
   * name of a built-in predicate. Needed for error checking, see bug
   * #1082.
   */
  public boolean isBuiltInPredicate(String name);

  /**
   * INTERNAL: Used for debugging.
   */
  public void dump();
}
