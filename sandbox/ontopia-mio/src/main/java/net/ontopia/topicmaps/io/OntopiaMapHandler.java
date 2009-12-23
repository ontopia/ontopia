/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.io;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.AbstractHamsterMapHandler;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.ClassInstanceUtils;
import net.ontopia.topicmaps.utils.KeyGenerator;
import net.ontopia.topicmaps.utils.MergeUtils;

/**
 * PUBLIC: {@link com.semagia.mio.IMapHandler} implementation.
 * 
 */
public class OntopiaMapHandler extends AbstractHamsterMapHandler<TopicIF> {

  private final TopicMapIF _tm;
  private final TopicMapBuilderIF _builder;
  private final Collection<DelayedRoleEvents> _delayedRoleEvents;

  /**
   * Creates a map handler which operates upon the provided topic map.
   *
   * @param topicMap The topic map.
   */
  public OntopiaMapHandler(TopicMapIF topicMap) {
    if (topicMap == null) {
      throw new IllegalArgumentException("The topic map must not be null");
    }
    _tm = topicMap;
    _builder = topicMap.getBuilder();
    _delayedRoleEvents = new ArrayList<DelayedRoleEvents>();
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.AbstractHamsterMapHandler#endTopicMap()
   */
  @Override
  public void endTopicMap() throws MIOException {
    super.endTopicMap();
    ClassInstanceUtils.resolveAssociations1(_tm);
    ClassInstanceUtils.resolveAssociations2(_tm);
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#createAssociation(java.lang.Object, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
   */
  @Override
  protected void createAssociation(TopicIF type, Collection<TopicIF> scope,
      TopicIF reifier, Collection<String> iids,
      Collection<IRole<TopicIF>> roles) throws MIOException {
    AssociationIF assoc = _builder.makeAssociation(type);
    applyScope(assoc, scope);
    for (IRole<TopicIF> r: roles) {
      AssociationRoleIF role = _builder.makeAssociationRole(assoc, r.getType(), r.getPlayer());
      if (r.getReifier() != null || !r.getItemIdentifiers().isEmpty()) {
        _delayedRoleEvents.add(new DelayedRoleEvents(role, r.getReifier(), r.getItemIdentifiers()));
      }
    }
    applyReifier(assoc, reifier);
    applyItemIdentifiers(assoc, iids);
    if (!_delayedRoleEvents.isEmpty()) {
      for (DelayedRoleEvents evt: _delayedRoleEvents) {
        applyReifier(evt.role, evt.reifier);
        applyItemIdentifiers(evt.role, evt.iids);
      }
      _delayedRoleEvents.clear();
    }
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#createName(java.lang.Object, java.lang.Object, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
   */
  @Override
  protected void createName(TopicIF parent, TopicIF type, String value,
      Collection<TopicIF> scope, TopicIF reifier,
      Collection<String> iids, Collection<IVariant<TopicIF>> variants)
      throws MIOException {
    TopicNameIF name = null;
    if (type == null) {
      type = _defaultNameType();
    }
    name = _builder.makeTopicName(parent, type, value);
    applyScope(name, scope);
    applyItemIdentifiers(name, iids);
    applyReifier(name, reifier);
    @SuppressWarnings("unchecked")
    final Collection<TopicIF> nameScope = name.getScope();
    for (IVariant<TopicIF> v: variants) {
      if (nameScope.containsAll(v.getScope())) {
        throw new MIOException("The variant's scope is not a superset of the parent's scope.");
      }
      VariantNameIF var = null;
      final String datatype = v.getDatatype();
      if (XSD.ANY_URI.equals(datatype)) {
        var = _builder.makeVariantName(name, createLocator(v.getValue()), v.getScope());
      }
      else if (XSD.STRING.equals(datatype)) {
        var = _builder.makeVariantName(name, v.getValue(), v.getScope());
      }
      else {
        var = _builder.makeVariantName(name, v.getValue(), createLocator(datatype), v.getScope());
      }
      applyItemIdentifiers(var, v.getItemIdentifiers());
      applyReifier(var, v.getReifier());
    }
  }

  private TopicIF _defaultNameType() throws MIOException {
    return createTopicBySubjectIdentifier(TMDM.TOPIC_NAME);
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#createOccurrence(java.lang.Object, java.lang.Object, java.lang.String, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection)
   */
  @Override
  protected void createOccurrence(TopicIF parent, TopicIF type, String value,
      String datatype, Collection<TopicIF> scope, TopicIF reifier,
      Collection<String> iids) throws MIOException {
    OccurrenceIF occ = null;
    if (XSD.ANY_URI.equals(datatype)) {
      occ = _builder.makeOccurrence(parent, type, createLocator(value));
    }
    else if (XSD.STRING.equals(datatype)) {
      occ = _builder.makeOccurrence(parent, type, value);
    }
    else {
      occ = _builder.makeOccurrence(parent, type, value, createLocator(datatype));
    }
    applyScope(occ, scope);
    applyItemIdentifiers(occ, iids);
    applyReifier(occ, reifier);
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#createTopicByItemIdentifier(java.lang.String)
   */
  @Override
  protected TopicIF createTopicByItemIdentifier(String iri)
      throws MIOException {
    final LocatorIF iid = createLocator(iri);
    final TMObjectIF existingConstruct = _tm.getObjectByItemIdentifier(iid);
    if (existingConstruct != null) {
      if (existingConstruct instanceof TopicIF) {
        return (TopicIF) existingConstruct;
      }
      else {
        throw new MIOException("The item identifier " + iri + " is already assigned to another construct");
      }
    }
    TopicIF topic = _tm.getTopicBySubjectIdentifier(iid);
    if (topic != null) {
      topic.addItemIdentifier(iid);
    }
    else {
      topic = _builder.makeTopic();
      topic.addItemIdentifier(iid);
    }
    return topic;
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#createTopicBySubjectIdentifier(java.lang.String)
   */
  @Override
  protected TopicIF createTopicBySubjectIdentifier(String iri)
      throws MIOException {
    final LocatorIF sid = createLocator(iri);
    TopicIF topic = _tm.getTopicBySubjectIdentifier(sid);
    if (topic != null) {
      return topic;
    }
    final TMObjectIF existingConstruct = _tm.getObjectByItemIdentifier(sid);
    if (existingConstruct != null && existingConstruct instanceof TopicIF) {
      return (TopicIF) existingConstruct;
    }
    else {
      topic = _builder.makeTopic();
      topic.addSubjectIdentifier(sid);
    }
    return topic;
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#createTopicBySubjectLocator(java.lang.String)
   */
  @Override
  protected TopicIF createTopicBySubjectLocator(String iri)
      throws MIOException {
    final LocatorIF slo = createLocator(iri);
    TopicIF topic = _tm.getTopicBySubjectLocator(slo);
    if (topic == null) {
      topic = _builder.makeTopic();
      topic.addSubjectLocator(slo);
    }
    return topic;
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#handleItemIdentifier(java.lang.Object, java.lang.String)
   */
  @Override
  protected void handleItemIdentifier(TopicIF topic, String iri)
      throws MIOException {
    final LocatorIF iid = createLocator(iri);
    final TMObjectIF existingConstruct = _tm.getObjectByItemIdentifier(iid);
    if (existingConstruct != null) {
      if (existingConstruct instanceof TopicIF) {
        if (!existingConstruct.equals(topic)) {
          merge(topic, (TopicIF) existingConstruct);
          topic = (TopicIF) existingConstruct;
        }
        return;
      }
      else {
        throw new MIOException("The item identifier " + iri + " is already assigned to another construct");
      }
    }
    final TopicIF existingTopic = _tm.getTopicBySubjectIdentifier(iid);
    if (existingTopic != null && !existingTopic.equals(topic)) {
      merge(topic, existingTopic);
      topic = existingTopic;
    }
    topic.addItemIdentifier(iid);
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#handleSubjectIdentifier(java.lang.Object, java.lang.String)
   */
  @Override
  protected void handleSubjectIdentifier(TopicIF topic, String iri)
      throws MIOException {
    final LocatorIF sid = createLocator(iri);
    TopicIF existingTopic = _tm.getTopicBySubjectIdentifier(sid);
    if (existingTopic != null) {
      if (!existingTopic.equals(topic)) {
        merge(topic, existingTopic);
      }
      return;
    }
    final TMObjectIF existing = _tm.getObjectByItemIdentifier(sid);
    if (existing != null && existing instanceof TopicIF && !existing.equals(topic)) {
      existingTopic = (TopicIF) existing;
      merge(topic, existingTopic);
      topic = existingTopic;
    }
    topic.addSubjectIdentifier(sid);
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#handleSubjectLocator(java.lang.Object, java.lang.String)
   */
  @Override
  protected void handleSubjectLocator(TopicIF topic, String iri)
      throws MIOException {
    final LocatorIF slo = createLocator(iri);
    final TopicIF existing = _tm.getTopicBySubjectLocator(slo);
    if (existing != null && !existing.equals(topic)) {
      merge(topic, existing);
    }
    else {
      topic.addSubjectLocator(slo);
    }
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#handleTopicMapItemIdentifier(java.lang.String)
   */
  @Override
  protected void handleTopicMapItemIdentifier(String iri) throws MIOException {
    _tm.addItemIdentifier(createLocator(iri));
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#handleTopicMapReifier(java.lang.Object)
   */
  @Override
  protected void handleTopicMapReifier(TopicIF reifier) throws MIOException {
    _tm.setReifier(reifier);
  }

  /* (non-Javadoc)
   * @see com.semagia.mio.helpers.HamsterHandler#handleTypeInstance(java.lang.Object, java.lang.Object)
   */
  @Override
  protected void handleTypeInstance(TopicIF instance, TopicIF type)
      throws MIOException {
    instance.addType(type);
  }

  /**
   * 
   *
   * @param iri
   * @return
   * @throws MIOException
   */
  private static LocatorIF createLocator(String iri) throws MIOException {
    try {
      return new URILocator(iri);
    }
    catch (MalformedURLException ex) {
      throw new MIOException(ex);
    }
  }

  /**
   * Merges the <tt>source</tt> into the <tt>target</tt>.
   *
   * @param source The source topic (will be removed).
   * @param target The target topic.
   */
  private void merge(TopicIF source, TopicIF target) {
    MergeUtils.mergeInto(target, source);
    super.notifyMerge(source, target);
  }

  /**
   * Sets the scope of the scoped construct.
   *
   * @param scoped The scoped construct.
   * @param scope A collection of topics or <tt>null</tt>.
   */
  private static void applyScope(ScopedIF scoped, Collection<TopicIF> scope) {
    if (scope != null) {
      for (TopicIF theme: scope) {
        scoped.addTheme(theme);
      }
    }
  }

  /**
   * Sets the item identifiers of the provided reifiable.
   *
   * @param reifiable The construct.
   * @param iids The item identifiers.
   * @throws MIOException In case of an identity conflict.
   */
  private void applyItemIdentifiers(ReifiableIF reifiable, Iterable<String> iids) throws MIOException {
    for (String iid: iids) {
      try {
        reifiable.addItemIdentifier(createLocator(iid));
      }
      catch (UniquenessViolationException ex) {
        final TMObjectIF existing = _tm.getObjectByItemIdentifier(createLocator(iid));
        if (areMergable(reifiable, existing)) {
          merge((ReifiableIF) existing, reifiable);
        }
        else {
          throw new MIOException(ex);
        }
      }
    }
  }

  /**
   * Sets the reifier of <tt>reifiable</tt> if <tt>reifier</tt> is not <tt>null</tt>.
   *
   * @param reifiable The reifiable construct.
   * @param reifier The reifier or <tt>null</tt>.
   * @throws MIOException In case of a model constraint violation.
   */
  private static void applyReifier(ReifiableIF reifiable, TopicIF reifier) throws MIOException {
    if (reifier == null) {
      return;
    }
    if (reifier.getReified() != null) {
      final ReifiableIF existing = reifier.getReified();
      if (existing.equals(reifiable)) {
        return;
      }
      if (areMergable(reifiable, existing)) {
        merge(existing, reifiable);
      }
      else {
        throw new MIOException("The topic reifies another construct");
      }
    }
    else {
      reifiable.setReifier(reifier);
    }
  }

  /**
   * Returns if the constructs are mergable.
   * 
   * @see #areMergable(ReifiableIF, ReifiableIF)
   *
   * @param reifiable The first construct.
   * @param tmo The second construct.
   * @return <tt>true</tt> if the constructs are mergable, otherwise <tt>false</tt>.
   */
  private static boolean areMergable(ReifiableIF reifiable, TMObjectIF tmo) {
    return tmo instanceof ReifiableIF 
        && areMergable(reifiable, (ReifiableIF) tmo);
  }

  /**
   * Returns if the constructs are mergable.
   * <p>
   * The constructs are mergable if they are duplicates.
   * </p>
   *
   * @param reifiableA The first construct.
   * @param reifiableB The second construct.
   * @return <tt>true</tt> if the constructs are mergable, otherwise <tt>false</tt>.
   */
  private static boolean areMergable(ReifiableIF reifiableA, ReifiableIF reifiableB) {
    boolean res = reifiableA.getClass().equals(reifiableB.getClass()) 
            && KeyGenerator.makeKey(reifiableA).equals(KeyGenerator.makeKey(reifiableB));
    if (reifiableA instanceof AssociationRoleIF) {
      // Only mergable if the parents are equal (they are duplicates)
      res = res && KeyGenerator.makeAssociationKey(((AssociationRoleIF) reifiableA).getAssociation())
              .equals(KeyGenerator.makeAssociationKey(((AssociationRoleIF) reifiableB).getAssociation()));
    }
    else if (reifiableA instanceof VariantNameIF) {
      final TopicNameIF parentA = ((VariantNameIF) reifiableA).getTopicName();
      final TopicNameIF parentB = ((VariantNameIF) reifiableB).getTopicName();
      // Only mergable if the parents belong to the same topic and if the names are equal (they are duplicates)
      res = res && parentA.getTopic().equals(parentB.getTopic())
            && KeyGenerator.makeTopicNameKey(parentA).equals(KeyGenerator.makeTopicNameKey(parentB));
    }
    return res;
  }

  /**
   * Merges the <tt>source</tt> into the <tt>target</tt>. The <tt>source</tt>
   * will be removed.
   *
   * @param source The source.
   * @param target The target.
   */
  private static void merge(ReifiableIF source, ReifiableIF target) {
    if (target instanceof AssociationRoleIF && 
        !((AssociationRoleIF) target).getAssociation().equals(((AssociationRoleIF) source).getAssociation())) {
      MergeUtils.mergeInto(((AssociationRoleIF) target).getAssociation(), ((AssociationRoleIF) source).getAssociation()); 
    }
    else {
      MergeUtils.mergeInto(target, source);
    }
  }

  /**
   * INTENRAL: Helper class to keep track of the role's reifier and item 
   * identifiers.
   */
  private static final class DelayedRoleEvents {
    final AssociationRoleIF role;
    final TopicIF reifier;
    final Collection<String> iids;
    
    DelayedRoleEvents(AssociationRoleIF role, TopicIF reifier, Collection<String> iids) {
      this.role = role;
      this.reifier = reifier;
      this.iids = iids;
    }
  }

}
