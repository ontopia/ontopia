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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.KeyGenerator;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * PUBLIC: This component can produce a model representing the
 * associations of a given topic.
 */
public class RelatedTopics {

  /**
   * PUBLIC: Flag used to indicated ascending ordering.
   */
  public static final int ORDERING_ASC = 1;
  /**
   * PUBLIC: Flag used to indicated descending ordering.
   */
  public static final int ORDERING_DESC = 2;
    
  // all of these contain object IDs rather than actual objects; this
  // avoids problems with hard references  
  private Set weaktypes;
  private Set exclassocs;
  private Set exclroles;
  private Set excltopics;
  private Set inclassocs;
  private Set incltopics;

  // if we have to load the configuration from the topic map it gets cached
  // here
  private Set weaktypes_cache;
  private Set exclassocs_cache;
  private Set excltopics_cache;
  private Function<TopicIF, String> sort; // this one is also cached
  
  // the maximum number of children to show
  private int maxchildren = -1;

  // queries
  private String headingOrderQueryString;
  private int headingOrdering = ORDERING_ASC;
  private String childOrderQueryString;
  private int childOrdering = ORDERING_ASC;
  private String filterquery;
  private DeclarationContextIF tologctx;
  
  // aggregation
  private boolean aggregateHierarchy;
  private Set aggregateAssociations;  
  
  // the system identity hash of the store from which we populated the
  // _cache variables last time
  private int storeid;
  // the system identity hash of the topic map from which we populated the
  // _cache variables last time
  private int tmid;

  private boolean useOntopolyNames;

  // configuration

  /**
   * PUBLIC: Set the set of association types which is to be
   * considered <em>weak</em> in the sense that associations of these
   * types are to be listed under the heading for the topic type of
   * the associated topics, and not under the association type.
   */
  public void setWeakAssociationTypes(Set weaktypes) {
    this.weaktypes = mapToObjectIds(weaktypes);
  }

  /**
   * PUBLIC: Set the set of association types which is not to be
   * shown.
   */
  public void setExcludeAssociationTypes(Set types) {
    this.exclassocs = mapToObjectIds(types);
  }

  /**
   * PUBLIC: Set the set of near roles types which is not to be included.
   *
   * @since 3.4.2
   */
  public void setExcludeRoleTypes(Set types) {
    this.exclroles = mapToObjectIds(types);
  }

  /**
   * PUBLIC: Set the set of topic types which is not to be shown.  For
   * n-ary associations the filter takes effect if any of the roles
   * are played by excluded types.
   */
  public void setExcludeTopicTypes(Set types) {
    this.excltopics = mapToObjectIds(types);
  }

  /**
   * PUBLIC: Set the set of association types which is to be
   * shown.
   */
  public void setIncludeAssociationTypes(Set types) {
    this.inclassocs = mapToObjectIds(types);
  }

  /**
   * PUBLIC: Set the set of topic types which is to be shown.  For
   * n-ary associations the filter takes effect if any of the roles
   * are not played by included types.
   */
  public void setIncludeTopicTypes(Set types) {
    this.incltopics = mapToObjectIds(types);
  }

  /**
   * PUBLIC: Sets a query to be used to filter topics shown as
   * related. Only topics for which the query does <em>not</em>
   * return any rows will be shown.
   */
  public void setFilterQuery(String query) {
    this.filterquery = query;
  }

  /**
   * PUBLIC: Sets the maximum number of children for a heading to show
   * by default. A negative value will show all children.
   *
   * @since 3.4
   */
  public void setMaxChildren(int maxchildren) {
    this.maxchildren = maxchildren;
  }

  /**
   * PUBLIC: Sets the query to use to get the sort key of each
   * heading topic.
   *
   * @since 3.4
   */
  public void setHeadingOrderQuery(String headingOrderQueryString) {
    this.headingOrderQueryString = headingOrderQueryString;
  }

  /**
   * PUBLIC: Sets the ordering direction to be used for headings.
   *
   * @since 3.4.1
   */
  public void setHeadingOrdering(int headingOrdering) {
    this.headingOrdering = headingOrdering;
  }

  /**
   * PUBLIC: Sets the query to use to get the sort key of each
   * child topic.
   *
   * @since 3.4
   */
  public void setChildOrderQuery(String childOrderQueryString) {
    this.childOrderQueryString = childOrderQueryString;
  }

  /**
   * PUBLIC: Sets the ordering direction to be used for children.
   *
   * @since 3.4.1
   */
  public void setChildOrdering(int childOrdering) {
    this.childOrdering = childOrdering;
  }

  /**
   * PUBLIC: Sets the flag indicating whether to do hierarchy
   * aggregation or not.
   *
   * @since 3.4.2
   */
  public void setAggregateHierarchy(boolean aggregateHierarchy) {
    this.aggregateHierarchy = aggregateHierarchy;
  }

  /**
   * PUBLIC: Sets the association types to do hierarchy aggregation
   * for.
   *
   * @since 3.4.2
   */
  public void setAggregateAssociations(Set aggregateAssociations) {
    this.aggregateAssociations = aggregateAssociations;
  }

  /**
   * PUBLIC: Passes in a tolog declaration context to be used when
   * parsing tolog queries.
   *
   * @since 3.4.2
   */
  public void setTologContext(DeclarationContextIF tologctx) {
    this.tologctx = tologctx;
  }
  
  // model building

  /**
   * PUBLIC: Builds a model representing the associations of the given
   * topic.
   * @return a list of Heading objects
   */
  public List makeModel(TopicIF topic) {
    // first, validate the configuration
    if (excltopics != null && !excltopics.isEmpty() &&
        incltopics != null && !incltopics.isEmpty()) {
      throw new OntopiaRuntimeException("Configuration fields includeTopicTypes and excludeTopicTypes cannot both be specified.");
    }
    if (exclassocs != null && !exclassocs.isEmpty() &&
        inclassocs != null && !inclassocs.isEmpty()) {
      throw new OntopiaRuntimeException("Configuration fields includeAssociationTypes and excludeAssociationTypes cannot both be specified.");
    }
        
    // then, update the configuration cache
    updateCache(topic.getTopicMap());

    ParsedQueryIF pquery = null;
    if (filterquery != null) {
      pquery = parse(topic.getTopicMap(), filterquery);
    }

    ParsedQueryIF headingOrderQuery = null;
    if (headingOrderQueryString != null) {
      headingOrderQuery = parse(topic.getTopicMap(), headingOrderQueryString);
    }
      
    ParsedQueryIF childOrderQuery = null;
    if (childOrderQueryString != null) {
      childOrderQuery = parse(topic.getTopicMap(), childOrderQueryString);
    }
    
    // group associations by the headings they will wind up under
    Map typemap = new HashMap();
    Iterator it = getRoles(topic).iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      if (isRoleHidden(role, pquery)) {
        continue; // if the filter hides this association we just skip it
      }

      AssociationIF assoc = role.getAssociation();
      if (getWeakTypes().contains(getObjectId(assoc.getType()))) {
        // this is a weak type
        if (assoc.getRoles().size() != 2) {
          throw new OntopiaRuntimeException("Weak associations cannot be " +
                                            "n-ary or unary");
        }
        AssociationRoleIF other = getOtherRole(assoc, role);
        TopicIF player = other.getPlayer();
        TopicIF ttype = null;
        if (player.getTypes().size() > 0) {
          ttype = (TopicIF) player.getTypes().iterator().next();
        }
        String key = getObjectId(ttype);
        Heading heading = (Heading) typemap.get(key);
        if (heading == null) {
          heading = new Heading(ttype);
          if (headingOrderQuery != null) {
            heading.setSortKey(getSortKey(ttype, headingOrderQuery));
          }
          typemap.put(key, heading);
        }
        Association child = new Association(role, false);
        if (childOrderQuery != null) {
          child.setSortKey(getSortKey(child.getPlayer(), childOrderQuery));
        }
        heading.addChild(child);
      } else {
        // not a weak type
        String key = getObjectId(assoc.getType()) + "." +
                     getObjectId(role.getType());
        Heading heading = (Heading) typemap.get(key);
        if (heading == null) {
          heading = new Heading(assoc.getType(), role.getType());
          if (headingOrderQuery != null) {
            heading.setSortKey(getSortKey(assoc.getType(), headingOrderQuery));
          }
          typemap.put(key, heading);
        }
        Association child = new Association(role, true);
        if (childOrderQuery != null) {
          child.setSortKey(getSortKey(child.getPlayer(), childOrderQuery));
        }
        heading.addChild(child);
      }
    }

    // sort the headings
    List headings = new ArrayList(typemap.values());
    Collections.sort(headings);

    // sort the children
    for (int i=0; i < headings.size(); i++) {
      Heading heading = (Heading)headings.get(i);
      Collections.sort(heading.children);      
    }
    
    // we're done
    return headings;
  }

  // --- Internal methods

  private Collection getRoles(TopicIF topic) {
    // if no hierarchy aggregation just return topic's direct roles
    if (!aggregateHierarchy) {
      return topic.getRoles();
    }
    
    // build aggregate query
    StringBuilder query = new StringBuilder();
    query.append("/* #OPTION: optimizer.reorder=false */ ");
    query.append("/* #OPTION: optimizer.hierarchy-walker=false */ ");
    query.append("using h for i\"http://www.techquila.com/psi/hierarchy/#\" ");
    query.append("subordinate($SUP, $SUB) :- ");
    query.append("  role-player($R1, $SUP), type($R1, $RT1), instance-of($RT1, h:superordinate-role-type), "); 
    query.append("  association-role($A, $R1), ");
    query.append("  association-role($A, $R2), $R1 /= $R2, ");
    query.append("  type($A, $AT), instance-of($AT, h:hierarchical-relation-type), ");  
    query.append("  type($R2, $RT2), instance-of($RT2, h:subordinate-role-type), ");  
    query.append("  role-player($R2, $SUB). "); 
    query.append("hierarchy($SUP, $SUB) :- ");  
    query.append("  { subordinate($SUP, $SUB) | "); 
    query.append("    subordinate($SUP, $X), hierarchy($X, $SUB) }. "); 
    query.append("select $R from "); 
    query.append("{ $T = %topic% | hierarchy(%topic%, $T) }, "); 
    query.append("role-player($R, $T), association-role($A, $R), type($A, $AT), "); 

    if (aggregateAssociations != null && !aggregateAssociations.isEmpty()) {
      // aggregate only given association types
      boolean useOrBranch = (aggregateAssociations.size() > 1);
      if (useOrBranch) {
        query.append("{ ");
      }    
      Iterator iter = aggregateAssociations.iterator();
      while (iter.hasNext()) {
        TopicIF atype = (TopicIF)iter.next();      
        query.append("$AT = @");
        query.append(atype.getObjectId());
        if (iter.hasNext()) {
          query.append(" | ");
        }      
      }    
      if (useOrBranch) {
        query.append(" }");
      }
    } else {
      // aggregate all except hierarchical association types
      query.append("not(instance-of($AT, h:hierarchical-relation-type))");     
    }    
    query.append(" order by $R?");

    // execute query
    Map result = new HashMap();    
    QueryProcessorIF proc = QueryUtils.getQueryProcessor(topic.getTopicMap());
    try {      
      QueryResultIF qr = proc.execute(query.toString(), Collections.singletonMap("topic", topic));
      try {      
        while (qr.next()) {
          AssociationRoleIF role = (AssociationRoleIF)qr.getValue(0);
          String rkey = KeyGenerator.makeAssociationKey(role.getAssociation(), role);
          if (!result.containsKey(rkey)) {
            result.put(rkey, role);
          }
        }
      } finally {
        qr.close();
      }
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    }
    
    return result.values();
  }
  
  private boolean isRoleHidden(AssociationRoleIF role, ParsedQueryIF pquery) {
    // is the association type filtered out?
    AssociationIF assoc = role.getAssociation();
    Set hide = (exclassocs == null) ? exclassocs_cache : exclassocs;
    if (hide.contains(getObjectId(assoc.getType())) || // filtered by exclude
        (inclassocs != null && !inclassocs.contains(getObjectId(assoc.getType())))) { // filtered by include
      return true;
    }

    // is the role type filtered out?
    if (exclroles != null && exclroles.contains(getObjectId(role.getType()))) {
      return true;
    }
            
    // are any of the topics in the association filtered out?
    hide = (excltopics == null) ? excltopics_cache : excltopics;
    if (hide.isEmpty() && pquery == null) {
      return false;
    }
    
    Iterator it = assoc.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF other = (AssociationRoleIF) it.next();
      if (other.equals(role)) {
        continue;
      }

      if (isTopicHidden(other.getPlayer(), hide, pquery)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isTopicHidden(TopicIF topic, Set hide, ParsedQueryIF pquery) {
    boolean inclTopicMatch = false;
    Iterator it = topic.getTypes().iterator();
    while (it.hasNext()) {
      TopicIF type = (TopicIF) it.next();
      // FIXME: should we support subtyping here?
      if (hide.contains(getObjectId(type))) {
        return true;
      }
      
      if (!inclTopicMatch) {
        inclTopicMatch = (incltopics != null && incltopics.contains(getObjectId(type)));
      }
    }
    
    return (incltopics != null && !inclTopicMatch) ||
           (pquery != null && istrue(pquery, topic));
  }

  private Object getSortKey(TopicIF topic, ParsedQueryIF skquery) {
    if (topic == null) {
      return null;
    }
    QueryResultIF result = null;
    try {
      result = skquery.execute(Collections.singletonMap("topic", topic));
      if (result.next()) {
        return result.getValue(0);
      } else {
        return null;
      }
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (result != null) {
        result.close();
      }
    }
  }
  
  private Set getWeakTypes() {
    return weaktypes == null ? weaktypes_cache : weaktypes;
  }
  
  private String getObjectId(TMObjectIF object) {
    if (object == null) {
      return "NULL";
    } else {
      return object.getObjectId();
    }
  }
  
  private static AssociationRoleIF getOtherRole(AssociationIF assoc,
                                                AssociationRoleIF role) {
    // INV: assoc.getRoles().size() == 2
    Iterator it = assoc.getRoles().iterator();
    AssociationRoleIF other = (AssociationRoleIF) it.next();
    if (other == role) {
      other = (AssociationRoleIF) it.next();
    }
    return other;
  }

  private Set mapToObjectIds(Set objects) {
    if (objects == null) {
      return null;
    }
    Set objids = new CompactHashSet(objects.size());
    Iterator it = objects.iterator();
    while (it.hasNext()) {
      TMObjectIF obj = (TMObjectIF) it.next();
      objids.add(obj.getObjectId());
    }
    return objids;
  }

  private void updateCache(TopicMapIF topicmap) {
    if (System.identityHashCode(topicmap.getStore()) == storeid &&
        System.identityHashCode(topicmap) == tmid) {
      return; // we already have this
    }

    String decl = "using port for i\"http://psi.ontopia.net/portlets/\" ";
    QueryProcessorIF proc = QueryUtils.getQueryProcessor(topicmap);
    weaktypes_cache = new CompactHashSet();
    try {
      QueryResultIF result = proc.execute(decl +
        "port:not-semantic-type($AT : port:type)?");
      while (result.next()) {
        weaktypes_cache.add(((TopicIF) result.getValue(0)).getObjectId());
      }
      result.close();
    } catch (InvalidQueryException e) {
      // happens if the port:* topics don't exist in the TM; that's OK
    }

    excltopics_cache = new CompactHashSet();
    try {
      QueryResultIF result = proc.execute(decl +
        "port:is-hidden-topic-type($AT : port:type)?");
      while (result.next()) {
        excltopics_cache.add(((TopicIF) result.getValue(0)).getObjectId());
      }
      result.close();
    } catch (InvalidQueryException e) {
      // happens if the port:* topics don't exist in the TM; that's OK
    }    
    
    exclassocs_cache = new CompactHashSet();
    try {
      QueryResultIF result = proc.execute(decl +
        "port:is-hidden-association-type($AT : port:type)?");
      while (result.next()) {
        exclassocs_cache.add(((TopicIF) result.getValue(0)).getObjectId());
      }
      result.close();
    } catch (InvalidQueryException e) {
      // happens if the port:* topics don't exist in the TM; that's OK
    }

    sort = TopicStringifiers.getFastSortNameStringifier(topicmap);
    
    storeid = System.identityHashCode(topicmap.getStore());
    tmid = System.identityHashCode(topicmap);
  }

  private ParsedQueryIF parse(TopicMapIF tm, String query) {
    try {
      QueryProcessorIF proc = QueryUtils.getQueryProcessor(tm);
      return proc.parse(query, tologctx);
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  private boolean istrue(ParsedQueryIF pquery, TopicIF topic) {
    QueryResultIF result = null;
    try {
      result = pquery.execute(Collections.singletonMap("topic", topic));
      return result.next();
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (result != null) {
        result.close();
      }
    }
  }

  private int compareHeadings(Object o1, Object o2) {
    int result = _compare(o1, o2);
    if (headingOrdering == ORDERING_DESC) {
      return -1 * result;
    } else {
      return result;
    }
  }

  private int compareChildren(Object o1, Object o2) {
    int result = _compare(o1, o2);
    if (childOrdering == ORDERING_DESC) {
      return -1 * result;
    } else {
      return result;
    }
  }

  private int _compare(Object o1, Object o2) {
    // NOTE: helper method that compares object in much the same way as 
    if (o1 instanceof String && o2 instanceof String) {
      // sort string case insensitively
      return StringUtils.compareIgnoreCase((String)o1, (String)o2);
    } else if (o1 instanceof Comparable && o2 instanceof Comparable) {
      // compare comparable objects
      return ((Comparable)o1).compareTo((Comparable)o2);
    } else if (o1 instanceof TopicIF && o2 instanceof TopicIF) {
      // compare topics
      TopicIF t1 = (TopicIF)o1;
      TopicIF t2 = (TopicIF)o2;
      String s1 = sort.apply(t1);
      String s2 = sort.apply(t2);
      return StringUtils.compareIgnoreCase(s1, s2);      
    }
    //! else if (o1 instanceof TMObjectIF && o2 instanceof TMObjectIF) {
    //!   // compare tmobjects
    //!   TMObjectIF t1 = (TMObjectIF)o1;
    //!   TMObjectIF t2 = (TMObjectIF)o2;
    //!   return ObjectUtils.compareIgnoreCase(t1.getObjectId(), t2.getObjectId());
    //! }
    throw new OntopiaRuntimeException("Unsupported sort keys: " + o1 + " and " + o2);
  }

  public void setUseOntopolyNames(boolean useOntopolyNames) {
    this.useOntopolyNames = useOntopolyNames;
  }

  // --- Heading

  public class Heading implements Comparable {
    private TopicIF topic;
    private TopicIF nearRoleType;
    private List children;
    private boolean isTopicType;
    private int arity;
    private Object sortkey;
    
    private Heading(TopicIF topic, TopicIF nearRoleType) {
      this.topic = topic;
      this.nearRoleType = nearRoleType;
      this.isTopicType = false;
      this.children = new ArrayList();
      this.arity = 0;
    }

    private Heading(TopicIF topic) {
      this.topic = topic;
      this.isTopicType = true;
      this.children = new ArrayList();
      this.arity = 0;
    }

    /**
     * Adds a new association to the heading given the near association
     * role.
     * @param assoctype Whether or not the parent heading represents
     * an association type.
     */
    private void addChild(Association assoc) {
      children.add(assoc);
      arity = Math.max(arity, assoc.getArity());
    }

    public String getTitle() {
      if (useOntopolyNames && nearRoleType != null) {
        // get name from ontopoly role field
        QueryProcessorIF proc = QueryUtils.getQueryProcessor(topic.getTopicMap());
        try {
          StringBuilder query = new StringBuilder();
          query.append("using on for i\"http://psi.ontopia.net/ontology/\" ");
          query.append("select $NAME from ");
          query.append("on:has-association-type(%AT% : on:association-type, $AF : on:association-field), ");
          query.append("on:has-association-field($AF : on:association-field, $RF : on:role-field), ");
          query.append("on:has-role-type($RF : on:role-field, %RT% : on:role-type), ");
          query.append("topic-name($RF, $TN), value($TN, $NAME) ");
          query.append("limit 1?");

          Map params = new HashMap(2);
          params.put("AT", topic);
          params.put("RT", nearRoleType);
          QueryResultIF qr = proc.execute(query.toString(), params);
          try {      
            if (qr.next()) {
              return (String)qr.getValue(0);
            }
          } finally {
            qr.close();
          }
        } catch (InvalidQueryException e) {
          throw new OntopiaRuntimeException(e);
        }
      }
      Collection scope = (nearRoleType != null ? Collections.singleton(nearRoleType) : Collections.EMPTY_SET);
      Function<TopicIF, String> strify = TopicStringifiers.getTopicNameStringifier(scope);
      return strify.apply(topic);
    }

    public void setSortKey(Object sortkey) {
      this.sortkey = sortkey;
    }

    @Override
    public int compareTo(Object o) {
      if (!(o instanceof Heading)) {
        return 0;
      }
      Heading other = (Heading)o;
      // prefer sort key over title
      Object tkey = (this.sortkey != null ? this.sortkey : this.getTitle());
      Object okey = (other.sortkey != null ? other.sortkey : other.getTitle());
      //! System.out.println("h:" + tkey + " <-> " + okey + " = " + ObjectUtils.compareIgnoreCase(tkey, okey));
      try {
        return compareHeadings(tkey, okey);
      } catch (ClassCastException e) {
        throw new OntopiaRuntimeException("Heading sort keys cannot be compared: " + tkey + " and " + okey);
      }
    }    
      
    public TopicIF getTopic() {
      return topic;
    }

    public TopicIF getNearRoleType() {
      return nearRoleType;
    }

    public boolean getIsTopicType() {
      return isTopicType;
    }
    
    public boolean getIsAssociationType() {
      return !isTopicType;
    }

    public int getArity() {
      return arity;
    }

    public List getChildren() {
      // only return maximum number of children if specified
      if (maxchildren >= 0 && children.size() > maxchildren) {
        return children.subList(0, maxchildren);
      } else {
        return children;
      }
    }

    public boolean getMoreChildren() {
      return (maxchildren >= 0 && children.size() > maxchildren);
    }
    
  }  

  // --- Association

  public class Association implements Comparable {
    private AssociationRoleIF role;
    private AssociationIF assoc;
    private boolean assoctype;
    private List roles;
    private TopicIF player;
    private TopicIF roleType;
    private Object sortkey;
    
    /**
     * Creates a new association.
     * @param assoctype Whether or not the parent heading represents
     * an association type.
     */
    private Association(AssociationRoleIF role, boolean assoctype) {
      this.role = role;
      this.assoc = role.getAssociation();
      this.assoctype = assoctype;

      if (getArity() == 2) {
        AssociationRoleIF other = getOtherRole(assoc, role);
        this.player = other.getPlayer();
        if (assoctype) {
          this.roleType = other.getType();
        }
      }
    }

    public int getArity() {
      if (assoctype) {
        return assoc.getRoles().size();
      } else {
        return 2;
      }
    }

    public TopicIF getPlayer() {
      return player;
    }

    public List getRoles() {
      if (!assoctype || roles != null) {
        return roles;
      }

      roles = new ArrayList(getArity());
      Iterator it = assoc.getRoles().iterator();
      while (it.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it.next();
        if (role == this.role) {
          continue;
        }

        roles.add(role);
      }
      return roles;
    }

    public Collection getScope() {
      return assoc.getScope();
    }

    public TopicIF getReifier() {
      return assoc.getReifier();
    }

    public TopicIF getRoleType() {
      return roleType;
    }

    public String getTitle() {
      Collection scope = (roleType != null ? Collections.singleton(roleType) : Collections.EMPTY_SET);
      Function<TopicIF, String> strify = TopicStringifiers.getTopicNameStringifier(scope);
      return strify.apply(player);
    }

    public void setSortKey(Object sortkey) {
      this.sortkey = sortkey;
    }

    @Override
    public int compareTo(Object o) {
      if (!(o instanceof Association)) {
        return 0;
      }
      Association other = (Association)o;
      Object tkey = (this.sortkey != null ? this.sortkey : this.getTitle());
      Object okey = (other.sortkey != null ? other.sortkey : other.getTitle());
      //! System.out.println("a:" + tkey + " <-> " + okey + " = " + ObjectUtils.compareIgnoreCase(tkey, okey));
      try {
        return compareChildren(tkey, okey);
      } catch (ClassCastException e) {
        throw new OntopiaRuntimeException("Child sort keys cannot be compared: " + tkey + " and " + okey);
      }
    }
  }
}
