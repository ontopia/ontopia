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

package net.ontopia.topicmaps.nav.context;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;

/**
 * INTERNAL: Abstract class which provides access to filtering themes
 * out which are not relevant to the user context.
 */
public class BasenameUserThemeFilter extends AbstractUserThemeFilter {

  // members
  protected ScopeIndexIF scopeIndex;
  protected ClassInstanceIndexIF instanceIndex;


  public BasenameUserThemeFilter(TopicMapIF topicMap) {
    // super(topicMap);
    setTopicMap(topicMap);
  }

  //
  // implementing method from UserThemeFilterIF
  //
  
  @Override
  public void setTopicMap(TopicMapIF topicMap) {
    this.topicMap = topicMap;

    // initialize useful indexes
    scopeIndex = (ScopeIndexIF) topicMap.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");
    instanceIndex = (ClassInstanceIndexIF) topicMap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
  }
  
  /**
   * Process theme and find out if it belongs to the user context and
   * should therefore be displayed when selecting them. If any of the
   * following rules apply the theme should be not displayed.
   *
   * [Assumption:
   *      Topic "A" is a theme that scopes a base name of topic "B"]
   * 
   *
   * Rule 1: if there exists an association between topic belonging to 
   *         this theme and one or more topics which are related to the
   *         scoped base names
   *         OR
   *         if one or more topics belonging to scoped basename(s)
   *         is/are of type of the theme
   *
   *         ( "B" is associated with "A" OR
   *           "B" is an instance of "A" )
   *
   * Role 2: topic belonging to this theme is used to type association roles
   *         AND
   *         one or more topics belonging to with this theme scoped basenames
   *         are used to type associations in which the
   *         topic belonging to the theme is playing an association role
   *
   *         ( "A" is the role type in at least one association of type "B" AND
   *           "B" is an association type )
   *
   * Role 3: one or more of the topics belonging to the scoped basenames
   *         is/are used as a scope theme itself/themselves
   *
   *         ( "B" is a theme )
   *
   * @return boolean: true if this theme should not be displayed
   *                  for user context configuration
   */
  @Override
  public boolean shouldNotBeUsed(TopicIF actTheme) {

    boolean usedAsTopicType           = false; // (pre) Rule 1A
    boolean usedAsAssociationRoleType = false; // (pre) Rule 2

    TypeHierarchyUtils hierarchyHelper = new TypeHierarchyUtils();
    
    // --- get base names that use this theme
    Collection baseNames = scopeIndex.getTopicNames(actTheme);
    //log.info("\n  used for scoping " + baseNames.size() + " topic base names.");

    // is not used for scoping anywhere?
    if (baseNames.size() == 0) {
      return true;
    }

    // (pre) RULE 1A: theme topic is used to type topics
    if ( instanceIndex.usedAsTopicType(actTheme) ) {
      usedAsTopicType = true;
    }

    // (pre) RULE 2: theme topic is used to type association roles
    if ( instanceIndex.usedAsAssociationRoleType(actTheme) ) {
      //log.debug("  *** " + actTheme + " is used for typing association roles ");
      usedAsAssociationRoleType = true;
    }

    // set up number of topics that are checked
    int nTotalTopics = baseNames.size();
    int nRemainingTopics = nTotalTopics;
    
    // --- iterate through all topics which belong to scoped basenames
    Iterator itNames = baseNames.iterator();
    while (itNames.hasNext()) {
      TopicNameIF baseName = (TopicNameIF) itNames.next();
      TopicIF topic = baseName.getTopic();
      //log.debug("  * scoped baseName: " + baseName);

      boolean typedByTheme        = false; // Rule 1A
      boolean associatedWithTheme = false; // Rule 1B
      boolean usedToTypeAssocs    = false; // Rule 2
      boolean usedAsTopicNameTheme = false; // Rule 3A
  
      // RULE 1A: topic belonging to scoped basename is of type of theme topic
      if ( usedAsTopicType && hierarchyHelper.isInstanceOf(topic, actTheme) ) {
        typedByTheme = true;
      }
      else {
      
        // RULE 1B: check if associated with the topic of this theme
        if ( hierarchyHelper.isAssociatedWith(topic, actTheme) ) {
          associatedWithTheme = true;
        }
        else {

          // RULE 2: with this theme scoped base name is used to type associations
          if ( usedAsAssociationRoleType && instanceIndex.usedAsAssociationType(topic) ) {
            usedToTypeAssocs = true;
            // FIXME: be more accurate here
            // "B" is an association type AND
            // "A" is the role type in at least one association of type "B"
          }
          else {

            // RULE 3A: if used in other theme
            boolean checkRule3 = false;
            if ( scopeIndex.usedAsTopicNameTheme(topic) && checkRule3) {

              // usedAsTopicNameTheme = true;
              // -- break out of the loop if at least one topic is used as theme
              // nRemainingTopics = 0;
              // break;
              
              // RULE 3B: check for all topics if they are all used as themes
              Iterator itNamesR3 = baseNames.iterator();
              int nRemainingTopicsR3 = baseNames.size();
              while (itNamesR3.hasNext()) {
                TopicNameIF baseNameR3 = (TopicNameIF) itNamesR3.next();
                TopicIF topicR3 = baseNameR3.getTopic();

                //log.debug("  Check Rule 3 for topic: " + topicR3);
                //log.debug("  -> usedToScope: " + scopeIndex.usedAsTopicNameTheme(topicR3));
                
                if ( scopeIndex.usedAsTopicNameTheme(topicR3) ) {
                  nRemainingTopicsR3--;
                } else {
                  break;
                }
              }
              // theme should not be displayed, if all topics are used for scoping
              if (nRemainingTopicsR3 == 0) {
                nRemainingTopics = 0;
                break;
              }
              
            } // if RULE 3A applies

                
          } // if RULE 2 applies
          
        } // if RULE 1B applies
      } // if RULE 1A applies

      // if rule (1A|1B|2|3A) apply then reduce number of remaining topics
      if (associatedWithTheme || typedByTheme || usedToTypeAssocs || usedAsTopicNameTheme) {
        nRemainingTopics--;
      }

    } // while itNames


    // if no topics left, theme should NOT be displayed 
    return (nRemainingTopics == 0);

  }


  
}





