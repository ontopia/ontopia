package net.ontopia.topicmaps.nav2.webapps.ontopoly.utils;

// TODO javadoc

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.QueryMapper;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.AssociationBuilder;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.ObjectUtils;

public class OntopolyModelUtils {

  private OntopolyModelUtils() {
  }

  public static RowMapperIF getRowMapperOneColumn() {
    return RowMapperOneColumn.INSTANCE;
  }

  private static class RowMapperOneColumn implements RowMapperIF {
		public static final RowMapperIF INSTANCE = new RowMapperOneColumn();
    public Object mapRow(QueryResultIF QueryResult, int rowno) {
      // hardcoded to return the value in the first column
      return QueryResult.getValue(0);
    }
  }

  public static TopicIF getTopicIF(TopicMap tm, LocatorIF base, String subjectIndicator) {
		return getTopicIF(tm, base.resolveAbsolute(subjectIndicator), true);
	}

  public static TopicIF getTopicIF(TopicMap tm, LocatorIF subjectIdentifier) {
    return getTopicIF(tm, subjectIdentifier, true);
  }

  public static TopicIF getTopicIF(TopicMap tm, LocatorIF base, String subjectIndicator, boolean mustExist) {
		return getTopicIF(tm, base.resolveAbsolute(subjectIndicator), mustExist);
	}

  public static TopicIF getTopicIF(TopicMap tm, LocatorIF subjectIdentifier, boolean mustExist) {
		TopicIF t = tm.getTopicMapIF().getTopicBySubjectIdentifier(subjectIdentifier);
		if (t == null && mustExist) 
      throw new RuntimeException("Topic not found by subject identifier: " + subjectIdentifier);
		return t;
	}

  //! public static TopicIF getTopicIF(TopicMap tm, String subjectIndicator) {
	//! 	
  //!   String query = "$TOPIC = " + subjectIndicator + "?";
	//! 
  //!   List result = tm.getQueryWrapper().queryForList(query,
  //!       OntopolyModelUtils.getRowMapperOneColumn());
	//! 
  //!   return (TopicIF) result.get(0);
  //! }
  
  /*public static AssociationIF findUnaryAssociation(TopicIF aType,
      TopicIF player, TopicIF rType) {
    Iterator it = player.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF aRole = (AssociationRoleIF) it.next();
      if (aRole.getType().equals(rType)) {
        AssociationIF assoc = aRole.getAssociation();
        if (assoc.getType().equals(aType)) {
          return assoc;
        }
      }
    }
    return null;
  }*/

  public static boolean isUnaryPlayer(
      TopicMap tm, TopicIF aType, TopicIF player, TopicIF rType) {
    return findUnaryAssociation(tm, aType, player, rType) != null;
  }
  
  public static AssociationIF findUnaryAssociation(
      TopicMap tm, TopicIF aType, TopicIF player, TopicIF rType) {

    Iterator iter = player.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF)iter.next();
      AssociationIF assoc = role.getAssociation();
      if (ObjectUtils.equals(role.getType(), rType) &&
          ObjectUtils.equals(assoc.getType(), aType))
        return assoc;
    }
    return null;
  }
  
//  public static Collection findUnaryAssociations(
//      TopicMap tm, TopicIF aType, TopicIF rType1) {
//    String query = "select $A from type($R1, %rType1%),"
//      + "association-role($A, $R1) , type($A, %aType%)?";
//
//    Map params = new HashMap();
//    params.put("rType1", rType1);
//    params.put("aType", aType);
//
//    return tm.getQueryWrapper().queryForList(query,
//        OntopolyModelUtils.getRowMapperOneColumn(), params);
//  }
  
  public static Collection<TopicIF> findBinaryPlayers(
      TopicMap tm, TopicIF aType, TopicIF player1, TopicIF rType1, TopicIF rType2) {
    List<TopicIF> result = new ArrayList<TopicIF>();
    Iterator iter = player1.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role1 = (AssociationRoleIF)iter.next();
      AssociationIF assoc = role1.getAssociation();
      Collection roles = assoc.getRoles();
      if (roles.size() != 2) continue;
      if (ObjectUtils.equals(role1.getType(), rType1) &&
          ObjectUtils.equals(assoc.getType(), aType)) {
        Iterator riter = roles.iterator();
        while (riter.hasNext()) {
          AssociationRoleIF role2 = (AssociationRoleIF)riter.next();
          if (ObjectUtils.different(role1, role2)) {
            result.add(role2.getPlayer());
          }
        }
      }
    }
    return result;
  }
  
  public static Collection<TopicIF> findBinaryPlayers(TopicMap tm, TopicIF aType, 
      TopicIF player1, TopicIF rType1, TopicIF rType2, TopicIF theme) {
    List<TopicIF> result = new ArrayList<TopicIF>();
    Iterator iter = player1.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role1 = (AssociationRoleIF)iter.next();
      AssociationIF assoc = role1.getAssociation();
      Collection scope = assoc.getScope();
      if (!(scope.size() == 1 && scope.contains(theme))) continue;
      Collection roles = assoc.getRoles();
      if (roles.size() != 2) continue;
      if (ObjectUtils.equals(role1.getType(), rType1) &&
          ObjectUtils.equals(assoc.getType(), aType)) {
        Iterator riter = roles.iterator();
        while (riter.hasNext()) {
          AssociationRoleIF role2 = (AssociationRoleIF)riter.next();
          if (ObjectUtils.different(role1, role2)) {
            result.add(role2.getPlayer());
          }
        }
      }
    }
    return result;
  }

  public static AssociationIF findBinaryAssociation(
      TopicMap tm, TopicIF aType, 
      TopicIF player1, TopicIF rType1, TopicIF player2, TopicIF rType2) {
    String query = "select $A from role-player($R1, %player1%), "
        + "type($R1, %rType1%), association-role($A, $R1) ,"
        + "type($A, %aType%), association-role($A, $R2) ,"
        + "role-player($R2, %player2%), type($R2, %rType2%)?";

    Map<String,TopicIF> params = new HashMap<String,TopicIF>();
    params.put("player1", player1);
    params.put("rType1", rType1);
    params.put("aType", aType);
    params.put("player2", player2);
    params.put("rType2", rType2);

    QueryMapper<AssociationIF> qm = tm.newQueryMapperNoWrap();    
    return removeDuplicateAssociations(qm.queryForList(query, params));
  }

  public static Collection<AssociationIF> findBinaryAssociations(
      TopicMap tm, TopicIF aType, 
      TopicIF player1, TopicIF rType1, TopicIF rType2) {
    String query = "select $A from role-player($R1, %player1%), "
        + "type($R1, %rType1%), association-role($A, $R1) ,"
        + "type($A, %aType%), association-role($A, $R2) ,"
        + "type($R2, %rType2%)?";

    Map<String,TopicIF> params = new HashMap<String,TopicIF>();
    params.put("player1", player1);
    params.put("rType1", rType1);
    params.put("aType", aType);
    params.put("rType2", rType2);

    QueryMapper<AssociationIF> qm = tm.newQueryMapperNoWrap();    
    return qm.queryForList(query, params);
  }
  
  public static Collection<TopicIF> findTernaryPlayers(
      TopicMap tm, TopicIF aType, TopicIF player1, TopicIF rType1, TopicIF player2, TopicIF rType2, TopicIF rType3) {
    AssociationRoleIF secondRole;
    AssociationRoleIF thirdRole;
    List<TopicIF> result = new ArrayList<TopicIF>();
    Iterator iter = player1.getRoles().iterator();
    while (iter.hasNext()) {
      secondRole = null;
      thirdRole = null;
      AssociationRoleIF role1 = (AssociationRoleIF)iter.next();
      AssociationIF assoc = role1.getAssociation();
      if (ObjectUtils.equals(role1.getType(), rType1) &&
          ObjectUtils.equals(assoc.getType(), aType)) {
        Iterator riter = assoc.getRoles().iterator();
        while (riter.hasNext()) {
          AssociationRoleIF role2 = (AssociationRoleIF)riter.next();
          if (ObjectUtils.different(role1, role2)) {
            if (ObjectUtils.equals(rType2, role2.getType()) && ObjectUtils.equals(player2, role2.getPlayer()))
              secondRole = role2;
            else if (ObjectUtils.equals(rType3, role2.getType()))
              thirdRole = role2;
            else
              break;
          }
        }
        if (secondRole != null && thirdRole != null)
          result.add(thirdRole.getPlayer());
      }
    }
    return result;
  }

  public static AssociationIF findTernaryAssociation(
      TopicMap tm, TopicIF aType, TopicIF player1,
      TopicIF rType1, TopicIF player2, TopicIF rType2, TopicIF player3,
      TopicIF rType3) {
    String query = "select $A from role-player($R1, %player1%), "
        + "type($R1, %rType1%), association-role($A, $R1) ,"
        + "type($A, %aType%), association-role($A, $R2) ,"
        + "role-player($R2, %player2%), type($R2, %rType2%) ,"
        + "association-role($A, $R3) , role-player($R3, %player3%),"
        + "type($R3, %rType3%)?";

    Map<String,TopicIF> params = new HashMap<String,TopicIF>();
    params.put("player1", player1);
    params.put("rType1", rType1);
    params.put("aType", aType);
    params.put("player2", player2);
    params.put("rType2", rType2);
    params.put("player3", player3);
    params.put("rType3", rType3);

    QueryMapper<AssociationIF> qm = tm.newQueryMapperNoWrap();
    
    return removeDuplicateAssociations(qm.queryForList(query, params));
  }

//  public static Collection findTernaryAssociations(
//      TopicMap tm, TopicIF aType, 
//      TopicIF player1, TopicIF rType1, TopicIF player2,TopicIF rType2, TopicIF rType3) {
//    String query = "select $A from role-player($R1, %player1%), "
//        + "type($R1, %rType1%), association-role($A, $R1) ,"
//        + "type($A, %aType%), association-role($A, $R2) ,"
//        + "role-player($R2, %player2%), type($R2, %rType2%) ,"
//        + "association-role($A, $R3) , type($R3, %rType3%)?";
//
//    Map params = new HashMap();
//    params.put("player1", player1);
//    params.put("rType1", rType1);
//    params.put("aType", aType);
//    params.put("player2", player2);
//    params.put("rType2", rType2);
//    params.put("rType3", rType3);
//
//    return tm.getQueryWrapper().queryForList(query,
//        OntopolyModelUtils.getRowMapperOneColumn(), params);
//  }
  
//  public static AssociationIF findQuadaryAssociation(
//      TopicMap tm, TopicIF aType, TopicIF player1,
//      TopicIF rType1, TopicIF player2, TopicIF rType2, TopicIF player3,
//      TopicIF rType3, TopicIF player4, TopicIF rType4) {
//    String query = "select $A from role-player($R1, %player1%), "
//        + "type($R1, %rType1%), association-role($A, $R1) ,"
//        + "type($A, %aType%), association-role($A, $R2) ,"
//        + "role-player($R2, %player2%), type($R2, %rType2%) ,"
//        + "association-role($A, $R3) , role-player($R3, %player3%),"
//        + "type($R3, %rType3%), association-role($A, $R4),"
//        + "role-player($R4, %player4%), type($R4, %rType4%)?";
//
//    Map params = new HashMap();
//    params.put("player1", player1);
//    params.put("rType1", rType1);
//    params.put("aType", aType);
//    params.put("player2", player2);
//    params.put("rType2", rType2);
//    params.put("player3", player3);
//    params.put("rType3", rType3);
//    params.put("player4", player4);
//    params.put("rType4", rType4);
//
//    return removeDuplicateAssociations(tm.getQueryWrapper().queryForList(
//        query, OntopolyModelUtils.getRowMapperOneColumn(), params));
//  }
//  
//  public static Collection findQuadaryAssociations(TopicMap tm,
//      TopicIF aType, TopicIF player1, TopicIF rType1, TopicIF player2,
//      TopicIF rType2, TopicIF player3,
//      TopicIF rType3,TopicIF rType4) {
//    
//    String query = "select $A from role-player($R1, %player1%), "
//        + "type($R1, %rType1%), association-role($A, $R1) ,"
//        + "type($A, %aType%), association-role($A, $R2) ,"
//        + "role-player($R2, %player2%), type($R2, %rType2%) ,"
//        + "association-role($A, $R3), role-player($R3, %player3%) ,"
//        + "type($R3, %rType3%), association-role($A, $R4)," 
//        + "type($R4, %rType4%)?";
//
//    Map params = new HashMap();
//    params.put("player1", player1);
//    params.put("rType1", rType1);
//    params.put("aType", aType);
//    params.put("player2", player2);
//    params.put("rType2", rType2);
//    params.put("player3", player3);
//    params.put("rType3", rType3);   
//    params.put("rType4", rType4);
//
//    return tm.getQueryWrapper().queryForList(query,
//        OntopolyModelUtils.getRowMapperOneColumn(), params);
//  }

  public static List<AssociationIF> findAssociations(
		TopicIF aType, TopicIF[] rTypes, TopicIF[] players, Collection scope) {
		
		List<AssociationIF> result = new ArrayList<AssociationIF>();
		
		TopicIF player = players[0];
		
		Collection proles = player.getRoles();
		Iterator iter = proles.iterator();
		while (iter.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF)iter.next();
			
      // compare current player
      if (!ObjectUtils.equals(role.getType(), rTypes[0])) continue;
			
      // compare association type
      AssociationIF assoc = role.getAssociation();
      if (!ObjectUtils.equals(assoc.getType(), aType)) continue;
      
      // compare arity
      Collection roles = assoc.getRoles();
      if (rTypes.length != roles.size()) continue;
        
      // compare scope
			if (!CollectionUtils.equalsUnorderedSet(assoc.getScope(), scope)) continue;
			
      // compare other players
      int matchCount = 1;
      boolean[] matches = new boolean[players.length];
      matches[0] = true;
			
      Iterator riter = roles.iterator();
      while (riter.hasNext()) {
				AssociationRoleIF orole = (AssociationRoleIF)riter.next();
				if (ObjectUtils.equals(orole, role)) continue;
				TopicIF ortype = orole.getType();
				TopicIF oplayer = orole.getPlayer();
				int match = -1;
				for (int i=1; i < players.length; i++) { 
					if (ObjectUtils.equals(ortype, rTypes[i]) &&
							ObjectUtils.equals(oplayer, players[i]) && !matches[i]) {
						match = i;
						matches[i] = true;
						matchCount++;
						break;
					}
				}
				if (match == -1) break;
			}
			if (matchCount == players.length)
				result.add(assoc);
		}
		return result;
	}

  private static AssociationIF removeDuplicateAssociations(Collection<AssociationIF> associations) {
    AssociationIF uniqueAssoc = null;

    Iterator<AssociationIF> it = associations.iterator();
    if (it.hasNext()) {
      uniqueAssoc = it.next();
    }
    while (it.hasNext()) {
      it.next().remove();
    }

    return uniqueAssoc;
  }

  public static void makeUnaryAssociation(TopicIF aType, TopicIF player,
      TopicIF rType) {
    AssociationBuilder assocBuilder = new AssociationBuilder(aType, rType);
    assocBuilder.makeAssociation(player);
  }

  public static void makeBinaryAssociation(TopicIF aType, TopicIF player1,
      TopicIF rType1, TopicIF player2, TopicIF rType2) {
    AssociationBuilder assocBuilder = new AssociationBuilder(aType, rType1,
        rType2);
    assocBuilder.makeAssociation(player1, player2);
  }

  public static void makeTernaryAssociation(TopicIF aType, TopicIF player1,
      TopicIF rType1, TopicIF player2, TopicIF rType2, TopicIF player3,
      TopicIF rType3) {
    AssociationBuilder assocBuilder = new AssociationBuilder(aType, rType1,
        rType2, rType3);
    assocBuilder.makeAssociation(player1, player2, player3);
  }
  
  public static void makeQuadaryAssociation(TopicIF aType, TopicIF player1,
      TopicIF rType1, TopicIF player2, TopicIF rType2, TopicIF player3,
      TopicIF rType3, TopicIF player4,
      TopicIF rType4) {
    
    AssociationBuilder assocBuilder = new AssociationBuilder(aType, rType1,
        rType2, rType3, rType4);
    assocBuilder.makeAssociation(player1, player2, player3, player4);
  }

  public static AssociationIF makeAssociation(
      TopicIF aType, TopicIF[] rTypes, TopicIF[] players, Collection scope) {

      TopicMapBuilderIF builder = aType.getTopicMap().getBuilder();
      AssociationIF assoc = builder.makeAssociation(aType);
      for (int i=0; i < rTypes.length; i++) { 
      builder.makeAssociationRole(assoc, rTypes[i], players[i]);
      }
      return assoc;
  }

  public static OccurrenceIF findOccurrence(TopicIF oType, TopicIF topicIF) { 
    Iterator it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = (OccurrenceIF) it.next();
      if (ObjectUtils.equals(occurIF.getType(), oType))
        return occurIF;
    }
    return null;
  }

  public static OccurrenceIF findOccurrence(TopicIF oType, TopicIF topicIF, LocatorIF datatype, Collection scope) { 
    Iterator it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = (OccurrenceIF) it.next();
      if (ObjectUtils.equals(occurIF.getType(), oType) && 
          ObjectUtils.equals(occurIF.getDataType(), datatype) && 
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope))
        return occurIF;
    }
    return null;
  }
  
  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = (OccurrenceIF) it.next();
      if (ObjectUtils.equals(occurIF.getType(), oType))
        result.add(occurIF);
    }
    return result;
  }
  
  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, LocatorIF datatype) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = (OccurrenceIF) it.next();
      if (ObjectUtils.equals(occurIF.getType(), oType) && 
          ObjectUtils.equals(occurIF.getDataType(), datatype) )
      result.add(occurIF);
    }
    return result;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, Collection scope) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = (OccurrenceIF) it.next();
      if (ObjectUtils.equals(occurIF.getType(), oType) &&
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope))
      result.add(occurIF);
    }
    return result;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, LocatorIF datatype, Collection scope) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = (OccurrenceIF) it.next();
      if (ObjectUtils.equals(occurIF.getType(), oType) && 
          ObjectUtils.equals(occurIF.getDataType(), datatype) && 
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope))
      result.add(occurIF);
    }
    return result;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, String value, Collection scope) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = (OccurrenceIF) it.next();
      if (ObjectUtils.equals(occurIF.getValue(), value) && 
          ObjectUtils.equals(occurIF.getType(), oType) && 
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope))
      result.add(occurIF);
    }
    return result;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, String value, LocatorIF datatype, Collection scope) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = (OccurrenceIF) it.next();
      if (ObjectUtils.equals(occurIF.getValue(), value) && 
          ObjectUtils.equals(occurIF.getType(), oType) && 
          ObjectUtils.equals(occurIF.getDataType(), datatype) && 
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope))
      result.add(occurIF);
    }
    return result;
  }

  public static OccurrenceIF makeOccurrence(TopicIF otype, TopicIF topicIF, String value, LocatorIF datatype, Collection scope) {
    TopicMapBuilderIF builder = topicIF.getTopicMap().getBuilder();
    OccurrenceIF occ = builder.makeOccurrence(topicIF, otype, value, datatype);
    Iterator iter = scope.iterator();
    while (iter.hasNext()) {
      TopicIF scopingTopic = (TopicIF)iter.next();
      occ.addTheme(scopingTopic);
    }
    return occ;
  }

  public static List<TopicNameIF> findTopicNames(TopicIF nType, TopicIF topicIF) { 
    // HACK: this method treats on:untyped-name and names with null types the same
    boolean isUntypedName = (nType == null || nType.getSubjectIdentifiers().contains(URILocator.create("http://psi.ontopia.net/ontology/untyped-name")));
    List<TopicNameIF> result = new ArrayList<TopicNameIF>();
    Iterator it = topicIF.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF nameIF = (TopicNameIF) it.next();
      if ((ObjectUtils.equals(nameIF.getType(), nType) || (isUntypedName && nameIF.getType() == null)))
        result.add(nameIF);
    }
    return result;
  }

  public static List<TopicNameIF> findTopicNames(TopicIF nType, TopicIF topicIF, Collection scope) { 
    // HACK: this method treats on:untyped-name and names with null types the same
    boolean isUntypedName = (nType == null || nType.getSubjectIdentifiers().contains(URILocator.create("http://psi.ontopia.net/ontology/untyped-name")));
    List<TopicNameIF> result = new ArrayList<TopicNameIF>();
    Iterator it = topicIF.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF nameIF = (TopicNameIF) it.next();
      if ((ObjectUtils.equals(nameIF.getType(), nType) || 
       (isUntypedName && nameIF.getType() == null)) && 
          CollectionUtils.equalsUnorderedSet(nameIF.getScope(), scope))
      result.add(nameIF);
    }
    return result;
  }

  public static List<TopicNameIF> findTopicNames(TopicIF nType, TopicIF topicIF, String value, Collection scope) { 
    // HACK: this method treats on:untyped-name and names with null types the same
    boolean isUntypedName = (nType == null || nType.getSubjectIdentifiers().contains(URILocator.create("http://psi.ontopia.net/ontology/untyped-name")));
    List<TopicNameIF> result = new ArrayList<TopicNameIF>();
    Iterator it = topicIF.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF nameIF = (TopicNameIF) it.next();
      if (ObjectUtils.equals(nameIF.getValue(), value) && 
          (ObjectUtils.equals(nameIF.getType(), nType)  || 
       (isUntypedName && nameIF.getType() == null)) && 
          CollectionUtils.equalsUnorderedSet(nameIF.getScope(), scope))
      result.add(nameIF);
    }
    return result;
  }

  public static TopicNameIF makeTopicName(TopicIF ntype, TopicIF topicIF, String value, Collection scope) {
    // HACK: treating untyped-name as if it was had no type
    if (ntype != null && 
    ntype.getSubjectIdentifiers().contains(URILocator.create("http://psi.ontopia.net/ontology/untyped-name")))
     ntype = null;

    TopicMapBuilderIF builder = topicIF.getTopicMap().getBuilder();
    TopicNameIF name = builder.makeTopicName(topicIF, ntype, value);
    Iterator iter = scope.iterator();
    while (iter.hasNext()) {
      TopicIF scopingTopic = (TopicIF)iter.next();
      name.addTheme(scopingTopic);
    }
    return name;
  }

  public static List<AssociationRoleIF> findRoles(TopicIF aType, TopicIF rType, TopicIF player) { 
    List<AssociationRoleIF> result = new ArrayList<AssociationRoleIF>();
    Iterator it = player.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      AssociationIF association = role.getAssociation();
      if (ObjectUtils.equals(role.getType(), rType) && 
          ObjectUtils.equals(association.getType(), aType))
      result.add(role);
    }
    return result;
  }

//  public static List findRoles(TopicIF aType, TopicIF rType, TopicIF player, Collection scope) { 
//    List result = new ArrayList();
//    Iterator it = player.getRoles().iterator();
//    while (it.hasNext()) {
//      AssociationRoleIF role = (AssociationRoleIF) it.next();
//      AssociationIF association = role.getAssociation();
//      if (ObjectUtils.equals(role.getType(), rType) && 
//          ObjectUtils.equals(association.getType(), aType) && 
//          CollectionUtils.equalsUnorderedSet(association.getScope(), scope))
//      result.add(role);
//    }
//    return result;
//  }
  
  public static void setName(TopicIF nType, TopicIF topic, String value, Collection scope) {
    if (value != null && topic != null) {
      // update existing new name or create a new one
      Collection names = OntopolyModelUtils.findTopicNames(null, topic, scope);
      Iterator iter = names.iterator();
      if (iter.hasNext()) {
        TopicNameIF bn = (TopicNameIF) iter.next();
        bn.setValue(value);
      } else {
        topic.getTopicMap().getBuilder().makeTopicName(topic, value);
      }
      // remove superfluous names
      while (iter.hasNext())
        ((TopicNameIF) iter.next()).remove();
    }
  }
  
}
