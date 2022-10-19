/*
 * #!
 * Ontopoly Editor
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
package ontopoly.utils;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Test;

public class OntopolyModelUtilsTests {
  
  /**
   * Issue 369: removing a field from a topic type removes a random field. Caused by a missing
   * player check in OntopolyModelUtilsTests.findBinaryAssociation()
   */
  @Test
  public void testIssue369() {
    TopicMapIF tm = new InMemoryTopicMapStore().getTopicMap();
    TopicMapBuilderIF builder = tm.getBuilder();

    TopicIF at = builder.makeTopic();
    TopicIF rt1 = builder.makeTopic();
    TopicIF rp1 = builder.makeTopic();
    TopicIF rt2 = builder.makeTopic();
    TopicIF rp2 = builder.makeTopic();

    TopicIF falseplayer = builder.makeTopic();

    AssociationIF a = builder.makeAssociation(at, rt1, rp1);
    builder.makeAssociationRole(a, rt2, rp2);

    // using the incorrect player should not find an association
    AssociationIF falseAssociation = OntopolyModelUtils.findBinaryAssociation(null, at, rp1, rt1, falseplayer, rt2);
    Assert.assertNull("Issue 369: findBinaryAssociation ignores player2", falseAssociation);

    // using the correct player should find the association
    AssociationIF correctAssociation = OntopolyModelUtils.findBinaryAssociation(null, at, rp1, rt1, rp2, rt2);
    Assert.assertNotNull("Issue 369: findBinaryAssociation ignores player2", correctAssociation);

    // we should have found 'a'
    Assert.assertEquals("Issue 369: findBinaryAssociation ignores player2", a, correctAssociation);
  }
}
