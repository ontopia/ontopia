/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

package net.ontopia.topicmaps.schema.impl.osl;

import java.io.IOException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SchemaWriterTest extends AbstractSchemaTestCase {
  private OSLSchema schema;
  
  @Before
  public void setUp() {
    try {
      schema = new OSLSchema(new URILocator("http://www.ontopia.net"));
    } catch (java.net.MalformedURLException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    }
  }
  
  // --- Test methods

  @Test
  public void testEmpty() throws IOException, SchemaSyntaxException {
    writeSchema("out", "empty.xml", schema);
    
    schema = (OSLSchema) readSchema("out", "empty.xml");
    
    Assert.assertTrue("empty schema had non-empty rule set collection",
               schema.getRuleSets().isEmpty());
    Assert.assertTrue("empty schema had non-empty topic class collection",
               schema.getTopicClasses().isEmpty());
    Assert.assertTrue("empty schema had non-empty association class collection",
               schema.getAssociationClasses().isEmpty());

    Assert.assertTrue("rule set found in empty schema",
               schema.getRuleSet("bongo") == null);
    Assert.assertTrue("topic class found in empty schema",
               schema.getTopicClass("bongo") == null);
  }

  @Test
  public void testEmptyClass() throws IOException, SchemaSyntaxException {
    TopicClass klass = new TopicClass(schema, "bingo");
    TypeSpecification typespec = new TypeSpecification();
    typespec.setClassMatcher(new SubjectIndicatorMatcher(new URILocator("http://psi.ontopia.net/bongo/#bongo")));
    klass.setTypeSpecification(typespec);
    schema.addTopicClass(klass);
    writeSchema("out", "emptyclass.xml", schema);
    
    schema = (OSLSchema) readSchema("out", "emptyclass.xml");
    Assert.assertTrue("schema had non-empty rule set collection",
               schema.getRuleSets().isEmpty());
    Assert.assertTrue("wrong number of classes in schema",
               schema.getTopicClasses().size() == 1);
    Assert.assertTrue("schema had non-empty association class collection",
               schema.getAssociationClasses().isEmpty());
    Assert.assertTrue("topic class not found",
               schema.getTopicClass("bingo") != null);
  }

  @Test
  public void testUnscopedOccurrence() throws IOException, SchemaSyntaxException {
    TopicClass klass = new TopicClass(schema, "bingo");
    TypeSpecification typespec = new TypeSpecification();
    typespec.setClassMatcher(new SubjectIndicatorMatcher(new URILocator("http://psi.ontopia.net/bongo/#bongo")));
    klass.setTypeSpecification(typespec);

    OccurrenceConstraint oc = new OccurrenceConstraint(klass);
    typespec = new TypeSpecification();
    typespec.setClassMatcher(new SubjectIndicatorMatcher(new URILocator("http://psi.ontopia.net/bongo/#rongo")));
    oc.setTypeSpecification(typespec);
    klass.addOccurrenceConstraint(oc);    
    schema.addTopicClass(klass);
    
    writeSchema("out", "unscoped-occ.xml", schema);
    schema = (OSLSchema) readSchema("out", "unscoped-occ.xml");
    
    Assert.assertTrue("schema had non-empty rule set collection",
               schema.getRuleSets().isEmpty());
    Assert.assertTrue("wrong number of classes in schema",
               schema.getTopicClasses().size() == 1);
    Assert.assertTrue("schema had non-empty association class collection",
               schema.getAssociationClasses().isEmpty());
    Assert.assertTrue("topic class not found",
               schema.getTopicClass("bingo") != null);

    klass = schema.getTopicClass("bingo");
    Assert.assertTrue("topic class lost occurrence constraint",
               klass.getOccurrenceConstraints().size() == 1);
  }
}
