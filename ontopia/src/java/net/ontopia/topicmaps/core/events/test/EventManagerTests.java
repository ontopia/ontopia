
// $Id$

package net.ontopia.topicmaps.core.events.test;

import java.io.File;
import java.util.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.test.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.events.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.impl.utils.EventListenerIF;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
  
public class EventManagerTests extends AbstractTopicMapTest {

  protected TopicMapReferenceIF topicmapRef;
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TopicMapBuilderIF builder; // builder used for creating new objects
  protected TesterListener listener;
  
  public EventManagerTests(String name) {
    super(name);
  }
  
  public void setUp() {
    // get a new topic map object from the factory.
    topicmapRef = factory.makeTopicMapReference();
    listener = new TesterListener();
    try {
      // load topic map
      TopicMapStoreIF store = topicmapRef.createStore(false);
      TopicMapIF tm = store.getTopicMap();
      EventManagerIF emanager = ((AbstractTopicMapStore)store).getEventManager();
      emanager.addListener(listener, "AssociationIF.addRole");    
      emanager.addListener(listener, "AssociationIF.addTheme");
      emanager.addListener(listener, "AssociationIF.removeRole");
      emanager.addListener(listener, "AssociationIF.removeTheme");
      emanager.addListener(listener, "AssociationIF.setType");
      emanager.addListener(listener, "AssociationRoleIF.setPlayer");
      emanager.addListener(listener, "AssociationRoleIF.setType");
      emanager.addListener(listener, "OccurrenceIF.addTheme");
      emanager.addListener(listener, "OccurrenceIF.removeTheme");
      emanager.addListener(listener, "OccurrenceIF.setDataType");
      emanager.addListener(listener, "OccurrenceIF.setType");
      emanager.addListener(listener, "OccurrenceIF.setValue");
      emanager.addListener(listener, "ReifiableIF.setReifier");
      emanager.addListener(listener, "TMObjectIF.addItemIdentifier");
      emanager.addListener(listener, "TMObjectIF.removeItemIdentifier");
      emanager.addListener(listener, "TopicIF.addOccurrence");    
      emanager.addListener(listener, "TopicIF.addSubjectIdentifier");    
      emanager.addListener(listener, "TopicIF.addSubjectLocator");    
      emanager.addListener(listener, "TopicIF.addTopicName");    
      emanager.addListener(listener, "TopicIF.addType");
      emanager.addListener(listener, "TopicIF.removeOccurrence");    
      emanager.addListener(listener, "TopicIF.removeSubjectIdentifier");    
      emanager.addListener(listener, "TopicIF.removeSubjectLocator");    
      emanager.addListener(listener, "TopicIF.removeTopicName");    
      emanager.addListener(listener, "TopicIF.removeType");
      emanager.addListener(listener, "TopicMapIF.addAssociation");    
      emanager.addListener(listener, "TopicMapIF.addTopic");    
      emanager.addListener(listener, "TopicMapIF.removeAssociation");
      emanager.addListener(listener, "TopicMapIF.removeTopic");
      emanager.addListener(listener, "TopicNameIF.addTheme");
      emanager.addListener(listener, "TopicNameIF.addVariant");
      emanager.addListener(listener, "TopicNameIF.removeTheme");
      emanager.addListener(listener, "TopicNameIF.removeVariant");
      emanager.addListener(listener, "TopicNameIF.setType");
      emanager.addListener(listener, "TopicNameIF.setValue");
      emanager.addListener(listener, "VariantNameIF.addTheme");
      emanager.addListener(listener, "VariantNameIF.removeTheme");
      emanager.addListener(listener, "VariantNameIF.setDataType");
      emanager.addListener(listener, "VariantNameIF.setValue");     
      topicmap = store.getTopicMap();

      // get the builder of that topic map.
      builder = topicmap.getBuilder();
      
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void tearDown() {
    // Inform the factory that the topic map is not needed anymore.
    topicmap.getStore().close();
    factory.releaseTopicMapReference(topicmapRef);
    // Reset the member variables.
    topicmap = null;
    builder = null;
  }
  
  // --- Test cases

  class Event {
    Object object;
    String event;
    Object new_value;
    Object old_value;
    Event(Object object, String event, Object new_value, Object old_value) {
      this.object = object;
      this.event = event;
      this.new_value = new_value;
      this.old_value = old_value;
    }
    public String toString() {
        return event + " " + object;
    }

    public boolean equals(Object o) {
      if (o instanceof Event) {
        Event oevent = (Event)o;
        // System.out.println(" " + this + " " + o + " " + object.equals(oevent.object) + " " + event.equals(oevent.event));
        return object.equals(oevent.object) && event.equals(oevent.event);
      }
      return false;
    }
  }

  class TesterListener implements EventListenerIF {
    List<Event> seenEvents = new ArrayList<Event>();

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
  
  public void testTopicLifecycle() {

    // --- topic events

    TopicIF topic = builder.makeTopic();

    // TopicIF.addSubjectLocator
    LocatorIF topic_subject_locator = Locators.getURILocator("topic:subject-locator");
    topic.addSubjectLocator(topic_subject_locator);

    // TopicIF.removeSubjectLocator
    topic.removeSubjectLocator(topic_subject_locator);

    // TopicIF.addSubjectIdentifier
    LocatorIF topic_subject_identifier = Locators.getURILocator("topic:subject-identifier");
    topic.addSubjectIdentifier(topic_subject_identifier);

    // TopicIF.removeSubjectIdentifier
    topic.removeSubjectIdentifier(topic_subject_identifier);

    // TopicIF.addItemIdentifier
    LocatorIF topic_item_identifier = Locators.getURILocator("topic:item-identifier");
    topic.addItemIdentifier(topic_item_identifier);
    
    // TopicIF.removeItemIdentifier
    topic.removeItemIdentifier(topic_item_identifier);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, "TopicMapIF.addTopic", topic, null), 
                    new Event(topic, "TopicIF.addSubjectLocator", topic_subject_locator, null), 
                    new Event(topic, "TopicIF.removeSubjectLocator", null, topic_subject_locator), 
                    new Event(topic, "TopicIF.addSubjectIdentifier", topic_subject_identifier, null), 
                    new Event(topic, "TopicIF.removeSubjectIdentifier", null, topic_subject_locator), 
                    new Event(topic, "TMObjectIF.addItemIdentifier", topic_item_identifier, null), 
                    new Event(topic, "TMObjectIF.removeItemIdentifier", null, topic_item_identifier)));

    // TopicIF.addType
    TopicIF topic_type = builder.makeTopic();
    topic.addType(topic_type);

    // TopicIF.removeType
    topic.removeType(topic_type);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, "TopicMapIF.addTopic", topic_type, null), 
                    new Event(topic, "TopicIF.addType", topic_type, null), 
                    new Event(topic, "TopicIF.removeType", null, topic_type)));

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
    LocatorIF tn_item_identifier = Locators.getURILocator("tn:item-identifier");
    tn.addItemIdentifier(tn_item_identifier);
    
    // TopicNameIF.removeItemIdentifier
    tn.removeItemIdentifier(tn_item_identifier);

    // TopicNameIF.addTheme
    TopicIF tn_theme = builder.makeTopic();
    tn.addTheme(tn_theme);

    // TopicNameIF.removeTheme
    tn.removeTheme(tn_theme);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, "TopicMapIF.addTopic", tn_type1, null), 
                    new Event(topic, "TopicIF.addTopicName", tn, null), 
                    new Event(tn, "TopicNameIF.setType", tn_type1, null), 
                    new Event(tn, "TopicNameIF.setValue", tn_tn1, null), 
                    new Event(tn, "TopicNameIF.setValue", tn_tn2, tn_tn1), 
                    new Event(topicmap, "TopicMapIF.addTopic", tn_type2, null), 
                    new Event(tn, "TopicNameIF.setType", tn_type2, tn_type1), 
                    new Event(tn, "TMObjectIF.addItemIdentifier", tn_item_identifier, null), 
                    new Event(tn, "TMObjectIF.removeItemIdentifier", null, tn_item_identifier), 
                    new Event(topicmap, "TopicMapIF.addTopic", tn_theme, null), 
                    new Event(tn, "TopicNameIF.addTheme", tn_theme, null), 
                    new Event(tn, "TopicNameIF.removeTheme", null, tn_theme)));

    // TopicNameIF.addVariant
    String vn_vn1 = "vn1";
    VariantNameIF vn = builder.makeVariantName(tn, vn_vn1);

    // VariantNameIF.setValue
    String vn_vn2 = "123";
    vn.setValue(vn_vn2, DataTypes.TYPE_INTEGER);

    // VariantNameIF.addItemIdentifier
    LocatorIF vn_item_identifier = Locators.getURILocator("vn:item-identifier");
    vn.addItemIdentifier(vn_item_identifier);
    
    // VariantNameIF.removeItemIdentifier
    vn.removeItemIdentifier(vn_item_identifier);

    // VariantNameIF.addTheme
    TopicIF vn_theme = builder.makeTopic();
    vn.addTheme(vn_theme);

    // VariantNameIF.removeTheme
    vn.removeTheme(vn_theme);

    listener.assertEvents(
      Arrays.asList(new Event(tn, "TopicNameIF.addVariant", vn, null), 
                    new Event(vn, "VariantNameIF.setDataType", DataTypes.TYPE_STRING, null), 
                    new Event(vn, "VariantNameIF.setValue", vn_vn1, null), 
                    new Event(vn, "VariantNameIF.setDataType", DataTypes.TYPE_INTEGER, null), 
                    new Event(vn, "VariantNameIF.setValue", vn_vn2, vn_vn1), 
                    new Event(vn, "TMObjectIF.addItemIdentifier", vn_item_identifier, null), 
                    new Event(vn, "TMObjectIF.removeItemIdentifier", null, vn_item_identifier), 
                    new Event(topicmap, "TopicMapIF.addTopic", vn_theme, null), 
                    new Event(vn, "VariantNameIF.addTheme", vn_theme, null), 
                    new Event(vn, "VariantNameIF.removeTheme", null, vn_theme)));

    // VariantNameIF.remove
    vn.remove();
    
    listener.assertEvents(
      Arrays.asList(new Event(tn, "TopicNameIF.removeVariant", vn, null)));
 
    // TopicNameIF.remove
    tn.remove();

    // WARN: TopicNameIF.removeVariant event if variants exist at this point

    listener.assertEvents(
      Arrays.asList(new Event(topic, "TopicIF.removeTopicName", tn, null))); 

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
    LocatorIF oc_item_identifier = Locators.getURILocator("oc:item-identifier");
    oc.addItemIdentifier(oc_item_identifier);
    
    // OccurrenceIF.removeItemIdentifier
    oc.removeItemIdentifier(oc_item_identifier);
 
    // OccurrenceIF.addTheme
    TopicIF oc_theme = builder.makeTopic();
    oc.addTheme(oc_theme);
 
    // OccurrenceIF.removeTheme
    oc.removeTheme(oc_theme);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, "TopicMapIF.addTopic", oc_type1, null), 
                    new Event(topic, "TopicIF.addOccurrence", oc, null), 
                    new Event(oc, "OccurrenceIF.setType", oc_type1, null), 
                    new Event(oc, "OccurrenceIF.setDataType", DataTypes.TYPE_STRING, null), 
                    new Event(oc, "OccurrenceIF.setValue", oc_oc1, null), 
                    new Event(oc, "OccurrenceIF.setDataType", DataTypes.TYPE_INTEGER, null), 
                    new Event(oc, "OccurrenceIF.setValue", oc_oc2, oc_oc1), 
                    new Event(topicmap, "TopicMapIF.addTopic", oc_type2, null), 
                    new Event(oc, "OccurrenceIF.setType", oc_type2, oc_type1), 
                    new Event(oc, "TMObjectIF.addItemIdentifier", oc_item_identifier, null), 
                    new Event(oc, "TMObjectIF.removeItemIdentifier", null, oc_item_identifier), 
                    new Event(topicmap, "TopicMapIF.addTopic", oc_theme, null), 
                    new Event(oc, "OccurrenceIF.addTheme", oc_theme, null), 
                    new Event(oc, "OccurrenceIF.removeTheme", null, oc_theme)));
 
    // OccurrenceIF.remove
    oc.remove();

    listener.assertEvents(
      Arrays.asList(new Event(topic, "TopicIF.removeOccurrence", oc, null))); 

    // TopicIF.remove
    topic.remove();

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, "TopicMapIF.removeTopic", null, topic))); 

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
    LocatorIF as_item_identifier = Locators.getURILocator("as:item-identifier");
    as.addItemIdentifier(as_item_identifier);
    
    // AssociaitonIF.removeItemIdentifier
    as.removeItemIdentifier(as_item_identifier);
 
    // AssociationIF.addTheme
    TopicIF as_theme = builder.makeTopic();
    as.addTheme(as_theme);
 
    // AssociationIF.removeTheme
    as.removeTheme(as_theme);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, "TopicMapIF.addTopic", as_type1, null), 
                    new Event(topicmap, "TopicMapIF.addAssociation", as, null), 
                    new Event(as, "AssociationIF.setType", as_type1, null), 
                    new Event(topicmap, "TopicMapIF.addTopic", ar1_type1, null), 
                    new Event(topicmap, "TopicMapIF.addTopic", ar1_player1, null), 
                    new Event(as, "AssociationIF.addRole", ar1, null), 
                    new Event(ar1, "AssociationRoleIF.setType", ar1_type1, null), 
                    new Event(ar1, "AssociationRoleIF.setPlayer", ar1_player1, null), 
                    new Event(topicmap, "TopicMapIF.addTopic", ar2_type1, null), 
                    new Event(topicmap, "TopicMapIF.addTopic", ar2_player1, null), 
                    new Event(as, "AssociationIF.addRole", ar2, null), 
                    new Event(ar2, "AssociationRoleIF.setType", ar2_type1, null), 
                    new Event(ar2, "AssociationRoleIF.setPlayer", ar2_player1, null), 
                    new Event(topicmap, "TopicMapIF.addTopic", as_type2, null), 
                    new Event(as, "AssociationIF.setType", as_type2, as_type1), 
                    new Event(as, "TMObjectIF.addItemIdentifier", as_item_identifier, null), 
                    new Event(as, "TMObjectIF.removeItemIdentifier", null, as_item_identifier), 
                    new Event(topicmap, "TopicMapIF.addTopic", as_theme, null), 
                    new Event(as, "AssociationIF.addTheme", as_theme, null), 
                    new Event(as, "AssociationIF.removeTheme", null, as_theme)));
 
    // AssociationRoleIF.setType
    TopicIF ar2_type2 = builder.makeTopic();
    ar2.setType(ar2_type2);
 
    // AssociationRoleIF.setPlayer
    TopicIF ar2_player2 = builder.makeTopic();
    ar2.setPlayer(ar2_player2);
 
    // AssociationRoleIF.addItemIdentifier
    LocatorIF ar2_item_identifier = Locators.getURILocator("ar:item-identifier");
    ar2.addItemIdentifier(ar2_item_identifier);
    
    // AssociationRoleIF.removeItemIdentifier
    ar2.removeItemIdentifier(ar2_item_identifier);

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, "TopicMapIF.addTopic", ar2_type2, null), 
                    new Event(ar2, "AssociationRoleIF.setType", ar2_type2, ar2_type1), 
                    new Event(topicmap, "TopicMapIF.addTopic", ar2_player2, null), 
                    new Event(ar2, "AssociationRoleIF.setPlayer", ar2_player2, ar2_player1), 
                    new Event(ar2, "TMObjectIF.addItemIdentifier", ar2_item_identifier, null), 
                    new Event(ar2, "TMObjectIF.removeItemIdentifier", null, ar2_item_identifier)));
 
    // AssociationRoleIF.remove
    ar2.remove();

    listener.assertEvents(
      Arrays.asList(new Event(as, "AssociationIF.removeRole", null, ar2))); 
 
    // AssociationIF.remove
    as.remove();

    listener.assertEvents(
      Arrays.asList(new Event(topicmap, "TopicMapIF.removeAssociation", null, as))); 
    
  }

}
