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

import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Utilities for copying topic map data.
 *
 * @since 3.4
 */
public class CopyUtils {

  /**
   * INTERNAL: Creates a new topic and copies all the characteristics
   * from the source topic to this new topic.
   *
   * @return the new topic with a copy of all the characteristics.
   */
  public static TopicIF copyCharacteristics(TopicIF source) {
    TopicMapBuilderIF builder = source.getTopicMap().getBuilder();
    TopicIF n = builder.makeTopic();
    copyCharacteristics(n, source);
    return n;
  }
  
  /**
   * INTERNAL: Copies all the characteristics from the source topic to
   * the target topic.
   */
  public static void copyCharacteristics(TopicIF target, TopicIF source) {
    copyTypes(target, source);
    copyTopicNames(target, source);
    copyOccurrences(target, source);
    copyAssociations(target, source);
  }
  
  // --- occurrences

  private static void copyOccurrences(TopicIF target, TopicIF source) {
    Iterator<OccurrenceIF> it = source.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF o = it.next();
      copyOccurrence(target, o);
    }
  }

  /**
   * INTERNAL: Copies the occurrence from the source topic to the
   * target topic.
   *
   * @since 4.0
   */
  public static OccurrenceIF copyOccurrence(TopicIF target, OccurrenceIF source) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();
    OccurrenceIF o = builder.makeOccurrence(target, source.getType(), ""); // HACK: needs improvement
    CopyUtils.copyOccurrenceData(o, source);
    copyScope(o, source);
    return o;
  }

  /**
   * INTERNAL: Copies the occurrence value and datatype from the
   * source topic to the target topic.
   *
   * @since 4.0
   */
  public static void copyOccurrenceData(OccurrenceIF target, OccurrenceIF source) {
    if (source.getLength() > DataTypes.SIZE_THRESHOLD) {
      Reader r = source.getReader();
      try {
        target.setReader(r, source.getLength(), source.getDataType());
      } catch (Exception e) {
        try {
          r.close();
        } catch (Exception e2) {
        }
        throw new OntopiaRuntimeException(e);
      }
    } else {
      target.setValue(source.getValue(), source.getDataType());
    }
  }

  // --- base names

  private static void copyTopicNames(TopicIF target, TopicIF source) {
    Iterator<TopicNameIF> it = source.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF o = it.next();
      copyTopicName(target, o);
    }
  }
  
  /**
   * INTERNAL: Copies the name from the source topic to the
   * target topic.
   *
   * @since 4.0
   */
  public static TopicNameIF copyTopicName(TopicIF target, TopicNameIF source) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();
    TopicNameIF n = builder.makeTopicName(target, source.getType(), source.getValue());
    copyScope(n, source);
    copyVariants(n, source);
    return n;
  }

  // --- variants

  private static void copyVariants(TopicNameIF target, TopicNameIF source) {
    Iterator<VariantNameIF> it = source.getVariants().iterator();
    while (it.hasNext()) {
      VariantNameIF o = it.next();
      copyVariant(target, o);
    }
  }
  
  /**
   * INTERNAL: Copies the variant from the source topic to the
   * target topic.
   *
   * @since 4.0
   */
  public static VariantNameIF copyVariant(TopicNameIF target, VariantNameIF source) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();
    VariantNameIF n = builder.makeVariantName(target, "", Collections.emptySet()); // HACK: needs improvement
    copyScope(n, source);
    copyVariantData(n, source);
    return n;
  }

  /**
   * INTERNAL: Copies the variant value and datatype from the
   * source topic to the target topic.
   *
   * @since 4.0
   */
  public static void copyVariantData(VariantNameIF target, VariantNameIF source) {
    if (source.getLength() > DataTypes.SIZE_THRESHOLD) {
      target.setReader(source.getReader(), source.getLength(), source.getDataType());
    } else {
      target.setValue(source.getValue(), source.getDataType());
    }
  }

  // --- associations

  private static void copyAssociations(TopicIF target, TopicIF source) {
    Set<AssociationIF> uniqueAssocs = new HashSet<AssociationIF>();

    Iterator<AssociationRoleIF> roleIterator = source.getRoles().iterator();
    while (roleIterator.hasNext()) {
      AssociationRoleIF o = roleIterator.next();
      uniqueAssocs.add(o.getAssociation());
    }
    Iterator<AssociationIF> associationIterator = uniqueAssocs.iterator();
    while (associationIterator.hasNext()) {
      AssociationIF o = associationIterator.next();
      copyAssociation(target, o, source);
    }
  }
  
  /**
   * INTERNAL: Copies the variant from the source topic to the
   * target topic.
   *
   * @since 4.0
   */
  public static AssociationIF copyAssociation(TopicIF targetPlayer, AssociationIF source, TopicIF sourcePlayer) {
    TopicMapBuilderIF builder = targetPlayer.getTopicMap().getBuilder();
    AssociationIF n = builder.makeAssociation(source.getType());
    copyScope(n, source);

    Iterator<AssociationRoleIF> it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF o = it.next();
      TopicIF player = o.getPlayer();
      if (player != null && player.equals(sourcePlayer)) {
        player = targetPlayer;
      }
      builder.makeAssociationRole(n, o.getType(), player);
    }
    return n;
  }
  
  // --- scope

  private static void copyScope(ScopedIF target, ScopedIF source) {
    Iterator<TopicIF> it = source.getScope().iterator();
    while (it.hasNext()) {
      target.addTheme(it.next());
    }
  }

  // --- types

  private static void copyTypes(TopicIF target, TopicIF source) {
    Iterator<TopicIF> it = source.getTypes().iterator();
    while (it.hasNext()) {
      target.addType(it.next());
    }
  }

}
