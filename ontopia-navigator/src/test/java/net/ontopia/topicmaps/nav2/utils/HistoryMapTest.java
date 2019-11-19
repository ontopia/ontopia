/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.nav2.utils;

import org.junit.Assert;
import org.junit.Test;

public class HistoryMapTest {

  private static final String s1 = "String1";
  private static final String s2 = "zwo";
  private static final String s3 = "Nummer tres";
  private static final String s4 = "viere";
  private static final String s5 = "zwo";

  protected HistoryMap makeHistoryMap() {
    HistoryMap hm = new HistoryMap(3, true);
    hm.add(s1);
    hm.add(s2);
    hm.add(s3);
    return hm;
  }
  
  @Test
  public void testAdd() {
    HistoryMap hm = makeHistoryMap();
    Assert.assertTrue("Expected other HistoryMap result, got " + hm,
               (hm.size() == 3) &&
               hm.containsValue(s1) && hm.containsValue(s2) && hm.containsValue(s3));
    hm.add(s4);
    Assert.assertTrue("First should be gone, but got" + hm,
               (hm.size() == 3) &&
               hm.containsValue(s2) && hm.containsValue(s3) && hm.containsValue(s4));
  }
  
  @Test
  public void testGet() {
    HistoryMap hm = makeHistoryMap();
    Assert.assertTrue("1-Expected to get second element, but got " + hm.getEntry(2),
               (hm.size() == 3) && hm.getEntry(2).equals(s2));
    hm.add(s4);
    Assert.assertTrue("2-Expected to get second element, but got " + hm.getEntry(2),
               (hm.size() == 3) && hm.getEntry(2).equals(s3));
  }

  @Test
  public void testSuppressDuplicates() {
    HistoryMap hm = makeHistoryMap();
    Assert.assertTrue("Expected to get second element, but got " + hm.getEntry(2),
               (hm.size() == 3) && hm.getEntry(2).equals(s2));
    hm.add(s5);
    Assert.assertTrue("Duplicate suppression did not work, got  " + hm.getEntry(2),
               (hm.size() == 3) && hm.getEntry(2).equals(s2));
  }
  
}
