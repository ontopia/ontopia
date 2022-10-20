/*
 * #!
 * Ontopia Vizigator
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
package net.ontopia.topicmaps.viz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav.utils.comparators.TopicComparator;
import net.ontopia.utils.OntopiaRuntimeException;

public class VizTopicTypePriorityConfigManager {

  private VizTopicMapConfigurationManager tmConfig;

  private TopicIF rankTopic;
  private TopicIF rankAssociationType;
  private TopicIF rankAboveRoleType;
  private TopicIF rankBelowRoleType;
  private TopicIF defaultTypePrecedenceTopic;

  private static TopicComparator topicComparator = new TopicComparator();
  
  private static final String RANK_TOPIC = 
    VizTopicMapConfigurationManager.BASE + "rank-topic";
  private static final String DEFAULT_TYPE_PRECEDENCE_TOPIC = 
    VizTopicMapConfigurationManager.BASE + "default-precedence-topic";
  private static final String RANK_ASSOCIATION = 
      VizTopicMapConfigurationManager.BASE + "rank-association";
  private static final String RANK_ABOVE_ROLE = 
      VizTopicMapConfigurationManager.BASE + "rank-above-role";
  private static final String RANK_BELOW_ROLE = 
      VizTopicMapConfigurationManager.BASE + "rank-below-role";

  public VizTopicTypePriorityConfigManager(
      VizTopicMapConfigurationManager tmConfig) {
    this.tmConfig = tmConfig;

    rankTopic = tmConfig.getTopic(RANK_TOPIC);
    defaultTypePrecedenceTopic =
        tmConfig.getTopic(DEFAULT_TYPE_PRECEDENCE_TOPIC);
    rankAssociationType = tmConfig.getTopic(RANK_ASSOCIATION);
    rankAboveRoleType = tmConfig.getTopic(RANK_ABOVE_ROLE);
    rankBelowRoleType = tmConfig.getTopic(RANK_BELOW_ROLE);
    
    if (rankTopic.getRolesByType(rankBelowRoleType).isEmpty()) {
      // Initialize the priority circuit with rankTopic.
      AssociationIF newRankAssociation = 
          tmConfig.builder.makeAssociation(rankAssociationType);
      tmConfig.builder.makeAssociationRole(newRankAssociation, rankBelowRoleType, rankTopic);
      tmConfig.builder.makeAssociationRole(newRankAssociation, rankAboveRoleType, rankTopic);
    }
    
    if (!isRanked(defaultTypePrecedenceTopic)) {
      rankLast(defaultTypePrecedenceTopic);
    }
  }
  
  /**
   * Get the topic ranked above or below this topic.
   * If 'up' is true, will return the topic ranked above.
   * Otherwise it will return the topic ranked below. 
   * @param source The topic ranked above/below target (pending 'up').
   * @param up Indicates whether to return the below or above topic.
   * @return The topic ranked below or above this topic.
   */
  private TopicIF getNeighbourRanked(TopicIF source, boolean up) {
    AssociationRoleIF sourceRole = getRankRole(source, up);
    AssociationIF association = getRankAssociation(sourceRole);
    AssociationRoleIF targetRole = getRankRole(association, up);

    TopicIF target = targetRole.getPlayer();
    if (target == null) {
      throw new OntopiaRuntimeException("Error in configuration: Every"
          + " rank-below-role must have a player.");
    }
    return target;
  }
  
  /**
   * Get the the above/below rank role played by a given topic.
   * @param source The topic to get the rank role from.
   * @param up Indicates whether to get the role moving up or down in rank.
   * @return the the above/below rank role played by 'source'.
   */
  private AssociationRoleIF getRankRole(TopicIF source, boolean up) {
    TopicIF sourceRoleType = up ? rankBelowRoleType : rankAboveRoleType;
    
    // Get the above/below rank role played by the given topic.
    Collection rankRoles = source.getRolesByType(sourceRoleType);
    if (rankRoles.size() != 1) {
      String nextTo = up ? "below" : "above";
      
      if (rankRoles.isEmpty()) {
        throw new OntopiaRuntimeException("Error in configuration: Missing"
            + " ranked " + nextTo + " role on configuration topic.");
      }

      throw new OntopiaRuntimeException("Error in configuration: Found more"
          + " than one ranked " + nextTo + " role on configuration topic.");
    }

    AssociationRoleIF rankRole = (AssociationRoleIF)rankRoles.iterator()
        .next();
    return rankRole;
  }
  
  /**
   * Get the rank association of a given rank role (there can be at most one).
   * @param source A rank role of the rank association.
   * @return the rank association of 'source'.
   */  
  private AssociationIF getRankAssociation(AssociationRoleIF source) {
    // Get the rank association of the given rank role.
    AssociationIF rankAssociation = source.getAssociation();
    
    if (rankAssociation == null || rankAssociation.getType()
        != rankAssociationType) {
      throw new OntopiaRuntimeException("Error in configuration: Missing rank"
          + " association on rank role.");
    }
    return rankAssociation;
  }
    
  /**
   * Get the the above/below rank role of a given rank association.
   * @param source The association to get the rank role from.
   * @param up Indicates whether to get the role moving up or down in rank.
   * @return the the above/below rank role played by 'source'.
   */
  private AssociationRoleIF getRankRole(AssociationIF source, boolean up) {    
    TopicIF targetRoleType = up ? rankAboveRoleType : rankBelowRoleType;
    
    // Get the above/below rank role of the given association.
    Collection rankRoles = source.getRolesByType(targetRoleType);
    if (rankRoles.size() > 1) {
      String nextTo = up ? "above" : "below";
      throw new OntopiaRuntimeException("Error in configuration: Every rank"
          + " association must have exactly one rank-" + nextTo + "-role.");
    }

    AssociationRoleIF belowRole = (AssociationRoleIF)rankRoles.iterator()
        .next();
    return belowRole;
  }
  
  /**
   * Add source to the end of the ranking list.
   * @param source The topic to be added to the end of the ranking list.
   */
  public void rankLast(TopicIF source) {
    // NOTE: The rank is a circuit, with rankTopic ranked aboved the topic
    // with the highest rank and below the topic with the lowest rank.

    // Get the rank-below role played by 'rankTopic'.
    AssociationRoleIF role = getRankRole(rankTopic, true);
    
    // Let 'source' take the place of 'rankTopic' as the lowest ranked.
    role.setPlayer(source);
    
    // Now rank 'rankTopic' below 'source' to maintain the circuit.
    AssociationIF newRankAssociation = tmConfig.builder.makeAssociation(rankAssociationType);
    tmConfig.builder.makeAssociationRole(newRankAssociation, rankBelowRoleType, rankTopic);
    tmConfig.builder.makeAssociationRole(newRankAssociation, rankAboveRoleType, source);
  }
  
  public void rankNextTo(TopicIF ranked, TopicIF toRank, boolean before) {
    // NOTE: The rank is a circuit, with rankTopic ranked aboved the topic
    // with the highest rank and below the topic with the lowest rank.

    // Get the rank-role on the side where 'toRank' should be inserted.
    AssociationRoleIF nextToRole = getRankRole(ranked, before);
    
    // Attach 'toRank' to to the topic next to 'ranked'.
    nextToRole.setPlayer(toRank);
    
    // Now attach 'toRank' to 'ranked'.
    AssociationIF newRankAssociation = tmConfig.builder.makeAssociation(rankAssociationType);
    tmConfig.builder.makeAssociationRole(newRankAssociation, rankAboveRoleType, before ? toRank : ranked);
    tmConfig.builder.makeAssociationRole(newRankAssociation, rankBelowRoleType, before ? ranked : toRank);
  }
  
  public boolean isRanked(TopicIF configTopic) {
    return getConfiguredTopicTypeRank().contains(configTopic);
  }
  
  public List getConfiguredTopicTypeRank() {
    ArrayList retVal = new ArrayList();
    
    // Start with the topic ranked just below the rank topic.
    TopicIF currentTopic = getNeighbourRanked(getRankTopic(), false);
    
    // Process each ranked topic in turn until the rank topic is reached (again)
    while (currentTopic != rankTopic) {
      retVal.add(currentTopic);
      currentTopic = getNeighbourRanked(currentTopic, false);
    }
    return retVal;
  }

  public List getRankedTopicTypes(Collection realTopics) {
    // Map config topics to real topics.
    Map configToRealTopics = new HashMap();
    Iterator realTopicsIt = realTopics.iterator();
    while (realTopicsIt.hasNext()) {
      TopicIF currentTopic = (TopicIF)realTopicsIt.next();
      configToRealTopics.put(tmConfig.getConfigTopic(currentTopic), 
                             currentTopic);
    }
    
    List retVal = new ArrayList();
    
    // Start with the topic ranked just below the rank topic.
    TopicIF currentTopic = getNeighbourRanked(getRankTopic(), false);
    
    // Process each ranked topic in turn until the rank topic is reached (again)
    while (currentTopic != rankTopic) {
      if (currentTopic == defaultTypePrecedenceTopic) {
        retVal.add(defaultTypePrecedenceTopic);
      } else {
        TopicIF rankedTopic = (TopicIF)configToRealTopics.get(currentTopic);
        if (rankedTopic != null) {
          retVal.add(rankedTopic);
        }
      }
      currentTopic = getNeighbourRanked(currentTopic, false);
    }
    return retVal;
  }

  public void augmentTopicTypeRank(Collection topicTypes) {
    List configuredRank = getConfiguredTopicTypeRank();
    
    TopicIF afterDefault = 
        getNeighbourRanked(defaultTypePrecedenceTopic, false);
    
    Iterator topicTypesIt = topicTypes.iterator();
    while (topicTypesIt.hasNext()) {
      TopicIF currentTopic = (TopicIF)topicTypesIt.next();
      TopicIF configTopic = tmConfig.getConfigTopic(currentTopic);
      
      if (!configuredRank.contains(configTopic)) {
        configuredRank.add(configTopic);
        
        // Add configTopic to the end of the ranking list.
        rankNextTo(afterDefault, configTopic, true);
      }
    }
  }
  
  public void changeRank(TopicIF realTopic, boolean up) {
    TopicIF topic = tmConfig.getConfigTopic(realTopic);

    // Get the relevant roles played by 'topic' and 'swappedTopic', i.e. the
    // topic is should be swapped with.
    AssociationRoleIF outerRole = getRankRole(topic, !up);
    AssociationRoleIF innerRole = getRankRole(topic, up);
    AssociationIF association = getRankAssociation(innerRole);
    AssociationRoleIF newInnerRole = getRankRole(association, up);
    TopicIF swappedTopic = newInnerRole.getPlayer();
    AssociationRoleIF newOuterRole = getRankRole(swappedTopic, up);
    
    // Replace 'topic's position by 'swappedTopic'.
    outerRole.setPlayer(swappedTopic);
    innerRole.setPlayer(swappedTopic);
    
    // Replace 'swappedTopic's position by 'topic'.
    newOuterRole.setPlayer(topic);
    newInnerRole.setPlayer(topic);
  }

  public TopicIF highestRankedType(List realTypes) {
    // If the topic is untyped, no type is highest in rank.
    if (realTypes.isEmpty()) {
      return null;
    }

    // Sort the real topic types.
    Collections.sort(realTypes, topicComparator );

    // Get the topics with a rank in order of rank.
    List rankedTypes = getRankedTopicTypes(realTypes);
    
    // If there are no ranked topics, choose the first of the real topics.
    if (rankedTypes.isEmpty()) {
      return (TopicIF)realTypes.iterator().next();
    }
    
    TopicIF firstRanked = (TopicIF)rankedTypes.get(0);
    if (!firstRanked.equals(defaultTypePrecedenceTopic)) {
      return firstRanked;
    }
      
    // For default type, search 'realTypes' for the first unranked type.
    Iterator realTypesIt = realTypes.iterator();
    while (realTypesIt.hasNext()) {
      TopicIF currentType = (TopicIF)realTypesIt.next();
      if (!isRanked(tmConfig.getConfigTopic(currentType))) {
        return currentType;
      }
    }
    
    // If no unranked type was found, return the ranked type below the default
    return (TopicIF)rankedTypes.get(1);
  }

  private TopicIF getRankTopic() {
    return rankTopic;
  }

  public TopicIF getDefaultTypePrecedenceTopic() {
    return defaultTypePrecedenceTopic;
  }
}
