
// $Id: TopicModificationTests.java,v 1.14 2008/06/13 08:36:25 geir.gronmo Exp $

package net.ontopia.topicmaps.core.events.test;

import java.io.File;
import java.util.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.test.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.events.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.ImportExportUtils;
  
public class TopicModificationTests extends AbstractTopicMapTest {

  protected TopicMapReferenceIF topicmapRef;
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TopicIF bart;
  protected TopicMapBuilderIF builder; // builder used for creating new objects
  protected TesterListener listener;
  
  public TopicModificationTests(String name) {
    super(name);
  }
  
  public void setUp() {
    // get a new topic map object from the factory.
    topicmapRef = factory.makeTopicMapReference();
    listener = new TesterListener();
    TopicMapEvents.addTopicListener(topicmapRef, listener);
    try {
      // load topic map
      topicmap = topicmapRef.createStore(false).getTopicMap();
      ImportExportUtils.getImporter(resolveFileName("various", "bart.ltm")).importInto(topicmap);
      topicmap.getStore().commit();
      
      // get the builder of that topic map.
      builder = topicmap.getBuilder();

      // get test topic
      bart = topicmap.getTopicBySubjectIdentifier(Locators.getURILocator("test:bart"));
      
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void tearDown() {
    // Inform the factory that the topic map is not needed anymore.
    topicmap.getStore().close();
    TopicMapEvents.removeTopicListener(topicmapRef, listener);
    factory.releaseTopicMapReference(topicmapRef);
    // Reset the member variables.
    topicmap = null;
    builder = null;
  }
  
  // --- Test cases

  class TesterListener implements TopicMapListenerIF {
    Collection snapshots = new HashSet();
    
    public void objectAdded(TMObjectIF o) {
    }

    public void objectModified(TMObjectIF snapshot) {      
      this.snapshots.add(((TopicIF)snapshot).getObjectId());
    }
    
    public void objectRemoved(TMObjectIF o) {
    }

    public void reset() {
      snapshots.clear();
    }
  }

  protected void beforeTest() {
    listener.reset();
    assertTrue("listener not properly reset", listener.snapshots.isEmpty());
  }
  
  protected void afterTest() {
    topicmap.getStore().commit();
    assertTrue("topic was not registered as modified", listener.snapshots.contains(bart.getObjectId()));
  }
  
  public void testTopicLifecycle() {
    assertTrue("Could not find topic.", bart != null);

    // TopicIF.addSubjectLocator
    beforeTest();
    bart.addSubjectLocator(Locators.getURILocator("x:subject-locator"));
    afterTest();

    // TopicIF.removeSubjectLocator
    beforeTest();
    bart.removeSubjectLocator(Locators.getURILocator("x:subject-locator"));
    afterTest();

    // TopicIF.addSubjectIdentifier
    beforeTest();
    bart.addSubjectIdentifier(Locators.getURILocator("x:subject-indicator"));
    afterTest();

    // TopicIF.removeSubjectIdentifier
    beforeTest();
    bart.removeSubjectIdentifier(Locators.getURILocator("x:subject-indicator"));
    afterTest();

    // TopicIF.addTopicName
    beforeTest();
    TopicNameIF bn = builder.makeTopicName(bart, "");
    afterTest();

    // TopicNameIF.remove
    beforeTest();
    bn.remove();
    afterTest();

    // TopicIF.addOccurrence
    beforeTest();
    OccurrenceIF oc = builder.makeOccurrence(bart, bart, "");
    afterTest();

    // OccurrenceIF.remove
    beforeTest();
    oc.remove();
    afterTest();

    // TopicIF.addType
    beforeTest();
    TopicIF other = topicmap.getTopicBySubjectIdentifier(Locators.getURILocator("test:other"));
    bart.addType(other);
    afterTest();

    // TopicIF.removeType
    beforeTest();
    bart.removeType(other);
    afterTest();

    // TopicIF.addItemIdentifier
    beforeTest();
    bart.addItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();
    
    // TopicIF.removeItemIdentifier
    beforeTest();
    bart.removeItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();

    // -----------------------------------------------------------------------------
    
    // TopicNameIF.setValue
    beforeTest();
    bn = (TopicNameIF)bart.getTopicNames().iterator().next();
    bn.setValue("New name");
    afterTest();

    // TopicNameIF.addItemIdentifier
    beforeTest();
    bn.addItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();
    
    // TopicNameIF.removeItemIdentifier
    beforeTest();
    bn.removeItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();

    // TopicNameIF.addTheme
    beforeTest();
    bn.addTheme(other);
    afterTest();

    // TopicNameIF.removeTheme
    beforeTest();
    bn.removeTheme(other);
    afterTest();

    // TopicNameIF.setType
    beforeTest();
    bn.setType(other);
    afterTest();

    // TopicNameIF.addVariant
    beforeTest();
    VariantNameIF vn = builder.makeVariantName(bn, "");
    afterTest();

    // VariantNameIF.setValue
    beforeTest();
    vn.setValue("New variant");
    afterTest();

    // VariantNameIF.setLocator
    beforeTest();
    vn.setLocator(Locators.getURILocator("x:variant-locator"));
    afterTest();

    // VariantNameIF.addItemIdentifier
    beforeTest();
    vn.addItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();
    
    // VariantNameIF.removeItemIdentifier
    beforeTest();
    vn.removeItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();

    // VariantNameIF.addTheme
    beforeTest();
    vn.addTheme(other);
    afterTest();

    // VariantNameIF.removeTheme
    beforeTest();
    vn.removeTheme(other);
    afterTest();

    // VariantNameIF.remove
    beforeTest();
    vn.remove();
    afterTest();

    // -----------------------------------------------------------------------------

    // OccurrenceIF.setValue
    beforeTest();
    oc = (OccurrenceIF)bart.getOccurrences().iterator().next();
    oc.setValue("New value");
    afterTest();
    
    // OccurrenceIF.setLocator
    beforeTest();
    oc.setLocator(Locators.getURILocator("x:occurrence-locator"));
    afterTest();

    // OccurrenceIF.addItemIdentifier
    beforeTest();
    oc.addItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();
    
    // OccurrenceIF.removeItemIdentifier
    beforeTest();
    oc.removeItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();

    // OccurrenceIF.addTheme
    beforeTest();
    oc.addTheme(other);
    afterTest();

    // OccurrenceIF.removeTheme
    beforeTest();
    oc.removeTheme(other);
    afterTest();

    // OccurrenceIF.setType
    beforeTest();
    oc.setType(other);
    afterTest();

    // -----------------------------------------------------------------------------

    // AssociationRoleIF.setPlayer
    beforeTest();
    AssociationRoleIF ar = (AssociationRoleIF)bart.getRoles().iterator().next();
    AssociationIF as = ar.getAssociation();
    AssociationRoleIF or = null;
    Iterator iter = as.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF)iter.next();
      if (!role.equals(ar)) {
        or = role;
        break;
      }
    }
    TopicIF springfield = or.getPlayer();
    or.setPlayer(or.getType());
    topicmap.getStore().commit();
    assertTrue("bart was not registered as modified", listener.snapshots.contains(bart.getObjectId()));
    assertTrue("springfield was not registered as modified", listener.snapshots.contains(springfield.getObjectId()));

    // AssociationIF.addItemIdentifier
    beforeTest();
    as.addItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();
    
    // AssociationIF.removeItemIdentifier
    beforeTest();
    as.removeItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();

    // AssociationRoleIF.addItemIdentifier
    beforeTest();
    ar.addItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();
    
    // AssociationRoleIF.removeItemIdentifier
    beforeTest();
    ar.removeItemIdentifier(Locators.getURILocator("x:source-locator"));
    afterTest();
    
    // AssociationIF.addTheme
    beforeTest();
    as.addTheme(other);
    afterTest();
    
    // AssociationIF.removeTheme
    beforeTest();
    as.removeTheme(other);
    afterTest();

    // AssociationIF.setType
    beforeTest();
    as.setType(other);
    afterTest();

    // AssociationRoleIF.setType
    beforeTest();
    ar.setType(other);
    afterTest();

    // AssociationIF.addRole
    beforeTest();
    AssociationRoleIF xr = builder.makeAssociationRole(as, builder.makeTopic(), builder.makeTopic());
    afterTest();

    // AssociationRoleIF.remove
    beforeTest();
    xr.remove();
    afterTest();

    // AssociationIF.addRole
    beforeTest();
    xr = builder.makeAssociationRole(as, builder.makeTopic(), builder.makeTopic());
    afterTest();

    // AssociationIF.remove
    beforeTest();
    xr.getAssociation().remove();
    afterTest();
    
  }

}
