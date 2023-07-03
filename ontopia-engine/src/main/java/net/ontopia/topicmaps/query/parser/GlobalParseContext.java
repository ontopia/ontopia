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

import java.net.URISyntaxException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.BadObjectReferenceException;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Represents the global immutable context of all tolog
 * queries.
 */
public class GlobalParseContext implements ParseContextIF {
  private PredicateFactoryIF factory;
  private TopicMapIF topicmap;
  private LocatorIF base;

  public GlobalParseContext(PredicateFactoryIF factory, TopicMapIF topicmap) {
    this(factory, topicmap, topicmap.getStore().getBaseAddress());
  }

  public GlobalParseContext(PredicateFactoryIF factory, TopicMapIF topicmap, LocatorIF base) {
    this.factory = factory;
    this.topicmap = topicmap;
    this.base = base;
  }

  @Override
  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  @Override
  public LocatorIF resolveQName(QName qname) {
    // FIXME: right exception?
    throw new OntopiaRuntimeException("No such prefix " + qname.getPrefix());
  }
  
  @Override
  public void addPrefixBinding(String prefix, String uri, int qualification) {
    throw new OntopiaRuntimeException("Can't add bindings to global context");
  }

  @Override
  public void addPredicate(PredicateIF predicate) {
    throw new OntopiaRuntimeException("Can't add predicates to global context");
  }

  @Override
  public LocatorIF absolutify(String uriref) throws AntlrWrapException {
    try {
      if (base == null) {
        return new URILocator(uriref);
      } else {
        return base.resolveAbsolute(uriref);
      }
    } catch (OntopiaRuntimeException e) {
      if (e.getCause() != null &&
          e.getCause() instanceof URISyntaxException) {
        throw new AntlrWrapException(new InvalidQueryException(
                 "URI reference '" + uriref+"' is not a valid URI reference."));
      }
      throw e;
    } catch (URISyntaxException | IllegalArgumentException e) {
      throw new AntlrWrapException(new InvalidQueryException("URI reference '" +
                                                uriref + "' not a valid URI."));
    }
  }
  
  public TMObjectIF getObjectByIdentifier(String id) {
    if (base == null) {
      return null;
    }
    LocatorIF loc = base.resolveAbsolute("#" + id);
    return topicmap.getObjectByItemIdentifier(loc);
  }

  @Override
  public TMObjectIF getObjectByObjectId(String id) throws AntlrWrapException {
    return topicmap.getObjectById(id);
  }
  
  @Override
  public TopicIF getTopicBySubjectIdentifier(String uri) 
    throws AntlrWrapException {
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(absolutify(uri));
    if (topic == null) {
      throw new AntlrWrapException(new BadObjectReferenceException("No topic with subject identifier '" + uri + "' found"));
    }
    return topic;
  }

  @Override
  public TopicIF getTopicBySubjectLocator(String uri) 
    throws AntlrWrapException {
    TopicIF topic = topicmap.getTopicBySubjectLocator(absolutify(uri));
    if (topic == null) {
      throw new AntlrWrapException(new BadObjectReferenceException("No topic with subject locator '" + uri + "' found"));
    }
    return topic;
  }

  @Override
  public TMObjectIF getObjectByItemId(String uri) 
    throws AntlrWrapException {
    TMObjectIF object = topicmap.getObjectByItemIdentifier(absolutify(uri));
    if (object == null) {
      throw new AntlrWrapException(new BadObjectReferenceException("No object with item identifier '" + uri + "' found"));
    }
    return object;
  }

  @Override
  public TMObjectIF getObject(QName qname) throws AntlrWrapException {
    if (qname.getPrefix() != null) { // no prefixes bound here, so report error
      throw new AntlrWrapException(
              new InvalidQueryException("Unbound prefix " + qname.getPrefix() +
                                        " in " + qname));
    }

    return getObjectByIdentifier(qname.getLocalName());
  }

  @Override
  public TopicIF getTopic(QName qname) throws AntlrWrapException {
    TMObjectIF object = getObject(qname);
    // FIXME: check type
    return (TopicIF) object;
  }

  @Override
  public PredicateIF getPredicate(QName qname, boolean assoc)
    throws AntlrWrapException {
    PredicateIF predicate = factory.createPredicate(qname.getLocalName());

    if (predicate == null) {
      TopicIF topic = getTopic(qname);
      if (topic != null) {
        predicate = getPredicate(topic, assoc);
      }
    }

    return predicate;
  }

  @Override
  public PredicateIF getPredicate(TopicIF topic, boolean assoc) {
    return factory.createPredicate(topic, assoc);
  }

  @Override
  public PredicateIF getPredicate(ParsedRule rule) {
    return factory.createPredicate(rule);
  }

  @Override
  public ModuleIF getModule(String uri) {
    return factory.createModule(uri);
  }

  @Override
  public boolean isLoading(String uri) {
    return false;
  }
  
  @Override
  public boolean isBuiltInPredicate(String name) {
    return factory.isBuiltInPredicate(name);
  }


  @Override
  public void dump() {
    System.out.println("===== GlobalParseContext " + this);
  }  
}
