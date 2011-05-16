// $Id: HistoryMapTest.java,v 1.6 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils;

import junit.framework.TestCase;

public class HistoryMapTest extends TestCase {

  public HistoryMapTest(String name) {
    super(name);
  }

  String s1 = "String1";
  String s2 = "zwo";
  String s3 = "Nummer tres";
  String s4 = "viere";
  String s5 = "zwo";
  
  protected HistoryMap makeHistoryMap() {
    HistoryMap hm = new HistoryMap(3, true);
    hm.add(s1);
    hm.add(s2);
    hm.add(s3);
    return hm;
  }
  
  public void testAdd() {
    HistoryMap hm = makeHistoryMap();
    assertTrue("Expected other HistoryMap result, got " + hm,
               (hm.size() == 3) &&
               hm.containsValue(s1) && hm.containsValue(s2) && hm.containsValue(s3));
    hm.add(s4);
    assertTrue("First should be gone, but got" + hm,
               (hm.size() == 3) &&
               hm.containsValue(s2) && hm.containsValue(s3) && hm.containsValue(s4));
  }
  
  public void testGet() {
    HistoryMap hm = makeHistoryMap();
    assertTrue("1-Expected to get second element, but got " + hm.getEntry(2),
               (hm.size() == 3) && hm.getEntry(2).equals(s2));
    hm.add(s4);
    assertTrue("2-Expected to get second element, but got " + hm.getEntry(2),
               (hm.size() == 3) && hm.getEntry(2).equals(s3));
  }

  public void testSuppressDuplicates() {
    HistoryMap hm = makeHistoryMap();
    assertTrue("Expected to get second element, but got " + hm.getEntry(2),
               (hm.size() == 3) && hm.getEntry(2).equals(s2));
    hm.add(s5);
    assertTrue("Duplicate suppression did not work, got  " + hm.getEntry(2),
               (hm.size() == 3) && hm.getEntry(2).equals(s2));
  }
  
}




