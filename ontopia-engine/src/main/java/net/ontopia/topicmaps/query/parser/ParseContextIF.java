/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
  
  int SUBJECT_IDENTIFIER = 1;
  int SUBJECT_LOCATOR    = 2;
  int ITEM_IDENTIFIER    = 3;
  int MODULE             = 4;

  /**
   * INTERNAL: Returns the topic map being parsed against.
   */
  TopicMapIF getTopicMap();

  /**
   * INTERNAL: Returns the full locator for the given QName, or
   * reports an error if the prefix is unbound, or if the prefix is
   * bound to something other than a subject identifier namespace
   * (since this is used for the CTM part of tolog INSERT only).
   * FIXME: what exception to throw?
   */
  LocatorIF resolveQName(QName qname);
  
  /**
   * INTERNAL: Adds a prefix binding to the context.
   * @param prefix The prefix whose binding is being defined.
   * @param uri The (possibly relative) URI reference to which the prefix is bound.
   * @param qualification The interpretation of the URI. (Defined
   * using local constants.)
   */
  void addPrefixBinding(String prefix, String uri, int qualification)
    throws AntlrWrapException;

  /**
   * INTERNAL: Adds a new predicate to the context.
   */
  void addPredicate(PredicateIF predicate) throws AntlrWrapException;

  /**
   * INTERNAL: Interprets the given prefix and localname as a topic,
   * returning null if none is found.
   */
  TopicIF getTopic(QName qname) throws AntlrWrapException;

  TMObjectIF getObject(QName qname) throws AntlrWrapException;
  
  /**
   * INTERNAL: Returns the named predicate, or null if it does not
   * exist.
   */
  PredicateIF getPredicate(QName qname, boolean assoc)
    throws AntlrWrapException;
  
  PredicateIF getPredicate(TopicIF topic, boolean assoc);

  PredicateIF getPredicate(ParsedRule rule);

  ModuleIF getModule(String uri);

  LocatorIF absolutify(String uriref) throws AntlrWrapException ;
  
  TopicIF getTopicBySubjectIdentifier(String uri) throws AntlrWrapException;

  TopicIF getTopicBySubjectLocator(String uri) throws AntlrWrapException;

  TMObjectIF getObjectByItemId(String uri) throws AntlrWrapException;

  TMObjectIF getObjectByObjectId(String id) throws AntlrWrapException;

  /**
   * INTERNAL: Returns true if the uri is being loaded or has already
   * been loaded. This method is used to make sure that there are no
   * infinite recursion when importing modules.
   */
  boolean isLoading(String uri);

  /**
   * INTERNAL: Used to find out if the given predicate name is the
   * name of a built-in predicate. Needed for error checking, see bug
   * #1082.
   */
  boolean isBuiltInPredicate(String name);

  /**
   * INTERNAL: Used for debugging.
   */
  void dump();
}
