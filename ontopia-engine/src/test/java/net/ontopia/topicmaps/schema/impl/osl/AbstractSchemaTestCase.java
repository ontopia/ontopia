
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
