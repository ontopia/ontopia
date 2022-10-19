/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2015 The Ontopia Project
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests IteratorIterator. Introduced for #515.
 */
public class IteratorIteratorTest {

  @Test
  @SuppressWarnings("unchecked")
  public void testSingleEmpty() {
    Iterator<Iterator<Object>> singleEmpty = it(it());

    IteratorIterator<Object> iit = new IteratorIterator<Object>(singleEmpty);

    Assert.assertFalse(iit.hasNext());

    try {
      iit.next();
      Assert.fail("Expected ");
    } catch (NoSuchElementException nsee) {
      // expected
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testLastEmpty() {
    Iterator<Iterator<Object>> lastEmpty = it(it(new Object()), it());
    IteratorIterator<Object> iit = new IteratorIterator<Object>(lastEmpty);

    // collection 1: 1 item
    Assert.assertTrue(iit.hasNext());
    Assert.assertNotNull(iit.next());

    // collection 2: 0 items
    Assert.assertFalse(iit.hasNext());

    try {
      iit.next();
      Assert.fail("Expected ");
    } catch (NoSuchElementException nsee) {
      // expected
    }
  }

  private <O> Iterator<O> it(O... items) {
    return Arrays.asList(items).iterator();
  }

}
