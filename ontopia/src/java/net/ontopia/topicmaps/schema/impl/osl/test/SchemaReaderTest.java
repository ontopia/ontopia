
// $Id: SchemaReaderTest.java,v 1.8 2008/06/12 14:37:23 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl.test;

import java.io.File;
import java.io.IOException;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;
import net.ontopia.topicmaps.xml.test.AbstractXMLTestCase;

public class SchemaReaderTest extends AbstractSchemaTestCase {
  
  public SchemaReaderTest(String name) {
    super(name);
  }
  
  // --- Test methods

  public void testEmpty() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "empty.xml");
    
    assertTrue("empty schema had non-empty rule set collection",
           schema.getRuleSets().isEmpty());
    assertTrue("empty schema had non-empty topic class collection",
           schema.getTopicClasses().isEmpty());
    assertTrue("empty schema had non-empty association class collection",
           schema.getAssociationClasses().isEmpty());

    assertTrue("rule set found in empty schema",
           schema.getRuleSet("bongo") == null);
    assertTrue("topic class found in empty schema",
           schema.getTopicClass("bongo") == null);

    assertTrue("schema read from file had null base address",
           schema.getAddress() != null);
  }

  public void testLoose() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "loose.xml");

    assertTrue("schema was not loose, as specified in XML document",
           !schema.isStrict());
  }

  public void testDoctype() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "doctype.xml");

    // the real test here is: can we read it at all?
    
    assertTrue("schema was not strict, as specified in XML document",
           schema.isStrict());
  }

  public void testTopicClass() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "topic.xml");

    assertTrue("schema did not have one topic class",
           schema.getTopicClasses().size() == 1);

    TopicClass tclass = schema.getTopicClass("something");
    assertTrue("topic class not found by ID",
           tclass != null);
    assertTrue("topic class does not use loose matching, as specified in file",
           !tclass.isStrict());

    verifySingleTypeSpec(tclass, "#something");
  }

  public void testTopicNameConstraint() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "basename.xml");

    assertTrue("schema did not have one topic class",
           schema.getTopicClasses().size() == 1);

    TopicClass tclass = schema.getTopicClass("something");
    assertTrue("topic class not found by ID",
           tclass != null);

    assertTrue("topic class did not have one base name constraint",
           tclass.getTopicNameConstraints().size() == 1);

    TopicNameConstraint bnc =
      (TopicNameConstraint) tclass.getTopicNameConstraints().iterator().next();

    verifyMinMax(bnc, 1, 2);
    verifySingleScopeSpec(bnc, "#something-else");
  }

  public void testOccurrenceConstraint() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "occurrence.xml");

    assertTrue("schema did not have one topic class",
           schema.getTopicClasses().size() == 1);

    TopicClass tclass = schema.getTopicClass("something");
    assertTrue("topic class not found by ID",
           tclass != null);

    assertTrue("topic class did not have one occurrence constraint",
           tclass.getOccurrenceConstraints().size() == 1);

    OccurrenceConstraint occ =
      (OccurrenceConstraint) tclass.getOccurrenceConstraints().iterator().next();
    assertTrue("occurrence is not set to EITHER",
           occ.getInternal() == OccurrenceConstraint.RESOURCE_EITHER);

    verifyMinMax(occ, 3, 4);
    verifySingleScopeSpec(occ, "#occtheme");
    verifySingleTypeSpec(occ, "#occtype");
  }

  public void testOccurrenceExternal() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "extocc.xml");

    assertTrue("schema did not have one topic class",
           schema.getTopicClasses().size() == 1);

    TopicClass tclass = schema.getTopicClass("something");
    assertTrue("topic class not found by ID",
           tclass != null);

    assertTrue("topic class did not have one occurrence constraint",
           tclass.getOccurrenceConstraints().size() == 1);

    OccurrenceConstraint occ =
      (OccurrenceConstraint) tclass.getOccurrenceConstraints().iterator().next();
    assertTrue("occurrence is not set to external",
           occ.getInternal() == OccurrenceConstraint.RESOURCE_EXTERNAL);

    verifyMinMax(occ, 0, CardinalityConstraintIF.INFINITY);
    verifySingleTypeSpec(occ, "#occtype");
  }
  
  public void testTopicRoleConstraint() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "trole.xml");

    TopicClass tclass = schema.getTopicClass("something");
    assertTrue("topic class not found by ID",
           tclass != null);

    assertTrue("topic class did not have one role constraint",
           tclass.getRoleConstraints().size() == 1);

    TopicRoleConstraint rc =
      (TopicRoleConstraint) tclass.getRoleConstraints().iterator().next();

    verifyMinMax(rc, 5, 6);
    verifySingleTypeSpec(rc, "#roletype");

    assertTrue("topic role did not have one allowed association type",
           rc.getAssociationTypes().size() == 1);

    TypeSpecification spec =
      (TypeSpecification) rc.getAssociationTypes().iterator().next();
    InternalTopicRefMatcher matcher =
      (InternalTopicRefMatcher) spec.getClassMatcher();
    assertTrue("topic matcher had wrong relative URI",
           matcher.getRelativeURI().equals("#assoctype"));
  }

  public void testAssocRoleConstraint() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("in", "arole.xml");

    assertTrue("schema did not have one association class",
           schema.getAssociationClasses().size() == 1);
    
    AssociationClass aclass =
      (AssociationClass) schema.getAssociationClasses().iterator().next();

    assertTrue("association class had scope spec",
           aclass.getScopeSpecification() == null);
    assertTrue("association class did not have one role constraint",
           aclass.getRoleConstraints().size() == 1);

    AssociationRoleConstraint rc =
      (AssociationRoleConstraint)aclass.getRoleConstraints().iterator().next();

    verifyMinMax(rc, 6, 7);
    verifySingleTypeSpec(rc, "#roletype");

    assertTrue("association role did not have one allowed player type",
           rc.getPlayerTypes().size() == 1);

    TypeSpecification spec =
      (TypeSpecification) rc.getPlayerTypes().iterator().next();
    InternalTopicRefMatcher matcher =
      (InternalTopicRefMatcher) spec.getClassMatcher();
    assertTrue("topic matcher had wrong relative URI",
           matcher.getRelativeURI().equals("#playertype"));
  }

  public void testForward() throws IOException, SchemaSyntaxException {
    OSLSchema schema = (OSLSchema) readSchema("schemas", "forward-ref.xml");

    // the real test here is: can we read it at all?
    // we also check the forward reference, to make sure it worked

    TopicClass tclass = schema.getTopicClass("tc43");
    assertTrue("Couldn't find topic class with ID 'tc43'",
               tclass != null);

    assertTrue("Topic class has no superclass!",
               tclass.getSuperclass() != null);
  }

  
  // --- Helper methods

  protected void verifyMinMax(CardinalityConstraintIF constraint,
                              int min, int max) {
    assertTrue("constraint did not have min=" + min,
           constraint.getMinimum() == min);
    assertTrue("base name constraint did not have max=" + max,
           constraint.getMaximum() == max);
  }
  
  protected void verifySingleScopeSpec(ScopedConstraintIF constraint,
                                       String uri) {
    ScopeSpecification spec = constraint.getScopeSpecification();
    assertTrue("constraint had no scope specification",
           spec != null);

    assertTrue("scope specification did not have one matcher",
           spec.getThemeMatchers().size() == 1);

    InternalTopicRefMatcher matcher =
      (InternalTopicRefMatcher) spec.getThemeMatchers().iterator().next();
    assertTrue("scope specification did not have one matcher",
           spec.getThemeMatchers().size() == 1);
    assertTrue("matcher had bad URI: " + matcher.getRelativeURI(),
           matcher.getRelativeURI().equals(uri));    
  }

  protected void verifySingleTypeSpec(TypedConstraintIF constraint,
                                      String uri) {
    TypeSpecification spec = constraint.getTypeSpecification();
    assertTrue("constraint had no type specification",
           spec != null);

    InternalTopicRefMatcher matcher =
      (InternalTopicRefMatcher) spec.getClassMatcher();
    assertTrue("topic matcher had wrong relative URI",
           matcher.getRelativeURI().equals(uri));
  }  
}
