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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class that performs the actual db2tm processing.
 */
public class Processor {
  
  // --- define a logging category.
  private static Logger log = LoggerFactory.getLogger(Processor.class);
  
  public static int NEVER_COMMIT_MODE = 0;
  public static int RELATIONAL_COMMIT_MODE = 1;
  public static int TUPLE_COMMIT_MODE = 2;
  public static int COUNT_COMMIT_MODE = 3;
  public static int DEFAULT_COMMIT_MODE = NEVER_COMMIT_MODE;

  private static final LocatorIF LOC_SYNCHRONIZATION_STATE =
    URILocator.create("http://psi.ontopia.net/db2tm/synchronization-state");
    
  private Processor() {
  }
  
  private static TopicMapIF doCommit(TopicMapIF topicmap) throws IOException {
    TopicMapStoreIF store = topicmap.getStore();
    TopicMapReferenceIF reference = store.getReference();
    store.commit();
    if (!(store instanceof InMemoryTopicMapStore)) {
      store.close();
      store = reference.createStore(false);
    } //never forget!
    topicmap = store.getTopicMap();

    return topicmap;
  }

  private static String commitModeToString(int usedCommitMode, int usedCommitCount) {
    if (usedCommitMode == RELATIONAL_COMMIT_MODE) {
      return "relational";
    }
    if (usedCommitMode == TUPLE_COMMIT_MODE) {
      return "tuple";
    }
    if (usedCommitMode == COUNT_COMMIT_MODE) {
      return "count (" + usedCommitCount + ")";
    }
    return "unknown";
  }

  /**
   * INTERNAL: Runs a DB2TM process by adding tuples to the topic map.
   */
  public static void addRelations(RelationMapping rmapping, Collection<String> relnames, TopicMapIF topicmap, LocatorIF baseloc) {
    int ttuples = 0;
    long tstime = System.currentTimeMillis();
    Context ctx = new Context();
    if (log.isInfoEnabled()) {
      log.info("Adding relations: {}", new Date());
    }

    try {
      // verify relation mapping
      Map<DataSourceIF, Collection<Relation>> ds_relations = Utils.verifyRelationsForMapping(rmapping);
      
      // set up context object
      ctx.setMapping(rmapping);
      ctx.setTopicMap(topicmap);

      int topLevelCommitMode = NEVER_COMMIT_MODE; // default
      int topLevelCommitCount = -1;

      // figure out the commit mode
      String cm = rmapping.getCommitMode();
      if (cm != null) {
        if ("relation".equals(cm)) {
          topLevelCommitMode = RELATIONAL_COMMIT_MODE;
        }
        if ("tuple".equals(cm)) {
          topLevelCommitMode = TUPLE_COMMIT_MODE;
        }
        if (cm.startsWith("count:")) {
          topLevelCommitMode = COUNT_COMMIT_MODE;
          topLevelCommitCount = Integer.parseInt(cm.substring(6));
        }
      }

      if (baseloc != null) {
        ctx.setBaseLocator(baseloc);
      } else {
        log.info("No base locator specified, so using base of topic maps store.");
        ctx.setBaseLocator(topicmap.getStore().getBaseAddress());
      }
      
      // loop over datasources
      for (DataSourceIF datasource : ds_relations.keySet()) {
        log.debug("Adding tuples from data source {}", datasource);
      
        // loop over relations
        for (Relation relation : ds_relations.get(datasource)) {
          String relationName = relation.getName();

          // do not process non-listed relations
          if (relnames != null && !relnames.contains(relationName)) {
            log.debug("  ignoring relation: {}", relationName);
            continue;
          } else {
            log.info("  adding relation: {}", relationName);
          }
      
          int rtuples = 0;
          long rstime1 = System.currentTimeMillis();
          long rstime2 = 0;
      
          // set current relation
          ctx.setRelation(relation);
      
          // figure out commit mode for this relation
          int usedCommitMode = NEVER_COMMIT_MODE; // default
          int usedCommitCount = -1;
          cm = relation.getCommitMode();
          if (cm == null) {
            usedCommitMode = topLevelCommitMode;
            usedCommitCount = topLevelCommitCount;
          } else {
            if ("relation".equals(cm)) {
              usedCommitMode = RELATIONAL_COMMIT_MODE;
            }
            if ("tuple".equals(cm)) {
              usedCommitMode = TUPLE_COMMIT_MODE;
            }
            if (cm.startsWith("count:")) {
              usedCommitMode = COUNT_COMMIT_MODE;
              usedCommitCount = Integer.parseInt(cm.substring(6));
            }
          }

          // changelog synchronization; set start order values
            for (Changelog sync : relation.getSyncs()) {
              String maxOrderValue = datasource.getMaxOrderValue(sync);
              log.debug("New order value: {}={}", sync.getTable(), maxOrderValue);
              setStartOrder(sync, ctx, maxOrderValue);
            }

          if (usedCommitMode > NEVER_COMMIT_MODE) {
            log.info("  using commit mode: {}", commitModeToString(usedCommitMode, usedCommitCount));
          }
          
          // loop over tuples        
          TupleReaderIF reader = datasource.getReader(relationName);
          String [] tuple = null;
          while ((tuple = reader.readNext()) != null) {
            // process individual tuple
            long time = System.currentTimeMillis();
            // FIXME: we could change to updateTuple here with no ill effects,
            // except possibly a performance hit. still debating whether to do
            // that.
            addTuple(relation, tuple, ctx);
            rstime2 += (System.currentTimeMillis()-time);
            rtuples++;
            if (usedCommitMode == TUPLE_COMMIT_MODE) {
              topicmap = doCommit(topicmap);
              ctx.setTopicMap(topicmap);
            }

            if ((usedCommitMode == COUNT_COMMIT_MODE) && (rtuples % usedCommitCount == 0)) {
              topicmap = doCommit(topicmap);
              ctx.setTopicMap(topicmap);
              log.info("    committed after {} tuples ", rtuples);
            }
          }

          // commit if needed
          if ((usedCommitMode == RELATIONAL_COMMIT_MODE) || (usedCommitMode == COUNT_COMMIT_MODE)) {
            topicmap = doCommit(topicmap);
            ctx.setTopicMap(topicmap);
          }

          log.info("    Added {} tuples from {}, {}/{} ms",
                   new Object[] {rtuples, relationName, (System.currentTimeMillis()-rstime1), rstime2});
          ttuples += rtuples;
          reader.close();
        }
      }
    } catch (Exception e) {
      // unwrap so we can find the real exception
      String msg = e.getMessage();
      if (e instanceof OntopiaRuntimeException &&
          e.getCause() instanceof Exception) {
        e = (Exception) e.getCause();
      }
      
      if (e instanceof DB2TMException) {
        // don't wrap if it's already a DB2TMException, because this causes
        // the cmd-line tool to hide the real error
        throw (DB2TMException) e;
      } else {
        throw new DB2TMException(msg == null ? "Error occurred in addRelations call." : msg, e);
      }
    } finally {
      ctx.close();
    }
    if (log.isInfoEnabled()) {
      log.info("done adding relations: {} tuples, {} ms. {}", new Object[] {ttuples, (System.currentTimeMillis()-tstime), new Date()});
    }
  }

  /**
   * INTERNAL: Runs a DB2TM process by removing tuples from the topic map.
   */
  public static void removeRelations(RelationMapping rmapping, Collection<String> relnames, TopicMapIF topicmap, LocatorIF baseloc) {
    int ttuples = 0;
    long tstime = System.currentTimeMillis();
    Context ctx = new Context();
    if (log.isInfoEnabled()) {
      log.info("Removing relations: {}", new Date());
    }

    try {
      // verify relation mapping
      Map<DataSourceIF, Collection<Relation>> ds_relations = Utils.verifyRelationsForMapping(rmapping);
      
      // set up context object
      ctx.setMapping(rmapping);
      ctx.setTopicMap(topicmap);
      if (baseloc != null) {
        ctx.setBaseLocator(baseloc);
      } else {
        log.info("No base locator specified, so using base of topic maps store.");
        ctx.setBaseLocator(topicmap.getStore().getBaseAddress());
      }
      
      // loop over datasources
      for (DataSourceIF datasource : ds_relations.keySet()) {
        log.debug("Removing tuples from data source: {}", datasource);
      
        // loop over relations
        for (Relation relation : ds_relations.get(datasource)) {
          String relationName = relation.getName();
      
          // do not process non-listed relations
          if (relnames != null && !relnames.contains(relationName)) {
            log.debug("  ignoring relation: {}", relationName);
            continue;
          } else {
            log.debug("  removing relation: {}", relationName);
          }
      
          int rtuples = 0;
          long rstime1 = System.currentTimeMillis();
          long rstime2 = 0;
      
          // set current relation
          ctx.setRelation(relation);
          
          // loop over tuples        
          TupleReaderIF reader = datasource.getReader(relationName);
          
          String [] tuple = null;
          while ((tuple = reader.readNext()) != null) {
            // process individual tuple
            long time = System.currentTimeMillis();
            removeTuple(relation, tuple, ctx);
            rstime2 += (System.currentTimeMillis()-time);
            rtuples++;
          }
          log.info("    Removed {} tuples from {}, {}/{} ms",
                   new Object[] {rtuples, relationName, (System.currentTimeMillis()-rstime1), rstime2});
          ttuples += rtuples;
        }
      }
    } catch (Exception e) {
      throw new DB2TMException("Error occurred in removeRelations call.", e);
    } finally {
      ctx.close();
    }
    if (log.isInfoEnabled()) {
      log.info("done removing relations: {} tuples, {} ms. {}", new Object[] {ttuples, (System.currentTimeMillis()-tstime), new Date()});
    }
  }
  
  public static void addTuple(Relation relation, String[] tuple, Context ctx) {
    if (log.isDebugEnabled()) {
      log.debug("    a({}),{}", StringUtils.join(tuple, "|"), tuple.length);
    }
    
    List<Entity> entities = relation.getEntities();
    for (int i=0; i < entities.size(); i++) {
      Entity entity = entities.get(i);
      try {
        Object o = addEntity(relation, entity, tuple, ctx);
        ctx.setEntityObject(i, o);
      } catch (Exception e) {
        throw new DB2TMException("Error occurred while adding tuple " + Arrays.asList(tuple) + " from relation " + relation.getName() + " to entity " + entity, e);
      }
    }
  }

  protected static Object addEntity(Relation relation, Entity entity, String[] tuple, Context ctx) {
    // check condition before proceeding
    if (!checkCondition(relation, entity, tuple, ctx)) {
      return null;
    }
    
    TopicIF topic = null;
    
    if (entity.requiresTopic()) {
      // look up or create topic given identities
      topic = addIdentities(topic, relation, entity, tuple, ctx);

      // NOTE: if the topic is null at this point none of the identity
      // locators can be created
      
      // do nothing more not if entity is not primary
      if (topic == null) {
        if (!entity.isPrimary()) {
          return null;
        } else if (entity.getEntityType() == Entity.TYPE_TOPIC) {
          throw new DB2TMInputException("Not able to find topic for primary entity. None of the identity fields could be used.", entity, tuple);
        } else if (entity.getEntityType() == Entity.TYPE_ASSOCIATION) {
          // create new topic if not found
          topic = ctx.getBuilder().makeTopic();
          ctx.registerNewObject(topic);
        }
      }
      
      // add topic types
      if (entity.getEntityType() == Entity.TYPE_TOPIC) {
        // NOTE: association reifiers cannot have types
        addTypes(topic, entity.getTypes(), entity, tuple, ctx);
      }
        
      // add characteristics
      List<Field> cfields = entity.getCharacteristicFields();
      for (int i=0; i < cfields.size(); i++) {
       Field field = cfields.get(i);
          
       switch (field.getFieldType()) {
       case Field.TYPE_TOPIC_NAME:
         addTopicName(topic, relation, entity, field, i, tuple, ctx);
         break;
       case Field.TYPE_OCCURRENCE:
         addOccurrence(topic, relation, entity, field, i, tuple, ctx);
         break;
       case Field.TYPE_PLAYER:
         addPlayer(topic, relation, entity, field, i, tuple, ctx);
         break;
       default:
         throw new DB2TMConfigException("Illegal characteristic field type: " + field);
       }
      }
    }

    if (entity.getEntityType() == Entity.TYPE_ASSOCIATION) {
      // create association
      return addAssociation(topic, relation, entity, tuple, ctx);

    } else {
      return topic;
    }
  }

  protected static boolean checkCondition(Relation relation, Entity entity, String[] tuple, Context ctx) {
    ValueIF condition = entity.getConditionValue();    
    return (condition == null || condition.getValue(tuple) != null);
  }
  
  protected static AssociationIF addAssociation(TopicIF reifier, Relation relation, Entity entity, 
                                                String[] tuple, Context ctx) {
    
    // roles in association
    List<Field> rfields = entity.getRoleFields();
    int rlen = rfields.size();
    
    // only create association when all mandatory players actually exist
    TopicIF[] rtypes = new TopicIF[rlen];
    TopicIF[] players = new TopicIF[rlen];
    for (int i=0; i < rlen; i++) {
      Field role = rfields.get(i);
      players[i] = Utils.getTopic(role.getPlayer(), ctx);
      // if player is null then we'll do nothing
      if (players[i] == null) {
        switch (role.getOptional()) {
        case Field.OPTIONAL_FALSE:
          return null;
        case Field.OPTIONAL_TRUE:
          continue;
        case Field.OPTIONAL_DEFAULT:
          if (rlen > 2) {
            continue;
        } else {
            return null;
        }
        }
      }
      // get role type
      rtypes[i] = Utils.getTopic(role.getRoleType(), ctx);
      if (rtypes[i] == null) {
        throw new DB2TMInputException("Role type not found", entity, tuple, role.getRoleType());
      }
    }
    
    // find association      
    AssociationIF assoc = findAssociationByIdentities(relation, entity, tuple, ctx);

    // get association type
    TopicIF atype = Utils.getTopic(entity.getAssociationType(), ctx);
    if (atype == null) {
      throw new DB2TMInputException("Association type not found", entity, tuple, entity.getAssociationType());
    }
    
    if (assoc == null) {    
      // create association
      assoc = ctx.getBuilder().makeAssociation(atype);
      log.trace("      +A {} {}", assoc, atype);
    
      // add roles
      int arity = 0;
      for (int i=0; i < rlen; i++) {
        if (players[i] != null) {
          arity++;
          log.trace("      +R {} :{}", players[i], rtypes[i]);
          ctx.getBuilder().makeAssociationRole(assoc, rtypes[i], players[i]);
          if (arity == 1) {
            ctx.characteristicsChanged(players[i]);
          }
        }
      }
      
      // add scope
      addScope(assoc, entity.getScope(), entity, tuple, ctx);
    
      
    } else {
      // reuse association      
      log.trace("      =A {}", assoc);
      assoc.setType(atype);

      List<AssociationRoleIF> oroles = new ArrayList<AssociationRoleIF>(assoc.getRoles());
      for (int i=0; i < rlen; i++) {
        AssociationRoleIF or = extractRoleOfType(oroles, rtypes[i]);
        if (or != null) {
          if (!Objects.equals(or.getPlayer(), players[i])) {
            or.setPlayer(players[i]);
          }
          log.trace("      =R {} :{}", players[i], rtypes[i]);
        } else {
          log.trace("      +R {} :{}", players[i], rtypes[i]);
          ctx.getBuilder().makeAssociationRole(assoc, rtypes[i], players[i]);
        }
        if (i == 1) {
          ctx.characteristicsChanged(players[i]);
        }
      }
      if (!oroles.isEmpty()) {
        for (int i=0; i < oroles.size(); i++) {
          AssociationRoleIF or = oroles.get(i);
          TopicIF player = or.getPlayer();
          log.trace("      -R {} :{}", player, or.getType());
          or.remove();
          if (player != null) {
            ctx.characteristicsChanged(player);
          }
        }
      }
    
      // replace scope
      updateScope(assoc, entity.getScope(), entity, tuple, ctx);      
    }
    
    // add missing identities
    addIdentities(assoc, relation, entity, tuple, ctx);

    // if reifier, handle reification
    if (reifier != null) {
      assoc.setReifier(reifier);
    }
    return assoc;
  }

  public static void removeTuple(Relation relation, String[] tuple, Context ctx) {
    if (log.isDebugEnabled()) {
      log.trace("    r({}),{}", StringUtils.join(tuple, "|"), tuple.length);
    }

    List<Entity> entities = relation.getEntities();
    
    // first find entity objects with ids (used to look up other
    // characteristics)
    for (int i=0; i < entities.size(); i++) {
      Entity entity = entities.get(i);
      Object o = findTopicByIdentities(relation, entity, tuple, ctx);
      ctx.setEntityObject(i, o);
    }
    // then try to remove each of them (note: reverse order)
    for (int i=entities.size()-1; i >=0; i--) {
      Entity entity = entities.get(i);
      try {
        removeEntity(relation, entity, tuple, ctx);
      } catch (Exception e) {
        throw new DB2TMException("Error occurred while removing tuple " + Arrays.asList(tuple) + " from relation " + relation.getName() + " to entity " + entity, e);
      }
    }
  }

  protected static void removeEntity(Relation relation, Entity entity, String[] tuple, Context ctx) {
    // find candidate topic
    TopicIF topic = null;
    if (entity.requiresTopic()) {
      topic = findTopicByIdentities(relation, entity, tuple, ctx);
    }
    
    if (entity.getEntityType() == Entity.TYPE_TOPIC) {
      // remove topic
      if (topic != null) {
        removeTopic(topic, relation, entity, tuple, ctx);
      }
      
    } else if (entity.getEntityType() == Entity.TYPE_ASSOCIATION) {
      if (topic != null) {
        // if reifier topic found, then use that topic to find association instance
        TMObjectIF reified = topic.getReified();
        if (reified instanceof AssociationIF) {
          // remove association
          AssociationIF assoc = (AssociationIF)reified;
          log.trace("      -A-reified {} -> {} {}", new Object[] {topic, assoc, assoc.getType()});
          assoc.remove();
        }
        // remove reifier topic
        removeTopic(topic, relation, entity, tuple, ctx);
      } else {
        // find association 
        removeAssociation(relation, entity, tuple, ctx);
      }
    }
  }

  protected static void removeTopic(TopicIF topic, Relation relation, Entity entity, String[] tuple, Context ctx) {
    // if entity is primary; delete topic
    if (entity.isPrimary()) {
      // delete topic (and identities)
      deleteTopic(topic);
    } else {
      log.debug("      >T {}", topic);

      // TODO: reject if non-primary entity and relation.cardinality > 1 and field is dynamic
      // CONSTRAINT: primary entity cannot occur in multiple rows if changelog
    
      // remove characteristics, but not identities
      for (Field field : entity.getCharacteristicFields()) {
        switch (field.getFieldType()) {
        case Field.TYPE_TOPIC_NAME: {
          List<TopicNameIF> names = getTopicNames(topic, relation, entity, field, tuple, ctx);
          for (int i=0; i < names.size(); i++) {
            TopicNameIF _bn = names.get(i);
            log.trace("      -N {} {}", topic, _bn);
            _bn.remove();
          }
          //! removeTopicName(topic, relation, entity, field, tuple, ctx);
          break;
        } case Field.TYPE_OCCURRENCE: {
          List<OccurrenceIF> occs = getOccurrences(topic, relation, entity, field, tuple, ctx);
          for (int i=0; i < occs.size(); i++) {
            OccurrenceIF _occ = occs.get(i);
            log.trace("      -O {} {}", topic, _occ);
            _occ.remove();
          }
          //! removeOccurrence(topic, relation, entity, field, tuple, ctx);
          break;
        } case Field.TYPE_PLAYER: {
          List<AssociationRoleIF> roles = getPlayers(topic, relation, entity, field, tuple, ctx);
          for (int i=0; i < roles.size(); i++) {
            AssociationRoleIF role = roles.get(i);
            AssociationIF assoc = role.getAssociation();
            log.trace("      -P {} {}", assoc, assoc.getType());
            assoc.remove();
          }
          //! removePlayer(topic, relation, entity, field, tuple, ctx);
          break;
        } default:
          throw new DB2TMConfigException("Illegal characteristic field type: " + field);
        }
      }
      // remove types
      removeTypes(topic, entity.getTypes(), ctx);          
    }
  }

  protected static void deleteTopic(TopicIF topic) {
    // first remove all topics that reifies any of the topic's associations
    for (AssociationRoleIF role : topic.getRoles()) {
      AssociationIF assoc = role.getAssociation();
      // if reifier topic found, then remove it
      TopicIF reifier = assoc.getReifier();
      if (reifier != null) {
        // remove reifier topic
        log.trace("      -A-reifier {} {} -> {}", new Object[] {topic, reifier, assoc});
        reifier.remove();
      }
    }
    // remove topic (and identities)
    log.debug("      -T {}", topic);
    topic.remove();
  }
  
  protected static TopicIF findTopicByIdentities(Relation relation, Entity entity, String[] tuple, Context ctx) {
    for (Field field : entity.getIdentityFields()) {
      TopicIF topic = findTopicByIdentity(relation, entity, field, tuple, ctx);
      if (topic != null) {
        return topic;
      }
    }
    return null;
  }
  
  private static TopicIF findTopicByIdentity(Relation relation, Entity entity, Field field, String[] tuple, Context ctx) {
    switch (field.getFieldType()) {
    case Field.TYPE_SUBJECT_LOCATOR: {
      LocatorIF loc = Utils.getLocator(relation, entity, field, tuple, ctx);
      if (loc == null) {
        return null;
      }
      return ctx.getTopicMap().getTopicBySubjectLocator(loc);
    }
    case Field.TYPE_SUBJECT_IDENTIFIER: {
      LocatorIF loc = Utils.getLocator(relation, entity, field, tuple, ctx);
      if (loc == null) {
        return null;
      }
      return ctx.getTopicMap().getTopicBySubjectIdentifier(loc);
    }
    case Field.TYPE_ITEM_IDENTIFIER: {
      // note: do not look up topics by item identifier if entity type is association
      if (entity.getEntityType() == Entity.TYPE_ASSOCIATION) {
        return null;
      }
      LocatorIF loc = Utils.getLocator(relation, entity, field, tuple, ctx);
      if (loc == null) {
        return null;
      }
      TMObjectIF tmobject = ctx.getTopicMap().getObjectByItemIdentifier(loc);
      if (tmobject instanceof TopicIF) {
        return (TopicIF)tmobject;
      } else {
        if (tmobject != null) {
          log.warn("Item identifier lookup returned non-topic: {} -> {}", loc, tmobject);
        }
        return null;
      }
    } default:
      throw new DB2TMConfigException("Illegal identity field type: " + field);
    }
  }
  
  protected static AssociationIF findAssociationByIdentities(Relation relation, Entity entity, String[] tuple, Context ctx) {
    // look up association object by item identifier
    for (Field field : entity.getIdentityFields()) {
      // associations can only have item identifiers
      if (field.getFieldType() == Field.TYPE_ITEM_IDENTIFIER) {
        LocatorIF loc = Utils.getLocator(relation, entity, field, tuple, ctx);
        TMObjectIF tmobject = ctx.getTopicMap().getObjectByItemIdentifier(loc);
        if (tmobject instanceof AssociationIF) {
          return (AssociationIF)tmobject;
        } else if (tmobject != null) {
          log.warn("Item identifier lookup returned non-association: {} -> {}", loc, tmobject);
        }
      }
    }
    return null;
  }

  protected static TopicIF addIdentities(TopicIF topic, Relation relation,
                                         Entity entity,
                                         String[] tuple, Context ctx) {
    // look up topic and/or add identities
    TopicMapIF tm = ctx.getTopicMap();

    // Note: topic will be created only if entity is primary
    for (Field field : entity.getIdentityFields()) {
      TopicIF found = null;
      LocatorIF loc = null;

      switch (field.getFieldType()) {
      case Field.TYPE_SUBJECT_LOCATOR:
        loc = Utils.getLocator(relation, entity, field, tuple, ctx);
        if (loc == null) {
          continue;
      }
        found = tm.getTopicBySubjectLocator(loc);

        if (found != null) {
          if (topic != null) {
            if (!found.equals(topic)) {
              ctx.mergeTopics(topic, found);
            }
          } else {
            topic = found;
          }
        } else {
          if (topic == null) {
            topic = ctx.getBuilder().makeTopic();
            ctx.registerNewObject(topic);
          }
          topic.addSubjectLocator(loc);
        }
        break;
      case Field.TYPE_SUBJECT_IDENTIFIER:
        loc = Utils.getLocator(relation, entity, field, tuple, ctx);
        if (loc == null) {
          continue;
      }
        found = tm.getTopicBySubjectIdentifier(loc);

        if (found != null) {
          if (topic != null) {
            if (!found.equals(topic)) {
              ctx.mergeTopics(topic, found);
            }
          } else {
            topic = found;
          }
        } else {
          if (topic == null) {
            topic = ctx.getBuilder().makeTopic();
            ctx.registerNewObject(topic);
          }
          topic.addSubjectIdentifier(loc);
        }
        break;
      case Field.TYPE_ITEM_IDENTIFIER:
        // note: add item identifier iff entity type is topic
        if (entity.getEntityType() == Entity.TYPE_TOPIC) {
          loc = Utils.getLocator(relation, entity, field, tuple, ctx);
          if (loc == null) {
            continue;
          }
          found = (TopicIF) tm.getObjectByItemIdentifier(loc);
          
          if (found != null) {
            if (topic != null) {
              if (!found.equals(topic)) {
                ctx.mergeTopics(topic, found);
              }
            } else {
              topic = found;
            }
          } else {
            if (topic == null) {
              topic = ctx.getBuilder().makeTopic();
              ctx.registerNewObject(topic);
            }
            topic.addItemIdentifier(loc);
          }
        }
        break;
      default:
        throw new DB2TMConfigException("Illegal identity field type: " + field);
      }
    }
    return topic;
  }

  protected static TopicIF updateIdentities(TopicIF topic, Relation relation, Entity entity,
                                            String[] tuple, Context ctx) {
    // FIXME: do we want to just blindly update identities like this?
    return addIdentities(topic, relation, entity, tuple, ctx);
  }
  
  protected static void addIdentities(AssociationIF assoc, Relation relation, Entity entity,
                                      String[] tuple, Context ctx) {
    Objects.requireNonNull(assoc, "Cannot add identities to null association.");
    for (Field field : entity.getIdentityFields()) {
      if (field.getFieldType() == Field.TYPE_ITEM_IDENTIFIER) {
        LocatorIF loc = Utils.getLocator(relation, entity, field, tuple, ctx);
        if (loc == null) {
          continue;
        }        
        // note: at this point we should know that there are no other objects with the same identity
        assoc.addItemIdentifier(loc);
      }      
    }
  }
  
  protected static void addTypes(TopicIF topic, String[] types, Entity entity, String[] tuple, Context ctx) {
    for (int i = 0; i < types.length; i++) {
      TopicIF type = Utils.getTopic(types[i], ctx);
      if (type != null) {
        topic.addType(type);
      } else {
        throw new DB2TMInputException("Topic type not found", entity, tuple, types[i]);
      }
    }
  }

  protected static void updateTypes(TopicIF topic, String[] types, Entity entity, String[] tuple, Context ctx) {
    // this is a bit convoluted because we don't want to change anything
    // unless the set of types has actually changed

    // tracking old types with this
    Set<TopicIF> oldtypes = new CompactHashSet<TopicIF>(topic.getTypes());

    // loop over new list of types
    for (int i = 0; i < types.length; i++) {
      TopicIF type = Utils.getTopic(types[i], ctx);
      if (type == null) {
        throw new DB2TMInputException("Topic type not found", entity, tuple,
                types[i]);
      }

      if (oldtypes.contains(type)) {
        oldtypes.remove(type);
      } else {
        topic.addType(type);
      }
    }

    // any old types that still remain need to be removed
    for (TopicIF oldtype : oldtypes) {
      topic.removeType(oldtype);
    }
  }
  
  protected static void removeTypes(TopicIF topic, String[] types, Context ctx) {
    for (int i = 0; i < types.length; i++) {
      TopicIF type = Utils.getTopic(types[i], ctx);
      if (type != null) {
        topic.removeType(type);
      }
    }
  }

  protected static void addScope(ScopedIF scoped, String[] scope, Entity entity, String[] tuple, Context ctx) {
    // TODO: should really remove any existing scope
    for (int i = 0; i < scope.length; i++) {
      TopicIF theme = Utils.getTopic(scope[i], ctx);
      if (theme != null) {
        scoped.addTheme(theme);
      } else {
        throw new DB2TMInputException("Scoping topic not found", entity, tuple, scope[i]);
      }
    }
  }

  protected static void updateScope(ScopedIF scoped, String[] scope, Entity entity, String[] tuple, Context ctx) {
    // clear existing scope
    Collection<TopicIF> _scope = scoped.getScope();
    if (!_scope.isEmpty()) {
      TopicIF[] themes = _scope.toArray(new TopicIF[0]);
      for (int i=0; i < themes.length; i++) {
        scoped.removeTheme(themes[i]);
      }
    }
    // add new scoping topics
    addScope(scoped, scope, entity, tuple, ctx);
  }
  
  protected static boolean compareScope(String[] scope1, Collection<TopicIF> scope2, Entity entity, String[] tuple, Context ctx) {
    if (scope1.length != scope2.size()) {
      return false; // ISSUE: what if scope attribute contains duplicates?
    }
    for (int i=0; i < scope1.length; i++) {
      TopicIF theme = Utils.getTopic(scope1[i], ctx);
      if (theme == null) {
        throw new DB2TMInputException("Scoping topic not found", entity, tuple, scope1[i]);
      }
      if (!scope2.contains(theme)) {
        return false;
      }
    }
    return true;
  }

  protected static void addTopicName(TopicIF topic, Relation relation,
                                     Entity entity, Field field, int fieldIndex,
                                     String[] tuple, Context ctx) {
    String value = Utils.getValue(relation, entity, field, tuple, ctx);
    if (!Utils.isValueEmpty(value)) {
      TopicIF type = Utils.getTopic(field.getType(), ctx);
      if (type == null && field.getType() != null) {
        throw new DB2TMInputException("Name type not found", entity, tuple, field.getType());
      }
      
      TopicNameIF bn = (TopicNameIF)ctx.reuseOldFieldValue(topic, fieldIndex);
      if (bn == null) {
        bn = ctx.getBuilder().makeTopicName(topic, type, value);
        addScope(bn, field.getScope(), entity, tuple, ctx);
        log.trace("      +N {} {}", topic, bn);
      } else {
        if (!bn.getValue().equals(value)) {
          bn.setValue(value);
          log.trace("      =N {} {}", topic, bn);
        }
      }
      // notify context
      ctx.characteristicsChanged(topic);
    }
  }

  protected static List<TopicNameIF> getTopicNames(TopicIF topic, Relation relation,
                                      Entity entity, Field field,
                                      String[] tuple, Context ctx) {
    TopicIF type = Utils.getTopic(field.getType(), ctx);
    if (type == null) {
      if (field.getType() != null) {
        throw new DB2TMInputException("Name type not found", entity, tuple,
                field.getType());
      }
      // this means the type is the default name type. sync of this will fail
      // because null != defaultnametype.
      type = getDefaultNameType(ctx);
    }
    
    // loop over names and update
    List<TopicNameIF> result = new ArrayList<TopicNameIF>();
    Collection<TopicNameIF> bns = topic.getTopicNames();
    if (!bns.isEmpty()) {
      TopicNameIF[] ba = bns.toArray(new TopicNameIF[0]);
      for (int i=0; i < ba.length; i++) {
        TopicNameIF _bn = ba[i];
        // check type
        TopicIF _type = _bn.getType();
        if (!Objects.equals(_type, type)) {
          continue;
        }
        // check scope
        if (!compareScope(field.getScope(), _bn.getScope(), entity, tuple, ctx)) {
          continue;
        }
        result.add(_bn);          
      }
    }
    return result;
  }

  private static TopicIF getDefaultNameType(Context ctx) {
    // if the default topic name type doesn't exist this will return nothing.
    // that's OK, because in that case there will be no existing names with
    // this type, either.
    return ctx.getTopicMap().getTopicBySubjectIdentifier(PSI.getSAMNameType());
  }

  protected static void removeTopicName(TopicIF topic, Relation relation, Entity entity, Field field,
                                        String[] tuple, Context ctx) {
    String value = Utils.getValue(relation, entity, field, tuple, ctx);
    TopicIF type = Utils.getTopic(field.getType(), ctx);
    if (type == null) {
      if (field.getType() != null) {
        throw new DB2TMInputException("Name type not found", entity, tuple,
                                      field.getType());
      }
      // this means the type is the default name type. remove will fail
      // because null != defaultnametype.
      type = getDefaultNameType(ctx);
    }

    // loop over names and remove first matching
    Iterator<TopicNameIF> iter = topic.getTopicNames().iterator();
    while (iter.hasNext()) {
      TopicNameIF _bn = iter.next();
      // check value
      if (!Objects.equals(_bn.getValue(), value)) {
        continue;
      }
      // check type
      if (!Objects.equals(_bn.getType(), type)) {
        continue;
      }
      // check scope
      if (!compareScope(field.getScope(), _bn.getScope(), entity, tuple, ctx)) {
        continue;
      }

      log.trace("      -N {} {}", topic, _bn);
      // remove matching name
      _bn.remove();
      // notify context
      ctx.characteristicsChanged(topic);
      break;
    }
  }

  protected static void addOccurrence(TopicIF topic, Relation relation,
                                      Entity entity, Field field, int fieldIndex,
                                      String[] tuple, Context ctx) {
    String value = Utils.getValue(relation, entity, field, tuple, ctx);
    if (!Utils.isValueEmpty(value)) {

      TopicIF type = Utils.getTopic(field.getType(), ctx);
      if (type == null) {
        throw new DB2TMInputException("Occurrence type not found", entity, tuple, field.getType());
      }

      String occvalue = value;
      LocatorIF occDatatype = DataTypes.TYPE_STRING;
      if (field.getDatatype() != null) {
        String datatype = Utils.expandPrefixedValue(field.getDatatype(), ctx);
        if (datatype.equals(DataTypes.TYPE_URI)) {
          occvalue = ctx.getBaseLocator().resolveAbsolute(value).getAddress();
          occDatatype = DataTypes.TYPE_URI;
        } else {
          occDatatype = URILocator.create(datatype);
        }
      }
      
      OccurrenceIF oc = (OccurrenceIF)ctx.reuseOldFieldValue(topic, fieldIndex);
      if (oc == null) {
        // FIXME: rewrite so that we can set occurrence value directly
        oc = ctx.getBuilder().makeOccurrence(topic, type, occvalue, occDatatype); 
        addScope(oc, field.getScope(), entity, tuple, ctx);
        log.trace("      +O {} {}", topic, oc);
      } else {
        if (!oc.getValue().equals(occvalue) ||
            !oc.getDataType().equals(occDatatype)) {
          oc.setValue(occvalue, occDatatype);
          log.trace("      =O {} {}", topic, oc);
        }
      }
      // notify context
      ctx.characteristicsChanged(topic);
    }
  }

  protected static List<OccurrenceIF> getOccurrences(TopicIF topic, Relation relation, Entity entity, Field field,
                                       String[] tuple, Context ctx) {
    TopicIF type = Utils.getTopic(field.getType(), ctx);
    if (type == null) {
      throw new DB2TMInputException("Occurrence type not found", entity, tuple, field.getType());
    }
    
    //! String datatype = (field.getDatatype() == null ? null : Utils.expandPrefixedValue(field.getDatatype(), ctx));    

    // loop over occurrences and clear
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Collection<OccurrenceIF> occs = topic.getOccurrences();
    if (!occs.isEmpty()) {
      OccurrenceIF[] oa = occs.toArray(new OccurrenceIF[0]);
      for (int i=0; i < oa.length; i++) {
        OccurrenceIF _occ = oa[i];
        // check type
        if (!Objects.equals(_occ.getType(), type)) {
          continue;
        }

				// FIXME: compare datatype?

        // check scope
        if (!compareScope(field.getScope(), _occ.getScope(), entity, tuple, ctx)) {
          continue;
        }
        result.add(_occ);                  
      }
    }
    return result;
  }
    
  protected static void removeOccurrence(TopicIF topic, Relation relation, Entity entity, Field field,
                                         String[] tuple, Context ctx) {
    
    String value = Utils.getValue(relation, entity, field, tuple, ctx);
    TopicIF type = Utils.getTopic(field.getType(), ctx);
    if (type == null) {
      throw new DB2TMInputException("Occurrence type not found", entity, tuple, field.getType());
    }

    //! String datatype = (field.getDatatype() == null ? null : Utils.expandPrefixedValue(field.getDatatype(), ctx));

    // loop over occurrences and remove first matching
    Iterator<OccurrenceIF> iter = topic.getOccurrences().iterator();
    while (iter.hasNext()) {
      OccurrenceIF _occ = iter.next();
      // check value or locator
			if (!Objects.equals(_occ.getValue(), value)) {
        continue;
      }

			// FIXME: compare datatype?

      // check type
      if (!Objects.equals(_occ.getType(), type)) {
        continue;
      }
      // check scope
      if (!compareScope(field.getScope(), _occ.getScope(), entity, tuple, ctx)) {
        continue;
      }

      log.trace("      -O {} {}", topic, _occ);
      // remove matching occurrence
      _occ.remove();
      // notify context
      ctx.characteristicsChanged(topic);
      break;
    }
  }

  protected static void addPlayer(TopicIF topic, Relation relation,
                                  Entity entity, Field field, int fieldIndex,
                                  String[] tuple, Context ctx) {
    
    // other roles in association
    List<Field> rfields = field.getOtherRoleFields();
    int rlen = rfields.size();

    // only create association when all mandatory players actually exist
    TopicIF[] rtypes = new TopicIF[rlen];
    TopicIF[] players = new TopicIF[rlen];
    for (int i=0; i < rlen; i++) {
      Field role = rfields.get(i);
      players[i] = Utils.getTopic(role.getPlayer(), ctx);

      // if player is null then we'll do nothing
      if (players[i] == null) {
        switch (role.getOptional()) {
        case Field.OPTIONAL_FALSE:
          return;
        case Field.OPTIONAL_TRUE:
          continue;
        case Field.OPTIONAL_DEFAULT:
          if (rlen > 2) {
            continue;
        } else {
            return;
        }
        }
      }
      // get role type
      rtypes[i] = Utils.getTopic(role.getRoleType(), ctx);
      if (rtypes[i] == null) {
        throw new DB2TMInputException("Role type not found", entity, tuple, role.getRoleType());
      }
    }

    AssociationRoleIF ar = (AssociationRoleIF)ctx.reuseOldFieldValue(topic, fieldIndex);
    if (ar == null) {
      // get association type
      TopicIF atype = Utils.getTopic(field.getAssociationType(), ctx);
      if (atype == null) {
        throw new DB2TMInputException("Association type not found", entity, tuple, entity.getAssociationType());
      }
      // get current role type
      TopicIF rtype = Utils.getTopic(field.getRoleType(), ctx);
      if (rtype == null) {
        throw new DB2TMInputException("Role type not found", entity, tuple, field.getRoleType());
      }
      
      // create association
      AssociationIF assoc = ctx.getBuilder().makeAssociation(atype);
      log.trace("      +P {} {}", assoc, atype);

      // add scope
      addScope(assoc, field.getScope(), entity, tuple, ctx);
      
      // add current role
      log.trace("      +R {} :{}", topic, rtype);
      ctx.getBuilder().makeAssociationRole(assoc, rtype, topic);
      
      // add other roles
      for (int i=0; i < rlen; i++) {
        // do not create role if player is null
        if (players[i] != null) {
          log.trace("      +R {} :{}", players[i], rtypes[i]);
          ctx.getBuilder().makeAssociationRole(assoc, rtypes[i], players[i]);
        } else {
          log.trace("      ?R {} :{}", players[i], rtypes[i]);          
        }
      }

    } else {
      // reuse association
      AssociationIF assoc = ar.getAssociation();
      log.trace("      =P {} {}", topic, assoc);

      List<AssociationRoleIF> oroles = new ArrayList<AssociationRoleIF>(assoc.getRoles());
      oroles.remove(ar);
      for (int i=0; i < rlen; i++) {
        AssociationRoleIF or = extractRoleOfType(oroles, rtypes[i]);
        if (or != null) {
          if (!Objects.equals(or.getPlayer(), players[i])) {
            or.setPlayer(players[i]);
          }
          log.trace("      =R {} :{}", players[i], rtypes[i]);
        } else {
          log.trace("      +R {} :{}", players[i], rtypes[i]);
          ctx.getBuilder().makeAssociationRole(assoc, rtypes[i], players[i]);
        }
      }
      if (!oroles.isEmpty()) {
        for (int i=0; i < oroles.size(); i++) {
          AssociationRoleIF or = oroles.get(i);
          TopicIF player = or.getPlayer();
          log.trace("      -R {} :{}", player, or.getType());
          or.remove();
          if (player != null) {
            ctx.characteristicsChanged(player);
          }
        }
      }      
    }
    // notify context
    ctx.characteristicsChanged(topic);
  }

  private static AssociationRoleIF extractRoleOfType(List<AssociationRoleIF> roles, TopicIF rtype) {
    int length = roles.size();
    for (int i=0; i < length; i++) {
      AssociationRoleIF r = roles.get(i);
      if (Objects.equals(rtype, r.getType())) {
        roles.remove(i);
        return r;
      }
    }
    return null;
  }
  
  protected static List<AssociationRoleIF> getPlayers(TopicIF topic, Relation relation, Entity entity, Field field,
                                   String[] tuple, Context ctx) {    
    TopicIF atype = Utils.getTopic(field.getAssociationType(), ctx);
    if (atype == null) {
      throw new DB2TMInputException("Association type not found", entity, tuple, field.getAssociationType());
    }
    TopicIF rtype_p = Utils.getTopic(field.getRoleType(), ctx);
    if (rtype_p == null) {
      throw new DB2TMInputException("Role type not found", entity, tuple, field.getRoleType());
    }

    // loop over roles and update
    List<AssociationRoleIF> result = new ArrayList<AssociationRoleIF>();
    Collection<AssociationRoleIF> troles = topic.getRoles();
    if (!troles.isEmpty()) {
      AssociationRoleIF[] ra = troles.toArray(new AssociationRoleIF[0]);
      Collection<Field> rfields = field.getOtherRoleFields();
      
      outer:
      for (int i=0; i < ra.length; i++) {
        AssociationRoleIF role = ra[i];
        // check role type
        if (!Objects.equals(role.getType(), rtype_p)) {
          continue;
        }
        // check association type
        AssociationIF assoc = role.getAssociation();
        if (!Objects.equals(assoc.getType(), atype)) {
          continue;
        }
        // check scope
        if (!compareScope(field.getScope(), assoc.getScope(), entity, tuple, ctx)) {
          continue;
        }
        // check association cardinality
        Collection<AssociationRoleIF> roles = assoc.getRoles();
        if (roles.size() != (rfields.size() + 1)) {
          continue;
        }
        for (AssociationRoleIF arole : roles) {
          if (arole.equals(role)) {
            continue;
          }
          TopicIF rtype = arole.getType();
          // check role
          Field matching_rfield = null;
          for (Field rfield : rfields) {
            TopicIF rtype_o = Utils.getTopic(rfield.getRoleType(), ctx);
            if (rtype_o == null) {
              throw new DB2TMInputException("Role type not found", entity, tuple, rfield.getRoleType());
            }
            // check role type
            if (!Objects.equals(rtype, rtype_o)) {
              continue;
            }
            // role field matched
            matching_rfield = rfield;
            break;
          }
          if (matching_rfield == null) {
            continue outer;
          }
        }
        result.add(role);
      }
    }
    return result;
  }
  
  protected static void removePlayer(TopicIF topic, Relation relation, Entity entity, Field field,
                                     String[] tuple, Context ctx) {
    
    TopicIF atype = Utils.getTopic(field.getAssociationType(), ctx);
    if (atype == null) {
      throw new DB2TMInputException("Association type not found", entity, tuple, field.getAssociationType());
    }
    TopicIF rtype_p = Utils.getTopic(field.getRoleType(), ctx);
    if (rtype_p == null) {
      throw new DB2TMInputException("Role type not found", entity, tuple, field.getRoleType());
    }

    Collection<Field> rfields = field.getOtherRoleFields();

    outer:
    for (AssociationRoleIF role : topic.getRoles()) {
      // check role type
      if (!Objects.equals(role.getType(), rtype_p)) {
        continue;
      }
      // check association type
      AssociationIF assoc = role.getAssociation();
      if (!Objects.equals(assoc.getType(), atype)) {
        continue;
      }
      // check scope
      if (!compareScope(field.getScope(), assoc.getScope(), entity, tuple, ctx)) {
        continue;
      }
      // check association cardinality
      Collection<AssociationRoleIF> roles = assoc.getRoles();
      if (roles.size() != (rfields.size() + 1)) {
        continue;
      }
      for (AssociationRoleIF arole : roles) {
        if (arole.equals(role)) {
          continue;
        }
        TopicIF rtype = arole.getType();
        TopicIF player = arole.getPlayer();
        // check role
        Field matching_rfield = null;
        for (Field rfield : rfields) {
          TopicIF rtype_o = Utils.getTopic(rfield.getRoleType(), ctx);
          if (rtype_o == null) {
            throw new DB2TMInputException("Role type not found", entity, tuple, rfield.getRoleType());
          }
          // check role type and player
          if (!Objects.equals(rtype, rtype_o) ||
              !Objects.equals(player, Utils.getTopic(rfield.getPlayer(), ctx))) {
            continue;
          }
          // role field matched
          matching_rfield = rfield;
          break;
        }
        if (matching_rfield == null) {
          continue outer;
        }
      }
      //! // if reifier topic found, then remove it (or its characteristics)
      //! TopicIF reifier = assoc.getReifier();
      //! if (reifier != null)
      //!   // remove reifier topic
      //!   reifier.remove();
      log.trace("      -P {} {}", assoc, atype);
      // remove association
      assoc.remove();
      // notify context
      ctx.characteristicsChanged(topic);
      break;
    }    
  }

  protected static void removeAssociation(Relation relation, Entity entity, String[] tuple, Context ctx) {

    // TODO: needs improvement. take optional roles into account
    
    // use first role fields as starting point
    List<Field> rfields = entity.getRoleFields();
    Field pfield = rfields.get(0);
    
    TopicIF atype = Utils.getTopic(entity.getAssociationType(), ctx);
    if (atype == null) {
      throw new DB2TMInputException("Association type not found", entity, tuple, entity.getAssociationType());
    }
    TopicIF rtype_p = Utils.getTopic(pfield.getRoleType(), ctx);
    if (rtype_p == null) {
      throw new DB2TMInputException("Role type not found", entity, tuple, pfield.getRoleType());
    }

    TopicIF topic = Utils.getTopic(pfield.getPlayer(), ctx);
    
    // if player topic is gone, then there won't be any matching associations either
    if (topic == null) {
      return;
    }
    
    outer:
    for (AssociationRoleIF role : topic.getRoles()) {
      // check role type
      if (!Objects.equals(role.getType(), rtype_p)) {
        continue;
      }
      // check association type
      AssociationIF assoc = role.getAssociation();
      if (!Objects.equals(assoc.getType(), atype)) {
        continue;
      }
      // check association cardinality
      Collection<AssociationRoleIF> roles = assoc.getRoles();
      if (roles.size() != rfields.size()) {
        continue;
      }
      for (AssociationRoleIF arole : roles) {
        if (arole.equals(role)) {
          continue;
        }
        TopicIF rtype = arole.getType();
        TopicIF player = arole.getPlayer();
        // check role
        Field matching_rfield = null;
        for (int i=0; i < rfields.size(); i++) {
          Field rfield = rfields.get(i);
          if (rfield.equals(pfield)) {
            continue;
          }
          TopicIF rtype_o = Utils.getTopic(rfield.getRoleType(), ctx);
          if (rtype_o == null) {
            throw new DB2TMInputException("Role type not found", entity, tuple, rfield.getRoleType());
          }
          // check role type and player
          if (!Objects.equals(rtype, rtype_o) ||
              !Objects.equals(player, Utils.getTopic(rfield.getPlayer(), ctx))) {
            continue;
          }
          // role field matched
          matching_rfield = rfield;
          break;
        }
        if (matching_rfield == null) {
          continue outer;
        }
      }
      // check scope
      if (!compareScope(entity.getScope(), assoc.getScope(), entity, tuple, ctx)) {
        continue;
      }

      // if reifier topic found, then remove it (or its characteristics)
      TopicIF reifier = assoc.getReifier();
      if (reifier != null) {
        removeTopic(reifier, relation, entity, tuple, ctx);
      }
      // remove association
      if (entity.isPrimary()) {
        log.trace("      -A {} {}", assoc, atype);
        assoc.remove();
      } else {
        log.trace("      >A {}", assoc);
      }
      break;
    }    
  }

  /**
   * INTERNAL: Runs a DB2TM process by synchronizing the relations.
   */
  public static void synchronizeRelations(RelationMapping rmapping,
                                          Collection<String> relnames,
                                          TopicMapIF topicmap,
                                          LocatorIF baseloc) {
    synchronizeRelations(rmapping, relnames, topicmap, baseloc, false);
  }
  
  public static void synchronizeRelations(RelationMapping rmapping,
                                          Collection<String> relnames,
                                          TopicMapIF topicmap,
                                          LocatorIF baseloc,
                                          boolean forceRescan) {
    int ttuples = 0;
    long tstime = System.currentTimeMillis();
    Context ctx = new Context();
    if (log.isInfoEnabled()) {
      log.info("Synchronizing relations: {}", new Date());
    }

    try {
      // verify relation mapping
      Map<DataSourceIF, Collection<Relation>> ds_relations = Utils.verifyRelationsForMapping(rmapping);
      
      // set up context object
      ctx.setMapping(rmapping);
      ctx.setTopicMap(topicmap);
      if (baseloc != null) {
        ctx.setBaseLocator(baseloc);
      } else {
        log.info("No base locator specified, so using base of topic maps store.");
        ctx.setBaseLocator(topicmap.getStore().getBaseAddress());
      }
      
      // loop over datasources
      for (DataSourceIF datasource : ds_relations.keySet()) {
        log.debug("Synchronizing relations in data source: {}", datasource);
      
        // loop over relations
        for (Relation relation : ds_relations.get(datasource)) {
          String relationName = relation.getName();
      
          // do not process non-listed relations
          if (relnames != null && !relnames.contains(relationName)) {
            log.debug("  ignoring relation: {}", relationName);
            continue;
          }
      
          // figure out what the synchronization type is
          int synctype = relation.getSynchronizationType();
      
          if (forceRescan) {
            synctype = Relation.SYNCHRONIZATION_RESCAN;
          }
      
          if (synctype == Relation.SYNCHRONIZATION_UNKNOWN) {
            if (!relation.getSyncs().isEmpty()) {
              synctype = Relation.SYNCHRONIZATION_CHANGELOG;
              log.debug("  defaulting synchronization type for relation {} to {}", relationName, synctype);
            } else {
              synctype = Relation.SYNCHRONIZATION_RESCAN;
              log.debug("  defaulting synchronization type for relation {} to {}", relationName, synctype);
            }
          }
          log.debug("  synchronizing relation: {} type: {} {} force: {}", 
                    new Object[] {relation.getName(), synctype, Relation.getSynchronizationTypeName(synctype), forceRescan});
          
          int rtuples = 0;
          long rstime1 = System.currentTimeMillis();
          long rstime2 = 0;
      
          // set current relation
          ctx.setRelation(relation);
            
          // synchronize relation if configured to do so
          if (synctype == Relation.SYNCHRONIZATION_CHANGELOG) {
            // changelog synchronization
              for (Changelog sync : relation.getSyncs()) {
                log.debug("  changelog, table {}", sync.getTable());
                
                // get start order from topic map
                String startOrder = getStartOrder(sync, ctx);
                String highestOrder = startOrder;
                log.debug("Old order value: {}={}", sync.getTable(), startOrder);
                ChangelogReaderIF reader = datasource.getChangelogReader(sync, startOrder);
                reader = new ChangelogReaderWrapper(reader, relation);
                
                try {
                  String[] tuple;
                  while ((tuple = reader.readNext()) != null) {
                    // process individual tuple
                    long time = System.currentTimeMillis();
                    
                    // track order value
                    String orderValue = reader.getOrderValue();                
                    if (highestOrder == null ||
                        highestOrder.compareTo(orderValue) < 0) {
                      highestOrder = orderValue;
                    }

                    if (reader.getChangeType() == ChangeType.UPDATE) {
                      updateTuple(relation, tuple, ctx);
                    } else {
                      removeTuple(relation, tuple, ctx);
                    }
                    
                    rstime2 += (System.currentTimeMillis()-time);
                    rtuples++;
                  }
                  
                  // update start order
                  log.debug("New order value: {}={}", sync.getTable(), highestOrder);
                  setStartOrder(sync, ctx, highestOrder);
                  
                } finally {
                  reader.close();
                }
              }            
          }
          else if (synctype == Relation.SYNCHRONIZATION_RESCAN) {
            
            // EXPERIMENTAL: load extents
            ctx.loadExtents();
      
            // update start order values if there are changelogs declared
              for (Changelog sync : relation.getSyncs()) {
                String maxOrderValue = datasource.getMaxOrderValue(sync);
                log.debug("New order value: {}={}", sync.getTable(), maxOrderValue);
                setStartOrder(sync, ctx, maxOrderValue);
              }
            
            // full relation rescan
            TupleReaderIF reader = datasource.getReader(relationName);
            
            try {
              log.debug("  full rescan, table {}", relationName);
                
              String [] tuple = null;
              while ((tuple = reader.readNext()) != null) {
                // process individual tuple
                long time = System.currentTimeMillis();
                  
                updateTuple(relation, tuple, ctx);
                  
                rstime2 += (System.currentTimeMillis()-time);
                rtuples++;
              }
            } finally {
              reader.close();
            }
              
            // EXPERIMENTAL: remove untouched extent objects from the topic map
            ctx.removeExtentObjects();
          }
      
          // EXPERIMENTAL: remove expired field values (characteristics)
          ctx.removeOldValues();
          
          log.info("    Synchronized {} tuples for {}, {}/{} ms",
                   new Object[] {rtuples, relationName, (System.currentTimeMillis()-rstime1), rstime2});
          ttuples += rtuples;
        }
      }
    } finally {
      ctx.close();
    }
    if (log.isInfoEnabled()) {
      log.info("done synchronizing relations: {} tuples, {} ms. {}", new Object[] {ttuples, (System.currentTimeMillis()-tstime), new Date()});
    }
  }
  
  /**
   * INTERNAL: Gets the current start order value for the given changelog.
   */
  private static String getStartOrder(Changelog sync, Context ctx) {
    // if there is no reifier then no sync has been made yet
    TopicMapIF topicmap = ctx.getTopicMap();
    TopicIF reifier = topicmap.getReifier();
      
    if (reifier != null) {
      // look up occurrence type
      TopicIF otype = topicmap.getTopicBySubjectIdentifier(LOC_SYNCHRONIZATION_STATE);
      if (otype != null) {
        // create prefix value
        String procname = ctx.getMapping().getName();
        String relname = sync.getRelation().getName();
        String syncname = sync.getTable();
        String prefix = procname + ":" + relname + ":" + syncname + ":";      
        // loop over occurrences to find appropriate value
        for (OccurrenceIF occ : reifier.getOccurrences()) {
          TopicIF otype_ = occ.getType();
          if (otype_ != null && otype_.equals(otype)) {
            String value = occ.getValue();
            if (value != null && value.startsWith(prefix)) {
              return value.substring(prefix.length());
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * INTERNAL: Sets the start order value for the given changelog.
   */
  private static void setStartOrder(Changelog sync, Context ctx, String startOrder) {
    if (startOrder != null) {
      // if there is no reifier then no sync has been made yet
      TopicMapIF topicmap = ctx.getTopicMap();
      TopicIF reifier = topicmap.getReifier();
      // look up occurrence type
      TopicIF otype = topicmap.getTopicBySubjectIdentifier(LOC_SYNCHRONIZATION_STATE);
      
      // create prefix value
      String procname = ctx.getMapping().getName();
      String relname = sync.getRelation().getName();
      String syncname = sync.getTable();
      String prefix = procname + ":" + relname + ":" + syncname + ":";      
      OccurrenceIF match = null;
      
      if (reifier != null && otype != null) {
        // loop over occurrences to find appropriate value
        for (OccurrenceIF occ : reifier.getOccurrences()) {
          TopicIF otype_ = occ.getType();
          if (otype_ != null && otype_.equals(otype)) {
            String value = occ.getValue();
            if (value != null && value.startsWith(prefix)) {
              match = occ;
              break;
            }
          }
        }
      }
      
      String matchValue = prefix + startOrder;
      if (match == null) {
        if (reifier == null) {
          // create reifier
          reifier = ctx.getBuilder().makeTopic();
          topicmap.setReifier(reifier);
        }
        if (otype == null) {
          // create occurrence type
          otype = ctx.getBuilder().makeTopic();
          otype.addSubjectIdentifier(LOC_SYNCHRONIZATION_STATE);
          ctx.getBuilder().makeTopicName(otype, "DB2TM synchronization state");
        }
        // create new occurrence
        match = ctx.getBuilder().makeOccurrence(reifier, otype, matchValue); 
      } else {
        // update value
        match.setValue(matchValue);
      }
    }
  }
  
  private static void updateTuple(Relation relation, String[] tuple, Context ctx) {

    if (log.isDebugEnabled()) {
      log.debug("    u({}),{}", StringUtils.join(tuple, "|"), tuple.length);
    }
    
    List<Entity> entities = relation.getEntities();
    for (int i=0; i < entities.size(); i++) {
      Entity entity = entities.get(i);
      try {
        Object o = updateEntity(relation, entity, tuple, ctx);
        ctx.setEntityObject(i, o);
      } catch (Exception e) {
        throw new DB2TMException("Error occurred while updating tuple " + Arrays.asList(tuple) + " from relation " + relation.getName() + " to entity " + entity, e);
      }
    }

  }

  private static Object updateEntity(Relation relation, Entity entity, String[] tuple, Context ctx) {
    // 1. create entity if it does not exist
    // 2. synchronize characteristics

    TopicIF topic = null;
    if (entity.requiresTopic()) {
      // find candidate topic
      topic = addIdentities(topic, relation, entity, tuple, ctx);

      // FIXME: if we track updated objects can we then avoid loading
      // full extents?
      
      if (topic != null) {
        // this is an existing object, so we need to track it
        boolean firstTimeSeen = ctx.registerOldObject(topic);

        // if this is the first time we see this object then track the
        // existing values of relevant fields. once we've done that we
        // can add new characteristics. it will also allow us to reuse
        // any of those values. note that this tracking will only
        // happen once per object per relation.
        List<Field> cfields = entity.getCharacteristicFields();
        if (firstTimeSeen) {
          List<?>[] existingValues = new List<?>[cfields.size()];
          for (int i=0; i < cfields.size(); i++) {
            Field field = cfields.get(i);
            switch (field.getFieldType()) {
            case Field.TYPE_TOPIC_NAME:
              existingValues[i] = getTopicNames(topic, relation, entity, field, tuple, ctx);
              break;
            case Field.TYPE_OCCURRENCE:
              existingValues[i] = getOccurrences(topic, relation, entity, field, tuple, ctx);
              break;
            case Field.TYPE_PLAYER:
              existingValues[i] = getPlayers(topic, relation, entity, field, tuple, ctx);
              break;
            default:
              throw new DB2TMConfigException("Illegal characteristic field type: " + field);
            }
          }
          ctx.registerOldFieldValues(topic, existingValues);
        }        

        // ISSUE: should we clear the types and identities here as well?
        
        // update identities
        topic = updateIdentities(topic, relation, entity, tuple, ctx);
        
        // update topic types if primary
        if (entity.getEntityType() == Entity.TYPE_TOPIC) {
          // NOTE: association reifiers cannot have types
          if (entity.isPrimary()) {
            updateTypes(topic, entity.getTypes(), entity, tuple, ctx);
          }
        }

        // update characteristics
        for (int i=0; i < cfields.size(); i++) {
          Field field = cfields.get(i);
          
          switch (field.getFieldType()) {
          case Field.TYPE_TOPIC_NAME:
            addTopicName(topic, relation, entity, field, i, tuple, ctx);
            break;
          case Field.TYPE_OCCURRENCE:
            addOccurrence(topic, relation, entity, field, i, tuple, ctx);
            break;
          case Field.TYPE_PLAYER:
            addPlayer(topic, relation, entity, field, i, tuple, ctx);
            break;
          default:
            throw new DB2TMConfigException("Illegal characteristic field type: " + field);
          }
        }
        
      }
      // create topic entity if it does not exist
      else if (entity.getEntityType() == Entity.TYPE_TOPIC) {
        return addEntity(relation, entity, tuple, ctx);
      }
      
      // if association entity, we'll have to wait a little      
    }
    
    if (entity.getEntityType() == Entity.TYPE_ASSOCIATION) {
      // create association
      return addAssociation(topic, relation, entity, tuple, ctx);
    } else {
      return topic;
    }
  }
  
}
