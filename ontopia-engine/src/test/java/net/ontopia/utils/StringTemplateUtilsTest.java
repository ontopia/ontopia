//#! Ignore-License (manual license due to #! appearing in body)
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class StringTemplateUtilsTest {
  
  // --- test cases
  
  @Test
  public void testProcessEmpty() {
    assertProcessesAs("", "", Collections.EMPTY_MAP);
  }

  @Test
  public void testProcessString() {
    assertProcessesAs("a string", "a string", Collections.EMPTY_MAP);
  }

  @Test
  public void testProcessSinglePercent() {
    assertProcessesAs("a 100%% increase", "a 100% increase",
                  Collections.EMPTY_MAP);
  }

  @Test
  public void testProcessSinglePercentAtStart() {
    assertProcessesAs("%% increase", "% increase", Collections.EMPTY_MAP);
  }

  @Test
  public void testProcessSinglePercentAtEnd() {
    assertProcessesAs("120 %%", "120 %", Collections.EMPTY_MAP);
  }

  @Test
  public void testProcessDoublePercent() {
    assertProcessesAs("aaa %%%% aaa", "aaa %% aaa", Collections.EMPTY_MAP);
  }  

  @Test
  public void testProcessOnlySinglePercent() {
    assertProcessesAs("%%", "%", Collections.EMPTY_MAP);
  }

  @Test
  public void testProcessParamRef() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    assertProcessesAs("hi to %person%!", "hi to Lars Marius!", map);
  }

  @Test
  public void testProcessParamRefDirect() {
    assertProcessesAs("hi to %person%!", "hi to Niko!", "person", "Niko");
  }
  
  @Test
  public void testProcessParamRefDirectWithSep() {
    assertProcessesAs("hi to #person#!", "hi to Niko!", "person", "Niko", '#');
  }
  
  @Test
  public void testProcessParamRefAndSingle() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    assertProcessesAs("hi to %person%%%!", "hi to Lars Marius%!", map);
  }
  
  @Test
  public void testProcessParamRefAtStart() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    assertProcessesAs("%person% wrote this", "Lars Marius wrote this", map);
  }
  
  @Test
  public void testProcessParamRefAtStartDirect() {
    assertProcessesAs("%person% extended this a bit", "Niko extended this a bit",
                  "person", "Niko");
  }
  
  @Test
  public void testProcessParamRefAtEnd() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    assertProcessesAs("hi to %person%", "hi to Lars Marius", map);
  }
  
  @Test
  public void testProcessOnlyParamRef() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    assertProcessesAs("%person%", "Lars Marius", map);
  }
  
  @Test
  public void testProcessTwoParamRefs() {
    Map map = new HashMap();
    map.put("person", "Lars Marius");
    map.put("age", "28");
    assertProcessesAs("in 2002 %person% is %age% years old",
                  "in 2002 Lars Marius is 28 years old", map);
  }

  @Test
  public void testProcessThreeParamRefs() {
    Map map = new HashMap();
    map.put("new", "123");
    map.put("new2", "456");
    map.put("value", "789");
    assertProcessesAs("[%new% : %new2% = \"%value%\"]",
                  "[123 : 456 = \"789\"]", map);
  }
  
  @Test
  public void testProcessAdjacentParamRefs() {
    Map map = new HashMap();
    map.put("given", "Lars Marius");
    map.put("family", " Garshol");
    assertProcessesAs("%given%%family%", "Lars Marius Garshol", map);
  }


  @Test
  public void testProcessUnknownParam() {
    try {
      StringTemplateUtils.replace("an %unknown% param", Collections.EMPTY_MAP);
      Assert.fail("Unknown parameter accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }

  @Test
  public void testProcessUnknownParamDirect() {
    try {
      StringTemplateUtils.replace("an %unknown% param", "name", "Niko");
      Assert.fail("Unknown parameter accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }
  
  @Test
  public void testProcessUnterminatedParam() {
    try {
      StringTemplateUtils.replace("an %unknown param", Collections.EMPTY_MAP);
      Assert.fail("Unterminated parameter accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }
  
  @Test
  public void testProcessUnterminatedValue() {
    try {
      Map map = new HashMap(); map.put("unset", null);
      StringTemplateUtils.replace("an %unset% param", map);
      Assert.fail("Unterminated value accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }

  @Test
  public void testProcessUnterminatedValueDirect() {
    try {
      StringTemplateUtils.replace("an %unset% param", "unset", null);
      Assert.fail("Unterminated value accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }
  
  // --- helpers
  
  protected void assertProcessesAs(String template, String target, Map params) {
    String result = StringTemplateUtils.replace(template, params);
    Assert.assertTrue("'" + template + "'should resolve to '" + target +
               "', got '" + result + "'", target.equals(result));
  }
  
  protected void assertProcessesAs(String template, String target,
                               String param, String value) {
    String result = StringTemplateUtils.replace(template, param, value);
    Assert.assertTrue("'" + template + "'should resolve to '" + target +
               "', got '" + result + "'", target.equals(result));
  }

  protected void assertProcessesAs(String template, String target,
                               String param, String value, char sep) {
    String result = StringTemplateUtils.replace(template, param, value, sep);
    Assert.assertTrue("'" + template + "'should resolve to '" + target +
               "', got '" + result + "'", target.equals(result));
  }
  
}
