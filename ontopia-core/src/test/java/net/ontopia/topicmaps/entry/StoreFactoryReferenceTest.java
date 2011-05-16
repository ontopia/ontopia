
// $Id: StoreFactoryReferenceTest.java,v 1.2 2004/11/19 12:52:47 grove Exp $

package net.ontopia.topicmaps.entry;

import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;

public class StoreFactoryReferenceTest extends AbstractTopicMapReferenceTest {

  public StoreFactoryReferenceTest(String name) {
    super(name);
  }

  public void testStoreFactoryRef() throws java.io.IOException {
    String id = "sfr.xtm";
    String title = "SFRM";

    TopicMapStoreFactoryIF sf = new InMemoryStoreFactory();
    StoreFactoryReference ref = new StoreFactoryReference(id, title, sf);

    // run abstract url topic map reference tests
    boolean checkOpenAfterClose = false;
    doAbstractTopicMapReferenceTests(ref, checkOpenAfterClose);
  }
  
}
