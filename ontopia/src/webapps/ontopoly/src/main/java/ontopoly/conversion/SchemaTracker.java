package ontopoly.conversion;

//$Id: SchemaTracker.java,v 1.1 2009/04/23 05:39:14 geir.gronmo Exp $

import gnu.trove.TObjectIntHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

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

  protected static final Collection NULL_COLLECTION = Collections.singleton(null);
  
  protected Map ttypes = new HashMap(); // ttype : { TopicType }
  protected Map atypes = new HashMap(); // atypes : { rtypes : { ptypes : PlayerType } }
  
  protected Collection non_symmetric_atypes = new HashSet();
  
  protected Collection utypedp = new HashSet(); // untyped players
  protected Collection utypedt = new HashSet(); // untyped topics
  
  protected Map nscopes = new HashMap(); // nscope : [ ttypes ]
  
  private class TopicType {
//   protected TopicIF type;
   protected int count;
  
   protected IdentityType subloc = new IdentityType();
   protected IdentityType subind = new IdentityType();
  
   protected Map ntypes = new HashMap(); // ntype : CharType
   protected Map oitypes = new HashMap(); // otype : CharType
   protected Map oetypes = new HashMap(); // otype : CharType    
  }
  
  private abstract class AbstractProperty {
   protected int count;
   protected int mincard = -1;
   protected int maxcard = -1;
   void registerCardinality(int cardinality) {
     this.count += cardinality;
     if (cardinality > maxcard || maxcard == -1)
       this.maxcard = cardinality;
     if (cardinality < mincard || mincard == -1)
       this.mincard = cardinality;      
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
  
  public void trackTopics(Collection topics) {
   Iterator iter = topics.iterator();
   while (iter.hasNext()) {
     trackTopic((TopicIF)iter.next());
   }
  }
  
  public void trackTopic(TopicIF topic) {
  
   // topic types
   Collection types = topic.getTypes();
   if (types.isEmpty()) {
     types = NULL_COLLECTION;
     utypedt.add(topic);
   }
  
   Iterator titer = types.iterator();
   while (titer.hasNext()) {
     TopicIF ttype = (TopicIF)titer.next();
  
     TopicType ttinfo = createTopicType(ttype);
     ttinfo.count++;
  
     // subject locators
     Collection sublocs = topic.getSubjectLocators();
     ttinfo.subloc.registerCardinality(sublocs.size());
  
     // subject indicators
     Collection subinds = topic.getSubjectIdentifiers();
     ttinfo.subind.registerCardinality(subinds.size());
     
     // names
     TObjectIntHashMap ncards = new TObjectIntHashMap();      
     Iterator niter = topic.getTopicNames().iterator();
     while (niter.hasNext()) {
       TopicNameIF tn = (TopicNameIF)niter.next();
       // translate name scopes into name types
       if (tn.getType() == null) {
         Collection scope = tn.getScope();
         if (scope.size() == 1) {
           Iterator siter = scope.iterator();
           while (siter.hasNext()) {
             TopicIF theme = (TopicIF)siter.next();
             Collection nstypes = (Collection)nscopes.get(theme);
             if (nstypes == null) {
               nstypes = new HashSet();
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
       if (ncards.containsKey(ctype.type))
         ncards.increment(ctype.type);
       else
         ncards.put(ctype.type, 1);
     }
     // register cardinalities
     Iterator ntiter = ttinfo.ntypes.values().iterator();
     while (ntiter.hasNext()) {
       CharType ct = (CharType)ntiter.next();
       ct.registerCardinality(ncards.get(ct.type));
     }      
     
     // occurrences
     TObjectIntHashMap oicards = new TObjectIntHashMap();      
     TObjectIntHashMap oecards = new TObjectIntHashMap();      
     Iterator oiter = topic.getOccurrences().iterator();
     while (oiter.hasNext()) {
       OccurrenceIF oc = (OccurrenceIF)oiter.next();
       // internal occurrences vs external occurrences
       if (oc.getLocator() == null) {
         CharType ctype = createCharType(ttinfo.oitypes, oc.getType());
         ctype.count++;
         // track cardinalities
         if (oicards.containsKey(ctype.type))
           oicards.increment(ctype.type);
         else
           oicards.put(ctype.type, 1);
       } else {
         CharType ctype = createCharType(ttinfo.oetypes, oc.getType());
         ctype.count++;
         // track cardinalities
         if (oecards.containsKey(ctype.type))
           oecards.increment(ctype.type);
         else
           oecards.put(ctype.type, 1);
       }
     }
     // register cardinalities
     Iterator oititer = ttinfo.oitypes.values().iterator();
     while (oititer.hasNext()) {
       CharType ct = (CharType)oititer.next();
       ct.registerCardinality(oicards.get(ct.type));
     }      
     Iterator oetiter = ttinfo.oetypes.values().iterator();
     while (oetiter.hasNext()) {
       CharType ct = (CharType)oetiter.next();
       ct.registerCardinality(oecards.get(ct.type));
     }      
   }
  }
  
  public void trackAssociations(Collection assocs) {
   Iterator iter = assocs.iterator();
   while (iter.hasNext()) {
     trackAssociation((AssociationIF)iter.next());
   }
  }
  
  public void trackAssociation(AssociationIF assoc) {
   TopicIF atype = assoc.getType();
  
   Collection roles = assoc.getRoles();
  
   boolean symmetric = (roles.size() == 2);
   TopicIF prev_rtype = null;
   
   Iterator riter = roles.iterator();
   while (riter.hasNext()) {
     AssociationRoleIF role = (AssociationRoleIF)riter.next();
     TopicIF rtype = role.getType();
     TopicIF player = role.getPlayer();
  
     // symmetric association?
     if (symmetric) {
       if (prev_rtype == null)
         prev_rtype = rtype;
       else if (rtype == null || !rtype.equals(prev_rtype))          
         symmetric = false;
     }        
     
     if (player != null) {        
       Collection ptypes = player.getTypes();      
       if (ptypes.isEmpty()) {
         ptypes = NULL_COLLECTION;
         utypedp.add(player);
       }
       // TODO: mincard not really perfectly calculated
       int cardinality = getPlayerCardinality(atype, rtype, player);
       Iterator piter = ptypes.iterator();
       while (piter.hasNext()) {
         TopicIF ptype = (TopicIF)piter.next();
         PlayerType pinfo = createPlayerType(atypes, atype, rtype, ptype);
         pinfo.registerCardinality(0); // mincard, see above
         pinfo.registerCardinality(cardinality);
       }
     }
   }
   // non-symmetric association type
   if (!symmetric)
     non_symmetric_atypes.add(atype);
  }
  
  protected int getPlayerCardinality(TopicIF atype, TopicIF rtype, TopicIF player) {
   int result = 0;
   Iterator iter = player.getRoles().iterator();
   while (iter.hasNext()) {
     AssociationRoleIF role = (AssociationRoleIF)iter.next();
     TopicIF _rtype = role.getType();
     if (_rtype == null || !_rtype.equals(rtype)) continue;
  
     AssociationIF assoc = role.getAssociation();
     if (assoc == null) continue;
     
     TopicIF _atype = assoc.getType();
     if (_atype != null && _atype.equals(atype))
       result++;
   }
   return result;
  }
  
  // --- getters
  
  public Collection getTopicTypes() {
   return ttypes.keySet();
  }
  
  public int getTopicTypeInstances(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null)
     return 0;
   else
     return tt.count;
  }
  
  public int getSubjectLocatorMinCardinality(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   return tt.subloc.mincard;
  }
  
  public int getSubjectLocatorMaxCardinality(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   return tt.subloc.maxcard;
  }
  
  public int getSubjectIndicatorMinCardinality(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   return tt.subind.mincard;
  }
  
  public int getSubjectIndicatorMaxCardinality(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   return tt.subind.maxcard;
  }
  
  public Collection getUntypedTopics() {
   return utypedt;
  }
  
  public Collection getUntypedPlayers() {
   return utypedp;
  }
  
  public Collection getNameTypes(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return Collections.EMPTY_SET;
   return tt.ntypes.keySet();
  }
  
  public int getNameTypeMinCardinality(TopicIF ttype, TopicIF ntype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   CharType ct = (CharType)tt.ntypes.get(ntype);
   if (ct == null) return 0;
   return ct.mincard;
  }
  
  public int getNameTypeMaxCardinality(TopicIF ttype, TopicIF ntype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   CharType ct = (CharType)tt.ntypes.get(ntype);
   if (ct == null) return 0;
   return ct.maxcard;
  }
  
  public Collection getExternalOccurrenceTypes(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return Collections.EMPTY_SET;
   return tt.oetypes.keySet();
  }
  
  public int getExternalOccurrenceTypeMinCardinality(TopicIF ttype, TopicIF otype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   CharType ct = (CharType)tt.oetypes.get(otype);
   if (ct == null) return 0;
   return ct.mincard;
  }
  
  public int getExternalOccurrenceTypeMaxCardinality(TopicIF ttype, TopicIF otype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   CharType ct = (CharType)tt.oetypes.get(otype);
   if (ct == null) return 0;
   return ct.maxcard;
  }
  
  public Collection getInternalOccurrenceTypes(TopicIF ttype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return Collections.EMPTY_SET;
   return tt.oitypes.keySet();
  }
  
  public int getInternalOccurrenceTypeMinCardinality(TopicIF ttype, TopicIF otype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   CharType ct = (CharType)tt.oitypes.get(otype);
   if (ct == null) return 0;
   return ct.mincard;
  }
  
  public int getInternalOccurrenceTypeMaxCardinality(TopicIF ttype, TopicIF otype) {
   TopicType tt = getTopicType(ttype);
   if (tt == null) return 0;
   CharType ct = (CharType)tt.oitypes.get(otype);
   if (ct == null) return 0;
   return ct.maxcard;
  }
  
  public Collection getAssociationTypes() {
   return atypes.keySet();
  }
  
  public Collection getRoleTypes(TopicIF atype) {
   Map rmap = (Map)atypes.get(atype);
   if (rmap == null)
     return Collections.EMPTY_SET;
   else
     return rmap.keySet();
  }
  
  public Collection getPlayerTypes(TopicIF atype, TopicIF rtype) {
   Map rmap = (Map)atypes.get(atype);
   if (rmap == null)
     return  Collections.EMPTY_SET;
   
   Map pmap = (HashMap)rmap.get(rtype);
   if (pmap == null)
     return Collections.EMPTY_SET;
   else
     return pmap.keySet();
  }
  
  public int getPlayerTypeMinCardinality(TopicIF atype, TopicIF rtype, TopicIF ptype) {
   Map rmap = (Map)atypes.get(atype);
   if (rmap == null) return 0;
   
   Map pmap = (Map)rmap.get(rtype);
   if (pmap == null) return 0;
  
   PlayerType pt = (PlayerType)pmap.get(ptype);
   return (pt == null ? 0 : pt.mincard);
  }
  
  public int getPlayerTypeMaxCardinality(TopicIF atype, TopicIF rtype, TopicIF ptype) {
   Map rmap = (Map)atypes.get(atype);
   if (rmap == null) return 0;
   
   Map pmap = (Map)rmap.get(rtype);
   if (pmap == null) return 0;
  
   PlayerType pt = (PlayerType)pmap.get(ptype);
   return (pt == null ? 0 : pt.maxcard);
  }
  
  public Collection getOntologyTypes() {
   Collection onto_types = new HashSet();
  
   // topic types
   Iterator ttypes = getTopicTypes().iterator();
   while (ttypes.hasNext()) {
     TopicIF ttype = (TopicIF)ttypes.next();
     onto_types.add(ttype);
     // name types
     Iterator ntypes = getNameTypes(ttype).iterator();
     while (ntypes.hasNext()) {
       TopicIF ntype = (TopicIF)ntypes.next();
       onto_types.add(ntype);
     }
     // external occurrence types
     Iterator oetypes = getExternalOccurrenceTypes(ttype).iterator();
     while (oetypes.hasNext()) {
       TopicIF oetype = (TopicIF)oetypes.next();
       onto_types.add(oetype);
     }
     // internal occurrence types
     Iterator oitypes = getInternalOccurrenceTypes(ttype).iterator();
     while (oitypes.hasNext()) {
       TopicIF oitype = (TopicIF)oitypes.next();
       onto_types.add(oitype);
     }
   }      
  
   // association types
   Iterator atypes = getAssociationTypes().iterator();
   while (atypes.hasNext()) {
     TopicIF atype = (TopicIF)atypes.next();
     onto_types.add(atype);
     // role types
     Iterator rtypes = getRoleTypes(atype).iterator();
     while (rtypes.hasNext()) {
       TopicIF rtype = (TopicIF)rtypes.next();
       onto_types.add(rtype);
     }
   }
   return onto_types;
  }
  
  public Collection getSuspectNameScopes() {
   return nscopes.keySet();
  }
  
  public Collection getNameScopeTopicTypes(TopicIF ntheme) {
   return (Collection)nscopes.get(ntheme);
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
  
  protected CharType getCharType(Map cmap, TopicIF ctype) {
   return (CharType)cmap.get(ctype);
  }
  
  protected CharType createCharType(Map cmap, TopicIF ctype) {
   CharType t = (CharType)cmap.get(ctype);
   if (t == null) {
     t = new CharType();
     t.type = ctype;
     cmap.put(ctype, t);
   }
   return t;
  }
  
  protected PlayerType getPlayerType(Map amap, TopicIF atype, TopicIF rtype, TopicIF ptype) {
   Map rmap = (Map)amap.get(atype);
   if (rmap == null) return null;
   Map pmap = (Map)rmap.get(rtype);
   if (pmap == null) return null;
   return (PlayerType)pmap.get(ptype);
  }
  
  protected PlayerType createPlayerType(Map amap, TopicIF atype, TopicIF rtype, TopicIF ptype) {
   Map rmap = (Map)amap.get(atype);
   if (rmap == null) {
     rmap = new HashMap();
     amap.put(atype, rmap);
   }
   Map pmap = (Map)rmap.get(rtype);
   if (pmap == null) {
     pmap = new HashMap();
     rmap.put(rtype, pmap);
   }
   PlayerType t = (PlayerType)pmap.get(ptype);
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
