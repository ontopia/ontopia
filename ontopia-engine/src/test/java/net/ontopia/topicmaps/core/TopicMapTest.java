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

package net.ontopia.topicmaps.core;

import net.ontopia.infoset.impl.basic.URILocator;
import org.junit.Assert;
import org.junit.Test;

public abstract class TopicMapTest extends AbstractTMObjectTest {

  protected TopicMapIF tm;

  // --- Test cases

	@Test
	public void testReification() {
		TopicIF reifier = builder.makeTopic();
		ReifiableIF reifiable = tm;

    Assert.assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    Assert.assertTrue("Topic reifying the reifiable was found",
							 reifiable.getReifier() == null);

		reifiable.setReifier(reifier);
    Assert.assertTrue("No topic reifying the reifiable was found",
							 reifiable.getReifier() == reifier);
    Assert.assertTrue("No object reified by the reifying topic was found",
							 reifier.getReified() == reifiable);

		reifiable.setReifier(null);
    Assert.assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    Assert.assertTrue("Topic reifying the first reifiable was found",
							 reifiable.getReifier() == null);
	}

  @Test
  public void testAssociations() {
    // STATE 1
    Assert.assertTrue("association set not empty initially",
           tm.getAssociations().size() == 0);

    // STATE 2
    AssociationIF association = builder.makeAssociation(builder.makeTopic());
    // added by builder

    Assert.assertTrue("association not added",
           tm.getAssociations().size() == 1);

    Assert.assertTrue("association identity not retained",
           tm.getAssociations().iterator().next().equals(association));

    // STATE 2
    association.remove();
    Assert.assertTrue("association not removed",
           tm.getAssociations().size() == 0);

    // verify that it's safe
    association.remove();
  }
    
  @Test
  public void testAssociationRemove() {
    TopicIF at = builder.makeTopic();
    TopicIF rt1 = builder.makeTopic();
    TopicIF rt2 = builder.makeTopic();
    TopicIF t1 = builder.makeTopic();
    TopicIF t2 = builder.makeTopic();
    AssociationIF association = builder.makeAssociation(at);
    builder.makeAssociationRole(association, rt1, t1);
    builder.makeAssociationRole(association, rt2, t2);

    association.remove();
    Assert.assertTrue("removing association from topic map does not remove child roles from their players",
           t1.getRoles().size() == 0);
    Assert.assertTrue("removing association from topic map does not remove child roles from their players",
           t2.getRoles().size() == 0);
  }
    
  @Test
  public void testTopics() {
    // STATE 1
    Assert.assertTrue("topic set not empty initially",
           tm.getTopics().size() == 0);

    // STATE 2
    TopicIF topic = builder.makeTopic();
    // added by builder

    Assert.assertTrue("topic not added",
           tm.getTopics().size() == 1);

    Assert.assertTrue("topic identity not retained",
           tm.getTopics().iterator().next().equals(topic));

    // STATE 3
    topic.remove();
    Assert.assertTrue("topic not removed",
           tm.getTopics().size() == 0);
    Assert.assertTrue("topic topicMap property not reset after topic removed",
           topic.getTopicMap() == null);

    // verify that it's safe
    topic.remove();
  }

  @Test
  public void testTopicBySubject() {
    try {
      TopicIF topic = builder.makeTopic();
      URILocator loc = URILocator.create("http://www.ontopia.net");
      topic.addSubjectLocator(loc);

      TopicIF found = tm.getTopicBySubjectLocator(loc);
      Assert.assertTrue("topic not found by subject", found.equals(topic));

      topic.removeSubjectLocator(loc);
      found = tm.getTopicBySubjectLocator(loc);
      Assert.assertTrue("topic found by subject when it shouldn't be",
             found == null);

      topic.addSubjectLocator(loc);        
      Assert.assertTrue("topic not found by subject",
             tm.getTopicBySubjectLocator(loc).equals(topic));

      tm.remove();
      Assert.assertTrue("topic found by subject after it has been removed",
             tm.getTopicBySubjectLocator(loc) == null);            

      try {
        tm.getTopicBySubjectLocator(null);
        Assert.fail("getTopicBySubjectLocator() accepts null parameter.");
      } catch (NullPointerException ex) {
        // Expected
      }
    }
    catch (ConstraintViolationException e) {
      e.printStackTrace();
      Assert.fail("spurious ConstraintViolationException");
    }
  }

  @Test
  public void testTopicByIndicator() {
    try {
      TopicIF topic = builder.makeTopic();
      URILocator loc = URILocator.create("http://www.ontopia.net");
      topic.addSubjectIdentifier(loc);

      TopicIF found = tm.getTopicBySubjectIdentifier(loc);
      Assert.assertTrue("topic not found by indicator", found.equals(topic));

      topic.removeSubjectIdentifier(loc);
      found = tm.getTopicBySubjectIdentifier(loc);
      Assert.assertTrue("topic found by indicator when it shouldn't be",
             found == null);

      topic.addSubjectIdentifier(loc);       
      Assert.assertTrue("topic not found by indicator",
             tm.getTopicBySubjectIdentifier(loc).equals(topic));

      tm.remove();
      Assert.assertTrue("topic found by indicator after it has been removed",
             tm.getTopicBySubjectIdentifier(loc) == null);

      try {
        tm.getTopicBySubjectIdentifier(null);
        Assert.fail("getTopicBySubjectIdentifier accepts null parameter");
      } catch (NullPointerException e) {
        // Expected.
      }
                    
    }
    catch (ConstraintViolationException e) {
      Assert.fail("spurious ConstraintViolationException");
    }
  }

  @Test
  public void testObjectBySourceLocator() {
    try {
      TopicIF topic = builder.makeTopic();
      URILocator loc = URILocator.create("http://www.ontopia.net/topicmaptest.xtm#foo");
      topic.addItemIdentifier(loc);

      TopicIF found = (TopicIF)tm.getObjectByItemIdentifier(loc);
      Assert.assertTrue("topic not found by source locator", found.equals(topic));

      topic.removeItemIdentifier(loc);
      found = (TopicIF)tm.getObjectByItemIdentifier(loc);
      Assert.assertTrue("topic found by source locator when it shouldn't be",
             found == null);

      topic.addItemIdentifier(loc);          
      Assert.assertTrue("topic not found by source locator",
             tm.getObjectByItemIdentifier(loc).equals(topic));

      tm.remove();
      Assert.assertTrue("topic found by source locator after it has been removed",
             tm.getObjectByItemIdentifier(loc) == null);

      try {
        tm.getObjectByItemIdentifier(null);
        Assert.fail("getObjectByItemIdentifier accepts null parameter");
      } catch (NullPointerException e) {
        // Expected.
      }
                    
    }
    catch (ConstraintViolationException e) {
      Assert.fail("spurious ConstraintViolationException");
    }
  }

  @Test
  public void testTopicRemovalPolicyRole() {
    TopicIF topic = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role1 = builder.makeAssociationRole(assoc, builder.makeTopic(), topic);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, builder.makeTopic(), other);

    topic.remove();

    Assert.assertTrue("association not deleted", assoc.getTopicMap() == null);
    Assert.assertTrue("role1 not deleted", role1.getTopicMap() == null);
    Assert.assertTrue("role2 not deleted", role2.getTopicMap() == null);
    Assert.assertTrue("remaining topic has roles left", other.getRoles().size() == 0);
  }

  @Test
  public void testTopicRemovalPolicyTypeUse() {
    //! System.out.println("TM: " + tm.getTopics());
    //! System.out.println("--0");
    TopicIF topic = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    //! System.out.println("--1");
    other.addType(topic);
    //! System.out.println("--2");
    topic.remove();
    
    Assert.assertEquals("To many topics after remove", 0, topicmap.getTopics().size());
  }

  @Test
  public void testObjectById() {
    TopicIF topic = builder.makeTopic();
    String id = topic.getObjectId();
    Assert.assertTrue("Could not locate object by ID (" + id + ")", tm.getObjectById(id) != null);
    Assert.assertTrue("Found wrong object type by ID (" + id + ")", tm.getObjectById(id) instanceof TopicIF);
    Assert.assertTrue("Found wrong object: " + tm.getObjectById(id).getObjectId() + " - " + topic.getObjectId(), 
           tm.getObjectById(id).getObjectId().equals(topic.getObjectId()));

    topic.remove();

    //! System.out.println("------------------------->" + topic);
    //! System.out.println("--" + topic.getTopicMap());
    //! System.out.println("--" + tm.getObjectById(id) + " " + System.identityHashCode(tm.getObjectById(id)));
    //! System.out.println("--" + tm.getObjectById(id).getTopicMap());
    Assert.assertTrue("Found topic by ID after it was removed",
           tm.getObjectById(id) == null);
    
    try {
      tm.getObjectById(null);
      Assert.fail("getObjectById accepts null parameter.");
    } catch (NullPointerException ex) {
      // Expected.
    }
  }

  @Test
  public void testObjectByNonNumericId() {
    Assert.assertTrue("Found object by non-sensical ID 'bongo'",
               tm.getObjectById("Bongo") == null);
  }
  
  // --- Internal methods

  @Override
  public void setUp() throws Exception {
    super.setUp();
    tm = topicmap;
    object = tm;
  }

  @Override
  protected TMObjectIF makeObject() {
    return null;
  }
  
}
