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

package net.ontopia.persistence.proxy;

import java.util.Map;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
  
public abstract class MasterTest implements java.io.Serializable {
  
  protected transient Map data;
  
  public String testname;
  public String objectId;
  public String value;
  
  public MasterTest(Map data, String testname) {
    this.data = data;
    this.testname = testname;
  }
  
  public abstract void run();

  public TopicIF getTopic() {
    return (TopicIF)data.get("TopicIF");
  }

  public TopicNameIF getTopicName() {
    return (TopicNameIF)data.get("TopicNameIF");
  }

  public VariantNameIF getVariantName() {
    return (VariantNameIF)data.get("VariantNameIF");
  }

  public OccurrenceIF getOccurrence() {
    return (OccurrenceIF)data.get("OccurrenceIF");
  }

  public AssociationIF getAssociation() {
    return (AssociationIF)data.get("AssociationIF");
  }

  public AssociationRoleIF getAssociationRole() {
    return (AssociationRoleIF)data.get("AssociationRoleIF");
  }
  
}
