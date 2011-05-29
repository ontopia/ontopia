
package net.ontopia.infoset.content;

import java.util.*;

public class InMemoryContentStoreTest extends AbstractContentStoreTest {
  
  public InMemoryContentStoreTest(String name) {
    super(name);
  }

  public void setUp() {
    store = new InMemoryContentStore();
  }
}
