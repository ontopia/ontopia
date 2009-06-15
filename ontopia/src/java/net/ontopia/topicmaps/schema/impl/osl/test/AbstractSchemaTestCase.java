
// $Id: AbstractSchemaTestCase.java,v 1.6 2004/09/06 10:13:54 grove Exp $

package net.ontopia.topicmaps.schema.impl.osl.test;

import java.io.File;
import java.io.IOException;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;
import net.ontopia.topicmaps.xml.test.AbstractXMLTestCase;

public class AbstractSchemaTestCase extends AbstractXMLTestCase {
  
  public AbstractSchemaTestCase(String name) {
    super(name);
  }

  // --- Utilities

  protected SchemaIF readSchema(String directory, String filename)
    throws IOException, SchemaSyntaxException {
    File file = new File(resolveFileName("schema" + File.separator + directory, filename));
    if (!file.getParentFile().exists())
      file.getParentFile().mkdirs();
    OSLSchemaReader reader = new OSLSchemaReader(file);
    return reader.read();
  }
  
  protected void writeSchema(String directory, String filename, OSLSchema schema)
    throws IOException, SchemaSyntaxException {
    File file = new File(resolveFileName("schema" + File.separator + directory, filename));
    if (!file.getParentFile().exists())
      file.getParentFile().mkdirs();
    OSLSchemaWriter writer = new OSLSchemaWriter(file, "utf-8");
    writer.write(schema);
  }

}
