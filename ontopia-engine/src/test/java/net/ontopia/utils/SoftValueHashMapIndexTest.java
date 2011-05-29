
package net.ontopia.utils;

import net.ontopia.utils.SoftValueHashMapIndex;

public class SoftValueHashMapIndexTest extends SoftHashMapIndexTest {
  
  public SoftValueHashMapIndexTest(String name) {
    super(name);
  }
  
  public void setUp() {
    index = new SoftValueHashMapIndex();
  }

}
