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

package net.ontopia.topicmaps.nav2.portlets.pojos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.nav2.portlets.pojos.Menu.Heading;
import net.ontopia.topicmaps.nav2.portlets.pojos.Menu.Item;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: Helper methods for Menu.
 */
public class MenuUtils {
  // Indicates that a menu item (ItemIF) should be moved up the list.
  public static final boolean UP = true;
  // Indicates that a menu item (ItemIF) should be moved down the list.
  public static final boolean DOWN = false;

  /**
   * Runs the given query with the given topic as parameter %topic% and returns
   * the first value (of the first collumn) in the result collection.
   * @param topic The parameter refered to as %topic% in the query.
   * @param pq The query that genereates the result.
   * @return The first value returned by the query.
   */
  public static Object getFirstValue(TopicIF topic, ParsedQueryIF pq) {
    QueryResultIF qr = null;
    try {
      qr = pq.execute(Collections.singletonMap("topic", topic));
      while (qr.next()) {
        return qr.getValue(0);
      }
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
    return null;
  }
  
  /**
   * Get the values of a given query with a given %topic% parameter as a List
   * @param topic The topic parameter represened by %topic% in the query.
   * @param parsedQuery The query.
   * @return the first result column values of the query, as a List.
   */
  public static List getResultValues(TopicIF topic, ParsedQueryIF parsedQuery) {    
    List topics = new ArrayList();
    QueryResultIF qr = null;
    try {
      qr = parsedQuery.execute(Collections.singletonMap("topic", topic));
      while (qr.next()) {
        topics.add(qr.getValue(0));
      }
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
    return topics;
  }

  /**
   * Test if the given query returns any result rows.
   * @param topic The %topic% parameter in the query.
   * @param query The query.
   * @return true if query returns one or more result rows. Otherwise, false.
   */
  public static boolean getResultTrue(TopicIF topic, String query) {
    Map params = new HashMap();
    params.put("topic", topic);
    QueryProcessorIF proc = QueryUtils.getQueryProcessor(topic.getTopicMap());
    QueryResultIF qr = null;
    try {
      qr = proc.execute(query, params);
      return qr.next();
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
  }
  
  /** 
   * Create new Heading as child of the given parent
   * @param topic Represents the parent.
   * @param title The title of the Heading.
   * @return The Heading that was created.
   */
  public static Heading createHeading(TopicIF topic, String title) {
    return (Heading)createChild(topic, title, true);
  }
    
  /** 
   * Create new Item as child of the given parent
   * @param topic Represents the parent.
   * @param title The title of the Item.
   * @return The Item that was created.
   */
  public static Item createItem(TopicIF topic, String title) {
    return (Item)createChild(topic, title, false);
  }
    
  /**
   * Move the given child one step up or down the list of children on the parent
   * @param topic Represents the chils.
   * @param up use UP or DOWN to move up or down the list, respectively.
   */
  public static void moveOne(TopicIF topic, boolean up) {
    TopicMapIF tm = topic.getTopicMap();
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    DeclarationContextIF dc = optimisticParse(tm, 
        "using menu for i\"http://psi.ontopia.net/portal/menu/\"");
    ParsedQueryIF neighbourQuery = optimisticParse(qp, dc,
        "select $CSORT, $CVAL from " +
        "menu:parent-child($PARENT : menu:parent, %topic% : menu:child), " +
        "menu:parent-child($PARENT : menu:parent, $CHILD : menu:child), " +
        "occurrence(%topic%, $HSORT), type($HSORT, menu:sort), " +
        "occurrence($CHILD, $CSORT), type($CSORT, menu:sort), " +
        "value($HSORT, $HVAL), value($CSORT, $CVAL)," +
        "$HVAL " + (up ? ">" : "<") + " $CVAL order by $CVAL?");
    ParsedQueryIF sortKeyQuery = optimisticParse(qp, dc, 
        "occurrence(%topic%, $SORT), type($SORT, menu:sort)?");
      
    // Get the sort key of this heading
    OccurrenceIF sortKey = (OccurrenceIF)getFirstValue(topic, sortKeyQuery);

    // Get the neighbour (previous/next) sort key.
    OccurrenceIF neighbourKey = up ? getLastOfQuery(topic, neighbourQuery)
                                  : getFirstOfQuery(topic, neighbourQuery);
    
    // Can't move beyond the extremes of the list.
    if (neighbourKey == null) {
      return;
    }
    
    // Swap sort keys
    String swap = sortKey.getValue();
    sortKey.setValue(neighbourKey.getValue());
    neighbourKey.setValue(swap);
  }
  
  /**
   * Set the basename of a given topic, removing any old basenames. 
   * @param topic The topic.
   * @param baseName The new basename.
   */
  public static void setUniqueTopicName(TopicIF topic, String baseName) {
    TopicMapBuilderIF builder = topic.getTopicMap().getBuilder();

    Collection oldTopicNames = new HashSet(topic.getTopicNames());
    Iterator oldTopicNamesIt = oldTopicNames.iterator();
    while (oldTopicNamesIt.hasNext()) {
      TopicNameIF currentTopicName = (TopicNameIF)oldTopicNamesIt.next();
      currentTopicName.remove();
    }
    
    builder.makeTopicName(topic, baseName);    
  }
  
  /**
   * Set the occurrence of a given type on a given topic, removing any existing
   * occurrences of the same type on that topic.
   * @param topic The topic that should have the occurrence.
   * @param typeId The type, as refered to in the query (e.g. "menu:link")
   * @param value The value of the occurrence.
   */
  public static void setUniqueOccurrence(TopicIF topic, String typeId,
      String value) {
    String query = "select $LINK from " +
        "occurrence(%topic%, $LINK), type($LINK, " + typeId + ")?";
    List occs = getResultValues(topic, query);
    Iterator occsIt = occs.iterator();
    while (occsIt.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF)occsIt.next();
      occ.remove();
    }

    TopicMapIF tm = topic.getTopicMap();
    TopicIF type = getTopic(typeId, tm);
    TopicMapBuilderIF builder = tm.getBuilder();
    builder.makeOccurrence(topic, type, value);
  }

  /**
   * Set the binary association with given role types, association types and
   * players, removing any existing associations with the same role types and 
   * association type on player1. 
   * @param player1 The first player, for which old associations are removed.
   * @param rType1Id The first role type, as a string (e.g. "menu:item")
   * @param aTypeId The association type, as a string (e.g. "menu:item-topic")
   * @param rType2Id The second role type, as a string (e.g. "menu:topic")
   * @param player2 The second player.
   */
  public static void setUniqueAssociation(TopicIF player1, String rType1Id,
      String aTypeId, String rType2Id, TopicIF player2) {
    String query = "select $ASSOC from type($ASSOC, " + aTypeId + "), " +
        "association-role($ASSOC, $ROLE1), " +
        "type($ROLE1, " + rType1Id +"), " +
        "role-player($ROLE1, %topic%)?";
    List assocs = getResultValues(player1, query);

    TopicMapIF tm = player1.getTopicMap();
    Iterator assocsIt = assocs.iterator();
    while (assocsIt.hasNext()) {
      AssociationIF assoc = (AssociationIF)assocsIt.next();
      assoc.remove();
    }

    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF aType = getTopic(aTypeId, tm);
    TopicIF rType1 = getTopic(rType1Id, tm);
    TopicIF rType2 = getTopic(rType2Id, tm);

    AssociationIF assoc = builder.makeAssociation(aType);
    builder.makeAssociationRole(assoc, rType1, player1);
    builder.makeAssociationRole(assoc, rType2, player2);
  }

  /**
   * Parse the given query for the given topic map.
   * InvalidQueryExceptions thrown during the parse process are caught and
   * re-thrown with an additional message as OntopiaRuntimeExceptions. This
   * avoids external try {} catch() {} blocks around this method.
   * @param query The query to parse.
   * @param tm The topicmap used by the query.
   * @return A ParsedQueryIF representing the parsed query.
   */
  protected static ParsedQueryIF optimisticParse(String query, TopicMapIF tm) {
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    DeclarationContextIF dc = optimisticParse(tm, 
        "using menu for i\"http://psi.ontopia.net/portal/menu/\"");
    return optimisticParse(qp, dc, query);
  }

  /**
   * Parse the given declaration-context-query for the given topic map.
   * InvalidQueryExceptions thrown during the parse process are caught and
   * re-thrown with an additional message as OntopiaRuntimeExceptions. This
   * avoids external try {} catch() {} blocks around this method.
   * @param tm The topicmap used by the query.
   * @param query The query to parse.
   * @return A DeclarationContextIF representing the parsed query.
   */
  protected static DeclarationContextIF optimisticParse(TopicMapIF tm, 
      String query) {
    try {
      return QueryUtils.parseDeclarations(tm, query);
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException("There was a problem parsing the " +
          "query \"" + query + 
          "\" probably due to an error in the menu ontology.", e);
    }
  }
  
  /**
   * Parse the given declaration-context-query for the given topic map.
   * InvalidQueryExceptions thrown during the parse process are caught and
   * re-thrown with an additional message as OntopiaRuntimeExceptions. This
   * avoids external try {} catch() {} blocks around this method.
   * @param qp The query processor that will process the query.
   * @param dc A declaration context to be used by the query.
   * @param query The query to parse.
   * @return A ParsedQueryIF representing the parsed query.
   */
  private static ParsedQueryIF optimisticParse(QueryProcessorIF qp, 
      DeclarationContextIF dc, String query) {
    try {
      return qp.parse(query, dc);
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException("There was a problem parsing the " +
          "query \"" + query + 
          "\" probably due to an error in the menu ontology.", e);
    }
  }
  
  /**
   * Runs the given query with the given topic as parameter %topic% and returns
   * the last value (of the first collumn) in the result collection.
   * @param topic The parameter refered to as %topic% in the query.
   * @param query The query that genereates the result.
   * @return The last value returned by the query.
   */
  private static String getLastValue(TopicIF topic,
      ParsedQueryIF query) {
    List resultValues = getResultValues(topic, query);
    if (resultValues.isEmpty()) {
      return null;
    }
    return (String)resultValues.get(resultValues.size() - 1);
  }

  /**
   * Get the result values of the given query with the given %topic% parameter.
   * @param topic Parameter represented by %topic% in the query.
   * @param query The query.
   * @return The first column of the result values.
   */
  private static List getResultValues(TopicIF topic, String query) {
    TopicMapIF tm = topic.getTopicMap();
    ParsedQueryIF pq = optimisticParse(query, tm);
    return getResultValues(topic, pq);
  }

  /**
   * Get the topic represented in queries by 'id' from the given topic map.
   * @param id The id of the topic as given in a query (e.g. "menu:item")
   * @param tm The topic map containing the sought topic.
   * @return The topic represented by 'id'
   */
  private static TopicIF getTopic(String id, TopicMapIF tm) {
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    QueryResultIF qr = null;
    try {
      DeclarationContextIF dc = QueryUtils.parseDeclarations(tm, 
          "using menu for i\"http://psi.ontopia.net/portal/menu/\"");
      qr = qp.execute("topic($T), $T = " + id + "?", dc);
      if (!qr.next()) {
        throw new OntopiaRuntimeException("Getting topic '" + id +
                                          "' gave no results.");
      }
      Object retObject = qr.getValue(0);
      if (!(retObject instanceof TopicIF)) {
        throw new OntopiaRuntimeException("Getting topic '" + id + "' should " +
                                          "give a result of type TopicIF, but" +
                                          " gave a result of type " + 
                                          retObject.getClass().getName());
      }
      TopicIF retTopic = (TopicIF)retObject;
      if (qr.next()) {
        throw new OntopiaRuntimeException("Getting topic '" + id +
                                          "' should give a unique result, but" +
                                          " gives more than one result.");
      }
      return retTopic;
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
  }

  /**
   * Get the last value of the first column from the result of a given query.
   * @param topic Parameter represented by %topic% in the query.
   * @param query The query.
   * @return The last value of the first column from the result of the query.
   */
  private static OccurrenceIF getLastOfQuery(TopicIF topic,
      ParsedQueryIF query) {
    List resultValues =
        (List)getResultValues(topic, query);
    if (resultValues.isEmpty()) {
      return null;
    } else {
      return (OccurrenceIF)resultValues.get(resultValues.size() - 1);
    }
  }
  
  /**
   * Get the first value of the first column from the result of a given query.
   * @param topic Parameter represented by %topic% in the query.
   * @param query The query.
   * @return The first value of the first column from the result of the query.
   */
  private static OccurrenceIF getFirstOfQuery(TopicIF topic,
      ParsedQueryIF query) {
    List resultValues =
        (List)getResultValues(topic, query);
    if (resultValues.isEmpty()) {
      return null;
    } else {
      return (OccurrenceIF)resultValues.get(0);
    }
  }

  /**
   * Create a child (Heading or Item) of the parent represented by a given topic
   * @param topic The topic of the parent.
   * @param title The title of the new child.
   * @param isHeading true to create a Heading, false to create an Item.
   * @return
   */
  private static Menu.ChildIF createChild(TopicIF topic, String title, 
      boolean isHeading) {
    TopicMapIF tm = topic.getTopicMap();
    TopicMapBuilderIF builder = tm.getBuilder();
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    DeclarationContextIF dc = optimisticParse(tm, 
        "using menu for i\"http://psi.ontopia.net/portal/menu/\"");

    TopicIF itemIFTopicType = getTopic(
        isHeading ? "menu:heading" : "menu:item", tm);
    TopicIF parentChildAssociation = getTopic("menu:parent-child", tm);
    TopicIF parentRoleType = getTopic("menu:parent", tm);
    TopicIF childRoleType = getTopic("menu:child", tm);
    TopicIF sortOccurrenceType = getTopic("menu:sort", tm);

    ParsedQueryIF sortKeysQuery = optimisticParse(qp, dc, "select $SORT from " +
        "menu:parent-child(%topic% : menu:parent, $CHILD : menu:child), " +
        "{ menu:sort($CHILD, $SORT) } order by $SORT?");
    
    // Create the heading topic.
    TopicIF itemIFTopic = builder.makeTopic(itemIFTopicType);
    
    // Make the heading child of the menu.
    AssociationIF assoc = builder.makeAssociation(parentChildAssociation);
    builder.makeAssociationRole(assoc, parentRoleType, topic);
    builder.makeAssociationRole(assoc, childRoleType, itemIFTopic);
    
    // Get the highest sort key of children of this menu.
    String lastSortKey = getLastValue(topic, sortKeysQuery);
    int lastSortKeyInt = Integer.parseInt(lastSortKey);
    lastSortKeyInt++;
    String newSortKey = StringUtils.leftPad(Integer.toString(lastSortKeyInt), 3, '0');
    builder.makeOccurrence(itemIFTopic, sortOccurrenceType, newSortKey);
    
    Menu.ChildIF itemIF;
    if (isHeading) { 
      itemIF = new Heading(itemIFTopic);
    } else {
      itemIF = new Item(itemIFTopic);
    }
    itemIF.setTitle(title);
    return itemIF;
  }
  
  /**
   * Build the Heading of a given topic, from the topic map content.
   */
  private static Heading buildHeading(TopicIF topic) {
    Heading heading = new Heading(topic);
    heading.children = buildChildren(topic);
    return heading;
  }
  
  /**
   * Build the Item of a given topic, from the topic map content.
   */
  private static Item buildItem(TopicIF topic) {
    TopicMapIF tm = topic.getTopicMap();
    ParsedQueryIF itemTopicQuery = MenuUtils.optimisticParse(
        "select $TOPIC from " +
        "menu:item-topic(%topic% : menu:item, $TOPIC : menu:topic)?", tm);
    ParsedQueryIF linkQuery = MenuUtils.optimisticParse(
        "select $LINK from menu:link(%topic%, $LINK)?", tm);
    ParsedQueryIF imageQuery = MenuUtils.optimisticParse(
        "select $IMAGE from menu:image(%topic%, $IMAGE)?", tm);

    Item item = new Item(topic);
    item.associatedTopic = (TopicIF)MenuUtils
        .getFirstValue(topic,itemTopicQuery);
    item.link = (String)MenuUtils.getFirstValue(topic, linkQuery);
    item.image = (String)MenuUtils.getFirstValue(topic, imageQuery);
    
    String query = item.getCondition();
    item.condition = (query == null) || MenuUtils
        .getResultTrue(item.associatedTopic, query);
    
    return item;
  }

  /**
   * Build the List of children of a given parent, from the topic map content.
   */
  private static List buildChildren(TopicIF topic) {    
    TopicMapIF tm = topic.getTopicMap();
    ParsedQueryIF childrenQuery = MenuUtils.optimisticParse(
        "select $CHILD, $SORT from " +
        "menu:parent-child(%topic% : menu:parent, $CHILD : menu:child), " +
        "{ menu:sort($CHILD, $SORT) } order by $SORT?", tm);
    ParsedQueryIF headingsQuery = MenuUtils.optimisticParse(
        "select $CHILD from " +
        "menu:parent-child(%topic% : menu:parent, $CHILD : menu:child), " +
        "instance-of($CHILD, menu:heading)?", tm);
    List childrenTopics = MenuUtils.getResultValues(topic, childrenQuery);
    List headingsTopics = MenuUtils.getResultValues(topic, headingsQuery);
    
    // populate children
    List children = new ArrayList(childrenTopics.size());
    for (int i=0; i < childrenTopics.size(); i++) {
      TopicIF child = (TopicIF)childrenTopics.get(i);
      if (headingsTopics.contains(child)) {
        children.add(buildHeading(child));
      } else {
        children.add(buildItem(child));
      }
    }
    return children;    
  }
}
