
// $Id: GlobalParseContext.java,v 1.3 2009/02/27 12:01:06 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.net.MalformedURLException;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

public class GlobalParseContext implements ParseContextIF {
  private LocatorIF base;
  private TopicMapIF topicmap;
  private TopicMapBuilderIF builder;
  private Map prefixes;
  private Map templates;
  private int counter;
  private Set include_uris; // extra base URIs passed in from include masters

  public GlobalParseContext(TopicMapIF topicmap, LocatorIF base) {
    this.topicmap = topicmap;
    this.base = base;
    this.builder = topicmap.getBuilder();
    this.prefixes = new HashMap();
    this.templates = new HashMap();
    this.counter = 0;
    this.include_uris = new CompactHashSet();
    try {
      prefixes.put("xsd", new URILocator("http://www.w3.org/2001/XMLSchema#"));
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void addPrefix(String prefix, LocatorIF locator) {
    LocatorIF boundto = (LocatorIF) prefixes.get(prefix);
    if (boundto != null && !boundto.equals(locator))
      throw new InvalidTopicMapException("Attempted to bind prefix " + prefix +
                                         " to " + locator.getAddress() + ", but "+
                                         "it's already bound to " +
                                         boundto.getAddress());

    prefixes.put(prefix, locator);
  }

  public void addIncludeUri(LocatorIF uri) {
    include_uris.add(uri);
  }

  public Set getIncludeUris() {
    return include_uris;
  }
  
  public LocatorIF resolveQname(String qname) {
    int ix = qname.indexOf(':');
    String prefix = qname.substring(0, ix);
    String local = qname.substring(ix+1);
    
    LocatorIF boundto = (LocatorIF) prefixes.get(prefix);
    if (boundto == null)
      throw new InvalidTopicMapException("Cannot resolve qname " + qname + ", " +
                                         "prefix not bound");

    try {
      return new URILocator(boundto.getAddress() + local);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public ValueGeneratorIF getTopicById(String id) {
    if (base == null)
      // when no base locator only absolute URIs are allowed
      // see http://code.google.com/p/ontopia/issues/detail?id=182
      throw new InvalidTopicMapException("Cannot resolve id '" + id +
                                         "' when no base locator");
    LocatorIF itemid = base.resolveAbsolute('#' + id);
    return new TopicByItemIdentifierGenerator(this, itemid, id);
  }
  
  public ValueGeneratorIF getTopicByItemIdentifier(LocatorIF itemid) {
    return new TopicByItemIdentifierGenerator(this, itemid);
  }

  public ValueGeneratorIF getTopicBySubjectLocator(LocatorIF subjloc) {
    return new TopicBySubjectLocatorGenerator(this, subjloc);
  }

  public ValueGeneratorIF getTopicBySubjectIdentifier(LocatorIF subjid) {
    return new TopicBySubjectIdentifierGenerator(this, subjid);
  }

  public ValueGeneratorIF getTopicByQname(String qname) {
    return new TopicBySubjectIdentifierGenerator(this, resolveQname(qname));
  }
  
  public TopicIF makeAnonymousTopic() {
    counter++;
    LocatorIF itemid = base.resolveAbsolute("#$__" + counter);
    return makeTopicByItemIdentifier(itemid);
  }

  public TopicIF makeAnonymousTopic(String wildcard_name) {
    counter++;
    LocatorIF itemid = base.resolveAbsolute("#$__" + counter + '.' +
                                            wildcard_name);
    return makeTopicByItemIdentifier(itemid);
  }

  public void registerTemplate(String name, Template template) {
    if (templates.containsKey(name))
      throw new InvalidTopicMapException("Template " + name + " already defined");
    templates.put(name, template);
  }

  public Template getTemplate(String name) {
    return (Template) templates.get(name);
  }

  public Map getTemplates() {
    return templates;
  }
  
  public TopicIF makeTopicById(String id) {
    return makeTopicByItemIdentifier(base.resolveAbsolute('#' + id));
  }

  public TopicIF makeTopicByItemIdentifier(LocatorIF itemid) {
    TopicIF topic = (TopicIF) topicmap.getObjectByItemIdentifier(itemid);
    if (topic == null) {
      topic = builder.makeTopic();
      topic.addItemIdentifier(itemid);
    }
    return topic;
  }

  public TopicIF makeTopicBySubjectLocator(LocatorIF subjloc) {
    TopicIF topic = topicmap.getTopicBySubjectLocator(subjloc);
    if (topic == null) {
      topic = builder.makeTopic();
      topic.addSubjectLocator(subjloc);
    }
    return topic;
  }

  public TopicIF makeTopicBySubjectIdentifier(LocatorIF subjid) {
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(subjid);
    if (topic == null) {
      topic = builder.makeTopic();
      topic.addSubjectIdentifier(subjid);
    }
    return topic;
  }
  
  // --- Internal generator classes

  static abstract class InternalTopicGenerator extends AbstractTopicGenerator {
    protected ParseContextIF context;
    protected LocatorIF locator;

    public InternalTopicGenerator(ParseContextIF context, LocatorIF locator) {
      this.context = context;
      this.locator = locator;
    }
    
    public ValueGeneratorIF copy() {
      return this; // FIXME: this is safe as long as we keep making new generators
    }
  }
  
  static class TopicByItemIdentifierGenerator extends InternalTopicGenerator {
    private String id;

    public TopicByItemIdentifierGenerator(ParseContextIF context,
                                          LocatorIF locator) {
      super(context, locator);
    }

    public TopicByItemIdentifierGenerator(ParseContextIF context,
                                          LocatorIF locator,
                                          String id) {
      super(context, locator);
      this.id = id;
    }
    
    public TopicIF getTopic() {
      TopicIF topic = context.makeTopicByItemIdentifier(locator);
      if (id != null && !context.getIncludeUris().isEmpty()) {
        Iterator it = context.getIncludeUris().iterator();
        while (it.hasNext()) {
          LocatorIF loc = (LocatorIF) it.next();
          topic.addItemIdentifier(loc.resolveAbsolute('#' + id));
        }
      }
      return topic;
    }
  }

  static class TopicBySubjectIdentifierGenerator extends InternalTopicGenerator {

    public TopicBySubjectIdentifierGenerator(ParseContextIF context,
                                          LocatorIF locator) {
      super(context, locator);
    }
    
    public TopicIF getTopic() {
      return context.makeTopicBySubjectIdentifier(locator);
    }
  }

  static class TopicBySubjectLocatorGenerator extends InternalTopicGenerator {

    public TopicBySubjectLocatorGenerator(ParseContextIF context,
                                          LocatorIF locator) {
      super(context, locator);
    }
    
    public TopicIF getTopic() {
      return context.makeTopicBySubjectLocator(locator);
    }
  }
}
