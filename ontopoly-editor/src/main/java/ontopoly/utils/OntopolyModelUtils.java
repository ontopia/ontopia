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
package ontopoly.utils;

// TODO javadoc

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.AssociationBuilder;
import net.ontopia.utils.CollectionUtils;
import ontopoly.model.QueryMapper;
import ontopoly.model.TopicMap;

public class OntopolyModelUtils {

  private OntopolyModelUtils() {
  }

  public static RowMapperIF<Object> getRowMapperOneColumn() {
    return RowMapperOneColumn.INSTANCE;
  }

  private static class RowMapperOneColumn<T> implements RowMapperIF<T> {
    public static final RowMapperIF<Object> INSTANCE = new RowMapperOneColumn<Object>();
    @SuppressWarnings("unchecked")
    @Override
    public T mapRow(QueryResultIF queryResult, int rowno) {
      // return the value in the first column
      return (T)queryResult.getValue(0);
    }
  }

  public static TopicIF getTopicIF(TopicMap tm, LocatorIF base, String subjectIndicator) {
    return getTopicIF(tm, base.resolveAbsolute(subjectIndicator), true);
  }

  public static TopicIF getTopicIF(TopicMap tm, LocatorIF subjectIdentifier) {
    return getTopicIF(tm, subjectIdentifier, true);
  }

  public static TopicIF getTopicIF(TopicMap tm, LocatorIF subjectIdentifier, boolean mustExist) {
    TopicIF t = tm.getTopicMapIF().getTopicBySubjectIdentifier(subjectIdentifier);
    if (t == null && mustExist) {
      throw new RuntimeException("Topic not found by subject identifier: " + subjectIdentifier);
    }
    return t;
  }

  public static boolean isUnaryPlayer(
      TopicMap tm, TopicIF aType, TopicIF player, TopicIF rType) {
    return findUnaryAssociation(tm, aType, player, rType) != null;
  }

  public static AssociationIF findUnaryAssociation(
      TopicMap tm, TopicIF aType, TopicIF player, TopicIF rType) {

    Iterator<AssociationRoleIF> iter = player.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role = iter.next();
      AssociationIF assoc = role.getAssociation();
      if (Objects.equals(role.getType(), rType) &&
          Objects.equals(assoc.getType(), aType)) {
        return assoc;
      }
    }
    return null;
  }

  public static boolean hasBinaryAssociation(TopicIF player1, TopicIF aType,
      TopicIF rType1, TopicIF rType2) {
    for (AssociationRoleIF role : player1.getRolesByType(rType1, aType)) {
      AssociationIF assoc = role.getAssociation();
      for (AssociationRoleIF role2 : assoc.getRoles()) {
        if (!Objects.equals(role2, role) && role2.getType().equals(rType2)) {
          return true;
        }
      }
    }
    return false;
  }

  public static TopicIF findBinaryPlayer(TopicMap tm, LocatorIF atypeId,
      TopicIF player1, LocatorIF rtype1Id, LocatorIF rtype2Id) {
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, atypeId);
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, rtype1Id);
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, rtype2Id);

    Iterator<AssociationRoleIF> iter = player1.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role1 = iter.next();
      AssociationIF assoc = role1.getAssociation();
      Collection<AssociationRoleIF> roles = assoc.getRoles();
      if (roles.size() != 2) {
        continue;
      }
      if (Objects.equals(role1.getType(), rType1) &&
          Objects.equals(assoc.getType(), aType)) {
        Iterator<AssociationRoleIF> riter = roles.iterator();
        while (riter.hasNext()) {
          AssociationRoleIF role2 = riter.next();
          if (!Objects.equals(role1, role2) && role2.getType().equals(rType2)) {
            return role2.getPlayer();
          }
        }
      }
    }
    return null;
  }

  public static Collection<TopicIF> findBinaryPlayers(
      TopicMap tm, TopicIF aType, TopicIF player1, TopicIF rType1, TopicIF rType2) {
    List<TopicIF> result = new ArrayList<TopicIF>();
    Iterator<AssociationRoleIF> iter = player1.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role1 = iter.next();
      AssociationIF assoc = role1.getAssociation();
      Collection<AssociationRoleIF> roles = assoc.getRoles();
      if (roles.size() != 2) {
        continue;
      }
      if (Objects.equals(role1.getType(), rType1) &&
          Objects.equals(assoc.getType(), aType)) {
        Iterator<AssociationRoleIF> riter = roles.iterator();
        while (riter.hasNext()) {
          AssociationRoleIF role2 = riter.next();
          if (!Objects.equals(role1, role2) && role2.getType().equals(rType2)) {
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
    Iterator<AssociationRoleIF> iter = player1.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role1 = iter.next();
      AssociationIF assoc = role1.getAssociation();
      Collection<TopicIF> scope = assoc.getScope();
      if (!(scope.size() == 1 && scope.contains(theme))) {
        continue;
      }
      Collection<AssociationRoleIF> roles = assoc.getRoles();
      if (roles.size() != 2) {
        continue;
      }
      if (Objects.equals(role1.getType(), rType1) &&
          Objects.equals(assoc.getType(), aType)) {
        Iterator<AssociationRoleIF> riter = roles.iterator();
        while (riter.hasNext()) {
          AssociationRoleIF role2 = riter.next();
          if (!Objects.equals(role1, role2) && role2.getType().equals(rType2)) {
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

    Iterator<AssociationRoleIF> iter = player1.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role1 = iter.next();
      AssociationIF assoc = role1.getAssociation();
      Collection<AssociationRoleIF> roles = assoc.getRoles();
      if (roles.size() != 2) {
        continue;
      }
      if (Objects.equals(role1.getType(), rType1) &&
          Objects.equals(assoc.getType(), aType)) {
        Iterator<AssociationRoleIF> riter = roles.iterator();
        while (riter.hasNext()) {
          AssociationRoleIF role2 = riter.next();
          if (!Objects.equals(role1, role2) && role2.getType().equals(rType2) && role2.getPlayer().equals(player2)) {
            return assoc;
          }
        }
      }
    }
    return null;
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
    Iterator<AssociationRoleIF> iter = player1.getRoles().iterator();
    while (iter.hasNext()) {
      secondRole = null;
      thirdRole = null;
      AssociationRoleIF role1 = iter.next();
      AssociationIF assoc = role1.getAssociation();
      if (Objects.equals(role1.getType(), rType1) &&
          Objects.equals(assoc.getType(), aType)) {
        Iterator<AssociationRoleIF> riter = assoc.getRoles().iterator();
        while (riter.hasNext()) {
          AssociationRoleIF role2 = riter.next();
          if (!Objects.equals(role1, role2)) {
            if (Objects.equals(rType2, role2.getType()) && Objects.equals(player2, role2.getPlayer())) {
              secondRole = role2;
            } else if (Objects.equals(rType3, role2.getType())) {
              thirdRole = role2;
            } else {
              break;
            }
          }
        }
        if (secondRole != null && thirdRole != null) {
          result.add(thirdRole.getPlayer());
        }
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

  public static List<AssociationIF> findAssociations(
      TopicIF aType, TopicIF[] rTypes, TopicIF[] players, Collection<TopicIF> scope) {

    List<AssociationIF> result = new ArrayList<AssociationIF>();

    TopicIF player = players[0];

    Collection<AssociationRoleIF> proles = player.getRoles();
    Iterator<AssociationRoleIF> iter = proles.iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role = iter.next();

      // compare current player
      if (!Objects.equals(role.getType(), rTypes[0])) {
        continue;
      }

      // compare association type
      AssociationIF assoc = role.getAssociation();
      if (!Objects.equals(assoc.getType(), aType)) {
        continue;
      }

      // compare arity
      Collection<AssociationRoleIF> roles = assoc.getRoles();
      if (rTypes.length != roles.size()) {
        continue;
      }

      // compare scope
      if (!CollectionUtils.equalsUnorderedSet(assoc.getScope(), scope)) {
        continue;
      }

      // compare other players
      int matchCount = 1;
      boolean[] matches = new boolean[players.length];
      matches[0] = true;

      Iterator<AssociationRoleIF> riter = roles.iterator();
      while (riter.hasNext()) {
        AssociationRoleIF orole = riter.next();
        if (Objects.equals(orole, role)) {
          continue;
        }
        TopicIF ortype = orole.getType();
        TopicIF oplayer = orole.getPlayer();
        int match = -1;
        for (int i=1; i < players.length; i++) { 
          if (Objects.equals(ortype, rTypes[i]) &&
              Objects.equals(oplayer, players[i]) && !matches[i]) {
            match = i;
            matches[i] = true;
            matchCount++;
            break;
          }
        }
        if (match == -1) {
          break;
        }
      }
      if (matchCount == players.length) {
        result.add(assoc);
      }
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
      TopicIF aType, TopicIF[] rTypes, TopicIF[] players, Collection<TopicIF> scope) {

    TopicMapBuilderIF builder = aType.getTopicMap().getBuilder();
    AssociationIF assoc = builder.makeAssociation(aType);
    for (int i=0; i < rTypes.length; i++) { 
      builder.makeAssociationRole(assoc, rTypes[i], players[i]);
    }
    return assoc;
  }

  public static OccurrenceIF findOccurrence(TopicIF oType, TopicIF topicIF) { 
    Iterator<OccurrenceIF> it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = it.next();
      if (Objects.equals(occurIF.getType(), oType)) {
        return occurIF;
      }
    }
    return null;
  }

  public static OccurrenceIF findOccurrence(TopicIF oType, TopicIF topicIF, LocatorIF datatype, Collection<TopicIF> scope) { 
    Iterator<OccurrenceIF> it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = it.next();
      if (Objects.equals(occurIF.getType(), oType) && 
          Objects.equals(occurIF.getDataType(), datatype) && 
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope)) {
        return occurIF;
      }
    }
    return null;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator<OccurrenceIF> it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = it.next();
      if (Objects.equals(occurIF.getType(), oType)) {
        result.add(occurIF);
      }
    }
    return result;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, LocatorIF datatype) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator<OccurrenceIF> it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = it.next();
      if (Objects.equals(occurIF.getType(), oType) && 
          Objects.equals(occurIF.getDataType(), datatype) ) {
        result.add(occurIF);
      }
    }
    return result;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, Collection<TopicIF> scope) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator<OccurrenceIF> it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = it.next();
      if (Objects.equals(occurIF.getType(), oType) &&
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope)) {
        result.add(occurIF);
      }
    }
    return result;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, LocatorIF datatype, Collection<TopicIF> scope) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator<OccurrenceIF> it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = it.next();
      if (Objects.equals(occurIF.getType(), oType) && 
          Objects.equals(occurIF.getDataType(), datatype) && 
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope)) {
        result.add(occurIF);
      }
    }
    return result;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, String value, Collection<TopicIF> scope) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator<OccurrenceIF> it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = it.next();
      if (Objects.equals(occurIF.getValue(), value) && 
          Objects.equals(occurIF.getType(), oType) && 
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope)) {
        result.add(occurIF);
      }
    }
    return result;
  }

  public static List<OccurrenceIF> findOccurrences(TopicIF oType, TopicIF topicIF, String value, LocatorIF datatype, Collection<TopicIF> scope) { 
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    Iterator<OccurrenceIF> it = topicIF.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occurIF = it.next();
      if (Objects.equals(occurIF.getValue(), value) && 
          Objects.equals(occurIF.getType(), oType) && 
          Objects.equals(occurIF.getDataType(), datatype) && 
          CollectionUtils.equalsUnorderedSet(occurIF.getScope(), scope)) {
        result.add(occurIF);
      }
    }
    return result;
  }

  public static OccurrenceIF makeOccurrence(TopicIF otype, TopicIF topicIF, String value, LocatorIF datatype, Collection<TopicIF> scope) {
    TopicMapBuilderIF builder = topicIF.getTopicMap().getBuilder();
    OccurrenceIF occ = builder.makeOccurrence(topicIF, otype, value, datatype);
    Iterator<TopicIF> iter = scope.iterator();
    while (iter.hasNext()) {
      TopicIF scopingTopic = iter.next();
      occ.addTheme(scopingTopic);
    }
    return occ;
  }

  public static List<TopicNameIF> findTopicNames(TopicIF nType, TopicIF topicIF) { 
    List<TopicNameIF> result = new ArrayList<TopicNameIF>();
    Iterator<TopicNameIF> it = topicIF.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF nameIF = it.next();
      if (Objects.equals(nameIF.getType(), nType)) {
        result.add(nameIF);
      }
    }
    return result;
  }

  public static List<TopicNameIF> findTopicNames(TopicIF nType, TopicIF topicIF, Collection<TopicIF> scope) { 
    List<TopicNameIF> result = new ArrayList<TopicNameIF>();
    Iterator<TopicNameIF> it = topicIF.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF nameIF = it.next();
      if (Objects.equals(nameIF.getType(), nType) && 
          CollectionUtils.equalsUnorderedSet(nameIF.getScope(), scope)) {
        result.add(nameIF);
      }
    }
    return result;
  }

  public static List<TopicNameIF> findTopicNames(TopicIF nType, TopicIF topicIF, String value) { 
    List<TopicNameIF> result = new ArrayList<TopicNameIF>();
    Iterator<TopicNameIF> it = topicIF.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF nameIF = it.next();
      if (Objects.equals(nameIF.getValue(), value) && 
          Objects.equals(nameIF.getType(), nType)) {
        result.add(nameIF);
      }
    }
    return result;
  }

  public static List<TopicNameIF> findTopicNames(TopicIF nType, TopicIF topicIF, String value, Collection<TopicIF> scope) { 
    List<TopicNameIF> result = new ArrayList<TopicNameIF>();
    Iterator<TopicNameIF> it = topicIF.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF nameIF = it.next();
      if (Objects.equals(nameIF.getValue(), value) && 
          Objects.equals(nameIF.getType(), nType) && 
          CollectionUtils.equalsUnorderedSet(nameIF.getScope(), scope)) {
        result.add(nameIF);
      }
    }
    return result;
  }

  public static TopicNameIF makeTopicName(TopicIF ntype, TopicIF topicIF, String value, Collection<TopicIF> scope) {
    TopicMapBuilderIF builder = topicIF.getTopicMap().getBuilder();
    TopicNameIF name = builder.makeTopicName(topicIF, ntype, value);
    Iterator<TopicIF> iter = scope.iterator();
    while (iter.hasNext()) {
      TopicIF scopingTopic = iter.next();
      name.addTheme(scopingTopic);
    }
    return name;
  }

  public static List<AssociationRoleIF> findRoles(TopicIF aType, TopicIF rType, TopicIF player) { 
    List<AssociationRoleIF> result = new ArrayList<AssociationRoleIF>();
    Iterator<AssociationRoleIF> it = player.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = it.next();
      AssociationIF association = role.getAssociation();
      if (Objects.equals(role.getType(), rType) && 
          Objects.equals(association.getType(), aType)) {
        result.add(role);
      }
    }
    return result;
  }

  public static List<AssociationRoleIF> findRoles(TopicIF aType, TopicIF rType, TopicIF player, Collection<TopicIF> scope) { 
    List<AssociationRoleIF> result = new ArrayList<AssociationRoleIF>();
    Iterator<AssociationRoleIF> it = player.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = it.next();
      AssociationIF association = role.getAssociation();
      if (Objects.equals(role.getType(), rType) && 
          Objects.equals(association.getType(), aType) && 
          CollectionUtils.equalsUnorderedSet(association.getScope(), scope)) {
        result.add(role);
      }
    }
    return result;
  }

  public static void setName(TopicIF nType, TopicIF topic, String value, Collection<TopicIF> scope) {
    if (value != null && topic != null) {
      // update existing new name or create a new one
      Collection<TopicNameIF> names = OntopolyModelUtils.findTopicNames(null, topic, scope);
      Iterator<TopicNameIF> iter = names.iterator();
      if (iter.hasNext()) {
        TopicNameIF bn = iter.next();
        bn.setValue(value);
      } else {
        topic.getTopicMap().getBuilder().makeTopicName(topic, value);
      }
      // remove superfluous names
      while (iter.hasNext()) {
        ((TopicNameIF) iter.next()).remove();
      }
    }
  }

}
