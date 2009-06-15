
// $Id: TopicMapTest.java,v 1.28 2008/06/13 08:36:25 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import java.net.MalformedURLException;
import junit.framework.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

public class TopicMapTest extends AbstractTMObjectTest {

  protected TopicMapIF tm;
  // The expected exception message if NULL is passed to a function
  // which does not accept it.
  protected static String NULLPOINTERMESSAGE = "null is not a valid argument.";

  public TopicMapTest(String name) {
    super(name);
  }
    
  // --- Test cases

	public void testReification() {
		TopicIF reifier = builder.makeTopic();
		ReifiableIF reifiable = tm;

    assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    assertTrue("Topic reifying the reifiable was found",
							 reifiable.getReifier() == null);

		reifiable.setReifier(reifier);
    assertTrue("No topic reifying the reifiable was found",
							 reifiable.getReifier() == reifier);
    assertTrue("No object reified by the reifying topic was found",
							 reifier.getReified() == reifiable);

		reifiable.setReifier(null);
    assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    assertTrue("Topic reifying the first reifiable was found",
							 reifiable.getReifier() == null);
	}

  public void testAssociations() {
    // STATE 1
    assertTrue("association set not empty initially",
           tm.getAssociations().size() == 0);

    // STATE 2
    AssociationIF association = builder.makeAssociation(builder.makeTopic());
    // added by builder

    assertTrue("association not added",
           tm.getAssociations().size() == 1);

    assertTrue("association identity not retained",
           tm.getAssociations().iterator().next().equals(association));

    // STATE 2
    association.remove();
    assertTrue("association not removed",
           tm.getAssociations().size() == 0);

    // verify that it's safe
    association.remove();
  }
    
  public void testAssociationRemove() {
    TopicIF at = builder.makeTopic();
    TopicIF rt1 = builder.makeTopic();
    TopicIF rt2 = builder.makeTopic();
    TopicIF t1 = builder.makeTopic();
    TopicIF t2 = builder.makeTopic();
    AssociationIF association = builder.makeAssociation(at);
    AssociationRoleIF role1 = builder.makeAssociationRole(association, rt1, t1);
    AssociationRoleIF role2 = builder.makeAssociationRole(association, rt2, t2);

    association.remove();
    assertTrue("removing association from topic map does not remove child roles from their players",
           t1.getRoles().size() == 0);
    assertTrue("removing association from topic map does not remove child roles from their players",
           t2.getRoles().size() == 0);
  }
    
  public void testTopics() {
    // STATE 1
    assertTrue("topic set not empty initially",
           tm.getTopics().size() == 0);

    // STATE 2
    TopicIF topic = builder.makeTopic();
    // added by builder

    assertTrue("topic not added",
           tm.getTopics().size() == 1);

    assertTrue("topic identity not retained",
           tm.getTopics().iterator().next().equals(topic));

    // STATE 3
    topic.remove();
    assertTrue("topic not removed",
           tm.getTopics().size() == 0);
    assertTrue("topic topicMap property not reset after topic removed",
           topic.getTopicMap() == null);

    // verify that it's safe
    topic.remove();
  }

  public void testTopicBySubject() {
    try {
      TopicIF topic = builder.makeTopic();
      URILocator loc = new URILocator("http://www.ontopia.net");
      topic.addSubjectLocator(loc);

      TopicIF found = tm.getTopicBySubjectLocator(loc);
      assertTrue("topic not found by subject", found.equals(topic));

      topic.removeSubjectLocator(loc);
      found = tm.getTopicBySubjectLocator(loc);
      assertTrue("topic found by subject when it shouldn't be",
             found == null);

      topic.addSubjectLocator(loc);        
      assertTrue("topic not found by subject",
             tm.getTopicBySubjectLocator(loc).equals(topic));

      tm.remove();
      assertTrue("topic found by subject after it has been removed",
             tm.getTopicBySubjectLocator(loc) == null);            

      try {
        tm.getTopicBySubjectLocator(null);
        fail("getTopicBySubjectLocator() accepts null parameter.");
      } catch (NullPointerException ex) {
        // Expected
        assertTrue("Got a NullPointerException with an unexcpected message: " 
               + ex.getMessage(),
               ex.getMessage().equals(NULLPOINTERMESSAGE));
      }
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given");
    }
    catch (ConstraintViolationException e) {
      e.printStackTrace();
      fail("spurious ConstraintViolationException");
    }
  }

  public void testTopicByIndicator() {
    try {
      TopicIF topic = builder.makeTopic();
      URILocator loc = new URILocator("http://www.ontopia.net");
      topic.addSubjectIdentifier(loc);

      TopicIF found = tm.getTopicBySubjectIdentifier(loc);
      assertTrue("topic not found by indicator", found.equals(topic));

      topic.removeSubjectIdentifier(loc);
      found = tm.getTopicBySubjectIdentifier(loc);
      assertTrue("topic found by indicator when it shouldn't be",
             found == null);

      topic.addSubjectIdentifier(loc);       
      assertTrue("topic not found by indicator",
             tm.getTopicBySubjectIdentifier(loc).equals(topic));

      tm.remove();
      assertTrue("topic found by indicator after it has been removed",
             tm.getTopicBySubjectIdentifier(loc) == null);

      try {
        tm.getTopicBySubjectIdentifier(null);
        fail("getTopicBySubjectIdentifier accepts null parameter");
      } catch (NullPointerException e) {
        // Expected.
        assertTrue("Got a NullPointerException with an unexcpected message: " 
               + e.getMessage(),
               e.getMessage().equals(NULLPOINTERMESSAGE));
      }
                    
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given");
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException");
    }
  }

  public void testObjectBySourceLocator() {
    try {
      TopicIF topic = builder.makeTopic();
      URILocator loc = new URILocator("http://www.ontopia.net/topicmaptest.xtm#foo");
      topic.addItemIdentifier(loc);

      TopicIF found = (TopicIF)tm.getObjectByItemIdentifier(loc);
      assertTrue("topic not found by source locator", found.equals(topic));

      topic.removeItemIdentifier(loc);
      found = (TopicIF)tm.getObjectByItemIdentifier(loc);
      assertTrue("topic found by source locator when it shouldn't be",
             found == null);

      topic.addItemIdentifier(loc);          
      assertTrue("topic not found by source locator",
             tm.getObjectByItemIdentifier(loc).equals(topic));

      tm.remove();
      assertTrue("topic found by source locator after it has been removed",
             tm.getObjectByItemIdentifier(loc) == null);

      try {
        tm.getObjectByItemIdentifier(null);
        fail("getObjectByItemIdentifier accepts null parameter");
      } catch (NullPointerException e) {
        // Expected.
        assertTrue("Got a NullPointerException with an unexcpected message: " 
               + e.getMessage(),
               e.getMessage().equals(NULLPOINTERMESSAGE));
      }
                    
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) bad URL given");
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException");
    }
  }

  public void testTopicRemovalPolicyRole() {
    TopicIF topic = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role1 = builder.makeAssociationRole(assoc, builder.makeTopic(), topic);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, builder.makeTopic(), other);

    topic.remove();

    assertTrue("association not deleted", assoc.getTopicMap() == null);
    assertTrue("role1 not deleted", role1.getTopicMap() == null);
    assertTrue("role2 not deleted", role2.getTopicMap() == null);
    assertTrue("remaining topic has roles left", other.getRoles().size() == 0);
  }

  public void testTopicRemovalPolicyTypeUse() {
    //! System.out.println("TM: " + tm.getTopics());
    //! System.out.println("--0");
    TopicIF topic = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    //! System.out.println("--1");
    other.addType(topic);
    //! System.out.println("--2");
    topic.remove();
  }

  public void testObjectById() {
    TopicIF topic = builder.makeTopic();
    String id = topic.getObjectId();
    assertTrue("Could not locate object by ID (" + id + ")", tm.getObjectById(id) != null);
    assertTrue("Found wrong object type by ID (" + id + ")", tm.getObjectById(id) instanceof TopicIF);
    assertTrue("Found wrong object: " + tm.getObjectById(id).getObjectId() + " - " + topic.getObjectId(), 
           tm.getObjectById(id).getObjectId().equals(topic.getObjectId()));

    topic.remove();

    //! System.out.println("------------------------->" + topic);
    //! System.out.println("--" + topic.getTopicMap());
    //! System.out.println("--" + tm.getObjectById(id) + " " + System.identityHashCode(tm.getObjectById(id)));
    //! System.out.println("--" + tm.getObjectById(id).getTopicMap());
    assertTrue("Found topic by ID after it was removed",
           tm.getObjectById(id) == null);
    
    try {
      tm.getObjectById(null);
      fail("getObjectById accepts null parameter.");
    } catch (NullPointerException ex) {
      // Expected.
      assertTrue("Got a NullPointerException with an unexcpected message: " 
             + ex.getMessage(),
             ex.getMessage().equals(NULLPOINTERMESSAGE));
    }
  }

  public void testObjectByNonNumericId() {
    assertTrue("Found object by non-sensical ID 'bongo'",
               tm.getObjectById("Bongo") == null);
  }
  
  // --- Internal methods

  public void setUp() {
    super.setUp();
    tm = topicmap;
    object = tm;
  }

  protected TMObjectIF makeObject() {
    return null;
  }
  
}
