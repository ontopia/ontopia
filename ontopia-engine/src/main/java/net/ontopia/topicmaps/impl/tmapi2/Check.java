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

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.Collection;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

/**
 * INTERNAL: Provides various argument constraint checks.
 */
public final class Check {
  private static final String NOT_PART_OF_TOPICMAP = " is not part of Topic Map";

  private Check() {
    // noop.
  }

  /**
   * Throws a {@link ModelConstraintException} with the specified
   * <tt>sender</tt> and <tt>msg</tt>
   * 
   * @param sender
   *          The sender
   * @param msg
   *          The error message
   */
  private static void reportError(Construct sender, String msg) {
    throw new ModelConstraintException(sender, msg);
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param scope
   *          The scope.
   */
  public static void scopeNotNull(Construct sender, Topic[] scope) {
    if (scope == null) {
      reportError(sender, "The scope must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>theme</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param theme
   *          The theme.
   */
  public static void themeNotNull(Construct sender, Topic theme) {
    if (theme == null) {
      throw new ModelConstraintException(sender, "The theme must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param scope
   *          The scope.
   */
  public static void scopeNotNull(Construct sender, Collection<Topic> scope) {
    if (scope == null) {
      reportError(sender, "The scope must not be null");
    }
  }
  
  /**
   * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is
   * empty.
   * 
   * @param sender
   *          The sender.
   * @param scope
   *          The scope.
   */
  public static void scopeNotEmpty(Construct sender, Collection<Topic> scope) {
    if (scope.size() == 0) {
      reportError(sender, "The scope must not be empty");
    }
  }
  
  /**
   * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is
   * empty.
   * 
   * @param sender
   *          The sender.
   * @param scope
   *          The scope.
   */
  public static void scopeNotEmpty(Construct sender, Topic[] scope) {
    if (scope.length == 0) {
      reportError(sender, "The scope must not be empty");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>type</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param type
   *          The type.
   */
  public static void typeNotNull(Construct sender, Topic type) {
    if (type == null) {
      reportError(sender, "The type must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>value</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param value
   *          The value.
   */
  public static void valueNotNull(Construct sender, Object value) {
    if (value == null) {
      reportError(sender, "The value must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>value</tt> or the
   * <tt>datatype</tt> is <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param value
   *          The value.
   * @param datatype
   *          The datatype.
   */
  public static void valueNotNull(Construct sender, Object value,
      Locator datatype) {
    valueNotNull(sender, value);
    if (datatype == null) {
      reportError(sender, "The datatype must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>player</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param player
   *          The player.
   */
  public static void playerNotNull(Construct sender, Topic player) {
    if (player == null) {
      reportError(sender, "The role player must not be null");
    }
  }

  public static void subjectIdentifierNotNull(Construct sender, Locator loc) {
    if (loc == null) {
      reportError(sender, "The subject identifier must not be null");
    }
  }

  public static void subjectLocatorNotNull(Construct sender, Locator loc) {
    if (loc == null) {
      reportError(sender, "The subject locator must not be null");
    }
  }

  public static void itemIdentifierNotNull(Construct sender, Locator loc) {
    if (loc == null) {
      reportError(sender, "The item identifier must not be null");
    }
  }

  /**
   * Throws a IllegalArgumentException if the subject locator is
   * <code>null</code>
   * 
   * @param loc
   *          the locator to check
   */
  public static void subjectLocatorNotNull(Locator loc) {
    if (loc == null) {
      throw new IllegalArgumentException("The subject locator must not be null");
    }
  }

  /**
   * Throws a IllegalArgumentException if the subject identifier is
   * <code>null</code>
   * 
   * @param sid
   *          the identifier to check
   */
  public static void subjectIdentifierNotNull(Locator sid) {
    if (sid == null) {
      throw new IllegalArgumentException("The subject locator must not be null");
    }
  }

  /**
   * Throws a IllegalArgumentException if the value is <code>null</code>
   * 
   * @param value
   *          the value to check
   */
  public static void valueNotNull(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }
  }

  /**
   * Throws a IllegalArgumentException if the locator is <code>null</code>
   * 
   * @param loc
   *          the locator to check
   */
  public static void locatorNotNull(Locator loc) {
    if (loc == null) {
      throw new IllegalArgumentException("The locator must not be null");
    }
  }

  /**
   * Throws a IllegalArgumentException if the datatype is <code>null</code>
   * 
   * @param loc
   *          the locator to check
   */
  public static void datatypeNotNull(Locator loc) {
    if (loc == null) {
      throw new IllegalArgumentException("The datatype must not be null");
    }
  }

  /**
   * Throws a {@link IllegalArgumentException} iff the <tt>theme</tt> is
   * <tt>null</tt>.
   * 
   * @param theme
   *          The theme.
   */
  public static void themeNotNull(Topic theme) {
    if (theme == null) {
      throw new IllegalArgumentException("The theme must not be null");
    }
  }

  /**
   * Throws a {@link IllegalArgumentException} iff the <tt>theme</tt> is
   * <tt>null</tt>.
   * 
   * @param themes
   *          The array of themes.
   */
  public static void themeNotNull(Topic... themes) {
    if (themes == null) {
      throw new IllegalArgumentException("The theme must not be null");
    }
  }

  /**
   * Throws a {@link IllegalArgumentException} iff the <tt>type</tt> is
   * <tt>null</tt>.
   * 
   * @param type
   *          The type.
   */
  public static void typeNotNull(Topic type) {
    if (type == null) {
      throw new IllegalArgumentException("The type must not be null");
    }
  }

  /**
   * Checks if the scope is in the topic map
   * 
   * 
   * @param topicMap
   *          the topicMap creating a scoped statement
   * @param scope
   *          an array of themes (scope types)
   * @throws ModelConstraintException
   *           if a theme is not part of the topic map
   */
  public static void scopeInTopicMap(TopicMap topicMap, Topic... scope) {
    for (Topic theme : scope) {
      if (!theme.getTopicMap().equals(topicMap )) {
        reportError(topicMap, "Theme " + theme.getId()
            + NOT_PART_OF_TOPICMAP);
      }
    }
  }

  /**
   * Checks if the type is in the topic map
   * 
   * 
   * @param topicMap
   *          the topicMap creating a typed construct
   * @param type
   *          type to check
   * @throws ModelConstraintException
   *           if the type is not part of the topic map
   */
  public static void typeInTopicMap(TopicMap topicMap, Topic type) {
    if (!type.getTopicMap().equals(topicMap )) {
      reportError(topicMap, "Type " + type.getId()
          + NOT_PART_OF_TOPICMAP);
    }

  }

  /**
   * Checks if the reifier is in the topic map
   * 
   * 
   * @param topicMap
   *          the topicMap creating a typed construct
   * @param reifier
   *          reifier to check
   * @throws ModelConstraintException
   *           if the reifier is not part of the topic map
   */
  public static void reifierInTopicMap(TopicMap topicMap, Topic reifier) {
    if (!reifier.getTopicMap().equals(topicMap )) {
      reportError(topicMap, "Reifier " + reifier.getId()
          + NOT_PART_OF_TOPICMAP);
    }

  }

  /**
   * Checks if the player is in the topic map
   * 
   * 
   * @param topicMap
   *          the topicMap creating a typed construct
   * @param player
   *          player to check
   * @throws ModelConstraintException
   *           if the player is not part of the topic map
   */
  public static void playerInTopicMap(TopicMap topicMap, Topic player) {
    if (!player.getTopicMap().equals(topicMap )) {
      reportError(topicMap, "Player " + player.getId()
          + NOT_PART_OF_TOPICMAP);
    }

  }
}
