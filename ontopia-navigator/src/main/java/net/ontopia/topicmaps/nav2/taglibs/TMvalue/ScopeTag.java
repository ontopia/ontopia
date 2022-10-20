/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.ScopedIF;

import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all themes
 * in the scope of all the objects in a collection.
 *
 * @see net.ontopia.topicmaps.core.ScopedIF#getScope()
 */
public class ScopeTag extends BaseValueProducingAndAcceptingTag {
  
  @Override
  public Collection process(Collection characteristics) throws JspTagException {
    // find all themes of all characteristics in collection
    if (characteristics == null) {
      return Collections.EMPTY_SET;
    } else {
      ArrayList themes = new ArrayList();
      Iterator iter = characteristics.iterator();
      ScopedIF object = null;
      while (iter.hasNext()) {
        object = (ScopedIF) iter.next();
        // just get the scope stated for this object
        if (object != null) {
          themes.addAll( object.getScope() );
        }
      } // while
      return themes;
    }
  }

  // -----------------------------------------------------------
  // tag attributes
  // -----------------------------------------------------------

  /**
   * DEPRECATED: should use effective scope, default: yes.
   */
  public void setEffective(String useEffectiveScope) {
    // ignore
  }
  
}
