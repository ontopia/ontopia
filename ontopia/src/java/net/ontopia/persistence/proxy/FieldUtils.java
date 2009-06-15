// $Id: FieldUtils.java,v 1.20 2005/07/12 09:37:39 grove Exp $

package net.ontopia.persistence.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ontopia.utils.OntopiaRuntimeException;

//! import org.apache.log4j.*;

/**
 * INTERNAL: Class containing utility methods for processing
 * descriptor information.
 */

public class FieldUtils {

  //! // Define a logging category.
  //! static Logger log = Logger.getLogger(FieldUtils.class.getName());

  /**
   * INTERNAL: Returns an array containing the tables in which the
   * fields are stored.
   */
  public static String[] getTables(FieldInfoIF[] fields) {
    Set _tables = new HashSet();
    for (int i=0; i < fields.length; i++) {
      _tables.add(fields[i].getTable());
    }
    String[] tables = new String[_tables.size()];
    _tables.toArray(tables);
    return tables;
  }
  
  /**
   * INTERNAL: Returns the names of the value columns that the fields
   * span.
   */
  public static String[] getColumns(FieldInfoIF[] fields) {
    List columns = new ArrayList();
    addColumns(fields, columns);
    
    String[] _columns = new String[columns.size()];
    columns.toArray(_columns);
    return _columns;
  }
  
  public static void addColumns(FieldInfoIF field, Collection columns) {
    String[] names = field.getValueColumns();
    for (int n=0; n < names.length; n++) {
      columns.add(names[n]);
    }
  }
  
  public static void addColumns(FieldInfoIF[] fields, Collection columns) {
    for (int i=0; i < fields.length; i++) {
      String[] names = fields[i].getValueColumns();
      for (int n=0; n < names.length; n++) {
        columns.add(names[n]);
      }
    }
  }
  
  /**
   * INTERNAL: Returns the number of columns that the fields span.
   */
  public static int getColumnCount(FieldInfoIF[] fields) {
    int columns = 0;
    for (int i=0; i < fields.length; i++) {
      columns += fields[i].getColumnCount();
    }
    return columns;
  }
  
  /**
   * INTERNAL: Utility method that creates an int array containing the
   * result set index for which the field handler should start
   * reading. Note that result set indexes are 1-indexed.
   */
  public static int[] getColumnOffsets(FieldHandlerIF[] fhandlers, int start) {
    int[] offsets = new int[fhandlers.length];
    if (fhandlers.length > 0) {
      offsets[0] = start;
      for (int i=1; i< fhandlers.length; i++) {
        // Offset is last offset plus column count of previous field handler
        offsets[i] = offsets[i-1] + fhandlers[i-1].getColumnCount();
      }
    }
    return offsets;
  }
  
  /**
   * INTERNAL: Utility method that creates an int array containing the
   * result set index for which the field handler should start
   * reading. Note that result set indexes are 1-indexed.
   */
  public static int[] getResultSetOffsets(FieldHandlerIF[] fhandlers) {
    return getColumnOffsets(fhandlers, 1);
  }

  /**
   * INTERNAL: Filters the field descriptors by cardinality.
   */
  public static FieldDescriptor[] filterByCardinality(FieldDescriptor[] fdescs, int cardinality) {
    Collection result = new ArrayList();
    for (int i=0; i < fdescs.length; i++) {
      FieldDescriptor fdesc = fdescs[i];
      if (fdesc.getCardinality() == cardinality)
        result.add(fdesc);
    }
    return toFieldDescriptorArray(result);
  }

  /**
   * INTERNAL: Filters the field infos by cardinality.
   */
  public static FieldInfoIF[] filterByCardinality(FieldInfoIF[] finfos, int cardinality) {
    Collection result = new ArrayList();
    for (int i=0; i < finfos.length; i++) {
      FieldInfoIF finfo = finfos[i];
      if (finfo.getCardinality() == cardinality)
        result.add(finfo);
    }
    return toFieldInfoArray(result);
  }

  /**
   * INTERNAL: Filters out all but the aggregate field infos.
   */
  public static FieldInfoIF[] filterAggregate(FieldInfoIF[] finfos) {
    Collection result = new ArrayList();
    for (int i=0; i < finfos.length; i++) {
      FieldInfoIF finfo = finfos[i];
      if (finfo.isAggregateField())
        result.add(finfo);
    }
    return toFieldInfoArray(result);
  }

  /**
   * INTERNAL: Returns the field descriptors that are stored in the
   * specified table.
   */
  public static FieldDescriptor[] filterByTable(FieldDescriptor[] fdescs, String table) {
    Collection result = new ArrayList();
    for (int i=0; i < fdescs.length; i++) {
      FieldDescriptor fdesc = fdescs[i];
      if (table.equals(fdesc.getTable()))
        result.add(fdesc);
    }
    return toFieldDescriptorArray(result);
  }

  /**
   * INTERNAL: Returns the getter methods for the given field
   * descriptor. This method uses reflection to locate the method.
   */
  public static Method getGetterMethod(FieldDescriptor fdesc) throws Exception {
    Class object_class = fdesc.getClassDescriptor().getDescriptorClass();

    // Getter
    String getter_name = fdesc.getGetter();
    if (getter_name == null) {
      // Create bean getter method name
      String fname =  fdesc.getName();
      getter_name = "get" + fname.substring(0, 1).toUpperCase() + fname.substring(1);
      //! throw new OntopiaRuntimeException("Getter method for " + object_class.getName() + "." +
      //!                                   fdesc.getName() + " descriptor not specified.");
    }
    
    //! if (log.isDebugEnabled())
    //!   log.debug("Looking up: " + object_class.getName() + "." + getter_name + "()");

    try {
      // Try declared interfaces
      // log.debug("Trying declared interfaces...");
      if (fdesc.getClassDescriptor() != null) {
        Class[] interfaces = fdesc.getClassDescriptor().getInterfaces();
        if (interfaces != null) {
          for (int i=0; i < interfaces.length; i++) {
            try {
              //! if (log.isDebugEnabled())
              //!   log.debug("Trying declared interface: " + interfaces[i]);
              return interfaces[i].getMethod(getter_name, new Class[0]);
            } catch (NoSuchMethodException e2) {
              // Ignore
            }
          }
        }
      }
      
      return object_class.getMethod(getter_name, new Class[0]);
    } catch (NoSuchMethodException e) {
      throw new OntopiaRuntimeException("Cannot find getter method for field " + fdesc.getName());
    }
  }
  
  /**
   * INTERNAL: Returns the setter methods for the given field
   * descriptor. This method uses reflection to locate the method.
   */
  public static Method getSetterMethod(FieldDescriptor fdesc) throws Exception {
    Class object_class = fdesc.getClassDescriptor().getDescriptorClass();
    Class value_class;
    if (fdesc.isOneToOne()) {
      value_class = fdesc.getValueClass();
    } else {
      value_class = fdesc.getCollectionClass();
    }
    
    String setter_name = fdesc.getSetter();
    if (setter_name == null)
      throw new OntopiaRuntimeException("Setter method for " + object_class.getName() + "."
                                        + fdesc.getName() + " descriptor not specified.");
    //! if (log.isDebugEnabled())
    //!   log.debug("Looking up: " + object_class.getName() + "." + setter_name + "(" + value_class + ")");

    if (fdesc.isPrimitiveField()) {
      try {
        // First try the primitive wrapper class
        return object_class.getMethod(setter_name, new Class[] {value_class});
      } catch (NoSuchMethodException e) {
        // Ignore
      } 
      try {
        // Then try the actual primitive class
        return object_class.getMethod(setter_name, new Class[] {getPrimitiveClass(value_class)});
      } catch (NoSuchMethodException e) {
        // Ignore
      } 
    }
    else {
      try {
        // Try declared interfaces
        // log.debug("Trying declared interfaces...");
        if (fdesc.getValueClassDescriptor() != null) {
          Class[] interfaces = fdesc.getValueClassDescriptor().getInterfaces();
          if (interfaces != null) {
            for (int i=0; i < interfaces.length; i++) {
              try {
                //! if (log.isDebugEnabled())
                //!   log.debug("Trying declared interface: " + interfaces[i]);
                return object_class.getMethod(setter_name, new Class[] {interfaces[i]});
              } catch (NoSuchMethodException e2) {
                // Ignore
              }
            }
          }
        }
        
        return object_class.getMethod(setter_name, new Class[] {value_class});
      } catch (NoSuchMethodException e) {
        // Try interfaces
        // log.debug("Trying interfaces...");
        Class[] interfaces = getImplementedInterfaces(value_class);
        for (int i=0; i < interfaces.length; i++) {
          try {
            //! if (log.isDebugEnabled())
            //!   log.debug("Trying interface: " + interfaces[i]);
            return object_class.getMethod(setter_name, new Class[] {interfaces[i]});
          } catch (NoSuchMethodException e2) {
            // Ignore
          } 
        }
      }
    }
    throw new OntopiaRuntimeException("Cannot find setter method for field " + fdesc.getName());
  }

  /**
   * INTERNAL: Returns the primitive class for the specified primitive
   * wrapper class.
   */
  public static Class getPrimitiveClass(Class klass) {
    // Note: Only primitive wrapper classes are expected as argument.
    if (klass.equals(String.class))
      return klass;
    else if (klass.equals(Long.class))
      return Long.TYPE;
    else if (klass.equals(Integer.class))
      return Integer.TYPE;
    else if (klass.equals(Float.class))
      return Float.TYPE;
    else
      throw new OntopiaRuntimeException("Unsupported primitive wrapper class " + klass);
  }

  /**
   * INTERNAL: Returns all interfaces implemented by this class and
   * its superclasses.
   */
  public static Class[] getImplementedInterfaces(Class klass) {
    Set result = new HashSet();
    accumulateImplementedInterfaces(klass, result);
    
    Class[] interfaces = new Class[result.size()];
    result.toArray(interfaces);
    return interfaces;
    
  }

  /**
   * INTERNAL: Accumulates the interfaces implemented by this class
   * and its superclasses. All recognized interfaces are added to the
   * result argument collection.
   */
  protected static void accumulateImplementedInterfaces(Class klass, Set result) {
    // Interfaces
    Class[] _interfaces = klass.getInterfaces();
    for (int i=0; i < _interfaces.length; i++) {
      Class _interface = _interfaces[i];
      accumulateImplementedInterfaces(_interface, result);
      result.add(_interface);
    }
    // Superclasses
    Class _superclass = klass.getSuperclass();
    if (_superclass != null)
      accumulateImplementedInterfaces(_superclass, result);
  }
  
  /**
   * INTERNAL: Joins the two String arrays by producing a new composite
   * string array.
   */
  public static String[] joinStrings(String[] a, String[] b) {
    String[] result = new String[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }
  
  /**
   * INTERNAL: Joins the two FieldInfoIF arrays by producing a new
   * composite string array.
   */
  public static FieldInfoIF[] joinFieldInfos(FieldInfoIF[] a, FieldInfoIF[] b) {
    FieldInfoIF[] result = new FieldInfoIF[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }

  /**
   * INTERNAL: Utility method that converts a collection of strings to
   * an array of strings.
   */
  public static String[] toStringArray(Collection strings) {
    String[] _strings = new String[strings.size()];
    strings.toArray(_strings);
    return _strings;
  }
  
  /**
   * INTERNAL: Utility method that converts a collection of field
   * descriptors to an array of field descriptors.
   */
  public static FieldDescriptor[] toFieldDescriptorArray(Collection fdescs) {
    FieldDescriptor[] _fdescs = new FieldDescriptor[fdescs.size()];
    fdescs.toArray(_fdescs);
    return _fdescs;
  }
  
  /**
   * INTERNAL: Utility method that converts a collection of field
   * infos to an array of field infos.
   */
  public static FieldInfoIF[] toFieldInfoArray(Collection finfos) {
    FieldInfoIF[] _finfos = new FieldInfoIF[finfos.size()];
    finfos.toArray(_finfos);
    return _finfos;
  }
  /**
   * INTERNAL: Utility method that extracts the field names of an
   * array of field descriptors.
   */
  public static String[] getFieldNames(FieldDescriptor[] fields) {
    String[] names = new String[fields.length];
    for (int i=0; i < fields.length; i++) {
      names[i] = fields[i].getName();
    }
    return names;
  }

}





