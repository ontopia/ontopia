
// $Id: TestOSLValidate.java,v 1.3 2008/01/14 12:52:33 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;
import net.ontopia.topicmaps.schema.impl.osl.*;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.OSLValidate;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.utils.FileUtils;

public class TestOSLValidate extends AbstractWebedTestCase {

  private final static String testdataDirectory = "webed";
  
  public TestOSLValidate(String name) {
    super(name);
  }

  // --- Tests

  public void testNoSchema() {
    try {
      ActionIF action = new OSLValidate();
      ActionParametersIF params = makeParameters(tm, "");
      ActionResponseIF response = makeResponse();
      action.perform(params, response);
      fail("Action did not detect missing schema");
    } catch (ActionRuntimeException e) {
      assertTrue("Received non-critical error for missing schema",
                 e.getCritical());
    }
  }

  public void testValidateTM() throws Exception {
    // run the action
    OSLSchemaAwareIF action = new OSLValidate();
    action.setSchema(loadSchema("football.osl"));
    ActionParametersIF params = makeParameters(tm, "");
    ActionResponseIF response = makeResponse();
    action.perform(params, response);

    // if we get here we have a schema, and the TM is valid, so all is fine
  }

  public void testValidateTopic() throws Exception {
    // create invalid topic
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF player = getTopicById(tm, "player");
    TopicIF topic = builder.makeTopic(player);
    // topic has no name, and so is invalid

    // get topic to validate
    TopicIF gamst = getTopicById(tm, "gamst");
    
    // run the action
    OSLSchemaAwareIF action = new OSLValidate();
    action.setSchema(loadSchema("football.osl"));
    ActionParametersIF params = makeParameters(gamst, "");
    ActionResponseIF response = makeResponse();
    action.perform(params, response);

    // if we get here we have a schema, and the TM is valid, so all is fine
    // also, we did not validate the invalid topic
  }

  public void testValidateAssociation() throws Exception {
    // create invalid topic
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF player = getTopicById(tm, "player");
    TopicIF topic = builder.makeTopic(player);
    // topic has no name, and so is invalid

    // get association to validate
    TopicIF gamst = getTopicById(tm, "gamst");
    AssociationRoleIF role = (AssociationRoleIF)
      gamst.getRoles().iterator().next();
    AssociationIF assoc = role.getAssociation();
    
    // run the action
    OSLSchemaAwareIF action = new OSLValidate();
    action.setSchema(loadSchema("football.osl"));
    ActionParametersIF params = makeParameters(assoc, "");
    ActionResponseIF response = makeResponse();
    action.perform(params, response);

    // if we get here we have a schema, and the TM is valid, so all is fine
    // also, we did not validate the invalid topic
  }

  public void testValidateBadObject() throws Exception {
    // run the action
    OSLSchemaAwareIF action = new OSLValidate();
    action.setSchema(loadSchema("football.osl"));
    ActionParametersIF params = makeParameters("", "");
    ActionResponseIF response = makeResponse();

    // test
    try {
      action.perform(params, response);
      fail("Validation of non-TM object undetected");
    } catch (ActionRuntimeException e) {
      assertTrue("Non-critical error for validation of bad object",
                 e.getCritical());
    }
  }

  public void testValidateTopicInvalid() throws Exception {
    // create invalid topic
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF player = getTopicById(tm, "player");
    TopicIF topic = builder.makeTopic(player);
    // topic has no name, and so is invalid
    
    // get ready
    OSLSchemaAwareIF action = new OSLValidate();
    action.setSchema(loadSchema("football.osl"));
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();

    // test
    try {
      action.perform(params, response);
      fail("Invalid topic accepted");
    } catch (ActionRuntimeException e) {
      assertTrue("Invalid topic gave non-critical error", e.getCritical());
    }
  }

  public void testNoArguments() throws Exception {
    // run the action
    OSLSchemaAwareIF action = new OSLValidate();
    action.setSchema(loadSchema("football.osl"));
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, "");
    ActionResponseIF response = makeResponse();

    // test
    try {
      action.perform(params, response);
      fail("Invalid parameters accepted");
    } catch (ActionRuntimeException e) {
    }
  }
  
  
  // FIXME: validate a collection of topics and associations
  // FIXME: test handling of syntactically invalid schema

  // --- Helpers

  private OSLSchema loadSchema(String file) 
    throws IOException, SchemaSyntaxException {
    file = FileUtils.getTestInputFile(testdataDirectory, file);
    OSLSchemaReader reader = new OSLSchemaReader(file);
    return (OSLSchema) reader.read();
  }
  
}
