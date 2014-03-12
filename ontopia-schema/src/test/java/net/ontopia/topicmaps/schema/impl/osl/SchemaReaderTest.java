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

package net.ontopia.topicmaps.schema.impl.osl;

import java.io.IOException;
import net.ontopia.topicmaps.schema.core.CardinalityConstraintIF;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;
import org.junit.Assert;
import org.junit.Test;

public class SchemaReaderTest extends AbstractSchemaTestCase {

  // --- Test methods

  @Test
  public void testEmpty() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "empty.xml");
    
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

    Assert.assertTrue("schema read from file had null base address",
           schema.getAddress() != null);
  }

  @Test
  public void testLoose() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "loose.xml");

    Assert.assertTrue("schema was not loose, as specified in XML document",
           !schema.isStrict());
  }

  @Test(expected=SchemaSyntaxException.class)
  public void testDoctype() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "doctype.xml");

    // the real test here is: can we read it at all?
    
    Assert.assertTrue("schema was not strict, as specified in XML document",
           schema.isStrict());
  }

  @Test
  public void testTopicClass() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "topic.xml");

    Assert.assertTrue("schema did not have one topic class",
           schema.getTopicClasses().size() == 1);

    TopicClass tclass = schema.getTopicClass("something");
    Assert.assertTrue("topic class not found by ID",
           tclass != null);
    Assert.assertTrue("topic class does not use loose matching, as specified in file",
           !tclass.isStrict());

    verifySingleTypeSpec(tclass, "#something");
  }

  @Test
  public void testTopicNameConstraint() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "basename.xml");

    Assert.assertTrue("schema did not have one topic class",
           schema.getTopicClasses().size() == 1);

    TopicClass tclass = schema.getTopicClass("something");
    Assert.assertTrue("topic class not found by ID",
           tclass != null);

    Assert.assertTrue("topic class did not have one base name constraint",
           tclass.getTopicNameConstraints().size() == 1);

    TopicNameConstraint bnc =
      (TopicNameConstraint) tclass.getTopicNameConstraints().iterator().next();

    verifyMinMax(bnc, 1, 2);
    verifySingleScopeSpec(bnc, "#something-else");
  }

  @Test
  public void testOccurrenceConstraint() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "occurrence.xml");

    Assert.assertTrue("schema did not have one topic class",
           schema.getTopicClasses().size() == 1);

    TopicClass tclass = schema.getTopicClass("something");
    Assert.assertTrue("topic class not found by ID",
           tclass != null);

    Assert.assertTrue("topic class did not have one occurrence constraint",
           tclass.getOccurrenceConstraints().size() == 1);

    OccurrenceConstraint occ =
      (OccurrenceConstraint) tclass.getOccurrenceConstraints().iterator().next();
    Assert.assertTrue("occurrence is not set to EITHER",
           occ.getInternal() == OccurrenceConstraint.RESOURCE_EITHER);

    verifyMinMax(occ, 3, 4);
    verifySingleScopeSpec(occ, "#occtheme");
    verifySingleTypeSpec(occ, "#occtype");
  }

  @Test
  public void testOccurrenceExternal() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "extocc.xml");

    Assert.assertTrue("schema did not have one topic class",
           schema.getTopicClasses().size() == 1);

    TopicClass tclass = schema.getTopicClass("something");
    Assert.assertTrue("topic class not found by ID",
           tclass != null);

    Assert.assertTrue("topic class did not have one occurrence constraint",
           tclass.getOccurrenceConstraints().size() == 1);

    OccurrenceConstraint occ =
      (OccurrenceConstraint) tclass.getOccurrenceConstraints().iterator().next();
    Assert.assertTrue("occurrence is not set to external",
           occ.getInternal() == OccurrenceConstraint.RESOURCE_EXTERNAL);

    verifyMinMax(occ, 0, CardinalityConstraintIF.INFINITY);
    verifySingleTypeSpec(occ, "#occtype");
  }
  
  @Test
  public void testTopicRoleConstraint() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "trole.xml");

    TopicClass tclass = schema.getTopicClass("something");
    Assert.assertTrue("topic class not found by ID",
           tclass != null);

    Assert.assertTrue("topic class did not have one role constraint",
           tclass.getRoleConstraints().size() == 1);

    TopicRoleConstraint rc =
      (TopicRoleConstraint) tclass.getRoleConstraints().iterator().next();

    verifyMinMax(rc, 5, 6);
    verifySingleTypeSpec(rc, "#roletype");

    Assert.assertTrue("topic role did not have one allowed association type",
           rc.getAssociationTypes().size() == 1);

    TypeSpecification spec =
      (TypeSpecification) rc.getAssociationTypes().iterator().next();
    InternalTopicRefMatcher matcher =
      (InternalTopicRefMatcher) spec.getClassMatcher();
    Assert.assertTrue("topic matcher had wrong relative URI",
           matcher.getRelativeURI().equals("#assoctype"));
  }

  @Test
  public void testAssocRoleConstraint() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "arole.xml");

    Assert.assertTrue("schema did not have one association class",
           schema.getAssociationClasses().size() == 1);
    
    AssociationClass aclass =
      (AssociationClass) schema.getAssociationClasses().iterator().next();

    Assert.assertTrue("association class had scope spec",
           aclass.getScopeSpecification() == null);
    Assert.assertTrue("association class did not have one role constraint",
           aclass.getRoleConstraints().size() == 1);

    AssociationRoleConstraint rc =
      (AssociationRoleConstraint)aclass.getRoleConstraints().iterator().next();

    verifyMinMax(rc, 6, 7);
    verifySingleTypeSpec(rc, "#roletype");

    Assert.assertTrue("association role did not have one allowed player type",
           rc.getPlayerTypes().size() == 1);

    TypeSpecification spec =
      (TypeSpecification) rc.getPlayerTypes().iterator().next();
    InternalTopicRefMatcher matcher =
      (InternalTopicRefMatcher) spec.getClassMatcher();
    Assert.assertTrue("topic matcher had wrong relative URI",
           matcher.getRelativeURI().equals("#playertype"));
  }

  @Test
  public void testForward() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("schemas", "forward-ref.xml");

    // the real test here is: can we read it at all?
    // we also check the forward reference, to make sure it worked

    TopicClass tclass = schema.getTopicClass("tc43");
    Assert.assertTrue("Couldn't find topic class with ID 'tc43'",
               tclass != null);

    Assert.assertTrue("Topic class has no superclass!",
               tclass.getSuperclass() != null);
  }

  
  // --- Helper methods

  protected void verifyMinMax(CardinalityConstraintIF constraint,
                              int min, int max) {
    Assert.assertTrue("constraint did not have min=" + min,
           constraint.getMinimum() == min);
    Assert.assertTrue("base name constraint did not have max=" + max,
           constraint.getMaximum() == max);
  }
  
  protected void verifySingleScopeSpec(ScopedConstraintIF constraint,
                                       String uri) {
    ScopeSpecification spec = constraint.getScopeSpecification();
    Assert.assertTrue("constraint had no scope specification",
           spec != null);

    Assert.assertTrue("scope specification did not have one matcher",
           spec.getThemeMatchers().size() == 1);

    InternalTopicRefMatcher matcher =
      (InternalTopicRefMatcher) spec.getThemeMatchers().iterator().next();
    Assert.assertTrue("scope specification did not have one matcher",
           spec.getThemeMatchers().size() == 1);
    Assert.assertTrue("matcher had bad URI: " + matcher.getRelativeURI(),
           matcher.getRelativeURI().equals(uri));    
  }

  protected void verifySingleTypeSpec(TypedConstraintIF constraint,
                                      String uri) {
    TypeSpecification spec = constraint.getTypeSpecification();
    Assert.assertTrue("constraint had no type specification",
           spec != null);

    InternalTopicRefMatcher matcher =
      (InternalTopicRefMatcher) spec.getClassMatcher();
    Assert.assertTrue("topic matcher had wrong relative URI",
           matcher.getRelativeURI().equals(uri));
  }  
}
