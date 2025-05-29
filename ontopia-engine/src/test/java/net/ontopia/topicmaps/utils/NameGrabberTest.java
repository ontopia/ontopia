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

package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NameGrabberTest {

  private TopicIF topicSort;
  private TopicIF topicPlay;
  private TopicIF topic5;
  private TopicNameIF basename5A;
  private TopicNameIF basename5B;
  
  @Before
  public void setUp() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    TopicMapBuilderIF builder = store.getTopicMap().getBuilder();
    
    topicSort = builder.makeTopic();
    LocatorIF sortRef = URILocator.create("http://www.topicmaps.org/xtm/1.0/core.xtm#sort");
    topicSort.addSubjectIdentifier(sortRef);
    topicPlay = builder.makeTopic();
    TopicIF topicWriter = builder.makeTopic();

    
    TopicIF topic1 = builder.makeTopic();
    TopicNameIF basename1 = builder.makeTopicName(topic1, "Wilhelmine von Hillern");
    basename1.addTheme(topicWriter);
    VariantNameIF variant1A = builder.makeVariantName(basename1, "Hillern, Wilhelmine", Collections.emptySet());
    variant1A.addTheme(topicSort);
    
    TopicIF topic2 = builder.makeTopic();
    TopicNameIF basename2 = builder.makeTopicName(topic2, "Alphonse Daudet");
    basename2.addTheme(topicWriter);
    VariantNameIF variant2A = builder.makeVariantName(basename2, "Daudet, Alphonse", Collections.emptySet());
    variant2A.addTheme(topicSort);

    TopicIF topic3 = builder.makeTopic();
    TopicNameIF basename3 = builder.makeTopicName(topic3, "El trovador");
    basename3.addTheme(topicPlay);
    VariantNameIF variant3A = builder.makeVariantName(basename3, "Trovador", Collections.emptySet());
    variant3A.addTheme(topicSort);

    TopicIF topic4 = builder.makeTopic();
    TopicNameIF basename4 = builder.makeTopicName(topic4, "The Merry Wives of Windsor");
    basename4.addTheme(topicPlay);
    VariantNameIF variant4A = builder.makeVariantName(basename4, "Merry Wives of Windsor", Collections.emptySet());
    variant4A.addTheme(topicSort);

    topic5 = builder.makeTopic();
    basename5A = builder.makeTopicName(topic5, "Die Jungfrau von Orleans");
    basename5A.addTheme(topicPlay);
    VariantNameIF variant5A = builder.makeVariantName(basename5A, "Jungfrau von Orleans", Collections.emptySet());
    variant5A.addTheme(topicSort);
    
    basename5B = builder.makeTopicName(topic5, "Jungfrau von Orleans");

    VariantNameIF variant5C = builder.makeVariantName(basename5A, "Jungfrau von Orleans", Collections.emptySet());
    variant5C.addTheme(topicPlay);
  }

  // --- Test cases

  @Test
  public void testNameGrabber5A() {
    List basenameScope = new ArrayList();
    basenameScope.add(topicPlay);
    List variantScope = new ArrayList();
    variantScope.add(topicSort);
    Function grabber = new NameGrabber(basenameScope, variantScope);

    Assert.assertTrue("wrong base name grabbed",
           ((TopicNameIF) grabber.apply(topic5)).equals(basename5A));
  }
  
  @Test
  public void testNameGrabber5B() {
    Function grabber = new NameGrabber(Collections.EMPTY_LIST);

    Assert.assertTrue("wrong base name grabbed",
           ((TopicNameIF) grabber.apply(topic5)).equals(basename5B));
  }

  @Test
  public void testNameGrabber5C() {
    List variantScope = new ArrayList();
    variantScope.add(topicSort);
    Function grabber = new NameGrabber(Collections.EMPTY_LIST, variantScope);

    Assert.assertTrue("wrong base name grabbed",
           ((TopicNameIF) grabber.apply(topic5)).equals(basename5A));
  }
}
