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

package net.ontopia.topicmaps.schema.impl.osl.cmdline;

import java.io.File;
import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.schema.core.SchemaValidatorIF;
import net.ontopia.topicmaps.schema.core.SchemaViolationException;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchema;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchemaReader;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchemaWriter;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;
import net.ontopia.utils.TestFileUtils;
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
public class SchemaGeneratorTest {
  
  private final static String testdataDirectory = "schema";

  private String base;
  private String filename;

  @Parameters
  public static List generateTests() {
    ResourcesFilterIF filter = new ResourcesFilterIF() {
      @Override
      public boolean ok(String resourcePath) {
        if (resourcePath.endsWith("schema.xtm")) return true;
        if (resourcePath.endsWith("test_topicmap.ltm")) return true;
        //! if (resourcePath.endsWith("../../nav2/topicmaps/opera.xtm")) return true;
        return false;
      }
    };
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", filter);
  }

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
