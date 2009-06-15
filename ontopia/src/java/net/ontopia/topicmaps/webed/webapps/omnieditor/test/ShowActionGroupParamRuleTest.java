
// $Id: ShowActionGroupParamRuleTest.java,v 1.2 2003/12/22 19:16:09 larsga Exp $

package net.ontopia.topicmaps.webed.webapps.omnieditor.test;

import java.util.*;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.utils.*;
import net.ontopia.topicmaps.webed.webapps.omnieditor.*;

public class ShowActionGroupParamRuleTest extends AbstractOntopiaTestCase {

  ParamRuleIF rule;
  String input;
  String output;
  
  public ShowActionGroupParamRuleTest(String name) {
    super(name);
  }

  public void setUp() {
    rule = new ShowActionGroupParamRule();
  }
  
  public void testA() {
    input = "topic_edit.jsp?ag=editNames&show_ag=editIntOccs";
    output = rule.generate(null, "", "", input);
    assertTrue("Unexpected output " + output,
           output.equals("topic_edit.jsp?ag=editIntOccs"));
  }

  public void testB() {
    input = "topic_edit.jsp?show_ag=editIntOccs&ag=editNames";
    output = rule.generate(null, "", "", input);
    assertTrue("Unexpected output " + output,
           output.equals("topic_edit.jsp?ag=editIntOccs&"));
  }

  public void testC() {
    input = "topic_edit.jsp?ag=editNames";
    output = rule.generate(null, "", "", input);
    assertTrue("Unexpected output " + output,
           output.equals("topic_edit.jsp?"));
  }

  public void testD() {
    input = "topic_edit.jsp?show_ag=editNames";
    output = rule.generate(null, "", "", input);
    assertTrue("Unexpected output " + output,
           output.equals("topic_edit.jsp?ag=editNames"));
  }

  
}
