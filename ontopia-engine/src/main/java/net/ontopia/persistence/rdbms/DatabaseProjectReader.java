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

package net.ontopia.persistence.rdbms;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.PrettyPrinter;
import net.ontopia.xml.SAXTracker;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/** 
 * INTERNAL: Class that can read a database schema definition from an
 * XML representation.
 */

public class DatabaseProjectReader {
  private static final String PROPERTY = "property";
  private static final String VALUE = "value";
  private static final String SIZE = "size";
  private static final String TYPE = "type";
  private static final String NAME = "name";
  private static final String PLATFORM = "platform";
  private static final String CDATA = "CDATA";
    
  protected static final AttributesImpl EMPTY_ATTR_LIST = new AttributesImpl();
  protected static final String EMPTY_NAMESPACE = "";
  protected static final String EMPTY_LOCALNAME = "";

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
    XMLReader parser = DefaultXMLReaderFactory.createXMLReader();    
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

  public static void saveProject(Project project, ContentHandler dh) throws SAXException {
    AttributesImpl atts = new AttributesImpl();
    
    dh.startDocument();

    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "dbschema", EMPTY_ATTR_LIST);

    Iterator<String> platforms = project.getDataTypePlatforms().iterator();
    if (platforms.hasNext()) {
      while (platforms.hasNext()) {        
        String platform = platforms.next();
        
        atts.clear();
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, PLATFORM, CDATA, platform);
        dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "datatypes", atts);
        
        Iterator<DataType> datatypes = project.getDataTypes(platform).iterator();
        while (datatypes.hasNext()) {
          // Platform datatypes
          DataType datatype = datatypes.next();
          atts.clear();
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, NAME, CDATA, (datatype.getName()));
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, TYPE, CDATA, (datatype.getType()));
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, SIZE, CDATA, (datatype.getSize() == null ? "" : datatype.getSize()));
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "class", CDATA, (datatype.isVariable() ? "variable" : "constant"));
          
          dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "datatype", atts);

          // Datatype properties
          Iterator<String> properties = datatype.getProperties().iterator();
          while (properties.hasNext()) {
            String name = properties.next();
            String value = datatype.getProperty(name);
            if (value != null) {
              atts.clear();
              atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, NAME, CDATA, name);
              atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, VALUE, CDATA, value);
              dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, PROPERTY, atts);
              dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, PROPERTY);
            }
          }
          
          dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "datatype");
          
        }
      }
      dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "datatypes");
    }
    
    Iterator<Table> tables = project.getTables().iterator();
    while (tables.hasNext()) {
      Table table = tables.next();

      // Table attributes
      atts.clear();
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, NAME, CDATA, table.getName());
      if (table.getShortName() != null) {
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "short", CDATA, table.getShortName());
      }
      if (table.getPrimaryKeys() != null) {
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "pks", CDATA, StringUtils.join(table.getPrimaryKeys(), " "));
      }
      dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "table", atts);

      // Table properties
      Iterator<String> properties = table.getProperties().iterator();
      while (properties.hasNext()) {
        String name = properties.next();
        String value = table.getProperty(name);
        if (value != null) {
          atts.clear();
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, NAME, CDATA, name);
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, VALUE, CDATA, value);
          dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, PROPERTY, atts);
          dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, PROPERTY);
        }
      }

      Iterator<Column> columns = table.getColumns().iterator();
      while (columns.hasNext()) {
        Column column = columns.next();

        // Column attributes
        atts.clear();
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, NAME, CDATA, column.getName());
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, TYPE, CDATA, column.getType());
        
        if (column.isReference()) {
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "reftab", CDATA, column.getReferencedTable());
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "refcol", CDATA, column.getReferencedColumn());
        }
        
        if (column.getSize() != null) {
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, SIZE, CDATA, column.getName());
        }
        if (column.isNullable()) {
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "null", CDATA, "yes");
        }
        if (column.getDefault() != null) {
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "default", CDATA, column.getDefault());
        }
        dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "column", atts);
      
        // Column properties
        Iterator<String> properties2 = column.getProperties().iterator();
        while (properties2.hasNext()) {
          String name = properties2.next();
          String value = column.getProperty(name);
          if (value != null) {
            atts.clear();
            atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, NAME, CDATA, name);
            atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, VALUE, CDATA, value);
            dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, PROPERTY, atts);
            dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, PROPERTY);
          }
        }        
        dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "column");        
      }
      
      dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "table");      
    }
    
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "dbschema");
    dh.endDocument();
  }
  
  static class ProjectHandler extends SAXTracker {

    private static final String EL_DBSCHEMA = "dbschema";
    private static final String EL_DATATYPES = "datatypes";
    private static final String EL_DATATYPE = "datatype";
    private static final String EL_TABLE = "table";
    private static final String EL_COLUMN = "column";
    private static final String EL_INDEX = "index";
    private static final String EL_PROPERTY = PROPERTY;
    private static final String EL_CREATE_ACTION = "create-action";
    private static final String EL_DROP_ACTION = "drop-action";

    protected Project project;
    protected Map<String, Object> info;
    
    public ProjectHandler() {
      keepContentsOf("create-action");
      keepContentsOf("drop-action");
    }

    @Override
    public void startDocument() {
      project = new Project();
      info = new HashMap<String, Object>();
    }

    @Override
    public void endDocument() {
      info = null;
    }
    
    @Override
    public void startElement(String uri, String name, String qname, Attributes atts) throws SAXException {
      super.startElement(uri, name, qname, atts);

      // System.out.println("S: " + name + ":" + openElements);
      
      if (EL_COLUMN.equals(qname)) {
        // Instantiate new column instance
        Column column = new Column();

        String cname = atts.getValue(NAME);
        if (cname == null) {
          throw new OntopiaRuntimeException("column.name must be specified: " + cname);
        } 
        column.setName(cname);

        String type = atts.getValue(TYPE);
        if (type == null) {
          throw new OntopiaRuntimeException("column.type must be specified: " + cname);
        }

        // if (project.getDataTypeByName(type) == null)
        //   throw new OntopiaRuntimeException("Unknown datatype:: " + type);
          
        column.setType(type);
        
        String size = atts.getValue(SIZE);
        if (size != null) {
          column.setSize(size);
        }

        String default_value = atts.getValue("default");
        if (default_value != null) {
          column.setDefault(default_value);
        }

        String reftable = atts.getValue("reftab");
        if (reftable != null) {
          column.setReferencedTable(reftable);
        }

        String refcol = atts.getValue("refcol");
        if ((refcol == null && reftable != null) ||
            (refcol != null && reftable == null)) {
          throw new OntopiaRuntimeException("column.refcol and reftable must both specified:" + cname);
        } 
        if (refcol != null) {
          column.setReferencedColumn(refcol);
        }

        String nullable = atts.getValue("null");
        if (nullable == null || nullable.equals("no")) {
          column.setNullable(false);
        } else {
          column.setNullable(true);
        }
        
        Table table = (Table)info.get(EL_TABLE);
        // Add column to table
        table.addColumn(column);

        info.put(EL_COLUMN, column);
      
      } else if (EL_INDEX.equals(qname)) {
        // Instantiate new index instance
        Index index = new Index();

        String iname = atts.getValue(NAME);
        if (iname == null) {
          throw new OntopiaRuntimeException("index.name must be specified: " + iname);
        } 
        index.setName(iname);

        String isname = atts.getValue("short");
        index.setShortName(isname);

        String icolumns = atts.getValue("columns");
        if (icolumns == null) {
          throw new OntopiaRuntimeException("index.columns must be specified: " + icolumns);
        } 
        index.setColumns(StringUtils.split(icolumns, ","));
        
        Table table = (Table)info.get(EL_TABLE);
        // Add index to table
        table.addIndex(index);

        info.put(EL_INDEX, index);
        
      } else if (EL_TABLE.equals(qname)) {
        // Instantiate new table instance
        Table table = new Table();

        String tname = atts.getValue(NAME);
        if (tname == null) {
          throw new OntopiaRuntimeException("table.name must be specified: " + tname);
        }  
        table.setName(tname);

        String tsname = atts.getValue("short");
        table.setShortName(tsname);
        
        String pkeys = atts.getValue("pks");
        if (pkeys != null) {
          table.setPrimaryKeys(StringUtils.split(pkeys));
        }

        // Add table to project
        project.addTable(table);
        info.put(EL_TABLE, table);

      } else if (EL_DATATYPES.equals(qname)) {
        String platform = atts.getValue(PLATFORM);
        if (platform == null) {
          throw new OntopiaRuntimeException("datatypes.platform must be specified: " + platform);
        }
        
        info.put(EL_DATATYPES, platform);

      } else if (EL_DATATYPE.equals(qname)) {
        // Instantiate new datatype instance
        DataType datatype = new DataType();
        String platform = (String)info.get(EL_DATATYPES);
        
        String dname = atts.getValue(NAME);
        if (dname == null) {
          throw new OntopiaRuntimeException("datatype.name must be specified: " + dname);
        }
        datatype.setName(dname);

        String type = atts.getValue(TYPE);
        if (type == null) {
          throw new OntopiaRuntimeException("datatype.type must be specified: " + dname);
        }       
        datatype.setType(type);

        String klass = atts.getValue("class");
        if (klass == null || klass.equals("variable")) {
          datatype.setVariable(true);
        } else {
          datatype.setVariable(false);
        }
        
        String size = atts.getValue(SIZE);
        if (datatype.isVariable()) {
          if (size == null) {
            throw new OntopiaRuntimeException("datatype.size must be specified: " + dname);
          }     
          datatype.setSize(size);
        }

        // Add table to project
        project.addDataType(datatype, platform);        
        info.put(EL_DATATYPE, datatype);

      } else if (EL_PROPERTY.equals(qname)) {
        if (info.containsKey(EL_DATATYPE)) {
          String propname = atts.getValue(NAME);
          if (propname == null) {
            throw new OntopiaRuntimeException("property.name must be specified.");
          }     
          String value = atts.getValue(VALUE);
          if (value == null) {
            throw new OntopiaRuntimeException("property.value must be specified.");
          }
          DataType datatype = (DataType)info.get(EL_DATATYPE);
          datatype.addProperty(propname, value);
        }
        else if (info.containsKey(EL_COLUMN)) {
          String propname = atts.getValue(NAME);
          if (propname == null) {
            throw new OntopiaRuntimeException("property.name must be specified.");
          }     
          String value = atts.getValue(VALUE);
          if (value == null) {
            throw new OntopiaRuntimeException("property.value must be specified.");
          }
          Column column = (Column)info.get(EL_COLUMN);
          column.addProperty(propname, value);
        }
        else if (info.containsKey(EL_TABLE)) {
          String propname = atts.getValue(NAME);
          if (propname == null) {
            throw new OntopiaRuntimeException("property.name must be specified.");
          }     
          String value = atts.getValue(VALUE);
          if (value == null) {
            throw new OntopiaRuntimeException("property.value must be specified.");
          }
          Table table = (Table)info.get(EL_TABLE);
          table.addProperty(propname, value);
        }
        else {
          throw new OntopiaRuntimeException("property element in unknown parent." + info);
        } 
          
      } else if (EL_CREATE_ACTION.equals(qname)) {
        String platform = atts.getValue(PLATFORM);
        if (platform == null) {
          throw new OntopiaRuntimeException("create-action.platform must be specified: " + platform);
        } 

        info.put(EL_CREATE_ACTION, platform); 
      } else if (EL_DROP_ACTION.equals(qname)) {
        String platform = atts.getValue(PLATFORM);
        if (platform == null) {
          throw new OntopiaRuntimeException("drop-action.platform must be specified: " + platform);
        } 

        info.put(EL_DROP_ACTION, platform); 
      } else if (EL_DBSCHEMA.equals(qname)) {
      }
    }
    
    @Override
    public void endElement(String uri, String name, String qname) throws SAXException {
      super.endElement(uri, name, qname);

      if (EL_DATATYPES.equals(qname)) {
        // Remove types entry
        info.remove(EL_DATATYPES);
      } else if (EL_DATATYPE.equals(qname)) {
        // Remove type entry
        info.remove(EL_DATATYPE);
      } else if (EL_TABLE.equals(qname)) {
        // Remove table entry
        info.remove(EL_TABLE);
      } else if (EL_COLUMN.equals(qname)) {
        // Remove column entry
        info.remove(EL_COLUMN);
      } else if (EL_INDEX.equals(qname)) {
        // Remove index entry
        info.remove(EL_INDEX);
      } else if (EL_CREATE_ACTION.equals(qname)) {
        project.addCreateAction((String)info.get(EL_CREATE_ACTION), content.toString());
        // Remove table entry
        info.remove(EL_CREATE_ACTION);
      } else if (EL_DROP_ACTION.equals(qname)) {
        project.addDropAction((String)info.get(EL_DROP_ACTION), content.toString());
        // Remove table entry
        info.remove(EL_DROP_ACTION);
      } else if (EL_PROPERTY.equals(qname)) {
      } else if (EL_DBSCHEMA.equals(qname)) {
      } else {
        System.out.println("Ignoring: " + name);        
      }
      // System.out.println("E: " + name);
      
    }

    protected Map<String, String> parseAttribs(String content) {
      Map<String, String> result = new HashMap<String, String>();
      
      String[] fields = StringUtils.split(content);
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
