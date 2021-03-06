/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.actions;

import java.io.IOException;
import java.util.Collections;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchema;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchemaReader;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.core.OSLSchemaAwareIF;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.OSLValidate;
import net.ontopia.utils.TestFileUtils;

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
    file = TestFileUtils.getTestInputFile(testdataDirectory, file);
    OSLSchemaReader reader = new OSLSchemaReader(file);
    return (OSLSchema) reader.read();
  }
  
}
