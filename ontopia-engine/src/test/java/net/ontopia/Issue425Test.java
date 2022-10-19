/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

package net.ontopia;

import java.net.URISyntaxException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTestFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for issue 425: Deleting a topic may cause loss of locator on an unrelated topic.
 * This seems to only happen in MySQL, due to case insensitive collation.
 */
public class Issue425Test extends AbstractTopicMapTest {

  @Test
  public void testURN() throws URISyntaxException {
    
    TopicIF t1 = builder.makeTopic();
    TopicIF t2 = builder.makeTopic();
    
    t1.addSubjectIdentifier(URILocator.create("urn:test1"));
    t2.addSubjectIdentifier(URILocator.create("urn:TEST1"));
    
    topicmap.getStore().commit();
    
    Assert.assertEquals(2, topicmap.getTopics().size());
    
    topicmap.getTopicBySubjectIdentifier(URILocator.create("urn:test1")).remove();
    topicmap.getStore().commit();

    Assert.assertEquals(1, topicmap.getTopics().size());
    
  }

  @Test
  public void testURI() throws URISyntaxException {
    
    TopicIF t1 = builder.makeTopic();
    TopicIF t2 = builder.makeTopic();
    
    t1.addSubjectIdentifier(URILocator.create("http://example.com/?q=a"));
    t2.addSubjectIdentifier(URILocator.create("http://example.com/?q=A"));
    
    topicmap.getStore().commit();
    
    Assert.assertEquals(2, topicmap.getTopics().size());
    
    topicmap.getTopicBySubjectIdentifier(URILocator.create("http://example.com/?q=a")).remove();
    topicmap.getStore().commit();

    Assert.assertEquals(1, topicmap.getTopics().size());
    
  }

  @Override
  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }
}
