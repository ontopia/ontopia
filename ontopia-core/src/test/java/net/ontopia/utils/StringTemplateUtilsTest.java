// $Id: StringTemplateUtilsTest.java,v 1.8 2004/03/08 09:37:27 larsga Exp $

package net.ontopia.utils;

import java.util.*;
import junit.framework.TestCase;

public class StringTemplateUtilsTest extends TestCase {
  
  public StringTemplateUtilsTest(String name) {
    super(name);
  }

  // --- test cases
  
  public void testProcessEmpty() {
    verifyProcess("", "", Collections.EMPTY_MAP);
  }

  public void testProcessString() {
    verifyProcess("a string", "a string", Collections.EMPTY_MAP);
  }

  public void testProcessSinglePercent() {
    verifyProcess("a 100%% increase", "a 100% increase",
                  Collections.EMPTY_MAP);
  }

  public void testProcessSinglePercentAtStart() {
    verifyProcess("%% increase", "% increase", Collections.EMPTY_MAP);
  }

  public void testProcessSinglePercentAtEnd() {
    verifyProcess("120 %%", "120 %", Collections.EMPTY_MAP);
  }

  public void testProcessDoublePercent() {
    verifyProcess("aaa %%%% aaa", "aaa %% aaa", Collections.EMPTY_MAP);
  }  

  public void testProcessOnlySinglePercent() {
    verifyProcess("%%", "%", Collections.EMPTY_MAP);
  }

  public void testProcessParamRef() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    verifyProcess("hi to %person%!", "hi to Lars Marius!", map);
  }

  public void testProcessParamRefDirect() {
    verifyProcess("hi to %person%!", "hi to Niko!", "person", "Niko");
  }
  
  public void testProcessParamRefDirectWithSep() {
    verifyProcess("hi to #person#!", "hi to Niko!", "person", "Niko", '#');
  }
  
  public void testProcessParamRefAndSingle() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    verifyProcess("hi to %person%%%!", "hi to Lars Marius%!", map);
  }
  
  public void testProcessParamRefAtStart() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    verifyProcess("%person% wrote this", "Lars Marius wrote this", map);
  }
  
  public void testProcessParamRefAtStartDirect() {
    verifyProcess("%person% extended this a bit", "Niko extended this a bit",
                  "person", "Niko");
  }
  
  public void testProcessParamRefAtEnd() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    verifyProcess("hi to %person%", "hi to Lars Marius", map);
  }
  
  public void testProcessOnlyParamRef() {
    Map map = new HashMap(); map.put("person", "Lars Marius");
    verifyProcess("%person%", "Lars Marius", map);
  }
  
  public void testProcessTwoParamRefs() {
    Map map = new HashMap();
    map.put("person", "Lars Marius");
    map.put("age", "28");
    verifyProcess("in 2002 %person% is %age% years old",
                  "in 2002 Lars Marius is 28 years old", map);
  }

  public void testProcessThreeParamRefs() {
    Map map = new HashMap();
    map.put("new", "123");
    map.put("new2", "456");
    map.put("value", "789");
    verifyProcess("[%new% : %new2% = \"%value%\"]",
                  "[123 : 456 = \"789\"]", map);
  }
  
  public void testProcessAdjacentParamRefs() {
    Map map = new HashMap();
    map.put("given", "Lars Marius");
    map.put("family", " Garshol");
    verifyProcess("%given%%family%", "Lars Marius Garshol", map);
  }


  public void testProcessUnknownParam() {
    try {
      StringTemplateUtils.replace("an %unknown% param", Collections.EMPTY_MAP);
      fail("Unknown parameter accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }

  public void testProcessUnknownParamDirect() {
    try {
      StringTemplateUtils.replace("an %unknown% param", "name", "Niko");
      fail("Unknown parameter accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }
  
  public void testProcessUnterminatedParam() {
    try {
      StringTemplateUtils.replace("an %unknown param", Collections.EMPTY_MAP);
      fail("Unterminated parameter accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }
  
  public void testProcessUnterminatedValue() {
    try {
      Map map = new HashMap(); map.put("unset", null);
      StringTemplateUtils.replace("an %unset% param", map);
      fail("Unterminated value accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }

  public void testProcessUnterminatedValueDirect() {
    try {
      StringTemplateUtils.replace("an %unset% param", "unset", null);
      fail("Unterminated value accepted");
    } catch (OntopiaRuntimeException e) {
    }
  }
  
  // --- helpers
  
  protected void verifyProcess(String template, String target, Map params) {
    String result = StringTemplateUtils.replace(template, params);
    assertTrue("'" + template + "'should resolve to '" + target +
               "', got '" + result + "'", target.equals(result));
  }
  
  protected void verifyProcess(String template, String target,
                               String param, String value) {
    String result = StringTemplateUtils.replace(template, param, value);
    assertTrue("'" + template + "'should resolve to '" + target +
               "', got '" + result + "'", target.equals(result));
  }

  protected void verifyProcess(String template, String target,
                               String param, String value, char sep) {
    String result = StringTemplateUtils.replace(template, param, value, sep);
    assertTrue("'" + template + "'should resolve to '" + target +
               "', got '" + result + "'", target.equals(result));
  }
  
}




