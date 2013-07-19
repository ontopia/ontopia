/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.schema.core.SchemaIF;
import net.ontopia.topicmaps.schema.core.SchemaValidatorIF;

/**
 * PUBLIC: Represents an entire OSL schema.
 */
public class OSLSchema implements SchemaIF {
  protected SchemaValidatorIF validator;
  protected Collection topicClasses;
  protected Collection assocClasses;
  protected boolean strict;
  protected Map rulesets;
  protected Map topicsById;
  protected LocatorIF address;

  /**
   * PUBLIC: Creates a new OSL schema object.
   * @param address The base address of the schema.
   */
  public OSLSchema(LocatorIF address) {
    this.address = address;
    
    validator = new SchemaValidator(this);
    topicClasses = new ArrayList();
    assocClasses = new ArrayList();
    rulesets = new HashMap();
    topicsById = new HashMap();
    strict = false;
  }

  // --- SchemaIF methods
  
  public SchemaValidatorIF getValidator() {
    return validator;
  }

  public LocatorIF getAddress() {
    return address;
  }

  // --- OSLSchema

  /**
   * INTERNAL: Adds a RuleSet object to the schema.
   */
  public void addRuleSet(RuleSet rule) {
    // FIXME: duplicates!
    rulesets.put(rule.getId(), rule);
  }

  /**
   * INTERNAL: Adds a TopicClass object to the schema.
   */
  public void addTopicClass(TopicClass topicClass) {
    topicClasses.add(topicClass);
    if (topicClass.getId() != null)
      topicsById.put(topicClass.getId(), topicClass);
  }

  /**
   * INTERNAL: Adds an AssociationClass object to the schema.
   */
  public void addAssociationClass(AssociationClass assocClass) {
    assocClasses.add(assocClass);
  }
  
  /**
   * INTERNAL: Returns all the rule sets in this schema.
   */
  public Collection getRuleSets() {
    return rulesets.values();
  }
  
  /**
   * INTERNAL: Returns all the topic classes in this schema.
   */
  public Collection getTopicClasses() {
    return topicClasses;
  }

  /**
   * INTERNAL: Returns all the association classes in this schema.
   */
  public Collection getAssociationClasses() {
    return assocClasses;
  }

  /**
   * INTERNAL: Removes the rule set from this schema. If the schema
   * does not have the rule set the call is ignored.
   */
  public void removeRuleSet(RuleSet rule) {
    rulesets.remove(rule.getId());
  }

  /**
   * INTERNAL: Removes the topic class from this schema. If the schema
   * does not have the topic class the call is ignored.
   */
  public void removeTopicClass(TopicClass topicClass) {
    topicClasses.remove(topicClass);
    if (topicClass.getId() != null)
      topicsById.remove(topicClass.getId());
  }

  /**
   * INTERNAL: Removes the association class from this schema. If the schema
   * does not have the association class the call is ignored.
   */
  public void removeAssociationClass(AssociationClass assocClass) {
    assocClasses.remove(assocClass);
  }

  /**
   * INTERNAL: Returns the topic class that has the given ID. If no topic
   * class has this ID, null is returned.
   */
  public TopicClass getTopicClass(String id) {
    return (TopicClass) topicsById.get(id);
  }
  
  /**
   * INTERNAL: Returns the rule set that has the given ID. If no rule
   * set has this ID, null is returned.
   */
  public RuleSet getRuleSet(String id) {
    return (RuleSet) rulesets.get(id);
  }

  /**
   * INTERNAL: True if the schema uses strict matching, false otherwise.
   */
  public boolean isStrict() {
    return strict;
  }

  /**
   * INTERNAL: Sets the matching policy of the schema.
   */
  public void setIsStrict(boolean strict) {
    this.strict = strict;
  }
  
}
