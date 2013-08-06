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

import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReference;

public class ResourceTopicMapSourceTest extends AbstractTopicMapSourceTest {

  public ResourceTopicMapSourceTest(String name) {
    super(name);
  }

  // --- Test cases (LTM)
  // The test cases try to use a topic map that is available in the classpath:
  // net/ontopia/topicmaps/db2tm/mondial.ltm

  public void testLTM1() {
    ResourceTopicMapSource source = new ResourceTopicMapSource(
        "net/ontopia/testdata/various/bart.ltm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("LTM");
    verifyLTMSource(source);
  }

  public void testLTM2() {
    ResourceTopicMapSource source = new ResourceTopicMapSource();
    source.setResourceName("net/ontopia/testdata/various/bart.ltm");
    source.setId("fooid");
    source.setTitle("footitle");
    verifyLTMSource(source);
  }

  @SuppressWarnings("unchecked")
  protected void verifyLTMSource(ResourceTopicMapSource source) {
    Collection refs = source.getReferences();
    assertTrue("URLTopicMapSource.getReferences().size() != 1", refs.size() == 1);
    TopicMapReferenceIF ref = (TopicMapReferenceIF)refs.iterator().next();
    assertTrue("!TopicMapReference.getId().equals('fooid')", "fooid".equals(ref.getId()));
    assertTrue("!TopicMapReference.getTitle().equals('footitle')", "footitle".equals(ref.getTitle()));    
    assertTrue("!(TopicMapReferenceIF instanceof LTMTopicMapReference)", ref instanceof LTMTopicMapReference);
    
    try {
      TopicMapStoreIF store = ref.createStore(true);
      assertTrue("ref.createStore(true) == null", store != null);
    
      TopicMapIF tm = store.getTopicMap();
      assertTrue("store.getTopicMap() == null", tm != null);

      Collection topics = tm.getTopics();
      assertTrue("tm.getTopics() == null", topics != null);
      assertTrue("tm.getTopics().size() == 0", topics.size() > 0);
    } catch (IOException e) {
      fail("Could not create TopicMapStoreIF: + " + e.getMessage());
    }
  }
}
