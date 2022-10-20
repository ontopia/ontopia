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
  private static final Logger log = LoggerFactory.getLogger(RDBMSMapping.class.getName());

  protected ObjectRelationalMapping mapping;
  protected Map<Object, ClassInfoIF> class_infos;
  
  public RDBMSMapping(ObjectRelationalMapping mapping) {
    this.mapping = mapping;
    class_infos = new HashMap<Object, ClassInfoIF>();
  }

  /**
   * INTERNAL: Returns the object relational mapping instance wrapped
   * by this class.
   */  
  public ObjectRelationalMapping getMapping() {
    return mapping;
  }
  
  @Override
  public synchronized ClassInfoIF getClassInfo(Class<?> type) {
    ClassInfoIF ci = class_infos.get(type);
    if (ci == null) {
      ClassDescriptor cdesc = mapping.getDescriptorByClass(type);
      if (cdesc == null) {
        throw new OntopiaRuntimeException("Descriptor for type " + type + " not found.");
      }
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

  @Override
  public boolean isDeclared(Class<?> type) {
    return (mapping.getDescriptorByClass(type) != null);
  }
}
