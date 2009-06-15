
// $Id: AbstractTMObjectTest.java,v 1.26 2008/06/13 08:36:25 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import java.net.MalformedURLException;
import junit.framework.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

public abstract class AbstractTMObjectTest extends AbstractTopicMapTest {
  protected static final String MSG_NULL_ARGUMENT = "null is not a valid argument.";

  protected TopicMapReferenceIF topicmapRef;
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TMObjectIF    object;      // object being tested
  protected TMObjectIF    parent;      // parent of object being tested, if any
  protected TopicMapBuilderIF builder; // builder used for creating new objects

  public AbstractTMObjectTest(String name) {
    super(name);
  }


  public void setUp() {
    // Get a new topic map object from the factory.
    topicmapRef = factory.makeTopicMapReference();
    try {
      topicmap = topicmapRef.createStore(false).getTopicMap();
      // Get the builder of that topic map.
      builder = topicmap.getBuilder();
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void tearDown() {
    if (topicmapRef != null) {
      // Inform the factory that the topic map is not needed anymore.
      topicmap.getStore().close();
      factory.releaseTopicMapReference(topicmapRef);
      // Reset the member variables.
      topicmapRef = null;
      topicmap = null;
      builder = null;
    }
  }

  protected abstract TMObjectIF makeObject();

  // --- Test cases

  public void testObjectId() {
    assertTrue("object id not null", object.getObjectId() != null);
    assertTrue("object not found by id",
           topicmap.getObjectById(object.getObjectId()).equals(object));
  }

  //! public void _testObjectIdUnassignable() { // Note: this test is really specific to the basic implementation
  //!   TMObjectIF object = makeParentlessObject();
  //!   if (object == null) return; // makes no sense for topic maps :)
  //!   try {
  //!     fail("could assign id even with no topic map: " +
  //!          object.getObjectId());
  //!   }
  //!   catch (OntopiaRuntimeException e) {
  //!   }
  //! }

  public void testEquals() {
    assertTrue("object does not equal itself", object.equals(object));

    TMObjectIF other = makeObject();
    if (other != null) {
      assertTrue("object equals other objects",
                 !object.equals(other));
    }
  }

  public void testDuplicateSourceLocator() {
    try {
      URILocator loc = new URILocator("http://www.ontopia.net");
      object.addItemIdentifier(loc);

      // if this is a topic map we stop, since new object will be
      // another topic map
      if (object instanceof TopicMapIF) return;
      TMObjectIF object2 = makeObject();
      if (object2 != null) {
        try {
          object2.addItemIdentifier(loc);
          fail("duplicate source locator allowed");
        }
        catch (ConstraintViolationException e) {
        }
      }
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given" + e);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException");
    }
  }

  public void testTopicSubjectIndicatorSourceLocator() {
    try {
      URILocator loc = new URILocator("http://www.ontopia.net");

      TopicIF topic = builder.makeTopic();                      
      object.addItemIdentifier(loc);
                        
      topic.addSubjectIdentifier(loc);
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given" + e);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException");
    }
  }

  public void testSourceLocatorTopicSubjectIndicator() {
    try {
      URILocator loc = new URILocator("http://www.ontopia.net");

      TopicIF topic = builder.makeTopic();
      topic.addSubjectIdentifier(loc);
                        
      object.addItemIdentifier(loc);
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given" + e);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException");
    }
  }
        
  //! public void _testSourceLocatorUnassignable() {
  //!   try {
  //!     URILocator loc = new URILocator("http://www.opera.com");
  //!     TMObjectIF object = makeParentlessObject();
  //!     if (object == null) return; // makes no sense for topic maps :)
  //!     object.addItemIdentifier(loc);
  //!     fail("source locator allowed when not in topic map");
  //!   }
  //!   catch (ConstraintViolationException e) {
  //!   }
  //!   catch (MalformedURLException e) {
  //!     fail("(INTERNAL) bad URL given" + e);
  //!   }
  //! }

  public void testSourceLocators() {
    // STATE 1: has no source locator
    assertTrue("empty object has source locators",
           object.getItemIdentifiers().size() == 0);

    try {
      // STATE 2: has only one source locator
      URILocator loc = new URILocator("http://www.ontopia.net");
      object.addItemIdentifier(loc);
        
      assertTrue("source locator not added",
             object.getItemIdentifiers().size() == 1);
        
      assertTrue("source locator not added",
             object.getItemIdentifiers().iterator().next().equals(loc));

      TMObjectIF found = topicmap.getObjectByItemIdentifier(loc);
      assertTrue("can't look up by resid", found.equals(object));

      found = topicmap.getObjectByItemIdentifier(new URILocator("http://www.ontopia.net"));
      assertTrue("can't look up by resid", found.equals(object));
        
      object.addItemIdentifier(loc);
      assertTrue("duplicate source locator not rejected",
             object.getItemIdentifiers().size() == 1);

      // STATE 3: has no source locator
      object.removeItemIdentifier(loc);
      assertTrue("source locator not removed",
             object.getItemIdentifiers().size() == 0);

      found = topicmap.getObjectByItemIdentifier(loc);
      assertTrue("can still look up by resid", found == null);
        
      // removing locator that is not present works OK
      object.removeItemIdentifier(loc);
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given" + e);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException");
    }
  }

  public void testMoveSourceLocators() { // bug #273
    try {
      URILocator loc = new URILocator("http://www.ontopia.net");
      TopicIF topic = builder.makeTopic();
      object.addItemIdentifier(loc);

      // we do this here to make the RDBMS backend write the
      // changes in the transaction to the DB
      URILocator loc2 = new URILocator("http://www.ontopia.com");
      TMObjectIF lookedup = topicmap.getObjectByItemIdentifier(loc2);
      // </end>
      
      // ready to blow us up
      object.removeItemIdentifier(loc);
      topic.addItemIdentifier(loc);

      lookedup = topicmap.getObjectByItemIdentifier(loc);
      assertTrue("wrong object returned on lookup: " + lookedup,
             lookedup == topic);
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given" + e);
    }
  }
  
  public void testIsModifiable() {
    assertTrue("is not modifiable", !object.isReadOnly());
  }

  public void testTopicMap() {
    assertTrue("has the wrong topic map", object.getTopicMap().equals(topicmap));
  }


}





