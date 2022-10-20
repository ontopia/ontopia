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

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all the classes
 * of all the objects in a collection.
 */
public class ClassesOfTag extends BaseValueProducingAndAcceptingTag {

  @Override
  public Collection process(Collection tmObjects) throws JspTagException {
    // find all the classes of all tmObjects in collection
    // avoid duplicate type entries therefore use a 'Set'
    if (tmObjects == null) {
      return Collections.EMPTY_SET;
    } else{
      Set types = new HashSet();
      Iterator iter = tmObjects.iterator();
      Object obj = null;
      while (iter.hasNext()) {
        obj = iter.next();
        // --- for occurrence, association, or association role objects
        if (obj instanceof TypedIF) {
          TypedIF singleTypedObj = (TypedIF) obj;
          TopicIF type = singleTypedObj.getType();
          if (type != null) {
            types.add( type );
          }
        }
        // --- for topic objects
        else if (obj instanceof TopicIF) {
          TopicIF topic = (TopicIF) obj;
          Collection _types = topic.getTypes();
          if (!_types.isEmpty()) {
            types.addAll( _types );
          }
        }
      } // while    
      return new ArrayList(types);
    }
  }

}
