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

package net.ontopia.persistence.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.Slf4jSaxErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL: The generic object relational mapping definition class.
 */

public class ObjectRelationalMapping {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(ObjectRelationalMapping.class.getName());
  
  protected Map<Class<?>, ClassDescriptor> cdescs = new HashMap<Class<?>, ClassDescriptor>();
  
  class MappingHandler extends DefaultHandler {

    protected ObjectRelationalMapping mapping;
    protected ClassDescriptor cdesc;
    
    MappingHandler(ObjectRelationalMapping mapping) {
      this.mapping = mapping;
    }

    /**
     * INTERNAL: Looks up a class object by its name.
     */
    protected Class<?> getClassByName(String class_name) {
      try {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return Class.forName(class_name, true, classLoader);
      } catch (ClassNotFoundException e) {
        log.error("Cannot find class " + e.getMessage());
        throw new OntopiaRuntimeException(e);
      }
    }
    
    @Override
    public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
      if ("class".equals(name)) {
        // Get descriptor class
        Class<?> klass = getClassByName(atts.getValue("name"));
        Class<?> klass_immutable = getClassByName(atts.getValue("immutable"));

        // Create new class descriptor
        cdesc = new ClassDescriptor(klass, klass_immutable, mapping);

        // Set abstract flag (default: concrete)
        String isabstract = atts.getValue("abstract");
        if (isabstract != null && "yes".equals(isabstract)) {
          cdesc.setAbstract(true);
        }
        else {
          cdesc.setAbstract(false);
        }

        // Set class descriptor type (default: identifiable)
        String type = atts.getValue("type");
        if (type == null || "identifiable".equals(type)) {
          cdesc.setType(ClassInfoIF.TYPE_IDENTIFIABLE);
        }
        else if ("aggregate".equals(type)) {
          cdesc.setType(ClassInfoIF.TYPE_AGGREGATE);
        //! }
        //! else if (type.equals("primitive")) {
        //!   cdesc.setType(ClassInfoIF.TYPE_PRIMITIVE);
        } else {
          throw new OntopiaRuntimeException("class.type contains invalid value: " + type);
        }

        if (cdesc.getType() != ClassInfoIF.TYPE_AGGREGATE) {
          // Set table name
          String table = atts.getValue("table");
          if (table == null) {
            throw new OntopiaRuntimeException("class.table must be specified: " + cdesc.getName());
          }
          cdesc.setMasterTable(table);

          // Set identity field
          String identity = atts.getValue("identity");
          if (identity == null) {
            throw new OntopiaRuntimeException("class.identity must be specified: " + cdesc.getName());
          }
          cdesc.setIdentityFieldNames(new String[] {identity});

          // Set class structure (default: object)
          String structure = atts.getValue("structure");
          if (structure == null || "object".equals(structure)) {
            cdesc.setStructure(ClassInfoIF.STRUCTURE_OBJECT);
          }
          else if ("collection".equals(structure)) {
            cdesc.setStructure(ClassInfoIF.STRUCTURE_COLLECTION);
            //! }
            //! else if (structure.equals("map")) {
            //!   cdesc.setStructure(ClassInfoIF.STRUCTURE_MAP);
          } else {
            throw new OntopiaRuntimeException("class.structure contains invalid value: " + structure);
          }
          
        }
        
        // Extends
        String _extends = atts.getValue("extends");
        if (_extends != null) {
          String[] _class_names = StringUtils.split(_extends);
          Class<?>[] _classes = new Class<?>[_class_names.length];
          for (int i=0; i < _class_names.length; i++) {
            _classes[i] = getClassByName(_class_names[i]);
          }
          cdesc.setExtends(_classes);
        }
        
        // Interfaces
        String _interfaces = atts.getValue("interfaces");
        if (_interfaces != null) {
          String[] _class_names = StringUtils.split(_interfaces);
          Class<?>[] _classes = new Class<?>[_class_names.length];
          for (int i=0; i < _class_names.length; i++) {
            _classes[i] = getClassByName(_class_names[i]);
          }
          cdesc.setInterfaces(_classes);
        }
           
        // Register class descriptor with mapping
        mapping.addClass(cdesc);
      }
      else if ("field".equals(name)) {
        if (cdesc == null) {
          throw new OntopiaRuntimeException("No parent class descriptor for field: " + atts.getValue("name"));
        }

        // Create new field descriptor
        name = atts.getValue("name");
        if (name == null) {
          throw new OntopiaRuntimeException("field.name must be specified: " + cdesc.getName());
        }
        
        FieldDescriptor fdesc = new FieldDescriptor(name, cdesc);
        
        String klass = atts.getValue("class");
        if (klass == null) {
          throw new OntopiaRuntimeException("field.class must be specified: " + fdesc.getName());
        }

        // FIXME: should add more primitive types
        if ("string".equals(klass)) {
          fdesc.setValueClass(String.class);
        }
        else if ("integer".equals(klass)) {
          fdesc.setValueClass(Integer.class);
        }
        else if ("long".equals(klass)) {
          fdesc.setValueClass(Long.class);
        }
        else if ("float".equals(klass)) {
          fdesc.setValueClass(Float.class);
        }
        else if ("clob".equals(klass)) {
          fdesc.setValueClass(java.io.Reader.class);
        }
        else {
          fdesc.setValueClass(getClassByName(klass));
        }

        // Required
        String required = atts.getValue("required");
        if (required != null && ("yes".equals(required) || "true".equals(required))) {
          fdesc.setRequired(true);
        } else {
          fdesc.setRequired(false);
        }

        // Readonly
        String readonly = atts.getValue("readonly");
        if (readonly != null && ("yes".equals(readonly) || "true".equals(readonly))) {
          fdesc.setReadOnly(true);
        } else {
          fdesc.setReadOnly(false);
        }
          
        // Relationship type
        String type = atts.getValue("type");
        if (type == null) {
          throw new OntopiaRuntimeException("field.type must be specified: " + fdesc.getName());          
        }
        else if ("1:1".equals(type)) {
          fdesc.setCardinality(FieldDescriptor.ONE_TO_ONE);

          // Columns
          String columns = atts.getValue("columns");
          if (columns != null) {
            //throw new OntopiaRuntimeException("field.columns must be specified: " + fdesc.getName());
            fdesc.setColumns(StringUtils.split(columns));
          }
        }
        else if ("1:M".equals(type)) {
          fdesc.setCardinality(FieldDescriptor.ONE_TO_MANY);
        }
        else if ("M:M".equals(type)) {
          fdesc.setCardinality(FieldDescriptor.MANY_TO_MANY);
          
          // Many key
          String manykey = atts.getValue("many-keys");
          if (manykey == null) {
            throw new OntopiaRuntimeException("field.many-keys must be specified: " + fdesc.getName());
          }         
          fdesc.setManyKeys(StringUtils.split(manykey));
        }
        else {
          throw new OntopiaRuntimeException("field.type contains invalid value: " + type);
          }
        
        // Join table
        String jointable = atts.getValue("join-table");
        if (jointable == null) {
          if (!fdesc.isOneToOne()) {
            throw new OntopiaRuntimeException("field.join-table must be specified: " + fdesc.getName());
          }
        } else {
          fdesc.setJoinTable(jointable);
        }
        
        // Join keys
        String joinkeys = atts.getValue("join-keys");
        if (joinkeys == null) {
          if (!fdesc.isOneToOne()) {
            throw new OntopiaRuntimeException("field.join-keys must be specified: " + fdesc.getName());
          }
        } else {
          fdesc.setJoinKeys(StringUtils.split(joinkeys));
        }

        // Collection
        if (fdesc.isCollectionField()) {
          String collection = atts.getValue("collection");
          // FIXME: should possibly also support 'map'.
          if (collection == null || "list".equals(collection)) {
            fdesc.setCollectionClass(java.util.ArrayList.class);
          } else if ("set".equals(collection)) {
            fdesc.setCollectionClass(java.util.HashSet.class);
          } else {
            fdesc.setCollectionClass(getClassByName(collection));
          }
        }

        // Getter method
        String getter = atts.getValue("get-method");
        if (getter != null) {
          fdesc.setGetter(getter);
        }
        
        // Setter method
        String setter = atts.getValue("set-method");
        if (setter != null) {
          fdesc.setSetter(setter);
        }

        // Register field descriptor with class descriptor
        cdesc.addField(fdesc);
      }
    }

    @Override
    public void endElement (String uri, String name, String qName) throws SAXException {
      if ("class".equals(name)) {
        // Reset class descriptor field
        cdesc = null;
      } 
    }

  }

  /**
   * INTERNAL: Creates an object relational mapping instance that is
   * to read its definition from the specified mapping file. The
   * mapping file input stream should be an XML file.
   */
  public ObjectRelationalMapping(InputStream istream) {
    loadMapping(new InputSource(istream));
  }
  
  /**
   * INTERNAL: Read a mapping description from the specified file.
   */
  protected void loadMapping(InputSource isource) {

    // Read mapping file.
    ContentHandler handler = new MappingHandler(this);
    
    try {
      XMLReader parser = DefaultXMLReaderFactory.createXMLReader();
      parser.setContentHandler(handler);
      parser.setErrorHandler(new Slf4jSaxErrorHandler(log));
      parser.parse(isource);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    } catch (SAXException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: Utility method that converts a collection of class
   * descriptors to an array of class descriptors.
   */
  protected ClassDescriptor[] toClassDescriptorArray(Collection<ClassDescriptor> cdescs) {
    ClassDescriptor[] _cdescs = new ClassDescriptor[cdescs.size()];
    cdescs.toArray(_cdescs);
    return _cdescs;
  }

  /**
   * INTERNAL: Gets all the class descriptors in the mapping.
   */
  public ClassDescriptor[] getClassDescriptors() {
    return toClassDescriptorArray(cdescs.values());
  }
  
  /**
   * INTERNAL: Gets the class descriptor by object type.
   */
  public ClassDescriptor getDescriptorByClass(Class<?> type) {
    return cdescs.get(type);
  }
    
  /**
   * INTERNAL: Adds the class descriptor to the mapping.
   */
  public void addClass(ClassDescriptor cdesc) {
    cdescs.put(cdesc.getDescriptorClass(), cdesc);
  }
  
}





