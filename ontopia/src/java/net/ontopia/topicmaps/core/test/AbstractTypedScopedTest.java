
// $Id: AbstractTypedScopedTest.java,v 1.10 2008/06/12 14:37:13 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import junit.framework.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;


// PLEASE NOTE: this is a duplicate of AbstractTypedTest, but with
// AbstractScopedTest as a base.


public abstract class AbstractTypedScopedTest extends AbstractScopedTest {
  protected TypedIF typed;
  
  public AbstractTypedScopedTest(String name) {
    super(name);
  }

  // --- Test cases

  public void testType() {
		if (typed instanceof TopicNameIF)
			assertTrue("type not null initially", typed.getType() == null);
		else
			assertTrue("type null initially", typed.getType() != null);

    TopicIF type = builder.makeTopic();
    typed.setType(type);
    assertTrue("type identity not retained", typed.getType().equals(type));

		if (typed instanceof TopicNameIF) {
			typed.setType(null);
			assertTrue("type could not be set to null", typed.getType() == null);
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





