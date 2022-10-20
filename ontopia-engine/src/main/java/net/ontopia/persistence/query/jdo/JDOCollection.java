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

package net.ontopia.persistence.query.jdo;

import java.util.Collection;

/**
 * INTERNAL: JDOQL value: collection. Class used to represent
 * collections of object instances.
 */

public class JDOCollection implements JDOValueIF {

  protected Collection coll;
  protected Class eltype;
  
  public JDOCollection(Collection coll, Class eltype) {
    if (coll == null) {
      throw new IllegalArgumentException("Collection cannot be null.");
    }
    if (eltype == null) {
      throw new IllegalArgumentException("Element type cannot be null.");
    }
    
    this.coll = coll;
    this.eltype = eltype;
  }

  @Override
  public int getType() {
    return COLLECTION;
  }

  public Class getValueType() {
    return coll.getClass();
  }

  public Class getElementType() {
    return eltype;
  }

  public Collection getValue() {
    return coll;
  }
  
  @Override
  public int hashCode() {
    return coll.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JDOCollection) {
      JDOCollection other = (JDOCollection)obj;    
      return coll.equals(other.coll);
    }
    return false;
  }

  @Override
  public String toString() {
    return coll.toString();
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    // no-op
  }
  
}
