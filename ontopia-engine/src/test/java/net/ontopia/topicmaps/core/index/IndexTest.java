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

package net.ontopia.topicmaps.core.index;

import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * Performs a series of tests on the index interfaces.
 */

public abstract class IndexTest extends AbstractTopicMapTest {  

  public IndexTest(String name) {
    super(name);
  }

  // inserted to keep JUnit quiet. please remove.
  public void testDummy() {
  }
  
  //! /**
  //!  * Test that every index advertised by the index manager is accessible.
  //!  * Test that the index returned by the manager is an instance of the specified interface
  //!  * Test that every time an index is made active, it is added to the active indexes collection
  //!  * Test that a bogus name of index is not accessible.
  //!  */
  //! public void testIndexAccess() {
  //!   Collection supported = ixmgr.getSupportedIndexes();
  //!   Iterator it = supported.iterator();
  //!   while (it.hasNext()) {
  //!     String ixName = (String)it.next();
  //!     assertNotNull("Null index name.", ixName);
	//! 
  //!     Class ixCls = null;
  //!     try {
  //!       ixCls = Class.forName(ixName);
  //!     } catch (ClassNotFoundException ex) {
  //!       fail("Index class: " + ixName + " not found.");
  //!     }
	//! 
  //!     IndexIF ix = ixmgr.getIndex(ixName);
  //!     assertNotNull("Null index", ix);
  //!     assertTrue("Returned index is not an instance of the specified interface: " + ixName,
  //!            ixCls.isInstance(ix));
  //!          
  //!     assertTrue("Index is not reported as active: " + ixName,
  //!            ixmgr.isActive(ixName));
  //!     assertTrue("Activated index name not found in active indexes collection: " + ixName,
  //!            ixmgr.getActiveIndexes().contains(ix));
	//! 
  //!   }
  //! }

  /**
   * Tests that every getXXXTypes() function in the index returns an empty collection.
   */
  protected void testEmptyTypesIndexes(ClassInstanceIndexIF ix) {
    assertTrue("AssociationRoleTypes not empty.", ix.getAssociationRoleTypes().isEmpty());
    assertTrue("AssociationTypes not empty.", ix.getAssociationTypes().isEmpty());
    assertTrue("OccurrenceTypes not empty", ix.getOccurrenceTypes().isEmpty());
    assertTrue("TopicTypes not empty", ix.getTopicTypes().isEmpty());
  }
}





