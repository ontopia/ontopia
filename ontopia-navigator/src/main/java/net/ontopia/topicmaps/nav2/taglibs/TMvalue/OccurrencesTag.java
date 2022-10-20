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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import javax.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseScopedTag;
import net.ontopia.utils.FilterIF;

/**
 * INTERNAL: Value Producing Tag for finding all the occurrences
 * of all the topics in a collection.
 */
public class OccurrencesTag extends BaseScopedTag {

  // contstants
  public static final String TYPE_NAME_ALL      = "all";
  public static final String TYPE_NAME_INTERNAL = "internal";
  public static final String TYPE_NAME_EXTERNAL = "external";

  protected static final int TYPE_ALL      = 1;
  protected static final int TYPE_INTERNAL = 2;
  protected static final int TYPE_EXTERNAL = 3;
  
  // tag attributes
  private int type = TYPE_ALL;
  
  @Override
  public Collection process(Collection topics) throws JspTagException {
    // find all occurrences of all topics in collection
    if (topics == null) {
      return Collections.EMPTY_SET;
    } else {
      ArrayList occurrences = new ArrayList();
      Iterator iter = topics.iterator();
      Object obj = null;
      TopicIF topic = null; // current topic
      FilterIF scopeFilter = null;

      Iterator iterOccs; // only intermediate use
      OccurrenceIF occ; // only intermediate use

      Collection occs;
      Collection filteredOccs;

      // log.debug("---OccurrencesTag - type: " + type);
      // setup scope filter for user context filtering
      if (useUserContextFilter) {
        scopeFilter = getScopeFilter(SCOPE_OCCURRENCES);
      }

      try {
        while (iter.hasNext()) {
          obj = iter.next();
          topic = (TopicIF) obj;
          occs = topic.getOccurrences();
          filteredOccs = null;
          switch (type) {
          case TYPE_ALL: {
            // --- get all occurrences
            filteredOccs = occs;
            break;
          }
          case TYPE_INTERNAL: {
            // --- get only internal occurrences
            iterOccs = occs.iterator();
            filteredOccs = new ArrayList(occs.size());
            while (iterOccs.hasNext()) {
              occ = (OccurrenceIF) iterOccs.next();
              if (Objects.equals(DataTypes.TYPE_STRING, occ.getDataType())) {
                filteredOccs.add(occ);
                }
            }
            break;
          }
          case TYPE_EXTERNAL: {
            // --- get only external occurrences
            iterOccs = occs.iterator();
            filteredOccs = new ArrayList(occs.size());
            while (iterOccs.hasNext()) {
              occ = (OccurrenceIF) iterOccs.next();
              if (Objects.equals(DataTypes.TYPE_URI, occ.getDataType())) {
                filteredOccs.add(occ);
                }
            }
            break;
          }
          }
          
          // apply user context filter
          if (!filteredOccs.isEmpty() && scopeFilter != null) {
            filteredOccs = scopeFilter.filter(filteredOccs.iterator());
          }

          // now add the filtered occurrences to the result
          occurrences.addAll(filteredOccs);
          
        } // while iter
      } catch (ClassCastException e) {
        String msg = "OccurrencesTag expected to get a input collection of topic" +
          "instances, but got instance of class " + obj.getClass().getName();
        throw new NavigatorRuntimeException(msg);
      }
      return occurrences;
      // log.debug("OccurrencesTag: " + occurrences + " type: " + type +
      //           " contextFilter: " + useUserContextFilter +
      //           " variableName: " + variableName);
    }
  }

  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  /**
   * Sets type of wanted occurrences: The following values are
   * allowed:
   * <ul>
   *   <li><code>internal</code>,
   *   <li><code>external</code>,
   *   <li><code>all</code> (default).
   * </ul>
   */
  public void setType(String typeString) throws NavigatorRuntimeException {
    if (typeString.equals(TYPE_NAME_INTERNAL)) {
      type = TYPE_INTERNAL;
    } else if (typeString.equals(TYPE_NAME_EXTERNAL)) {
      type = TYPE_EXTERNAL;
    } else if (typeString.equals(TYPE_NAME_ALL)) {
      type = TYPE_ALL;
    } else {
      throw new NavigatorRuntimeException("Occurrences tag got invalid value for 'type' attribute: " + typeString);
    }
  }
  
}
