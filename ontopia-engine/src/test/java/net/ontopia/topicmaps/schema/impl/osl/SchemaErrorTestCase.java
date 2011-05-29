
package net.ontopia.topicmaps.schema.impl.osl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Collection;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.utils.ResourcesDirectoryReader;
import net.ontopia.utils.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SchemaErrorTestCase extends AbstractSchemaTestCase {

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "error", ".xml");
  }

    private final String base;
    private final String filename;

    public SchemaErrorTestCase(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testSchemaError() throws IOException, SchemaSyntaxException {
      FileUtils.verifyDirectory(base, "out");
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
