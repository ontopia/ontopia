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
import java.util.Iterator;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.tmapi2.index.LiteralIndexImpl;
import net.ontopia.topicmaps.impl.tmapi2.index.NameIndex;
import net.ontopia.topicmaps.impl.tmapi2.index.ScopedIndexImpl;
import net.ontopia.topicmaps.impl.tmapi2.index.TypeInstanceIndexImpl;
import net.ontopia.topicmaps.utils.MergeUtils;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class TopicMapImpl extends ReifiableImpl implements TopicMap {

  private TopicMapSystemIF tmsystem;
  private TopicMapIF wrapped;
  private TopicImpl defaultNameType = null;

  // TMAPI indexes - lazy created when needed
  private ScopedIndex scopedIndex = null;
  private TypeInstanceIndex typeInstanceIndex = null;
  private LiteralIndex literalIndex = null;

  // the name index is for internal use and is definitely needed so we
  // instantiate it right now
  private NameIndex nameIndex = new NameIndex(this);

  public TopicMapImpl(TopicMapSystemIF tmsystem, TopicMapStoreIF store) {
    super(null);
    this.tmsystem = tmsystem;
    wrapped = store.getTopicMap();
    topicMap = this;
  }

  protected TopicImpl getDefaultNameType() {
    if (defaultNameType == null) {
      String psi = net.ontopia.topicmaps.utils.PSI.SAM_NAMETYPE;
      defaultNameType = createTopicBySubjectIdentifier(createLocator(psi));
    }
    return defaultNameType;
  }

  @Override
  public Locator getLocator() {
    return wrapLocator(wrapped.getStore().getBaseAddress());
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */

  @Override
  public TopicMapIF getWrapped() {
    return wrapped;
  }

  public TopicNameIF unwrapName(Name name) {
    return ((NameImpl) name).getWrapped();
  }

  public VariantNameIF unwrapVariant(Variant variant) {
    return ((VariantImpl) variant).getWrapped();
  }

  public LocatorIF unwrapLocator(Locator loc) {
    // other lcoators implementation should be supported to
    if (loc instanceof LocatorImpl) {
      return ((LocatorImpl) loc).getWrapped();
    } else {
      return URILocator.create(loc.toExternalForm());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#close()
   */
  @Override
  public void close() {
    wrapped.getStore().commit();
    wrapped.getStore().close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#createAssociation(org.tmapi.core.Topic,
   * org.tmapi.core.Topic[])
   */
  @Override
  public Association createAssociation(Topic type, Topic... scope) {
    Check.typeNotNull(this, type);
    Check.scopeNotNull(this, scope);
    Check.scopeInTopicMap(this, scope);
    Check.typeInTopicMap(this, type);
    AssociationIF assoc = wrapped.getBuilder().makeAssociation(
        unwrapTopic(type));
    for (Topic theme : scope) {
      assoc.addTheme(unwrapTopic(theme));
    }
    return wrapAssociation(assoc);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#createAssociation(org.tmapi.core.Topic,
   * java.util.Collection)
   */

  @Override
  public Association createAssociation(Topic type, Collection<Topic> scope) {
    Check.scopeNotNull(this, scope);
    return createAssociation(type, scope.toArray(new Topic[0]));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#createLocator(java.lang.String)
   */

  @Override
  public Locator createLocator(String reference) {
    return tmsystem.createLocator(reference);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#createTopic()
   */

  @Override
  public TopicImpl createTopic() {
    TopicImpl topic = wrapTopic(wrapped.getBuilder().makeTopic());
    Locator itemIdentifier = createLocator("urn:x-ontopia" + System.currentTimeMillis()); // using time to hopefully get an unique locator
    
    // check if we have a construct with the generated item identifier
    while (getConstructByItemIdentifier(itemIdentifier)!=null) {
      itemIdentifier = createLocator("urn:x-ontopia" + System.currentTimeMillis()); // using time to hopefully get an unique locator
    }
    topic.addItemIdentifier(itemIdentifier);
    return topic;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.tmapi.core.TopicMap#createTopicByItemIdentifier(org.tmapi.core.Locator)
   */

  @Override
  public TopicImpl createTopicByItemIdentifier(Locator iid) {
    Check.itemIdentifierNotNull(this, iid);
    ConstructImpl tmc = getConstructByItemIdentifier(iid);
    TopicImpl topic = null;
    if (tmc != null && tmc instanceof TopicImpl) {
      topic = (TopicImpl) tmc;
    }
    if (topic != null) {
      return topic;
    }
    topic = getTopicBySubjectIdentifier(iid);
    if (topic == null) {
      topic = wrapTopic(wrapped.getBuilder().makeTopic());
    }
    topic.addItemIdentifier(iid);
    return topic;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.tmapi.core.TopicMap#createTopicBySubjectIdentifier(org.tmapi.core.Locator
   * )
   */

  @Override
  public TopicImpl createTopicBySubjectIdentifier(Locator sid) {
    Check.itemIdentifierNotNull(this, sid);
    TopicImpl topic = getTopicBySubjectIdentifier(sid);
    if (topic != null) {
      return topic;
    }
    ConstructImpl tmc = getConstructByItemIdentifier(sid);
    if (tmc != null && tmc instanceof TopicImpl) {
      topic = (TopicImpl) tmc;
    }
    if (topic == null) {
      topic = wrapTopic(wrapped.getBuilder().makeTopic());
    }
    topic.addSubjectIdentifier(sid);
    return topic;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.tmapi.core.TopicMap#createTopicBySubjectLocator(org.tmapi.core.Locator)
   */

  @Override
  public TopicImpl createTopicBySubjectLocator(Locator slo) {
    Check.itemIdentifierNotNull(this, slo);
    TopicImpl topic = getTopicBySubjectLocator(slo);
    if (topic == null) {
      topic = wrapTopic(wrapped.getBuilder().makeTopic());
      topic.addSubjectLocator(slo);
    }
    return topic;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#getAssociations()
   */

  @Override
  public Set<Association> getAssociations() {
    return wrapSet(wrapped.getAssociations());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#getConstructById(java.lang.String)
   */

  @Override
  public ConstructImpl getConstructById(String id) {
    if (id == null) {
      throw new IllegalArgumentException("The id must not be null");
    }
    return wrapTMObject(wrapped.getObjectById(id));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.tmapi.core.TopicMap#getConstructByItemIdentifier(org.tmapi.core.Locator
   * )
   */

  @Override
  public ConstructImpl getConstructByItemIdentifier(Locator iid) {
    if (iid == null) {
      throw new IllegalArgumentException("The item identifier must not be null");
    }
    return wrapTMObject(wrapped.getObjectByItemIdentifier(unwrapLocator(iid)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#getIndex(java.lang.Class)
   */

  @SuppressWarnings("unchecked")
  @Override
  public <I extends Index> I getIndex(Class<I> idx) {
    if (idx == org.tmapi.index.ScopedIndex.class) {
      if (scopedIndex == null) {
        scopedIndex = new ScopedIndexImpl(this);
      }

      return (I) scopedIndex;
    }

    if (idx == org.tmapi.index.TypeInstanceIndex.class) {
      if (typeInstanceIndex == null) {
        typeInstanceIndex = new TypeInstanceIndexImpl(this);
      }

      return (I) typeInstanceIndex;
    }

    if (idx == org.tmapi.index.LiteralIndex.class) {
      if (literalIndex == null) {
        literalIndex = new LiteralIndexImpl(this);
      }

      return (I) literalIndex;
    }
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#getParent()
   */

  @Override
  public ConstructImpl getParent() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.tmapi.core.TopicMap#getTopicBySubjectIdentifier(org.tmapi.core.Locator)
   */

  @Override
  public TopicImpl getTopicBySubjectIdentifier(Locator sid) {
    Check.subjectIdentifierNotNull(sid);
    return wrapTopic(wrapped.getTopicBySubjectIdentifier(unwrapLocator(sid)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.tmapi.core.TopicMap#getTopicBySubjectLocator(org.tmapi.core.Locator)
   */

  @Override
  public TopicImpl getTopicBySubjectLocator(Locator slo) {
    Check.subjectLocatorNotNull(slo);
    return wrapTopic(wrapped.getTopicBySubjectLocator(unwrapLocator(slo)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#getTopics()
   */

  @Override
  public Set<Topic> getTopics() {
    return wrapSet(wrapped.getTopics());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMap#mergeIn(org.tmapi.core.TopicMap)
   */

  @Override
  public void mergeIn(TopicMap tm) {
    if (tm == null) {
      throw new IllegalArgumentException("The topic map must not be null");
    }
    if (this.equals(tm)) {
      return;
    }
    MergeUtils.mergeInto(wrapped, ((TopicMapImpl) tm).getWrapped());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Construct#remove()
   */

  @Override
  public void remove() {
    wrapped.getStore().delete(true);
    tmsystem.remove(wrapped.getStore().getBaseAddress());
  }

  /* ---- wrappers: topic */

  public TopicImpl wrapTopic(TopicIF topic) {
    return topic == null ? null : new TopicImpl(this, topic);
  }

  public TopicIF unwrapTopic(Topic topic) {
    return topic == null ? null : ((TopicImpl) topic).getWrapped();
  }

  /* ---- wrappers: association */

  public AssociationImpl wrapAssociation(AssociationIF association) {
    return association == null ? null : new AssociationImpl(this, association);
  }

  /* ---- wrappers: role */

  public RoleImpl wrapRole(AssociationRoleIF role) {
    return role == null ? null : new RoleImpl(this, role);
  }

  /* ---- wrappers: topic map object */

  public ConstructImpl wrapTMObject(TMObjectIF tmobject) {
    if (tmobject == null) {
      return null;
    } else if (tmobject instanceof AssociationIF) {
      return new AssociationImpl(this, (AssociationIF) tmobject);
    } else if (tmobject instanceof AssociationRoleIF) {
      return new RoleImpl(this, (AssociationRoleIF) tmobject);
    } else if (tmobject instanceof TopicNameIF) {
      return wrapName((TopicNameIF) tmobject);
    } else if (tmobject instanceof OccurrenceIF) {
      return new OccurrenceImpl(this, (OccurrenceIF) tmobject);
    } else if (tmobject instanceof TopicIF) {
      return new TopicImpl(this, (TopicIF) tmobject);
    } else if (tmobject instanceof TopicMapIF) {
      return this;
    } else if (tmobject instanceof VariantNameIF) {
      return wrapVariant((VariantNameIF) tmobject);
    } else {
      throw new TMAPIRuntimeException("Invalid topic map object type: "
          + tmobject);
    }
  }

  /* ---- wrappers: set */

  public <T> Set<T> wrapSet(Collection<?> coll) {
    return new LazySet<T>(this, coll);
  }

  public Occurrence wrapOccurrence(OccurrenceIF occ) {
    return new OccurrenceImpl(this, occ);
  }

  public NameImpl wrapName(TopicNameIF name) {
    if (name == null) {
      return null;
    }

    NameImpl wrapper = nameIndex.getName(name);
    if (wrapper == null) {
      wrapper = new NameImpl(this, name);
      nameIndex.addName((NameImpl) wrapper);
    }
    
    // need to update variant cache of wrapper
    if (wrapper.getVariants().size() != name.getVariants().size()) {
      wrapper.clearVariants();
      Iterator<?> it = name.getVariants().iterator();
      while (it.hasNext()) {
        VariantNameIF v = (VariantNameIF) it.next();
        wrapVariant(v);
      }
    }
    
    return wrapper;
  }

  public Locator wrapLocator(LocatorIF loc) {
    return tmsystem.wrapLocator(loc);
  }

  public VariantImpl wrapVariant(VariantNameIF variant) {
    if (variant == null) {
      return null;
    }

    NameImpl name = nameIndex.getName(variant.getTopicName());
    // if we don't have a wrapped name, we create one
    if (name==null) {
      name = wrapName(variant.getTopicName());
    }
    VariantImpl v = null;

    for (Variant tmp : name.getVariants()) {
      if (((VariantImpl)tmp).getWrapped().equals(variant)) {
        v = (VariantImpl) tmp;
        break;
      }
    }
    if (v == null) {
      v = new VariantImpl(this, name, variant);
      name.addVariant(v); 
    }

    return v;
  }

}
