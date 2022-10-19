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

package net.ontopia.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CachedIndexTest {
  private CachedIndex index;
  
  @Before
  public void setUp() {
    index = new CachedIndex(new EmptyIndex());
  }

  // --- Test cases

  @Test
  public void testEmpty() {
    Assert.assertTrue("found key in empty index",
           index.get("larsga") == null);
  }

  @Test
  public void testFind() {
    assertContainsAfterAdd("larsga", "Lars Marius Garshol");
  }
  
  @Test
  public void testFindMore() {
    assertContainsAfterAdd("larsga", "Lars Marius Garshol");
    assertContainsAfterAdd("grove", "Geir Ove Gronmo");
    assertContainsAfterAdd("tine", "Tine Holst");
    assertContainsAfterAdd("sylvias", "Sylvia Schwab");
    assertContainsAfterAdd("pepper", "Steve Pepper");
    assertContainsAfterAdd("hca", "Hans Christian Alsos");
    assertContainsAfterAdd("niko", "Niko Schmuck");
    assertContainsAfterAdd("pam", "Pamela Gennusa");
    assertContainsAfterAdd("kal", "Kal Ahmed");
    assertContainsAfterAdd("murray", "Murray Woodman");
    
    assertContains("larsga", "Lars Marius Garshol");
    assertContains("grove", "Geir Ove Gronmo");
    assertContains("tine", "Tine Holst");
    assertContains("sylvias", "Sylvia Schwab");
    assertContains("pepper", "Steve Pepper");
    assertContains("hca", "Hans Christian Alsos");
    assertContains("niko", "Niko Schmuck");
    assertContains("pam", "Pamela Gennusa");
    assertContains("kal", "Kal Ahmed");
    assertContains("murray", "Murray Woodman");

    Assert.assertTrue("non-existent key found",
           index.get("dummy") == null);
  }

  @Test
  public void testExpand() {
    index = new CachedIndex(new EmptyIndex(), 1000, 5, true);
    
    assertContainsAfterAdd("larsga", "Lars Marius Garshol");
    assertContainsAfterAdd("grove", "Geir Ove Gronmo");
    assertContainsAfterAdd("tine", "Tine Holst");
    assertContainsAfterAdd("sylvias", "Sylvia Schwab");
    assertContainsAfterAdd("pepper", "Steve Pepper");
    assertContainsAfterAdd("hca", "Hans Christian Alsos");
    assertContainsAfterAdd("niko", "Niko Schmuck");
    assertContainsAfterAdd("pam", "Pamela Gennusa");
    assertContainsAfterAdd("kal", "Kal Ahmed");
    assertContainsAfterAdd("murray", "Murray Woodman");

    assertContains("larsga", "Lars Marius Garshol");
    assertContains("grove", "Geir Ove Gronmo");
    assertContains("tine", "Tine Holst");
    assertContains("sylvias", "Sylvia Schwab");
    assertContains("pepper", "Steve Pepper");
    assertContains("hca", "Hans Christian Alsos");
    assertContains("niko", "Niko Schmuck");
    assertContains("pam", "Pamela Gennusa");
    assertContains("kal", "Kal Ahmed");
    assertContains("murray", "Murray Woodman");

    Assert.assertTrue("non-existent key found",
           index.get("dummy") == null);
  }

  @Test
  public void testPrune() {
    index = new CachedIndex(new SameIndex(), 250, 5, true);

    for (int ix = 0; ix < 10000; ix++) {
      String key = Integer.toString((int) (Math.random() * 500));
      Assert.assertTrue("didn't find value",
             index.get(key).equals(key));
    }

    Assert.assertTrue("number of keys in index too high",
           index.getKeyNumber() <= 250);
  }

  @Test
  public void testChange() {
    assertContainsAfterAdd("larsga", "Lars Marius Garshol");
    assertContainsAfterAdd("larsga", "LMG");
    assertContains("larsga", "LMG");
    assertContainsAfterAdd("larsga", "Lars Marius Garshol");
    assertContains("larsga", "Lars Marius Garshol");
  }
  
  // --- Helper methods

  private void assertContainsAfterAdd(String key, String value) {
    index.put(key, value);
    assertContains(key, value);
  }

  private void assertContains(String key, String value) {
    String found = (String) index.get(key);
    Assert.assertTrue("did not find value on lookup",
           found != null);
    Assert.assertTrue("found '" + found + "' on lookup, expected '" + value + "'",
           found.equals(value));
  }
  
  // --- SameIndex

  class SameIndex implements LookupIndexIF {
    @Override
    public Object get(Object key) {
      return key;
    }

    @Override
    public Object put(Object key, Object value) {
      return value;
    }

    @Override
    public Object remove(Object key) {
      return key;
    }
  }
  
  // --- EmptyIndex

  class EmptyIndex implements LookupIndexIF {
    @Override
    public Object get(Object key) {
      return null;
    }

    @Override
    public Object put(Object key, Object value) {
      return value;
    }

    @Override
    public Object remove(Object key) {
      return null;
    }
  }
  
}
