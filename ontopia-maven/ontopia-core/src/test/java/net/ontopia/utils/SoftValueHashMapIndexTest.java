
// $Id: SoftValueHashMapIndexTest.java,v 1.1 2005/08/24 12:49:19 grove Exp $

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
