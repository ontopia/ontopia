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

import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;
import org.junit.Test;

public class StoreFactoryReferenceTest extends AbstractTopicMapReferenceTest {

  @Test
  public void testStoreFactoryRef() throws java.io.IOException {
    String id = "sfr.xtm";
    String title = "SFRM";

    TopicMapStoreFactoryIF sf = new InMemoryStoreFactory();
    StoreFactoryReference ref = new StoreFactoryReference(id, title, sf);

    // run abstract url topic map reference tests
    boolean checkOpenAfterClose = false;
    assertCompliesToAbstractTopicMapReference(ref, checkOpenAfterClose);
  }
  
}
