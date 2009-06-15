
// $Id: SchemaWriterTest.java,v 1.1 2003/03/16 14:17:07 larsga Exp $

package net.ontopia.topicmaps.schema.impl.osl.test;

import java.io.File;
import java.io.IOException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;
import net.ontopia.topicmaps.xml.test.AbstractXMLTestCase;

public class SchemaWriterTest extends AbstractSchemaTestCase {
  private OSLSchema schema;
  
  public SchemaWriterTest(String name) {
    super(name);
  }

  public void setUp() {
    try {
      schema = new OSLSchema(new URILocator("http://www.ontopia.net"));
    } catch (java.net.MalformedURLException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    }
  }
  
  // --- Test methods

  public void testEmpty() throws IOException, SchemaSyntaxException {
    writeSchema("out", "empty.xml", schema);
    
    schema = (OSLSchema) readSchema("out", "empty.xml");
    
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
  }

  public void testEmptyClass() throws IOException, SchemaSyntaxException {
    TopicClass klass = new TopicClass(schema, "bingo");
    TypeSpecification typespec = new TypeSpecification();
    typespec.setClassMatcher(new SubjectIndicatorMatcher(new URILocator("http://psi.ontopia.net/bongo/#bongo")));
    klass.setTypeSpecification(typespec);
    schema.addTopicClass(klass);
    writeSchema("out", "emptyclass.xml", schema);
    
    schema = (OSLSchema) readSchema("out", "emptyclass.xml");
    assertTrue("schema had non-empty rule set collection",
               schema.getRuleSets().isEmpty());
    assertTrue("wrong number of classes in schema",
               schema.getTopicClasses().size() == 1);
    assertTrue("schema had non-empty association class collection",
               schema.getAssociationClasses().isEmpty());
    assertTrue("topic class not found",
               schema.getTopicClass("bingo") != null);
  }

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
    
    assertTrue("schema had non-empty rule set collection",
               schema.getRuleSets().isEmpty());
    assertTrue("wrong number of classes in schema",
               schema.getTopicClasses().size() == 1);
    assertTrue("schema had non-empty association class collection",
               schema.getAssociationClasses().isEmpty());
    assertTrue("topic class not found",
               schema.getTopicClass("bingo") != null);

    klass = schema.getTopicClass("bingo");
    assertTrue("topic class lost occurrence constraint",
               klass.getOccurrenceConstraints().size() == 1);
  }
}
