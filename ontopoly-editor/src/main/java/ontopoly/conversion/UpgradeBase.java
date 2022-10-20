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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import ontopoly.model.QueryMapper;
import ontopoly.model.TopicMap;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.utils.OntopiaRuntimeException;

public abstract class UpgradeBase {
  
  protected static final LocatorIF psibase = URILocator.create("http://psi.ontopia.net/ontology/");
  protected static final LocatorIF xsdbase = URILocator.create("http://www.w3.org/2001/XMLSchema");
  protected static final LocatorIF xtmbase = URILocator.create("http://www.topicmaps.org/xtm/1.0/core.xtm");
  protected static final LocatorIF teqbase = URILocator.create("http://www.techquila.com/psi/hierarchy/");
  
  protected static final LocatorIF base_on = URILocator.create("http://psi.ontopia.net/ontology/"); 
  protected static final LocatorIF base_tech = URILocator.create("http://www.techquila.com/psi/hierarchy/"); 
  protected static final LocatorIF base_xtm = URILocator.create("http://www.topicmaps.org/xtm/1.0/core.xtm");
  protected static final LocatorIF base_xsd = URILocator.create("http://www.w3.org/2001/XMLSchema#"); 

  protected TopicMap topicMap;
  protected TopicMapIF topicmap;
  
  protected DeclarationContextIF dc;
  protected QueryProcessorIF qp;
  
  UpgradeBase(TopicMap topicMap) throws InvalidQueryException {
    this.topicMap = topicMap;
    this.topicmap = topicMap.getTopicMapIF();
    this.dc = QueryUtils.parseDeclarations(topicMap.getTopicMapIF(), 
      "using on for i\"http://psi.ontopia.net/ontology/\" " +  
      "using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\"\n");
    this.qp = QueryUtils.getQueryProcessor(topicMap.getTopicMapIF());
  }
  
  public void upgrade() {    
    // produce LTM fragment
    StringBuilder sb = new StringBuilder();
    sb.append("#PREFIX on  @\"http://psi.ontopia.net/ontology/\"\n");
    sb.append("#PREFIX xtm @\"http://www.topicmaps.org/xtm/1.0/core.xtm#\"\n");
    sb.append("#PREFIX tech @\"http://www.techquila.com/psi/hierarchy/#\"\n");
    sb.append("#PREFIX xsd @\"http://www.w3.org/2001/XMLSchema#\"\n");
    importLTM(sb);
    
    // import ltm fragment
    Reader reader = new StringReader(sb.toString());    
    TopicMapReaderIF importer = new LTMTopicMapReader(reader, URILocator.create("http://psi.ontopia.net/ontology/"));
    try {
      importer.importInto(topicmap);
    } catch (IOException e1) {
      throw new OntopiaRuntimeException(e1);
    }

    // transform topic map
    try {
      transform();
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  protected abstract void importLTM(StringBuilder sb);
  
  protected abstract void transform() throws InvalidQueryException;

  /**
   * Runs a tolog update statement to modify the topic map.
   */
  protected int doUpdate(String update) throws InvalidQueryException {
    return qp.update(update, dc);
  }
  
  protected static TopicIF getTopic(TopicMapIF topicmap, LocatorIF base_on, String ref) {
    LocatorIF loc = base_on.resolveAbsolute(ref);
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(loc);
    if (topic == null) { 
      throw new OntopiaRuntimeException("Cannot find topic with id " + loc.getAddress());
    } else {
      return topic;
    }
  }

  protected static void translateAssociations(String atype1, String[] rtypes1,
      String atype2, String[] rtypes2, TopicMapIF tm, LocatorIF base_on, QueryProcessorIF qp, DeclarationContextIF dc) throws InvalidQueryException {
    StringBuilder sb = new StringBuilder();
    sb.append("select $A");
    for (int i=0; i < rtypes1.length; i++) {
      sb.append(", $P" + i);
    }    
    sb.append(" from ");
    sb.append("association($A), type($A, " + atype1 + ")");
    for (int i=0; i < rtypes1.length; i++) {
      sb.append(", association-role($A, $R"+ i + "), type($R" + i + ", " + rtypes1[i] + ")");
      if (i > 0) {
        sb.append(", $R" + i + " /= $R" + (i-1));
      }
      sb.append(", role-player($R" + i + ", $P" +i + ")");
    }
    sb.append(", not(association-role($A, $R" + rtypes1.length + ")");
    for (int i=0; i < rtypes1.length; i++) {
      sb.append(", $R" + rtypes1.length + " /= $R" + i);
    }
    sb.append(")");
    sb.append("?");
    TopicMapBuilderIF builder = tm.getBuilder();
    QueryResultIF qr = null;
    try {
      qr =  qp.execute(sb.toString(), dc);
      while (qr.next()) {
        AssociationIF a1 = (AssociationIF)qr.getValue(0);
        a1.remove();
        TopicIF _atype2 = getTopic(tm, base_on, atype2); 
        AssociationIF a2 = builder.makeAssociation(_atype2);
        for (int i=0; i < rtypes1.length; i++) {
          TopicIF _rtype2 = getTopic(tm, base_on, rtypes2[i]); 
          TopicIF player = (TopicIF)qr.getValue(i+1);
          builder.makeAssociationRole(a2, _rtype2, player);
        }        
      }              
    } finally {
      if (qr != null) {
        qr.close();
      }
    }    
  }

  protected static void removeAssociations(String atype, String[] rtypes, QueryProcessorIF qp, DeclarationContextIF dc) throws InvalidQueryException {
    StringBuilder sb = new StringBuilder();
    sb.append("select $A from ");
    sb.append("association($A), type($A, " + atype + ")");
    for (int i=0; i < rtypes.length; i++) {
      sb.append(", association-role($A, $R"+ i + "), type($R" + i + ", " + rtypes[i] + ")");
      if (i > 0) {
        sb.append(", $R" + i + " /= $R" + (i-1));
      }
    }
    sb.append(", not(association-role($A, $R" + rtypes.length + ")");
    for (int i=0; i < rtypes.length; i++) {
      sb.append(", $R" + rtypes.length + " /= $R" + i);
    }
    sb.append(")");
    sb.append("?");
    QueryResultIF qr = null;
    try {
      qr =  qp.execute(sb.toString(), dc);
      while (qr.next()) {
        AssociationIF a = (AssociationIF)qr.getValue(0);
        a.remove();
      }              
    } finally {
      if (qr != null) {
        qr.close();
      }
    } 
  }

  protected static void removeObjects(TopicMapIF topicMap, DeclarationContextIF dc, String removalQuery) throws InvalidQueryException {
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(topicMap);
    QueryResultIF qr = null;
    try {
      qr =  qp.execute(removalQuery, dc);
      while (qr.next()) {
        TMObjectIF tmobject = (TMObjectIF)qr.getValue(0);
        if (tmobject.getTopicMap() != null) {
          tmobject.remove();
        }
      }
    } finally {
      if (qr != null) {
        qr.close();
      }
    }    
  }
  
  protected static void assignField(TopicMapIF topicmap,
      LocatorIF base_on, TopicIF oField, String ptypeId) {
    TopicIF ptype = getTopic(topicmap, base_on, ptypeId);
    assignField(topicmap, base_on, oField, ptype);  
  }
  
  protected static void assignField(TopicMapIF topicmap,
      LocatorIF base_on, TopicIF oField, TopicIF ptype) {

    TopicMapBuilderIF builder = topicmap.getBuilder();    
    // TopicIF cardinality = getTopic(topicmap, base_on, "cardinality-0-1"); // NOTE: fixed
    
    // on:has-field(%new-field-assignment% : on:field-assignment, %new-role-field% : on:field-definition)              
    AssociationIF a2 = builder.makeAssociation(getTopic(topicmap, base_on, "has-field"));
    builder.makeAssociationRole(a2, getTopic(topicmap, base_on, "field-owner"), ptype);
    builder.makeAssociationRole(a2, getTopic(topicmap, base_on, "field-definition"), oField);
  }

//  protected static RoleFieldDefinition defineRoleFields(TopicMapIF topicmap, LocatorIF base_on,
//      String atypeId, String afieldName, String[] rtypeIds, String[] rfieldNames, String[] ptypeIds) {
//    // { A, B } -> {{A}, {B}}
//    String[][] pptypeIds = new String[ptypeIds.length][];
//    for (int p=0; p < ptypeIds.length; p++) {
//      pptypeIds[p] = new String[] { ptypeIds[p] };
//    }
//    return defineRoleFields(topicmap, base_on, atypeId, afieldName, rtypeIds, rfieldNames, pptypeIds);
//  }
  
  protected static RoleFieldDefinition defineRoleFields(TopicMapIF topicmap, LocatorIF base_on,
        String atypeId, String afieldName, String[] rtypeIds, String[] rfieldNames, String[][] ptypeIds) {
    TopicIF atype = getTopic(topicmap, base_on, atypeId);
    TopicIF[] rtypes = new TopicIF[rtypeIds.length];
    TopicIF[][] ptypes = new TopicIF[ptypeIds.length][];
    for (int r=0; r < rtypeIds.length; r++) {
      rtypes[r] = getTopic(topicmap, base_on, rtypeIds[r]);
      ptypes[r] = new TopicIF[ptypeIds[r].length];
      for (int p=0; p < ptypeIds[r].length; p++) {
        ptypes[r][p] = getTopic(topicmap, base_on, ptypeIds[r][p]);
      }
    }
    return defineRoleFields(topicmap, base_on, atype, afieldName, rtypes, rfieldNames, ptypes);
  }

//  protected static RoleFieldDefinition defineRoleFields(TopicMapIF topicmap, LocatorIF base_on,
//      TopicIF atype, String afieldName, TopicIF[] rtypes, String[] rfieldNames, TopicIF[] ptypes) {
//    // { A, B } -> {{A}, {B}}
//    TopicIF[][] pptypes = new TopicIF[ptypes.length][];
//    for (int p=0; p < ptypes.length; p++) {
//      pptypes[p] = new TopicIF[] { ptypes[p] };
//    }    
//    return defineRoleFields(topicmap, base_on, atype, afieldName, rtypes, rfieldNames, pptypes );  
//  }
  
  protected static RoleFieldDefinition defineRoleFields(TopicMapIF topicmap, LocatorIF base_on,
      TopicIF atype, String afieldName, TopicIF[] rtypes, String[] rfieldNames, TopicIF[][] pptypes) {
    
    TopicMapBuilderIF builder = topicmap.getBuilder();    

    RoleFieldDefinition rfd = new RoleFieldDefinition();
    //!rfd.atype = atype;
    //!rfd.rtypes = rtypes;
    //!rfd.pptypes = pptypes;
    rfd.rfields = new TopicIF[rtypes.length];
    
    // TopicIF cardinality = getTopic(topicmap, base_on, "cardinality-0-M"); // NOTE: fixed
    TopicIF ic = getTopic(topicmap, base_on, "drop-down-list"); // NOTE: fixed

    TopicIF associationField = builder.makeTopic(getTopic(topicmap, base_on, "association-field"));
    builder.makeTopicName(associationField, afieldName);
    rfd.afield = associationField;
    
    AssociationIF hasAssociationType = builder.makeAssociation(getTopic(topicmap, base_on, "has-association-type"));
    builder.makeAssociationRole(hasAssociationType, getTopic(topicmap, base_on, "association-type"), atype);
    builder.makeAssociationRole(hasAssociationType, getTopic(topicmap, base_on, "association-field"), associationField);

    for (int r=0; r < rtypes.length; r++) {
      TopicIF roleField = builder.makeTopic(getTopic(topicmap, base_on, "role-field"));
      builder.makeTopicName(roleField, rfieldNames[r]);
      rfd.rfields[r] = roleField;
      TopicIF rtype = rtypes[r];

      AssociationIF hasAssociationField = builder.makeAssociation(getTopic(topicmap, base_on, "has-association-field"));
      builder.makeAssociationRole(hasAssociationField, getTopic(topicmap, base_on, "role-field"), roleField);
      builder.makeAssociationRole(hasAssociationField, getTopic(topicmap, base_on, "association-field"), associationField);

      AssociationIF hasRoleType = builder.makeAssociation(getTopic(topicmap, base_on, "has-role-type"));
      builder.makeAssociationRole(hasRoleType, getTopic(topicmap, base_on, "role-type"), rtype);
      builder.makeAssociationRole(hasRoleType, getTopic(topicmap, base_on, "role-field"), roleField);

      AssociationIF useIC = builder.makeAssociation(getTopic(topicmap, base_on, "use-interface-control"));
      builder.makeAssociationRole(useIC, getTopic(topicmap, base_on, "field-definition"), roleField);
      builder.makeAssociationRole(useIC, getTopic(topicmap, base_on, "interface-control"), ic);     

      TopicIF[] ptypes = pptypes[r];
      for (int p=0; p < ptypes.length-1; p++) {
        TopicIF ptype = ptypes[p];        
        assignField(topicmap, base_on, roleField, ptype);
      }
      TopicIF cardinality = ptypes[ptypes.length-1]; // WARNING: cardinality is last element
      AssociationIF hasCardinality = builder.makeAssociation(getTopic(topicmap, base_on, "has-cardinality"));
      builder.makeAssociationRole(hasCardinality, getTopic(topicmap, base_on, "field-definition"), roleField);
      builder.makeAssociationRole(hasCardinality, getTopic(topicmap, base_on, "cardinality"), cardinality);
    }
    return rfd;
  }
  
  protected static class RoleFieldDefinition {
    //! private TopicIF atype;
    //! private TopicIF[] rtypes;
    //! private TopicIF[][] ptypes;
    protected TopicIF afield;
    protected TopicIF rfields[];
  }
  
  protected static void removeTopicIfExist(TopicMapIF topicmap, LocatorIF base_on, String ref) {
    LocatorIF loc = base_on.resolveAbsolute(ref);
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(loc);
    if (topic != null) {
      topic.remove();
    }
  }
  
  protected static void removeTopic(TopicMapIF topicmap, LocatorIF base_on, String ref) {
    LocatorIF loc = base_on.resolveAbsolute(ref);
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(loc);
    if (topic == null) { 
      throw new OntopiaRuntimeException("Cannot find topic with id " + loc.getAddress());
    } else {
      topic.remove();
    }
  }

//  protected static TopicIF getTopic(TopicMapIF topicmap, String ref) {
//    LocatorIF loc = URILocator.create(ref);
//    TopicIF topic = topicmap.getTopicBySubjectIdentifier(loc);
//    if (topic == null) 
//      throw new OntopiaRuntimeException("Cannot find topic with id " + loc.getAddress());
//    else
//      return topic;
//  }

  protected static String getSymbolicId(TopicIF rf) {
    String prefix = "http://psi.ontopia.net/ontology/";
    int beginIndex = prefix.length();
    Iterator<LocatorIF> iter = rf.getSubjectIdentifiers().iterator();
    while (iter.hasNext()) {
      LocatorIF loc = iter.next();
      String address = loc.getAddress();
      if (address.startsWith(prefix)) {
        return address.substring(beginIndex);
      }
    }
    return null;
  }

  protected static void makePublicSystemTopic(TopicMapIF topicmap,
      LocatorIF base_on, LocatorIF base_t, String topic) {
    TopicIF systemTopic = getTopic(topicmap, base_on, "system-topic"); 
    TopicIF publicSystemTopic = getTopic(topicmap, base_on, "public-system-topic"); 
    TopicIF t = getTopic(topicmap, base_t, topic);
    t.removeType(systemTopic);
    t.addType(publicSystemTopic);
  }

//  protected static void makePartOfOntology(TopicMapIF topicmap,
//      LocatorIF base_on, LocatorIF base_t, String topic, String ontology) {
//    TopicMapBuilderIF builder = topicmap.getBuilder();    
//    AssociationIF viewMode = builder.makeAssociation(getTopic(topicmap, base_on, "member-of-ontology"));
//    builder.makeAssociationRole(viewMode, getTopic(topicmap, base_on, "ontology-member"),
//                                          getTopic(topicmap, base_t, topic));    
//    builder.makeAssociationRole(viewMode, getTopic(topicmap, base_on, "ontology"), 
//                                          getTopic(topicmap, base_on, ontology));    
//  }

  protected static void renameSubjectIdentifier(TopicMapIF topicmap, LocatorIF base_on,
      String oldId, String newId) {
    TopicIF identityType = getTopic(topicmap, base_on, oldId);
    identityType.addSubjectIdentifier(base_on.resolveAbsolute(newId));
    identityType.removeSubjectIdentifier(base_on.resolveAbsolute(oldId));    
  }

  protected static void renameTopics(TopicMapIF topicmap, String oldName, String newName) {
    NameIndexIF nix = (NameIndexIF)topicmap.getIndex(NameIndexIF.class.getName());
    Iterator<TopicNameIF> names = nix.getTopicNames(oldName).iterator();
    while (names.hasNext()) {
      TopicNameIF tn = names.next();
      tn.setValue(newName);
    }
  }
  
  protected static void assignViewMode(TopicMapIF topicmap, LocatorIF base_on, TopicIF rfield, String view, String mode) {
    TopicMapBuilderIF builder = topicmap.getBuilder();    
    AssociationIF viewMode = builder.makeAssociation(getTopic(topicmap, base_on, "use-view-mode"));
    builder.makeAssociationRole(viewMode, getTopic(topicmap, base_on, "field-definition"), rfield);    
    builder.makeAssociationRole(viewMode, getTopic(topicmap, base_on, "fields-view"), 
                                            getTopic(topicmap, base_on, view));    
    builder.makeAssociationRole(viewMode, getTopic(topicmap, base_on, "view-mode"), 
                                            getTopic(topicmap, base_on, mode));    
  }
  
  protected static void assignEmbedded(TopicMapIF topicmap, LocatorIF base_on, TopicIF rfield, String cview) {
    assignViewMode(topicmap, base_on, rfield, cview, "view-mode-embedded");
  }
  
  protected static void assignValueView(TopicMapIF topicmap, LocatorIF base_on, TopicIF rfield, String pview, String cview) {
    TopicMapBuilderIF builder = topicmap.getBuilder();    
    AssociationIF valueView = builder.makeAssociation(getTopic(topicmap, base_on, "use-value-view"));
    builder.makeAssociationRole(valueView, getTopic(topicmap, base_on, "field-definition"), rfield);    
    builder.makeAssociationRole(valueView, getTopic(topicmap, base_on, "parent-view"), 
                                            getTopic(topicmap, base_on, pview));    
    builder.makeAssociationRole(valueView, getTopic(topicmap, base_on, "child-view"), 
                                            getTopic(topicmap, base_on, cview));
  }
  
  protected static void assignEditMode(TopicMapIF topicmap, LocatorIF base_on, TopicIF rfield, String mode) {
    TopicMapBuilderIF builder = topicmap.getBuilder();    
    AssociationIF isEmbedded = builder.makeAssociation(getTopic(topicmap, base_on, "use-edit-mode"));
    builder.makeAssociationRole(isEmbedded, getTopic(topicmap, base_on, "field-definition"), rfield);    
    builder.makeAssociationRole(isEmbedded, getTopic(topicmap, base_on, "edit-mode"), 
                                            getTopic(topicmap, base_on, mode));    
  }

  protected static void assignFieldsView(TopicMapIF topicmap,
      LocatorIF base_on, TopicIF fieldDefinition, String view) {    

    TopicMapBuilderIF builder = topicmap.getBuilder();    
    TopicIF fieldsView = getTopic(topicmap, base_on, view);

    AssociationIF fieldInView = builder.makeAssociation(getTopic(topicmap, base_on, "field-in-view"));
    builder.makeAssociationRole(fieldInView, getTopic(topicmap, base_on, "field-definition"), fieldDefinition);
    builder.makeAssociationRole(fieldInView, getTopic(topicmap, base_on, "fields-view"), fieldsView);
  }

  protected static TopicIF defineOccurrenceField(TopicMap topicMap,
      LocatorIF base_on, String otypeId, String datatype, String cardinality) {
    TopicMapIF topicmap = topicMap.getTopicMapIF();
    TopicIF otype = getTopic(topicmap, base_on, otypeId);
    TopicIF card = getTopic(topicmap, base_on, cardinality);
    TopicIF dt = getTopic(topicmap, base_on, datatype);
    return defineOccurrenceField(topicMap, base_on, otype, dt, card);  
  }
  
  protected static TopicIF defineOccurrenceField(TopicMap topicMap,
      LocatorIF base_on, TopicIF otype, TopicIF datatype, TopicIF cardinality) {
    
    TopicMapIF topicmap = topicMap.getTopicMapIF();
    TopicMapBuilderIF builder = topicmap.getBuilder();    

    // find existing field definition
    String query = "on:has-occurrence-type(%OT% : on:occurrence-type, $OF : on:occurrence-field)?";
    Map<String,TopicIF> params = Collections.singletonMap("OT", otype);
    
    QueryMapper<TopicIF> qm = topicMap.newQueryMapperNoWrap();
    TopicIF oField = qm.queryForObject(query, params);

    if (oField == null) {
      oField = builder.makeTopic(getTopic(topicmap, base_on, "occurrence-field"));
      AssociationIF hasOccurrenceType = builder.makeAssociation(getTopic(topicmap, base_on, "has-occurrence-type"));
      builder.makeAssociationRole(hasOccurrenceType, getTopic(topicmap, base_on, "occurrence-type"), otype);
      builder.makeAssociationRole(hasOccurrenceType, getTopic(topicmap, base_on, "occurrence-field"), oField);
    }
    
   query = "on:has-datatype(%OF% : on:field-definition, $DT : on:datatype)?";
   if (!qm.isTrue(query, Collections.singletonMap("OF", oField))) { 
     AssociationIF hasField = builder.makeAssociation(getTopic(topicmap, base_on, "has-datatype"));
     builder.makeAssociationRole(hasField, getTopic(topicmap, base_on, "field-definition"), oField);
     builder.makeAssociationRole(hasField, getTopic(topicmap, base_on, "datatype"), datatype);
   }
    
   query = "on:has-cardinality(%OF% : on:field-definition, $CD : on:cardinality)?";
   if (!qm.isTrue(query, Collections.singletonMap("OF", oField))) { 
     AssociationIF hasCardinality = builder.makeAssociation(getTopic(topicmap, base_on, "has-cardinality"));
     builder.makeAssociationRole(hasCardinality, getTopic(topicmap, base_on, "field-definition"), oField);
     builder.makeAssociationRole(hasCardinality, getTopic(topicmap, base_on, "cardinality"), cardinality);
   }
   return oField;
  }

  protected static OccurrenceIF addOccurrence(TopicMap topicMap, LocatorIF base_on, TopicIF topic, String otype, String datatype, String value) {
    TopicMapIF topicmap = topicMap.getTopicMapIF();    
    TopicMapBuilderIF builder = topicmap.getBuilder();
    LocatorIF dt;
    if ("datatype-string".equals(datatype)) {
      dt = DataTypes.TYPE_STRING;
    } else if ("datatype-number".equals(datatype)) {
      dt = DataTypes.TYPE_DECIMAL;
    } else {
      throw new RuntimeException("Unsupported datatype: " + datatype);
    }
    return builder.makeOccurrence(topic, getTopic(topicmap, base_on, otype), value, dt);
  }
  
  protected static TopicIF defineIdentityField(TopicMapIF topicmap,
      LocatorIF base_on, String itypeId, String cardinality) {
    TopicIF itype = getTopic(topicmap, base_on, itypeId);
    TopicIF card = getTopic(topicmap, base_on, cardinality);
    return defineIdentityField(topicmap, base_on, itype, card);  
  }
  
  protected static TopicIF defineIdentityField(TopicMapIF topicmap,
      LocatorIF base_on, TopicIF itype, TopicIF cardinality) {

    TopicMapBuilderIF builder = topicmap.getBuilder();    

    TopicIF iField = builder.makeTopic(getTopic(topicmap, base_on, "identity-field"));
    AssociationIF hasOccurrenceType = builder.makeAssociation(getTopic(topicmap, base_on, "has-identity-type"));
    builder.makeAssociationRole(hasOccurrenceType, getTopic(topicmap, base_on, "identity-type"), itype);
    builder.makeAssociationRole(hasOccurrenceType, getTopic(topicmap, base_on, "identity-field"), iField);

    AssociationIF hasCardinality = builder.makeAssociation(getTopic(topicmap, base_on, "has-cardinality"));
    builder.makeAssociationRole(hasCardinality, getTopic(topicmap, base_on, "field-definition"), iField);
    builder.makeAssociationRole(hasCardinality, getTopic(topicmap, base_on, "cardinality"), cardinality);
    return iField;
  }
  
}
