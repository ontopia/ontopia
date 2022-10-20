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
package ontopoly.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.model.FieldAssignment;
import ontopoly.model.TopicMap;
import org.apache.commons.lang3.StringUtils;

public class Upgrade_1_9 extends UpgradeBase {
  
  Upgrade_1_9(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuilder sb) {
    sb.append("[on:system-topic  : on:topic-type on:system-topic]\n");
    sb.append("[on:public-system-topic  : on:topic-type on:system-topic = \"Public system topic\"]\n");
    sb.append("xtm:superclass-subclass(on:system-topic : xtm:superclass, on:public-system-topic : xtm:subclass)\n");

    sb.append("[xtm:superclass-subclass @\"http://psi.ontopia.net/ontology/superclass-subclass\"]\n"); // 91
    sb.append("[xtm:superclass @\"http://psi.ontopia.net/ontology/superclass\"]\n"); // 91
    sb.append("[xtm:subclass @\"http://psi.ontopia.net/ontology/subclass\"]\n"); // 91

    sb.append("[on:auto-complete  : on:interface-control = \"Auto-complete\"]\n");
    sb.append("[on:item-identifier  : on:identity-field on:system-topic = \"Item identifier\"]\n");

    sb.append("[on:description @\"http://purl.org/dc/elements/1.1/description\"]\n");
    sb.append("[on:creator @\"http://purl.org/dc/elements/1.1/creator\"]\n");
    sb.append("[on:version @\"http://purl.org/dc/elements/1.1/version\"]\n");
    
    // new topics                    
    sb.append("[on:has-field : on:association-type on:system-topic = \"Has field\"]\n");          

    sb.append("[on:field-value-order : on:occurrence-type on:system-topic = \"Field value order\"]\n");          

    sb.append("[on:field-owner : on:role-type on:system-topic = \"Field owner\"]\n");          

    sb.append("[on:field-definition : on:topic-type on:role-type on:system-topic = \"Field definition\"]\n");
    sb.append("on:is-abstract(on:field-definition : on:topic-type)\n");
    
    sb.append("[on:identity-field-temp : on:topic-type on:role-type on:system-topic = \"Identity field temp\"]\n");
    sb.append("[on:name-field : on:topic-type on:role-type on:system-topic = \"Name field\"]\n");
    sb.append("[on:occurrence-field : on:topic-type on:role-type on:system-topic = \"Occurrence field\"]\n");
    // sb.append("[on:association-field : on:topic-type on:role-type on:system-topic = \"Association field\"]\n");
    sb.append("[on:role-field : on:topic-type on:role-type on:system-topic = \"Role field\"]\n");

    sb.append("[on:has-association-field : on:association-type on:system-topic = \"Has association field\"]\n");
    sb.append("[on:association-field : on:topic-type on:role-type on:system-topic = \"Association field\"]\n");

    sb.append("xtm:superclass-subclass(on:field-definition : xtm:superclass, on:identity-field-temp : xtm:subclass)\n");
    sb.append("xtm:superclass-subclass(on:field-definition : xtm:superclass, on:name-field : xtm:subclass)\n");
    sb.append("xtm:superclass-subclass(on:field-definition : xtm:superclass, on:occurrence-field : xtm:subclass)\n");
    //sb.append("xtm:superclass-subclass(on:field-definition : xtm:superclass, on:association-field : xtm:subclass)\n");
    sb.append("xtm:superclass-subclass(on:field-definition : xtm:superclass, on:role-field : xtm:subclass)\n");
    
    //sb.append("[on:has-valid-roletype : on:association-type on:system-topic = \"Has valid role type\"]\n");
              
    sb.append("[on:has-association-type : on:association-type on:system-topic = \"Has association type\"]\n");
    sb.append("[on:has-role-type : on:association-type on:system-topic = \"Has role type\"]\n");
    sb.append("[on:has-identity-type : on:association-type on:system-topic = \"Has identity type\"]\n");
    sb.append("[on:has-name-type : on:association-type on:system-topic = \"Has name type\"]\n");
    sb.append("[on:has-occurrence-type : on:association-type on:system-topic = \"Has occurrence type\"]\n");
    
    sb.append("[on:use-interface-control : on:association-type on:system-topic = \"Use interface control\"]\n");
    
    sb.append("[on:min-cardinality : on:occurrence-type on:system-topic = \"Minimum cardinality\"]\n");
    sb.append("[on:max-cardinality : on:occurrence-type on:system-topic = \"Maximum cardinality\"]\n");
    sb.append("{on:cardinality-0-1, on:min-cardinality, [[0]]}\n");
    sb.append("{on:cardinality-0-M, on:min-cardinality, [[0]]}\n");
    sb.append("{on:cardinality-1-1, on:min-cardinality, [[1]]}\n");
    sb.append("{on:cardinality-1-M, on:min-cardinality, [[1]]}\n");
    sb.append("{on:cardinality-0-1, on:max-cardinality, [[1]]}\n");
    sb.append("{on:cardinality-1-1, on:max-cardinality, [[1]]}\n");

    sb.append("[on:is-hidden-type : on:association-type on:system-topic = \"Is hidden type\"]\n");
    sb.append("[on:is-readonly-type : on:association-type on:system-topic = \"Is read-only type\"]\n");

//    sb.append("[on:use-as-role-type : on:association-type on:system-topic = \"Use as role type\"]\n");          
    
    sb.append("[on:use-view-mode : on:association-type on:system-topic = \"Use View mode\"]\n");
    sb.append("[on:view-mode : on:topic-type on:role-type on:system-topic = \"View mode\"]\n");

    sb.append("[on:view-mode-readonly : on:view-mode on:system-topic = \"Read-only\"]");
    sb.append("[on:view-mode-hidden : on:view-mode on:system-topic = \"Hidden\"]");
    sb.append("[on:view-mode-not-traversable : on:view-mode on:system-topic = \"Not traversable\"]");
    sb.append("[on:view-mode-embedded : on:view-mode on:system-topic = \"Embedded\"]");

    sb.append("[on:use-value-view : on:association-type on:system-topic = \"Use value view\"]");
    sb.append("[on:parent-view : on:role-type on:system-topic = \"Parent view\"]");
    sb.append("[on:child-view : on:role-type on:system-topic = \"Child view\"]");

    sb.append("[on:use-edit-mode : on:association-type on:system-topic = \"Use edit mode\"]\n");
    sb.append("[on:edit-mode : on:topic-type on:role-type on:system-topic = \"Edit mode\"]\n");          
    sb.append("[on:edit-mode-existing-values-only : on:edit-mode on:system-topic = \"Existing values only\"]");
    sb.append("[on:edit-mode-new-values-only : on:edit-mode on:system-topic = \"New values only\"]");
    sb.append("[on:edit-mode-owned-values : on:edit-mode on:system-topic = \"Owned values\"]");
    sb.append("[on:edit-mode-normal : on:edit-mode on:system-topic = \"Normal\"]");
    sb.append("[on:edit-mode-no-edit : on:edit-mode on:system-topic = \"No edit\"]");

    sb.append("[on:use-create-action : on:association-type on:system-topic = \"Use create action\"]\n");
    sb.append("[on:create-action : on:topic-type on:role-type on:system-topic = \"Create action\"]\n");
    sb.append("[on:create-action-popup : on:create-action on:system-topic = \"Edit new topic in popup window\"]");
    sb.append("[on:create-action-navigate : on:create-action on:system-topic = \"Go to new topic\"]");
    sb.append("[on:create-action-none : on:create-action on:system-topic = \"None\"]");

    sb.append("[on:is-sortable-field : on:association-type on:system-topic = \"Is sortable\"]\n");

    sb.append("[on:ted-ontology-version : on:occurrence-type]\n");
    sb.append("[on:topic-map : on:topic-type on:system-topic = \"Topic map\"]\n");

    sb.append("[on:allowed-players-query : on:occurrence-type on:system-topic = \"Players query\"]\n");
    sb.append("[on:allowed-players-search-query : on:occurrence-type on:system-topic = \"Players search query\"]\n");
    sb.append("[on:allowed-players-types-query : on:occurrence-type on:system-topic = \"Players types query\"]\n");
    sb.append("[on:tolog-declarations : on:occurrence-type on:system-topic = \"Tolog declarations\"]\n");
    
    //! sb.append("[on:dependent-objects-query : on:occurrence-type on:system-topic = \"Dependent objects query\"]\n");

    /* ONTOLOGIES */
    sb.append("[on:ontology : on:topic-type on:role-type on:system-topic = \"Ontology\"]\n");          
    sb.append("[on:ontopoly-topicmaps-ontology : on:ontology on:system-topic = \"Topic Maps ontology\"]\n");
    sb.append("[on:ontopoly-ui-ontology : on:ontology on:system-topic = \"User-interface ontology\"]\n");
    
//    sb.append("[on:member-of-ontology : on:association-type on:system-topic = \"Part of ontology\"]\n");          
//    sb.append("[on:ontology-member : on:role-type on:system-topic = \"Ontology member\"]\n");          
    
    /* VIEWS */
//    sb.append("[on:fields-view-owner : on:role-type on:system-topic = \"Fields view owner\"]\n");          
//    sb.append("[on:fields-view : on:topic-type on:role-type on:system-topic = \"Fields view\"]\n");          
//    sb.append("[on:fields-declaration-view : on:fields-view on:system-topic = \"Fields declaration view\"]\n");          
//    sb.append("on:owns-fields-view(on:topic-type : on:fields-view-owner, on:fields-declaration-view : on:fields-view)\n");          
//    sb.append("on:owns-fields-view(on:fields-view : on:fields-view-owner, on:fields-declaration-view : on:fields-view)\n");          

    /* VIEWS */
    sb.append("[on:fields-view : on:topic-type on:role-type on:system-topic = \"Fields view\"]\n");          
    sb.append("[on:field-in-view : on:association-type on:system-topic = \"Field in view\"]\n");                    
    
    sb.append("[on:default-fields-view : on:fields-view on:system-topic = \"Default\"]\n");          
    sb.append("[on:advanced-fields-view : on:fields-view on:system-topic = \"Advanced\"]\n");          
    sb.append("[on:declared-fields-view : on:fields-view on:system-topic = \"Declared fields\"]\n");          
    
    sb.append("[on:association-field-embedded-view : on:fields-view on:system-topic = \"Association field view\"]\n");          
    sb.append("[on:role-field-embedded-view : on:fields-view on:system-topic = \"Role field view\"]\n");          
    sb.append("[on:name-field-embedded-view : on:fields-view on:system-topic = \"Name field view\"]\n");          
    sb.append("[on:occurrence-field-embedded-view : on:fields-view on:system-topic = \"Occurrence field view\"]\n");          
    sb.append("[on:identity-field-embedded-view : on:fields-view on:system-topic = \"Identity field view\"]\n");          

    sb.append("[on:is-hidden-view : on:association-type on:system-topic = \"Is hidden view\"]\n");
    sb.append("on:is-hidden-view(on:declared-fields-view : on:fields-view)\n");          
    
    sb.append("[on:is-embedded-view : on:association-type on:system-topic = \"Is embedded view\"]\n");                    
    sb.append("on:is-embedded-view(on:association-field-embedded-view : on:fields-view)\n");          
    sb.append("on:is-embedded-view(on:role-field-embedded-view : on:fields-view)\n");          
    sb.append("on:is-embedded-view(on:name-field-embedded-view : on:fields-view)\n");          
    sb.append("on:is-embedded-view(on:occurrence-field-embedded-view : on:fields-view)\n");          
    sb.append("on:is-embedded-view(on:identity-field-embedded-view : on:fields-view)\n");
    
    /* HIERARCHIES */
    sb.append("[on:superordinate-role-type : on:association-type on:system-topic = \"Superordinate role\"]\n");                    
    sb.append("[on:subordinate-role-type : on:association-type on:system-topic = \"Subordinate role\"]\n");                    

    //! sb.append("[on:hierarchy : on:topic-type on:system-topic = \"Hierarchy\"]\n");          
    sb.append("[on:hierarchy-definition-query : on:occurrence-type on:system-topic = \"Hierarchy definition query\"]\n");
    
    /* DATATYPES */
    sb.append("[on:datatype-locator : on:occurrence-type on:system-topic = \"Datatype locator\"]\n");
    sb.append("[xsd:date : on:system-topic]\n");
    sb.append("[xsd:dateTime : on:system-topic]\n");
    sb.append("[xsd:decimal : on:system-topic]\n");
    sb.append("[xsd:string : on:system-topic]\n");
    sb.append("[xsd:anyURI : on:system-topic]\n");
    sb.append("[on:datatype-date @\"http://www.w3.org/2001/XMLSchema#date\"]\n");
    sb.append("[on:datatype-datetime @\"http://www.w3.org/2001/XMLSchema#dateTime\"]\n");          
    sb.append("[on:datatype-number @\"http://www.w3.org/2001/XMLSchema#decimal\"]\n");          
    sb.append("[on:datatype-string @\"http://www.w3.org/2001/XMLSchema#string\"]\n");          
    sb.append("[on:datatype-uri @\"http://www.w3.org/2001/XMLSchema#anyURI\"]\n");          
    sb.append("{on:datatype-date, on:datatype-locator, \"http://www.w3.org/2001/XMLSchema#date\"}\n");
    sb.append("{on:datatype-datetime, on:datatype-locator, \"http://www.w3.org/2001/XMLSchema#dateTime\"}\n");
    sb.append("{on:datatype-number, on:datatype-locator, \"http://www.w3.org/2001/XMLSchema#decimal\"}\n");
    sb.append("{on:datatype-string, on:datatype-locator, \"http://www.w3.org/2001/XMLSchema#string\"}\n");
    sb.append("{on:datatype-uri, on:datatype-locator, \"http://www.w3.org/2001/XMLSchema#anyURI\"}\n");
    sb.append("{on:datatype-html, on:datatype-locator, \"http://psi.ontopia.net/ontology/datatype-html\"}\n");
    sb.append("{on:datatype-image, on:datatype-locator, \"http://psi.ontopia.net/ontology/datatype-image\"}\n");    
  }
  
  @Override
  protected void transform() throws InvalidQueryException {

    TopicIF topicmapType = getTopic(topicmap, base_on, "topic-map");
    TopicIF reifier = topicmap.getReifier();
    reifier.addType(topicmapType);
    
    // on:ontology-topic-type -> on:ontology-type
    renameSubjectIdentifier(topicmap, base_on, "ontology-topic-type", "ontology-type");

    // on:identity-field -> on:identity-type and introduce new on:identity-field
    renameSubjectIdentifier(topicmap, base_on, "identity-field", "identity-type");
    
    renameTopics(topicmap, "Identity field", "Identity type");          
    renameSubjectIdentifier(topicmap, base_on, "identity-field-temp", "identity-field");
    renameTopics(topicmap, "Identity field temp", "Identity field");          
    
    // on:field no longer instance of on:field-type
    TopicIF fieldTopic = getTopic(topicmap, base_on, "field");
    TopicIF fieldType = getTopic(topicmap, base_on, "field-type");
    fieldTopic.removeType(fieldType);
    
    // remove topics
    removeTopic(topicmap, base_on, "pattern");
    
    // make ontology topics instances of on:system-topic
    TopicIF systemTopic = getTopic(topicmap, base_on, "system-topic");
    getTopic(topicmap, base_on, "datatype-html").addType(systemTopic);
    getTopic(topicmap, base_on, "datatype-image").addType(systemTopic);
    getTopic(topicmap, base_on, "cardinality-1-1").addType(systemTopic);
    getTopic(topicmap, base_on, "cardinality-0-1").addType(systemTopic);
    getTopic(topicmap, base_on, "cardinality-1-M").addType(systemTopic);
    getTopic(topicmap, base_on, "cardinality-0-M").addType(systemTopic);
    getTopic(topicmap, base_on, "subject-identifier").addType(systemTopic);
    getTopic(topicmap, base_on, "subject-locator").addType(systemTopic);
    getTopic(topicmap, base_on, "auto-complete").addType(systemTopic);
    getTopic(topicmap, base_on, "browse-dialog").addType(systemTopic);
    getTopic(topicmap, base_on, "search-dialog").addType(systemTopic);
    getTopic(topicmap, base_on, "drop-down-list").addType(systemTopic);

    // make types of ontology topics explicit          
    TopicIF topicType = getTopic(topicmap, base_on, "topic-type");
    getTopic(topicmap, base_on, "cardinality").addType(topicType);
    getTopic(topicmap, base_on, "datatype").addType(topicType);
    getTopic(topicmap, base_on, "interface-control").addType(topicType);
    getTopic(topicmap, base_on, "identity-type").addType(topicType);

    getTopic(topicmap, base_on, "role-type").addType(topicType);
    getTopic(topicmap, base_on, "association-type").addType(topicType);
    getTopic(topicmap, base_on, "name-type").addType(topicType);
    getTopic(topicmap, base_on, "occurrence-type").addType(topicType);
    getTopic(topicmap, base_on, "topic-type").addType(topicType);

    getTopic(topicmap, base_tech, "#hierarchical-relation-type").addType(topicType);
    getTopic(topicmap, base_tech, "#subordinate-role-type").addType(topicType);
    getTopic(topicmap, base_tech, "#superordinate-role-type").addType(topicType);

    TopicIF roleType = getTopic(topicmap, base_on, "role-type");
    getTopic(topicmap, base_on, "cardinality").addType(roleType);
    getTopic(topicmap, base_on, "datatype").addType(roleType);
    getTopic(topicmap, base_on, "interface-control").addType(roleType);
    getTopic(topicmap, base_on, "identity-type").addType(roleType);

    getTopic(topicmap, base_on, "role-type").addType(roleType);
    getTopic(topicmap, base_on, "association-type").addType(roleType);
    getTopic(topicmap, base_on, "name-type").addType(roleType);
    getTopic(topicmap, base_on, "occurrence-type").addType(roleType);
    getTopic(topicmap, base_on, "ontology-type").addType(roleType);
    getTopic(topicmap, base_on, "topic-type").addType(roleType);
    
    // ISSUE: what to do with xtm:sort topic?
    
    // on:is-hidden
    translateAssociations("on:is-hidden",      new String[] { "on:topic-type" },           
                          "is-hidden-type", new String[] { "ontology-type" }, 
                          topicmap, base_on, qp, dc);
    removeAssociations("on:is-hidden", new String[] { "on:topic-type" }, qp, dc);
    removeAssociations("on:is-hidden", new String[] { "on:ontology-type" }, qp, dc);
    
    
    translateAssociations("on:is-hidden",      new String[] { "on:field-type" },           
                          "is-hidden-type", new String[] { "ontology-type" }, 
                          topicmap, base_on, qp, dc);
    removeAssociations("on:is-hidden", new String[] { "on:field-type" }, qp, dc);
    removeAssociations("on:is-hidden", new String[] { "on:ontology-type" }, qp, dc);
    
    // on:is-readonly
    translateAssociations("on:is-readonly",      new String[] { "on:topic-type" },           
                          "is-readonly-type", new String[] { "ontology-type" }, 
                          topicmap, base_on, qp, dc);
    removeAssociations("on:is-readonly", new String[] { "on:topic-type" }, qp, dc);
              
    translateAssociations("on:is-readonly",      new String[] { "on:field-type" },           
                          "is-readonly-type", new String[] { "ontology-type" }, 
                          topicmap, base_on, qp, dc);
    removeAssociations("on:is-readonly", new String[] { "on:field-type" }, qp, dc);
    
    // on:has-role
//    translateAssociations("on:has-role",           new String[] { "on:association-type", "on:role-type" },           
//                          "has-valid-roletype", new String[] { "association-type", "role-type" }, 
//                          topicmap, base_on, qp, dc);
    removeAssociations("on:has-role", new String[] { "on:association-type", "on:role-type" }, qp, dc);
    
    
    // create association fields
    TopicIF tt_association_field = getTopic(topicmap, base_on, "association-field");
    TopicIF tt_role_field = getTopic(topicmap, base_on, "role-field");
    
    // track field definitions with cardinalities
    Set<TopicIF> assignedCardinality = new HashSet<TopicIF>();
    
    // retrieve all association fields
    List<TopicIF[]> afields = new ArrayList<TopicIF[]>();
    QueryResultIF qr = null;
    try {
      qr =  qp.execute("select $TT, $AT, $RT from on:has-field($TT : on:topic-type, $AT : on:field, $RT : on:role-type)?", dc);
      while (qr.next()) {
        afields.add(new TopicIF[] { (TopicIF)qr.getValue(0), (TopicIF)qr.getValue(1), (TopicIF)qr.getValue(2) });              
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
    removeAssociations("on:has-field", new String[] { "on:topic-type", "on:field", "on:role-type" }, qp, dc);
    
    // loop over the fields so that we can execute other queries as we go
    Iterator<TopicIF[]> afiter = afields.iterator();
    while (afiter.hasNext()) {
      TopicIF[] fen = afiter.next();
      
      TopicIF tt = fen[0];
      TopicIF at = fen[1];
      TopicIF rt = fen[2];
        
      TopicMapBuilderIF builder = topicmap.getBuilder();
      
      TopicIF associationField = null;
      boolean existingAField = false;
      try {
        Map<String,TopicIF> params = Collections.singletonMap("AT", at);
        qr =  qp.execute("select $AF from " 
            + "on:has-association-type(%AT% : on:association-type, $AF : on:association-field)?",
            params, dc);
        if (qr.next()) {
          existingAField = true;
          associationField = (TopicIF)qr.getValue(0);
        } else {
          associationField = builder.makeTopic(tt_association_field);
        }
      } finally {
        if (qr != null) {
          qr.close();
        }
      }

      if (!existingAField) {
        // on:has-association-type(%new-association-field% : on:association-field, $AT : on:association-type)
        AssociationIF a3 = builder.makeAssociation(getTopic(topicmap, base_on, "has-association-type"));
        builder.makeAssociationRole(a3, getTopic(topicmap, base_on, "association-field"), associationField);
        builder.makeAssociationRole(a3, getTopic(topicmap, base_on, "association-type"), at);
      }
      
      TopicIF roleField = null;
      boolean existingRField = false;
      try {
        Map<String,TopicIF> params = new HashMap<String,TopicIF>(2);
        params.put("AT", at);
        params.put("RT", rt);
        qr =  qp.execute("select $RF from " 
            + "on:has-association-type(%AT% : on:association-type, $AF : on:association-field), "
            + "on:has-association-field($AF : on:association-field, $RF : on:role-field), "
            + "on:has-role-type($RF : on:role-field, %RT% : on:role-type)?", 
            params, dc);
        if (qr.next()) {
          existingRField = true;
          roleField = (TopicIF)qr.getValue(0);
        } else {
          roleField = builder.makeTopic(tt_role_field);
        }
      } finally {
        if (qr != null) {
          qr.close();
        }
      }
      
      if (!existingRField) {

        AssociationIF hasAssociationField = builder.makeAssociation(getTopic(topicmap, base_on, "has-association-field"));
        builder.makeAssociationRole(hasAssociationField, getTopic(topicmap, base_on, "role-field"), roleField);
        builder.makeAssociationRole(hasAssociationField, getTopic(topicmap, base_on, "association-field"), associationField);
        
        // on:has-role-type(%new-role-field% : on:role-field, $RT : on:role-type)
        AssociationIF a4 = builder.makeAssociation(getTopic(topicmap, base_on, "has-role-type"));
        builder.makeAssociationRole(a4, getTopic(topicmap, base_on, "role-field"), roleField);
        builder.makeAssociationRole(a4, getTopic(topicmap, base_on, "role-type"), rt);
    
        // move role labels from association type to role field
        try {
          Map<String,TopicIF> params = new HashMap<String,TopicIF>();
          params.put("AT", at);
          params.put("RT", rt);
          qr =  qp.execute("select $TN from topic-name(%AT%, $TN), scope($TN, %RT%), not(type($TN, $NT)), not(scope($TN, $SC), $SC /= %RT%)?", params, dc);
          if (qr.next()) {
            TopicNameIF tn1 = (TopicNameIF)qr.getValue(0);
            builder.makeTopicName(roleField, tn1.getValue());
            tn1.remove();
          } else {
            // builder.makeTopicName(newRoleField, TopicStringifiers.toString(at));                
          }
        } finally {
          if (qr != null) {
            qr.close();
          }
        }
        
        // on:use-control($AT : on:association-type, $RT : on:role-type, $IC : on:interface-control)
        try {
          Map<String,TopicIF> params = new HashMap<String,TopicIF>();
          params.put("AT", at);
          params.put("RT", rt);
          qr =  qp.execute("select $IC from on:use-control(%AT% : on:association-type, %RT% : on:role-type, $IC : on:interface-control)?", params, dc);
          if (qr.next()) {
            TopicIF ic = (TopicIF)qr.getValue(0);                            
            // on:use-control($RF : on:field-definition, $IC : on:interface-control)
            AssociationIF a5 = builder.makeAssociation(getTopic(topicmap, base_on, "use-interface-control"));
            builder.makeAssociationRole(a5, getTopic(topicmap, base_on, "field-definition"), roleField);
            builder.makeAssociationRole(a5, getTopic(topicmap, base_on, "interface-control"), ic);
          }              
        } finally {
          if (qr != null) {
            qr.close();
          }
        }
        
      }
      
      // on:has-field(%new-field-assignment% : on:field-assignment, %new-role-field% : on:field-definition)              
      assignField(topicmap, base_on, roleField, tt);
      
      // on:has-cardinality($TT : on:topic-type, $AT : on:field, $RT : on:role-type, $C : on:cardinality)
      if (!assignedCardinality.contains(roleField)) {
        try {
          Map<String,TopicIF> params = new HashMap<String,TopicIF>();
          params.put("TT", tt);
          params.put("AT", at);
          params.put("RT", rt);
          qr =  qp.execute("select $C from on:has-cardinality(%TT% : on:topic-type, %AT% : on:field, %RT% : on:role-type, $C : on:cardinality)?", params, dc);
          if (qr.next()) {
            TopicIF card = (TopicIF)qr.getValue(0);
            // on:has-cardinality($TT : on:topic-type, $RF : on:field-definition, $C : on:cardinality)
            AssociationIF a6 = builder.makeAssociation(getTopic(topicmap, base_on, "has-cardinality"));
            builder.makeAssociationRole(a6, getTopic(topicmap, base_on, "field-definition"), roleField);
            builder.makeAssociationRole(a6, getTopic(topicmap, base_on, "cardinality"), card);
            assignedCardinality.add(roleField);
          }
        } finally {
          if (qr != null) {
            qr.close();
          }
        }
      }
      
      // update field order scope
      try {
        Map<String,TopicIF> params = new HashMap<String,TopicIF>();
        params.put("TT", tt);
        params.put("AT", at);
        params.put("RT", rt);
        String query = 
          "subclasses-of($SUP, $SUB) :- { " +
          "  xtm:superclass-subclass($SUP : xtm:superclass, $SUB : xtm:subclass) | " +
          "  xtm:superclass-subclass($SUP : xtm:superclass, $MID : xtm:subclass), subclasses-of($MID, $SUB) " +
          "}. " +
          "select $OC from { $TT = %TT% | subclasses-of(%TT%, $TT) }, occurrence($TT, $OC), type($OC, on:field-order), scope($OC, %RT%), scope($OC, %AT%)?";
        qr =  qp.execute(query, params, dc);
        while (qr.next()) {
          OccurrenceIF oc = (OccurrenceIF)qr.getValue(0);
          oc.removeTheme(at);
          oc.removeTheme(rt);
          oc.addTheme(roleField);
        }              
      } finally {
        if (qr != null) {
          qr.close();
        }
      }
    }
      
    // retrieve all non-association fields
    List<TopicIF[]> ofields = new ArrayList<TopicIF[]>();
    try {
      qr =  qp.execute("select $TT, $XT from on:has-field($TT : on:topic-type, $XT : on:field), not(direct-instance-of($XT, on:association-type)), not(direct-instance-of($XT, on:role-type))?", dc);
      while (qr.next()) {
        ofields.add(new TopicIF[] { (TopicIF)qr.getValue(0), (TopicIF)qr.getValue(1) });              
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
    removeAssociations("on:has-field", new String[] { "on:topic-type", "on:field" }, qp, dc);

    // create default fields for identity types, name types and occurrence types
    TopicMapBuilderIF builder = topicmap.getBuilder();
    Map<TopicIF,TopicIF> xtfields = new HashMap<TopicIF,TopicIF>();
    
    // identity types: subject identifier
    TopicIF subjectIdentifier = getTopic(topicmap, base_on, "subject-identifier");
    TopicIF subjectIdentifierField = defineIdentityField(topicmap, base_on, "subject-identifier", "cardinality-0-M");
    xtfields.put(subjectIdentifier, subjectIdentifierField);
//    assignFieldsView(topicmap, base_on, subjectIdentifierField, "default-fields-view");
//    assignFieldsView(topicmap, base_on, subjectIdentifierField, "association-field-embedded-view");
//    assignFieldsView(topicmap, base_on, subjectIdentifierField, "role-field-embedded-view");
//    assignFieldsView(topicmap, base_on, subjectIdentifierField, "name-field-embedded-view");
//    assignFieldsView(topicmap, base_on, subjectIdentifierField, "occurrence-field-embedded-view");
//    assignFieldsView(topicmap, base_on, subjectIdentifierField, "identity-field-embedded-view");            
    
    TopicIF subjectLocator = getTopic(topicmap, base_on, "subject-locator");
    TopicIF subjectLocatorField = defineIdentityField(topicmap, base_on, "subject-locator", "cardinality-0-M");
    xtfields.put(subjectLocator, subjectLocatorField);

    TopicIF itemIdentifier = getTopic(topicmap, base_on, "item-identifier");
    TopicIF itemIdentifierField = defineIdentityField(topicmap, base_on, "item-identifier", "cardinality-0-M");
    xtfields.put(itemIdentifier, itemIdentifierField);

    assignField(topicmap, base_on, subjectIdentifierField, "topic-type");          
    assignField(topicmap, base_on, subjectIdentifierField, "identity-type");          
    assignField(topicmap, base_on, subjectIdentifierField, "name-type");          
    assignField(topicmap, base_on, subjectIdentifierField, "occurrence-type");          
    assignField(topicmap, base_on, subjectIdentifierField, "role-type");          
    assignField(topicmap, base_on, subjectIdentifierField, "association-type");          

    assignField(topicmap, base_on, subjectIdentifierField, "field-definition");          
    assignField(topicmap, base_on, subjectIdentifierField, "association-field");          

    assignField(topicmap, base_on, subjectIdentifierField, "cardinality");          
    assignField(topicmap, base_on, subjectIdentifierField, "datatype");          
    assignField(topicmap, base_on, subjectIdentifierField, "interface-control");          

    assignField(topicmap, base_on, subjectIdentifierField, "view-mode");          
    assignField(topicmap, base_on, subjectIdentifierField, "edit-mode");          
    assignField(topicmap, base_on, subjectIdentifierField, "create-action");          

    assignField(topicmap, base_on, subjectIdentifierField, "fields-view");          
      
    // occurrence types
    Collection<TopicIF> nonFieldOccurrenceTypes = new HashSet<TopicIF>();
    nonFieldOccurrenceTypes.add(getTopic(topicmap, base_on, "field-order"));
    nonFieldOccurrenceTypes.add(getTopic(topicmap, base_on, "field-value-order"));
    nonFieldOccurrenceTypes.add(getTopic(topicmap, base_on, "ted-ontology-version"));

    try {
      qr =  qp.execute("select $XT, $DT from instance-of($XT, on:occurrence-type), { on:has-datatype($XT : on:field, $DT : on:datatype) }?", dc);
      while (qr.next()) {
        TopicIF xtType = (TopicIF)qr.getValue(0);
        if (nonFieldOccurrenceTypes.contains(xtType)) {
          continue;
        }
        
        TopicIF xtField = builder.makeTopic(getTopic(topicmap, base_on, "occurrence-field"));
        xtfields.put(xtType, xtField);
        
        // on:has-occurrence-type(%new-field% : on:occurrence-field, $XT : on:occurrence-type)
        AssociationIF a3 = builder.makeAssociation(getTopic(topicmap, base_on, "has-occurrence-type"));
        builder.makeAssociationRole(a3, getTopic(topicmap, base_on, "occurrence-field"), xtField);
        builder.makeAssociationRole(a3, getTopic(topicmap, base_on, "occurrence-type"), xtType);
        
        // on:has-datatype($TT : on:topic-type, $RF : on:field-definition, $C : on:datatype)
        TopicIF datatype = (TopicIF)qr.getValue(1);
        if (datatype != null) {
          AssociationIF a6 = builder.makeAssociation(getTopic(topicmap, base_on, "has-datatype"));
          builder.makeAssociationRole(a6, getTopic(topicmap, base_on, "field-definition"), xtField);
          builder.makeAssociationRole(a6, getTopic(topicmap, base_on, "datatype"), datatype);
        }
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
    
    // name types
    try {
      qr =  qp.execute("select $XT from instance-of($XT, on:name-type)?", dc);
      while (qr.next()) {
        TopicIF xtType = (TopicIF)qr.getValue(0);
        TopicIF xtField = builder.makeTopic(getTopic(topicmap, base_on, "name-field"));
        xtfields.put(xtType, xtField);
        
        // on:has-name-type(%new-field% : on:name-field, $XT : on:name-type)
        AssociationIF a3 = builder.makeAssociation(getTopic(topicmap, base_on, "has-name-type"));
        builder.makeAssociationRole(a3, getTopic(topicmap, base_on, "name-field"), xtField);
        builder.makeAssociationRole(a3, getTopic(topicmap, base_on, "name-type"), xtType);
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
      
    // loop over the fields so that we can execute other queries as we go
    Iterator<TopicIF[]> ofiter = ofields.iterator();
    while (ofiter.hasNext()) {
      TopicIF[] fen = ofiter.next();
      
      TopicIF tt = fen[0];
      TopicIF xt = fen[1];
      
      TopicIF xtField = xtfields.get(xt);
      if (xtField == null) {
        throw new OntopiaRuntimeException("Could not find field for " + xt);
      }
      // builder.makeTopicName(newField, TopicStringifiers.toString(xt));
      
      assignField(topicmap, base_on, xtField, tt);
      
      // update field order scope
      try {
        Map<String,TopicIF> params = new HashMap<String,TopicIF>();
        params.put("TT", tt);
        params.put("XT", xt);
        String query = 
          "subclasses-of($SUP, $SUB) :- { " +
          "  xtm:superclass-subclass($SUP : xtm:superclass, $SUB : xtm:subclass) | " +
          "  xtm:superclass-subclass($SUP : xtm:superclass, $MID : xtm:subclass), subclasses-of($MID, $SUB) " +
          "}. " +
          "select $OC from { $TT = %TT% | subclasses-of(%TT%, $TT) }, occurrence($TT, $OC), type($OC, on:field-order), scope($OC, %XT%)?";
        qr =  qp.execute(query, params, dc);
        while (qr.next()) {
          OccurrenceIF oc = (OccurrenceIF)qr.getValue(0);
          oc.removeTheme(xt);
          oc.addTheme(xtField);
        }
      } finally {
        if (qr != null) {
          qr.close();
        }
      }
      
      // on:has-cardinality($XT : on:field-definition,  $C : on:cardinality)
      if (!assignedCardinality.contains(xtField)) {
        try {
          Map<String,TopicIF> params = new HashMap<String,TopicIF>();
          params.put("TT", tt);
          params.put("XT", xt);
          qr =  qp.execute("select $C from on:has-cardinality(%TT% : on:topic-type, %XT% : on:field, $C : on:cardinality)?", params, dc);
          if (qr.next()) {
            TopicIF card = (TopicIF)qr.getValue(0);
            // on:has-cardinality($TT : on:topic-type, $RF : on:field-definition, $C : on:cardinality)
            AssociationIF a6 = builder.makeAssociation(getTopic(topicmap, base_on, "has-cardinality"));
            builder.makeAssociationRole(a6, getTopic(topicmap, base_on, "field-definition"), xtField);
            builder.makeAssociationRole(a6, getTopic(topicmap, base_on, "cardinality"), card);
            assignedCardinality.add(xtField);
          }
        } finally {
          if (qr != null) {
            qr.close();
          }
        }
      }
    }
    removeAssociations("on:has-datatype", new String[] { "on:field", "on:datatype" }, qp, dc);            

    removeAssociations("on:has-cardinality", new String[] { "on:topic-type", "on:field", "on:role-type", "on:cardinality" }, qp, dc);            
    removeAssociations("on:has-cardinality", new String[] { "on:topic-type", "on:field", "on:cardinality" }, qp, dc);            
    
    removeAssociations("on:use-control", new String[] { "on:association-type", "on:role-type", "on:interface-control" }, qp, dc);

    // remove on:default-cardinality
    removeAssociations("on:default-cardinality", new String[] { "on:cardinality", "on:field" }, qp, dc);
    removeAssociations("on:default-cardinality", new String[] { "on:cardinality", "on:field-type" }, qp, dc);
    
    // remove on:field-type
    getTopic(topicmap, base_on, "role-type").removeType(fieldType);
    getTopic(topicmap, base_on, "association-type").removeType(fieldType);
    getTopic(topicmap, base_on, "identity-type").removeType(fieldType);
    getTopic(topicmap, base_on, "name-type").removeType(fieldType);
    getTopic(topicmap, base_on, "occurrence-type").removeType(fieldType);
    removeTopic(topicmap, base_on, "field-type");
    
    // remove topics
    removeTopicIfExist(topicmap, base_on, "is-hierarchical");
    removeTopic(topicmap, base_on, "is-hidden");
    removeTopic(topicmap, base_on, "is-readonly");
    removeTopic(topicmap, base_on, "field");
    removeTopic(topicmap, base_on, "use-control");
    removeTopic(topicmap, base_on, "default-cardinality");
    removeTopic(topicmap, base_on, "has-role");

    // define meta-ontology associations
    RoleFieldDefinition hasField = 
      defineRoleFields(topicmap, base_on, "has-field", "Has field", 
        new String[] {"field-owner", "field-definition"},
        new String[] {"Fields", "Used by"}, 
        new String[][] {
          new String[] {"topic-type", "cardinality-0-M"}, 
          new String[] {"field-definition", "cardinality-1-M"}});
    //assignFieldsView(topicmap, base_on, hasField.rfields[0], "advanced-fields-view");
    assignFieldsView(topicmap, base_on, hasField.rfields[0], "declared-fields-view");
    assignFieldsView(topicmap, base_on, hasField.rfields[1], "default-fields-view");
//    assignFieldsView(topicmap, base_on, hasField.rfields[1], "association-field-embedded-view");
    assignFieldsView(topicmap, base_on, hasField.rfields[1], "role-field-embedded-view");
    assignFieldsView(topicmap, base_on, hasField.rfields[1], "name-field-embedded-view");
    assignFieldsView(topicmap, base_on, hasField.rfields[1], "occurrence-field-embedded-view");
    assignFieldsView(topicmap, base_on, hasField.rfields[1], "identity-field-embedded-view");

//    defineRoleFields(topicmap, base_on, "use-as-role-type", "Use as role type", 
//        new String[] {"topic-type"},
//        new String[] {"Use as role type"}, 
//        new String[][] {
//        new String[] {"topic-type", "cardinality-0-1"}});
    
    RoleFieldDefinition fieldInView = 
      defineRoleFields(topicmap, base_on, "field-in-view", "Fields in view", 
        new String[] {"field-definition", "fields-view"},
        new String[] {"Part of views", "Fields in view"}, 
        new String[][] {
          new String[] {"field-definition", "cardinality-0-M"}, 
          new String[] {"fields-view", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, fieldInView.rfields[0], "advanced-fields-view");

    defineRoleFields(topicmap, base_on, "is-hidden-view", "Hidden views", 
        new String[] {"fields-view"},
        new String[] {"Is hidden view"}, 
        new String[][] {
        new String[] {"fields-view", "cardinality-0-1"}});

    defineRoleFields(topicmap, base_on, "is-embedded-view", "Embedded views", 
        new String[] {"fields-view"},
        new String[] {"Is embedded view"}, 
        new String[][] {
        new String[] {"fields-view", "cardinality-0-1"}});
      
    RoleFieldDefinition hasCardinality = 
      defineRoleFields(topicmap, base_on, "has-cardinality", "Has cardinality",
        new String[] {"cardinality", "field-definition"},
        new String[] {"Field cardinalities", "Cardinality"},
        new String[][] {
          new String[] {"cardinality", "cardinality-0-M"}, 
          new String[] {"field-definition", "cardinality-0-1"}});
      assignFieldsView(topicmap, base_on, hasCardinality.rfields[0], "declared-fields-view");
      assignFieldsView(topicmap, base_on, hasCardinality.rfields[1], "default-fields-view");
      //! assignFieldsView(topicmap, base_on, hasCardinality.rfields[1], "association-field-embedded-view");
      assignFieldsView(topicmap, base_on, hasCardinality.rfields[1], "role-field-embedded-view");
      assignFieldsView(topicmap, base_on, hasCardinality.rfields[1], "name-field-embedded-view");
      assignFieldsView(topicmap, base_on, hasCardinality.rfields[1], "occurrence-field-embedded-view");
      assignFieldsView(topicmap, base_on, hasCardinality.rfields[1], "identity-field-embedded-view");

    RoleFieldDefinition formsHierarchyFor = 
      defineRoleFields(topicmap, base_on, "forms-hierarchy-for", "Forms hierarchy for", 
        new String[] {"association-type", "topic-type"},
        new String[] {"Hierarchical for", "Hierarchical associations"},  
        new String[][] {
        new String[] {"association-type", "cardinality-0-M"}, 
        new String[] {"topic-type", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, formsHierarchyFor.rfields[0], "advanced-fields-view");
    assignFieldsView(topicmap, base_on, formsHierarchyFor.rfields[1], "advanced-fields-view");
    
    RoleFieldDefinition superOrdinateRoleType = 
      defineRoleFields(topicmap, base_on, "superordinate-role-type", "Superordinate role type", 
        new String[] {"association-type", "role-type"},
        new String[] {"Superordinate role type", "Superordinate role type for"}, 
        new String[][] {
          new String[] {"association-type", "cardinality-0-1"}, 
          new String[] {"role-type", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, superOrdinateRoleType.rfields[0], "advanced-fields-view");
    assignFieldsView(topicmap, base_on, superOrdinateRoleType.rfields[1], "advanced-fields-view");
    assignEditMode(topicmap, base_on, superOrdinateRoleType.rfields[0], "edit-mode-existing-values-only");
    assignEditMode(topicmap, base_on, superOrdinateRoleType.rfields[1], "edit-mode-existing-values-only");
    
    RoleFieldDefinition subOrdinateRoleType = 
      defineRoleFields(topicmap, base_on, "subordinate-role-type", "Subordinate role type", 
        new String[] {"association-type", "role-type"},
        new String[] {"Subordinate role type", "Subordinate role type for"}, 
        new String[][] {
          new String[] {"association-type", "cardinality-0-1"}, 
          new String[] {"role-type", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, subOrdinateRoleType.rfields[0], "advanced-fields-view");
    assignFieldsView(topicmap, base_on, subOrdinateRoleType.rfields[1], "advanced-fields-view");
    assignEditMode(topicmap, base_on, subOrdinateRoleType.rfields[0], "edit-mode-existing-values-only");
    assignEditMode(topicmap, base_on, subOrdinateRoleType.rfields[1], "edit-mode-existing-values-only");
    
    RoleFieldDefinition hasAssociationType = 
      defineRoleFields(topicmap, base_on, "has-association-type", "Has association type", 
        new String[] {"association-type", "association-field"},
        new String[] {"Association field", "Association type"},  
        new String[][] {
          new String[] {"association-type", "cardinality-0-M"},
          new String[] {"association-field", "cardinality-0-M"}});
    
    assignValueView(topicmap, base_on, hasAssociationType.rfields[0], "default-fields-view", "association-field-embedded-view");
    assignEmbedded(topicmap, base_on, hasAssociationType.rfields[0], "association-field-embedded-view");
    assignEditMode(topicmap, base_on, hasAssociationType.rfields[0], "edit-mode-owned-values");
    // assignFieldsView(topicmap, base_on, hasAssociationType.rfields[0], "association-field-embedded-view");
    //!assignFieldsView(topicmap, base_on, hasAssociationType.rfields[1], "advanced-fields-view");
    
    RoleFieldDefinition hasAssociationField = 
      defineRoleFields(topicmap, base_on, 
        "has-association-field", "Has association field", 
        new String[] {"association-field", "role-field"},
        new String[] {"Roles", "Association field"},  
        new String[][] {
          new String[] {"association-field", "cardinality-0-M"}, 
          new String[] {"role-field", "cardinality-0-M"}});
    assignValueView(topicmap, base_on, hasAssociationField.rfields[0], "default-fields-view", "role-field-embedded-view");
    assignValueView(topicmap, base_on, hasAssociationField.rfields[0], "association-field-embedded-view", "role-field-embedded-view");
    assignEmbedded(topicmap, base_on, hasAssociationField.rfields[0], "role-field-embedded-view");
    assignEditMode(topicmap, base_on, hasAssociationField.rfields[0], "edit-mode-owned-values");
    assignFieldsView(topicmap, base_on, hasAssociationField.rfields[0], "default-fields-view");
    assignFieldsView(topicmap, base_on, hasAssociationField.rfields[0], "association-field-embedded-view");
    //!assignFieldsView(topicmap, base_on, hasAssociationField.rfields[1], "advanced-fields-view");
    
    RoleFieldDefinition hasRoleType = 
      defineRoleFields(topicmap, base_on, "has-role-type", "Has role type", 
        new String[] {"role-type", "role-field"},
        new String[] {"Role field", "Role type"},  
        new String[][] {
          new String[] {"role-type", "cardinality-0-M"},
          new String[] {"role-field", "cardinality-1-1"}});
    assignValueView(topicmap, base_on, hasRoleType.rfields[0], "default-fields-view", "role-field-embedded-view");
    assignEmbedded(topicmap, base_on, hasRoleType.rfields[0], "role-field-embedded-view");
    assignEditMode(topicmap, base_on, hasRoleType.rfields[0], "edit-mode-no-edit");
    assignFieldsView(topicmap, base_on, hasRoleType.rfields[1], "default-fields-view");
    assignFieldsView(topicmap, base_on, hasRoleType.rfields[1], "role-field-embedded-view");

    addOccurrence(topicMap, base_on, hasRoleType.rfields[0], 
        "allowed-players-query", "datatype-string",
        "select $T from { instance-of($T, on:topic-type) | instance-of($T, on:role-type) }?");

//    RoleFieldDefinition hasRoleTypeTT = 
//      defineRoleFields(topicmap, base_on, "has-role-type", "Has role type", 
//        new String[] {"role-type", "role-field"},
//        new String[] {"Role field", "Role type"},  
//        new String[][] {
//          new String[] {"topic-type", "cardinality-0-M"},
//          new String[] {"role-field", "cardinality-1-1"}});
//    assignFieldsView(topicmap, base_on, hasRoleTypeTT.rfields[0], "advanced-fields-view");
//    assignEmbedded(topicmap, base_on, hasRoleTypeTT.rfields[0], "advanced-fields-view", "role-field-embedded-view");
//    assignEditMode(topicmap, base_on, hasRoleTypeTT.rfields[0], "edit-mode-no-edit");
//    assignFieldsView(topicmap, base_on, hasRoleTypeTT.rfields[1], "default-fields-view");
//    assignFieldsView(topicmap, base_on, hasRoleTypeTT.rfields[1], "role-field-embedded-view");
    
    RoleFieldDefinition hasIdentityType =
      defineRoleFields(topicmap, base_on, "has-identity-type", "Has identity type", 
        new String[] {"identity-type", "identity-field"}, 
        new String[] {"Identity field", "Identity type"}, 
        new String[][] {               
        new String[] {"identity-type", "cardinality-0-M"},
        new String[] {"identity-field", "cardinality-0-M"}});
    assignValueView(topicmap, base_on, hasIdentityType.rfields[0], "default-fields-view", "identity-field-embedded-view");
    assignEmbedded(topicmap, base_on, hasIdentityType.rfields[0], "identity-field-embedded-view");
    assignFieldsView(topicmap, base_on, hasIdentityType.rfields[0], "default-fields-view");
    assignFieldsView(topicmap, base_on, hasIdentityType.rfields[0], "identity-field-embedded-view");
    assignEditMode(topicmap, base_on, hasIdentityType.rfields[0], "edit-mode-owned-values");

    RoleFieldDefinition hasNameType = 
      defineRoleFields(topicmap, base_on, "has-name-type",  "Has name type",
        new String[] {"name-type", "name-field"},
        new String[] {"Name field", "Name type"},  
        new String[][] {
        new String[] {"name-type", "cardinality-0-M"},
        new String[] {"name-field", "cardinality-0-M"}});
    assignValueView(topicmap, base_on, hasNameType.rfields[0], "default-fields-view", "name-field-embedded-view");
    assignEmbedded(topicmap, base_on, hasNameType.rfields[0], "name-field-embedded-view");
    assignFieldsView(topicmap, base_on, hasNameType.rfields[0], "default-fields-view");
    assignFieldsView(topicmap, base_on, hasNameType.rfields[0], "name-field-embedded-view");
    assignEditMode(topicmap, base_on, hasNameType.rfields[0], "edit-mode-owned-values");

    RoleFieldDefinition hasOccurrenceType = 
      defineRoleFields(topicmap, base_on, "has-occurrence-type", "Has occurrence type", 
        new String[] {"occurrence-type", "occurrence-field"},
        new String[] {"Occurrence field", "Occurrence type"},  
        new String[][] {
        new String[] {"occurrence-type", "cardinality-0-M"},
        new String[] {"occurrence-field", "cardinality-0-M"}});
    assignValueView(topicmap, base_on, hasOccurrenceType.rfields[0], "default-fields-view", "occurrence-field-embedded-view");
    assignEmbedded(topicmap, base_on, hasOccurrenceType.rfields[0], "occurrence-field-embedded-view");
    assignFieldsView(topicmap, base_on, hasOccurrenceType.rfields[0], "default-fields-view");
    assignFieldsView(topicmap, base_on, hasOccurrenceType.rfields[0], "occurrence-field-embedded-view");
    assignEditMode(topicmap, base_on, hasOccurrenceType.rfields[0], "edit-mode-owned-values");

    RoleFieldDefinition hasDataType = 
      defineRoleFields(topicmap, base_on, "has-datatype",  "Has data type",
        new String[] {"datatype", "field-definition"}, 
        new String[] {"Occurrence field", "Data type"}, 
        new String[][] {
        new String[] {"datatype", "cardinality-0-M"}, 
        new String[] {"occurrence-field", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, hasDataType.rfields[1], "default-fields-view");
    assignFieldsView(topicmap, base_on, hasDataType.rfields[1], "occurrence-field-embedded-view");
    
    RoleFieldDefinition useInterfaceControl = 
      defineRoleFields(topicmap, base_on, "use-interface-control", "Uses interface control", 
        new String[] {"field-definition", "interface-control"},
        new String[] {"Interface control", "Field"},  
        new String[][] {
          new String[] {"role-field", "cardinality-0-1"}, 
          new String[] {"interface-control", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, useInterfaceControl.rfields[0], "default-fields-view");
    //! assignFieldsView(topicmap, base_on, useInterfaceControl.rfields[0], "association-field-embedded-view");
    assignFieldsView(topicmap, base_on, useInterfaceControl.rfields[0], "role-field-embedded-view");

    RoleFieldDefinition hasLargeInstanceSet = 
      defineRoleFields(topicmap, base_on, "has-large-instance-set", "Has large instance set", 
        new String[] {"topic-type"},
        new String[] {"Large instance set"},  
        new String[][] {new String[] {"topic-type", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, hasLargeInstanceSet.rfields[0], "advanced-fields-view");
    
    defineRoleFields(topicmap, base_on, "is-abstract",  "Is abstract",
        new String[] {"topic-type"},
        new String[] {"Abstract", ""},  
        new String[][] {new String[] {"topic-type", "cardinality-0-M"}});
    
    RoleFieldDefinition isHiddenType = 
      defineRoleFields(topicmap, base_on, "is-hidden-type", "Is hidden type", 
        new String[] {"ontology-type"},
        new String[] {"Hidden"},  
        new String[][] {new String[] {"topic-type", "identity-type", "name-type", "occurrence-type", "role-type", "association-type", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, isHiddenType.rfields[0], "advanced-fields-view");
    
    RoleFieldDefinition isReadOnlyType = 
      defineRoleFields(topicmap, base_on, "is-readonly-type", "Is read-only type", 
        new String[] {"ontology-type"},
        new String[] {"Read-only"},  
        new String[][] {new String[] {"topic-type", "identity-type", "name-type", "occurrence-type", "role-type", "association-type", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, isReadOnlyType.rfields[0], "advanced-fields-view");
    
    RoleFieldDefinition useViewMode = 
      defineRoleFields(topicmap, base_on, "use-view-mode", "Use view mode", 
        new String[] {"field-definition", "view-mode", "fields-view"},
        new String[] {"View mode", "View mode", "Used by"},  
        new String[][] {
          new String[] {"field-definition", "cardinality-0-M"}, 
          new String[] {"view-mode", "cardinality-0-M"}, 
          new String[] {"fields-view", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, useViewMode.rfields[0], "advanced-fields-view");

    RoleFieldDefinition valueView = 
      defineRoleFields(topicmap, base_on, "use-value-view", "Use value view", 
        new String[] {"field-definition", "parent-view", "child-view"},
        new String[] {"Value view", "Parent view", "Child view"},  
        new String[][] {
          new String[] {"field-definition", "cardinality-0-M"}, 
          new String[] {"fields-view", "cardinality-0-M"}, 
          new String[] {"fields-view", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, valueView.rfields[0], "advanced-fields-view");

    RoleFieldDefinition useEditMode = 
      defineRoleFields(topicmap, base_on, "use-edit-mode", "Use edit mode", 
        new String[] {"field-definition", "edit-mode"},
        new String[] {"Edit modes", "Used by"}, 
        new String[][] {
          new String[] {"field-definition", "cardinality-0-1"}, 
          new String[] {"edit-mode", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, useEditMode.rfields[0], "advanced-fields-view");
    assignEditMode(topicmap, base_on, useEditMode.rfields[0], "edit-mode-existing-values-only");

    RoleFieldDefinition useCreateAction = 
      defineRoleFields(topicmap, base_on, "use-create-action", "Use create action", 
        new String[] {"field-definition", "create-action"},
        new String[] {"Create actions", "Used by"}, 
        new String[][] {
          new String[] {"field-definition", "cardinality-0-1"}, 
          new String[] {"create-action", "cardinality-0-M"}});
    assignFieldsView(topicmap, base_on, useCreateAction.rfields[0], "advanced-fields-view");
    assignEditMode(topicmap, base_on, useCreateAction.rfields[0], "edit-mode-existing-values-only");

    RoleFieldDefinition isSortableField = 
      defineRoleFields(topicmap, base_on, "is-sortable-field", "Is sortable field", 
        new String[] {"field-definition"},
        new String[] {"Sortable"},  
        new String[][] {new String[] {"field-definition", "cardinality-0-1"}});
    assignFieldsView(topicmap, base_on, isSortableField.rfields[0], "advanced-fields-view");
    
    defineRoleFields(topicmap, base_on, "is-symmetric", "Is symmetric", 
        new String[] {"association-type"},
        new String[] {"Symmetric"},  
        new String[][] {new String[] {"association-type", "cardinality-0-M"}});

    RoleFieldDefinition supsubClass =
    defineRoleFields(topicmap, base_on, getTopic(topicmap, base_xtm, "#superclass-subclass"), "Superclass-subclass", 
        new TopicIF[] {getTopic(topicmap, base_xtm, "#superclass"), getTopic(topicmap, base_xtm, "#subclass")},
        new String[] {"Subclass", "Superclass"},  
        new TopicIF[][] {
          new TopicIF[] {getTopic(topicmap, base_on, "topic-type"), getTopic(topicmap, base_on, "cardinality-0-M")}, 
          new TopicIF[] {getTopic(topicmap, base_on, "topic-type"), getTopic(topicmap, base_on, "cardinality-0-1")}});          
    addOccurrence(topicMap, base_on, supsubClass.rfields[0], 
        "allowed-players-query", "datatype-string",
        "select $T from direct-instance-of(%topic%, $TT), direct-instance-of($T, $TT), $T /= %topic%?"); // 91
    addOccurrence(topicMap, base_on, supsubClass.rfields[1], 
        "allowed-players-query", "datatype-string",
        "select $T from direct-instance-of(%topic%, $TT), direct-instance-of($T, $TT), $T /= %topic%?"); // 91
    addOccurrence(topicMap, base_on, supsubClass.rfields[0], 
        "allowed-players-types-query", "datatype-string",
        "select $TT from direct-instance-of(%topic%, $TT), {$TT = on:topic-type | $TT = on:association-type | $TT = on:occurrence-type | $TT = on:name-type | $TT = on:role-type}?"); // 91
    addOccurrence(topicMap, base_on, supsubClass.rfields[1], 
        "allowed-players-types-query", "datatype-string",
        "select $TT from direct-instance-of(%topic%, $TT), {$TT = on:topic-type | $TT = on:association-type | $TT = on:occurrence-type | $TT = on:name-type | $TT = on:role-type}?"); // 91
    
    
// 91: commented out
//    supsubClass =
//    defineRoleFields(topicmap, base_on, getTopic(topicmap, base_xtm, "#superclass-subclass"), "Superclass-subclass", 
//        new TopicIF[] {getTopic(topicmap, base_xtm, "#superclass"), getTopic(topicmap, base_xtm, "#subclass")},
//        new String[] {"Subclass", "Superclass"},  
//        new TopicIF[][] {
//          new TopicIF[] {getTopic(topicmap, base_on, "name-type"), getTopic(topicmap, base_on, "cardinality-0-M")}, 
//          new TopicIF[] {getTopic(topicmap, base_on, "name-type"), getTopic(topicmap, base_on, "cardinality-0-1")}});
//    assignFieldsView(topicmap, base_on, supsubClass.rfields[0], "advanced-fields-view");
//    assignFieldsView(topicmap, base_on, supsubClass.rfields[1], "advanced-fields-view");
//
//    supsubClass =
//    defineRoleFields(topicmap, base_on, getTopic(topicmap, base_xtm, "#superclass-subclass"), "Superclass-subclass", 
//        new TopicIF[] {getTopic(topicmap, base_xtm, "#superclass"), getTopic(topicmap, base_xtm, "#subclass")},
//        new String[] {"Subclass", "Superclass"},  
//        new TopicIF[][] {
//          new TopicIF[] {getTopic(topicmap, base_on, "occurrence-type"), getTopic(topicmap, base_on, "cardinality-0-M")}, 
//          new TopicIF[] {getTopic(topicmap, base_on, "occurrence-type"), getTopic(topicmap, base_on, "cardinality-0-1")}});
//    assignFieldsView(topicmap, base_on, supsubClass.rfields[0], "advanced-fields-view");
//    assignFieldsView(topicmap, base_on, supsubClass.rfields[1], "advanced-fields-view");
//
//    supsubClass =
//    defineRoleFields(topicmap, base_on, getTopic(topicmap, base_xtm, "#superclass-subclass"), "Superclass-subclass", 
//        new TopicIF[] {getTopic(topicmap, base_xtm, "#superclass"), getTopic(topicmap, base_xtm, "#subclass")},
//        new String[] {"Subclass", "Superclass"},  
//        new TopicIF[][] {
//          new TopicIF[] {getTopic(topicmap, base_on, "association-type"), getTopic(topicmap, base_on, "cardinality-0-M")}, 
//          new TopicIF[] {getTopic(topicmap, base_on, "association-type"), getTopic(topicmap, base_on, "cardinality-0-1")}});
//    assignFieldsView(topicmap, base_on, supsubClass.rfields[0], "advanced-fields-view");
//    assignFieldsView(topicmap, base_on, supsubClass.rfields[1], "advanced-fields-view");
//
//    supsubClass =
//    defineRoleFields(topicmap, base_on, getTopic(topicmap, base_xtm, "#superclass-subclass"), "Superclass-subclass", 
//        new TopicIF[] {getTopic(topicmap, base_xtm, "#superclass"), getTopic(topicmap, base_xtm, "#subclass")},
//        new String[] {"Subclass", "Superclass"},  
//        new TopicIF[][] {
//          new TopicIF[] {getTopic(topicmap, base_on, "role-type"), getTopic(topicmap, base_on, "cardinality-0-M")}, 
//          new TopicIF[] {getTopic(topicmap, base_on, "role-type"), getTopic(topicmap, base_on, "cardinality-0-1")}});
//    assignFieldsView(topicmap, base_on, supsubClass.rfields[0], "advanced-fields-view");
//    assignFieldsView(topicmap, base_on, supsubClass.rfields[1], "advanced-fields-view");
  
//    RoleFieldDefinition memberOfOntology = 
//      defineRoleFields(topicmap, base_on, "member-of-ontology", "Part of ontology", 
//        new String[] {"ontology-member", "ontology"},
//        new String[] {"Part of ontology", "Ontology topics"},  
//        new String[][] {
//            new String[] {"topic-type", "name-type", "occurrence-type", "role-type", "association-type", "cardinality-0-M"}, 
//            new String[] {"ontology", "cardinality-1-M"}});
//    assignFieldsView(topicmap, base_on, memberOfOntology.rfields[0], "advanced-fields-view");
    
    TopicIF oField;
    oField = defineOccurrenceField(topicMap, base_on, "datatype-locator", "datatype-uri", "cardinality-1-1");
    assignField(topicmap, base_on, oField, "datatype");
    
    oField = defineOccurrenceField(topicMap, base_on, "description", "datatype-string", "cardinality-0-1");
    addOccurrence(topicMap, base_on, oField, "height", "datatype-number", "5");
    addOccurrence(topicMap, base_on, oField, "width", "datatype-number", "50");
    assignField(topicmap, base_on, oField, "topic-map");
    assignField(topicmap, base_on, oField, "topic-type");
    assignField(topicmap, base_on, oField, "identity-type");
    assignField(topicmap, base_on, oField, "name-type");
    assignField(topicmap, base_on, oField, "occurrence-type");
    assignField(topicmap, base_on, oField, "association-type");
    assignField(topicmap, base_on, oField, "role-type");
//    assignFieldsView(topicmap, base_on, oField, "default-fields-view");
//    assignFieldsView(topicmap, base_on, oField, "association-field-embedded-view");
//    assignFieldsView(topicmap, base_on, oField, "role-field-embedded-view");
//    assignFieldsView(topicmap, base_on, oField, "name-field-embedded-view");
//    assignFieldsView(topicmap, base_on, oField, "occurrence-field-embedded-view");
//    assignFieldsView(topicmap, base_on, oField, "identity-field-embedded-view");            

    oField = defineOccurrenceField(topicMap, base_on, "creator", "datatype-string", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "topic-map");

    oField = defineOccurrenceField(topicMap, base_on, "version", "datatype-string", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "topic-map");

    oField = defineOccurrenceField(topicMap, base_on, "allowed-players-query", "datatype-string", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "role-field");
    assignFieldsView(topicmap, base_on, oField, "advanced-fields-view");          
    addOccurrence(topicMap, base_on, oField, "height", "datatype-number", "5");
    addOccurrence(topicMap, base_on, oField, "width", "datatype-number", "50");

    oField = defineOccurrenceField(topicMap, base_on, "allowed-players-search-query", "datatype-string", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "role-field");
    assignFieldsView(topicmap, base_on, oField, "advanced-fields-view");          
    addOccurrence(topicMap, base_on, oField, "height", "datatype-number", "5");
    addOccurrence(topicMap, base_on, oField, "width", "datatype-number", "50");

    oField = defineOccurrenceField(topicMap, base_on, "allowed-players-types-query", "datatype-string", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "role-field");
    assignFieldsView(topicmap, base_on, oField, "advanced-fields-view");          
    addOccurrence(topicMap, base_on, oField, "height", "datatype-number", "5");
    addOccurrence(topicMap, base_on, oField, "width", "datatype-number", "50");

    oField = defineOccurrenceField(topicMap, base_on, "tolog-declarations", "datatype-string", "cardinality-0-1");          
    assignField(topicmap, base_on, oField, "topic-map");
    assignFieldsView(topicmap, base_on, oField, "advanced-fields-view");          
    addOccurrence(topicMap, base_on, oField, "height", "datatype-number", "5");
    addOccurrence(topicMap, base_on, oField, "width", "datatype-number", "50");

    oField = defineOccurrenceField(topicMap, base_on, "height", "datatype-number", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "occurrence-field");
    assignFieldsView(topicmap, base_on, oField, "default-fields-view");
    assignFieldsView(topicmap, base_on, oField, "occurrence-field-embedded-view");

    oField = defineOccurrenceField(topicMap, base_on, "width", "datatype-number", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "occurrence-field");
    assignFieldsView(topicmap, base_on, oField, "default-fields-view");
    assignFieldsView(topicmap, base_on, oField, "occurrence-field-embedded-view");

    oField = defineOccurrenceField(topicMap, base_on, "min-cardinality", "datatype-number", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "cardinality");

    oField = defineOccurrenceField(topicMap, base_on, "max-cardinality", "datatype-number", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "cardinality");

    oField = defineOccurrenceField(topicMap, base_on, "hierarchy-definition-query", "datatype-string", "cardinality-0-1");
    assignField(topicmap, base_on, oField, "topic-type");
    assignFieldsView(topicmap, base_on, oField, "advanced-fields-view");          
    addOccurrence(topicMap, base_on, oField, "height", "datatype-number", "5");
    addOccurrence(topicMap, base_on, oField, "width", "datatype-number", "50");

//    oField = defineOccurrenceField(topicMap, base_on, "pattern", "datatype-string", "cardinality-0-1");
//    assignField(topicmap, base_on, oField, "datatype");
    
    // default name fields
    TopicIF untypedName = getTopic(topicmap, base_on, "untyped-name"); // NOTE: fixed
    TopicIF cardinality11 = getTopic(topicmap, base_on, "cardinality-1-1"); // NOTE: fixed

    TopicIF nField = null;
    try {
      qr =  qp.execute("select $NF from direct-instance-of($NF, on:name-field), " + 
          "on:has-name-type($NF : on:name-field, on:untyped-name : on:name-type)?", dc);
      if (qr.next()) {
        nField = (TopicIF)qr.getValue(0);
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
    if (nField == null) {
      nField = builder.makeTopic(getTopic(topicmap, base_on, "name-field"));            
      AssociationIF hasNameTypeA = builder.makeAssociation(getTopic(topicmap, base_on, "has-name-type"));
      builder.makeAssociationRole(hasNameTypeA, getTopic(topicmap, base_on, "name-type"), untypedName);
      builder.makeAssociationRole(hasNameTypeA, getTopic(topicmap, base_on, "name-field"), nField);
    }
    
    // on:has-cardinality($TT : on:topic-type, $RF : on:field-definition, $C : on:cardinality)
    if (!assignedCardinality.contains(nField)) {
      AssociationIF hasCardinalityA = builder.makeAssociation(getTopic(topicmap, base_on, "has-cardinality"));
      builder.makeAssociationRole(hasCardinalityA, getTopic(topicmap, base_on, "field-definition"), nField);
      builder.makeAssociationRole(hasCardinalityA, getTopic(topicmap, base_on, "cardinality"), cardinality11);
      assignedCardinality.add(nField);
    }

    assignFieldsView(topicmap, base_on, nField, "default-fields-view");
//    assignFieldsView(topicmap, base_on, nField, "association-field-embedded-view");
    assignFieldsView(topicmap, base_on, nField, "role-field-embedded-view");
    assignFieldsView(topicmap, base_on, nField, "name-field-embedded-view");
    assignFieldsView(topicmap, base_on, nField, "occurrence-field-embedded-view");
    assignFieldsView(topicmap, base_on, nField, "identity-field-embedded-view");            
    
//    assignField(topicmap, base_on, nField, "topic-type");          
//    assignField(topicmap, base_on, nField, "name-type");          
//    assignField(topicmap, base_on, nField, "identity-type");          
//    assignField(topicmap, base_on, nField, "occurrence-type");          
//    assignField(topicmap, base_on, nField, "role-type");          
//    assignField(topicmap, base_on, nField, "association-type");          
    
    try {
      qr =  qp.execute("select $T from direct-instance-of($T, on:topic-type), " + 
          "direct-instance-of($T, on:system-topic), " +
          "not(xtm:superclass-subclass($T : xtm:subclass, $P : xtm:superclass))?", dc);
      while (qr.next()) {
        TopicIF tt = (TopicIF)qr.getValue(0);
        assignField(topicmap, base_on, nField, tt);
        
//        // on:has-field(%new-field-assignment% : on:field-assignment, %new-role-field% : on:field-definition)              
//        AssociationIF a2 = builder.makeAssociation(getTopic(topicmap, base_on, "has-field"));
//        builder.makeAssociationRole(a2, getTopic(topicmap, base_on, "field-owner"), tt);
//        builder.makeAssociationRole(a2, getTopic(topicmap, base_on, "field-definition"), nField);
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
    
//    try {
//      TopicIF ontopolyOntology = getTopic(topicmap, base_on, "ontopoly-ontology");
//      qr =  qp.execute("select $T from direct-instance-of($T, on:system-topic)?", dc);
//      while (qr.next()) {
//        TopicIF st = (TopicIF)qr.getValue(0);
//        // on:member-of-ontology(on:topic-type : on:topic-type, on:ontopoly-ontology : on:ontology)          
//        AssociationIF memberOfOntology = builder.makeAssociation(getTopic(topicmap, base_on, "member-of-ontology"));
//        builder.makeAssociationRole(memberOfOntology, getTopic(topicmap, base_on, "ontology-member"), st);
//        builder.makeAssociationRole(memberOfOntology, getTopic(topicmap, base_on, "ontology"), ontopolyOntology);
//        st.removeType(systemTopic);
//      }
//    } finally {
//      if (qr != null) qr.close();
//    }
    
    // TODO: turn on:is-symmetric assocation into a duplicate role field
    
    // rename topics
    renameTopics(topicmap, "Ontology Topic type", "Ontology type");
    
    // height and width
    try {
      qr =  qp.execute("select $OT, $OF, $OH, $OW from on:has-occurrence-type($OT : on:occurrence-type, $OF : on:occurrence-field), "
          + "{ occurrence($OT, $OH), type($OH, on:height) }, "
          + "{ occurrence($OT, $OW), type($OW, on:width) }?", dc);
      LocatorIF numberType = base_xsd.resolveAbsolute("#decimal");
      while (qr.next()) {
//        TopicIF otype = (TopicIF)qr.getValue(0);
        TopicIF ofield = (TopicIF)qr.getValue(1);
        OccurrenceIF occheight = (OccurrenceIF)qr.getValue(2);
        OccurrenceIF occwidth = (OccurrenceIF)qr.getValue(3);
        if (occheight != null) {
          TopicIF heightType = getTopic(topicmap, base_on, "height");
          builder.makeOccurrence(ofield, heightType, occheight.getValue(), numberType);
          occheight.remove();
        }
        if (occwidth != null) {
          TopicIF widthType = getTopic(topicmap, base_on, "width");
          builder.makeOccurrence(ofield, widthType, occwidth.getValue(), numberType);
          occwidth.remove();
        }
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
  
  //  makePublicSystemTopic(topicmap, base_on, base_xtm, "#superclass-subclass");
  //  makePublicSystemTopic(topicmap, base_on, base_xtm, "#superclass");
  //  makePublicSystemTopic(topicmap, base_on, base_xtm, "#subclass");
    makePublicSystemTopic(topicmap, base_on, base_on, "cardinality-1-1");
    makePublicSystemTopic(topicmap, base_on, base_on, "cardinality-1-M");
    makePublicSystemTopic(topicmap, base_on, base_on, "cardinality-0-M");
    makePublicSystemTopic(topicmap, base_on, base_on, "cardinality-0-1");
    makePublicSystemTopic(topicmap, base_on, base_on, "create-action-navigate");
    makePublicSystemTopic(topicmap, base_on, base_on, "create-action-none");
    makePublicSystemTopic(topicmap, base_on, base_on, "create-action-popup");
    makePublicSystemTopic(topicmap, base_on, base_on, "datatype-html");
    makePublicSystemTopic(topicmap, base_on, base_on, "datatype-image");
    makePublicSystemTopic(topicmap, base_on, base_xsd, "#date");
    makePublicSystemTopic(topicmap, base_on, base_xsd, "#dateTime");
    makePublicSystemTopic(topicmap, base_on, base_xsd, "#decimal");
    makePublicSystemTopic(topicmap, base_on, base_xsd, "#string");
    makePublicSystemTopic(topicmap, base_on, base_xsd, "#anyURI");
    makePublicSystemTopic(topicmap, base_on, base_on, "edit-mode-existing-values-only");
    makePublicSystemTopic(topicmap, base_on, base_on, "edit-mode-new-values-only");
    makePublicSystemTopic(topicmap, base_on, base_on, "edit-mode-no-edit");
    makePublicSystemTopic(topicmap, base_on, base_on, "edit-mode-normal");
    makePublicSystemTopic(topicmap, base_on, base_on, "edit-mode-owned-values");
    makePublicSystemTopic(topicmap, base_on, base_on, "advanced-fields-view");
    makePublicSystemTopic(topicmap, base_on, base_on, "default-fields-view");
    makePublicSystemTopic(topicmap, base_on, base_on, "auto-complete");
    makePublicSystemTopic(topicmap, base_on, base_on, "browse-dialog");
    makePublicSystemTopic(topicmap, base_on, base_on, "drop-down-list");
    makePublicSystemTopic(topicmap, base_on, base_on, "search-dialog");
    makePublicSystemTopic(topicmap, base_on, base_on, "untyped-name");
    makePublicSystemTopic(topicmap, base_on, base_on, "view-mode-readonly");
    makePublicSystemTopic(topicmap, base_on, base_on, "view-mode-hidden");
    makePublicSystemTopic(topicmap, base_on, base_on, "view-mode-not-traversable");
    makePublicSystemTopic(topicmap, base_on, base_on, "view-mode-embedded");
    makePublicSystemTopic(topicmap, base_on, base_on, "description");
  
    // add system topic as type on all field definitions
    TopicIF publicSystemTopic = getTopic(topicmap, base_on, "public-system-topic");
    
    try {
      // field definitions
      qr =  qp.execute("select $FD from instance-of($T, on:system-topic), " +
          "on:has-field($T : on:field-owner, $FD : on:field-definition)?", dc);
      while (qr.next()) {
        TopicIF field = (TopicIF)qr.getValue(0);
        field.addType(systemTopic);
      }
      // association fields
      qr =  qp.execute("select $AF from instance-of($T, on:system-topic), " +
          "on:has-field($T : on:field-owner, $FD : on:field-definition), " +
          "on:has-association-field($FD : on:role-field, $AF : on:association-field)?", dc);
      while (qr.next()) {
        TopicIF field = (TopicIF)qr.getValue(0);
        field.addType(systemTopic);
      }
  
      // turn description field into public-system-topic
      qr =  qp.execute("select $FD from instance-of($FD, on:field-definition), " +
          "{ on:has-occurrence-type($FD : on:occurrence-field, on:description : on:occurrence-type) |" +
          "  on:has-name-type($FD : on:name-field, on:untyped-name : on:name-type) }?", dc);
      while (qr.next()) {
        TopicIF field = (TopicIF)qr.getValue(0);
        field.removeType(systemTopic);
        field.addType(publicSystemTopic);
      }
      // remove system-topic from some other fields
      qr =  qp.execute("select $FD from instance-of($FD, on:field-definition), " +
          "{ on:has-occurrence-type($FD : on:occurrence-field, on:creator : on:occurrence-type) |" +
          "  on:has-occurrence-type($FD : on:occurrence-field, on:version : on:occurrence-type) }?", dc);
      while (qr.next()) {
        TopicIF field = (TopicIF)qr.getValue(0);
        field.removeType(systemTopic);
      }
      
      // remove superclass-subtype between topic-type and ontology-type
      qr =  qp.execute("select $A from role-player($R1, on:topic-type), association-role($A, $R1), type($R1, xtm:superclass), " +
          "association-role($A, $R2), type($R2, xtm:subclass), role-player($R2, on:ontology-type)?", dc);
      while (qr.next()) {
        AssociationIF assoc = (AssociationIF)qr.getValue(0);
        assoc.remove();
      }
  
      // remove on:role-type from topics that are already on:topic-type
      TopicIF roleType1 = getTopic(topicmap, base_on, "role-type");
      qr =  qp.execute("select $T from direct-instance-of($T, on:topic-type), direct-instance-of($T, on:role-type)?", dc);
      while (qr.next()) {
        TopicIF topicType1 = (TopicIF)qr.getValue(0);
        topicType1.removeType(roleType1);
      }
  
      // remove superflous superclass-subclass definitions
      qr =  qp.execute("select $PRF, $CRF, $AF from " + // 91
            "on:has-role-type($PRF : on:role-field, xtm:superclass : on:role-type), " + 
            "on:has-association-field($PRF : on:role-field, $AF : on:association-field), " + 
            "on:has-association-type($AF : on:association-field, xtm:superclass-subclass : on:association-type), " +
            "on:has-association-field($CRF : on:role-field, $AF : on:association-field), " +
            "on:has-role-type($CRF : on:role-field, xtm:subclass : on:role-type), " +
            "not(on:has-field($PRF : on:field-definition, on:topic-type : on:field-owner), " +
            "on:has-field($CRF : on:field-definition, on:topic-type : on:field-owner))?", dc);
      while (qr.next()) {
        TopicIF prf = (TopicIF)qr.getValue(0);
        TopicIF crf = (TopicIF)qr.getValue(1);
        TopicIF af = (TopicIF)qr.getValue(2);
        prf.remove();
        crf.remove();
        af.remove();
      }
      
      // add psi to field definitions and association fields
      
      // role-fields
      qr =  qp.execute("select $RF, $RT, $AT from instance-of($RF, on:role-field), instance-of($RF, on:system-topic), " +
          "on:has-role-type($RF : on:role-field, $RT : on:role-type), " +
          "on:has-association-field($RF : on:role-field, $AF : on:association-field), " +
          "on:has-association-type($AF : on:association-field, $AT : on:association-type)?", dc);
      while (qr.next()) {
        TopicIF rf = (TopicIF)qr.getValue(0);
        TopicIF rt = (TopicIF)qr.getValue(1);
        TopicIF at = (TopicIF)qr.getValue(2);
        String rtid = getSymbolicId(rt);
        String atid = getSymbolicId(at);
        if (rtid != null & atid != null) {
          rf.addSubjectIdentifier(base_on.resolveAbsolute("rf-" + rtid + "_" + atid));
        }
      }
      // association fields
      qr =  qp.execute("select $XF, $XT from " + // 91
          "instance-of($XF, on:association-field), instance-of($XF, on:system-topic), on:has-association-type($XF : on:association-field, $XT : on:association-type)?", dc);
      while (qr.next()) {
        TopicIF xf = (TopicIF)qr.getValue(0);
        TopicIF xt = (TopicIF)qr.getValue(1);
        String xtid = getSymbolicId(xt);
        if (xtid != null) {
          xf.addSubjectIdentifier(base_on.resolveAbsolute("af-" + xtid));
        }
      }
      // identity fields
      qr =  qp.execute("select $XF, $XT from " +
          "instance-of($XF, on:identity-field), instance-of($XF, on:system-topic), on:has-identity-type($XF : on:identity-field, $XT : on:identity-type)?", dc);
      while (qr.next()) {
        TopicIF xf = (TopicIF)qr.getValue(0);
        TopicIF xt = (TopicIF)qr.getValue(1);
        String xtid = getSymbolicId(xt);
        if (xtid != null) {
          xf.addSubjectIdentifier(base_on.resolveAbsolute("if-" + xtid));
        }
      }
      // name fields
      qr =  qp.execute("select $XF, $XT from " +
          "instance-of($XF, on:name-field), instance-of($XF, on:system-topic), on:has-name-type($XF : on:name-field, $XT : on:name-type)?", dc);
      while (qr.next()) {
        TopicIF xf = (TopicIF)qr.getValue(0);
        TopicIF xt = (TopicIF)qr.getValue(1);
        String xtid = getSymbolicId(xt);
        if (xtid != null) {
          xf.addSubjectIdentifier(base_on.resolveAbsolute("nf-" + xtid));
        }
      }
      // occurrence fields
      qr =  qp.execute("select $XF, $XT from " +
          "instance-of($XF, on:occurrence-field), instance-of($XF, on:system-topic), on:has-occurrence-type($XF : on:occurrence-field, $XT : on:occurrence-type)?", dc);
      while (qr.next()) {
        TopicIF xf = (TopicIF)qr.getValue(0);
        TopicIF xt = (TopicIF)qr.getValue(1);
        String xtid = getSymbolicId(xt);
        if (xtid != null) {
          xf.addSubjectIdentifier(base_on.resolveAbsolute("of-" + xtid));
        }
      }
      
    } finally {
      if (qr != null) {
        qr.close();
      }
    }

    // remove duplicate fields
    qr =  qp.execute("superclass-subclass($ANC, $DES) :- {" + // 91
        "xtm:superclass-subclass($ANC : xtm:superclass, $DES : xtm:subclass) | " +
        "xtm:superclass-subclass($ANC : xtm:superclass, $MID : xtm:subclass), superclass-subclass($MID, $DES) " +
        "}. " + 
        "select $A, $FD, $TT from " +
        "on:has-field($FD : on:field-definition, $TT : on:field-owner), " +
        "superclass-subclass($ST, $TT), " + 
        "on:has-field($ST : on:field-owner, $FD : on:field-definition), " + 
        "association($A), type($A, on:has-field), " +  
        "association-role($A, $R1), type($R1, on:field-definition), role-player($R1, $FD), " + 
        "association-role($A, $R2), type($R2, on:field-owner), role-player($R2, $TT)?", dc);
    while (qr.next()) {
      AssociationIF hasFieldAssoc = (AssociationIF)qr.getValue(0);
      hasFieldAssoc.remove();
    }

    // remove unused field orders
    qr =  qp.execute("superclass-subclass($ANC, $DES) :- {" + // 91
        "xtm:superclass-subclass($ANC : xtm:superclass, $DES : xtm:subclass) | " +
        "xtm:superclass-subclass($ANC : xtm:superclass, $MID : xtm:subclass), superclass-subclass($MID, $DES) " +
        "}. " + 
        "select $FOO from " + 
        "occurrence($TT, $FOO), type($FOO, on:field-order), scope($FOO, $FD), " +
        "not({ on:has-field($FD : on:field-definition, $TT : on:field-owner) | " + 
        "superclass-subclass($ST, $TT), " + 
        "on:has-field($ST : on:field-owner, $FD : on:field-definition) })?", dc);
    while (qr.next()) {
      OccurrenceIF fieldOrderOcc = (OccurrenceIF)qr.getValue(0);
      fieldOrderOcc.remove();
    }
    
    // reassign field orders < 1000
    try {
      String query =
        "select $FO from " +
        "occurrence($TT, $FO), type($FO, on:field-order)?";
      qr =  qp.execute(query, dc);
      while (qr.next()) {
        OccurrenceIF occ = (OccurrenceIF)qr.getValue(0);
        try {
          int order = Integer.parseInt(occ.getValue());
          if (order < 1000) {
            int neworder = (order * 1000) + 1000;
            occ.setValue(StringUtils.leftPad(Integer.toString(neworder), 9, '0'));
          }
        } catch (NumberFormatException e) {
          // ignore
        }
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }
    
    // order fields
    List<String> fdo = new ArrayList<String>();
    fdo.add("null|http://psi.ontopia.net/ontology/untyped-name");
    fdo.add("null|http://psi.ontopia.net/ontology/subject-identifier");
    fdo.add("null|http://psi.ontopia.net/ontology/description");
    fdo.add("null|http://purl.org/dc/elements/1.1/description");
    fdo.add("null|http://psi.ontopia.net/biography/date-of-birth");
    fdo.add("null|http://psi.ontopia.net/biography/date-of-death");
    fdo.add("null|http://psi.ontopia.net/occurrence-type/#comment");
    fdo.add("null|http://psi.ontopia.net/ontology/tolog-declarations");
    fdo.add("null|http://psi.ontopia.net/ontology/datatype-locator");
    fdo.add("null|http://purl.org/dc/elements/1.1/Date");
    fdo.add("null|http://www.kanzaki.com/ns/music#key");
    fdo.add("null|http://www.kanzaki.com/ns/music#opus");
    fdo.add("null|http://psi.ontopia.net/ontology/creator");
    fdo.add("null|http://psi.ontopia.net/ontology/version");
    fdo.add("http://psi.ontopia.net/ontology/is-abstract|http://psi.ontopia.net/ontology/topic-type");
  //  fdo.add("http://psi.ontopia.net/ontology/use-as-role-type|http://psi.ontopia.net/ontology/topic-type");
    fdo.add("http://psi.ontopia.net/ontology/is-hidden-view|http://psi.ontopia.net/ontology/fields-view");
    fdo.add("http://psi.ontopia.net/ontology/is-embedded-view|http://psi.ontopia.net/ontology/fields-view");
    fdo.add("http://psi.ontopia.net/ontology/is-hidden-type|http://psi.ontopia.net/ontology/ontology-type");
    fdo.add("http://psi.ontopia.net/ontology/is-readonly-type|http://psi.ontopia.net/ontology/ontology-type");
    fdo.add("http://psi.ontopia.net/ontology/is-sortable-field|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/is-symmetric|http://psi.ontopia.net/ontology/association-type");
    fdo.add("http://psi.ontopia.net/ontology/has-association-type|http://psi.ontopia.net/ontology/association-field");
    fdo.add("http://psi.ontopia.net/ontology/has-association-type|http://psi.ontopia.net/ontology/association-type");
    fdo.add("http://psi.ontopia.net/ontology/has-association-field|http://psi.ontopia.net/ontology/association-field");
    fdo.add("http://psi.ontopia.net/ontology/has-association-field|http://psi.ontopia.net/ontology/role-field");
    fdo.add("http://psi.ontopia.net/ontology/has-role-type|http://psi.ontopia.net/ontology/role-field");
    fdo.add("http://psi.ontopia.net/ontology/has-role-type|http://psi.ontopia.net/ontology/role-type");
    fdo.add("http://psi.ontopia.net/ontology/has-datatype|http://psi.ontopia.net/ontology/datatype");
    fdo.add("http://psi.ontopia.net/ontology/has-datatype|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/has-field|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/has-field|http://psi.ontopia.net/ontology/field-owner");
    fdo.add("http://psi.ontopia.net/ontology/has-cardinality|http://psi.ontopia.net/ontology/cardinality");
    fdo.add("http://psi.ontopia.net/ontology/has-cardinality|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/has-identity-type|http://psi.ontopia.net/ontology/identity-field");
    fdo.add("http://psi.ontopia.net/ontology/has-identity-type|http://psi.ontopia.net/ontology/identity-type");
    fdo.add("http://psi.ontopia.net/ontology/has-name-type|http://psi.ontopia.net/ontology/name-field");
    fdo.add("http://psi.ontopia.net/ontology/has-name-type|http://psi.ontopia.net/ontology/name-type");
    fdo.add("http://psi.ontopia.net/ontology/has-occurrence-type|http://psi.ontopia.net/ontology/occurrence-field");
    fdo.add("http://psi.ontopia.net/ontology/has-occurrence-type|http://psi.ontopia.net/ontology/occurrence-type");
    fdo.add("http://psi.ontopia.net/ontology/has-large-instance-set|http://psi.ontopia.net/ontology/topic-type");
    fdo.add("http://psi.ontopia.net/ontology/use-edit-mode|http://psi.ontopia.net/ontology/edit-mode");
    fdo.add("http://psi.ontopia.net/ontology/use-edit-mode|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/use-create-action|http://psi.ontopia.net/ontology/create-action");
    fdo.add("http://psi.ontopia.net/ontology/use-create-action|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/field-in-view|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/field-in-view|http://psi.ontopia.net/ontology/fields-view");
    fdo.add("http://psi.ontopia.net/ontology/use-interface-control|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/use-interface-control|http://psi.ontopia.net/ontology/interface-control");
    fdo.add("http://psi.ontopia.net/ontology/use-view-mode|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/use-view-mode|http://psi.ontopia.net/ontology/fields-view");
    fdo.add("http://psi.ontopia.net/ontology/use-view-mode|http://psi.ontopia.net/ontology/view-mode");
    fdo.add("http://psi.ontopia.net/ontology/use-value-view|http://psi.ontopia.net/ontology/field-definition");
    fdo.add("http://psi.ontopia.net/ontology/use-value-view|http://psi.ontopia.net/ontology/parent-view");
    fdo.add("http://psi.ontopia.net/ontology/use-value-view|http://psi.ontopia.net/ontology/child-view");
    fdo.add("http://www.kanzaki.com/ns/music#composer|http://www.kanzaki.com/ns/music#Composer");
    fdo.add("http://www.kanzaki.com/ns/music#composer|http://www.kanzaki.com/ns/music#Oeuvre");
    fdo.add("http://www.kanzaki.com/ns/music#conductor|http://www.kanzaki.com/ns/music#Conductor");
    fdo.add("http://www.kanzaki.com/ns/music#conductor|http://www.kanzaki.com/ns/music#Musical_Event");
    fdo.add("http://www.kanzaki.com/ns/music#performer|http://www.kanzaki.com/ns/music#Artist");
    fdo.add("http://www.kanzaki.com/ns/music#performer|http://www.kanzaki.com/ns/music#Musical_Event");
    fdo.add("http://www.kanzaki.com/ns/music#performs|http://www.kanzaki.com/ns/music#Artist");
    fdo.add("http://www.kanzaki.com/ns/music#performs|http://www.kanzaki.com/ns/music#Musical_Event");
    fdo.add("http://www.kanzaki.com/ns/music#performs|http://www.kanzaki.com/ns/music#Oeuvre");
    fdo.add("http://www.kanzaki.com/ns/music#program|http://www.kanzaki.com/ns/music#Musical_Event");
    fdo.add("http://www.kanzaki.com/ns/music#program|http://www.kanzaki.com/ns/music#Oeuvre");
    fdo.add("http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass|http://www.topicmaps.org/xtm/1.0/core.xtm#subclass");
    fdo.add("http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass|http://www.topicmaps.org/xtm/1.0/core.xtm#superclass");
  //  fdo.add("http://psi.ontopia.net/ontology/member-of-ontology|http://psi.ontopia.net/ontology/ontology");
  //  fdo.add("http://psi.ontopia.net/ontology/member-of-ontology|http://psi.ontopia.net/ontology/ontology-member");
    fdo.add("http://psi.ontopia.net/ontology/superordinate-role-type|http://psi.ontopia.net/ontology/association-type");
    fdo.add("http://psi.ontopia.net/ontology/superordinate-role-type|http://psi.ontopia.net/ontology/role-type");
    fdo.add("http://psi.ontopia.net/ontology/subordinate-role-type|http://psi.ontopia.net/ontology/association-type");
    fdo.add("http://psi.ontopia.net/ontology/subordinate-role-type|http://psi.ontopia.net/ontology/role-type");
    fdo.add("http://psi.ontopia.net/ontology/forms-hierarchy-for|http://psi.ontopia.net/ontology/association-type");
    fdo.add("http://psi.ontopia.net/ontology/forms-hierarchy-for|http://psi.ontopia.net/ontology/topic-type");
    fdo.add("null|http://psi.ontopia.net/ontology/height");
    fdo.add("null|http://psi.ontopia.net/ontology/width");
    fdo.add("null|http://psi.ontopia.net/ontology/allowed-players-query");
    fdo.add("null|http://psi.ontopia.net/ontology/allowed-players-search-query");
    fdo.add("null|http://psi.ontopia.net/ontology/allowed-players-types-query");
    fdo.add("null|http://psi.ontopia.net/ontology/min-cardinality");
    fdo.add("null|http://psi.ontopia.net/ontology/max-cardinality");
    fdo.add("null|http://psi.ontopia.net/ontology/hierarchy-definition-query");

// INFO: use this query to locate ontopoly types with no specific ordering:
//    using on for i"http://psi.ontopia.net/ontology/"
//    instance-of($TT, on:topic-type), on:has-field($TT : on:field-owner, $FD : on:field-definition),
//    not(occurrence($TT, $FOO), type($FOO, on:field-order), scope($FOO, $FD))?
        
    // assign new field orders to built-in fields
    try {
      String query =
        " subclasses-of($SUP, $SUB) :- { " +  
        "   xtm:superclass-subclass($SUP : xtm:superclass, $SUB : xtm:subclass) | " + 
        "   xtm:superclass-subclass($SUP : xtm:superclass, $MID : xtm:subclass), subclasses-of($MID, $SUB) " +  
        "}. " + 
        "select $TT, $FD, $AT, $FT from " +
        "on:has-field($DT : on:field-owner, $FD : on:field-definition), " + 
        "{ $TT = $DT | subclasses-of($DT, $TT) }," +  
        "{ on:has-identity-type($FD : on:identity-field, $FT : on:identity-type) " +  
        "| on:has-name-type($FD : on:name-field, $FT : on:name-type)" +
        "| on:has-occurrence-type($FD : on:occurrence-field, $FT : on:occurrence-type) " +
        "| on:has-association-field($FD : on:role-field, $AF : on:association-field)," + 
        "on:has-role-type($FD : on:role-field, $FT : on:role-type), " +
        "on:has-association-type($AF : on:association-field, $AT : on:association-type) " +
        "} order by $TT, $AT, $FT?";
      qr =  qp.execute(query, dc);
      while (qr.next()) {
        TopicIF tt = (TopicIF)qr.getValue(0);
        TopicIF fd = (TopicIF)qr.getValue(1);
        TopicIF at = (TopicIF)qr.getValue(2);
        TopicIF ft = (TopicIF)qr.getValue(3);
        String atkey = (at == null || at.getSubjectIdentifiers().isEmpty() ? null : at.getSubjectIdentifiers().iterator().next().getAddress());  
        String ftkey = (ft == null || ft.getSubjectIdentifiers().isEmpty() ? null : ft.getSubjectIdentifiers().iterator().next().getAddress());
        String fieldkey = atkey + "|" + ftkey;
        int ix = fdo.indexOf(fieldkey);
        if (ix > -1) {
          int order = 1000 * ix;
          FieldAssignment.setOrder(topicMap, tt, fd, order, false);
        }
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }

    // remove duplicate field orders (keep lowest sortkey) // 91
    qr = qp.execute("select $TT, $FD, $FO1, $FOV from " +
        "occurrence($TT, $FO1), type($FO1, on:field-order), scope($FO1, $FD), " +
        "occurrence($TT, $FO2), type($FO2, on:field-order), scope($FO2, $FD), " +
        "$FO1 /= $FO2, value($FO1, $FOV) " + 
        "order by $TT, $FD, $FOV?", dc);
    Map<String,OccurrenceIF> fieldOrders = new HashMap<String,OccurrenceIF>();
    while (qr.next()) {
      TopicIF tt = (TopicIF)qr.getValue(0);
      TopicIF fd = (TopicIF)qr.getValue(1);
      OccurrenceIF fieldOrderOcc = (OccurrenceIF)qr.getValue(2);
      String key = tt.getObjectId() + ":" + fd.getObjectId();
      if (fieldOrders.containsKey(key)) {
        fieldOrderOcc.remove();
      } else {
        fieldOrders.put(key, fieldOrderOcc);
      }
    }
    
  }

}
