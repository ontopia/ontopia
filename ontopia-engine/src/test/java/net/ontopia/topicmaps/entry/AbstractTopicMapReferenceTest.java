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

import java.util.Collection;
import java.util.Collections;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import org.junit.Assert;

public abstract class AbstractTopicMapReferenceTest {

  // --- utility methods

  public void assertCompliesToAbstractTopicMapReference(AbstractTopicMapReference ref, 
                                               boolean checkOpenAfterClose) throws java.io.IOException {
    // WARNING: always run these tests as the last test as the
    // reference is being closed

    // test id
    String id = ref.getId();
    Assert.assertTrue("Id default is not set", ref.getId() != null);
    ref.setId("foo");
    Assert.assertTrue("Id not equals 'foo'", "foo".equals(ref.getId()));
    ref.setId(id);
    Assert.assertTrue("Id not equals '" + id + "'", id.equals(ref.getId()));
    
    // test title
    String title = ref.getTitle();
    Assert.assertTrue("Title default is not set", ref.getTitle() != null);
    ref.setTitle("foo");
    Assert.assertTrue("Title not equals 'foo'", "foo".equals(ref.getTitle()));
    ref.setTitle(title);
    Assert.assertTrue("Title not equals '" + title + "'", title.equals(ref.getTitle()));
    
    // test source
    TopicMapSourceIF source = ref.getSource();
    TopicMapSourceIF esource = new EmptyTopicMapSource("empty");
    ref.setSource(esource);
    Assert.assertTrue("Source != esource", esource == ref.getSource());
    ref.setSource(source);
    Assert.assertTrue("Source not equals '" + source + "'", source == ref.getSource());

    // test createStore
    TopicMapStoreIF store1 = ref.createStore(true);
    TopicMapStoreIF store2 = ref.createStore(false);

    // reference should be open after create
    Assert.assertTrue("Reference not open after createStore", ref.isOpen());

    Assert.assertTrue("store1 is null", store1 != null);
    Assert.assertTrue("store2 is null", store2 != null);
    
    // if reference has been closed then isOpen should return false
    ref.close();
    Assert.assertTrue("Reference open after close", !ref.isOpen());
    Assert.assertTrue("Reference deleted after close", !ref.isDeleted());

    // should not be possible to create store after close
    try {
      ref.createStore(true);
      Assert.assertTrue("Reference open after Assert.failed createStore", ref.isOpen());
    } catch (ReferenceNotOpenException e) {
      Assert.fail("Could not create store after reference " + ref + " had been closed.");
    }

    // store1 and store2 should also have been closed
    if (checkOpenAfterClose) {
      Assert.assertTrue("store1 open after reference close", store1.isOpen());
      Assert.assertTrue("store2 open after reference close", store2.isOpen());
    }

    // should not be possible to delete after close
    if (ref.getSource() != null) {
      try {
        ref.delete();
        Assert.assertTrue("Reference not deleted after delete", ref.isDeleted());
        Assert.assertTrue("Reference open after delete", !ref.isOpen());
      } catch (ReferenceNotOpenException e) {
        Assert.fail("Could not delete reference " + ref + " after close.");
      }
    }

  }

  /* -- tm source stub used for testing purposes -- */
  static class EmptyTopicMapSource implements TopicMapSourceIF {
    private String id;
    private String title;
    EmptyTopicMapSource(String id) { this.id = id; }
    @Override
    public String getId() { return id; }
    @Override
    public void setId(String id) { this.id = id; }
    @Override
    public String getTitle() { return title; }
    @Override
    public void setTitle(String title) { this.title = title; }
    @Override
    public Collection getReferences() { return Collections.EMPTY_SET; }
    @Override
    public void refresh() { /* no-op */ };    
    @Override
    public void close() { /* no-op */ }
    @Override
    public boolean supportsCreate() {
      return false;
    }
    @Override
    public boolean supportsDelete() {
      return false;
    }
    @Override
    public TopicMapReferenceIF createTopicMap(String name, String baseAddress) {
      throw new UnsupportedOperationException();
    }
  }
}
