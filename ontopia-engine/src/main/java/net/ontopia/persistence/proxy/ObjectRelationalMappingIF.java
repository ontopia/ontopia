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


/**
 * INTERNAL: An object relational mapping wrapper class used by the
 * RDBMS proxy implementation.
 */

public interface ObjectRelationalMappingIF {

  /**
   * INTERNAL: Get the class info by object type.
   */  
  public ClassInfoIF getClassInfo(Class<?> type);

  /**
   * INTERNAL: Returns true if the object type has a class descriptor.
   */  
  public boolean isDeclared(Class<?> type);
  
}





