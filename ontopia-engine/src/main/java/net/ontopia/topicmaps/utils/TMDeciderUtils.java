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

package net.ontopia.topicmaps.utils;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.ontopia.utils.DeciderIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;

/**
 * INTERNAL: Utility class for creating topic map-based deciders.
 * @since 2.0
 */
public class TMDeciderUtils {

  /**
   * INTERNAL: Creates a decider that approves all objects that
   * implement TypedIF and which have a type included in the
   * collection.  All other objects are rejected.
   * @param oktypes a collection of TopicIF objects
   */
  public static DeciderIF getTypeDecider(Collection oktypes) {
    return new TypeDecider(oktypes);
  }

  /**
   * INTERNAL: Creates a decided that approves all objects that
   * implement TypedIF and which has a type which has one of the
   * subject identifiers as one of its subject identifiers. All other
   * objects are rejected.
   * @param okpsis a collection of LocatorIF or String objects
   */
  public static DeciderIF getTypePSIDecider(Collection okpsis)
    throws MalformedURLException {
    return new TypePSIDecider(okpsis);
  }

  // --- Internal classes

  static class TypeDecider implements DeciderIF {
    private Collection oktypes;
    
    public TypeDecider(Collection oktypes) {
      this.oktypes = oktypes;
    }
    
    @Override
    public boolean ok(Object object) {
      if (object instanceof TypedIF) {
        TypedIF typed = (TypedIF) object;
        return oktypes.contains(typed.getType());
      } 
      return false;
    }
  }

  static class TypePSIDecider implements DeciderIF {
    private Collection okpsis;
    
    public TypePSIDecider(Collection okpsis) throws MalformedURLException {
      this.okpsis = new HashSet();
      Iterator it = okpsis.iterator();
      while (it.hasNext()) {
        Object obj = it.next();
        LocatorIF psi;
        if (obj instanceof LocatorIF)
          psi = (LocatorIF) obj;
        else
          psi = new URILocator((String) obj);
        this.okpsis.add(psi);
      }
    }
    
    @Override
    public boolean ok(Object object) {
      if (object instanceof TypedIF) {
        TopicIF type = ((TypedIF) object).getType();
        if (type == null)
          return false;
        
        Iterator it = type.getSubjectIdentifiers().iterator();
        while (it.hasNext())
          if (okpsis.contains(it.next()))
            return true;
      } 
      return false;
    }
  }
}
