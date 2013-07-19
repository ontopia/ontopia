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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.utils.QueryUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Object used to hold contextual information while
 * processing tuples. Note that mapping, relation, topicMap and
 * baseLocator are all required properties.
 */
public class Context {
  
  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(Context.class.getName());

  protected RelationMapping rmapping;

  protected Relation relation;
  protected TopicMapIF topicmap;
  protected LocatorIF baseloc;
  protected TopicMapBuilderIF builder;

  protected QueryProcessorIF qp;
  protected DeclarationContextIF dc;
  
  protected Object[] entityObjects;
  protected Collection[] extents;

  protected Collection newObjects;
  protected Collection oldObjects;
  protected Map oldValues;
  
  protected static final int MAX_DSCANDIDATES = 5000;
  protected Set dsCandidates = new HashSet(MAX_DSCANDIDATES);

  Context() {
  }
  
  // -----------------------------------------------------------------------------
  // Semi-public methods
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Gets the relation mapping currently in use.
   */    
  public RelationMapping getMapping() {    
    return rmapping;
  }

  /**
   * INTERNAL: Sets the current relation mapping.
   */    
  public void setMapping(RelationMapping rmapping) {
    this.rmapping = rmapping;
  }

  /**
   * INTERNAL: Sets the current relation.
   */    
  public void setRelation(Relation relation) {
    this.relation = relation;
    this.entityObjects = new Object[relation.getEntities().size()];
    if (this.newObjects == null ||
        !this.newObjects.isEmpty())
      this.newObjects = new HashSet();
    if (this.oldObjects == null ||
        !this.oldObjects.isEmpty())
      this.oldObjects = new HashSet();
    if (this.oldValues == null ||
        !this.oldValues.isEmpty())
      this.oldValues = new HashMap();
    this.extents = new Collection[this.entityObjects.length];
  }

  /**
   * INTERNAL: Gets the topic map currently in use.
   */    
  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  /**
   * INTERNAL: Sets the current topic map.
   */    
  public void setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    this.builder = topicmap.getBuilder();
    this.qp = null;
    this.dc = null;
  }


  /**
   * INTERNAL: Gets the topic map query processor currently in use.
   */    
  public QueryProcessorIF getQueryProcessor() {
    if (qp == null)
      qp = QueryUtils.getQueryProcessor(getTopicMap());
    return qp;
  }

  public DeclarationContextIF getDeclarationContext() {
    if (dc == null) {
      String qdecl = getMapping().getQueryDeclarations();
      try {
        dc = QueryUtils.parseDeclarations(getTopicMap(), qdecl);
      } catch (InvalidQueryException e) {
        throw new DB2TMConfigException("Invalid query declarations: " + qdecl);
      }
    }
    return dc;
  }
  
  /**
   * INTERNAL: Gets the base locator currently in use.
   */    
  public LocatorIF getBaseLocator() {
    return baseloc;
  }

  /**
   * INTERNAL: Sets the current base locator.
   */    
  public void setBaseLocator(LocatorIF baseloc) {
    this.baseloc = baseloc;
  }

  /**
   * INTERNAL: Gets the topic map builder currently in use. This
   * builder is retrieved from the current topic map, so this is
   * primarily a convenience method.
   */
  TopicMapBuilderIF getBuilder() {
    return builder;
  }

  // -----------------------------------------------------------------------------
  // Entity objects
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Sets the current entity object.
   */    
  void setEntityObject(int ix, Object object) {
    this.entityObjects[ix] = object;
    // remove object from extent
    if (object != null && this.extents[ix] != null) {
      this.extents[ix].remove(object);      
    }
  }

  /**
   * INTERNAL: Gets the entity object by id
   */    
  Object getEntityObjectById(String id) {
    List entities = relation.getEntities();
    for (int i=0; i < entityObjects.length; i++) {
      Entity e = (Entity)entities.get(i);
      String eid = e.getId();
      if (id.equals(eid))
        return this.entityObjects[i];
    }
    throw new DB2TMConfigException("Entity reference " + id + " does not exist.");
  }

  /**
   * INTERNAL: Merge two topics. This method will replace all
   * references to the old topic with references to the new one.
   */    
  void mergeTopics(TopicIF target, TopicIF source) {
    // replace in entity object array
    for (int i=0; i < entityObjects.length; i++) {
      Object eo = entityObjects[i];
      if (eo != null && eo.equals(source)) {
        entityObjects[i] = target;
      }
    }
    // merge topics
    MergeUtils.mergeInto(target, source);
  }
  
  /**
   * INTERNAL: Register an object as an existing topic map object. The
   * method will return true if this is the first time we've seen it.
   */    
  boolean registerOldObject(Object object) {
    if (this.newObjects.contains(object))
      // if it is a new object return false
      return false;
    else
      // return true if this is the first time we see this object
      return this.oldObjects.add(object);
  }

  /**
   * INTERNAL: Register an object as a new topic map object (created
   * by db2tm). The method will return true if this is the first time
   * we've seen it.
   */    
  boolean registerNewObject(Object object) {
    // return true if this is the first time we see this object
    return this.newObjects.add(object);
  }

  /**
   * INTERNAL: Register the existing field values of an old object.
   */    
  void registerOldFieldValues(Object object, List[] values) {
    this.oldValues.put(object, values);
  }

  /**
   * INTERNAL: Return the existing field values of an old object.
   */    
  Object reuseOldFieldValue(Object object, int fieldIndex) {
    List[] fieldValues = (List[]) this.oldValues.get(object);
    if (fieldValues == null || fieldIndex > fieldValues.length-1)
      return null;
    List values = fieldValues[fieldIndex];
    if (values == null || values.isEmpty())
      return null;
    // reuse last object
    return values.remove(values.size() - 1);
  }

  void removeOldValues() {
    Iterator iter = this.oldValues.values().iterator();
    while (iter.hasNext()) {
      List[] fields = (List[])iter.next();
      if (fields != null && fields.length != 0) {
        for (int f=0; f < fields.length; f++) {
          List value = fields[f];
          if (value != null && !value.isEmpty()) {
            for (int v=0; v < value.size(); v++) {
              Object o = value.get(v);
              if (o instanceof TopicNameIF) {
                TopicNameIF bn = (TopicNameIF)o;
                TopicIF topic = bn.getTopic();
                // remove existing characteristic
                if (topic != null) {
                  log.debug("      -N " + topic + " " + bn);
                  bn.remove();
                  // notify context
                  characteristicsChanged(topic);
                }
              } else if (o instanceof OccurrenceIF) {
                OccurrenceIF oc = (OccurrenceIF)o;
                TopicIF topic = oc.getTopic();
                // remove existing characteristic
                if (topic != null) {
                  log.debug("      -O " + topic + " " + oc);
                  oc.remove();
                  // notify context
                  characteristicsChanged(topic);
                }
              } else if (o instanceof AssociationRoleIF) {
                AssociationRoleIF r = (AssociationRoleIF)o;
                // remove existing characteristic
                TopicIF topic = r.getPlayer();
                AssociationIF a = r.getAssociation();
                if (a != null) {
                  if (a.getTopicMap() != null) {
                    log.debug("      -R "  + topic + " :" + r.getType());
                    a.remove();
                    // notify context
                    if (topic != null) characteristicsChanged(topic);
                  }
                }
              } else {
                if (o != null)
                  System.err.println("Unknown old value: " + o);
              }
            }
          }          
        }
      }
    }
  }
  
  // -----------------------------------------------------------------------------
  // Extents
  // -----------------------------------------------------------------------------

  // NOTE: only used by full rescan synchronization
  
  public void loadExtents() {
    // prepare extent collections. objects in these collections will
    // be removed from the extent when they are accessed as entity
    // objects.
    List entities = relation.getEntities();    
    for (int i=0; i < entities.size(); i++) {
      Entity entity = (Entity)entities.get(i);
      if (entity.isPrimary()) {
        List extentQueries = entity.getExtentQueries();
        extents[i] = new HashSet();
        if (!extentQueries.isEmpty()) {
          for (int q=0; q < extentQueries.size(); q++) {
            accumulateObjectsFromQuery((String)extentQueries.get(q), null, extents[i]);
          }
        } else {
          // check to see if the entity should have a defaulted extent query
          if (entity.getEntityType() == Entity.TYPE_TOPIC) {
            // for each topic type apply default extent query
            String[] types = entity.getTypes();            
            if (types != null) {
              for (int t=0; t < types.length; t++) {
                TopicIF type = Utils.getTopic(types[t], this);
                if (type != null) {
                  String extentQuery = "direct-instance-of($O, %TYPE%)?";
                  Map params = Collections.singletonMap("TYPE", type);
                  log.info("      defaulting extent query for topic type '" + types[t] + "': " + extentQuery);
                  accumulateObjectsFromQuery(extentQuery, params, extents[i]);
                } else {
                  log.warn("      not able to figure out default extent query for topic type '" + types[t] + "'");
                }
              }
            }
          } else if (entity.getEntityType() == Entity.TYPE_ASSOCIATION) {
            // apply default extent query for association type
            String atype = entity.getAssociationType();
            if (atype != null) {
              TopicIF type = Utils.getTopic(atype, this);
              if (type != null) {
                String extentQuery = "association($O), type($O, %TYPE%)?";
                Map params = Collections.singletonMap("TYPE", type);
                log.info("      defaulting extent query for association type '" + atype + "': " + extentQuery);
                accumulateObjectsFromQuery(extentQuery, params, extents[i]);
              } else {
                log.warn("      not able to figure out default extent query for association type '" + atype + "'");
              }
            }
          }
        }
      }
    }
  }

  public void removeExtentObjects() {
    // remove leftover extent objects
    List entities = relation.getEntities();
    for (int i=0; i < entities.size(); i++) {
      Entity entity = (Entity)entities.get(i);
      if (entity.isPrimary()) {
        if (extents[i] != null) {
          log.debug("      removing objects from relation " + relation.getName() + " extent '" + entity.getId() + "'");
          Iterator iter = extents[i].iterator();
          if (entity.getEntityType() == Entity.TYPE_TOPIC) {
            while (iter.hasNext()) {
              TopicIF topic = (TopicIF)iter.next();
              log.debug("      !" + topic);
              topic.remove();
            }
          } else if (entity.getEntityType() == Entity.TYPE_ASSOCIATION) {
            while (iter.hasNext()) {
              AssociationIF assoc = (AssociationIF)iter.next();
              log.debug("      !" + assoc);
              assoc.remove();
            }
          } else {
            throw new DB2TMInputException("Unknown entity type: " + entity.getEntityType());
          }
          log.debug("      removed " + extents[i].size() + " objects from relation " + relation.getName() + " extent '" + entity.getId() + "'");
          extents[i] = null;
        }
      }
    }
  }
  
  private void accumulateObjectsFromQuery(String query, Map params, Collection objects) {
    QueryProcessorIF qp = getQueryProcessor();
    try {
      QueryResultIF qr = (params == null ?
                          qp.execute(query, getDeclarationContext()) :
                          qp.execute(query, params, getDeclarationContext()));
      try {
        while (qr.next()) {
          objects.add(qr.getValue(0));
        }
      } finally {
        qr.close();
      }
    } catch (InvalidQueryException e) {
      throw new DB2TMConfigException("Invalid extent query: " + query, e);
    }      
  }
  
  // ---------------------------------------------------------------------------
  // Duplicate suppression
  // ---------------------------------------------------------------------------
  
  void characteristicsChanged(TopicIF topic) {
    dsCandidates.add(topic);    
    if (dsCandidates.size() == MAX_DSCANDIDATES) {
      log.debug("Suppressing duplicates: " + dsCandidates.size());
      Iterator iter = dsCandidates.iterator();
      while (iter.hasNext()) {
        TopicIF candidate = (TopicIF)iter.next();
        if (candidate.getTopicMap() != null) {
          DuplicateSuppressionUtils.removeDuplicates(candidate);
          DuplicateSuppressionUtils.removeDuplicateAssociations(candidate);
        }
      }
      dsCandidates.clear();
    }
  }
  
  public void close() {
    if (dsCandidates.size() > 0) {
      log.debug("Suppressing duplicates: " + dsCandidates.size());
      Iterator iter = dsCandidates.iterator();
      while (iter.hasNext()) {
        TopicIF candidate = (TopicIF)iter.next();
        if (candidate.getTopicMap() != null) {
          DuplicateSuppressionUtils.removeDuplicates(candidate);
          DuplicateSuppressionUtils.removeDuplicateAssociations(candidate);
        }
      }
      dsCandidates.clear();
    }
  }
  
}
