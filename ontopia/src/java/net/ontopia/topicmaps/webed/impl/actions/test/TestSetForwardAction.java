
// $Id: TestSetForwardAction.java,v 1.1 2004/08/27 12:07:59 larsga Exp $

package net.ontopia.topicmaps.webed.impl.actions.test;

import java.util.*;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;

public class TestSetForwardAction extends AbstractOntopiaTestCase {

  public TestSetForwardAction(String name) {
    super(name);
  }

  // tests

  public void testNoParameters() throws ActionRuntimeException {
    ActionIF forward = new SetForwardAction();
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    forward.perform(params, response);
    assertNull("forward must be null", response.getForward());
  }

  public void testOnlyRequestParam() throws ActionRuntimeException {
    ActionIF forward = new SetForwardAction();
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, "foo.jsp");
    ActionResponseIF response = makeResponse();
    forward.perform(params, response);
    assertTrue("forward was wrong", response.getForward().equals("foo.jsp"));
  }

  public void testOneParam() throws ActionRuntimeException {
    ActionIF forward = new SetForwardAction();
    ActionParametersIF params = makeParameters("foo.jsp", "bar.jsp");
    ActionResponseIF response = makeResponse();
    forward.perform(params, response);
    assertTrue("forward was wrong", response.getForward().equals("foo.jsp"));
  }

  public void testEchoingRequestParams() throws ActionRuntimeException {
    ActionIF forward = new SetForwardAction();
    ActionParametersIF params = makeParameters(makeList("foo.jsp", "gurr"),
                                               "bar.jsp");
    ActionResponseIF response = makeResponse();
    forward.perform(params, response);
    
    assertTrue("forward was wrong", response.getForward().equals("foo.jsp"));
    assertTrue("wrong number of parameters set",
               response.getParameters().size() == 1);
    assertTrue("parameter gurr not set",
               response.getParameters().containsKey("gurr"));
    assertTrue("parameter gurr not null",
               response.getParameters().get("gurr") == null);
  }

  public void testEchoingRequestParams2() throws ActionRuntimeException {
    ActionIF forward = new SetForwardAction();
    ActionParametersIF params = makeParameters(makeList("foo.jsp", "gurr", "bah"),
                                               "bar.jsp");
    ActionResponseIF response = makeResponse();
    forward.perform(params, response);
    
    assertTrue("forward was wrong", response.getForward().equals("foo.jsp"));
    assertTrue("wrong number of parameters set",
               response.getParameters().size() == 2);
    assertTrue("parameter gurr not set",
               response.getParameters().containsKey("gurr"));
    assertTrue("parameter gurr not null",
               response.getParameters().get("gurr") == null);
    assertTrue("parameter bah not set",
               response.getParameters().containsKey("bah"));
    assertTrue("parameter bah not null",
               response.getParameters().get("bah") == null);
  }
  
  public void testBadParamType() throws ActionRuntimeException {
    ActionIF forward = new SetForwardAction();
    ActionParametersIF params = makeParameters(makeList(this), "bar.jsp");
    ActionResponseIF response = makeResponse();

    try {
      forward.perform(params, response);
      fail("action accepted parameter of bad type");
    } catch (ActionRuntimeException e) {
    }
  }

  public void testBadParamType2() throws ActionRuntimeException {
    ActionIF forward = new SetForwardAction();
    ActionParametersIF params = makeParameters(makeList(this, this), "bar.jsp");
    ActionResponseIF response = makeResponse();

    try {
      forward.perform(params, response);
      fail("action accepted parameter of bad type");
    } catch (ActionRuntimeException e) {
    }
  }
  
  // helper methods

  private List makeList(Object param1) {
    param1 = Collections.singleton(param1); // params are lists of collections...
    return Collections.singletonList(param1);
  }

  private List makeList(Object param1, Object param2) {
    List list = new ArrayList(2);
    list.add(Collections.singleton(param1));
    list.add(Collections.singleton(param2));
    return list;
  }

  private List makeList(Object param1, Object param2, Object param3) {
    List list = new ArrayList(3);
    list.add(Collections.singleton(param1));
    list.add(Collections.singleton(param2));
    list.add(Collections.singleton(param3));
    return list;
  }
  
  private ActionParametersIF makeParameters(Object param1, String value) {
    if (param1 instanceof List)
      return makeParameters((List) param1, value);

    return makeParameters(makeList(param1), value);
  }
  
  private ActionParametersIF makeParameters(List params) {
    return makeParameters(params, null);
  }

  private ActionParametersIF makeParameters(List params, String value) {
    String[] values = {value};
    return new ActionParameters("boo1", values, null, params, null, null);
  }
  
  private ActionResponseIF makeResponse() {
    javax.servlet.http.HttpServletRequest request = new FakeServletRequest();
    javax.servlet.http.HttpServletResponse response = new FakeServletResponse();
    return new ActionResponse(request, response);
  }
  
}
