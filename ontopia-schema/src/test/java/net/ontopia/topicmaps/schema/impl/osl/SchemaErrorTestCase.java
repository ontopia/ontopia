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
import java.util.List;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SchemaErrorTestCase extends AbstractSchemaTestCase {

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "error", ".xml");
  }

    private final String base;
    private final String filename;

    public SchemaErrorTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testSchemaError() throws IOException, SchemaSyntaxException {
      TestFileUtils.verifyDirectory(base, "out");
      try {
        readSchema("error", filename);
        Assert.fail("Read bad schema " + filename + " and found no errors");
      } catch (SchemaSyntaxException sse) {
        // ok
      } catch (IllegalArgumentException iae) {
        // for negmin.xml
        Assert.assertEquals("Cannot set minimum to negative value", iae.getMessage());
      }
    }
}
