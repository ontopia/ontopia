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

package net.ontopia.topicmaps.utils.ctm;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.net.URISyntaxException;
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
  private Map<String, LocatorIF> prefixes;
  private Map<String, Template> templates; // String = name + paramcount
  private Set<LocatorIF> include_uris; // extra base URIs passed in from 
                                       // include masters
  private int counter; // for anonymous topics

  public GlobalParseContext(TopicMapIF topicmap, LocatorIF base) {
    this.topicmap = topicmap;
    this.base = base;
    this.builder = topicmap.getBuilder();
    this.prefixes = new HashMap<String, LocatorIF>();
    this.templates = new HashMap<String, Template>();
    this.counter = 0;
    this.include_uris = new CompactHashSet<LocatorIF>();
  }

  @Override
  public void addPrefix(String prefix, LocatorIF locator) {
    LocatorIF boundto = prefixes.get(prefix);
    if (boundto != null && !boundto.equals(locator)) {
      throw new InvalidTopicMapException("Attempted to bind prefix " + prefix +
                                         " to " + locator.getAddress() + ", but "+
                                         "it's already bound to " +
                                         boundto.getAddress());
    }

    prefixes.put(prefix, locator);
  }

  @Override
  public void addIncludeUri(LocatorIF uri) {
    include_uris.add(uri);
  }

  @Override
  public Set<LocatorIF> getIncludeUris() {
    return include_uris;
  }
  
  @Override
  public LocatorIF resolveQname(String qname) {
    int ix = qname.indexOf(':');
    String prefix = qname.substring(0, ix);
    String local = qname.substring(ix+1);
    
    LocatorIF boundto = prefixes.get(prefix);
    if (boundto == null) {
      throw new InvalidTopicMapException("Cannot resolve qname " + qname + ", " +
                                         "prefix not bound");
    }

    try {
      return new URILocator(boundto.getAddress() + local);
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public ValueGeneratorIF getTopicById(String id) {
    if (base == null) {
      // when no base locator only absolute URIs are allowed
      // see https://github.com/ontopia/ontopia/issues/182
      throw new InvalidTopicMapException("Cannot resolve id '" + id +
                                         "' when no base locator");
    }
    LocatorIF itemid = base.resolveAbsolute('#' + id);
    return new TopicByItemIdentifierGenerator(this, itemid, id);
  }
  
  @Override
  public ValueGeneratorIF getTopicByItemIdentifier(LocatorIF itemid) {
    return new TopicByItemIdentifierGenerator(this, itemid);
  }

  @Override
  public ValueGeneratorIF getTopicBySubjectLocator(LocatorIF subjloc) {
    return new TopicBySubjectLocatorGenerator(this, subjloc);
  }

  @Override
  public ValueGeneratorIF getTopicBySubjectIdentifier(LocatorIF subjid) {
    return new TopicBySubjectIdentifierGenerator(this, subjid);
  }

  @Override
  public ValueGeneratorIF getTopicByQname(String qname) {
    return new TopicBySubjectIdentifierGenerator(this, resolveQname(qname));
  }
  
  @Override
  public TopicIF makeAnonymousTopic() {
    counter++;
    LocatorIF itemid = base.resolveAbsolute("#$__" + counter);
    return makeTopicByItemIdentifier(itemid);
  }

  @Override
  public TopicIF makeAnonymousTopic(String wildcard_name) {
    counter++;
    LocatorIF itemid = base.resolveAbsolute("#$__" + counter + '.' +
                                            wildcard_name);
    return makeTopicByItemIdentifier(itemid);
  }

  @Override
  public void registerTemplate(String name, Template template) {
    String key = name + template.getParameterCount();
    if (templates.containsKey(key)) {
      throw new InvalidTopicMapException("Template " + name + " already defined"
                                         + " with " + template.getParameterCount() + " parameters");
    }
    templates.put(key, template);
  }

  @Override
  public Template getTemplate(String name, int paramcount) {
    return templates.get(name + paramcount);
  }

  @Override
  public Map getTemplates() {
    return templates;
  }
  
  @Override
  public TopicIF makeTopicById(String id) {
    return makeTopicByItemIdentifier(base.resolveAbsolute('#' + id));
  }

  @Override
  public TopicIF makeTopicByItemIdentifier(LocatorIF itemid) {
    TopicIF topic = (TopicIF) topicmap.getObjectByItemIdentifier(itemid);
    if (topic == null) {
      topic = builder.makeTopic();
      topic.addItemIdentifier(itemid);
    }
    return topic;
  }

  @Override
  public TopicIF makeTopicBySubjectLocator(LocatorIF subjloc) {
    TopicIF topic = topicmap.getTopicBySubjectLocator(subjloc);
    if (topic == null) {
      topic = builder.makeTopic();
      topic.addSubjectLocator(subjloc);
    }
    return topic;
  }

  @Override
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
    
    @Override
    public ValueGeneratorIF copy() {
      return this; // FIXME: this is safe as long as we keep making new generators
    }
    
    @Override
    public String getLiteral() {
      throw new InvalidTopicMapException("Topic reference passed, but literal "+
                                         "expected: " + getDescription());
    }
  
    @Override
    public LocatorIF getDatatype() {
      throw new InvalidTopicMapException("Topic reference passed, but literal "+
                                         "expected: " + getDescription());
    }

    @Override
    public LocatorIF getLocator() {
      throw new InvalidTopicMapException("Topic reference passed, but locator "+
                                         "expected: " + getDescription());
    }

    protected abstract String getDescription();
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
    
    @Override
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

    @Override
    protected String getDescription() {
      if (id != null) {
        return "item identifier #" + id;
      } else {
        return "item identifier " + locator.getExternalForm();
      }
    }
  }

  static class TopicBySubjectIdentifierGenerator extends InternalTopicGenerator {

    public TopicBySubjectIdentifierGenerator(ParseContextIF context,
                                          LocatorIF locator) {
      super(context, locator);
    }
    
    @Override
    public TopicIF getTopic() {
      return context.makeTopicBySubjectIdentifier(locator);
    }

    @Override
    protected String getDescription() {
      return "subject identifier " + locator.getExternalForm();
    }
  }

  static class TopicBySubjectLocatorGenerator extends InternalTopicGenerator {

    public TopicBySubjectLocatorGenerator(ParseContextIF context,
                                          LocatorIF locator) {
      super(context, locator);
    }
    
    @Override
    public TopicIF getTopic() {
      return context.makeTopicBySubjectLocator(locator);
    }
    
    @Override
    protected String getDescription() {
      return "subject locator " + locator.getExternalForm();
    }
  }
}
