// $Id: AbstractTypedTest.java,v 1.8 2008/05/23 11:49:49 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import junit.framework.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

public abstract class AbstractTypedTest extends AbstractTMObjectTest {
  protected TypedIF typed;
  
  public AbstractTypedTest(String name) {
    super(name);
  }

    // --- Test cases

    public void testType() {
        assertTrue("type null initially", typed.getType() != null);

        TopicIF type = builder.makeTopic();
        typed.setType(type);
        assertTrue("type identity not retained", typed.getType().equals(type));

				try {
					typed.setType(null);
					fail("type could be set to null");
				} catch (NullPointerException e) {
				}
    }

}





