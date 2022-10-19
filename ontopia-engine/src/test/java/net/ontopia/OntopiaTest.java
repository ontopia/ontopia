/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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

package net.ontopia;

import net.ontopia.utils.OntopiaRuntimeException;
import org.junit.Assert;
import org.junit.Test;

public class OntopiaTest {
  
  @Test
  public void testGetName() {
    Assert.assertNotNull(Ontopia.getName());
    Assert.assertEquals("Ontopia Topic Maps Engine", Ontopia.getName());
  }
  
  @Test
  public void testGetVersion() {
    Assert.assertNotNull(Ontopia.getVersion());
  }
  
  @Test
  public void testGetBuildDate() {
    Assert.assertNotNull(Ontopia.getBuildDate());
  }
  
  @Test
  public void testGetBuildUser() {
    Assert.assertNotNull(Ontopia.getBuildUser());
  }
  
  @Test
  public void testGetInfo() {
    Assert.assertNotNull(Ontopia.getInfo());
  }
  
  @Test
  public void testCheckClasses() {
    try {
      Ontopia.checkClasses();
    } catch (OntopiaRuntimeException ore) {
      Assert.fail(ore.getMessage());
    }
  }
  
  @Test
  public void testCheck() {
    try {
      Ontopia.check();
    } catch (OntopiaRuntimeException ore) {
      Assert.fail(ore.getMessage());
    }
  }
}
