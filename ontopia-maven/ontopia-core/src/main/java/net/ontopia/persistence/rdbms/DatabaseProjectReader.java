
// $Id: DatabaseProjectReader.java,v 1.19 2008/08/29 12:55:19 geir.gronmo Exp $

package net.ontopia.persistence.rdbms;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.StreamUtils;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.PrettyPrinter;
import net.ontopia.xml.SAXTracker;

import org.xml.sax.Attributes;
import org.xml.sax.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributeListImpl;

/** 
 * INTERNAL: Class that can read a database schema definition from an
 * XML representation.
 */

public class DatabaseProjectReader {
    
  protected static final AttributeListImpl EMPTY_ATTR_LIST = new AttributeListImpl();

  private DatabaseProjectReader() { }

  /**
   * INTERNAL: Reads the database schema definition from the specified file.
   */
  public static Project loadProject(String filename) throws IOException, SAXException {
    return loadProject(StreamUtils.getInputStream(filename));
  }
  public static Project loadProject(InputStream istream) throws IOException, SAXException {
    return loadProject(new InputSource(istream));
  }
  public static Project loadProject(InputSource isource) throws IOException, SAXException {
    
    ProjectHandler handler = new ProjectHandler();
    XMLReader parser = new DefaultXMLReaderFactory().createXMLReader();    
    parser.setContentHandler(handler);
    parser.parse(isource);
    
    return handler.project;
  }
  
  public static void saveProject(Project project, String filename) throws IOException, SAXException {
    saveProject(project, filename, "utf-8");
  }
  
  public static void saveProject(Project project, String filename, String encoding) throws IOException, SAXException {
    PrintWriter print = new PrintWriter(new FileWriter(filename));
    saveProject(project, new PrettyPrinter(print, encoding));
    print.close();
  }

  public static void saveProject(Project project, DocumentHandler dh) throws SAXException {
    AttributeListImpl atts = new AttributeListImpl();
    
    dh.startDocument();

    dh.startElement("dbschema", EMPTY_ATTR_LIST);

    Iterator platforms = project.getDataTypePlatforms().iterator();
    if (platforms.hasNext()) {
      while (platforms.hasNext()) {        
        String platform = (String)platforms.next();
        
        atts.clear();
        atts.addAttribute("platform", "CDATA", platform);
        dh.startElement("datatypes", atts);
        
        Iterator datatypes = project.getDataTypes(platform).iterator();
        while (datatypes.hasNext()) {
          // Platform datatypes
          DataType datatype = (DataType)datatypes.next();
          atts.clear();
          atts.addAttribute("name", "CDATA", (datatype.getName()));
          atts.addAttribute("type", "CDATA", (datatype.getType()));
          atts.addAttribute("size", "CDATA", (datatype.getSize() == null ? "" : datatype.getSize()));
          atts.addAttribute("class", "CDATA", (datatype.isVariable() ? "variable" : "constant"));
          
          dh.startElement("datatype", atts);

          // Datatype properties
          Iterator properties = datatype.getProperties().iterator();
          while (properties.hasNext()) {
            String name = (String)properties.next();
            String value = datatype.getProperty(name);
            if (value != null) {
              atts.clear();
              atts.addAttribute("name", "CDATA", name);
              atts.addAttribute("value", "CDATA", value);
              dh.startElement("property", atts);
              dh.endElement("property");
            }
          }
          
          dh.endElement("datatype");
          
        }
      }
      dh.endElement("datatypes");
    }
    
    Iterator tables = project.getTables().iterator();
    while (tables.hasNext()) {
      Table table = (Table)tables.next();

      // Table attributes
      atts.clear();
      atts.addAttribute("name", "CDATA", table.getName());
      if (table.getShortName() != null)
        atts.addAttribute("short", "CDATA", table.getShortName());
      if (table.getPrimaryKeys() != null)
        atts.addAttribute("pks", "CDATA", StringUtils.join(table.getPrimaryKeys(), " "));
      dh.startElement("table", atts);

      // Table properties
      Iterator properties = table.getProperties().iterator();
      while (properties.hasNext()) {
        String name = (String)properties.next();
        String value = table.getProperty(name);
        if (value != null) {
          atts.clear();
          atts.addAttribute("name", "CDATA", name);
          atts.addAttribute("value", "CDATA", value);
          dh.startElement("property", atts);
          dh.endElement("property");
        }
      }

      Iterator columns = table.getColumns().iterator();
      while (columns.hasNext()) {
        Column column = (Column)columns.next();

        // Column attributes
        atts.clear();
        atts.addAttribute("name", "CDATA", column.getName());
        atts.addAttribute("type", "CDATA", column.getType());
        
        if (column.isReference()) {
          atts.addAttribute("reftab", "CDATA", column.getReferencedTable());
          atts.addAttribute("refcol", "CDATA", column.getReferencedColumn());
        }
        
        if (column.getSize() != null)
          atts.addAttribute("size", "CDATA", column.getName());
        if (column.isNullable())
          atts.addAttribute("null", "CDATA", "yes");
        if (column.getDefault() != null)
          atts.addAttribute("default", "CDATA", column.getDefault());
        dh.startElement("column", atts);
      
        // Column properties
        Iterator properties2 = column.getProperties().iterator();
        while (properties2.hasNext()) {
          String name = (String)properties2.next();
          String value = column.getProperty(name);
          if (value != null) {
            atts.clear();
            atts.addAttribute("name", "CDATA", name);
            atts.addAttribute("value", "CDATA", value);
            dh.startElement("property", atts);
            dh.endElement("property");
          }
        }        
        dh.endElement("column");        
      }
      
      dh.endElement("table");      
    }
    
    dh.endElement("dbschema");
    dh.endDocument();
  }
  
  static class ProjectHandler extends SAXTracker {

    static final String EL_DBSCHEMA = "dbschema";
    static final String EL_DATATYPES = "datatypes";
    static final String EL_DATATYPE = "datatype";
    static final String EL_TABLE = "table";
    static final String EL_COLUMN = "column";
    static final String EL_INDEX = "index";
    static final String EL_PROPERTY = "property";
    static final String EL_CREATE_ACTION = "create-action";
    static final String EL_DROP_ACTION = "drop-action";

    protected Project project;
    protected Map info;
    
    public ProjectHandler() {
      keepContentsOf("create-action");
      keepContentsOf("drop-action");
    }

    public void startDocument() {
      project = new Project();
      info = new HashMap();
    }

    public void endDocument() {
      info = null;
    }
    
    public void startElement(String uri, String name, String qname, Attributes atts) throws SAXException {
      super.startElement(uri, name, qname, atts);

      // System.out.println("S: " + name + ":" + openElements);
      
      if (qname == EL_COLUMN) {
        // Instantiate new column instance
        Column column = new Column();

        String cname = atts.getValue("name");
        if (cname == null) 
          throw new OntopiaRuntimeException("column.name must be specified: " + cname); 
        column.setName(cname);

        String type = atts.getValue("type");
        if (type == null) 
          throw new OntopiaRuntimeException("column.type must be specified: " + cname);

        // if (project.getDataTypeByName(type) == null)
        //   throw new OntopiaRuntimeException("Unknown datatype:: " + type);
          
        column.setType(type);
        
        String size = atts.getValue("size");
        if (size != null) column.setSize(size);

        String default_value = atts.getValue("default");
        if (default_value != null) column.setDefault(default_value);

        String reftable = atts.getValue("reftab");
        if (reftable != null) column.setReferencedTable(reftable);

        String refcol = atts.getValue("refcol");
        if ((refcol == null && reftable != null) ||
            (refcol != null && reftable == null))
          throw new OntopiaRuntimeException("column.refcol and reftable must both specified:" + cname); 
        if (refcol != null) column.setReferencedColumn(refcol);

        String nullable = atts.getValue("null");
        if (nullable == null || nullable.equals("no"))
          column.setNullable(false);
        else
          column.setNullable(true);
        
        Table table = (Table)info.get(EL_TABLE);
        // Add column to table
        table.addColumn(column);

        info.put(EL_COLUMN, column);
      
      } else if (qname == EL_INDEX) {
        // Instantiate new index instance
        Index index = new Index();

        String iname = atts.getValue("name");
        if (iname == null) 
          throw new OntopiaRuntimeException("index.name must be specified: " + iname); 
        index.setName(iname);

        String isname = atts.getValue("short");
        index.setShortName(isname);

        String icolumns = atts.getValue("columns");
        if (icolumns == null) 
          throw new OntopiaRuntimeException("index.columns must be specified: " + icolumns); 
        index.setColumns(StringUtils.split(icolumns, ","));
        
        Table table = (Table)info.get(EL_TABLE);
        // Add index to table
        table.addIndex(index);

        info.put(EL_INDEX, index);
        
      } else if (qname == EL_TABLE) {
        // Instantiate new table instance
        Table table = new Table();

        String tname = atts.getValue("name");
        if (tname == null) 
          throw new OntopiaRuntimeException("table.name must be specified: " + tname);  
        table.setName(tname);

        String tsname = atts.getValue("short");
        table.setShortName(tsname);
        
        String pkeys = atts.getValue("pks");
        if (pkeys != null)
          table.setPrimaryKeys(StringUtils.split(pkeys, " "));

        // Add table to project
        project.addTable(table);
        info.put(EL_TABLE, table);

      } else if (qname == EL_DATATYPES) {
        String platform = atts.getValue("platform");
        if (platform == null) 
          throw new OntopiaRuntimeException("datatypes.platform must be specified: " + platform);
        
        info.put(EL_DATATYPES, platform);

      } else if (qname == EL_DATATYPE) {
        // Instantiate new datatype instance
        DataType datatype = new DataType();
        String platform = (String)info.get(EL_DATATYPES);
        
        String dname = atts.getValue("name");
        if (dname == null) 
          throw new OntopiaRuntimeException("datatype.name must be specified: " + dname);
        datatype.setName(dname);

        String type = atts.getValue("type");
        if (type == null) 
          throw new OntopiaRuntimeException("datatype.type must be specified: " + dname);       
        datatype.setType(type);

        String klass = atts.getValue("class");
        if (klass == null || klass.equals("variable"))
          datatype.setVariable(true);
        else
          datatype.setVariable(false);
        
        String size = atts.getValue("size");
        if (datatype.isVariable()) {
          if (size == null)
            throw new OntopiaRuntimeException("datatype.size must be specified: " + dname);     
          datatype.setSize(size);
        }

        // Add table to project
        project.addDataType(datatype, platform);        
        info.put(EL_DATATYPE, datatype);

      } else if (qname == EL_PROPERTY) {
        if (info.containsKey(EL_DATATYPE)) {
          String propname = atts.getValue("name");
          if (propname == null)
              throw new OntopiaRuntimeException("property.name must be specified.");     
          String value = atts.getValue("value");
          if (value == null)
              throw new OntopiaRuntimeException("property.value must be specified.");
          DataType datatype = (DataType)info.get(EL_DATATYPE);
          datatype.addProperty(propname, value);
        }
        else if (info.containsKey(EL_COLUMN)) {
          String propname = atts.getValue("name");
          if (propname == null)
              throw new OntopiaRuntimeException("property.name must be specified.");     
          String value = atts.getValue("value");
          if (value == null)
              throw new OntopiaRuntimeException("property.value must be specified.");
          Column column = (Column)info.get(EL_COLUMN);
          column.addProperty(propname, value);
        }
        else if (info.containsKey(EL_TABLE)) {
          String propname = atts.getValue("name");
          if (propname == null)
              throw new OntopiaRuntimeException("property.name must be specified.");     
          String value = atts.getValue("value");
          if (value == null)
              throw new OntopiaRuntimeException("property.value must be specified.");
          Table table = (Table)info.get(EL_TABLE);
          table.addProperty(propname, value);
        }
        else
          throw new OntopiaRuntimeException("property element in unknown parent." + info); 
          
      } else if (qname == EL_CREATE_ACTION) {
        String platform = atts.getValue("platform");
        if (platform == null) 
          throw new OntopiaRuntimeException("create-action.platform must be specified: " + platform); 

        info.put(EL_CREATE_ACTION, platform); 
      } else if (qname == EL_DROP_ACTION) {
        String platform = atts.getValue("platform");
        if (platform == null) 
          throw new OntopiaRuntimeException("drop-action.platform must be specified: " + platform); 

        info.put(EL_DROP_ACTION, platform); 
      } else if (qname == EL_DBSCHEMA) {
      }
    }
    
    public void endElement(String uri, String name, String qname) throws SAXException {
      super.endElement(uri, name, qname);

      if (qname == EL_DATATYPES) {
        // Remove types entry
        info.remove(EL_DATATYPES);
      } else if (qname == EL_DATATYPE) {
        // Remove type entry
        info.remove(EL_DATATYPE);
      } else if (qname == EL_TABLE) {
        // Remove table entry
        info.remove(EL_TABLE);
      } else if (qname == EL_COLUMN) {
        // Remove column entry
        info.remove(EL_COLUMN);
      } else if (qname == EL_INDEX) {
        // Remove index entry
        info.remove(EL_INDEX);
      } else if (qname == EL_CREATE_ACTION) {
        project.addCreateAction((String)info.get(EL_CREATE_ACTION), content.toString());
        // Remove table entry
        info.remove(EL_CREATE_ACTION);
      } else if (qname == EL_DROP_ACTION) {
        project.addDropAction((String)info.get(EL_DROP_ACTION), content.toString());
        // Remove table entry
        info.remove(EL_DROP_ACTION);
      } else if (qname == EL_PROPERTY) {
      } else if (qname == EL_DBSCHEMA) {
      } else if (qname == EL_INDEX) {
      } else {
        System.out.println("Ignoring: " + name);        
      }
      // System.out.println("E: " + name);
      
    }

    protected Map parseAttribs(String content) {
      Map result = new HashMap();
      
      String[] fields = StringUtils.split(content.toString(), "\n");
      for (int i=0; i < fields.length; i++) {
        String field = fields[i];
        String[] entry = StringUtils.split(field, "=");
        if (entry.length != 2) {
          // System.out.println("Ignoring: '" + field + "' (" + entry.length + ")");
          continue;
        }
        result.put(entry[0], entry[1]);
      }
      return result;
    }
    
  }

  public static void main(String[] args) throws IOException, SAXException {
    DatabaseProjectReader preader = new DatabaseProjectReader();
    preader.loadProject(args[0]);
  }
  
}
