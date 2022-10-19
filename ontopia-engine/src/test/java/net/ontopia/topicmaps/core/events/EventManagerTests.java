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

package net.ontopia.topicmaps.core.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.impl.utils.EventListenerIF;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.utils.OntopiaRuntimeException;
import org.junit.Test;
  
public abstract class EventManagerTests extends AbstractTopicMapTest {

  protected TopicMapReferenceIF topicmapRef;
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TopicMapBuilderIF builder; // builder used for creating new objects
  protected TesterListener listener;
  
  @Override
  public void setUp() throws Exception {
    // get a new topic map object from the factory.
    factory = getFactory();
    topicmapRef = factory.makeTopicMapReference();
    listener = new TesterListener();
    try {
      // load topic map
      TopicMapStoreIF store = topicmapRef.createStore(false);
      store.getTopicMap();
      EventManagerIF emanager = ((AbstractTopicMapStore)store).getEventManager();
      emanager.addListener(listener, AssociationIF.EVENT_ADD_ROLE);    
      emanager.addListener(listener, AssociationIF.EVENT_ADD_THEME);
      emanager.addListener(listener, AssociationIF.EVENT_REMOVE_ROLE);
      emanager.addListener(listener, AssociationIF.EVENT_REMOVE_THEME);
      emanager.addListener(listener, AssociationIF.EVENT_SET_TYPE);
      emanager.addListener(listener, AssociationRoleIF.EVENT_SET_PLAYER);
      emanager.addListener(listener, AssociationRoleIF.EVENT_SET_TYPE);
      emanager.addListener(listener, OccurrenceIF.EVENT_ADD_THEME);
      emanager.addListener(listener, OccurrenceIF.EVENT_REMOVE_THEME);
      emanager.addListener(listener, OccurrenceIF.EVENT_SET_DATATYPE);
      emanager.addListener(listener, OccurrenceIF.EVENT_SET_TYPE);
      emanager.addListener(listener, OccurrenceIF.EVENT_SET_VALUE);
      emanager.addListener(listener, ReifiableIF.EVENT_SET_REIFIER);
      emanager.addListener(listener, TMObjectIF.EVENT_ADD_ITEMIDENTIFIER);
      emanager.addListener(listener, TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER);
      emanager.addListener(listener, TopicIF.EVENT_ADD_OCCURRENCE);    
      emanager.addListener(listener, TopicIF.EVENT_ADD_SUBJECTIDENTIFIER);    
      emanager.addListener(listener, TopicIF.EVENT_ADD_SUBJECTLOCATOR);    
      emanager.addListener(listener, TopicIF.EVENT_ADD_TOPICNAME);    
      emanager.addListener(listener, TopicIF.EVENT_ADD_TYPE);
      emanager.addListener(listener, TopicIF.EVENT_REMOVE_OCCURRENCE);    
      emanager.addListener(listener, TopicIF.EVENT_REMOVE_SUBJECTIDENTIFIER);    
      emanager.addListener(listener, TopicIF.EVENT_REMOVE_SUBJECTLOCATOR);    
      emanager.addListener(listener, TopicIF.EVENT_REMOVE_TOPICNAME);    
      emanager.addListener(listener, TopicIF.EVENT_REMOVE_TYPE);
      emanager.addListener(listener, TopicMapIF.EVENT_ADD_ASSOCIATION);    
      emanager.addListener(listener, TopicMapIF.EVENT_ADD_TOPIC);    
      emanager.addListener(listener, TopicMapIF.EVENT_REMOVE_ASSOCIATION);
      emanager.addListener(listener, TopicMapIF.EVENT_REMOVE_TOPIC);
      emanager.addListener(listener, TopicNameIF.EVENT_ADD_THEME);
      emanager.addListener(listener, TopicNameIF.EVENT_ADD_VARIANT);
      emanager.addListener(listener, TopicNameIF.EVENT_REMOVE_THEME);
      emanager.addListener(listener, TopicNameIF.EVENT_REMOVE_VARIANT);
      emanager.addListener(listener, TopicNameIF.EVENT_SET_TYPE);
      emanager.addListener(listener, TopicNameIF.EVENT_SET_VALUE);
      emanager.addListener(listener, VariantNameIF.EVENT_ADD_THEME);
      emanager.addListener(listener, VariantNameIF.EVENT_REMOVE_THEME);
      emanager.addListener(listener, VariantNameIF.EVENT_SET_DATATYPE);
      emanager.addListener(listener, VariantNameIF.EVENT_SET_VALUE);     
      topicmap = store.getTopicMap();

      // get the builder of that topic map.
      builder = topicmap.getBuilder();
      
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public void tearDown() {
    // Inform the factory that the topic map is not needed anymore.
    topicmap.getStore().close();
    factory.releaseTopicMapReference(topicmapRef);
    // Reset the member variables.
    topicmap = null;
    builder = null;
  }
  
  // --- Test cases

  protected class Event {
    private Object object;
    private String event;
    Event(Object object, String event, Object new_value, Object old_value) {
      this.object = object;
      this.event = event;
    }
    @Override
    public String toString() {
        return event + " " + object;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof Event) {
        Event oevent = (Event)o;
        // System.out.println(" " + this + " " + o + " " + object.equals(oevent.object) + " " + event.equals(oevent.event));
        return object.equals(oevent.object) && event.equals(oevent.event);
      }
      return false;
    }
  }

  protected class TesterListener implements EventListenerIF {
    private List<Event> seenEvents = new ArrayList<Event>();

    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      seenEvents.add(new Event(object, event, new_value, old_value));
    }

    public void reset() {
      seenEvents.clear();
    }

    public void assertEvents(List<Event> events) {
        boolean equals = true;
        int ix = -1;
        if (events.size() != seenEvents.size()) {
            equals = false;
        } else {
          for (int i=0; i < events.size(); i++) {
              if (!events.get(i).equals(seenEvents.get(i))) {
                  ix = i;
                  equals = false;
                  break;
              }
          }
        }
      if (!equals) {
          StringBuilder sb = new StringBuilder();
          sb.append("Events not as expected:\n");
          if (ix > -1) sb.append("Different at index: ").append(ix).append("\n");
          sb.append("seen (").append(seenEvents.size()).append(")\n");
          for (int i=0; i < seenEvents.size(); i++) {
              sb.append(i).append(": ").append(seenEvents.get(i)).append("\n");
          }
          sb.append("expected (").append(events.size()).append(")\n");
          for (int i=0; i < events.size(); i++) {
              sb.append(i).append(": ").append(events.get(i)).append("\n");
          }
          throw new RuntimeException(sb.toString());
      }
      reset();
    }

  }
  
  @Test
  public void testTopicLifecycle() {

    // --- topic events

    TopicIF topic = builder.makeTopic();

    // TopicIF.addSubjectLocator
    LocatorIF topic_subject_locator = URILocator.create("topic:subject-locator");
    topic.addSubjectLocator(topic_subject_locator);

    // TopicIF.removeSubjectLocator
    topic.removeSubjectLocator(topic_subject_locator);

    // TopicIF.addSubjectIdentifier
    LocatorIF topic_subject_identifier = URILocator.create("topic:subject-identifier");
    topic.addSubjectIdentifier(topic_subject_identifier);

    // TopicIF.removeSubjectIdentifier
    topic.removeSubjectIdentifier(topic_subject_identifier);

    // TopicIF.addItemIdentifier
    LocatorIF topic_item_identifier = URILocator.create("topic:item-identifier");
    topic.addItemIdentifier(topic_item_identifier);
    
    // TopicIF.removeItemIdentifier
    topic.removeItemIdentifier(topic_item_identifier);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, topic, null), 
                    new Event(topic, TopicIF.EVENT_ADD_SUBJECTLOCATOR, topic_subject_locator, null), 
                    new Event(topic, TopicIF.EVENT_REMOVE_SUBJECTLOCATOR, null, topic_subject_locator), 
                    new Event(topic, TopicIF.EVENT_ADD_SUBJECTIDENTIFIER, topic_subject_identifier, null), 
                    new Event(topic, TopicIF.EVENT_REMOVE_SUBJECTIDENTIFIER, null, topic_subject_locator), 
                    new Event(topic, TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, topic_item_identifier, null), 
                    new Event(topic, TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, null, topic_item_identifier)));

    // TopicIF.addType
    TopicIF topic_type = builder.makeTopic();
    topic.addType(topic_type);

    // TopicIF.removeType
    topic.removeType(topic_type);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, topic_type, null), 
                    new Event(topic, TopicIF.EVENT_ADD_TYPE, topic_type, null), 
                    new Event(topic, TopicIF.EVENT_REMOVE_TYPE, null, topic_type)));

    // -----------------------------------------------------------------------------

    // TopicIF.addTopicName
    TopicIF tn_type1 = builder.makeTopic();
    String tn_tn1 = "tn1";
    TopicNameIF tn = builder.makeTopicName(topic, tn_type1, tn_tn1);
    
    // TopicNameIF.setValue
    String tn_tn2 = "tn2";
    tn.setValue(tn_tn2);

    // TopicNameIF.setType
    TopicIF tn_type2 = builder.makeTopic();
    tn.setType(tn_type2);

    // TopicNameIF.addItemIdentifier
    LocatorIF tn_item_identifier = URILocator.create("tn:item-identifier");
    tn.addItemIdentifier(tn_item_identifier);
    
    // TopicNameIF.removeItemIdentifier
    tn.removeItemIdentifier(tn_item_identifier);

    // TopicNameIF.addTheme
    TopicIF tn_theme = builder.makeTopic();
    tn.addTheme(tn_theme);

    // TopicNameIF.removeTheme
    tn.removeTheme(tn_theme);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, tn_type1, null), 
                    new Event(topic, TopicIF.EVENT_ADD_TOPICNAME, tn, null), 
                    new Event(tn, TopicNameIF.EVENT_SET_TYPE, tn_type1, null), 
                    new Event(tn, TopicNameIF.EVENT_SET_VALUE, tn_tn1, null), 
                    new Event(tn, TopicNameIF.EVENT_SET_VALUE, tn_tn2, tn_tn1), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, tn_type2, null), 
                    new Event(tn, TopicNameIF.EVENT_SET_TYPE, tn_type2, tn_type1), 
                    new Event(tn, TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, tn_item_identifier, null), 
                    new Event(tn, TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, null, tn_item_identifier), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, tn_theme, null), 
                    new Event(tn, TopicNameIF.EVENT_ADD_THEME, tn_theme, null), 
                    new Event(tn, TopicNameIF.EVENT_REMOVE_THEME, null, tn_theme)));

    // TopicNameIF.addVariant
    String vn_vn1 = "vn1";
    VariantNameIF vn = builder.makeVariantName(tn, vn_vn1, Collections.emptySet());

    // VariantNameIF.setValue
    String vn_vn2 = "123";
    vn.setValue(vn_vn2, DataTypes.TYPE_INTEGER);

    // VariantNameIF.addItemIdentifier
    LocatorIF vn_item_identifier = URILocator.create("vn:item-identifier");
    vn.addItemIdentifier(vn_item_identifier);
    
    // VariantNameIF.removeItemIdentifier
    vn.removeItemIdentifier(vn_item_identifier);

    // VariantNameIF.addTheme
    TopicIF vn_theme = builder.makeTopic();
    vn.addTheme(vn_theme);

    // VariantNameIF.removeTheme
    vn.removeTheme(vn_theme);

    listener.assertEvents(
      Arrays.asList(new Event(tn, TopicNameIF.EVENT_ADD_VARIANT, vn, null), 
                    new Event(vn, VariantNameIF.EVENT_SET_DATATYPE, DataTypes.TYPE_STRING, null), 
                    new Event(vn, VariantNameIF.EVENT_SET_VALUE, vn_vn1, null), 
                    new Event(vn, VariantNameIF.EVENT_SET_DATATYPE, DataTypes.TYPE_INTEGER, null), 
                    new Event(vn, VariantNameIF.EVENT_SET_VALUE, vn_vn2, vn_vn1), 
                    new Event(vn, TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, vn_item_identifier, null), 
                    new Event(vn, TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, null, vn_item_identifier), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, vn_theme, null), 
                    new Event(vn, VariantNameIF.EVENT_ADD_THEME, vn_theme, null), 
                    new Event(vn, VariantNameIF.EVENT_REMOVE_THEME, null, vn_theme)));

    // VariantNameIF.remove
    vn.remove();
    
    listener.assertEvents(
      Arrays.asList(new Event(tn, TopicNameIF.EVENT_REMOVE_VARIANT, vn, null)));
 
    // TopicNameIF.remove
    tn.remove();

    // WARN: TopicNameIF.removeVariant event if variants exist at this point

    listener.assertEvents(
      Arrays.asList(new Event(topic, TopicIF.EVENT_REMOVE_TOPICNAME, tn, null))); 

    // -----------------------------------------------------------------------------
 
    // TopicIF.addOccurrence
    TopicIF oc_type1 = builder.makeTopic();
    String oc_oc1 = "oc1";
    OccurrenceIF oc = builder.makeOccurrence(topic, oc_type1, oc_oc1);
 
    // OccurrenceIF.setValue
    String oc_oc2 = "123";
    oc.setValue(oc_oc2, DataTypes.TYPE_INTEGER);
 
    // OccurrenceIF.setType
    TopicIF oc_type2 = builder.makeTopic();
    oc.setType(oc_type2);
 
    // OccurrenceIF.addItemIdentifier
    LocatorIF oc_item_identifier = URILocator.create("oc:item-identifier");
    oc.addItemIdentifier(oc_item_identifier);
    
    // OccurrenceIF.removeItemIdentifier
    oc.removeItemIdentifier(oc_item_identifier);
 
    // OccurrenceIF.addTheme
    TopicIF oc_theme = builder.makeTopic();
    oc.addTheme(oc_theme);
 
    // OccurrenceIF.removeTheme
    oc.removeTheme(oc_theme);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, oc_type1, null), 
                    new Event(topic, TopicIF.EVENT_ADD_OCCURRENCE, oc, null), 
                    new Event(oc, OccurrenceIF.EVENT_SET_TYPE, oc_type1, null), 
                    new Event(oc, OccurrenceIF.EVENT_SET_DATATYPE, DataTypes.TYPE_STRING, null), 
                    new Event(oc, OccurrenceIF.EVENT_SET_VALUE, oc_oc1, null), 
                    new Event(oc, OccurrenceIF.EVENT_SET_DATATYPE, DataTypes.TYPE_INTEGER, null), 
                    new Event(oc, OccurrenceIF.EVENT_SET_VALUE, oc_oc2, oc_oc1), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, oc_type2, null), 
                    new Event(oc, OccurrenceIF.EVENT_SET_TYPE, oc_type2, oc_type1), 
                    new Event(oc, TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, oc_item_identifier, null), 
                    new Event(oc, TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, null, oc_item_identifier), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, oc_theme, null), 
                    new Event(oc, OccurrenceIF.EVENT_ADD_THEME, oc_theme, null), 
                    new Event(oc, OccurrenceIF.EVENT_REMOVE_THEME, null, oc_theme)));
 
    // OccurrenceIF.remove
    oc.remove();

    listener.assertEvents(
      Arrays.asList(new Event(topic, TopicIF.EVENT_REMOVE_OCCURRENCE, oc, null))); 

    // TopicIF.remove
    topic.remove();

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, TopicMapIF.EVENT_REMOVE_TOPIC, null, topic))); 

    // -----------------------------------------------------------------------------

    // AssociationIF.addRole
    TopicIF as_type1 = builder.makeTopic();
    AssociationIF as = builder.makeAssociation(as_type1);

    TopicIF ar1_type1 = builder.makeTopic();
    TopicIF ar1_player1 = builder.makeTopic();
    AssociationRoleIF ar1 = builder.makeAssociationRole(as, ar1_type1, ar1_player1);

    TopicIF ar2_type1 = builder.makeTopic();
    TopicIF ar2_player1 = builder.makeTopic();
    AssociationRoleIF ar2 = builder.makeAssociationRole(as, ar2_type1, ar2_player1);
 
    // AssociationIF.setType
    TopicIF as_type2 = builder.makeTopic();
    as.setType(as_type2);
 
    // AssociationIF.addItemIdentifier
    LocatorIF as_item_identifier = URILocator.create("as:item-identifier");
    as.addItemIdentifier(as_item_identifier);
    
    // AssociaitonIF.removeItemIdentifier
    as.removeItemIdentifier(as_item_identifier);
 
    // AssociationIF.addTheme
    TopicIF as_theme = builder.makeTopic();
    as.addTheme(as_theme);
 
    // AssociationIF.removeTheme
    as.removeTheme(as_theme);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, as_type1, null), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_ASSOCIATION, as, null), 
                    new Event(as, AssociationIF.EVENT_SET_TYPE, as_type1, null), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, ar1_type1, null), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, ar1_player1, null), 
                    new Event(as, AssociationIF.EVENT_ADD_ROLE, ar1, null), 
                    new Event(ar1, AssociationRoleIF.EVENT_SET_TYPE, ar1_type1, null), 
                    new Event(ar1, AssociationRoleIF.EVENT_SET_PLAYER, ar1_player1, null), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, ar2_type1, null), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, ar2_player1, null), 
                    new Event(as, AssociationIF.EVENT_ADD_ROLE, ar2, null), 
                    new Event(ar2, AssociationRoleIF.EVENT_SET_TYPE, ar2_type1, null), 
                    new Event(ar2, AssociationRoleIF.EVENT_SET_PLAYER, ar2_player1, null), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, as_type2, null), 
                    new Event(as, AssociationIF.EVENT_SET_TYPE, as_type2, as_type1), 
                    new Event(as, TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, as_item_identifier, null), 
                    new Event(as, TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, null, as_item_identifier), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, as_theme, null), 
                    new Event(as, AssociationIF.EVENT_ADD_THEME, as_theme, null), 
                    new Event(as, AssociationIF.EVENT_REMOVE_THEME, null, as_theme)));
 
    // AssociationRoleIF.setType
    TopicIF ar2_type2 = builder.makeTopic();
    ar2.setType(ar2_type2);
 
    // AssociationRoleIF.setPlayer
    TopicIF ar2_player2 = builder.makeTopic();
    ar2.setPlayer(ar2_player2);
 
    // AssociationRoleIF.addItemIdentifier
    LocatorIF ar2_item_identifier = URILocator.create("ar:item-identifier");
    ar2.addItemIdentifier(ar2_item_identifier);
    
    // AssociationRoleIF.removeItemIdentifier
    ar2.removeItemIdentifier(ar2_item_identifier);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, ar2_type2, null), 
                    new Event(ar2, AssociationRoleIF.EVENT_SET_TYPE, ar2_type2, ar2_type1), 
                    new Event(topicmap, TopicMapIF.EVENT_ADD_TOPIC, ar2_player2, null), 
                    new Event(ar2, AssociationRoleIF.EVENT_SET_PLAYER, ar2_player2, ar2_player1), 
                    new Event(ar2, TMObjectIF.EVENT_ADD_ITEMIDENTIFIER, ar2_item_identifier, null), 
                    new Event(ar2, TMObjectIF.EVENT_REMOVE_ITEMIDENTIFIER, null, ar2_item_identifier)));
 
    // AssociationRoleIF.remove
    ar2.remove();

    listener.assertEvents(
      Arrays.asList(new Event(as, AssociationIF.EVENT_REMOVE_ROLE, null, ar2))); 
 
    // AssociationIF.remove
    as.remove();

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, TopicMapIF.EVENT_REMOVE_ASSOCIATION, null, as))); 
    
  }

}
