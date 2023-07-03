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

public abstract class AbstractTMObjectTest extends AbstractTopicMapTest {

  protected TMObjectIF    object;      // object being tested
  protected TMObjectIF    parent;      // parent of object being tested, if any

  protected abstract TMObjectIF makeObject();

  // --- Test cases

  @Test
  public void testObjectId() {
    Assert.assertTrue("object id not null", object.getObjectId() != null);
    Assert.assertEquals("object not found by id", topicmap.getObjectById(object.getObjectId()), object);
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

  @Test
  public void testEquals() {
    Assert.assertTrue("object does not equal itself", object.equals(object));

    TMObjectIF other = makeObject();
    if (other != null) {
      Assert.assertTrue("object equals other objects",
                 !object.equals(other));
    }
  }

  @Test
  public void testDuplicateSourceLocator() {
    try {
      URILocator loc = URILocator.create("http://www.ontopia.net");
      object.addItemIdentifier(loc);

      // if this is a topic map we stop, since new object will be
      // another topic map
      if (object instanceof TopicMapIF) {
        return;
      }
      TMObjectIF object2 = makeObject();
      if (object2 != null) {
        try {
          object2.addItemIdentifier(loc);
          Assert.fail("duplicate source locator allowed");
        }
        catch (ConstraintViolationException e) {
        }
      }
    }
    catch (ConstraintViolationException e) {
      Assert.fail("spurious ConstraintViolationException");
    }
  }

  @Test
  public void testTopicSubjectIndicatorSourceLocator() {
    try {
      URILocator loc = URILocator.create("http://www.ontopia.net");

      TopicIF topic = builder.makeTopic();                      
      object.addItemIdentifier(loc);
                        
      topic.addSubjectIdentifier(loc);
    }
    catch (ConstraintViolationException e) {
      Assert.fail("spurious ConstraintViolationException");
    }
  }

  @Test
  public void testSourceLocatorTopicSubjectIndicator() {
    try {
      URILocator loc = URILocator.create("http://www.ontopia.net");

      TopicIF topic = builder.makeTopic();
      topic.addSubjectIdentifier(loc);
                        
      object.addItemIdentifier(loc);
    }
    catch (ConstraintViolationException e) {
      Assert.fail("spurious ConstraintViolationException");
    }
  }
        
  //! public void _testSourceLocatorUnassignable() {
  //!   try {
  //!     URILocator loc = URILocator.create("http://www.opera.com");
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

  @Test
  public void testSourceLocators() {
    // STATE 1: has no source locator
    Assert.assertTrue("empty object has source locators",
           object.getItemIdentifiers().size() == 0);

    try {
      // STATE 2: has only one source locator
      URILocator loc = URILocator.create("http://www.ontopia.net");
      object.addItemIdentifier(loc);
        
      Assert.assertTrue("source locator not added",
             object.getItemIdentifiers().size() == 1);
        
      Assert.assertTrue("source locator not added",
             object.getItemIdentifiers().iterator().next().equals(loc));

      TMObjectIF found = topicmap.getObjectByItemIdentifier(loc);
      Assert.assertTrue("can't look up by resid", found.equals(object));

      found = topicmap.getObjectByItemIdentifier(URILocator.create("http://www.ontopia.net"));
      Assert.assertTrue("can't look up by resid", found.equals(object));
        
      object.addItemIdentifier(loc);
      Assert.assertTrue("duplicate source locator not rejected",
             object.getItemIdentifiers().size() == 1);

      // STATE 3: has no source locator
      object.removeItemIdentifier(loc);
      Assert.assertTrue("source locator not removed",
             object.getItemIdentifiers().size() == 0);

      found = topicmap.getObjectByItemIdentifier(loc);
      Assert.assertTrue("can still look up by resid", found == null);
        
      // removing locator that is not present works OK
      object.removeItemIdentifier(loc);
    }
    catch (ConstraintViolationException e) {
      Assert.fail("spurious ConstraintViolationException");
    }
  }

  @Test
  public void testMoveSourceLocators() { // bug #273
      URILocator loc = URILocator.create("http://www.ontopia.net");
      TopicIF topic = builder.makeTopic();
      object.addItemIdentifier(loc);

      // we do this here to make the RDBMS backend write the
      // changes in the transaction to the DB
      URILocator loc2 = URILocator.create("http://www.ontopia.com");
      TMObjectIF lookedup = topicmap.getObjectByItemIdentifier(loc2);
      // </end>
      
      // ready to blow us up
      object.removeItemIdentifier(loc);
      topic.addItemIdentifier(loc);

      lookedup = topicmap.getObjectByItemIdentifier(loc);
      Assert.assertTrue("wrong object returned on lookup: " + lookedup,
             lookedup == topic);
  }
  
  @Test
  public void testIsModifiable() {
    Assert.assertTrue("is not modifiable", !object.isReadOnly());
  }

  @Test
  public void testTopicMap() {
    Assert.assertTrue("has the wrong topic map", object.getTopicMap().equals(topicmap));
  }
}
