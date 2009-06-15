
// $Id: InMemoryContentStoreTest.java,v 1.1 2004/01/27 22:12:10 larsga Exp $

package net.ontopia.infoset.content.test;

import java.util.*;
import net.ontopia.test.*;
import net.ontopia.infoset.content.*;

public class InMemoryContentStoreTest extends AbstractContentStoreTest {
  
  public InMemoryContentStoreTest(String name) {
    super(name);
  }

  public void setUp() {
    store = new InMemoryContentStore();
  }
}
