// $Id: AbstractStringifierTest.java,v 1.6 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils.test;

import java.util.*;
import net.ontopia.test.*;
import net.ontopia.utils.*;

public abstract class AbstractStringifierTest extends AbstractOntopiaTestCase {

  protected int intended_size = 8;
  
  public AbstractStringifierTest(String name) {
    super(name);
  }

  protected void setUp() {
  }

  protected void tearDown() {
  }

  protected void testStringifier(Object str, Object identical, Object different) {
    assertTrue("stringifier is not equal", str.equals(identical));
    assertTrue("stringifier is equal", !str.equals(different));
  }
  
}




