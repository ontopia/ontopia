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

package net.ontopia.topicmaps.utils.deciders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Accepts or rejects topic map constructs based on their
 * relations to other topic map constructs and a filter that makes
 * decisions on an individual basis.  Used by the topic map exporters
 * to support topic filtering.
 */
public class TMExporterDecider implements Predicate<Object> {

  // Decides whether individual TMObjectIFs should be accepted or rejected.
  private Predicate<Object> filter;

  /**
   * Creates a new TMExporterDecider.
   * @param filter accepts or rejects an individual object
   */
  public TMExporterDecider(Predicate<Object> filter) {
    Objects.requireNonNull(filter, "Filter cannot be null.");
    this.filter = filter;
  }

  /**
   * Accepts or rejects AssociationIFs, TopicNameIFs, Collections, OccurrenceIFs,
   * TopicIFs and VariantNameIFs, base on the filter and their relations to
   * other objects. @param object The object to accept or reject.
   */
  @Override
  public boolean test(Object object) {
    // Check that none of the scoping topics are disallowed.
    if (object instanceof ScopedIF) {
      ScopedIF scoped = (ScopedIF)object;
      Iterator<TopicIF> scopeIt = scoped.getScope().iterator();
      while (scopeIt.hasNext()) {
        if (!test(scopeIt.next())) {
          return false;
        }
      }
    }

    if (object instanceof AssociationIF) {
      return test((AssociationIF)object);
    }
    if (object instanceof TopicNameIF) {
      return test((TopicNameIF)object);
    }
    if (object instanceof Collection) {
      return test((Collection<?>)object);
    }
    if (object instanceof OccurrenceIF) {
      return test((OccurrenceIF)object);
    }
    if (object instanceof TopicIF) {
      return test((TopicIF)object);
    }
    if (object instanceof VariantNameIF) {
      return test((VariantNameIF)object);
    }
    return true;
  }

  /**
   * Accepts or rejects a TopicNameIF
   * @param baseName to be accepted/rejected.
   * @return true iff baseName is accepted by the filter and its scope is also
   *         accepted by the filter.
   */
  public boolean test(TopicNameIF baseName) {
    return filter.test(baseName) && filter.test(baseName.getScope());
  }

  /**
   * Accepts or rejects a VariantNameIF
   * @param variantName to be accepted/rejected.
   * @return true iff variantName is accepted by the filter and its scope is
   *         also accepted by the filter.
   */
  public boolean test(VariantNameIF variantName) {
    return filter.test(variantName) && filter.test(variantName.getScope());
  }

  /**
   * Return true iff the association type, each role player and each role type
   * are accepted.
   * @param association the association to test for acceptance.
   * @return true iff the association is accepted.
   */
  public boolean test(AssociationIF association) {
    boolean retVal = test(association.getType());
    Iterator<AssociationRoleIF> rolesIt = association.getRoles().iterator();
    while (rolesIt.hasNext()) {
      AssociationRoleIF role = rolesIt.next();
      retVal &= test(role.getType()) && filter.test(role.getPlayer())
          && filter.test(role);
    }
    return retVal;
  }

  /**
   * Return true iff the type of the occurrence is accepted
   * @param occurrence The occurence to test for acceptance. It is assumed that
   *        occurrence will only be tested for acceptance if its parent topic
   *        has already been tested and passed.
   * @return true iff occurrence is accepted.
   */
  public boolean test(OccurrenceIF occurrence) {
    return filter.test(occurrence) && test(occurrence.getType())
        && test(occurrence.getScope()) && test(occurrence.getTopic());
  }

  /**
   * Return true iff the given topic, all of it's types and all types of the
   * types (etc. recursively) are accepted by the filter that was given in the
   * constructor.
   * @param topic The topic to test for acceptance.
   * @return true iff the topic is accepted.
   */
  public boolean test(TopicIF topic) {
    return test(topic, new ArrayList<TopicIF>());
  }

  /**
   * Accepts or rejects a collection of TopicsIFs.
   * @param coll The collection to test (search)
   * @return true iff whole collection of topics are accepted by filter.
   */
  private boolean test(Collection<?> coll) {
    Iterator<?> it = coll.iterator();
    while (it.hasNext()) {
      if (!test(it.next())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Return true iff the given topic, all of it's types and all types of the
   * types (etc. recursively) are accepted by the filter that was given in the
   * constructor.
   * @param topic The topic to test for acceptance.
   * @param checked Topics that have already been checked (passed).
   * @return true iff the topic is accepted.
   */
  private boolean test(TopicIF topic, Collection<TopicIF> checked) {
    // Only check each topic once.
    if (checked.contains(topic)) {
      return true;
    }
    if (topic == null) {
      return true;
    }

    if (filter.test(topic)) {
      checked.add(topic);
    } else {
      return false;
    }

    return true;
  }
}
