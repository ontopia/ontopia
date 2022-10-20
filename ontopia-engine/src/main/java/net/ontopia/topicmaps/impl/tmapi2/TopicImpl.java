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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.NotRemovableException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class TopicImpl extends ConstructImpl implements Topic {

  private TopicIF wrapped;

  public TopicImpl(TopicMapImpl topicMap, TopicIF topic) {
    super(topicMap);
    wrapped = topic;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */

  @Override
  protected TopicIF getWrapped() {
    return wrapped;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getSubjectIdentifiers()
   */

  @Override
  public Set<Locator> getSubjectIdentifiers() {
    return topicMap.wrapSet(wrapped.getSubjectIdentifiers());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#addSubjectIdentifier(org.tmapi.core.Locator)
   */

  @Override
  public void addSubjectIdentifier(Locator sid) {
    try {
      wrapped.addSubjectIdentifier(topicMap.unwrapLocator(sid));
    } catch (ConstraintViolationException ex) {
      throw new IdentityConstraintException(this, topicMap
          .getTopicBySubjectIdentifier(sid), sid,
          "A topic with the subject identifier " + sid.getReference()
              + " exists");
    } catch (NullPointerException ex) {
      throw new ModelConstraintException(this,
          "The subject identifier must not be null");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#removeSubjectIdentifier(org.tmapi.core.Locator)
   */

  @Override
  public void removeSubjectIdentifier(Locator sid) {
    if (sid == null) {
      throw new ModelConstraintException(this,
          "The subject identifier must not be null");
    }
    wrapped.removeSubjectIdentifier(topicMap.unwrapLocator(sid));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getSubjectLocators()
   */

  @Override
  public Set<Locator> getSubjectLocators() {
    return topicMap.wrapSet(wrapped.getSubjectLocators());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#addSubjectLocator(org.tmapi.core.Locator)
   */

  @Override
  public void addSubjectLocator(Locator slo) {
    try {
      wrapped.addSubjectLocator(topicMap.unwrapLocator(slo));
    } catch (ConstraintViolationException ex) {
      throw new IdentityConstraintException(this, topicMap
          .getTopicBySubjectLocator(slo), slo,
          "A topic with the subject locator " + slo.getReference() + " exists");
    } catch (NullPointerException ex) {
      throw new ModelConstraintException(this,
          "The subject locator must not be null");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#removeSubjectLocator(org.tmapi.core.Locator)
   */

  @Override
  public void removeSubjectLocator(Locator slo) {
    if (slo == null) {
      throw new ModelConstraintException(this,
          "The subject locator must not be null");
    }
    wrapped.removeSubjectLocator(topicMap.unwrapLocator(slo));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getTypes()
   */

  @Override
  public Set<Topic> getTypes() {
    return topicMap.wrapSet(wrapped.getTypes());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#addType(org.tmapi.core.Topic)
   */

  @Override
  public void addType(Topic type) {
    Check.typeNotNull(this, type);
    Check.typeInTopicMap(getTopicMap(), type);
    wrapped.addType(topicMap.unwrapTopic(type));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#removeType(org.tmapi.core.Topic)
   */

  @Override
  public void removeType(Topic type) {
    Check.typeNotNull(this, type);
    wrapped.removeType(topicMap.unwrapTopic(type));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createName(java.lang.String,
   * org.tmapi.core.Topic[])
   */

  @Override
  public Name createName(String value, Topic... scope) {
    return createName(topicMap.getDefaultNameType(), value, scope);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createName(java.lang.String,
   * java.util.Collection)
   */

  @Override
  public Name createName(String value, Collection<Topic> scope) {
    Check.scopeNotNull(this, scope);
    return createName(value, scope.toArray(new Topic[scope.size()]));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createName(org.tmapi.core.Topic,
   * java.lang.String, org.tmapi.core.Topic[])
   */

  @Override
  public Name createName(Topic type, String value, Topic... scope) {
    Check.typeNotNull(this, type);
    Check.valueNotNull(this, value);
    Check.scopeNotNull(this, scope);
    Check.scopeInTopicMap(getTopicMap(), scope);
    Check.typeInTopicMap(getTopicMap(), type);
    TopicNameIF name = topicMap.getWrapped().getBuilder().makeTopicName(
        wrapped, topicMap.unwrapTopic(type), value);
    applyScope(name, scope);
    return topicMap.wrapName(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createName(org.tmapi.core.Topic,
   * java.lang.String, java.util.Collection)
   */

  @Override
  public Name createName(Topic type, String value, Collection<Topic> scope) {
    Check.scopeNotNull(this, scope);
    return createName(type, value, scope.toArray(new Topic[scope.size()]));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic,
   * java.lang.String, org.tmapi.core.Topic[])
   */

  @Override
  public Occurrence createOccurrence(Topic type, String value, Topic... scope) {
    Check.typeNotNull(this, type);
    Check.valueNotNull(this, value);
    Check.scopeNotNull(this, scope);
    Check.scopeInTopicMap(getTopicMap(), scope);
    Check.typeInTopicMap(getTopicMap(), type);
    OccurrenceIF occ = topicMap.getWrapped().getBuilder().makeOccurrence(
        wrapped, topicMap.unwrapTopic(type), value);
    applyScope(occ, scope);
    return topicMap.wrapOccurrence(occ);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic,
   * java.lang.String, java.util.Collection)
   */

  @Override
  public Occurrence createOccurrence(Topic type, String value,
      Collection<Topic> scope) {
    Check.scopeNotNull(this, scope);
    return createOccurrence(type, value, scope.toArray(new Topic[scope.size()]));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic,
   * org.tmapi.core.Locator, org.tmapi.core.Topic[])
   */

  @Override
  public Occurrence createOccurrence(Topic type, Locator value, Topic... scope) {
    Check.typeNotNull(this, type);
    Check.valueNotNull(this, value);
    Check.scopeNotNull(this, scope);
    Check.scopeInTopicMap(getTopicMap(), scope);
    Check.typeInTopicMap(getTopicMap(), type);
    OccurrenceIF occ = topicMap.getWrapped().getBuilder().makeOccurrence(
        wrapped, topicMap.unwrapTopic(type), topicMap.unwrapLocator(value));
    applyScope(occ, scope);
    return topicMap.wrapOccurrence(occ);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic,
   * org.tmapi.core.Locator, java.util.Collection)
   */

  @Override
  public Occurrence createOccurrence(Topic type, Locator value,
      Collection<Topic> scope) {
    Check.scopeNotNull(this, scope);
    return createOccurrence(type, value, scope.toArray(new Topic[scope.size()]));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic,
   * java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])
   */

  @Override
  public Occurrence createOccurrence(Topic type, String value,
      Locator datatype, Topic... scope) {
    Check.typeNotNull(this, type);
    Check.valueNotNull(this, value, datatype);
    Check.scopeNotNull(this, scope);
    Check.typeInTopicMap(getTopicMap(), type);
    OccurrenceIF occ = topicMap.getWrapped().getBuilder().makeOccurrence(
        wrapped, topicMap.unwrapTopic(type), value,
        topicMap.unwrapLocator(datatype));
    applyScope(occ, scope);
    return topicMap.wrapOccurrence(occ);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#createOccurrence(org.tmapi.core.Topic,
   * java.lang.String, org.tmapi.core.Locator, java.util.Collection)
   */

  @Override
  public Occurrence createOccurrence(Topic type, String value,
      Locator datatype, Collection<Topic> scope) {
    Check.scopeNotNull(this, scope);
    return createOccurrence(type, value, datatype, scope
        .toArray(new Topic[scope.size()]));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getNames()
   */

  @Override
  public Set<Name> getNames() {
    return topicMap.wrapSet(wrapped.getTopicNames());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getNames(org.tmapi.core.Topic)
   */
  @Override
  public Set<Name> getNames(Topic type) {
    Check.typeNotNull(type);
    TopicIF type_ = topicMap.unwrapTopic(type);
    Collection<TopicNameIF> names = new ArrayList<TopicNameIF>();
    for (Iterator<TopicNameIF> iter = wrapped.getTopicNames().iterator(); iter
        .hasNext();) {
      TopicNameIF name = iter.next();
      if (type_.equals(name.getType())) {
        names.add(name);
      }
    }
    return topicMap.wrapSet(names);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getOccurrences()
   */

  @Override
  public Set<Occurrence> getOccurrences() {
    return topicMap.wrapSet(wrapped.getOccurrences());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getOccurrences(org.tmapi.core.Topic)
   */
  @Override
  public Set<Occurrence> getOccurrences(Topic type) {
    Check.typeNotNull(type);
    TopicIF type_ = topicMap.unwrapTopic(type);
    Collection<OccurrenceIF> occs = new ArrayList<OccurrenceIF>();
    for (Iterator<OccurrenceIF> iter = wrapped.getOccurrences().iterator(); iter
        .hasNext();) {
      OccurrenceIF occ = iter.next();
      if (type_.equals(occ.getType())) {
        occs.add(occ);
      }
    }
    return topicMap.wrapSet(occs);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getParent()
   */

  @Override
  public TopicMapImpl getParent() {
    return topicMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getReified()
   */

  @Override
  public Reifiable getReified() {
    return (Reifiable) topicMap.wrapTMObject(wrapped.getReified());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getRolesPlayed()
   */

  @Override
  public Set<Role> getRolesPlayed() {
    return topicMap.wrapSet(wrapped.getRoles());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getRolesPlayed(org.tmapi.core.Topic)
   */

  @Override
  public Set<Role> getRolesPlayed(Topic type) {
    Check.typeNotNull(type);
    return topicMap.wrapSet(wrapped.getRolesByType(topicMap.unwrapTopic(type)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#getRolesPlayed(org.tmapi.core.Topic,
   * org.tmapi.core.Topic)
   */

  @Override
  public Set<Role> getRolesPlayed(Topic type, Topic assocType) {
    Check.typeNotNull(type);
    
    if (assocType == null) {
      throw new IllegalArgumentException(
          "The association type must not be null");
    }
    return topicMap.wrapSet(wrapped.getRolesByType(topicMap.unwrapTopic(type),
        topicMap.unwrapTopic(assocType)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Topic#mergeIn(org.tmapi.core.Topic)
   */

  @Override
  public void mergeIn(Topic topic) {
    if (topicMap.unwrapTopic(topic) == getWrapped()) {
      return;
    }
    try {
      MergeUtils.mergeInto(this.getWrapped(), topicMap.unwrapTopic(topic));
      // getWrapped().merge(topicMap.unwrapTopic(topic));
    } catch (InvalidTopicMapException e) {
      throw new ModelConstraintException(this, e.getMessage());
    } catch (ConstraintViolationException e) {
      throw new ModelConstraintException(this, e.getMessage());
    }

  }

  @Override
  public void remove() {
    isDeletable();

    try {
      super.remove();
    } catch (NotRemovableException e) {
      TopicInUseException ex = new TopicInUseException(this, e.getMessage());
      ex.initCause(ex);
      throw ex;
    }
  }

  private void isDeletable() {
    if (getReified() != null) {
      throw new TopicInUseException(this, "The topic reifies a construct");
    }
    if (getRolesPlayed().size() > 0) {
      throw new TopicInUseException(this, "The topic plays at least one role");
    }

    TypeInstanceIndex idx = getTopicMap().getIndex(TypeInstanceIndex.class);

    if (idx.getOccurrenceTypes().contains(this)) {
      throw new TopicInUseException(this,
          "The topic is used as an occurrence type!");
    }

    if (idx.getRoleTypes().contains(this)) {
      throw new TopicInUseException(this, "The topic is used as a role type!");
    }

    if (idx.getAssociationTypes().contains(this)) {
      throw new TopicInUseException(this,
          "The topic is used as an association type!");
    }

    if (idx.getTopicTypes().contains(this)) {
      throw new TopicInUseException(this, "The topic is used as a topic type!");
    }

    if (idx.getNameTypes().contains(this)) {
      throw new TopicInUseException(this, "The topic is used as a name type!");
    }

    ScopedIndex si = topicMap.getIndex(ScopedIndex.class);
    if ((si.getAssociationThemes().contains(this))
        || (si.getNameThemes().contains(this))
        || (si.getOccurrenceThemes().contains(this))
        || (si.getVariantThemes().contains(this))) {
      throw new TopicInUseException(this,
          "The topic is used as a theme (scoping topic)!");
    }
  }

  /**
   * INTERNAL: Adds the specified scope to the scoped construct.
   * 
   * @param scoped
   *          The scoped construct.
   * @param scope
   *          The scope, must not be <tt>null</tt>.
   */
  private void applyScope(ScopedIF scoped, Topic... scope) {
    for (Topic theme : scope) {
      scoped.addTheme(topicMap.unwrapTopic(theme));
    }
  }

}
