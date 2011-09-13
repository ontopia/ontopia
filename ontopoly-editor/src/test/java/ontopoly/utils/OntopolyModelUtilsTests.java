package ontopoly.utils;

import junit.framework.Assert;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
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
