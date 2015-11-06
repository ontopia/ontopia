/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.PrettyPrinter;
import net.ontopia.xml.SAXTracker;
import net.ontopia.xml.ValidatingContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * INTERNAL: DB2TM relation mapping definition. Container for a set of
 * relations, entities and fields. The mapping can be instatiated by
 * calling the static read() methods, which will read its defintion
 * from an XML file.
 */
public class RelationMapping extends SAXTracker {

  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(RelationMapping.class);

  protected XMLReader reader;

  protected String name;
  protected String commitMode;
  protected File baseDirectory;
  protected Map<String, DataSourceIF> datasources;
  protected Map<String, Relation> relations;
  protected Map<String, Prefix> iprefixes;

  RelationMapping() {
    this.datasources = new HashMap<String, DataSourceIF>();
    this.relations = new HashMap<String, Relation>();
    this.iprefixes = new HashMap<String, Prefix>();

    // default commit mode, never commit
    this.commitMode = null;

    keepContentsOf("subject-locator");
    keepContentsOf("subject-identifier");
    keepContentsOf("item-identifier");
    keepContentsOf("topic-name");
    keepContentsOf("occurrence");
    keepContentsOf("param");
    keepContentsOf("condition");
    keepContentsOf("expression-column");
  }

  public void compile() {
    for (Relation rel : getRelations())
      rel.compile();
  }

  public void close() {
    for (DataSourceIF ds : getDataSources()) {
      try {
        ds.close();
      } catch (Throwable t) {
        // ignore
      }        
    }                           
  }
  
  protected static InputSource getRelaxNGSchema() throws IOException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream i = cl.getResourceAsStream("net/ontopia/topicmaps/db2tm/db2tm.rnc");
    return new InputSource(i);
  }

  public File getBaseDirectory() {
    return this.baseDirectory;
  }
  
  public void setBaseDirectory(File baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  public String getName() {
    return name;
  }

  public String getCommitMode() {
    return commitMode;
  }

  public Collection<DataSourceIF> getDataSources() {
    return datasources.values();
  }

  public DataSourceIF getDataSource(String id) {
    return datasources.get(id);
  }

  public void addDataSource(String id, DataSourceIF datasource) {
    datasources.put(id, datasource);
  }

  public Collection<Relation> getRelations() {
    return relations.values();
  }

  public Relation getRelation(String name) {
    return relations.get(name);
  }

  public void addRelation(Relation relation) {
    String name = relation.getName();
    if (relations.containsKey(name))
      throw new DB2TMException("Duplicate relation: " + name);
    else
      relations.put(name, relation);
  }

  public Prefix getPrefix(String prefix) {
    return iprefixes.get(prefix);
  }

  public String getQueryDeclarations() {
    // create prefix declaration string
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Prefix prefix : iprefixes.values()) {
      if (!first) { sb.append("\n"); }
      first = false;
      sb.append("using ");
      sb.append(prefix.getId());
      sb.append(" for ");
      switch (prefix.getType()) {
      case Prefix.TYPE_SUBJECT_IDENTIFIER:
        sb.append("i\"");
        break;
      case Prefix.TYPE_ITEM_IDENTIFIER:
        sb.append("s\"");
        break;
      case Prefix.TYPE_SUBJECT_LOCATOR:
        sb.append("a\"");
        break;
      }
      sb.append(prefix.getLocator());
      sb.append("\"");
    }
    return sb.toString();
  }
  
  public static RelationMapping read(File file) throws IOException {
    File parentFile = file.getParentFile();
    return read(new FileInputStream(file), parentFile);
  }

  public static RelationMapping readFromClasspath(String resource) throws IOException {
    if (resource == null)
      throw new DB2TMConfigException("Parameter 'resource' must be specified.");
    ClassLoader cloader = RelationMapping.class.getClassLoader();
    InputStream istream = cloader.getResourceAsStream(resource);
    if (istream != null) {
      log.debug("{}: loading from classpath", resource);
      return read(istream, null);
    } else {
      throw new DB2TMConfigException("Resource '" + resource + "' not found on classpath.");
    }
  }
  
  public static RelationMapping read(InputStream istream, File basedir)
    throws IOException {
    // Create new parser object
    XMLReader parser;
    try {
      parser = new DefaultXMLReaderFactory().createXMLReader();      
    } catch (SAXException e) {
      throw new IOException("Problems occurred when creating SAX2 XMLReader: " +
                            e.getMessage());
    }
    
    // Create content handler
    RelationMapping mapping = new RelationMapping();
    mapping.setBaseDirectory(basedir);
    ContentHandler vhandler =
      new ValidatingContentHandler(mapping, getRelaxNGSchema(), true);
    parser.setContentHandler(vhandler);
    
    try {
      // Parse input source
      parser.parse(new InputSource(istream));
    } catch (FileNotFoundException e) {
      log.error("Resource not found: {}", e.getMessage());
      throw e;
    } catch (SAXParseException e) {
      throw new OntopiaRuntimeException("XML parsing problem: " + e.toString() + " at: "+
                                        e.getSystemId() + ":" + e.getLineNumber() + ":" +
                                        e.getColumnNumber(), e);
    } catch (SAXException e) {
      if (e.getException() instanceof IOException)
        throw (IOException) e.getException();
      throw new OntopiaRuntimeException(e);
    }

    // Compile mapping
    mapping.compile();
    
    return mapping;
  }

  // --------------------------------------------------------------------------
  // Content handler
  // --------------------------------------------------------------------------

  protected Relation currel;
  protected Entity curent;
  protected Field curfield;
  protected ValueIF curvcol;
  protected Changelog cursync;
  protected ExpressionVirtualColumn curecol;

  // --------------------------------------------------------------------------
  // Document events
  // --------------------------------------------------------------------------
  
  public void startElement(String nsuri, String lname, String qname,
                           Attributes attrs) throws SAXException {

    // Relations    
    if (lname == "relation") {
      currel = new Relation(this);
      currel.setName(getValue(attrs, "name"));
      currel.setColumns(getValues(attrs, "columns", "column"));
      currel.setPrimaryKey(getValues(attrs, "primary-key"));
      currel.setCommitMode(getValue(attrs, "commit-mode"));
      String synctype = getValue(attrs, "synctype");
      if (synctype == null)
        currel.setSynchronizationType(Relation.SYNCHRONIZATION_UNKNOWN);
      else if (synctype.equals("none"))
        currel.setSynchronizationType(Relation.SYNCHRONIZATION_NONE);
      else if (synctype.equals("rescan"))
        currel.setSynchronizationType(Relation.SYNCHRONIZATION_RESCAN);
      else if (synctype.equals("changelog"))
        currel.setSynchronizationType(Relation.SYNCHRONIZATION_CHANGELOG);
      addRelation(currel);
    }

    // Entities
    else if (lname == "topic") {
      curent = new Entity(Entity.TYPE_TOPIC, currel);
      String primary = getValue(attrs, "primary");
      if (primary != null)
        curent.setPrimary(Boolean.valueOf(primary));
      curent.setId(getValue(attrs, "id"));
      String condition = getValue(attrs, "condition");
      if (condition != null)
        curent.setConditionValue(Values.getColumnValue(currel, condition));
      curent.setTypes(getValues(attrs, "types", "type"));
      currel.addEntity(curent);
    }
    else if (lname == "association") {
      curent = new Entity(Entity.TYPE_ASSOCIATION, currel);
      String primary = getValue(attrs, "primary");
      if (primary != null)
        curent.setPrimary(Boolean.valueOf(primary));
      curent.setId(getValue(attrs, "id"));
      String condition = getValue(attrs, "condition");
      if (condition != null)
        curent.setConditionValue(Values.getColumnValue(currel, condition));
      curent.setAssociationType(getValue(attrs, "type"));
      curent.setScope(getValues(attrs, "scope"));      
      currel.addEntity(curent);
    }

    // Identity Fields
    else if (lname == "subject-locator") { 
      curfield = new Field(Field.TYPE_SUBJECT_LOCATOR, curent);
      curfield.setColumn(getValue(attrs, "column"));
      curent.addField(curfield);
    }
    else if (lname == "subject-identifier") { 
      curfield = new Field(Field.TYPE_SUBJECT_IDENTIFIER, curent);
      curfield.setColumn(getValue(attrs, "column"));
      curent.addField(curfield);
    }
    else if (lname == "item-identifier") { 
      curfield = new Field(Field.TYPE_ITEM_IDENTIFIER, curent);
      curfield.setColumn(getValue(attrs, "column"));
      curent.addField(curfield);
    }

    // Characteristics
    else if (lname == "occurrence") {
      curfield = new Field(Field.TYPE_OCCURRENCE, curent);
      curfield.setColumn(getValue(attrs, "column"));
      curfield.setType(getValue(attrs, "type"));
      curfield.setScope(getValues(attrs, "scope"));      
      curfield.setDatatype(getValue(attrs, "datatype"));
      curent.addField(curfield);
    }
    else if (lname == "topic-name") {
      curfield = new Field(Field.TYPE_TOPIC_NAME, curent);
      curfield.setColumn(getValue(attrs, "column"));
      curfield.setType(getValue(attrs, "type"));
      curfield.setScope(getValues(attrs, "scope"));      
      curent.addField(curfield);
    }
    else if (lname == "player") {
      curfield = new Field(Field.TYPE_PLAYER, curent);
      curfield.setRoleType(getValue(attrs, "rtype"));
      curfield.setAssociationType(getValue(attrs, "atype"));
      curfield.setScope(getValues(attrs, "scope"));      
      curent.addField(curfield);
    }
    else if (lname == "other") {
      Field orole = new Field(Field.TYPE_ASSOCIATION_ROLE, curent);
      orole.setRoleType(getValue(attrs, "rtype"));
      orole.setPlayer(getValue(attrs, "player"));
      String optional = getValue(attrs, "optional");
      if (optional != null)
        orole.setOptional(Boolean.valueOf(optional).booleanValue());
      curfield.addOtherRoleField(orole);
    }
    else if (lname == "role") {
      curfield = new Field(Field.TYPE_ASSOCIATION_ROLE, curent);
      curfield.setColumn(getValue(attrs, "column"));
      curfield.setRoleType(getValue(attrs, "type"));
      curfield.setPlayer(getValue(attrs, "player"));
      String optional = getValue(attrs, "optional");
      if (optional != null)
        curfield.setOptional(Boolean.valueOf(optional).booleanValue());
      curent.addField(curfield);
    }

    // Virtual columns
    else if (lname == "mapping-column") {
      String colname = getValue(attrs, "name");
      String inputName = getValue(attrs, "column");
      curvcol = new MappingVirtualColumn(currel, colname, inputName);
      currel.addVirtualColumn(colname, curvcol);
    }
    else if (lname == "map") {
      ((MappingVirtualColumn)curvcol).addMapping(getValue(attrs, "from"), getValue(attrs, "to"));
    }
    else if (lname == "default") {
      ((MappingVirtualColumn)curvcol).setDefault(getValue(attrs, "to"));
    }

    // Function columns
    else if (lname == "function-column") {
      String colname = getValue(attrs, "name");
      String method = getValue(attrs, "method");
      curvcol = new FunctionVirtualColumn(currel, colname, method);
      currel.addVirtualColumn(colname, curvcol);
    }
      
    // Sync
    else if (lname == "changelog") {
      cursync = new Changelog(currel);
      cursync.setTable(getValue(attrs, "table"));
      cursync.setPrimaryKey(getValues(attrs, "primary-key"));
      cursync.setOrderColumn(getValue(attrs, "order-column"));
      cursync.setLocalOrderColumn(getValue(attrs, "local-order-column"));
      cursync.setCondition(getValue(attrs, "condition"));
      currel.addSync(cursync);
      if (currel.getSynchronizationType() == Relation.SYNCHRONIZATION_UNKNOWN)
        currel.setSynchronizationType(Relation.SYNCHRONIZATION_CHANGELOG);
    }
    else if (lname == "extent") {
      curent.addExtentQuery(getValue(attrs, "query"));
    }
    else if (lname == "expression-column") {
      curecol = new ExpressionVirtualColumn(getValue(attrs, "name"));
      cursync.addVirtualColumn(curecol);
    }
      
    // Prefixes
    else if (lname == "using") {
      String prefix = getValue(attrs, "prefix");
      int type = Prefix.TYPE_SUBJECT_IDENTIFIER;
      String locator = getValue(attrs, "subject-identifier");
      if (locator != null)
        type = Prefix.TYPE_SUBJECT_IDENTIFIER;
      if (locator == null) {
        locator = getValue(attrs, "item-identifier");
        if (locator != null)
          type = Prefix.TYPE_ITEM_IDENTIFIER;
      }
      if (locator == null) {
        locator = getValue(attrs, "subject-locator");
        if (locator != null)
          type = Prefix.TYPE_SUBJECT_LOCATOR;
      }
      iprefixes.put(prefix, new Prefix(prefix, locator, type));
    }

    // Other
    else if (lname == "db2tm") {
      name = getValue(attrs, "name");
      commitMode = getValue(attrs, "commit-mode");
    }

    // Sources
    else if (lname == "sources") {
    }
    else if (lname == "csv") {
      String id = getValue(attrs, "id");
      CSVDataSource datasource = new CSVDataSource(this);        
      // - path
      datasource.setPath(getValue(attrs, "path"));
      // - encoding
      String encoding = getValue(attrs, "encoding");
      if (encoding != null)
        datasource.setEncoding(getValue(attrs, "encoding"));

      // - separator
      String separator = getValue(attrs, "separator");
      if (separator != null)
        datasource.setSeparator(separator.charAt(0));
      // - quoting
      String quoting = getValue(attrs, "quoting");
      if (quoting != null)
        datasource.setQuoteCharacter(quoting.charAt(0));
      // - ignoreFirstLines
      String ignoreFirstLines = getValue(attrs, "ignoreFirstLines");
      if (ignoreFirstLines != null)
        datasource.setIgnoreFirstLines(Integer.parseInt(ignoreFirstLines));

      datasources.put(id, datasource);
    }
    else if (lname == "jdbc") {
      String id = getValue(attrs, "id");
      JDBCDataSource datasource = new JDBCDataSource(this);        
      datasource.setPropertyFile(getValue(attrs, "propfile"));
      datasources.put(id, datasource);
    }

    // call super
    super.startElement(nsuri, lname, qname, attrs);
  }
  
  public void endElement(String nsuri, String lname, String qname) 
    throws SAXException {

    // call super
    super.endElement(nsuri, lname, qname);

    if (lname == "subject-locator") {
      curfield.setPattern(content.toString());
    }
    else if (lname == "subject-identifier") {
      curfield.setPattern(content.toString());
    }
    else if (lname == "item-identifier") {
      curfield.setPattern(content.toString());
    }
    else if (lname == "topic-name") {
      curfield.setPattern(content.toString());
    }
    else if (lname == "occurrence") {
      curfield.setPattern(content.toString());
    }
    else if (lname == "relation") {
      currel = null;
    }
    else if (lname == "topic" || lname == "association") {
      curent = null;
    }
    else if (lname == "param") {
      ((FunctionVirtualColumn)curvcol).addParameter(content.toString());
    }
    else if (lname == "condition") {
      currel.setCondition(content.toString());
    }
    else if (lname == "mapping-column") {
      curvcol = null;
    }
    else if (lname == "function-column") {
      ((FunctionVirtualColumn)curvcol).compile();
      curvcol = null;
    }
    else if (lname == "changelog") {
      cursync = null;
    }
    else if (lname == "expression-column") {
      curecol.setSQLExpression(content.toString());
      curecol = null;
    }
  }

  // --------------------------------------------------------------------------
  // Helpers
  // --------------------------------------------------------------------------
  
  protected String getValue(Attributes attrs, String name) {
    return attrs.getValue("", name);
  }

  protected String getValue(Attributes attrs, String name, String _default) {
    String result = attrs.getValue("", name);
    return (result == null ? _default : result);
  }

  protected String[] getValues(Attributes attrs, String name) {
    String value = getValue(attrs, name);
    if (value == null)
      return new String[] { };
    else 
      return StringUtils.tokenize(value, " \t\n\r,");
  }

  protected String[] getValues(Attributes attrs, String plural, String singular) {
    String value = getValue(attrs, singular);
    if (value != null)
      return new String[] { value };
    else
      return getValues(attrs, plural);
  }

  protected void addAttribute(AttributesImpl atts, String name, String type, String value) {
    if (value != null) atts.addAttribute("", "", name, type, value);
  }

  protected void addAttribute(AttributesImpl atts, String name, String type, String[] values) {
    if (values != null) atts.addAttribute("", "", name, type, StringUtils.join(values, ","));
  }

  // --------------------------------------------------------------------------
  // Export
  // --------------------------------------------------------------------------

  public void write(Writer writer) throws SAXException {
    write(writer, "utf-8");
  }

  public void write(Writer writer, String encoding) throws SAXException {
    write(new PrettyPrinter(writer, encoding));
  }

  protected void write(ContentHandler dh) throws SAXException {

    // initialize attributes
    AttributesImpl atts = new AttributesImpl();

    // <db2tm name="...">
    if (name != null) addAttribute(atts, "name", "CDATA", name);

    dh.startDocument();
    dh.startElement("", "", "db2tm", atts);
    atts.clear();

    // prefixes
    for (Prefix prefix : iprefixes.values()) {
      addAttribute(atts, "prefix", "CDATA", prefix.getId());

      switch (prefix.getType()) {
      case Prefix.TYPE_SUBJECT_IDENTIFIER:
        addAttribute(atts, "subject-identifier", "CDATA", prefix.getLocator());
        break;
      case Prefix.TYPE_ITEM_IDENTIFIER:
        addAttribute(atts, "item-identifier", "CDATA", prefix.getLocator());
        break;
      case Prefix.TYPE_SUBJECT_LOCATOR:
        addAttribute(atts, "subject-locator", "CDATA", prefix.getLocator());
        break;
      }
             
      dh.startElement("", "", "using", atts);
      atts.clear();
      dh.endElement("", "", "using");
    }

    // relations
    for (Relation rel : getRelations()) {
      // <relation>
      addAttribute(atts, "name", "CDATA", rel.getName());
      addAttribute(atts, "columns", "CDATA", rel.getColumns());
      dh.startElement("", "", "relation", atts);
      atts.clear();

      outputEntities(rel, dh);

      // </relation>
      dh.endElement("", "", "relation");
    }

    // </db2tm>
    dh.endElement("", "", "db2tm");
    dh.endDocument();
  }

  protected void outputEntities(Relation rel, ContentHandler dh) throws SAXException {
    AttributesImpl atts = new AttributesImpl();

    List<Entity> entities = rel.getEntities();
    for (int i=0; i < entities.size(); i++) {
      Entity entity = entities.get(i);

      if (entity.getEntityType() == Entity.TYPE_TOPIC) {
        // <topic>
        if (entity.getId() != null)
          addAttribute(atts, "id", "CDATA", entity.getId());
        addAttribute(atts, "type", "CDATA", entity.getAssociationType());
        dh.startElement("", "", "topic", atts);
        atts.clear();
        
        outputFields(entity, dh);
        
        // </topic>
        dh.endElement("", "", "topic");
        
      } else if (entity.getEntityType() == Entity.TYPE_ASSOCIATION) {
        
        // <association>
        if (entity.getId() != null)
          addAttribute(atts, "id", "CDATA", entity.getId());
        addAttribute(atts, "type", "CDATA", entity.getAssociationType());
        addAttribute(atts, "scope", "CDATA", entity.getScope());
        
        dh.startElement("", "", "association", atts);
        atts.clear();
        
        outputFields(entity, dh);
        
        // </association>
        dh.endElement("", "", "association");
      }
    }
  }

  protected void outputFields(Entity entity, ContentHandler dh) throws SAXException {

    // identity fields
    for (Field field : entity.getIdentityFields()) {
      outputField(field, dh);
    }
    for (Field field : entity.getRoleFields()) {
      outputField(field, dh);
    }
    for (Field field : entity.getCharacteristicFields()) {
      outputField(field, dh);
    }
  }

  protected void outputField(Field field, ContentHandler dh) throws SAXException {
    AttributesImpl atts = new AttributesImpl();
    if (field.getFieldType() == Field.TYPE_SUBJECT_LOCATOR) {
        addAttribute(atts, "column", "CDATA", field.getColumn());
        dh.startElement("", "", "subject-locator", atts);
        char[] c = field.getPattern().toCharArray(); 
        dh.characters(c, 0, c.length);
        dh.endElement("", "", "subject-locator");
        atts.clear();
    }
    else if (field.getFieldType() == Field.TYPE_SUBJECT_IDENTIFIER) {
      addAttribute(atts, "column", "CDATA", field.getColumn());
      dh.startElement("", "", "subject-identifier", atts);
      char[] c = field.getPattern().toCharArray(); 
      dh.characters(c, 0, c.length);
      dh.endElement("", "", "subject-identifier");
      atts.clear();
    }
    else if (field.getFieldType() == Field.TYPE_ITEM_IDENTIFIER) {
      addAttribute(atts, "column", "CDATA", field.getColumn());
      dh.startElement("", "", "item-identifier", atts);
      char[] c = field.getPattern().toCharArray(); 
      dh.characters(c, 0, c.length);
      dh.endElement("", "", "item-identifier");
      atts.clear();
    }
    else if (field.getFieldType() == Field.TYPE_OCCURRENCE) {
      addAttribute(atts, "column", "CDATA", field.getColumn());
      addAttribute(atts, "type", "CDATA", field.getType());
      addAttribute(atts, "scope", "CDATA", field.getScope());
      addAttribute(atts, "datatype", "CDATA", field.getDatatype());
      dh.startElement("", "", "occurrence", atts);
      dh.endElement("", "", "occurrence");
      atts.clear();
    }
    else if (field.getFieldType() == Field.TYPE_TOPIC_NAME) {
      addAttribute(atts, "column", "CDATA", field.getColumn());
      addAttribute(atts, "type", "CDATA", field.getType());
      addAttribute(atts, "scope", "CDATA", field.getScope());
      dh.startElement("", "", "topic-name", atts);
      dh.endElement("", "", "topic-name");
      atts.clear();
    }
    else if (field.getFieldType() == Field.TYPE_PLAYER) {
      addAttribute(atts, "rtype", "CDATA", field.getRoleType());
      addAttribute(atts, "atype", "CDATA", field.getAssociationType());
      addAttribute(atts, "scope", "CDATA", field.getScope());
      
      dh.startElement("", "", "player", atts);
      atts.clear();

      for (Field orole : field.getOtherRoleFields()) {
        addAttribute(atts, "rtype", "CDATA", orole.getRoleType());
        addAttribute(atts, "player", "CDATA", orole.getPlayer());
        dh.startElement("", "", "other", atts);
        dh.endElement("", "", "other");
        atts.clear();
      }

      dh.endElement("", "", "player");
      atts.clear();
    }
    else if (field.getFieldType() == Field.TYPE_ASSOCIATION_ROLE) {
      addAttribute(atts, "type", "CDATA", field.getRoleType());
      addAttribute(atts, "player", "CDATA", field.getPlayer());
      dh.startElement("", "", "role", atts);
      dh.endElement("", "", "role");
      atts.clear();
    }
    else throw new OntopiaRuntimeException("Unknown field type: " + field.getType());
  }

  public String toString() {
    return "RelationMapping(" + getName() + ")";
  }

}
