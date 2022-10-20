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

package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * PUBLIC: Utilities for generating keys from complex topic map
 * objects. The keys from two different objects in the same topic map
 * will be equal if the two objects are equal according to the
 * equality rules of the TMDM.
 */
public class KeyGenerator {
  private static final char SPACER = '$';

  /**
   * PUBLIC: Makes a key for an occurrence. The key is made up of 
   * a scope key, a type key, and a data key.
   *
   * @return string containing key
   */ 
  public static String makeOccurrenceKey(OccurrenceIF occ) {
    return makeScopeKey(occ) + SPACER + makeTypedKey(occ) + makeDataKey(occ);
  }

  /**
   * PUBLIC: Makes a key for an occurrence, as it would look in
   * another topic map.   
   *
   * @since 5.1.3
   * @return string containing key
   */ 
  public static String makeOccurrenceKey(OccurrenceIF occ, TopicMapIF othertm) {
    return makeScopeKey(occ, othertm) + SPACER + makeTypedKey(occ, othertm) +
           makeDataKey(occ);
  }

  /**
   * PUBLIC: Makes a key for a topic name. The key is made up of 
   * a scope key and a value key.
   *
   * @return string containing key
   */ 
  public static String makeTopicNameKey(TopicNameIF bn) {
    return makeScopeKey(bn) + SPACER + makeTypedKey(bn) + "$$" + bn.getValue();
  }

  /**
   * PUBLIC: Makes a key for a topic name, as it would look in another
   * topic map.
   *
   * @since 5.1.3
   * @return string containing key
   */ 
  public static String makeTopicNameKey(TopicNameIF bn, TopicMapIF othertm) {
    return makeScopeKey(bn, othertm) + SPACER + makeTypedKey(bn, othertm) + "$$" +
           bn.getValue();
  }
  
  /**
   * PUBLIC: Makes a key for a variant name. The key is made up of 
   * a scope key and a value/locator key.
   *
   * @since 1.2.0
   * @return string containing key
   */ 
  public static String makeVariantKey(VariantNameIF vn) {
    return makeScopeKey(vn) + makeDataKey(vn);
  }
    
  /**
   * PUBLIC: Makes a key for an association. The key is made up from
   * the type and for each role its type and player.
   * @param assoc The association to make a key for
   * @return string containing key
   */
  public static String makeAssociationKey(AssociationIF assoc) {
    StringBuilder sb = new StringBuilder();

    // asssociation type key fragment
    sb.append(makeTypedKey(assoc)).append(SPACER)
      .append(makeScopeKey(assoc)).append(SPACER);
    
    List<AssociationRoleIF> roles = new ArrayList<AssociationRoleIF>(assoc.getRoles());
    String[] rolekeys = new String[roles.size()];
    for (int i = 0; i < rolekeys.length; i++) {
      rolekeys[i] = makeAssociationRoleKey(roles.get(i));
    }

    Arrays.sort(rolekeys);
    sb.append(StringUtils.join(rolekeys, SPACER));
    return sb.toString();
  }

  /**
   * PUBLIC: Makes a key for an association, but does not include
   * the player of the given role. The key is made up from the type
   * and for each role its type and player (except the given role for
   * which only the role type is included). This method is used to
   * create a signature for the association without taking one of the
   * roles into account.
   * @param assoc The association to make a key for
   * @param role The association role whose role player is to be left
   * out of the key.
   * @return string containing key
   * @since 3.4.2
   */
  public static String makeAssociationKey(AssociationIF assoc,
                                          AssociationRoleIF role) {
    StringBuilder sb = new StringBuilder();

    // asssociation type key fragment
    sb.append(makeTypedKey(assoc)).append(SPACER)
      .append(makeScopeKey(assoc)).append(SPACER);
    
    List<AssociationRoleIF> roles = new ArrayList<AssociationRoleIF>(assoc.getRoles());
    roles.remove(role);
    String[] rolekeys = new String[roles.size()];
    for (int i = 0; i < rolekeys.length; i++) {
      rolekeys[i] = makeAssociationRoleKey(roles.get(i));
    }
    
    sb.append(makeTypedKey(role)).append(SPACER);
    Arrays.sort(rolekeys);
    sb.append(StringUtils.join(rolekeys, SPACER));
    return sb.toString();
  }

  /**
   * PUBLIC: Makes a key for an association, as it would look in another
   * topic map.
   * @since 5.1.3
   */
  public static String makeAssociationKey(AssociationIF assoc,
                                          TopicMapIF othertm) {
    StringBuilder sb = new StringBuilder();

    // asssociation type key fragment
    sb.append(makeTypedKey(assoc, othertm)).append(SPACER)
      .append(makeScopeKey(assoc, othertm)).append(SPACER);
    
    Collection<AssociationRoleIF> roles = new ArrayList<AssociationRoleIF>(assoc.getRoles());
    String[] rolekeys = new String[roles.size()];
    int i = 0;
    for (AssociationRoleIF role : roles) {
      rolekeys[i++] = makeAssociationRoleKey(role, othertm);
    }
    
    Arrays.sort(rolekeys);
    sb.append(StringUtils.join(rolekeys, SPACER));
    return sb.toString();
  }
    
  /**
   * PUBLIC: Makes a key for an association role. The key is made up
   * of the role and the player.
   * @since 1.2.0
   * @param role The association role.
   * @return The key.
   */
  public static String makeAssociationRoleKey(AssociationRoleIF role) {
    return makeTypedKey(role) + ":" + role.getPlayer().getObjectId();
  }

  /**
   * PUBLIC: Makes a key for an association role, as it would look in
   * another topic map.
   * @since 5.1.3
   */
  public static String makeAssociationRoleKey(AssociationRoleIF role,
                                              TopicMapIF othertm) {
    TopicIF otherplayer = MergeUtils.findTopic(othertm, role.getPlayer());
    return makeTypedKey(role, othertm) + ":" + otherplayer.getObjectId();
  }
  
  /**
   * PUBLIC: Makes a key for any reifiable object, using the other
   * methods in this class.
   * @since 5.1.0
   */
  public static String makeKey(ReifiableIF object) {
    if (object instanceof TopicNameIF) {
      return makeTopicNameKey((TopicNameIF) object);
    } else if (object instanceof OccurrenceIF) {
      return makeOccurrenceKey((OccurrenceIF) object);
    } else if (object instanceof AssociationIF) {
      return makeAssociationKey((AssociationIF) object);
    } else if (object instanceof AssociationRoleIF) {
      return makeAssociationRoleKey((AssociationRoleIF) object);
    } else if (object instanceof VariantNameIF) {
      return makeVariantKey((VariantNameIF) object);
    } else {
      throw new OntopiaRuntimeException("Cannot make key for: " + object);
    }
  }

  /**
   * PUBLIC: Makes a key for any reifiable object as it would look
   * like were the object in another topic map. Useful for checking if
   * a given object exists in another topic map.
   *
   * @param object The object to make a key for.
   * @param topicmap The topic map in which to interpret the key.
   *
   * @since 5.1.3
   */
  public static String makeKey(ReifiableIF object, TopicMapIF topicmap) {
    if (object instanceof TopicNameIF) {
      return makeTopicNameKey((TopicNameIF) object, topicmap);
    } else if (object instanceof OccurrenceIF) {
      return makeOccurrenceKey((OccurrenceIF) object, topicmap);
    } else if (object instanceof AssociationIF) {
      return makeAssociationKey((AssociationIF) object, topicmap);
    } else if (object instanceof AssociationRoleIF) {
      return makeAssociationRoleKey((AssociationRoleIF) object, topicmap);
    } else {
      throw new OntopiaRuntimeException("Cannot make key for: " + object);
    }
  }
  
  // --- Helper methods

  // used by TopicMapSynchronizer
  protected static String makeTopicKey(TopicIF topic) {
    if (topic == null) {
      return "";
    } else {
      return topic.getObjectId();
    }
  }  
  
  protected static String makeTypedKey(TypedIF typed) {
    return typed.getType().getObjectId();
  }

  protected static String makeTypedKey(TypedIF typed, TopicMapIF othertm) {
    TopicIF othertype = MergeUtils.findTopic(othertm, typed.getType());
    return othertype.getObjectId();
  }
    
  protected static String makeScopeKey(ScopedIF scoped) {
    return makeScopeKey(scoped.getScope());
  }

  protected static String makeScopeKey(ScopedIF scoped, TopicMapIF othertm) {
    return makeScopeKey(scoped.getScope(), othertm);
  }
  
  protected static String makeScopeKey(Collection<TopicIF> scope) {
    Iterator<TopicIF> it = scope.iterator();
    String[] ids = new String[scope.size()];
    int ix = 0;
    while (it.hasNext()) {
      TopicIF theme = it.next();
      ids[ix++] = theme.getObjectId();
    }

    Arrays.sort(ids);
    return StringUtils.join(ids, " ");
  }

  protected static String makeScopeKey(Collection<TopicIF> scope,
                                       TopicMapIF othertm) {
    Iterator<TopicIF> it = scope.iterator();
    String[] ids = new String[scope.size()];
    int ix = 0;
    while (it.hasNext()) {
      TopicIF theme = it.next();
      TopicIF othertheme = MergeUtils.findTopic(othertm, theme);
      if (othertheme == null) {
        throw new OntopiaRuntimeException("No topic corresponding to: " +
                                          theme);
      }
      ids[ix++] = othertheme.getObjectId();
    }

    Arrays.sort(ids);
    return StringUtils.join(ids, " ");
  }
  
  protected static String makeDataKey(OccurrenceIF occ) {
    return "$$" + occ.getValue() + SPACER + occ.getDataType();
  }

  protected static String makeDataKey(VariantNameIF variant) {
    return "$$" + variant.getValue() + SPACER + variant.getDataType();
  }
}
