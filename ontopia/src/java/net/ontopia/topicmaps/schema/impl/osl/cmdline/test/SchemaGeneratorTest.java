// $Id: SchemaGeneratorTest.java,v 1.7 2002/11/21 09:23:11 grove Exp $

package net.ontopia.topicmaps.schema.impl.osl.cmdline.test;

import java.io.*;
import java.util.*;

import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;
import net.ontopia.topicmaps.schema.impl.osl.cmdline.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;

/**
 * INTERNAL: Test case that check whether schemas generated from topic
 * maps validate against the source topic maps.
 */

public class SchemaGeneratorTest extends AbstractTopicMapTestCase {
  
  private Collection tests;

  private void generateTestCases() {
    tests = new ArrayList();

    tests.add("schema.xtm");
    tests.add("test_topicmap.ltm");
    //! tests.add("../../nav2/topicmaps/opera.xtm");
  }


  public SchemaGeneratorTest(String name) {
    super(name);
    generateTestCases();
  }


  public void setUp() {
    String root = getTestDirectory();
    verifyDirectory(root, "schema", "out");
  }

  public void testGenerator() throws Exception {
    Iterator it = tests.iterator();
    while (it.hasNext()) {
      String filename = (String)it.next();
      String tmfile = resolveFileName("schema" + File.separator + "in", filename);
      String schemafile = resolveFileName("schema" + File.separator + "out", filename + ".xml");
      Generate gen = new Generate();
      OSLSchema schema = gen.createSchema(tmfile);
      new OSLSchemaWriter(new File(schemafile), "utf-8").write(schema);
      try {
        validate(tmfile, schemafile);
      } catch (SchemaViolationException e) {
        fail("Generated schema '" + schemafile + "' had validation errors: " + e.getMessage());
      }
    }
  }

  protected void validate(String tmfile, String schemafile) throws Exception {
    TopicMapIF topicmap = ImportExportUtils.getReader(tmfile).read();
    OSLSchemaReader reader = new OSLSchemaReader(new File(schemafile));
    OSLSchema schema = (OSLSchema)reader.read();
    SchemaValidatorIF validator = schema.getValidator();
    validator.validate(topicmap);
  }

}





