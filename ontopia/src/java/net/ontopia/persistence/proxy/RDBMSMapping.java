
// $Id: RDBMSMapping.java,v 1.13 2008/12/04 11:25:49 lars.garshol Exp $

package net.ontopia.persistence.proxy;

import java.util.HashMap;
import java.util.Map;

import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: An object relational mapping wrapper class used by the
 * RDBMS proxy implementation.
 */
public class RDBMSMapping implements ObjectRelationalMappingIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RDBMSMapping.class.getName());

  protected ObjectRelationalMapping mapping;
  protected Map class_infos;
  
  public RDBMSMapping(ObjectRelationalMapping mapping) {
    this.mapping = mapping;
    class_infos = new HashMap();
  }

  /**
   * INTERNAL: Returns the object relational mapping instance wrapped
   * by this class.
   */  
  public ObjectRelationalMapping getMapping() {
    return mapping;
  }
  
  public synchronized ClassInfoIF getClassInfo(Object type) {
    ClassInfoIF ci = (ClassInfoIF) class_infos.get(type);
    if (ci == null) {
      ClassDescriptor cdesc = mapping.getDescriptorByClass(type);
      if (cdesc == null)
        throw new OntopiaRuntimeException("Descriptor for type " + type + " not found.");
      log.debug("Compiling " + type + " class descriptor.");
      ClassInfo realci = new ClassInfo(this, cdesc);
      class_infos.put(type, realci);

      // before this method call, the ClassInfo object that's registered
      // is not initialized. however, we must register it before the call,
      // to avoid bottomless recursion. this means that the method must be
      // synchronized to avoid serious threading issues.
      realci.compile();
      ci = realci;
    }    
    return ci;
  }

  public boolean isDeclared(Object type) {
    return (mapping.getDescriptorByClass(type) != null);
  }
}
