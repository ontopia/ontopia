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

import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;

public abstract class AbstractSchemaTestCase {
  
  protected final static String testdataDirectory = "schema";

  protected SchemaIF readSchema(String directory, String filename)
    throws IOException, SchemaSyntaxException {

    OSLSchemaReader reader;
    if ("out".equals(directory)) {
      File file = TestFileUtils.getTestOutputFile(testdataDirectory, directory, filename);
      reader = new OSLSchemaReader(file);
    } else {
      String file = TestFileUtils.getTestInputFile(testdataDirectory, directory, filename);
      reader = new OSLSchemaReader(file);
    }
    return reader.read();
  }
  
  protected void writeSchema(String directory, String filename, OSLSchema schema)
    throws IOException, SchemaSyntaxException {
    File file = TestFileUtils.getTestOutputFile(testdataDirectory, directory, filename);
    OSLSchemaWriter writer = new OSLSchemaWriter(file, "utf-8");
    writer.write(schema);
  }

}
