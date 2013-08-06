/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.PageContext;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: Helper class used by DB2TM internals.
 */
public class Utils {

  private Utils() {
  }

  /**
   * INTERNAL: Helper method for maintaining a relation mapping
   * instance throughout a page context.
   */  
  public static RelationMapping getRelationMapping(PageContext ctxt) {
    RelationMapping db = (RelationMapping)
      ctxt.getAttribute("RelationMapping", ctxt.APPLICATION_SCOPE);
    if (db == null) {
      db = new RelationMapping();
      ctxt.setAttribute("RelationMapping", db, ctxt.APPLICATION_SCOPE);
    }
    return db;
  }

  /**
   * INTERNAL: Returns a map where the keys are data sources and each
   * entry is a collection of their individual relations. Before
   * returning all relations will be verified against the relations
   * declared in the mapping. If relations are missing an error is
   * issued indicating which ones are missing.
   */
  public static Map verifyRelationsForMapping(RelationMapping rmapping) {

    // build return value
    Collection ds = rmapping.getDataSources();
    Map foundRelations = new HashMap(ds.size());
    Iterator diter = ds.iterator();
    while (diter.hasNext()) {
      DataSourceIF datasource = (DataSourceIF)diter.next();
      foundRelations.put(datasource, datasource.getRelations());
    }
    
    // detect missing relations
    List missingRelations = new ArrayList();
    Iterator iter = rmapping.getRelations().iterator();
    while (iter.hasNext()) {
      Object relation = iter.next();
      boolean relationMapped = false;

      Iterator fiter = foundRelations.values().iterator();
      while (fiter.hasNext()) {
        Collection frels = (Collection)fiter.next();
        if (frels.contains(relation)) {
          relationMapped = true;
          break;
        };
      }
      if (!relationMapped) missingRelations.add(relation);
    }
    // complain if found mappings without relations
    int size = missingRelations.size();
    if (size > 1) {
      String[] relnames = new String[size];
      for (int i=0; i < relnames.length; i++) {
        relnames[i] = ((Relation)missingRelations.get(i)).getName();
      }
      throw new DB2TMException("No relations found for mappings: " +
                               StringUtils.join(relnames, ", "));
    } else if (size == 1) {
      throw new DB2TMException("No relation found for mapping: " +
                               ((Relation)missingRelations.get(0)).getName());
    }

    return foundRelations;
  }
  
  // ---------------------------------------------------------------------------
  // Utility methods
  // ---------------------------------------------------------------------------
  
  static TopicIF getTopic(String id, Context ctx) {
    // Note: null values or empty strings are considered dead
    if (isValueEmpty(id))
      return null;

    if (id.charAt(0) == '#') {
      String entity_id = id.substring(1);
      return (TopicIF)ctx.getEntityObjectById(entity_id);
    }

    TopicMapIF tm = ctx.getTopicMap();
    
    // resolve reference
    int loctype;
    LocatorIF loc;    
    int cix = id.indexOf(':');
    if (cix >= 1) {
      // prefix reference
      String prefix_id = id.substring(0, cix);
      Prefix prefix = ctx.getMapping().getPrefix(prefix_id);
      if (prefix == null)
        throw new DB2TMConfigException("Unknown prefix: '" + prefix_id +
                                       "' (value='" + id + "')");
      String relloc = prefix.getLocator() + id.substring(cix + 1);
      loc = ctx.getBaseLocator().resolveAbsolute(relloc);
      loctype = prefix.getType();
    } else {
      throw new DB2TMConfigException("Illegal prefixed value: '" + id + "'");
    }

    // look up topic by identifier
    switch (loctype) {
    case Prefix.TYPE_SUBJECT_IDENTIFIER:
      TopicIF topic = tm.getTopicBySubjectIdentifier(loc);
      if (topic != null)
        return topic;
      break;
    case Prefix.TYPE_ITEM_IDENTIFIER:
      TMObjectIF tmobject = tm.getObjectByItemIdentifier(loc);
      if (tmobject != null)
        return (TopicIF) tmobject;
      break;
    case Prefix.TYPE_SUBJECT_LOCATOR:
      topic = tm.getTopicBySubjectLocator(loc);
      if (topic != null)
        return topic;
      break;
    }

    // create new topic
    TopicIF newtopic = ctx.getBuilder().makeTopic();
    ctx.registerNewObject(newtopic);

    // add identity
    switch (loctype) {
    case Prefix.TYPE_SUBJECT_IDENTIFIER:
      newtopic.addSubjectIdentifier(loc);
      break;
    case Prefix.TYPE_ITEM_IDENTIFIER:
      newtopic.addItemIdentifier(loc);
      break;
    case Prefix.TYPE_SUBJECT_LOCATOR:
      newtopic.addSubjectLocator(loc);
      break;
    }

    return newtopic;
  }

  static String getValue(Relation relation, Entity entity, Field field,
                                   String[] tuple, Context ctx) {
    return field.getValue(tuple);
  }

  static boolean isValueEmpty(String value) {
    return (value == null || value.equals(""));
  }
  
  static LocatorIF getLocator(Relation relation, Entity entity, Field field,
      String[] tuple, Context ctx) {
    String value = getValue(relation, entity, field, tuple, ctx);
    if (isValueEmpty(value))
      return null;
    else
      return ctx.getBaseLocator().resolveAbsolute(value);
  }

  static String expandPrefixedValue(String value, Context ctx) {
    int cix = value.indexOf(':');
    if (cix >= 1) {
      // prefix reference
      String prefix_id = value.substring(0, cix);
      Prefix prefix = ctx.getMapping().getPrefix(prefix_id);
      if (prefix == null)
        throw new DB2TMConfigException("Unknown prefix: '" + prefix_id +
                                       "' (value='" + value + "')");      
      return prefix.getLocator() + value.substring(cix + 1);
    } else {
      throw new DB2TMConfigException("Illegal prefixed value: '" + value + "'");
    }
  }
  
}
