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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.xml.XTMTopicMapReference;
import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.OntopolyApplication;
import ontopoly.OntopolyContext;
import ontopoly.model.PSI;
import ontopoly.model.TopicMap;
import ontopoly.sysmodel.OntopolyRepository;
import ontopoly.sysmodel.TopicMapSource;
import org.apache.commons.lang3.StringUtils;

public class ConversionUtils {
  
  private static final LocatorIF psibase = URILocator.create("http://psi.ontopia.net/ontology/");
  private static final LocatorIF xsdbase = URILocator.create("http://www.w3.org/2001/XMLSchema");
  private static final LocatorIF xtmbase = URILocator.create("http://www.topicmaps.org/xtm/1.0/core.xtm");
  private static final LocatorIF teqbase = URILocator.create("http://www.techquila.com/psi/hierarchy/");
  
  public static String upgradeExisting(TopicMap topicMap) {
    UpgradeUtils.upgradeTopicMap(topicMap);
    String referenceId = topicMap.getId();
    OntopolyContext.getOntopolyRepository().registerOntopolyTopicMap(referenceId, topicMap.getName());
    return referenceId;
  }
  
  public static String convertExisting(TopicMap topicMap, String tmname) {
    OntopolyRepository ontopolyRepository = OntopolyContext.getOntopolyRepository();
    TopicMapRepositoryIF repository = ontopolyRepository.getTopicMapRepository();
    String referenceId = inferAndCreateSchema(null, topicMap, tmname, repository);
    OntopolyContext.getOntopolyRepository().registerOntopolyTopicMap(referenceId, topicMap.getName());
    return referenceId;
  }
  
  public static String convertNew(TopicMap oldTopicMap, String tmname, TopicMapSource tmsource) {
    TopicMapRepositoryIF repository = OntopolyContext.getOntopolyRepository().getTopicMapRepository();
    
    try {
        String referenceId = OntopolyContext.getOntopolyRepository().createOntopolyTopicMap(tmsource.getId(), tmname);
        TopicMapIF topicMapIF = OntopolyContext.getOntopolyRepository().getTopicMapRepository()
            .getReferenceByKey(referenceId).createStore(false).getTopicMap();
        
        TopicMap newTopicMap = new TopicMap(topicMapIF, referenceId);

        return inferAndCreateSchema(oldTopicMap, newTopicMap, tmname, repository);
      
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  private static String inferAndCreateSchema(TopicMap oldTopicMap, TopicMap newTopicMap, String tmname, TopicMapRepositoryIF repository) {

    boolean newtopicmap = (oldTopicMap != null);
    
    // create new topic map 
    TopicMapStoreIF store = newTopicMap.getTopicMapIF().getStore(); 
    try {
    
      TopicMapStoreIF oldstore = null;
      LocatorIF oldbaseloc = null;
      if (newtopicmap) {
      
        // get old store
        oldstore = oldTopicMap.getTopicMapIF().getStore();
        oldbaseloc = oldstore.getBaseAddress();
      
      }
      
      // convert topic map
      TopicMapIF tm = store.getTopicMap();
      Collection<LocatorIF> reifier_subinds = new HashSet<LocatorIF>();
      
      LocatorIF versionTopicPSI = URILocator.create("http://psi.ontopia.net/ontology/ted-ontology-version");
      if (newtopicmap) {
        // get hold of old reifier
        TopicIF oreifier = oldstore.getTopicMap().getReifier();
        if (oreifier == null) {
          reifier_subinds = Collections.emptySet();
        } else {
          reifier_subinds = oreifier.getSubjectIdentifiers();
        }
        // merge in old topic map
        try {
          MergeUtils.mergeInto(store.getTopicMap(), oldstore.getTopicMap());
        } finally {
          oldstore.close();
        }
      } else {
        // import TED ontology
        TopicMapReferenceIF ontologyTopicMapReference = repository.getReferenceByKey(OntopolyRepository.ONTOLOGY_TOPIC_MAP_ID);
        if (ontologyTopicMapReference == null) {
          throw new OntopiaRuntimeException("Could not find ontology topic map '" + OntopolyRepository.ONTOLOGY_TOPIC_MAP_ID + "'");
        }
        TopicMapStoreIF ontologyTopicMapStore = ontologyTopicMapReference.createStore(true);
        try {
          MergeUtils.mergeInto(tm, ontologyTopicMapStore.getTopicMap());
        } finally {
          ontologyTopicMapStore.close();
        }
   
        // reify topic map
        TopicMapBuilderIF tmbuilder = tm.getBuilder();
        TopicIF reifier = tm.getReifier();
        if (reifier == null) {
          reifier = tmbuilder.makeTopic();
          tm.setReifier(reifier);
          tmbuilder.makeTopicName(reifier, tmname);
          TopicIF versionTopic  = tm.getTopicBySubjectIdentifier(versionTopicPSI);
          tmbuilder.makeOccurrence(reifier, versionTopic, Float.toString(OntopolyApplication.CURRENT_VERSION_NUMBER));
        }
      }
      
      // copy old reifier reify topic map if not already done
      TopicIF nreifier = tm.getReifier();
      TopicIF oreifier = null;
      Iterator<LocatorIF> iiter = reifier_subinds.iterator();
      while (iiter.hasNext()) {
        LocatorIF reifier_subind = iiter.next();
        oreifier = tm.getTopicBySubjectIdentifier(reifier_subind);
        if (oreifier != null) {
          break;
        }
      }
      TopicMapBuilderIF tmbuilder = tm.getBuilder();
      if (nreifier == null) {
        if (oreifier != null) {
          nreifier = oreifier;
        } else {
          nreifier = tmbuilder.makeTopic();
          tm.setReifier(nreifier);
        }
          
      } else {
        if (oreifier != null && !Objects.equals(oreifier, nreifier)) {
          MergeUtils.mergeInto(nreifier, oreifier);
        }
      }
      // make topic map reifier and instance of on:topic-map
      nreifier.addType(topicByPSI(psibase.resolveAbsolute("topic-map"), tm));
      
      // replace reifier names with new one
      if (newtopicmap) {
        Object[] nrnames = nreifier.getTopicNames().toArray();
        for (int i=0; i < nrnames.length; i++) {
          TopicNameIF tn = (TopicNameIF)nrnames[i];
          tn.remove();
        }
        // String tmname = oldTopicMap.getName();1
        tmbuilder.makeTopicName(nreifier, tmname);
      }
      // Check ontology version:
      TopicIF versionTopic  = tm.getTopicBySubjectIdentifier(versionTopicPSI);
      String versionNumber = Float.toString(OntopolyApplication.CURRENT_VERSION_NUMBER); 
      OccurrenceIF versionOcc = getOccurrenceOfType(nreifier, versionTopic);
      if (versionOcc == null) {
        // create new ontology version occurrence
        tmbuilder.makeOccurrence(nreifier, versionTopic, versionNumber);
      } else {
        // Update ontology version value
        versionOcc.setValue(versionNumber);
      }
      
      // infer TED schema
      inferAndCreateSchema(tm, nreifier);
      
      // run duplicate suppression
      DuplicateSuppressionUtils.removeDuplicates(tm);
      
      TopicMapReferenceIF ref = store.getReference();
      if (ref instanceof XTMTopicMapReference) {
        // use old base address
        LocatorIF newbaseloc = store.getBaseAddress();
        if (oldbaseloc != null) {
          ((AbstractTopicMapStore)store).setBaseAddress(oldbaseloc);
        }
        
        // save topic map with TED ontology
        ((XTMTopicMapReference)ref).save();  
        
        // revert to new base address
        if (oldbaseloc != null) {
          ((AbstractTopicMapStore)store).setBaseAddress(newbaseloc);
        }
        
        // close reference
        ref.close();
      }
      
      // commit changes to topic map
      store.commit();
      
    } catch (Throwable e) {
      if (store != null) {
        store.abort();
      }
      throw new OntopiaRuntimeException(e);
    } finally {
      if (store != null) {
        store.close();
      }
    }
    
    // refresh repository, so that new reference is visible
    repository.refresh();
    
    return newTopicMap.getId();
  }
  
  private static void inferAndCreateSchema(TopicMapIF tm, TopicIF reifier)
    throws InvalidQueryException, MalformedURLException {

    // create tracker instance
    SchemaTracker tracker = new SchemaTracker();
    // track topics and associations
    tracker.trackTopics(tm.getTopics());
    tracker.trackAssociations(tm.getAssociations());
  
    TopicMapBuilderIF tmbuilder = tm.getBuilder();
    QueryProcessorIF queryProcessor = QueryUtils.getQueryProcessor(tm);
    QueryResultIF result = null;
  
    // declaration context
    DeclarationContextIF dc = QueryUtils.parseDeclarations(tm, 
        "using on for i\"http://psi.ontopia.net/ontology/\" " +  
        "using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" " +
        "supertype-of($SUB, $SUP) :-" +          
        " { xtm:superclass-subclass($SUB : xtm:subclass, $SUP : xtm:superclass) |" +
        "   xtm:superclass-subclass($SUB : xtm:subclass, $X : xtm:superclass)," +
        "   supertype-of($X, $SUP) }. " +
        "descendant-of($ANC, $DES) :- " +
        " { xtm:superclass-subclass($ANC : xtm:superclass, $DES : xtm:subclass) |" +
        "   xtm:superclass-subclass($ANC : xtm:superclass, $MID : xtm:subclass)," + 
        "   descendant-of($MID, $DES) }.");
    
    // load superclass-subclass hierarchy
    Map<TopicIF,Collection<TopicIF>> subsup = new HashMap<TopicIF,Collection<TopicIF>>();
    Map<TopicIF,Collection<TopicIF>> supsub = new HashMap<TopicIF,Collection<TopicIF>>();
    result = queryProcessor.execute("select $SUP, $SUB from xtm:superclass-subclass($SUB : xtm:subclass, $SUP : xtm:superclass)?", dc);
    try {
      while (result.next()) {
        TopicIF sup = (TopicIF)result.getValue(0);
        TopicIF sub = (TopicIF)result.getValue(1);
        // subtype to supertype mapping
        Collection<TopicIF> x = subsup.get(sub);
        if (x == null) {
          x = new HashSet<TopicIF>();
          subsup.put(sub, x);
        }
        x.add(sup);
        // supertype to subtype mapping
        Collection<TopicIF> y = supsub.get(sup);
        if (y == null) {
          y = new HashSet<TopicIF>();
          supsub.put(sup, y);
        }
        y.add(sub);
      }
    } finally {
      result.close();
    }
      
    // aggregate all ontology types
    Collection<TopicIF> onto_types = tracker.getOntologyTypes();
  
    // translate name scopes into name types
    ScopeIndexIF sindex = (ScopeIndexIF)tm.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");
    Collection<TopicIF> nstypes = new HashSet<TopicIF>();
    Iterator<TopicIF> nsiter = tracker.getSuspectNameScopes().iterator();
    while (nsiter.hasNext()) {
      TopicIF ntheme = nsiter.next();
      if (onto_types.contains(ntheme) || isTEDTopic(ntheme) || ntheme.equals(reifier)) {
        continue;
      }
      nstypes.add(ntheme);
      
      // translate name scope into name type
      Iterator<TopicNameIF> tniter = sindex.getTopicNames(ntheme).iterator();
      while (tniter.hasNext()) {
        TopicNameIF tn = tniter.next();
        tn.setType(ntheme);
        tn.removeTheme(ntheme);
        // WARN: what if basename have other themes in its name scope?
      }
      
      // register name field on topic type
      Iterator<TopicIF> nstiter = tracker.getNameScopeTopicTypes(ntheme).iterator();
      while (nstiter.hasNext()) {
        TopicIF ttype = nstiter.next();
        if (ttype == null) {
          continue; // HACK: don't know what to do here.
        }
        TopicIF nfield = registerNameType(ntheme, tm);
        registerNameField(ttype, nfield, 
                      topicByPSI(psibase.resolveAbsolute("cardinality-0-M"), tm), tm);            
      }      
    }
    
    // untyped topics
    Iterator<TopicIF> utyped = tracker.getUntypedTopics().iterator();
    while (utyped.hasNext()) {
      TopicIF untyped = utyped.next();
      if (untyped == null ||
          onto_types.contains(untyped) ||
          isTEDTopic(untyped) ||
          untyped == reifier ||
          nstypes.contains(untyped) ||
          supsub.containsKey(untyped) || 
          subsup.containsKey(untyped)) {
        continue;
      }
      registerUntypedTopic(untyped, tm); 
    }
  
    // add super types to list of topic types
    Collection<TopicIF> all_topic_types = new HashSet<TopicIF>(tracker.getTopicTypes());
    all_topic_types.addAll(supsub.keySet());
  
    // get topmost super types
    Collection<TopicIF> topmost_types = getTopMostTypes(all_topic_types, subsup);
    
    // create schema
    Iterator<TopicIF> ttypes = all_topic_types.iterator();
    while (ttypes.hasNext()) {
      TopicIF ttype = ttypes.next();
      if (ttype == null || isTEDTopic(ttype)) {
        continue;
      }
  
      // topic type      
      registerTopicType(ttype, tm);
  
      // register default name
      if (topmost_types.contains(ttype)) {
        registerDefaultNameField(ttype, tm);
      }
  
      // subject locator
      if (!isSubjectLocatorDeclaredOnSuperType(ttype, tracker, subsup)) {
        int maxcard = getBroadestSubjectLocatorMaxCardinality(ttype, tracker, supsub);
        if (maxcard > 0) {
          TopicIF subloc_card = getCardinalityTopic(getBroadestSubjectLocatorMinCardinality(ttype, tracker, supsub),
                                                    maxcard, tm);
          registerSubjectLocatorField(ttype, subloc_card, tm);
        }
      }
      
      // subject indicator
      if (!isSubjectIndicatorDeclaredOnSuperType(ttype, tracker, subsup)) {
        int maxcard = getBroadestSubjectIndicatorMaxCardinality(ttype, tracker, supsub);
        if (maxcard > 0) {
          TopicIF subind_card = getCardinalityTopic(getBroadestSubjectIndicatorMinCardinality(ttype, tracker, supsub),
                                                    maxcard, tm);
          registerSubjectIndicatorField(ttype, subind_card, tm);
        }
      }
      
      // name types
      Collection<TopicIF> n_decl_on_supertype = getNamesDeclaredOnSuperType(ttype, tracker, subsup);
      Iterator<TopicIF> ntypes = tracker.getNameTypes(ttype).iterator();
      while (ntypes.hasNext()) {
        TopicIF ntype = ntypes.next();
        if (ntype == null || isTEDTopic(ntype) || n_decl_on_supertype.contains(ntype)) {
          continue;
        }        
        TopicIF cardinality = getCardinalityTopic(getBroadestNameTypeMinCardinality(ttype, ntype, tracker, supsub),
                                                  getBroadestNameTypeMaxCardinality(ttype, ntype, tracker, supsub), tm);
        TopicIF nfield = registerNameType(ntype, tm);
        registerNameField(ttype, nfield, cardinality, tm);        
      }      
  
      // external occurrence types
      Collection<TopicIF> oe_decl_on_supertype = getExternalOccurrencesDeclaredOnSuperType(ttype, tracker, subsup);
      TopicIF datatype_uri = topicByPSI(xsdbase.resolveAbsolute("#anyURI"), tm);
      Iterator<TopicIF> oetypes = tracker.getExternalOccurrenceTypes(ttype).iterator();
      while (oetypes.hasNext()) {
        TopicIF oetype = oetypes.next();
        if (oetype == null || isTEDTopic(oetype) || oe_decl_on_supertype.contains(oetype)) {
          continue;
        }
        TopicIF cardinality = getCardinalityTopic(getBroadestExternalOccurrenceTypeMinCardinality(ttype, oetype, tracker, supsub),
                                                  getBroadestExternalOccurrenceTypeMaxCardinality(ttype, oetype, tracker, supsub), tm);
        oetype = registerOccurrenceType(oetype, datatype_uri, tm);
        registerOccurrenceField(ttype, oetype, cardinality, tm);        
      }      
      
      // internal occurrence types
      Collection<TopicIF> oi_decl_on_supertype = getInternalOccurrencesDeclaredOnSuperType(ttype, tracker, subsup);
      TopicIF datatype_string = topicByPSI(xsdbase.resolveAbsolute("#string"), tm);
      Iterator<TopicIF> oitypes = tracker.getInternalOccurrenceTypes(ttype).iterator();
      while (oitypes.hasNext()) {
        TopicIF oitype = oitypes.next();
        if (oitype == null || isTEDTopic(oitype) || oi_decl_on_supertype.contains(oitype)) {
          continue;
        }
        TopicIF cardinality = getCardinalityTopic(getBroadestInternalOccurrenceTypeMinCardinality(ttype, oitype, tracker, supsub),
                                                  getBroadestInternalOccurrenceTypeMaxCardinality(ttype, oitype, tracker, supsub), tm);
        oitype = registerOccurrenceType(oitype, datatype_string, tm);
        registerOccurrenceField(ttype, oitype, cardinality, tm);        
      }      
    }    
  
    // association types
    Collection<TopicIF> excluded_atypes = new HashSet<TopicIF>();
    excluded_atypes.add(topicByPSI(xtmbase.resolveAbsolute("#superclass-subclass"), tm));
    excluded_atypes.add(topicByPSI(teqbase.resolveAbsolute("#hierarchical-relation-type"), tm));
    
    Iterator<TopicIF> atypes = tracker.getAssociationTypes().iterator();
    while (atypes.hasNext()) {
      TopicIF atype = atypes.next();
      if (atype == null || isTEDTopic(atype) || excluded_atypes.contains(atype)) {
        continue;
      }
  
      // association type
      atype = registerAssociationType(atype, tm);
  
      // symmetric association
      if (tracker.isSymmetricAssociationType(atype)) {
        addAssociation1(psibase.resolveAbsolute("is-symmetric"), 
                        atype, psibase.resolveAbsolute("association-type"), tm);
      }
      
      // role types
      Iterator<TopicIF> rtypes = tracker.getRoleTypes(atype).iterator();
      while (rtypes.hasNext()) {
        TopicIF rtype = rtypes.next();
        if (rtype == null || isTEDTopic(rtype)) {
          continue;
        }
  
        // role type
        rtype = registerRoleType(rtype, tm);
  
        // count player type instances
        Collection<TopicIF> ptypes = tracker.getPlayerTypes(atype, rtype);
        int ptypes_count = 0;
        Iterator<TopicIF> ptiter = ptypes.iterator();
        while (ptiter.hasNext()) {
          ptypes_count += tracker.getTopicTypeInstances(ptiter.next());
        }
  
        // use search-dialog if drop-down list too long (> 50 elements)
        TopicIF interfaceControl;
        if (ptypes_count > 50) {
          interfaceControl = topicByPSI(psibase.resolveAbsolute("search-dialog"), tm);
        } else {
          interfaceControl = topicByPSI(psibase.resolveAbsolute("drop-down-list"), tm);
        }
        TopicIF rfield = registerRoleField(atype, rtype, interfaceControl, tm);
        
        ptiter = ptypes.iterator();
        while (ptiter.hasNext()) {
          TopicIF ptype = ptiter.next();
          if (ptype == null) {
            ptype = getUntypedTopic(psibase, tm);
          } else if (isTEDTopic(ptype)) {
            continue;
          }          
  
          if (!isRoleDeclaredOnSuperType(ptype, ptypes, subsup)) {
            TopicIF cardinality = getCardinalityTopic(getBroadestPlayerTypeMinCardinality(atype, rtype, ptype, tracker, supsub),
                                                      getBroadestPlayerTypeMaxCardinality(atype, rtype, ptype, tracker, supsub), tm);
            
            registerPlayerTypeField(rfield, ptype, cardinality, tm);
          }
        }
      }
    }
  
    // remove identity fields duplicated on subtypes
    // remove name fields duplicated on subtypes
  
    // remove occurrence fields duplicated on subtypes
    
    // remove fields duplicated on subtypes
    result = queryProcessor.execute("select $A1 from direct-instance-of($TTYPE, on:topic-type), descendant-of($XTYPE, $TTYPE), " +
        "role-player($R1, $TTYPE), type($R1, on:field-owner), " + 
        "association-role($A1, $R1), type($A1, on:has-field), " +
        "association-role($A1, $R2), $R1 /= $R2, type($R2, on:field-definition), role-player($R2, $FIELD), " +
        "role-player($R3, $XTYPE), type($R3, on:field-owner), " + 
        "association-role($A2, $R3), type($A2, on:has-field), " +
        "association-role($A2, $R4), $R3 /= $R4, type($R4, on:field-definition), role-player($R4, $FIELD)?", dc);
    try {
      while (result.next()) {
        AssociationIF assoc = (AssociationIF)result.getValue(0);
        assoc.remove();
      }
    } finally {
      result.close();
    }
  
    // generate field order
    TopicIF ted_field_order = topicByPSI(psibase.resolveAbsolute("field-order"), tm);
    
    List<Object[]> fields = new ArrayList<Object[]>();
    result = queryProcessor.execute("/* #OPTION: optimizer.reorder=false */ " +
        "select $topic, $owner, $field from " +
        "direct-instance-of($topic, on:topic-type), { descendant-of($owner, $topic) | $owner = $topic }, " +
        "role-player($R1, $owner), type($R1, on:field-owner), " +
        "association-role($A, $R1), type($A, on:has-field), " +
        "association-role($A, $R2), $R1 /= $R2, type($R2, on:field-definition), role-player($R2, $field), "+
        "not(instance-of($topic, on:system-topic))" +
        "order by $topic ?", dc);
    try {
      while (result.next()) {
        fields.add(result.getValues());
      }
    } finally {
      result.close();
    }
  
    // sort the fields
    Collections.sort(fields, new FieldsComparator(tm, psibase));
    
    // update/create field order values
    int fOrder = 1000;
    TopicIF prevTopic = null;
    Iterator<Object[]> fiter = fields.iterator();
    while (fiter.hasNext()) {
      Object[] f = fiter.next();
      TopicIF curTopic = (TopicIF)f[0];
      if (prevTopic != curTopic) {
        fOrder = 1000;
        prevTopic = curTopic;
      }
      
      // create new field order
      OccurrenceIF occ = tmbuilder.makeOccurrence(curTopic, ted_field_order, StringUtils.leftPad(Integer.toString(fOrder), 9, '0'));
      occ.addTheme((TopicIF)f[2]);
      fOrder = fOrder + 1000;
    }
  }

  private static class FieldsComparator implements Comparator<Object> {
    private TopicIF ted_nt;
    private TopicIF ted_si;
    private TopicIF ted_sl;
    private TopicIF ted_ot;
    private TopicIF ted_at;
    private TopicIF ted_untyped_name;

    private FieldsComparator(TopicMapIF tm, LocatorIF psibase) throws MalformedURLException {
      ted_nt = topicByPSI(psibase.resolveAbsolute("name-type"), tm);
      ted_si = topicByPSI(psibase.resolveAbsolute("subject-identifier"), tm);
      ted_sl = topicByPSI(psibase.resolveAbsolute("subject-locator"), tm);
      ted_ot = topicByPSI(psibase.resolveAbsolute("occurrence-type"), tm);
      ted_at = topicByPSI(psibase.resolveAbsolute("association-type"), tm);      
      ted_untyped_name = topicByPSI(PSI.TMDM_TOPIC_NAME, tm);
    }
  
    @Override
    public int compare(Object o1, Object o2) {
        Object[] f1 = (Object[])o1;
        Object[] f2 = (Object[])o2;

        // sort by topic type
        int tids = ((TopicIF)f1[0]).getObjectId().compareTo(((TopicIF)f2[0]).getObjectId());
        if (tids != 0) {
          return tids;
        }

        // sort by field class
        int fkey1 = 1000;
        int fkey2 = 1000;

        
        if (ted_sl.equals(f1[2])) {
          fkey1 = 2;
        } else if (ted_si.equals(f1[2])) {
          fkey1 = 3;
        } else {
          Iterator<TopicIF> fit = ((TopicIF)f1[2]).getTypes().iterator();
          while (fit.hasNext()) {
            TopicIF ft1 = fit.next();          
            if (ted_nt.equals(ft1)) {
              if (ted_untyped_name.equals(f1[2])) {
                fkey1 = 0;
              } else {
                fkey1 = 1;
              }
            } 
            else if (ted_ot.equals(ft1)) {
              fkey1 = 4;
            } else if (ted_at.equals(ft1)) {
              fkey1 = 5;
            } else {
              continue;
            }
            break;
          }
        }

        if (ted_sl.equals(f2[2])) {
          fkey2 = 2;
        } else if (ted_si.equals(f2[2])) {
          fkey2 = 3;
        } else {
          Iterator<TopicIF> fit = ((TopicIF)f2[2]).getTypes().iterator();
          while (fit.hasNext()) {
            TopicIF ft2 = fit.next();          
          
            if (ted_nt.equals(ft2)) {
              if (ted_untyped_name.equals(f2[2])) {
                fkey2 = 0;
              } else {
                fkey2 = 1;
              }
            } 
            else if (ted_ot.equals(ft2)) {
              fkey2 = 4;
            } else if (ted_at.equals(ft2)) {
              fkey2 = 5;
            } else {
              continue;
            }
            break;
          }          
        }

        //! if (fkey1 == 1000) System.out.println("FT1 type unknown: " + f1[2]);
        //! if (fkey2 == 1000) System.out.println("FT2 type unknown: " + f2[2]);

        if (fkey1 != fkey2) {
          return (fkey1 > fkey2 ? 1 : -1);
        }

        // sort by field type name
        String fn1 = TopicStringifiers.toString((TopicIF)(fkey1 == 5 ? f1[3] : f1[2]));
        String fn2 = TopicStringifiers.toString((TopicIF)(fkey2 == 5 ? f2[3] : f2[2]));
        return fn1.compareTo(fn2);
    }
  }
  
  private static void registerTopicType(TopicIF topic, TopicMapIF tm) throws MalformedURLException {
    // topic to be an instance of on:topic-type
    TopicIF topicType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("topic-type"));
    topic.addType(topicType);
  }

  private static void registerUntypedTopic(TopicIF topic, TopicMapIF tm) throws MalformedURLException {
    // topic to be an instance of on:untyped-topic
    topic.addType(getUntypedTopic(psibase, tm));
  }

  private static TopicIF registerNameType(TopicIF ntype, TopicMapIF tm) throws MalformedURLException {
    // topic to be an instance of on:name-type
    TopicIF nameType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("name-type"));
    ntype.addType(nameType);
    return ntype;
  }
  
  private static TopicIF registerOccurrenceType(TopicIF otype, TopicIF datatype, TopicMapIF tm) throws MalformedURLException {
    // topic to be an instance of on:occurrence-type
    TopicIF occType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("occurrence-type"));
    otype.addType(occType);
    // datatype
    addAssociation2(psibase.resolveAbsolute("has-datatype"), 
        otype, psibase.resolveAbsolute("field-definition"),
        datatype, psibase.resolveAbsolute("datatype"), tm);
    return otype;
  }
  
  private static TopicIF registerAssociationType(TopicIF atype, TopicMapIF tm) throws MalformedURLException {
    // topic to be an instance of on:association-type
    TopicIF assocType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("association-type"));
    atype.addType(assocType);
    return atype;
  }
  
  private static TopicIF registerRoleType(TopicIF rtype, TopicMapIF tm) throws MalformedURLException {
    // topic to be an instance of on:role-type
    TopicIF roleType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("role-type"));
    rtype.addType(roleType);
    return rtype;
  }
  
  private static TopicIF registerSubjectLocatorField(TopicIF ttype, TopicIF cardinality, TopicMapIF tm) throws MalformedURLException {
    TopicIF ifield = findIdentityField(topicByPSI(psibase.resolveAbsolute("subject-locator"), tm), tm);
    registerField(ttype, ifield, cardinality, tm);
    return ifield;
  }

  private static TopicIF registerSubjectIndicatorField(TopicIF ttype, TopicIF cardinality, TopicMapIF tm) throws MalformedURLException {
    TopicIF ifield = findIdentityField(topicByPSI(psibase.resolveAbsolute("subject-identifier"), tm), tm);
    registerField(ttype, ifield, cardinality, tm);
    return ifield;
  }

//  private static TopicIF registerItemIdentifierField(TopicIF ttype, TopicIF cardinality, TopicMapIF tm) throws MalformedURLException {
//    TopicIF ifield = findIdentityField(topicByPSI(psibase.resolveAbsolute("item-identifier"), tm), tm);
//    registerField(ttype, ifield, cardinality, tm);
//    return ifield;
//  }

  private static TopicIF findIdentityField(TopicIF itype, TopicMapIF tm) throws MalformedURLException {
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    QueryResultIF qr = null;
    TopicIF ifield = null;
    try {
      Map<String,TopicIF> params = Collections.singletonMap("itype", itype);
      qr = qp.execute("using on for i\"http://psi.ontopia.net/ontology/\"\n" +
          "select $ifield from on:has-identity-type(%itype% : on:identity-type, $ofield : on:identity-field)?", params);
      if (qr.next()) {
        ifield = (TopicIF)qr.getValue(0);
      }
    } catch (Exception e) {
      if (qr != null) {
        qr.close();
      }
    }
    if (ifield == null) {
      TopicIF fieldType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("identity-field"));
      ifield = tm.getBuilder().makeTopic(fieldType);
      addAssociation2(psibase.resolveAbsolute("has-identity-type"), 
          itype, psibase.resolveAbsolute("identity-type"),
          ifield, psibase.resolveAbsolute("identity-field"), tm);      
    }
    return ifield;
  }
  
  private static void registerDefaultNameField(TopicIF topic, TopicMapIF tm) throws MalformedURLException {
    // mandatory default name
    registerNameField(topic, topicByPSI(PSI.TMDM_TOPIC_NAME, tm), 
        topicByPSI(psibase.resolveAbsolute("cardinality-1-1"), tm), tm);
  }

  private static TopicIF registerNameField(TopicIF ttype, TopicIF ntype, TopicIF cardinality, TopicMapIF tm) throws MalformedURLException {
    TopicIF nfield = findNameField(ntype, tm);
    registerField(ttype, nfield, cardinality, tm);
    return nfield;
  }

  private static TopicIF findNameField(TopicIF ntype, TopicMapIF tm) throws MalformedURLException {
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    QueryResultIF qr = null;
    TopicIF nfield = null;
    try {
      Map<String,TopicIF> params = Collections.singletonMap("ntype", ntype);
      qr = qp.execute("using on for i\"http://psi.ontopia.net/ontology/\"\n" +
          "select $nfield from on:has-name-type(%ntype% : on:name-type, $nfield : on:name-field)?", params);
      if (qr.next()) {
        nfield = (TopicIF)qr.getValue(0);
      }
    } catch (Exception e) {
      if (qr != null) {
        qr.close();
      }
    }
    if (nfield == null) {
      TopicIF fieldType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("name-field"));
      nfield = tm.getBuilder().makeTopic(fieldType);
      addAssociation2(psibase.resolveAbsolute("has-name-type"), 
          ntype, psibase.resolveAbsolute("name-type"),
          nfield, psibase.resolveAbsolute("name-field"), tm);      
    }
    return nfield;
  }
  
  private static void registerField(TopicIF ttype, TopicIF xfield, TopicIF cardinality, TopicMapIF tm) throws MalformedURLException {
    addAssociation2(psibase.resolveAbsolute("has-field"), 
        ttype, psibase.resolveAbsolute("field-owner"),
        xfield, psibase.resolveAbsolute("field-definition"), tm);
    addAssociation2(psibase.resolveAbsolute("has-cardinality"), 
        xfield, psibase.resolveAbsolute("field-definition"), 
        cardinality, psibase.resolveAbsolute("cardinality"), tm);
  }
  
  private static TopicIF registerOccurrenceField(TopicIF ttype, TopicIF otype, TopicIF cardinality, TopicMapIF tm) throws MalformedURLException {
    TopicIF ofield = findOccurrenceField(otype, tm);
    registerField(ttype, ofield, cardinality, tm);
    addAssociation2(psibase.resolveAbsolute("has-datatype"), 
        ofield, psibase.resolveAbsolute("field-definition"), 
        topicByPSI(psibase.resolveAbsolute("datatype-string"), tm), psibase.resolveAbsolute("datatype"), tm);
    return ofield;
  }

  private static TopicIF findOccurrenceField(TopicIF otype, TopicMapIF tm) throws MalformedURLException {
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    QueryResultIF qr = null;
    TopicIF ofield = null;
    try {
      Map<String,TopicIF> params = Collections.singletonMap("otype", otype);
      qr = qp.execute("using on for i\"http://psi.ontopia.net/ontology/\"\n" +
          "select $ofield from on:has-occurrence-type(%otype% : on:occurrence-type, $ofield : on:occurrence-field)?", params);
      if (qr.next()) {
        ofield = (TopicIF)qr.getValue(0);
      }
    } catch (Exception e) {
      if (qr != null) {
        qr.close();
      }
    }
    if (ofield == null) {
      TopicIF fieldType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("occurrence-field"));
      ofield = tm.getBuilder().makeTopic(fieldType);
      addAssociation2(psibase.resolveAbsolute("has-occurrence-type"), 
          otype, psibase.resolveAbsolute("occurrence-type"),
          ofield, psibase.resolveAbsolute("occurrence-field"), tm);      
    }
    return ofield;
  }

  private static TopicIF registerRoleField(TopicIF atype, TopicIF rtype, TopicIF interfaceControl, TopicMapIF tm) throws MalformedURLException {
    TopicIF afield = findAssociationField(atype, tm);
    TopicIF rfield = findRoleField(afield, rtype, tm);
//    addAssociation2(psibase.resolveAbsolute("has-association-field"), 
//        afield, psibase.resolveAbsolute("association-field"),
//        rfield, psibase.resolveAbsolute("role-field"), tm);
    return rfield;
  }

  private static TopicIF findAssociationField(TopicIF atype, TopicMapIF tm) throws MalformedURLException {
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    QueryResultIF qr = null;
    TopicIF afield = null;
    try {
      Map<String,TopicIF> params = Collections.singletonMap("atype", atype);
      qr = qp.execute("using on for i\"http://psi.ontopia.net/ontology/\"\n" +
          "select $afield from on:has-association-type(%atype% : on:association-type, $afield : on:association-field)?", params);
      if (qr.next()) {
        afield = (TopicIF)qr.getValue(0);
      }
    } catch (Exception e) {
      if (qr != null) {
        qr.close();
      }
    }
    if (afield == null) {
      TopicIF fieldType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("association-field"));
      afield = tm.getBuilder().makeTopic(fieldType);
      addAssociation2(psibase.resolveAbsolute("has-association-type"), 
          atype, psibase.resolveAbsolute("association-type"),
          afield, psibase.resolveAbsolute("association-field"), tm);      
    }
    return afield;
  }

  private static TopicIF findRoleField(TopicIF afield, TopicIF rtype, TopicMapIF tm) throws MalformedURLException {
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    QueryResultIF qr = null;
    TopicIF rfield = null;
    try {
      Map<String,TopicIF> params = new HashMap<String,TopicIF>(2);
      params.put("afield", afield);
      params.put("rtype", rtype);
      qr = qp.execute("using on for i\"http://psi.ontopia.net/ontology/\"\n" +
          "select $rfield from on:has-association-field(%afield% : on:association-field, $rfield : on:role-field), " + 
          "on:has-role-type($rfield : on:role-field, %rtype% : on:role-type)?", params);
      if (qr.next()) {
        rfield = (TopicIF)qr.getValue(0);
      }
    } catch (Exception e) {
      if (qr != null) {
        qr.close();
      }
    }
    if (rfield == null) {
      TopicIF fieldType = tm.getTopicBySubjectIdentifier(psibase.resolveAbsolute("role-field"));
      rfield = tm.getBuilder().makeTopic(fieldType);
      addAssociation2(psibase.resolveAbsolute("has-role-type"), 
          rtype, psibase.resolveAbsolute("role-type"),
          rfield, psibase.resolveAbsolute("role-field"), tm);      
      addAssociation2(psibase.resolveAbsolute("has-association-field"), 
          afield, psibase.resolveAbsolute("association-field"),
          rfield, psibase.resolveAbsolute("role-field"), tm);      
    }
    return rfield;
  }

  private static void registerPlayerTypeField(TopicIF rfield, TopicIF ptype, TopicIF cardinality, TopicMapIF tm) throws MalformedURLException {
    registerField(ptype, rfield, cardinality, tm);
  }
  
  private static void addAssociation1(LocatorIF atypePSI, 
      TopicIF player1, LocatorIF rtype1PSI, TopicMapIF tm) throws MalformedURLException {
    TopicMapBuilderIF tmbuilder = tm.getBuilder();
    AssociationIF assoc = tmbuilder.makeAssociation(topicByPSI(atypePSI, tm));
    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype1PSI, tm), player1);
  } 
  
  private static void addAssociation2(LocatorIF atypePSI, 
      TopicIF player1, LocatorIF rtype1PSI, 
      TopicIF player2, LocatorIF rtype2PSI, TopicMapIF tm) throws MalformedURLException {
    TopicMapBuilderIF tmbuilder = tm.getBuilder();
    AssociationIF assoc = tmbuilder.makeAssociation(topicByPSI(atypePSI, tm));
    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype1PSI, tm), player1);
    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype2PSI, tm), player2);
  } 
  
//  private static void addAssociation3(LocatorIF atypePSI, 
//      TopicIF player1, LocatorIF rtype1PSI, 
//      TopicIF player2, LocatorIF rtype2PSI, 
//      TopicIF player3, LocatorIF rtype3PSI, TopicMapIF tm) throws MalformedURLException {
//    TopicMapBuilderIF tmbuilder = tm.getBuilder();
//    AssociationIF assoc = tmbuilder.makeAssociation(topicByPSI(atypePSI, tm));
//    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype1PSI, tm), player1);
//    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype2PSI, tm), player2);
//    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype3PSI, tm), player3);
//  } 
//  
//  private static void addAssociation4(LocatorIF atypePSI, 
//      TopicIF player1, LocatorIF rtype1PSI, 
//      TopicIF player2, LocatorIF rtype2PSI, 
//      TopicIF player3, LocatorIF rtype3PSI, 
//      TopicIF player4, LocatorIF rtype4PSI, TopicMapIF tm) throws MalformedURLException {
//    TopicMapBuilderIF tmbuilder = tm.getBuilder();
//    AssociationIF assoc = tmbuilder.makeAssociation(topicByPSI(atypePSI, tm));
//    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype1PSI, tm), player1);
//    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype2PSI, tm), player2);
//    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype3PSI, tm), player3);
//    tmbuilder.makeAssociationRole(assoc, topicByPSI(rtype4PSI, tm), player4);
//  } 

  private static TopicIF topicByPSI(LocatorIF psiloc, TopicMapIF tm) throws MalformedURLException {
    TopicIF topic = tm.getTopicBySubjectIdentifier(psiloc);
    if (topic == null) {
      throw new OntopiaRuntimeException("Could not find topic with PSI '" + psiloc + "'");
    }
    return topic;
  }
  
  private static boolean isTEDTopic(TopicIF topic) {
    Iterator<LocatorIF> iter = topic.getSubjectIdentifiers().iterator();
    while (iter.hasNext()) {
      LocatorIF loc = iter.next();
      String address = loc.getAddress();
      if (address.startsWith("http://psi.ontopia.net/ontology/") ||
          address.startsWith("http://www.techquila.com/psi/hierarchy/")) {
        return true;
      }
    }
    return false;
  }
  
  private static TopicIF getUntypedTopic(LocatorIF psibase, TopicMapIF tm) throws MalformedURLException {
    LocatorIF psi = psibase.resolveAbsolute("untyped-topic");
    TopicIF untyped = tm.getTopicBySubjectIdentifier(psi);
    if (untyped == null) {
      // create untyped type
      TopicMapBuilderIF builder = tm.getBuilder(); 
      untyped = builder.makeTopic();
      untyped.addSubjectIdentifier(psi);
      builder.makeTopicName(untyped, "Untyped topic");
      // register topic type
      registerTopicType(untyped, tm);
      // register default name
      registerDefaultNameField(untyped, tm);
    }
    return untyped;
  }

  protected static TopicIF getCardinalityTopic(int mincard, int maxcard, TopicMapIF tm) throws MalformedURLException {
    //! mincard = 0; // HACK! ignoring min cardinality for now as it is harder to compute
    if (maxcard == 1) {
      if (mincard == 1) {
        return topicByPSI(psibase.resolveAbsolute("cardinality-1-1"), tm);
      } else {
        return topicByPSI(psibase.resolveAbsolute("cardinality-0-1"), tm);
      }
    } else if (maxcard > 1) {
      if (mincard > 0) {
        return topicByPSI(psibase.resolveAbsolute("cardinality-1-M"), tm);
      } else {
        return topicByPSI(psibase.resolveAbsolute("cardinality-0-M"), tm);
      }
    } else {
      return topicByPSI(psibase.resolveAbsolute("cardinality-0-M"), tm);
    }
  }

  protected TopicIF getCardinalityTopic(int cardinalityCount, TopicMapIF tm) throws MalformedURLException {
    if (cardinalityCount <= 1) {
      return topicByPSI(psibase.resolveAbsolute("cardinality-0-1"), tm);
    } else {
      return topicByPSI(psibase.resolveAbsolute("cardinality-0-M"), tm);
    }
  }

  protected static boolean isSubjectLocatorDeclaredOnSuperType(TopicIF ttype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> subsup) {
    Collection<TopicIF> supertypes = subsup.get(ttype);
    if (supertypes == null) {
      return false;
    }
    Iterator<TopicIF> iter = supertypes.iterator();
    while (iter.hasNext()) {
      TopicIF supertype = iter.next();
      if (tracker.getSubjectLocatorMaxCardinality(supertype) > 0) {
        return true;
      }

      boolean onsup = isSubjectLocatorDeclaredOnSuperType(supertype, tracker, subsup);
      if (onsup) {
        return true;
      }
    }
    return false;
  }

  protected static boolean isSubjectIndicatorDeclaredOnSuperType(TopicIF ttype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> subsup) {
    Collection<TopicIF> supertypes = subsup.get(ttype);
    if (supertypes == null) {
      return false;
    }
    Iterator<TopicIF> iter = supertypes.iterator();
    while (iter.hasNext()) {
      TopicIF supertype = iter.next();
      if (tracker.getSubjectIndicatorMaxCardinality(supertype) > 0) {
        return true;
      }

      boolean onsup = isSubjectIndicatorDeclaredOnSuperType(supertype, tracker, subsup);
      if (onsup) {
        return true;
      }
    }
    return false;
  }

  protected static Collection<TopicIF> getNamesDeclaredOnSuperType(TopicIF ttype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> subsup) {
    Collection<TopicIF> supertypes = subsup.get(ttype);
    if (supertypes == null) {
      return Collections.emptySet();
    }
    Collection<TopicIF> result = new HashSet<TopicIF>();
    Iterator<TopicIF> iter = supertypes.iterator();
    while (iter.hasNext()) {
      TopicIF supertype = iter.next();
      result.addAll(tracker.getNameTypes(supertype));
      result.addAll(getNamesDeclaredOnSuperType(supertype, tracker, subsup));
    }
    return result;
  }

  protected static Collection<TopicIF> getInternalOccurrencesDeclaredOnSuperType(TopicIF ttype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> subsup) {
    Collection<TopicIF> supertypes = subsup.get(ttype);
    if (supertypes == null) {
      return Collections.emptySet();
    }
    Collection<TopicIF> result = new HashSet<TopicIF>();
    Iterator<TopicIF> iter = supertypes.iterator();
    while (iter.hasNext()) {
      TopicIF supertype = iter.next();
      result.addAll(tracker.getInternalOccurrenceTypes(supertype));
      result.addAll(getInternalOccurrencesDeclaredOnSuperType(supertype, tracker, subsup));
    }
    return result;
  }

  protected static Collection<TopicIF> getExternalOccurrencesDeclaredOnSuperType(TopicIF ttype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> subsup) {
    Collection<TopicIF> supertypes = subsup.get(ttype);
    if (supertypes == null) {
      return Collections.emptySet();
    }
    Collection<TopicIF> result = new HashSet<TopicIF>();
    Iterator<TopicIF> iter = supertypes.iterator();
    while (iter.hasNext()) {
      TopicIF supertype = iter.next();
      result.addAll(tracker.getExternalOccurrenceTypes(supertype));
      result.addAll(getExternalOccurrencesDeclaredOnSuperType(supertype, tracker, subsup));
    }
    return result;
  }

  protected static boolean isRoleDeclaredOnSuperType(TopicIF ttype, Collection<TopicIF> ptypes, Map<TopicIF,Collection<TopicIF>> subsup) {
    Collection<TopicIF> supertypes = subsup.get(ttype);
    if (supertypes == null) {
      return false;
    }
    Iterator<TopicIF> iter = supertypes.iterator();
    while (iter.hasNext()) {
      TopicIF supertype = (TopicIF)iter.next();
      if (ptypes.contains(supertype)) {
        return true;
      }

      boolean onsup = isRoleDeclaredOnSuperType(supertype, ptypes, subsup);
      if (onsup) {
        return true;
      }      
    }
    return false;
  }

  protected static int getBroadestSubjectLocatorMinCardinality(TopicIF ttype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getSubjectLocatorMinCardinality(ttype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestSubjectLocatorMinCardinality(subtype, tracker, supsub);
      if (card < cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestSubjectLocatorMaxCardinality(TopicIF ttype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getSubjectLocatorMaxCardinality(ttype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestSubjectLocatorMaxCardinality(subtype, tracker, supsub);
      if (card < cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestSubjectIndicatorMinCardinality(TopicIF ttype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getSubjectIndicatorMinCardinality(ttype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestSubjectIndicatorMinCardinality(subtype, tracker, supsub);
      if (card < cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestSubjectIndicatorMaxCardinality(TopicIF ttype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getSubjectIndicatorMaxCardinality(ttype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestSubjectIndicatorMaxCardinality(subtype, tracker, supsub);
      if (card < cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestNameTypeMinCardinality(TopicIF ttype, TopicIF ntype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getNameTypeMinCardinality(ttype, ntype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestNameTypeMinCardinality(subtype, ntype, tracker, supsub);
      if (card > cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestNameTypeMaxCardinality(TopicIF ttype, TopicIF ntype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getNameTypeMaxCardinality(ttype, ntype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestNameTypeMaxCardinality(subtype, ntype, tracker, supsub);
      if (card > cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestExternalOccurrenceTypeMinCardinality(TopicIF ttype, TopicIF oetype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getExternalOccurrenceTypeMinCardinality(ttype, oetype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestExternalOccurrenceTypeMinCardinality(subtype, oetype, tracker, supsub);
      if (card < cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestExternalOccurrenceTypeMaxCardinality(TopicIF ttype, TopicIF oetype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getExternalOccurrenceTypeMaxCardinality(ttype, oetype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestExternalOccurrenceTypeMaxCardinality(subtype, oetype, tracker, supsub);
      if (card > cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestInternalOccurrenceTypeMinCardinality(TopicIF ttype, TopicIF oitype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getInternalOccurrenceTypeMinCardinality(ttype, oitype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestInternalOccurrenceTypeMinCardinality(subtype, oitype, tracker, supsub);
      if (card < cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestInternalOccurrenceTypeMaxCardinality(TopicIF ttype, TopicIF oitype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ttype);
    int cardinality = tracker.getInternalOccurrenceTypeMaxCardinality(ttype, oitype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestInternalOccurrenceTypeMaxCardinality(subtype, oitype, tracker, supsub);
      if (card > cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestPlayerTypeMinCardinality(TopicIF atype, TopicIF rtype, TopicIF ptype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ptype);
    int cardinality = tracker.getPlayerTypeMinCardinality(atype, rtype, ptype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestPlayerTypeMinCardinality(atype, rtype, subtype, tracker, supsub);
      if (card < cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static int getBroadestPlayerTypeMaxCardinality(TopicIF atype, TopicIF rtype, TopicIF ptype, SchemaTracker tracker, Map<TopicIF,Collection<TopicIF>> supsub) {
    Collection<TopicIF> subtypes = supsub.get(ptype);
    int cardinality = tracker.getPlayerTypeMaxCardinality(atype, rtype, ptype);
    if (subtypes == null) {
      return cardinality;
    }
    Iterator<TopicIF> iter = subtypes.iterator();
    while (iter.hasNext()) {
      TopicIF subtype = iter.next();      
      int card = getBroadestPlayerTypeMaxCardinality(atype, rtype, subtype, tracker, supsub);
      if (card > cardinality) {
        cardinality = card;
      }
    }
    return cardinality;
  }

  protected static Collection<TopicIF> getTopMostTypes(Collection<TopicIF> ttypes, Map<TopicIF,Collection<TopicIF>> subsup) {
    Collection<TopicIF> result = new HashSet<TopicIF>();
    Iterator<TopicIF> iter = ttypes.iterator();
    while (iter.hasNext()) {
      TopicIF ttype = iter.next();
      if (!subsup.containsKey(ttype)) {
        result.add(ttype);
      }
    }
    return result;    
  }

  public static TopicMapSourceIF getSource(TopicMapRepositoryIF rep, String tmsource) {
    TopicMapSourceIF source = null;
    if (tmsource != null) {
      source = rep.getSourceById(tmsource);
    } else {
      // if tmsource is null, it means the list wasn't displayed,
      // because there's only one source which supports create. so we
      // have to find it.
      
      Iterator<TopicMapSourceIF> it = rep.getSources().iterator();
      while (it.hasNext()) {
        TopicMapSourceIF candidate = it.next();
        if (candidate.supportsCreate()) {
          source = candidate;
          break;
        }
      }
      if (source == null) {
        throw new OntopiaRuntimeException("No source supporting create was found!");
      }
    }
    if (!source.supportsCreate()) {
      throw new OntopiaRuntimeException("Topic map source '" + tmsource
          + "' does not support creating new topic maps.");
    }
    return source;
  }

  public static OccurrenceIF getOccurrenceOfType(TopicIF topic,
      TopicIF occType) {
    Collection<OccurrenceIF> result = getOccurrencesOfType(topic, occType);
    if (result.isEmpty()) {
      return null;
    }
    return result.iterator().next();
  }

  public static Collection<OccurrenceIF> getOccurrencesOfType(TopicIF topic,
      TopicIF occType) {
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    for (Iterator<OccurrenceIF> iter = topic.getOccurrences().iterator(); iter.hasNext();) {
      OccurrenceIF occurrence = iter.next();
      TopicIF otype = occurrence.getType();
      if (otype != null && otype .equals(occType)) {
        result.add(occurrence);
      }
    }
    return result;

  }
 
}
