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

import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
* INTERNAL: Utility class that tracks ontological information. This
* class is used by the ConvertTopicMap action as part of the ontopoly
* schema inferencing code.
*/

public class SchemaTracker {

  protected static final Collection<TopicIF> NULL_COLLECTION = Collections.singleton(null);
  
  protected Map<TopicIF,TopicType> ttypes = new HashMap<TopicIF,TopicType>(); // ttype : { TopicType }
  protected Map<TopicIF,Map<TopicIF,Map<TopicIF,PlayerType>>> atypes = new HashMap<TopicIF,Map<TopicIF,Map<TopicIF,PlayerType>>>(); // atypes : { rtypes : { ptypes : PlayerType } }
  
  protected Collection<TopicIF> non_symmetric_atypes = new HashSet<TopicIF>();
  
  protected Collection<TopicIF> utypedp = new HashSet<TopicIF>(); // untyped players
  protected Collection<TopicIF> utypedt = new HashSet<TopicIF>(); // untyped topics
  
  protected Map<TopicIF,Collection<TopicIF>> nscopes = new HashMap<TopicIF,Collection<TopicIF>>(); // nscope : [ ttypes ]
  
  private class TopicType {
    protected int count;
  
    protected IdentityType subloc = new IdentityType();
    protected IdentityType subind = new IdentityType();
  
    protected Map<TopicIF,CharType> ntypes = new HashMap<TopicIF,CharType>(); // ntype : CharType
    protected Map<TopicIF,CharType> oitypes = new HashMap<TopicIF,CharType>(); // otype : CharType
    protected Map<TopicIF,CharType> oetypes = new HashMap<TopicIF,CharType>(); // otype : CharType    
  }
  
  private abstract class AbstractProperty {
   protected int count;
   protected int mincard = -1;
   protected int maxcard = -1;
   protected void registerCardinality(int cardinality) {
     this.count += cardinality;
     if (cardinality > maxcard || maxcard == -1) {
       this.maxcard = cardinality;
     }
     if (cardinality < mincard || mincard == -1) {
       this.mincard = cardinality;
     }      
   }
  }  
  private class CharType extends AbstractProperty {
   protected TopicIF type;
  }
  private class IdentityType extends AbstractProperty {
  }
  private class PlayerType extends AbstractProperty {
//   protected TopicIF atype;
//   protected TopicIF rtype;
//   protected TopicIF ptype;
  }
  
  public void trackTopics(Collection<TopicIF> topics) {
   Iterator<TopicIF> iter = topics.iterator();
   while (iter.hasNext()) {
     trackTopic(iter.next());
   }
  }
  
  public void trackTopic(TopicIF topic) {
  
   // topic types
   Collection<TopicIF> types = topic.getTypes();
   if (types.isEmpty()) {
     types = NULL_COLLECTION;
     utypedt.add(topic);
   }
  
   Iterator<TopicIF> titer = types.iterator();
   while (titer.hasNext()) {
     TopicIF ttype = titer.next();
  
     TopicType ttinfo = createTopicType(ttype);
     ttinfo.count++;
  
     // subject locators
     Collection<LocatorIF> sublocs = topic.getSubjectLocators();
     ttinfo.subloc.registerCardinality(sublocs.size());
  
     // subject indicators
     Collection<LocatorIF> subinds = topic.getSubjectIdentifiers();
     ttinfo.subind.registerCardinality(subinds.size());
     
     // names
     TObjectIntHashMap<TopicIF> ncards = new TObjectIntHashMap<TopicIF>();      
     Iterator<TopicNameIF> niter = topic.getTopicNames().iterator();
     while (niter.hasNext()) {
       TopicNameIF tn = niter.next();
       // translate name scopes into name types
       if (tn.getType() == null) {
         Collection<TopicIF> scope = tn.getScope();
         if (scope.size() == 1) {
           Iterator<TopicIF> siter = scope.iterator();
           while (siter.hasNext()) {
             TopicIF theme = siter.next();
             Collection<TopicIF> nstypes = nscopes.get(theme);
             if (nstypes == null) {
               nstypes = new HashSet<TopicIF>();
               nscopes.put(theme, nstypes);
             }
             nstypes.add(ttype);
           }
         }
       }
       // name types
       CharType ctype = createCharType(ttinfo.ntypes, tn.getType());
       ctype.count++;
       // track cardinalities
       if (ncards.containsKey(ctype.type)) {
         ncards.increment(ctype.type);
       } else {
         ncards.put(ctype.type, 1);
       }
     }
     // register cardinalities
     Iterator<CharType> ntiter = ttinfo.ntypes.values().iterator();
     while (ntiter.hasNext()) {
       CharType ct = ntiter.next();
       ct.registerCardinality(ncards.get(ct.type));
     }      
     
     // occurrences
     TObjectIntHashMap<TopicIF> oicards = new TObjectIntHashMap<TopicIF>();      
     TObjectIntHashMap<TopicIF> oecards = new TObjectIntHashMap<TopicIF>();      
     Iterator<OccurrenceIF> oiter = topic.getOccurrences().iterator();
     while (oiter.hasNext()) {
       OccurrenceIF oc = oiter.next();
       // internal occurrences vs external occurrences
       if (oc.getLocator() == null) {
         CharType ctype = createCharType(ttinfo.oitypes, oc.getType());
         ctype.count++;
         // track cardinalities
         if (oicards.containsKey(ctype.type)) {
           oicards.increment(ctype.type);
         } else {
           oicards.put(ctype.type, 1);
         }
       } else {
         CharType ctype = createCharType(ttinfo.oetypes, oc.getType());
         ctype.count++;
         // track cardinalities
         if (oecards.containsKey(ctype.type)) {
           oecards.increment(ctype.type);
         } else {
           oecards.put(ctype.type, 1);
         }
       }
     }
     // register cardinalities
     Iterator<CharType> oititer = ttinfo.oitypes.values().iterator();
     while (oititer.hasNext()) {
       CharType ct = oititer.next();
       ct.registerCardinality(oicards.get(ct.type));
     }      
     Iterator<CharType> oetiter = ttinfo.oetypes.values().iterator();
     while (oetiter.hasNext()) {
       CharType ct = oetiter.next();
       ct.registerCardinality(oecards.get(ct.type));
     }      
   }
  }
  
  public void trackAssociations(Collection<AssociationIF> assocs) {
   Iterator<AssociationIF> iter = assocs.iterator();
   while (iter.hasNext()) {
     trackAssociation(iter.next());
   }
  }
  
  public void trackAssociation(AssociationIF assoc) {
   TopicIF atype = assoc.getType();
  
   Collection<AssociationRoleIF> roles = assoc.getRoles();
  
   boolean symmetric = (roles.size() == 2);
   TopicIF prev_rtype = null;
   
   Iterator<AssociationRoleIF> riter = roles.iterator();
   while (riter.hasNext()) {
     AssociationRoleIF role = riter.next();
     TopicIF rtype = role.getType();
     TopicIF player = role.getPlayer();
  
     // symmetric association?
     if (symmetric) {
       if (prev_rtype == null) {
         prev_rtype = rtype;
       } else if (rtype == null || !rtype.equals(prev_rtype)) {
         symmetric = false;
       }
     }        
     
     if (player != null) {        
       Collection<TopicIF> ptypes = player.getTypes();      
       if (ptypes.isEmpty()) {
         ptypes = NULL_COLLECTION;
         utypedp.add(player);
       }
       // TODO: mincard not really perfectly calculated
       int cardinality = getPlayerCardinality(atype, rtype, player);
       Iterator<TopicIF> piter = ptypes.iterator();
       while (piter.hasNext()) {
         TopicIF ptype = piter.next();
         PlayerType pinfo = createPlayerType(atypes, atype, rtype, ptype);
         pinfo.registerCardinality(0); // mincard, see above
         pinfo.registerCardinality(cardinality);
       }
     }
   }
   // non-symmetric association type
   if (!symmetric) {
     non_symmetric_atypes.add(atype);
   }
  }
  
  protected int getPlayerCardinality(TopicIF atype, TopicIF rtype, TopicIF player) {
   int result = 0;
   Iterator<AssociationRoleIF> iter = player.getRoles().iterator();
   while (iter.hasNext()) {
     AssociationRoleIF role = iter.next();
     TopicIF _rtype = role.getType();
     if (_rtype == null || !_rtype.equals(rtype)) {
       continue;
     }
  
     AssociationIF assoc = role.getAssociation();
     if (assoc == null) {
       continue;
     }
     
     TopicIF _atype = assoc.getType();
     if (_atype != null && _atype.equals(atype)) {
       result++;
     }
   }
   return result;
  }
  
  // --- getters
  
  public Collection<TopicIF> getTopicTypes() {
   return ttypes.keySet();
  }
  
  public int getTopicTypeInstances(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   } else {
     return tt.count;
   }
  }
  
  public int getSubjectLocatorMinCardinality(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   return tt.subloc.mincard;
  }
  
  public int getSubjectLocatorMaxCardinality(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   return tt.subloc.maxcard;
  }
  
  public int getSubjectIndicatorMinCardinality(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   return tt.subind.mincard;
  }
  
  public int getSubjectIndicatorMaxCardinality(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   return tt.subind.maxcard;
  }
  
  public Collection<TopicIF> getUntypedTopics() {
   return utypedt;
  }
  
  public Collection<TopicIF> getUntypedPlayers() {
   return utypedp;
  }
  
  public Collection<TopicIF> getNameTypes(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return Collections.emptySet();
   }
   return tt.ntypes.keySet();
  }
  
  public int getNameTypeMinCardinality(TopicIF ttype, TopicIF ntype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   CharType ct = (CharType)tt.ntypes.get(ntype);
   if (ct == null) {
     return 0;
   }
   return ct.mincard;
  }
  
  public int getNameTypeMaxCardinality(TopicIF ttype, TopicIF ntype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   CharType ct = (CharType)tt.ntypes.get(ntype);
   if (ct == null) {
     return 0;
   }
   return ct.maxcard;
  }
  
  public Collection<TopicIF> getExternalOccurrenceTypes(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return Collections.emptySet();
   }
   return tt.oetypes.keySet();
  }
  
  public int getExternalOccurrenceTypeMinCardinality(TopicIF ttype, TopicIF otype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   CharType ct = (CharType)tt.oetypes.get(otype);
   if (ct == null) {
     return 0;
   }
   return ct.mincard;
  }
  
  public int getExternalOccurrenceTypeMaxCardinality(TopicIF ttype, TopicIF otype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   CharType ct = (CharType)tt.oetypes.get(otype);
   if (ct == null) {
     return 0;
   }
   return ct.maxcard;
  }
  
  public Collection<TopicIF> getInternalOccurrenceTypes(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return Collections.emptySet();
   }
   return tt.oitypes.keySet();
  }
  
  public int getInternalOccurrenceTypeMinCardinality(TopicIF ttype, TopicIF otype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   CharType ct = (CharType)tt.oitypes.get(otype);
   if (ct == null) {
     return 0;
   }
   return ct.mincard;
  }
  
  public int getInternalOccurrenceTypeMaxCardinality(TopicIF ttype, TopicIF otype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) {
     return 0;
   }
   CharType ct = (CharType)tt.oitypes.get(otype);
   if (ct == null) {
     return 0;
   }
   return ct.maxcard;
  }
  
  public Collection<TopicIF> getAssociationTypes() {
   return atypes.keySet();
  }
  
  public Collection<TopicIF> getRoleTypes(TopicIF atype) {
   Map<TopicIF,Map<TopicIF,PlayerType>> rmap = atypes.get(atype);
   if (rmap == null) {
     return Collections.emptySet();
   } else {
     return rmap.keySet();
   }
  }
  
  public Collection<TopicIF> getPlayerTypes(TopicIF atype, TopicIF rtype) {
   Map<TopicIF,Map<TopicIF,PlayerType>> rmap = atypes.get(atype);
   if (rmap == null) {
     return  Collections.emptySet();
   }
   
   Map<TopicIF,PlayerType> pmap = rmap.get(rtype);
   if (pmap == null) {
     return Collections.emptySet();
   } else {
     return pmap.keySet();
   }
  }
  
  public int getPlayerTypeMinCardinality(TopicIF atype, TopicIF rtype, TopicIF ptype) {
   Map<TopicIF,Map<TopicIF,PlayerType>> rmap = atypes.get(atype);
   if (rmap == null) {
     return 0;
   }
   
   Map<TopicIF,PlayerType> pmap = rmap.get(rtype);
   if (pmap == null) {
     return 0;
   }
  
   PlayerType pt = pmap.get(ptype);
   return (pt == null ? 0 : pt.mincard);
  }
  
  public int getPlayerTypeMaxCardinality(TopicIF atype, TopicIF rtype, TopicIF ptype) {
   Map<TopicIF,Map<TopicIF,PlayerType>> rmap = atypes.get(atype);
   if (rmap == null) {
     return 0;
   }
   
   Map<TopicIF,PlayerType> pmap = rmap.get(rtype);
   if (pmap == null) {
     return 0;
   }
  
   PlayerType pt = pmap.get(ptype);
   return (pt == null ? 0 : pt.maxcard);
  }
  
  public Collection<TopicIF> getOntologyTypes() {
   Collection<TopicIF> onto_types = new HashSet<TopicIF>();
  
   // topic types
   Iterator<TopicIF> ttypes = getTopicTypes().iterator();
   while (ttypes.hasNext()) {
     TopicIF ttype = ttypes.next();
     onto_types.add(ttype);
     // name types
     Iterator<TopicIF> ntypes = getNameTypes(ttype).iterator();
     while (ntypes.hasNext()) {
       TopicIF ntype = ntypes.next();
       onto_types.add(ntype);
     }
     // external occurrence types
     Iterator<TopicIF> oetypes = getExternalOccurrenceTypes(ttype).iterator();
     while (oetypes.hasNext()) {
       TopicIF oetype = oetypes.next();
       onto_types.add(oetype);
     }
     // internal occurrence types
     Iterator<TopicIF> oitypes = getInternalOccurrenceTypes(ttype).iterator();
     while (oitypes.hasNext()) {
       TopicIF oitype = oitypes.next();
       onto_types.add(oitype);
     }
   }      
  
   // association types
   Iterator<TopicIF> atypes = getAssociationTypes().iterator();
   while (atypes.hasNext()) {
     TopicIF atype = atypes.next();
     onto_types.add(atype);
     // role types
     Iterator<TopicIF> rtypes = getRoleTypes(atype).iterator();
     while (rtypes.hasNext()) {
       TopicIF rtype = rtypes.next();
       onto_types.add(rtype);
     }
   }
   return onto_types;
  }
  
  public Collection<TopicIF> getSuspectNameScopes() {
   return nscopes.keySet();
  }
  
  public Collection<TopicIF> getNameScopeTopicTypes(TopicIF ntheme) {
   return nscopes.get(ntheme);
  }
  
  public boolean isSymmetricAssociationType(TopicIF atype) {
   return !non_symmetric_atypes.contains(atype);
  }
  
  // --- setters
  
  protected TopicType getTopicType(TopicIF ttype) {
   return (TopicType)ttypes.get(ttype);
  }
  
  protected TopicType createTopicType(TopicIF ttype) {
   TopicType t = (TopicType)ttypes.get(ttype);
   if (t == null) {
     t = new TopicType();
//     t.type = ttype;
     ttypes.put(ttype, t);
   }
   return t;
  }
  
  protected CharType getCharType(Map<TopicIF,CharType> cmap, TopicIF ctype) {
   return cmap.get(ctype);
  }
  
  protected CharType createCharType(Map<TopicIF,CharType> cmap, TopicIF ctype) {
   CharType t = cmap.get(ctype);
   if (t == null) {
     t = new CharType();
     t.type = ctype;
     cmap.put(ctype, t);
   }
   return t;
  }
  
  protected PlayerType getPlayerType(Map<TopicIF,Map<TopicIF,Map<TopicIF,PlayerType>>> amap, TopicIF atype, TopicIF rtype, TopicIF ptype) {
   Map<TopicIF,Map<TopicIF,PlayerType>> rmap = amap.get(atype);
   if (rmap == null) {
     return null;
   }
   Map<TopicIF,PlayerType> pmap = rmap.get(rtype);
   if (pmap == null) {
     return null;
   }
   return pmap.get(ptype);
  }
  
  protected PlayerType createPlayerType(Map<TopicIF,Map<TopicIF,Map<TopicIF,PlayerType>>> amap, TopicIF atype, TopicIF rtype, TopicIF ptype) {
   Map<TopicIF,Map<TopicIF,PlayerType>> rmap = amap.get(atype);
   if (rmap == null) {
     rmap = new HashMap<TopicIF,Map<TopicIF,PlayerType>>();
     amap.put(atype, rmap);
   }
   Map<TopicIF,PlayerType> pmap = rmap.get(rtype);
   if (pmap == null) {
     pmap = new HashMap<TopicIF,PlayerType>();
     rmap.put(rtype, pmap);
   }
   PlayerType t = pmap.get(ptype);
   if (t == null) {
     t = new PlayerType();
//     t.atype = atype;
//     t.rtype = rtype;
//     t.ptype = ptype;
     pmap.put(ptype, t);
   }
   return t;
  }

}
