
// $Id: XMLCollectionReader.java,v 1.16 2005/07/01 07:23:00 grove Exp $

package net.ontopia.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.ontopia.utils.URIUtils;
import net.ontopia.xml.DefaultXMLReaderFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * INTERNAL: The class used to read XML files describing test
 * collections.
 */

public class XMLCollectionReader {
  private Map testGroups;
    
  public XMLCollectionReader(String collectionFile) 
    throws SAXException, IOException {
    testGroups = new HashMap();
    readFile(collectionFile);
  }

  public Iterator getTests(String groupName) {
    TestGroup group = (TestGroup) testGroups.get(groupName);
    if (group == null)
      return null;
    else
      return group.getTests();
  }

  // --- Internal methods

  private void readFile(String collectionFile) 
    throws SAXException, IOException {
    XMLReader reader = new DefaultXMLReaderFactory().createXMLReader();
    reader.setContentHandler(new TestCollectionHandler(testGroups));
    reader.parse(URIUtils.toURL(new java.io.File(collectionFile)).toString());
  }

  // --- Test group class

  class TestGroup {
    private String name;
    private List   subgroups;
    private List   tests;

    public TestGroup(String name) {
      this.name = name;
      subgroups = new ArrayList();
      tests = new ArrayList();
    }

    public String getName() {
      return name;
    }

    public void addTest(String testName) {
      if (!tests.contains(testName))
        tests.add(testName);
    }

    public void addGroup(TestGroup subgroup) {
      if (!subgroups.contains(subgroup))
        subgroups.add(subgroup);
    }

    public Iterator getTests() {
      ArrayList all = new ArrayList();
      all.addAll(tests);

      Iterator it = subgroups.iterator();
      while (it.hasNext()) {
        TestGroup subgroup = (TestGroup) it.next();

        Iterator subit = subgroup.getTests();
        while (subit.hasNext()) 
          all.add(subit.next());
      }

      return all.iterator();
    }
  }
    
  // --- Internal ContentHandler

  class TestCollectionHandler extends org.xml.sax.helpers.DefaultHandler {
    private char[]    buffer;
    private int       nextChar;
    private Map       groups;
    private Locator   locator;
    private URL       base;
    private Stack     openelems;

    private TestGroup current;
    private String    propname;
        
    public TestCollectionHandler(Map groups) {
      buffer = new char[1024];
      nextChar = 0;
      this.groups = groups;
      openelems = new Stack();
    }

    public void setDocumentLocator(Locator locator) {
      this.locator = locator;
    }

    public void startDocument() throws SAXException {
      try {
        base = new URL(locator.getSystemId());
      }
      catch (MalformedURLException e) {
        throw new SAXException(e);
      }
    }
        
    public void startElement(String nsuri, String lname, String qname,
                             Attributes attrs) {
      if (lname == "") lname = qname; // workaround
      lname = lname.intern(); // workaround
      
      nextChar = 0;
      openelems.push(lname);
      if (lname == "group") 
        current = null;
    }

    public void characters(char[] content, int offset, int length) {
      System.arraycopy(content, offset, buffer, nextChar, length);
      nextChar += length;
    }

    public void endElement(String nsuri, String lname, String qname) throws SAXException {
      if (lname == "") lname = qname; // ugly workaround
      lname = lname.intern(); // ditto
      
      openelems.pop(); // parent is now top-most

      if (lname == "name") {
        String parent = (String) openelems.peek();

        if (parent == "group")     // group / name
          current = getGroup(new String(buffer, 0, nextChar));
        else                       // property / name
          propname = new String(buffer, 0, nextChar);
      }

      else if (lname == "value")     // property / value
        System.setProperty(propname, new String(buffer, 0, nextChar));
            
      else if (lname == "test")      // group / test
        current.addTest(new String(buffer, 0, nextChar));

      else if (lname == "group-ref") // group / group-ref
        current.addGroup(getGroup(new String(buffer, 0, nextChar)));

      else if (lname == "include") { // test-collection / include
        String file = new String(buffer, 0, nextChar);
        try {
          // handling relative URLs with respect to base
          file = new URL(base, file).toString();
        }
        catch (MalformedURLException e) {
          System.err.println("Malformed reference: " + file);
          return;
        }
        XMLReader reader = new DefaultXMLReaderFactory().createXMLReader();
        reader.setContentHandler(this);
        try {
          reader.parse(file);
        }
        catch (IOException e) {
          System.err.println("Couldn't find included file: " + file);
        }
        catch (SAXException e) {
          System.err.println("Parse error in included file " + file + ":" +
                             e.getMessage());
        }
      }
    }
        
    // --- Internal methods
        
    private TestGroup getGroup(String name) {
      TestGroup group = (TestGroup) groups.get(name);
      if (group == null) {
        group = new TestGroup(name);
        groups.put(name, group);
      }
      return group;
    }
  }
    
}
