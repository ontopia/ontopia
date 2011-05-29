
package net.ontopia.topicmaps.query.parser;

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.BadObjectReferenceException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Represents the global immutable context of all tolog
 * queries.
 */
public class GlobalParseContext implements ParseContextIF {
  private PredicateFactoryIF factory;
  private TopicMapIF topicmap;
  private LocatorIF base;
  private TologStatement statement;

  public GlobalParseContext(PredicateFactoryIF factory, TopicMapIF topicmap) {
    this(factory, topicmap, topicmap.getStore().getBaseAddress());
  }

  public GlobalParseContext(PredicateFactoryIF factory, TopicMapIF topicmap, LocatorIF base) {
    this.factory = factory;
    this.topicmap = topicmap;
    this.base = base;
  }

  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  public LocatorIF resolveQName(QName qname) {
    // FIXME: right exception?
    throw new OntopiaRuntimeException("No such prefix " + qname.getPrefix());
  }
  
  public void addPrefixBinding(String prefix, String uri, int qualification) {
    throw new OntopiaRuntimeException("Can't add bindings to global context");
  }

  public void addPredicate(PredicateIF predicate) {
    throw new OntopiaRuntimeException("Can't add predicates to global context");
  }

  public LocatorIF absolutify(String uriref) throws AntlrWrapException {
    try {
      LocatorIF loc = null;
      if (base == null)
        return new URILocator(uriref);
      else
        return base.resolveAbsolute(uriref);
    } catch (OntopiaRuntimeException e) {
      if (e.getCause() != null &&
          e.getCause() instanceof MalformedURLException) {
        throw new AntlrWrapException(new InvalidQueryException(
                 "URI reference '" + uriref+"' is not a valid URI reference."));
      }
      throw e;
    } catch (MalformedURLException e) {
      throw new AntlrWrapException(new InvalidQueryException("URI reference '" +
                                                uriref + "' not a valid URI."));
    }
  }
  
  public TMObjectIF getObjectByIdentifier(String id) {
    if (base == null)
      return null;
    LocatorIF loc = base.resolveAbsolute("#" + id);
    return topicmap.getObjectByItemIdentifier(loc);
  }

  public TMObjectIF getObjectByObjectId(String id) throws AntlrWrapException {
    return topicmap.getObjectById(id);
  }
  
  public TopicIF getTopicBySubjectIdentifier(String uri) 
    throws AntlrWrapException {
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(absolutify(uri));
    if (topic == null)
      throw new AntlrWrapException(new BadObjectReferenceException("No topic with subject identifier '" + uri + "' found"));
    return topic;
  }

  public TopicIF getTopicBySubjectLocator(String uri) 
    throws AntlrWrapException {
    TopicIF topic = topicmap.getTopicBySubjectLocator(absolutify(uri));
    if (topic == null)
      throw new AntlrWrapException(new BadObjectReferenceException("No topic with subject locator '" + uri + "' found"));
    return topic;
  }

  public TMObjectIF getObjectByItemId(String uri) 
    throws AntlrWrapException {
    TMObjectIF object = topicmap.getObjectByItemIdentifier(absolutify(uri));
    if (object == null)
      throw new AntlrWrapException(new BadObjectReferenceException("No object with item identifier '" + uri + "' found"));
    return object;
  }

  public TMObjectIF getObject(QName qname) throws AntlrWrapException {
    if (qname.getPrefix() != null) // no prefixes bound here, so report error
      throw new AntlrWrapException(
              new InvalidQueryException("Unbound prefix " + qname.getPrefix() +
                                        " in " + qname));

    return getObjectByIdentifier(qname.getLocalName());
  }

  public TopicIF getTopic(QName qname) throws AntlrWrapException {
    TMObjectIF object = getObject(qname);
    // FIXME: check type
    return (TopicIF) object;
  }

  public PredicateIF getPredicate(QName qname, boolean assoc)
    throws AntlrWrapException {
    PredicateIF predicate = factory.createPredicate(qname.getLocalName());

    if (predicate == null) {
      TopicIF topic = getTopic(qname);
      if (topic != null)
        predicate = getPredicate(topic, assoc);
    }

    return predicate;
  }

  public PredicateIF getPredicate(TopicIF topic, boolean assoc) {
    return factory.createPredicate(topic, assoc);
  }

  public PredicateIF getPredicate(ParsedRule rule) {
    return factory.createPredicate(rule);
  }

  public ModuleIF getModule(String uri) {
    return factory.createModule(uri);
  }

  public boolean isLoading(String uri) {
    return false;
  }
  
  public boolean isBuiltInPredicate(String name) {
    return factory.isBuiltInPredicate(name);
  }


  public void dump() {
    System.out.println("===== GlobalParseContext " + this);
  }  
}
