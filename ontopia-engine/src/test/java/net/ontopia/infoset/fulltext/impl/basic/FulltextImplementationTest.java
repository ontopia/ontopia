/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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

package net.ontopia.infoset.fulltext.impl.basic;

import net.ontopia.infoset.fulltext.core.FulltextImplementationIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.impl.utils.FulltextIndexManager;
import org.junit.Assert;
import org.junit.Test;

public class FulltextImplementationTest {

  @Test
  public void testFulltextImplementationIFServiceLoading() {
    FulltextIndexManager manager = new FulltextIndexManager(new InMemoryTopicMapStore());
    FulltextImplementationIF fulltextImplementation = manager.getFulltextImplementation();
    Assert.assertNotNull(fulltextImplementation);
    Assert.assertEquals(DummyFulltextImplementation.class, fulltextImplementation.getClass());
  }
}
