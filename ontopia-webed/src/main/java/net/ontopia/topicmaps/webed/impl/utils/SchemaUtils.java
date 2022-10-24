/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;

/**
 * INTERNAL: Utitlity class for providing help around making topic map
 * schema support available to the web editor.
 */
public class SchemaUtils {

  private static TypeHierarchyUtils hierUtils = new TypeHierarchyUtils();
  
  /**
   * INTERNAL: Return all objects that are in the given scope.
   */
  protected Collection getCharacteristicsInScope(Collection objects, Collection scope) {
    Iterator it = objects.iterator();
    Collection objectsInScope = new ArrayList();
    while (it.hasNext()) {
      ScopedIF scopedObj = (ScopedIF) it.next();
      if (scopedObj.getScope().equals(scope))
        objectsInScope.add(scopedObj);
    }
    return objectsInScope;
  }

  /**
   * INTERNAL: Return all typing objects that are equal to one
   * of the given types.
   */
  protected Collection getObjectsOfType(Collection allTypes, Collection curTypes) {
    Collection objectsOfType = new ArrayList();
    Iterator itA = allTypes.iterator();
    while (itA.hasNext()) {
      Object objAT = itA.next();
      if (objAT == null)
        continue;
      Iterator itB = curTypes.iterator();
      while (itB.hasNext()) {
        Object objCT = itB.next();
        if (objAT.equals(objCT))
          objectsOfType.add(objCT);
      } // while itB
    } // while itA
    return objectsOfType;    
  }
  
}
