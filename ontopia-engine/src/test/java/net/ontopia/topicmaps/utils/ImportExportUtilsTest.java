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

package net.ontopia.topicmaps.utils;

import org.junit.Assert;
import org.junit.Test;

public class ImportExportUtilsTest {

  // --- Tests

  @Test
  public void testGetTopicMapId() {
    assertCorrectId("1", 1L);
    assertCorrectId("123", 123L);
    assertCorrectId("M1", 1L);
    assertCorrectId("M123", 123L);
    assertCorrectId("x-ontopia:tm-rdbms:1", 1L);
    assertCorrectId("x-ontopia:tm-rdbms:123", 123L);
    assertCorrectId("x-ontopia:tm-rdbms:M1", 1L);
    assertCorrectId("x-ontopia:tm-rdbms:M123", 123L);
  }
  
  private void assertCorrectId(String id, long y) {
    long x = ImportExportUtils.getTopicMapId(id);
    Assert.assertTrue("Invalid id: " + x + " (should have been: " + y + ")", x == y);
  }
  
}
