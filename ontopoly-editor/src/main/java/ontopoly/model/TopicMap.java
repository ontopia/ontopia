/*
 * #!
 * Ontopoly Editor
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

package ontopoly.model;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.xml.XTMTopicMapReference;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.utils.OntopolyModelUtils;

/**
 * INTERNAL: Represents an Ontopoly topic map.
 */
public class TopicMap {
  protected static final String ON = "http://psi.ontopia.net/ontology/";
  protected static final String XTM = "http://www.topicmaps.org/xtm/1.0/core.xtm#";
  protected static final String TEST = "http://psi.example.org/test/";
  protected static final String TECH = "http://www.techquila.com/psi/hierarchy/#";
  protected static final String DC = "http://purl.org/dc/elements/1.1/";
  protected static final String XSD = "http://www.w3.org/2001/XMLSchema#";
  protected static final String TMDM = "http://psi.topicmaps.org/iso13250/model/";

  private static final String declarations = 
    "using xtm for i\"" + XTM + "\" "
    + "using on for i\"" + ON + "\" " 
    + "using test for i\"" + TEST + "\" "
    + "using tech for i\"" + TECH + "\" " 
    + "using tmdm for i\"" + TMDM + "\" " 
    + "using dc for i\"" + DC + "\" ";

  private TopicMapIF topicMapIF;
  private DeclarationContextIF dc;
  
  private String topicMapId;
  private QueryProcessorIF qp;

  private TopicIF defnametype; // cached here to avoid constant lookups

  public TopicMap(TopicMapIF topicMapIF, String topicMapId) {
    this.topicMapIF = topicMapIF;
    this.topicMapId = topicMapId;

    // initialize query wrapper
    initQueryContext();
  }

  private void initQueryContext() {
    try {
      this.qp = QueryUtils.getQueryProcessor(topicMapIF);
      // load built-in declarations
      this.dc = QueryUtils.parseDeclarations(topicMapIF, declarations);

      // load custom declarations
      TopicIF typeIf = OntopolyModelUtils.getTopicIF(this, PSI.ON_TOLOG_DECLARATIONS, false);
      if (typeIf != null) {
        TopicIF topicIf = getTopicMapIF().getReifier();
        if (topicIf != null) {
          OccurrenceIF occ = OntopolyModelUtils.findOccurrence(typeIf, topicIf);
          if (occ != null) {
            this.dc = QueryUtils.parseDeclarations(topicMapIF, declarations, this.dc);
          }
        }
      }
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public DeclarationContextIF getDeclarationContext() {
    return dc;
  }

  public QueryProcessorIF getQueryProcessor() {
    return qp;
  }
  
  public boolean containsOntology() {
    return (getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_ONTOLOGY_VERSION) != null);
  }

  public boolean isDeleteable() {
    return getTopicMapIF().getStore().getReference().getSource()
        .supportsDelete();
  }

  public TopicMapIF getTopicMapIF() {
    return topicMapIF;
  }

  public <T> QueryMapper<T> newQueryMapperNoWrap() {
    return new QueryMapper<T>(getQueryProcessor(), getDeclarationContext());
  }

  public <T> QueryMapper<T> newQueryMapper(final Class<T> type) {
    return new QueryMapper<T>(getQueryProcessor(), getDeclarationContext()) {
      @SuppressWarnings("unchecked")
      @Override
      protected T wrapValue(Object value) {
        // don't wrap if type is null
        if (type == null) {
          return (T)value;
        }
        
        // if (value == null) return null;
        try {      
          Constructor<T> constructor = getConstructor();
          if (constructor == null) {
            throw new OntopolyModelRuntimeException("Couldn't find constructor for the class: " + type);
          }
          return constructor.newInstance(new Object[] { value, TopicMap.this });
        } catch (Exception e) {
          throw new OntopolyModelRuntimeException(e);
        }
      }
      
      private Constructor<T> getConstructor() throws SecurityException, NoSuchMethodException {
        return type.getConstructor(TopicIF.class, TopicMap.class); 
      }
    };
  }

  public TopicIF getTopicIFById(String id) {
    // look up topic by object id or subject identifier
    TopicIF topic = (TopicIF)getTopicMapIF().getObjectById(id);
    if (topic == null) {
      try {
        return topicMapIF.getTopicBySubjectIdentifier(URILocator.create(id));
      } catch (Exception e) {
        return null;
      }
    }
    return topic;
  }

  public Topic getTopicById(String id) {
    TopicIF topic = getTopicIFById(id);
    return topic == null ? null : new Topic(topic, this);
  }

  public TopicType getTopicTypeById(String id) {
    TopicIF topic = getTopicIFById(id);
    return topic == null ? null : new TopicType(topic, this);
  }

  public Topic getReifier() {
    return new Topic(makeReifier(), this);
  }
  
  protected TopicIF makeReifier() {
    TopicIF reifier = getTopicMapIF().getReifier();
    if (reifier == null) {
      // IMPORTANT: check old-style reification
      TopicMapIF tm = getTopicMapIF();
      Iterator<LocatorIF> iter = tm.getItemIdentifiers().iterator();
      while (iter.hasNext()) {
        LocatorIF srcloc = iter.next();
        TopicIF _reifier = tm.getTopicBySubjectIdentifier(srcloc);
        if (_reifier != null) {
          if (reifier != null) {
            MergeUtils.mergeInto(reifier, _reifier);
          } else {
            reifier = _reifier;
          }
        }
      }
      if (reifier == null) {
        reifier = tm.getBuilder().makeTopic();
      }
      tm.setReifier(reifier);
      reifier.addType(tm.getTopicBySubjectIdentifier(PSI.ON_TOPIC_MAP));
    }
    return reifier;
  }

  /**
   * Returns the name of the topic map, or null if it has none.
   */
  public String getName() {
    TopicIF reifier = getTopicMapIF().getReifier();
    return reifier == null ? null : TopicStringifiers.toString(reifier);
  }

  /**
   * Returns the version of the Ontopoly meta-ontology used in this topic map.
   */
  public float getOntologyVersion() {
    TopicIF reifier = getTopicMapIF().getReifier();

    if (reifier == null) {
      // this is where issue 10 kicks in: if two topic maps have been
      // merged there is no reifier for the topic map any more. however,
      // the reifier will still be there, as a topic type type "topic map".
      // so we find that topic by simply querying for it, and then move on
      // from there.
      QueryMapper<TopicIF> qm = newQueryMapperNoWrap();
      reifier = qm.queryForObject("instance-of($T, on:topic-map)?");
    }
    
    TopicIF ontologyVersion = getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_ONTOLOGY_VERSION);
    Collection<TopicIF> scope = Collections.emptySet();    
    Collection<OccurrenceIF> occs = OntopolyModelUtils.findOccurrences(ontologyVersion, reifier, scope);
    if (occs.isEmpty()) {
      return 0;
    }
 
    String versionNumber = occs.iterator().next().getValue();
    try {
      return Float.parseFloat(versionNumber);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Returns the Id of the topic map reference in the topic map registry.
   */
  public String getId() {
    return topicMapId;
  }

  /**
   * Returns a list of the topic types that is not a system topic type.
   */
  public List<TopicType> getTopicTypes() {
    String query = "select $type from instance-of($type, on:topic-type) order by $type?";

    QueryMapper<TopicType> qm = newQueryMapper(TopicType.class);    
    return qm.queryForList(query);
  }

  /**
   * Returns a list of the topic types that is not a system topic type.
   */
  public List<TopicType> getRootTopicTypes() {
    String query = "select $type from instance-of($type, on:topic-type), " 
      + "not(xtm:superclass-subclass($type : xtm:subclass, $SUP : xtm:superclass)) "
      + "order by $type?";

    QueryMapper<TopicType> qm = newQueryMapper(TopicType.class);    
    return qm.queryForList(query);
  }

  /**
   * Returns a list of the topic types that is not a system topic type.
   */
  public List<TopicType> getTopicTypesWithLargeInstanceSets() {
    String query = "select $type from on:has-large-instance-set($type : on:topic-type)?";

    QueryMapper<TopicType> qm = newQueryMapper(TopicType.class);
    return qm.queryForList(query);
  }

//  public List<OccurrenceType> getOccurrenceTypes() {
//    String query = "select $type from "
//      + "instance-of($type, on:occurrence-type)"
//      + " order by $type?";
//
//    QueryMapper<OccurrenceType> qm = newQueryMapper(OccurrenceType.class);
//    return qm.queryForList(query);
//  }

  public List<OccurrenceField> getOccurrenceFields() {
    String query = "select $field from direct-instance-of($field, on:occurrence-field) order by $field?";

    QueryMapper<OccurrenceField> qm = newQueryMapper(OccurrenceField.class);
    return qm.queryForList(query);
  }

//  public List<AssociationType> getAssociationTypes() {
//    String query = "select $type from "
//      + "instance-of($type, on:association-type)"
//      + " order by $type?";
//
//    QueryMapper<AssociationType> qm = newQueryMapper(AssociationType.class);
//    return qm.queryForList(query);
//  }

//  public List<RoleType> getRoleTypes(boolean includeSystemTopics) {
//    String query = "";
//    if (includeSystemTopics)
//      query = "select $type from direct-instance-of($type, on:role-type) order by $type?";
//    else
//      query = "select $type from "
//        + "direct-instance-of($type, on:role-type)"
//        + ", not(instance-of($type, on:system-topic))"
//        + " order by $type?";
//
//    QueryMapper<RoleType> qm = newQueryMapper(RoleType.class);
//    return qm.queryForList(query);
//  }

  public List<RoleField> getRoleFields() {
    String query = "select $field from direct-instance-of($field, on:role-field) order by $field?";

    QueryMapper<RoleField> qm = newQueryMapper(RoleField.class);
    return qm.queryForList(query);
  }

//  public List<NameType> getNameTypes() {
//    String query = "select $type from direct-instance-of($type, on:name-type) order by $type?";
//
//    QueryMapper<NameType> qm = newQueryMapper(NameType.class);
//    return qm.queryForList(query);
//  }

  public List<NameField> getNameFields() {
    String query = "select $field from direct-instance-of($field, on:name-field) order by $field?";

    QueryMapper<NameField> qm = newQueryMapper(NameField.class);
    return qm.queryForList(query);
  }

//  public List<IdentityType> getIdentityTypes() {
//    String query = "select $type from instance-of($type, on:identity-type) order by $type?";
//
//    QueryMapper<IdentityType> qm = newQueryMapper(IdentityType.class);
//    return qm.queryForList(query);
//  }

  public List<IdentityField> getIdentityFields() {
    String query = "select $field from instance-of($field, on:identity-field) order by $field?";

    QueryMapper<IdentityField> qm = newQueryMapper(IdentityField.class);
    return qm.queryForList(query);
  }

  public List<QueryField> getQueryFields() {
    String query = "select $field from instance-of($field, on:query-field) order by $field?";

    QueryMapper<QueryField> qm = newQueryMapper(QueryField.class);
    return qm.queryForList(query);
  }

  public boolean isSaveable() {
    TopicMapReferenceIF ref = getTopicMapIF().getStore().getReference();
    return (ref instanceof XTMTopicMapReference);
  }

  public void save() {
    TopicMapReferenceIF ref = getTopicMapIF().getStore().getReference();
    if (ref instanceof XTMTopicMapReference) {
      try {
        ((XTMTopicMapReference) ref).save();
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }

  protected TopicIF createNamedTopic(String name, TopicIF type) {
    TopicMapBuilderIF builder = getTopicMapIF().getBuilder();
    TopicIF topic = builder.makeTopic(type);

    if (name != null && !name.equals("")) {
      builder.makeTopicName(topic, name);
    }

    return topic;
  }

  public TopicType createTopicType(String name) {
    TopicIF topicTypeIf= OntopolyModelUtils.getTopicIF(this, PSI.ON, "topic-type");
    TopicIF topicIf = createNamedTopic(name, topicTypeIf);

    TopicType topicType = new TopicType(topicIf, this);
    topicType.addField(getDefaultNameField());
    return topicType;
  }

  protected NameField getDefaultNameField() {
    TopicMap tm = this;
    NameType nameType = new NameType(OntopolyModelUtils.getTopicIF(tm, PSI.TMDM_TOPIC_NAME), tm);
    Collection<NameField> nameFields = nameType.getDeclaredByFields();
    return CollectionUtils.getFirstElement(nameFields);
  }
  
//  public IdentityField getIdentityField(IdentityType identityType) {
//    String query = "select $FD from on:has-identity-type(%type% : on:identity-type, $FD : on:identity-field) limit 1?";
//    Map<String,TopicIF> params = Collections.singletonMap("type", identityType.getTopicIF());
//
//    QueryMapper<TopicIF> qm = newQueryMapperNoWrap();
//    TopicIF fieldTopic = qm.queryForObject(query, params);
//    if (fieldTopic == null) 
//      throw new OntopolyModelRuntimeException("Could not find identity field for " + identityType);
//
//    return new IdentityField(fieldTopic, this, identityType);
//  }

  public NameType createNameType(String name) {
    TopicMap tm = this;
    TopicMapBuilderIF builder = tm.getTopicMapIF().getBuilder();

    // create name type
    TopicIF nameTypeTopic = createNamedTopic(name, OntopolyModelUtils.getTopicIF(tm, PSI.ON_NAME_TYPE));
    NameType nameType = new NameType(nameTypeTopic, tm);

    // create name field
    TopicIF nameFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-field");
    TopicIF nameFieldTopic = builder.makeTopic(nameFieldType);
    
    // on:has-name-type($TT : on:name-type, $FD : on:name-field)
    final TopicIF HAS_NAME_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-name-type");
    final TopicIF NAME_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-type");
    final TopicIF NAME_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_NAME_TYPE, 
        nameTypeTopic, NAME_TYPE,
        nameFieldTopic, NAME_FIELD);

    return nameType;
  }

//  public NameField getNameField(NameType nameType) {
//    String query = "select $FD from on:has-name-type(%type% : on:name-type, $FD : on:name-field) limit 1?";
//    Map<String,TopicIF> params = Collections.singletonMap("type", nameType.getTopicIF());
//
//    QueryMapper<TopicIF> qm = newQueryMapperNoWrap();
//    TopicIF fieldTopic = qm.queryForObject(query, params);
//    if (fieldTopic == null) 
//      throw new OntopolyModelRuntimeException("Could not find name field for " + nameType);
//
//    return new NameField(fieldTopic, this, nameType);
//  }

  public OccurrenceType createOccurrenceType(String name) {
    TopicMap tm = this;
    TopicMapBuilderIF builder = tm.getTopicMapIF().getBuilder();

    // create occurrence type
    TopicIF occurrenceTypeTopic = createNamedTopic(name, OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-type"));
    OccurrenceType occurrenceType = new OccurrenceType(occurrenceTypeTopic, tm);

    // create occurrence field
    TopicIF occurrenceFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-field");
    TopicIF occurrenceFieldTopic = builder.makeTopic(occurrenceFieldType);
    
    // on:has-occurrence-type($TT : on:occurrence-type, $FD : on:occurrence-field)
    final TopicIF HAS_OCCURRENCE_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-occurrence-type");
    final TopicIF OCCURRENCE_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-type");
    final TopicIF OCCURRENCE_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_OCCURRENCE_TYPE, 
        occurrenceTypeTopic, OCCURRENCE_TYPE,
        occurrenceFieldTopic, OCCURRENCE_FIELD);

    return occurrenceType;
  }

//  public OccurrenceField getOccurrenceField(OccurrenceType occurrenceType) {
//    String query = "select $FD from on:has-occurrence-type(%type% : on:occurrence-type, $FD : on:occurrence-field) limit 1?";
//    Map<String,TopicIF> params = Collections.singletonMap("type", occurrenceType.getTopicIF());
//
//    QueryMapper<TopicIF> qm = newQueryMapperNoWrap();
//    TopicIF fieldTopic = qm.queryForObject(query, params);
//    if (fieldTopic == null) 
//      throw new OntopolyModelRuntimeException("Could not find occurrence field for " + occurrenceType);
//
//    return new OccurrenceField(fieldTopic, this, occurrenceType);
//  }

  public AssociationType createAssociationType(String name) {
    TopicMap tm = this;
    TopicMapBuilderIF builder = tm.getTopicMapIF().getBuilder();

    // create association type
    TopicIF associationTypeTopic = createNamedTopic(name, OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type"));
    AssociationType associationType = new AssociationType(associationTypeTopic, tm);

    // create association field
    TopicIF associationFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-field");
    TopicIF associationFieldTopic = builder.makeTopic(associationFieldType);
    
    // on:has-association-type($TT : on:association-type, $FD : on:association-field)
    final TopicIF HAS_ASSOCIATION_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-association-type");
    final TopicIF ASSOCIATION_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type");
    final TopicIF ASSOCIATION_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_ASSOCIATION_TYPE, 
        associationType.getTopicIF(), ASSOCIATION_TYPE,
        associationFieldTopic, ASSOCIATION_FIELD);

    final TopicIF HAS_ASSOCIATION_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-association-field");
    final TopicIF ROLE_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "role-field");
    TopicIF roleFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "role-field");

    // create first role field
    TopicIF roleFieldTopic1 = builder.makeTopic(roleFieldType);
    
    // on:has-association-field($AF : on:association-field, $FD : on:role-field)
    OntopolyModelUtils.makeBinaryAssociation(HAS_ASSOCIATION_FIELD, 
                                             roleFieldTopic1, ROLE_FIELD,
                                             associationFieldTopic, ASSOCIATION_FIELD);

    // create second role field
    TopicIF roleFieldTopic2 = builder.makeTopic(roleFieldType);
    
    // on:has-association-field($AF : on:association-field, $FD : on:role-field)
    OntopolyModelUtils.makeBinaryAssociation(HAS_ASSOCIATION_FIELD, 
                                             roleFieldTopic2, ROLE_FIELD,
                                             associationFieldTopic, ASSOCIATION_FIELD);

    return associationType;
  }

//  public AssociationField getAssociationField(AssociationType atype) {
//
//    String query = "select $AF from "
//      + "on:has-association-type(%atype% : on:association-type, $AF : on:association-field) limit 1?";
//    Map<String,TopicIF> params = Collections.singletonMap("atype", atype.getTopicIF());
//
//    QueryMapper<TopicIF> qm = newQueryMapperNoWrap();
//    TopicIF fieldTopic = qm.queryForObject(query, params);
//    if (fieldTopic == null) 
//      throw new OntopolyModelRuntimeException("Could not find association field for " + atype);
//    
//    return new AssociationField(fieldTopic, this, atype);
//  }

  public RoleType createRoleType(String name) {
    TopicIF type = OntopolyModelUtils.getTopicIF(this, PSI.ON, "role-type");
    return new RoleType(createNamedTopic(name, type), this);
  }

//  public RoleField getRoleField(final AssociationType atype, final RoleType rtype) {
//
//    String query = "select $AF, $RF from "
//      + "on:has-association-type(%atype% : on:association-type, $AF : on:association-field), " 
//      + "on:has-association-field($AF : on:association-field, $RF : on:role-field), " 
//      + "on:has-role-type($RF : on:role-field, %rtype% : on:role-type) limit 1?";
//    Map<String,TopicIF> params = new HashMap<String,TopicIF>(2);
//    params.put("atype", atype.getTopicIF());
//    params.put("rtype", rtype.getTopicIF());
//    
//    QueryMapper<RoleField> qm = newQueryMapperNoWrap();
//    RoleField roleField = qm.queryForObject(query,
//        new RowMapperIF<RoleField>() {
//          public RoleField mapRow(QueryResultIF result, int rowno) {
//            TopicIF associationFieldTopic = (TopicIF)result.getValue(0);
//            TopicIF roleFieldTopic = (TopicIF)result.getValue(1);
//            return new RoleField(roleFieldTopic, TopicMap.this, rtype, new AssociationField(associationFieldTopic, TopicMap.this, atype));
//          }
//        }, params);
//
//    if (roleField == null) 
//      throw new OntopolyModelRuntimeException("Could not find field for " + atype + " and " + rtype);
//
//    return roleField;
//  }

  /**
   * Returns the topics that matches the given search term. Only topics of
   * allowed player types are returned.
   * 
   * @return a collection of Topic objects
   */
  public List<Topic> searchAll(String searchTerm) {
    String query = "select $topic, $score from "
        + "topic-name($topic, $tn), value-like($tn, %searchTerm%, $score) "
        + "order by $score desc, $topic?";

    Map<String,String> params = new HashMap<String,String>();
    params.put("searchTerm", searchTerm);

    QueryMapper<Topic> qm = newQueryMapperNoWrap();
    List<Topic> rows = qm.queryForList(query, params);

    Iterator<Topic> it = rows.iterator();
    List<Topic> results = new ArrayList<Topic>(rows.size());
    Collection<TopicIF> duplicateChecks = new HashSet<TopicIF>(rows.size());
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      if (duplicateChecks.contains(topic)) {
        continue; // avoid duplicates
      }
      results.add(new Topic(topic, this));
      duplicateChecks.add(topic);
    }

    return results;
  }

  /**
   * Package-internal method to get the name of a topic. Introduced in
   * Ontopia 5.1 for field definitions. Not used everywhere because I
   * don't understand the code well enough to do that, and anyway
   * things appear to work properly elsewhere. Should probably use this
   * in Topic.getName(), though.
   */
  protected String getTopicName(TopicIF topic, AbstractTypingTopic fallback) {
    if (defnametype == null) {
      defnametype = OntopolyModelUtils.getTopicIF(this, PSI.TMDM_TOPIC_NAME);
    }

    int score = -10;
    TopicNameIF best = null;
    for (TopicNameIF name : topic.getTopicNames()) {
      int points = 0;
      if (name.getType() == defnametype) {
        points += 10;
      }
      points -= name.getScope().size();
      if (points > score) {
      score = points;
      best = name;
      }
    }
    if (best != null) {
      return best.getValue();
    }

    return (fallback == null ? null : fallback.getName());
  }
}
