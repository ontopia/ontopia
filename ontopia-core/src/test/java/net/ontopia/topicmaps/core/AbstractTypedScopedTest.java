// $Id: AbstractTypedScopedTest.java,v 1.10 2008/06/12 14:37:13 geir.gronmo Exp $

package net.ontopia.topicmaps.core;

import net.ontopia.topicmaps.utils.PSI;

// PLEASE NOTE: this is a duplicate of AbstractTypedTest, but with
// AbstractScopedTest as a base.

public abstract class AbstractTypedScopedTest extends AbstractScopedTest {
  protected TypedIF typed;

  public AbstractTypedScopedTest(String name) {
    super(name);
  }

  // --- Test cases

  public void testType() {
    if (typed instanceof TopicNameIF) {
      TopicMapIF tm = typed.getTopicMap();
      TopicIF defaultNameType = tm.getTopicBySubjectIdentifier(PSI
          .getSAMNameType());
      assertTrue("type not equal to the default name type initially", typed
          .getType().equals(defaultNameType));
    } else {
      assertTrue("type null initially", typed.getType() != null);
    }

    TopicIF type = builder.makeTopic();
    typed.setType(type);
    assertTrue("type identity not retained", typed.getType().equals(type));

    if (typed instanceof TopicNameIF) {
      typed.setType(null);
      TopicMapIF tm = typed.getTopicMap();
      TopicIF defaultNameType = tm.getTopicBySubjectIdentifier(PSI
          .getSAMNameType());
      assertTrue("type is not equal to the default name type when set to null",
          typed.getType().equals(defaultNameType));
    } else {
      try {
        typed.setType(null);
        fail("type could be set to null");
      } catch (NullPointerException e) {
      }
      assertTrue("type identity not retained", typed.getType().equals(type));
    }
  }

}
