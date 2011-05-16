
// $Id: SchemaErrorTestGenerator.java,v 1.7 2003/07/28 10:08:39 larsga Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import net.ontopia.test.*;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;
import net.ontopia.topicmaps.xml.test.AbstractXMLTestCase;

public class SchemaErrorTestGenerator implements TestCaseGeneratorIF {

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractOntopiaTestCase.getTestDirectory();
    String base = root + File.separator + "schema" + File.separator + "error";
        
    File indir = new File(base + File.separator);
        
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
        
    for (int ix = 0; ix < infiles.length; ix++) {
      String name = infiles[ix].getName();
      if (name.endsWith(".xml")) 
        tests.add(new SchemaErrorTestCase(base, name));
    }

    return tests.iterator();
  }

  // --- Test case class

  public class SchemaErrorTestCase extends AbstractSchemaTestCase {
    private String base;
    private String filename;
        
    public SchemaErrorTestCase(String base, String filename) {
      super("testBadSchema");
      this.base = base;
      this.filename = filename;
    }

    // --- The test

    public void testBadSchema() throws IOException {
      try {
        readSchema("error", filename);
        fail("Read bad schema " + filename + " and found no errors");
      }
      catch (SchemaSyntaxException e) {
      }
    }
  }
}
