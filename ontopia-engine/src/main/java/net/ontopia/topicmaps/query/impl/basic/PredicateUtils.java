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

package net.ontopia.topicmaps.query.impl.basic;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Shared utilities for the predicate implementations.
 */
public class PredicateUtils {
  // generic
  public static final int NO_OPERATION            = 0;
                                                  
  // object-to-one                                
  public static final int NAME_TO_TOPIC           = 1;
  public static final int OCCURRENCE_TO_TOPIC     = 2;
  public static final int ROLE_TO_ASSOCIATION     = 3;
  public static final int REIFIER_TO_REIFIED      = 4;
  public static final int REIFIED_TO_REIFIER      = 5;
  public static final int INSTANCE_TO_TYPE        = 6;
  public static final int OBJECT_TO_VALUE         = 7;
  public static final int ROLE_TO_PLAYER          = 8;
  public static final int OBJECT_TO_RESOURCE      = 9;
  public static final int VNAME_TO_TNAME          = 10;
  public static final int SUBJLOC_TO_TOPIC        = 12;
  public static final int SRCLOC_TO_OBJECT        = 13;
  public static final int SUBJID_TO_TOPIC         = 14;
  public static final int STR_TO_LENGTH           = 15;
  public static final int OBJECT_TO_ID            = 16;
  public static final int ID_TO_OBJECT            = 17;
  public static final int EQUAL_TO_EQUAL          = 18;
  public static final int OBJECT_TO_DATATYPE      = 19;
                                                  
  // object-to-many                               
  public static final int ASSOCIATION_TO_ROLE     = 101;
  public static final int SCOPED_TO_THEME         = 102;
  public static final int RESOURCE_TO_OBJECT      = 103;
  public static final int TNAME_TO_VNAME          = 104;
  public static final int THEME_TO_SCOPED         = 105;
  public static final int TOPIC_TO_SUBJLOC        = 11;
  public static final int TOPIC_TO_SUBJID         = 106;
  public static final int OBJECT_TO_SRCLOC        = 107;
  public static final int TOPIC_TO_OCCURRENCE     = 108;
  public static final int TOPIC_TO_NAME           = 109;
                                                  
  // filter                                       
  public static final int FILTER_ROLE_PLAYER      = 201;
  public static final int FILTER_REIFIES          = 202;
  public static final int FILTER_TOPIC_NAME       = 203;
  public static final int FILTER_VALUE            = 204;
  public static final int FILTER_TYPE             = 205;
  public static final int FILTER_VARIANT          = 206;
  public static final int FILTER_SCOPE            = 207;
  public static final int FILTER_ASSOCIATION_ROLE = 208;
  public static final int FILTER_RESOURCE         = 209;
  public static final int FILTER_SUBJLOC          = 210;
  public static final int FILTER_SUBJID           = 211;
  public static final int FILTER_SRCLOC           = 212;
  public static final int FILTER_STR_STARTS_WITH  = 213;
  public static final int FILTER_STR_CONTAINS     = 214;
  public static final int FILTER_STR_LENGTH       = 215;
  public static final int FILTER_ID               = 216;
  public static final int FILTER_EQUALS           = 217;
  public static final int FILTER_OCCURRENCE       = 218;
  public static final int FILTER_STR_ENDS_WITH    = 219;
  public static final int FILTER_DATATYPE         = 220;

  // collection-to-one
  public static final int GENERATE_REIFIES        = 301;
  public static final int GENERATE_VALUE          = 302;
  public static final int GENERATE_TYPE           = 303;
  public static final int GENERATE_RESOURCES      = 304;
  public static final int GENERATE_SUBJLOC        = 305;
  public static final int GENERATE_ID             = 306;
  public static final int GENERATE_DATATYPE       = 307;
                                                  
  // collection-to-many                           
  public static final int GENERATE_TOPIC_NAME     = 401;
  public static final int GENERATE_OCCURRENCE     = 402;
  public static final int GENERATE_VARIANTS       = 403;
  public static final int GENERATE_ROLES          = 404;
  public static final int GENERATE_ROLE_PLAYER    = 405;
  public static final int GENERATE_SCOPED         = 406;
  public static final int GENERATE_SUBJID         = 407;
  public static final int GENERATE_SRCLOC         = 408;

  public static QueryMatches objectToOne(QueryMatches matches, int fromix,
                                         int toix, Class fromclass,
                                         int operation)
    throws InvalidQueryException {
    
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      if (!fromclass.isInstance(matches.data[ix][fromix])) {
        continue;
      }
      
      Object[] newRow = (Object[]) matches.data[ix].clone();
      switch(operation) {
      case ROLE_TO_PLAYER:
        newRow[toix] = ((AssociationRoleIF) newRow[fromix]).getPlayer();
        if (newRow[toix] == null) {
          continue;
      }
        break;
      case NAME_TO_TOPIC:
        newRow[toix] = ((TopicNameIF) newRow[fromix]).getTopic(); break;
      case OCCURRENCE_TO_TOPIC:
        newRow[toix] = ((OccurrenceIF) newRow[fromix]).getTopic(); break;
      case ROLE_TO_ASSOCIATION:
        newRow[toix] = ((AssociationRoleIF) newRow[fromix]).getAssociation(); break;
      case REIFIER_TO_REIFIED:
        newRow[toix] = ((TopicIF) newRow[fromix]).getReified();
        if (newRow[toix] == null) {
          continue;
      }
        break;
      case REIFIED_TO_REIFIER:
        newRow[toix] = ((ReifiableIF) newRow[fromix]).getReifier();
        if (newRow[toix] == null) {
          continue;
      }
        break;
      case INSTANCE_TO_TYPE:
        newRow[toix] = ((TypedIF) newRow[fromix]).getType();
        if (newRow[toix] == null) {
          continue;
      }
        break;
      case OBJECT_TO_VALUE:
        if (newRow[fromix] instanceof TopicNameIF) {
          newRow[toix] = ((TopicNameIF) newRow[fromix]).getValue();
        } else if (newRow[fromix] instanceof VariantNameIF) {
					VariantNameIF vn = (VariantNameIF) newRow[fromix];
					if (Objects.equals(vn.getDataType(), DataTypes.TYPE_URI)) { // exclude xsd:anyURI
            continue;
          }
					newRow[toix] = vn.getValue();
			  } else if (newRow[fromix] instanceof OccurrenceIF) {
					OccurrenceIF occ = (OccurrenceIF) newRow[fromix];
					if (Objects.equals(occ.getDataType(), DataTypes.TYPE_URI)) { // exclude xsd:anyURI
            continue;
          }
					newRow[toix] = occ.getValue();
				}
        if (newRow[toix] == null) {
          continue;
        }
        break;
      case OBJECT_TO_RESOURCE:
        if (newRow[fromix] instanceof VariantNameIF) {
          newRow[toix] = ((VariantNameIF) newRow[fromix]).getLocator();
        } else if (newRow[fromix] instanceof OccurrenceIF) {
          newRow[toix] = ((OccurrenceIF) newRow[fromix]).getLocator();
        }
        
        if (newRow[toix] == null) {
          continue;
        } else {
          newRow[toix] = ((LocatorIF) newRow[toix]).getAddress();
        }
        
        break;
      case VNAME_TO_TNAME:
        newRow[toix] = ((VariantNameIF) newRow[fromix]).getTopicName();
        break;
      case SUBJLOC_TO_TOPIC:
        LocatorIF loc = getLocator(newRow[fromix]);
        newRow[toix] = matches.getQueryContext().getTopicMap().getTopicBySubjectLocator(loc);
        if (newRow[toix] == null) {
          continue;
        }
        break;
      case SUBJID_TO_TOPIC:
        loc = getLocator(newRow[fromix]);
        newRow[toix] = matches.getQueryContext().getTopicMap().getTopicBySubjectIdentifier(loc);
        if (newRow[toix] == null) {
          continue;
        }
        break;
      case SRCLOC_TO_OBJECT:
        loc = getLocator(newRow[fromix]);
        newRow[toix] = matches.getQueryContext().getTopicMap().getObjectByItemIdentifier(loc);
        if (newRow[toix] == null) {
          continue;
        }
        break;
      case STR_TO_LENGTH:
        String str = (String) newRow[fromix];
        newRow[toix] = str.length();
        break;
      case OBJECT_TO_ID:
        TMObjectIF obj = (TMObjectIF) newRow[fromix];
        newRow[toix] = obj.getObjectId();
        break;
      case ID_TO_OBJECT:
        newRow[toix] = matches.getQueryContext().getTopicMap().getObjectById((String) newRow[fromix]);
        break;
      case EQUAL_TO_EQUAL:
        newRow[toix] = newRow[fromix];
        break;
      case OBJECT_TO_DATATYPE:
        if (newRow[fromix] instanceof VariantNameIF) {
          newRow[toix] = ((VariantNameIF) newRow[fromix]).getDataType();
        } else if (newRow[fromix] instanceof OccurrenceIF) {
          newRow[toix] = ((OccurrenceIF) newRow[fromix]).getDataType();
        }
        
        if (newRow[toix] == null) {
          continue;
        } else {
          newRow[toix] = ((LocatorIF) newRow[toix]).getAddress();
        }
        break;
      default:
        throw new OntopiaRuntimeException("INTERNAL ERROR: Unknown operation code " +
                                          operation);
      }

      if (result.last+1 == result.size) {
        result.increaseCapacity();
      }
      result.last++;
      result.data[result.last] = newRow;
    }

    return result;
  }

  public static QueryMatches objectToMany(QueryMatches matches, int fromix,
                                          int toix, Class fromclass,
                                          int operation, IndexIF index)
    throws InvalidQueryException {
    return objectToMany(matches, fromix, toix, fromclass, operation, index,
                        null);
  }
  
  public static QueryMatches objectToMany(QueryMatches matches, int fromix,
                                          int toix, Class fromclass,
                                          int operation, IndexIF index1,
                                          IndexIF index2)
    throws InvalidQueryException {
    
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      if (!fromclass.isInstance(matches.data[ix][fromix])) {
        continue;
      }

      Collection objects = null;
      switch(operation) {
      case ASSOCIATION_TO_ROLE:
        objects = ((AssociationIF) matches.data[ix][fromix]).getRoles();
        break;
      case SCOPED_TO_THEME:
        objects = ((ScopedIF) matches.data[ix][fromix]).getScope();
        break;
      case RESOURCE_TO_OBJECT:
        LocatorIF locator = getLocator(matches.data[ix][fromix]);
        objects = new ArrayList(((NameIndexIF) index1).getVariants(locator.getAddress(), DataTypes.TYPE_URI));
        objects.addAll(((OccurrenceIndexIF) index2).getOccurrences(locator.getAddress(), DataTypes.TYPE_URI));
        break;
      case TNAME_TO_VNAME:
        objects = ((TopicNameIF) matches.data[ix][fromix]).getVariants();
        break;
      case TOPIC_TO_SUBJLOC:
        objects = new ArrayList();
        Iterator it = ((TopicIF) matches.data[ix][fromix]).getSubjectLocators().iterator();
        while (it.hasNext()) {
          objects.add(((LocatorIF) it.next()).getAddress());
        }
        break;
      case TOPIC_TO_SUBJID:
        objects = new ArrayList();
        it = ((TopicIF) matches.data[ix][fromix]).getSubjectIdentifiers().iterator();
        while (it.hasNext()) {
          objects.add(((LocatorIF) it.next()).getAddress());
        }
        break;
      case OBJECT_TO_SRCLOC:
        objects = new ArrayList();
        it = ((TMObjectIF) matches.data[ix][fromix]).getItemIdentifiers().iterator();
        while (it.hasNext()) {
          objects.add(((LocatorIF) it.next()).getAddress());
        }
        break;
      case THEME_TO_SCOPED:
        TopicIF theme = (TopicIF) matches.data[ix][fromix];
        ScopeIndexIF sindex = (ScopeIndexIF) index1;
        objects = new ArrayList(sindex.getAssociations(theme));
        objects.addAll(sindex.getTopicNames(theme));
        objects.addAll(sindex.getVariants(theme));
        objects.addAll(sindex.getOccurrences(theme));
        break;
      case TOPIC_TO_OCCURRENCE:
        TopicIF topic = (TopicIF) matches.data[ix][fromix];
        objects = topic.getOccurrences();
        break;
      case TOPIC_TO_NAME:
        topic = (TopicIF) matches.data[ix][fromix];
        objects = topic.getTopicNames();
      }
      
      if (objects.isEmpty()) {
        continue;
      }
      
      while (result.last + objects.size() >= result.size) {
        result.increaseCapacity();
      }

      Object[] values = objects.toArray();

      for (int valueix = 0; valueix < values.length; valueix++) {
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[toix] = values[valueix];
        result.data[++result.last] = newRow;
      }
    }

    return result;
  }

  public static QueryMatches filter(QueryMatches matches, int ix1, int ix2,
                                    Class class1, Class class2, int operation)
    throws InvalidQueryException {

    QueryMatches result = new QueryMatches(matches);
    
    for (int ix = 0; ix <= matches.last; ix++) {
      // verify types of objects
      if (!class1.isInstance(matches.data[ix][ix1]) ||
          !class2.isInstance(matches.data[ix][ix2])) {
        continue;
      }

      // find correct value
      Object object = null;
      switch (operation) {
      case FILTER_TYPE:
        object = ((TypedIF) matches.data[ix][ix1]).getType();
        break;
      case FILTER_SCOPE:
        Object theme = matches.data[ix][ix2];
        if (((ScopedIF) matches.data[ix][ix1]).getScope().contains(theme)) {
          object = theme;
        }
        break;
      case FILTER_ROLE_PLAYER:
        AssociationRoleIF role = (AssociationRoleIF) matches.data[ix][ix1];
        object = role.getPlayer();
        break;
      case FILTER_REIFIES:
        ReifiableIF reified = (ReifiableIF) matches.data[ix][ix1];
        object = reified.getReifier();
        break;
      case FILTER_TOPIC_NAME:
        TopicNameIF bn = (TopicNameIF) matches.data[ix][ix1];
        object = bn.getTopic();
        break;
      case FILTER_OCCURRENCE:
        OccurrenceIF occ = (OccurrenceIF) matches.data[ix][ix1];
        object = occ.getTopic();
        break;
      case FILTER_VALUE:
        if (matches.data[ix][ix1] instanceof TopicNameIF) {
          object = ((TopicNameIF) matches.data[ix][ix1]).getValue();
        } else if (matches.data[ix][ix1] instanceof VariantNameIF) {
					VariantNameIF vn = (VariantNameIF) matches.data[ix][ix1];
					if (!Objects.equals(vn.getDataType(), DataTypes.TYPE_URI)) { // exclude xsd:anyURI
            object = vn.getValue();
          }
        } else if (matches.data[ix][ix1] instanceof OccurrenceIF) {
					OccurrenceIF occ2 = (OccurrenceIF) matches.data[ix][ix1];
					if (!Objects.equals(occ2.getDataType(), DataTypes.TYPE_URI)) { // exclude xsd:anyURI
            object = occ2.getValue();
          }
				}
        break;
      case FILTER_VARIANT:
        object = ((VariantNameIF) matches.data[ix][ix1]).getTopicName();
        break;
      case FILTER_ASSOCIATION_ROLE:
        object = ((AssociationRoleIF) matches.data[ix][ix1]).getAssociation();
        break;
      case FILTER_RESOURCE:
        if (matches.data[ix][ix1] instanceof OccurrenceIF) {
          object = ((OccurrenceIF) matches.data[ix][ix1]).getLocator();
        } else if (matches.data[ix][ix1] instanceof VariantNameIF) {
          object = ((VariantNameIF) matches.data[ix][ix1]).getLocator();
        }
        if (object != null) {
          object = ((LocatorIF) object).getAddress();
        }
        break;
      case FILTER_SUBJLOC:
        LocatorIF loc = getLocator(matches.data[ix][ix2]);
        if (((TopicIF) matches.data[ix][ix1]).getSubjectLocators().contains(loc)) {
          object = matches.data[ix][ix2];
        }
        break;
      case FILTER_SUBJID:
        loc = getLocator(matches.data[ix][ix2]);
        if (((TopicIF) matches.data[ix][ix1]).getSubjectIdentifiers().contains(loc)) {
          object = matches.data[ix][ix2];
        }
        break;
      case FILTER_SRCLOC:
        loc = getLocator(matches.data[ix][ix2]);
        if (((TMObjectIF) matches.data[ix][ix1]).getItemIdentifiers().contains(loc)) {
          object = matches.data[ix][ix2];
        }
        break;
      case FILTER_ID:
        object = ((TMObjectIF) matches.data[ix][ix1]).getObjectId();
        break;
      case FILTER_STR_STARTS_WITH:
        String str1 = (String) matches.data[ix][ix1];
        String str2 = (String) matches.data[ix][ix2];
        if (str1.startsWith(str2)) {
          object = str2;
        }
        break;
      case FILTER_STR_ENDS_WITH:
        String string1 = (String) matches.data[ix][ix1];
        String string2 = (String) matches.data[ix][ix2];
        if (string1.endsWith(string2)) {
          object = string2;
        }
        break;
      case FILTER_STR_CONTAINS:
        str1 = (String) matches.data[ix][ix1];
        str2 = (String) matches.data[ix][ix2];
        if (str1.indexOf(str2) != -1) {
          object = str2;
        }
        break;
      case FILTER_STR_LENGTH:
        str1 = (String) matches.data[ix][ix1];
        Number num = (Number) matches.data[ix][ix2];
        if (str1.length() == num.intValue()) {
          object = num;
        }
        break;
      case FILTER_EQUALS:
        object = matches.data[ix][ix1];
        break;
      case FILTER_DATATYPE:
        LocatorIF dt;
        if (matches.data[ix][ix1] instanceof VariantNameIF) {
          dt = ((VariantNameIF) matches.data[ix][ix1]).getDataType();
        } else if (matches.data[ix][ix1] instanceof OccurrenceIF) {
          dt = ((OccurrenceIF) matches.data[ix][ix1]).getDataType();
        } else {
          throw new InvalidQueryException("Internal error!");
        }
        object = dt.getAddress();
        break;
      case NO_OPERATION:
        break;
      }

      // check value found against value given
      if (object == null || !object.equals(matches.data[ix][ix2])) {
        continue;
      }

      // ok, add match
      if (result.last+1 == result.size) {
        result.increaseCapacity();
      }
      result.last++;
      result.data[result.last] = matches.data[ix];
    }

    return result;
  }

  /**
   * Given a collection of objects, produce a single new object from
   * each of them and put it into the toix column. So for each row in
   * the input result, either remove it (if it's of the wrong type, or
   * if it has no new object) or insert the new object in the toix
   * column.
   */
  public static QueryMatches collectionToOne(QueryMatches matches,
                                             Object[] objects,
                                             int fromix, int toix,
                                             int operation) {

    QueryMatches result = new QueryMatches(matches);

    // for each object in input collection...
    for (int oix = 0; oix < objects.length; oix++) {

      // generate other object
      Object other = null;
      switch(operation) {
      case GENERATE_REIFIES:
        other = ((TopicIF) objects[oix]).getReified();
        break;
      case GENERATE_VALUE:
        if (objects[oix] instanceof TopicNameIF) {
          other = ((TopicNameIF) objects[oix]).getValue();
        } else if (objects[oix] instanceof VariantNameIF) {
					VariantNameIF vn = (VariantNameIF) objects[oix];
					if (!Objects.equals(vn.getDataType(), DataTypes.TYPE_URI)) { // exclude xsd:anyURI
            other = vn.getValue();
          }
        } else {
					OccurrenceIF occ = (OccurrenceIF) objects[oix];
					if (!Objects.equals(occ.getDataType(), DataTypes.TYPE_URI)) { // exclude xsd:anyURI
            other = occ.getValue();
          }
				}
        break;
      case GENERATE_TYPE:
        other = ((TypedIF) objects[oix]).getType();
        break;
      case GENERATE_RESOURCES:
        if (objects[oix] instanceof OccurrenceIF) {
          other = ((OccurrenceIF) objects[oix]).getLocator();
        } else {
          other = ((VariantNameIF) objects[oix]).getLocator();
        }
        if (other != null) {
          other = ((LocatorIF) other).getAddress();
        }
        break;
      case GENERATE_ID:
        other = ((TMObjectIF) objects[oix]).getObjectId();
        break;
      case GENERATE_DATATYPE:
        if (objects[oix] instanceof VariantNameIF) {
          other = ((VariantNameIF) objects[oix]).getDataType().getAddress();
        } else if (objects[oix] instanceof OccurrenceIF) {
          other = ((OccurrenceIF) objects[oix]).getDataType().getAddress();
        }
        break;
      case NO_OPERATION:
        other = objects[oix];
      }

      // does this one generate matches?
      if (other == null) {
        continue;
      }

      // duplicate match set
      for (int ix = 0; ix <= matches.last; ix++) {
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[fromix] = objects[oix];
        newRow[toix] = other;

        if (result.last+1 == result.size) {
          result.increaseCapacity();
        }
        result.last++;
        
        result.data[result.last] = newRow;
      }
    }

    return result;
  }

  /**
   * Assuming both columns are empty, iterate over a collection of
   * start values, binding the first column to the start value, and
   * the second column to each object in a collection generated from
   * the start value.
   */
  public static QueryMatches generateFromCollection(QueryMatches matches,
                                                    int fromix, int toix,
                                                    Collection startset,
                                                    int operation) {

    QueryMatches result = new QueryMatches(matches);

    // for each object in input collection
    for (Iterator it = startset.iterator(); it.hasNext(); ) {
      Object next = it.next();

      // generate new collection
      Collection contained = null;
      switch(operation) {
      case GENERATE_TOPIC_NAME:
        contained = ((TopicIF) next).getTopicNames();
        break;
      case GENERATE_OCCURRENCE:
        contained = ((TopicIF) next).getOccurrences();
        break;
      case GENERATE_VARIANTS:
        contained = ((TopicNameIF) next).getVariants();
        break;
      case GENERATE_ROLES:
        contained = ((AssociationIF) next).getRoles();
        break;
      case GENERATE_ROLE_PLAYER:
        contained = ((TopicIF) next).getRoles();
        break;
      case GENERATE_SCOPED:
        contained = ((ScopedIF) next).getScope();
        break;
      case GENERATE_SUBJLOC:
        contained = new ArrayList();
        Iterator it2 = ((TopicIF) next).getSubjectLocators().iterator();
        while (it2.hasNext()) {
          contained.add(((LocatorIF) it2.next()).getAddress());
        }
        break;
      case GENERATE_SUBJID:
        contained = new ArrayList();
        it2 = ((TopicIF) next).getSubjectIdentifiers().iterator();
        while (it2.hasNext()) {
          contained.add(((LocatorIF) it2.next()).getAddress());
        }
        break;
      case GENERATE_SRCLOC:
        contained = new ArrayList();
        it2 = ((TMObjectIF) next).getItemIdentifiers().iterator();
        while (it2.hasNext()) {
          contained.add(((LocatorIF) it2.next()).getAddress());
        }
        break;
      }

      // does this one generate matches?
      if (contained.isEmpty()) {
        continue;
      }

      // for each contained object...
      Iterator it2 = contained.iterator();
      while (it2.hasNext()) {
        Object object = it2.next();
      
        // ...duplicate match set
        for (int ix = 0; ix <= matches.last; ix++) {
          Object[] newRow = (Object[]) matches.data[ix].clone();
          newRow[fromix] = next;
          newRow[toix] = object;
          
          if (result.last+1 == result.size) {
            result.increaseCapacity();
          }
          result.last++;
        
          result.data[result.last] = newRow;
        }
      }
    }

    return result;
    
  }

  /**
   * Filters a result set so that only rows where the object in a
   * specific column is an instance of a specific class are accepted.
   */
  public static QueryMatches filterClass(QueryMatches matches, int ix1,
                                         Class klass) {
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      // verify type of object
      if (!klass.isInstance(matches.data[ix][ix1])) {
        continue;
      }

      if (result.last+1 == result.size) {
        result.increaseCapacity();
      }
      result.last++;
      result.data[result.last] = matches.data[ix];
    }

    return result;
  }

  /**
   * Returns all topic map objects in the topic map.
   */
  public static Collection getAllObjects(TopicMapIF topicmap) {
    Collection objects = new ArrayList(topicmap.getTopics().size() * 10);
    objects.add(topicmap);
    objects.addAll(topicmap.getTopics());
    objects.addAll(topicmap.getAssociations());

    Iterator it = topicmap.getAssociations().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();
      objects.addAll(assoc.getRoles());
    }

    it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      objects.addAll(topic.getTopicNames());
      objects.addAll(topic.getOccurrences());

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();
        objects.addAll(bn.getVariants());
      }
    }

    return objects;
  }

  // Comparison

  public static int compare(Object o1, Object o2) {
    if (!(o1 instanceof Comparable)) {
			int h1 = (o1 == null ? 0 : o1.hashCode());
			int h2 = (o2 == null ? 0 : o2.hashCode());
      return h1 - h2;
		}
    
    try {
      return ((Comparable) o1).compareTo(o2);
    } catch (ClassCastException e) {
      // if we compare a String with an Integer we wind up here
      return o1.getClass().getName().compareTo(o2.getClass().getName());
    }
  }

  // Internal

  private static LocatorIF getLocator(Object locator) throws InvalidQueryException {
    try {
      return new URILocator((String) locator);
    } catch (URISyntaxException e) {
      throw new InvalidQueryException("Illegal URI: " + locator);
    }
  }
}
