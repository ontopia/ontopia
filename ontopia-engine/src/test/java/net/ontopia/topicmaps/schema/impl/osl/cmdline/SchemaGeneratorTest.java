
package net.ontopia.topicmaps.schema.impl.osl.cmdline;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;

import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * INTERNAL: Test case that check whether schemas generated from topic
 * maps validate against the source topic maps.
 */

@RunWith(Parameterized.class)
public class SchemaGeneratorTest extends TestCase {
  
  private final static String testdataDirectory = "schema";

  @Parameters
  public static List generateTests() {
    ResourcesFilterIF filter = new ResourcesFilterIF() {
      public boolean ok(String resourcePath) {
        if (resourcePath.endsWith("schema.xtm")) return true;
        if (resourcePath.endsWith("test_topicmap.ltm")) return true;
        //! if (resourcePath.endsWith("../../nav2/topicmaps/opera.xtm")) return true;
        return false;
      }
    };
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", filter);
  }

  private String base;
  private String filename;

  public SchemaGeneratorTest(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
  }

  @Test
  public void validate() throws Exception {
    TestFileUtils.verifyDirectory(base, "out");
    String tmfile = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
    String schemafile = base + File.separator + "out" + File.separator + filename + ".xml";
    Generate gen = new Generate();
    OSLSchema schemaOutput = gen.createSchema(tmfile);
    new OSLSchemaWriter(new File(schemafile), "utf-8").write(schemaOutput);
    try {
      TopicMapIF topicmap = ImportExportUtils.getReader(tmfile).read();
      OSLSchemaReader reader = new OSLSchemaReader(new File(schemafile));
      OSLSchema schemaInput = (OSLSchema)reader.read();
      SchemaValidatorIF validator = schemaInput.getValidator();
      validator.validate(topicmap);
    } catch (SchemaViolationException e) {
      Assert.fail("Generated schema '" + schemafile + "' had validation errors: " + e.getMessage());
    }
  }

}





