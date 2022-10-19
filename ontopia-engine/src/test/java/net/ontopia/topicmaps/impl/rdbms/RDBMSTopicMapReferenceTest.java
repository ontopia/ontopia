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

package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import org.junit.Assert;
import org.junit.Test;

public class RDBMSTopicMapReferenceTest {

  @Test
  public void testSetTitle() throws Exception {
    final String NEW_TITLE = "___NEW_TITLE___";

    RDBMSTestFactory factory = new RDBMSTestFactory();
    TopicMapReferenceIF before = factory.makeTopicMapReference();
    Assert.assertNotSame("Test reference already has title '" + NEW_TITLE + "', cannot test for change", NEW_TITLE, before.getTitle());
    before.setTitle(NEW_TITLE);
    String referenceId = before.getId();

    factory = new RDBMSTestFactory(); // reload
    TopicMapReferenceIF after = null;
    for (TopicMapReferenceIF i : factory.getSource().getReferences()) {
      if (referenceId.equals(i.getId())) { after = i; }
    }
    Assert.assertNotNull("Reference with id '" + referenceId + "' not found", after);
    Assert.assertEquals("Reference title not changed correctly", NEW_TITLE, after.getTitle());
  }

  @Test
  public void testSetBaseAddress() throws Exception {
    final LocatorIF NEW_BASE_ADDRESS = URILocator.create("foo:bar-" + System.currentTimeMillis());

    RDBMSTestFactory factory = new RDBMSTestFactory();
    RDBMSTopicMapReference before = (RDBMSTopicMapReference) factory.makeTopicMapReference();
    Assert.assertNotSame("Test reference already has base address '" + NEW_BASE_ADDRESS.getAddress() + "', cannot test for change", NEW_BASE_ADDRESS, before.getBaseAddress());
    before.setBaseAddress(NEW_BASE_ADDRESS);
    String referenceId = before.getId();

    factory = new RDBMSTestFactory(); // reload
    RDBMSTopicMapReference after = null;
    for (TopicMapReferenceIF i : factory.getSource().getReferences()) {
      if (referenceId.equals(i.getId())) { after = (RDBMSTopicMapReference) i; }
    }
    Assert.assertNotNull("Reference with id '" + referenceId + "' not found", after);
    Assert.assertEquals("Reference base address not changed correctly", NEW_BASE_ADDRESS, after.getBaseAddress());
  }

}
