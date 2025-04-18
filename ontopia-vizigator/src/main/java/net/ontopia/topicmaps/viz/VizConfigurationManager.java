/*
 * #!
 * Ontopia Vizigator
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

package net.ontopia.topicmaps.viz;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.CharacteristicUtils;
import net.ontopia.topicmaps.utils.NullResolvingExternalReferenceHandler;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;

/**
 * INTERNAL: Abstract configuration manager class.
 */
public abstract class VizConfigurationManager {
  protected TopicMapBuilderIF builder;
  protected TopicMapIF topicmap;
  protected static final String BASE = "http://psi.ontopia.net/viz/";
  
  //Stores general configuration for the Vizigator.
  protected TopicIF generalTopic;
  
  // Stores configuration information for untyped topics.
  protected TopicIF untypedTopic;
  
  // Configures all topic types that have no explicit configuration.
  protected TopicIF defaultType;
  
  // Configures all association types that have no explicit configuration.
  protected TopicIF defaultAssociationType;
  
  protected static final String GENERAL_TOPIC = BASE + 
      "vizigator-general";
  protected static final String DEFAULT_TYPE = BASE +
      "default-type";
  protected static final String UNTYPED = BASE +
      "untyped";
  protected static final String DEFAULT_ASSOCIATION_TYPE = BASE +
      "default-association-type";

  /**
   * Creates an empty configuration manager where everything is set to default.
   */
  public VizConfigurationManager() {
    init();
  }

  /**
   * Constructor initializes the configuration by loading a topic map from the
   * URL given in the parameter.
   */
  public VizConfigurationManager(File tmfile) throws IOException {
      this(URIUtils.toURL(tmfile));
  }

  /**
   * Constructor initializes the configuration by loading a topic map from the
   * URL given in the parameter.
   */
  public VizConfigurationManager(URL tmurl) throws IOException {
    if (tmurl != null) {
      XTMTopicMapReader reader = new XTMTopicMapReader(tmurl);
      reader.setExternalReferenceHandler(new NullResolvingExternalReferenceHandler());
      reader.setValidation(false); // means we don't need Jing
      try {
        topicmap = reader.read();
      } catch (OntopiaRuntimeException e) {
        // if we can't read the configuration, carry on anyway
        // init() will make a blank TM for us
      }
    }
    if (topicmap == null) {
      topicmap = new InMemoryTopicMapStore().getTopicMap();
    }
    init();
  }
  
  /**
   * Returns the occurrence of the given type, if there is one.
   */
  protected OccurrenceIF getOccurrence(TopicIF topic, TopicIF type) {
    return CharacteristicUtils.getByType(topic.getOccurrences(),
        type);
  }

  /**
   * Removes the occurrence of a given type from a given configuration topic.
   * Returns true iff the occurrence was found and removed.
   */
  protected boolean removeOccurrence(TopicIF topic, TopicIF type) {
    if (topic == null) {
      return false;
    }
    
    OccurrenceIF occurrence = CharacteristicUtils
        .getByType(topic.getOccurrences(), type);
    
    if (occurrence == null) {
      return false;
    }
    
    occurrence.remove();
    return true;
  }

  /**
   * Looks up a topic by subject indicator, creating it if it can't be found. If
   * a new topic is created, assign "basename" as a basename.
   */
  protected TopicIF getTopic(String indicator, String basename) {
    try {
      LocatorIF loc = new URILocator(indicator);
      TopicIF t = topicmap.getTopicBySubjectIdentifier(loc);

      if (t == null) {
        t = builder.makeTopic();
        if (basename != null) {
          builder.makeTopicName(t, basename);
        }
        t.addSubjectIdentifier(loc);
      }

      return t;
    } catch (URISyntaxException mue) {
      throw new OntopiaRuntimeException(Messages
          .getString("Viz.MalformedURLForTopicSubjectIndicator"), mue);
    }
  }

  /**
   * Looks up a topic by subject indicator, creating it if it can't be found.
   */
  protected TopicIF getTopic(String indicator) {
    return getTopic(indicator, null);
  }
  
  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  protected void removeOccurence(TopicIF type, TopicIF occtype) {
    TopicIF target = getConfigTopic(type);
    OccurrenceIF occ = getOccurrence(target, occtype);
    if (occ != null) {
      getOccurrence(target, occtype).remove();
    }
  }

  protected void setOccurenceValue(TopicIF type, TopicIF occtype, String value) {
    TopicIF cfgtopic = getConfigTopic(type);
    OccurrenceIF occ = getOccurrence(cfgtopic, occtype);
    if (value == null) {
      return; // don't make the occ if there is no value to give it
    }
    if (occ == null) {
      occ = builder.makeOccurrence(cfgtopic, occtype, value);
    } else {
      occ.setValue(value);
    }
  }

  /**
   * Sets the Shape setting for this association or topic type in the topic map.
   */
  protected void setOccurrenceValue(TopicIF type, TopicIF occtype, int integer) {
    setOccurenceValue(type, occtype, Integer.toString(integer));
  }

  /**
   * Sets the visibility setting for this association or topic type in the topic
   * map.
   */
  protected void setOccurenceValue(TopicIF type, TopicIF occtype, boolean value) {
    setOccurenceValue(type, occtype, Boolean.toString(value));
  }

  public String getOccurrenceValue(TopicIF type, TopicIF occtype) {
    TopicIF cfgtopic = getConfigTopic(type);
    if (cfgtopic == null) {
      return null;
    }

    OccurrenceIF occ = getOccurrence(cfgtopic, occtype);
    if (occ == null) {
      return null;
    }

    return occ.getValue();
  }

  protected boolean getOccurrenceValue(TopicIF type, TopicIF occtype,
      boolean defaultBoolean) {
    String value = getOccurrenceValue(type, occtype);
    if (value == null) {
      return defaultBoolean;
    }
    return "true".equalsIgnoreCase(value);
  }

  protected int getOccurrenceValue(TopicIF type, TopicIF occtype, int defaultInt) {
    String value = getOccurrenceValue(type, occtype);
    if (value == null) {
      return defaultInt;
    }
    return Integer.parseInt(value);
  }

    public void save(File f) throws IOException {
      XTMTopicMapWriter writer = new XTMTopicMapWriter(f);
      writer.write(topicmap);
    }

  protected void init() {
    if (topicmap == null) {
      InMemoryTopicMapStore store = new InMemoryTopicMapStore();
      topicmap = store.getTopicMap();

      // we set a base address for the topic map so that we can have
      // <base>#<id> URIs inside the topic map. this is used to avoid
      // portability problems with local ids in visualized topic maps
      try {
        store.setBaseAddress(new URILocator("x-ontopia:this:is:a:fake:url"));
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException("IMPOSSIBLE ERROR", e);
      }
    }

    builder = topicmap.getBuilder();
    generalTopic = getTopic(GENERAL_TOPIC);
    untypedTopic = getTopic(UNTYPED);

    defaultType = getTopic(DEFAULT_TYPE);
    defaultAssociationType = getTopic(DEFAULT_ASSOCIATION_TYPE);
  }

  /**
   * If the locator is of the form <base># <id>the <id>part is returned,
   * otherwise we return null. (Note that base will already contain the '#' at
   * the end.)
   */
  private String relativize(String base, LocatorIF locator) {
    if (base == null) {
      return null;
    }
    String uri = locator.getAddress();
    if (uri.startsWith(base)) {
      return uri.substring(base.length());
    }
    return null;
  }

  /**
   * Looks up the corresponding topic (from the visualized topic map) in the
   * configuration topic map, creating one if it doesn't exist.
   */
  protected TopicIF getConfigTopic(TopicIF real) {
    if (real == null) {
      return untypedTopic;
    }
    
    // setting up some variables for use later
    LocatorIF cfgloc = topicmap.getStore().getBaseAddress();
    LocatorIF _baseAddress = real.getTopicMap().getStore().getBaseAddress();
    String realbase = (_baseAddress == null ? null : _baseAddress.getAddress() + '#');

    // try to look topic up
    TopicIF cfg = null;

    Iterator<LocatorIF> it = real.getSubjectLocators().iterator();
    while (cfg == null && it.hasNext()) {
      LocatorIF loc = it.next();
      cfg = topicmap.getTopicBySubjectLocator(loc);
      if (cfg == null) {
        // is this branch ever used?
        TMObjectIF obj = topicmap.getObjectByItemIdentifier(loc);
        if (obj instanceof TopicIF) {
          cfg = (TopicIF) obj;
        }
      }

      String id = relativize(realbase, loc);
      if (cfg == null && id != null) {
        cfg = topicmap.getTopicBySubjectIdentifier(cfgloc.resolveAbsolute('#' + id));
      }
    }

    it = real.getSubjectIdentifiers().iterator();
    while (cfg == null && it.hasNext()) {
      LocatorIF loc = it.next();
      cfg = topicmap.getTopicBySubjectIdentifier(loc);
      if (cfg == null) {
        // is this branch ever used?
        TMObjectIF obj = topicmap.getObjectByItemIdentifier(loc);
        if (obj instanceof TopicIF) {
          cfg = (TopicIF) obj;
        }
      }

      String id = relativize(realbase, loc);
      if (cfg == null && id != null) {
        cfg = topicmap.getTopicBySubjectIdentifier(cfgloc.resolveAbsolute('#' + id));
      }
    }

    it = real.getItemIdentifiers().iterator();
    while (cfg == null && it.hasNext()) {
      LocatorIF loc = it.next();
      cfg = (TopicIF) topicmap.getObjectByItemIdentifier(loc);
      if (cfg == null) {
        cfg = topicmap.getTopicBySubjectIdentifier(loc);
      }

      String id = relativize(realbase, loc);
      if (cfg == null && id != null) {
        cfg = (TopicIF) topicmap.getObjectByItemIdentifier(cfgloc
            .resolveAbsolute('#' + id));
      }
    }
    
    // if topic doesn't exist, create it
    if (cfg == null) {
      cfg = builder.makeTopic();
  
      it = real.getSubjectLocators().iterator();
      while (it.hasNext()) {
        LocatorIF loc = it.next();
        String id = relativize(realbase, loc);
        if (id != null) {
          loc = cfgloc.resolveAbsolute('#' + id);
        }
        cfg.addSubjectLocator(loc);
      }
  
      it = real.getSubjectIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF loc = it.next();
        String id = relativize(realbase, loc);
        if (id != null) {
          loc = cfgloc.resolveAbsolute('#' + id);
        }
        cfg.addSubjectIdentifier(loc);
      }
  
      it = real.getItemIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF loc = it.next();
        String id = relativize(realbase, loc);
        if (id != null) {
          loc = cfgloc.resolveAbsolute('#' + id);
        }
        cfg.addItemIdentifier(loc);
      }
    }
    
    // finally done
    return cfg;
  }
}
