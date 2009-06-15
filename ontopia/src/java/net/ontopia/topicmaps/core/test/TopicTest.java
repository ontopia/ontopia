
// $Id: TopicTest.java,v 1.25 2008/06/13 08:17:51 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import java.net.MalformedURLException;
import junit.framework.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

public class TopicTest extends AbstractTMObjectTest {
  protected TopicIF topic;
  
  public TopicTest(String name) {
    super(name);
  }
    
  // --- Test cases

  public void testParentTopicMap() {
    assertTrue("parent topic map is not correct",
               topic.getTopicMap() == topicmap);
  }

  public void testSubject() {
    assertTrue("subject not null initially", topic.getSubjectLocators().isEmpty());

    try {
      URILocator loc = new URILocator("http://www.ontopia.net/");
      topic.addSubjectLocator(loc);
      assertTrue("subject not set properly", topic.getSubjectLocators().contains(loc));

      topic.removeSubjectLocator(loc);
      assertTrue("couldn't set subject to null", topic.getSubjectLocators().isEmpty());
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) URL malformed");
    }
    catch (ConstraintViolationException e) {
      fail("constraint violated for no good reason");
    }
  }

  public void testSubjectUnassignable() {
    TopicIF topic = builder.makeTopic();
    topic.remove();
    try {
      URILocator loc = new URILocator("http://www.ontopia.net");
      topic.addSubjectLocator(loc);
      fail("subject assigned when not attached to topic map");
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) URL malformed");
    }
    catch (ConstraintViolationException e) {
    }
  }
    
  public void testDuplicateSubject() {
    try {
      URILocator loc = new URILocator("http://www.ontopia.net");
      topic.addSubjectLocator(loc);

      TopicIF topic2 = builder.makeTopic();
      try {
        topic2.addSubjectLocator(loc);
        fail("duplicate subject allowed");
      }
      catch (ConstraintViolationException e) {
      }
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) URL malformed");
    }
    catch (ConstraintViolationException e) {
      fail("constraint violated for no good reason");
    }
  }
    
  public void testTypes() {
    // STATE 1
    assertTrue("type set not empty initially",
               topic.getTypes().size() == 0);

    // STATE 2
    TopicIF type = builder.makeTopic();
    topic.addType(type);

    assertTrue("type not added",
               topic.getTypes().size() == 1);

    assertTrue("type identity not retained",
               topic.getTypes().iterator().next().equals(type));

    topic.addType(type);

    assertTrue("duplicate not rejected",
               topic.getTypes().size() == 1);

    // STATE 2
    topic.removeType(type);
        
    assertTrue("type not removed",
               topic.getTypes().size() == 0);

    // verify that it's safe
    topic.removeType(type);
  }
  
  public void testOccurrences() {
    // STATE 1
    assertTrue("occurrence set not empty initially",
               topic.getOccurrences().size() == 0);

    // STATE 2
		TopicIF otype = builder.makeTopic();
    OccurrenceIF occurrence = builder.makeOccurrence(topic, otype, "");
    // added by builder

    assertTrue("occurrence not added",
               topic.getOccurrences().size() == 1);

    assertTrue("occurrence identity not retained",
               topic.getOccurrences().iterator().next().equals(occurrence));

    // STATE 2
    occurrence.remove();
    assertTrue("occurrence not removed",
               topic.getOccurrences().size() == 0);

    // verify that it's safe
    occurrence.remove();
  }
    
  public void testTopicNames() {
    // STATE 1
    assertTrue("basename set not empty initially",
               topic.getTopicNames().size() == 0);

    // STATE 2
    TopicNameIF basename = builder.makeTopicName(topic, "");
    // added by builder

    assertTrue("basename not added",
               topic.getTopicNames().size() == 1);

    assertTrue("basename identity not retained",
               topic.getTopicNames().iterator().next().equals(basename));

    // STATE 2
    basename.remove();
    assertTrue("basename not removed",
               topic.getTopicNames().size() == 0);

    // verify that it's safe
    basename.remove();
  }

  public void testRoles() {
    // STATE 1
    assertTrue("role set not empty initially",
               topic.getRoles().size() == 0);

    // STATE 2
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role = builder.makeAssociationRole(assoc, builder.makeTopic(), topic);

    assertTrue("role not added",
               topic.getRoles().size() == 1);

    assertTrue("role identity not retained",
               topic.getRoles().iterator().next().equals(role));

    role.setPlayer(topic);
    assertTrue("duplicate not rejected",
               topic.getRoles().size() == 1);

		try {
			role.setPlayer(null);
			fail("player could be set to null");
		} catch (NullPointerException e) {
		}
    assertTrue("player not retained", role.getPlayer().equals(topic));
  }
  
  public void testRolesByType() {
    TopicIF type = builder.makeTopic();
        
    assertTrue("roles by non-existent type initially not empty",
               topic.getRolesByType(type).size() == 0);

    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());

    // builder adds role to assoc
    AssociationRoleIF role = builder.makeAssociationRole(assoc, type, topic);

    assertTrue("roles of correct type not found",
               topic.getRolesByType(type).size() == 1);

    // builder adds role to assoc
    TopicIF other = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, rtype, other);

    assertTrue("role with no type found",
               topic.getRolesByType(type).size() == 1);

    assertTrue("role with no type not found",
               other.getRolesByType(rtype).size() == 1);
  }
    
  public void testSubjectIndicators() {
    // STATE 1
    assertTrue("indicator set not empty initially",
               topic.getSubjectIdentifiers().size() == 0);

    URILocator indicator = null;
    try {
      // STATE 2
      indicator = new URILocator("ftp://ftp.ontopia.net");
      topic.addSubjectIdentifier(indicator);
            
      assertTrue("indicator not added",
                 topic.getSubjectIdentifiers().size() == 1);
            
      assertTrue("indicator identity not retained",
                 topic.getSubjectIdentifiers().iterator().next().equals(indicator));
            
      topic.addSubjectIdentifier(indicator);       
      assertTrue("duplicate not rejected",
                 topic.getSubjectIdentifiers().size() == 1);
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given");
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException");
    }

    // STATE 3
    topic.removeSubjectIdentifier(indicator);
    assertTrue("indicator not removed",
               topic.getSubjectIdentifiers().size() == 0);

    // verify that it's safe
    topic.removeSubjectIdentifier(indicator);
  }
    
  public void testSubjectIndicatorUnassignable() {
    TopicIF topic = builder.makeTopic();
    topic.remove();

    try {
      URILocator loc = new URILocator("http://www.ontopia.net");
      topic.addSubjectIdentifier(loc);
      fail("subject indicator assigned when not attached to topic map");
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) URL malformed");
    }
    catch (ConstraintViolationException e) {
    }
  }

  public void testSubjectIndicatorDuplicate() {
    TopicIF t1 = builder.makeTopic();
    TopicIF t2 = builder.makeTopic();

    try {
      URILocator loc = new URILocator("http://www.ontopia.net");
      t1.addSubjectIdentifier(loc);

      try {
        t2.addSubjectIdentifier(loc);
        fail("accepted two subject indicators being set to the same URI");
      }
      catch (ConstraintViolationException e) {
      }
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) URL malformed");
    }
    catch (ConstraintViolationException e) {
      fail("constraint violated for no good reason");
    }
  }

  public void testTopicSubjectIndicatorSourceLocator() {
    // this is overridden from AbstractTMObject, because in this case
    // the collision is not allowed, but for all other TMObjectIFs it
    // is allowed (to provide reification)
    try {
      URILocator loc = new URILocator("http://www.ontopia.net");

      TopicIF topic = builder.makeTopic();                    
      object.addItemIdentifier(loc);
                        
      topic.addSubjectIdentifier(loc);
      fail("allowed subject indicator of one topic to be source locator of another");
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given" + e);
    }
    catch (ConstraintViolationException e) {
    }
  }

  public void testSourceLocatorTopicSubjectIndicator() {
    // this is overridden from AbstractTMObject, because in this case
    // the collision is not allowed, but for all other TMObjectIFs it
    // is allowed (to provide reification)
    try {
      URILocator loc = new URILocator("http://www.ontopia.net");

      TopicIF topic2 = builder.makeTopic();
      topic2.addSubjectIdentifier(loc);
                        
      topic.addItemIdentifier(loc);
      fail("allowed subject indicator of one topic to be source locator of another");
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given" + e);
    }
    catch (ConstraintViolationException e) {
    }
  }

  public void testBug652a() {
    try {
      TopicIF topic = builder.makeTopic();
      URILocator loc = new URILocator("http://www.ontopia.net/A");
      topic.addItemIdentifier(loc);
      topic.addSubjectIdentifier(loc);
      topic.removeSubjectIdentifier(loc);
      topic.removeItemIdentifier(loc);
      topic.remove();
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given" + e);
    }
  }

  public void testBug652b() {
    try {
      TopicIF topic = builder.makeTopic();
      URILocator loc = new URILocator("http://www.ontopia.net/B");
      topic.addSubjectIdentifier(loc);
      topic.addItemIdentifier(loc);
      topic.removeItemIdentifier(loc);
      topic.removeSubjectIdentifier(loc);
      topic.remove();
    } 
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given" + e);
    }
  }

  // --- Internal methods

  public void setUp() {
    super.setUp();
    topic = builder.makeTopic();
    parent = topicmap;
    object = topic;
  }

  protected TMObjectIF makeObject() {
    return builder.makeTopic();
  }

}
