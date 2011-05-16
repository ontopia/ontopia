
// $Id: AbstractSchemaTestCase.java,v 1.6 2004/09/06 10:13:54 grove Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.utils.FileUtils;

public abstract class AbstractSchemaTestCase {
  
  protected final static String testdataDirectory = "schema";

  protected SchemaIF readSchema(String directory, String filename)
    throws IOException, SchemaSyntaxException {

    OSLSchemaReader reader;
    if ("out".equals(directory)) {
      File file = FileUtils.getTestOutputFile(testdataDirectory, directory, filename);
      reader = new OSLSchemaReader(file);
    } else {
      String file = FileUtils.getTestInputFile(testdataDirectory, directory, filename);
      reader = new OSLSchemaReader(file);
    }
    return reader.read();
  }
  
  protected void writeSchema(String directory, String filename, OSLSchema schema)
    throws IOException, SchemaSyntaxException {
    File file = FileUtils.getTestOutputFile(testdataDirectory, directory, filename);
    OSLSchemaWriter writer = new OSLSchemaWriter(file, "utf-8");
    writer.write(schema);
  }

}
