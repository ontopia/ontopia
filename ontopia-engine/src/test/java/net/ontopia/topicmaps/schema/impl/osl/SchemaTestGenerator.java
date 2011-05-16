
// $Id: SchemaTestGenerator.java,v 1.10 2005/02/22 15:46:52 grove Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import net.ontopia.xml.Slf4jSaxErrorHandler;
import net.ontopia.xml.SAXTracker;
import net.ontopia.xml.ConfiguredXMLReaderFactory;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.test.AbstractXMLTestCase;
import net.ontopia.test.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaTestGenerator implements TestCaseGeneratorIF {
  static Logger log = LoggerFactory.getLogger(SchemaTestGenerator.class.getName());

  public Iterator generateTests() {
    String root = AbstractOntopiaTestCase.getTestDirectory();
    String base = root + File.separator + "schema" + File.separator;
        
    File config = new File(base + "config" + File.separator + "config.xml");
    if (!config.exists())
      throw new OntopiaRuntimeException("File " + config + 
                                        " does not exist!");

    return parse(config, base).iterator();
  }

  // --- Internal methods

  private Collection parse(File config, String base) {
    try {
      XMLReader parser = new ConfiguredXMLReaderFactory().createXMLReader();
    
      TestCaseContentHandler handler = new TestCaseContentHandler(base);
      parser.setContentHandler(handler);
      parser.setErrorHandler(new Slf4jSaxErrorHandler(log));
      parser.parse(config.toURL().toString());
      return handler.getTests();
    } catch (SAXException e) {
      throw new OntopiaRuntimeException(e);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  // --- Content handler for test case file

  class TestCaseContentHandler extends SAXTracker {
    private String base;
    private Collection tests;

    public TestCaseContentHandler(String base) {
      this.base = base;
      this.tests = new ArrayList();
    }

    public Collection getTests() {
      return tests;
    }
    
    public void startElement(String nsuri, String lname, String qname,
                             Attributes attrs) throws SAXException {

      if (qname == "test") 
        tests.add(new SchemaFileTestCase(attrs.getValue("topicmap"),
                                         attrs.getValue("schema"),
                                         attrs.getValue("valid").equalsIgnoreCase("yes")));

      super.startElement(nsuri, lname, qname, attrs);
    }
    
  }
  
  // --- Test case class
  
  public class SchemaFileTestCase extends AbstractSchemaTestCase {
    private String topicmap;
    private String schema;
    private boolean valid;
    
    public SchemaFileTestCase(String topicmap, String schema,
                              boolean valid) {
      super("testFile");
      this.topicmap = topicmap;
      this.schema = schema;
      this.valid = valid;
    }

    public void testFile() throws IOException, SchemaSyntaxException {
      OSLSchema schema = (OSLSchema) readSchema("schemas", this.schema);
      TopicMapIF topicmap = readTopicMap("topicmaps", this.topicmap);

      SchemaValidatorIF validator = schema.getValidator();
      try {
        validator.validate(topicmap);

        assertTrue("invalid topic map ("+ this.topicmap + ") validated with no errors", valid);
      } catch (SchemaViolationException e) {
        assertTrue("valid topic map (" + this.topicmap + ") had error: " + e.getMessage() +
               ", offender: " + e.getOffender(), !valid);
      }
    }

    protected TopicMapIF readTopicMap(String directory, String file)
      throws IOException {
      String filename = resolveFileName("schema" + File.separator + directory,
                                        file);
      TopicMapReaderIF reader = ImportExportUtils.getReader(filename);
      return reader.read();
    }
    
  }
}
