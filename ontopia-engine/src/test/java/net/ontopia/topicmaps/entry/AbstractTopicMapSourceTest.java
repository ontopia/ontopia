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

package net.ontopia.topicmaps.entry;

import org.junit.Assert;

public abstract class AbstractTopicMapSourceTest {
  
  // --- utility methods

  public static void assertCompliesToAbstractTopicMapSource(TopicMapSourceIF source) {
    // test id
    String id = source.getId();
    Assert.assertTrue("Id default is not set", source.getId() != null);
    try {
      source.setId("foo");
      Assert.assertTrue("Id not equals 'foo'", "foo".equals(source.getId()));
      source.setId(id);
      Assert.assertTrue("Id not equals '" + id + "'", id.equals(source.getId()));
    } catch (UnsupportedOperationException e) {
      // this is ok, because source does not have to support setId
    }

    // test title
    String title = source.getTitle();
    Assert.assertTrue("Title default is not set", source.getTitle() != null);
    try {
      source.setTitle("foo");
      Assert.assertTrue("Title not equals 'foo'", "foo".equals(source.getTitle()));
      source.setTitle(title);
      Assert.assertTrue("Title not equals '" + title + "'", title.equals(source.getTitle()));
    } catch (UnsupportedOperationException e) {
      // this is ok, because source does not have to support setTitle
    }

    // test getReferences    
    Assert.assertTrue("TopicMapSource.references == null", source.getReferences() != null);

    if (source != null) {
      if (source.supportsCreate()) {
        // create topic map and then delete it
        int refcountX = source.getReferences().size();
        TopicMapReferenceIF ref = source.createTopicMap("barfoo", null);
        int refcountY = source.getReferences().size();
        Assert.assertTrue("Number of referenced not increased by one", refcountY == (refcountX+1));
        Assert.assertTrue("Id of create reference not set", ref.getId() != null);
        Assert.assertTrue("Created reference thinks that it is deleted", !ref.isDeleted());
        ref.delete();
        Assert.assertTrue("Deleted reference does not know that it is deleted", ref.isDeleted());
        //! int refcountZ = source.getReferences().size();
        //! Assert.assertTrue("Number of referenced not same as before create", refcountZ == refcountX);
        
      } else {
        // verify that one cannot call createTopicMap method
        try {
          source.createTopicMap("barfoo", null);
          Assert.fail("Source says that it does not support creating topicmaps, but allows create method to be called.");
        } catch (UnsupportedOperationException e) {
          // ok.
        }
      }
    }

    // test refresh (tests are in subclass)
  }
  
}
