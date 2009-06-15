// $Id: CachedIndexTest.java,v 1.8 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils.test;

import java.util.*;
import net.ontopia.test.*;
import net.ontopia.utils.*;

public class CachedIndexTest extends AbstractOntopiaTestCase {
  private CachedIndex index;
  
  public CachedIndexTest(String name) {
    super(name);
  }

  protected void setUp() {
    index = new CachedIndex(new EmptyIndex());
  }

  protected void tearDown() {
  }

  // --- Test cases

  public void testEmpty() {
    assertTrue("found key in empty index",
           index.get("larsga") == null);
  }

  public void testFind() {
    check("larsga", "Lars Marius Garshol");
  }
  
  public void testFindMore() {
    check("larsga", "Lars Marius Garshol");
    check("grove", "Geir Ove Gronmo");
    check("tine", "Tine Holst");
    check("sylvias", "Sylvia Schwab");
    check("pepper", "Steve Pepper");
    check("hca", "Hans Christian Alsos");
    check("niko", "Niko Schmuck");
    check("pam", "Pamela Gennusa");
    check("kal", "Kal Ahmed");
    check("murray", "Murray Woodman");
    
    lookfor("larsga", "Lars Marius Garshol");
    lookfor("grove", "Geir Ove Gronmo");
    lookfor("tine", "Tine Holst");
    lookfor("sylvias", "Sylvia Schwab");
    lookfor("pepper", "Steve Pepper");
    lookfor("hca", "Hans Christian Alsos");
    lookfor("niko", "Niko Schmuck");
    lookfor("pam", "Pamela Gennusa");
    lookfor("kal", "Kal Ahmed");
    lookfor("murray", "Murray Woodman");

    assertTrue("non-existent key found",
           index.get("dummy") == null);
  }

  public void testExpand() {
    index = new CachedIndex(new EmptyIndex(), 1000, 5, true);
    
    check("larsga", "Lars Marius Garshol");
    check("grove", "Geir Ove Gronmo");
    check("tine", "Tine Holst");
    check("sylvias", "Sylvia Schwab");
    check("pepper", "Steve Pepper");
    check("hca", "Hans Christian Alsos");
    check("niko", "Niko Schmuck");
    check("pam", "Pamela Gennusa");
    check("kal", "Kal Ahmed");
    check("murray", "Murray Woodman");

    lookfor("larsga", "Lars Marius Garshol");
    lookfor("grove", "Geir Ove Gronmo");
    lookfor("tine", "Tine Holst");
    lookfor("sylvias", "Sylvia Schwab");
    lookfor("pepper", "Steve Pepper");
    lookfor("hca", "Hans Christian Alsos");
    lookfor("niko", "Niko Schmuck");
    lookfor("pam", "Pamela Gennusa");
    lookfor("kal", "Kal Ahmed");
    lookfor("murray", "Murray Woodman");

    assertTrue("non-existent key found",
           index.get("dummy") == null);
  }

  public void testPrune() {
    index = new CachedIndex(new SameIndex(), 250, 5, true);

    for (int ix = 0; ix < 10000; ix++) {
      String key = Integer.toString((int) (Math.random() * 500));
      assertTrue("didn't find value",
             index.get(key).equals(key));
    }

    assertTrue("number of keys in index too high",
           index.getKeyNumber() <= 250);
  }

  public void testChange() {
    check("larsga", "Lars Marius Garshol");
    check("larsga", "LMG");
    lookfor("larsga", "LMG");
    check("larsga", "Lars Marius Garshol");
    lookfor("larsga", "Lars Marius Garshol");
  }
  
  // --- Helper methods

  private void check(String key, String value) {
    index.put(key, value);
    lookfor(key, value);
  }

  private void lookfor(String key, String value) {
    String found = (String) index.get(key);
    assertTrue("did not find value on lookup",
           found != null);
    assertTrue("found '" + found + "' on lookup, expected '" + value + "'",
           found.equals(value));
  }
  
  // --- SameIndex

  class SameIndex implements LookupIndexIF {
    public Object get(Object key) {
      return key;
    }

    public Object put(Object key, Object value) {
      return value;
    }

    public Object remove(Object key) {
      return key;
    }
  }
  
  // --- EmptyIndex

  class EmptyIndex implements LookupIndexIF {
    public Object get(Object key) {
      return null;
    }

    public Object put(Object key, Object value) {
      return value;
    }

    public Object remove(Object key) {
      return null;
    }
  }
  
}




