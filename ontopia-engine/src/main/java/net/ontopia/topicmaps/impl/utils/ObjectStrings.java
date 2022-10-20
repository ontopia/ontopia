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

package net.ontopia.topicmaps.impl.utils;

import java.util.Objects;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.CollectionUtils;

/**
 * INTERNAL: Collection of toString implementations for the various
 * topic map object implementations.
 */

public class ObjectStrings {
  private static final String UNASSIGNED = "unassigned";
  private static final int MAX_STRING = 50;
  
  public static String toString(String impl, AssociationIF assoc) {
    String id = UNASSIGNED;
    try {
      if (assoc.getTopicMap() != null) {
        id = assoc.getObjectId();
      }
      return "[" + impl + ", " + id + ", type: " + assoc.getType() + "]";
    } catch (Throwable t) {
      return "[" + impl + ", " + id + "]";
    }
  }
  
  public static String toString(String impl, AssociationRoleIF role) {
    String id = UNASSIGNED;
    try {
      if (role.getTopicMap() != null) {
        id = role.getObjectId();
      }
      return "[" + impl + ", " + id + ", type: " + role.getType() + "]";
    } catch (Throwable t) {
      return "[" + impl + ", " + role.getObjectId() + "!]";
    }
  }
  
  public static String toString(String impl, TopicNameIF basename) {
    String id = UNASSIGNED;
    try {
      if (basename.getTopicMap() != null) {
        id = basename.getObjectId();
      }      
      if (basename.getValue() != null) {
        return "[" + impl + ", " + id + ", '" + basename.getValue() + "']";
      } else {
        return "[" + impl + ", " + id + "]";
      }
    } catch (Throwable t) {
      return "[" + impl + ", " + basename.getObjectId() + "!]";
    }
    
  }
  
  public static String toString(String impl, OccurrenceIF occurs) {
    String id = UNASSIGNED;
    try {
      if (occurs.getTopicMap() != null) {
        id = occurs.getObjectId();
      }
      if (Objects.equals(occurs.getDataType(), DataTypes.TYPE_URI)) {
        return "[" + impl + ", " + id + " (" + occurs.getValue() + ")]";
      } else {
        String value = occurs.getValue();
        if (value == null) {
          return "[" + impl + ", " + id + " null]";
        } else if (value.length() > MAX_STRING) {
          return "[" + impl + ", " + id + " <" + value.substring(0, MAX_STRING) +
          "...>]";
        } else {
          return "[" + impl + ", " + id + " <" + value + ">]";
        }
      }
    } catch (Throwable t) {
      return "[" + impl + ", " + occurs.getObjectId() + "!]";
    }
  }
  
  public static String toString(String impl, TopicIF topic) {
    String id = UNASSIGNED;
    try {
      if (topic.getTopicMap() != null) {
        id = topic.getObjectId();
      }
      
      // Subject
      if (topic.getSubjectLocators().size() > 0) {
        return "[" + impl + ", " + id + " (" +
					CollectionUtils.getFirstElement(topic.getSubjectLocators()) + ")]";
      
      // Subject indicators
      } else if (topic.getSubjectIdentifiers().size() > 0) {
        return "[" + impl + ", " + id + " {" +
					CollectionUtils.getFirstElement(topic.getSubjectIdentifiers()) + "}]";
      
      // Source locators
      } else if (topic.getItemIdentifiers().size() > 0) {
        return "[" + impl + ", " + id + " <" +
					CollectionUtils.getFirstElement(topic.getItemIdentifiers()) + ">]";
      } else {
        return "[" + impl + ", " + id + "]";
      }
    } catch (Throwable t) {
      return "[" + impl + ", " + topic.getObjectId() + "!]";
    }
  }
  
  public static String toString(String impl, TopicMapIF topicmap) {
    String id = UNASSIGNED;
    try {
      if (topicmap.getTopicMap() != null) {
        id = topicmap.getObjectId();
      }
      return "[" + impl + ", " + id + "]";
    } catch (Throwable t) {
      return "[" + impl + ", " + topicmap.getObjectId() + "!]";
    }
  }
  
  public static String toString(String impl, VariantNameIF variant) {
    String id = UNASSIGNED;
    try {
      if (variant.getTopicMap() != null) {
        id = variant.getObjectId();
      }
      if (Objects.equals(variant.getDataType(), DataTypes.TYPE_URI)) {
        return "[" + impl + ", " + id + " (" + variant.getValue() + ")]";
      } else {
        String value = variant.getValue();
        if (value == null) {
          return "[" + impl + ", " + id + " null]";
        } else if (value.length() > MAX_STRING) {
          return "[" + impl + ", " + id + " <" + value.substring(0, MAX_STRING) +
          "...>]";
        } else {
          return "[" + impl + ", " + id + " <" + value + ">]";
        }
      }
    } catch (Throwable t) {
      return "[" + impl + ", " + variant.getObjectId() + "!]";
    }
  }
  
}
