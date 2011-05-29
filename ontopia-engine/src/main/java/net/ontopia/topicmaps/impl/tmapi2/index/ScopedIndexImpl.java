
package net.ontopia.topicmaps.impl.tmapi2.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.impl.tmapi2.Check;
import net.ontopia.topicmaps.impl.tmapi2.LazySet;
import net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;

/**
 * This is the implementation for the TMAPI2 {@link ScopedIndex} interface.
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class ScopedIndexImpl implements org.tmapi.index.ScopedIndex {

  private final ScopeIndexIF wrapped;

  private final TopicMapImpl topicMap;

  public ScopedIndexImpl(TopicMapImpl topicMap) {
    this.topicMap = topicMap;
    this.wrapped = (ScopeIndexIF) topicMap.getWrapped()
        .getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");
  }

  public Collection<Topic> getAssociationThemes() {
    return new LazySet<Topic>(topicMap, getWrapped().getAssociationThemes());
  }

  public Collection<Association> getAssociations(Topic theme) {
    TopicIF oTopic = null;
    if (theme != null)
      oTopic = topicMap.unwrapTopic(theme);

    return new LazySet<Association>(topicMap, wrapped.getAssociations(oTopic));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getAssociations(org.tmapi.core.Topic[],
   * boolean)
   */
  @SuppressWarnings("unchecked")
  public Collection<Association> getAssociations(Topic[] themes,
      boolean matchAll) {
    Check.themeNotNull(themes);

    Set<AssociationIF> resultSet = new HashSet<AssociationIF>();
    boolean first = true;
    for (Topic theme : themes) {
      TopicIF oTopic = topicMap.unwrapTopic(theme);
      Collection<AssociationIF> tmp = wrapped.getAssociations(oTopic);
      if ((first) || (!matchAll)) {
        resultSet.addAll(tmp);
        first = false;
      } else {
        resultSet.retainAll(tmp);
      }
    }

    if (resultSet.isEmpty())
      return Collections.emptySet();

    return new LazySet<Association>(topicMap, resultSet);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getNameThemes()
   */
  public Collection<Topic> getNameThemes() {
    return new LazySet<Topic>(topicMap, getWrapped().getTopicNameThemes());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getNames(org.tmapi.core.Topic)
   */
  public Collection<Name> getNames(Topic theme) {
    TopicIF oTopic = null;
    if (theme != null)
      oTopic = topicMap.unwrapTopic(theme);

    return new LazySet<Name>(topicMap, wrapped.getTopicNames(oTopic));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getNames(org.tmapi.core.Topic[], boolean)
   */
  @SuppressWarnings("unchecked")
  public Collection<Name> getNames(Topic[] themes, boolean matchAll) {
    Check.themeNotNull(themes);
    Set<TopicNameIF> resultSet = new HashSet<TopicNameIF>();
    boolean first = true;
    for (Topic theme : themes) {
      TopicIF oTopic = topicMap.unwrapTopic(theme);
      Collection<TopicNameIF> tmp = wrapped.getTopicNames(oTopic);
      if ( (first) || (!matchAll)) {
        resultSet.addAll(tmp);
        first = false;
      } else {
        resultSet.retainAll(tmp);
      }
    }

    if (resultSet.isEmpty())
      return Collections.emptySet();

    return new LazySet<Name>(topicMap, resultSet);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getOccurrenceThemes()
   */
  public Collection<Topic> getOccurrenceThemes() {
    return new LazySet<Topic>(topicMap, getWrapped().getOccurrenceThemes());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getOccurrences(org.tmapi.core.Topic)
   */
  public Collection<Occurrence> getOccurrences(Topic theme) {
    TopicIF oTopic = null;
    if (theme != null)
      oTopic = topicMap.unwrapTopic(theme);

    return new LazySet<Occurrence>(topicMap, wrapped.getOccurrences(oTopic));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getOccurrences(org.tmapi.core.Topic[],
   * boolean)
   */
  @SuppressWarnings("unchecked")
  public Collection<Occurrence> getOccurrences(Topic[] themes, boolean matchAll) {
    Check.themeNotNull(themes);
    Set<OccurrenceIF> resultSet = new HashSet<OccurrenceIF>();
    boolean first = true;
    for (Topic theme : themes) {
      TopicIF oTopic = topicMap.unwrapTopic(theme);
      Collection<OccurrenceIF> tmp = wrapped.getOccurrences(oTopic);
      if ((first) || (!matchAll)) {
        first = false;
        resultSet.addAll(tmp);
      } else {
        resultSet.retainAll(tmp);
      }
    }

    if (resultSet.isEmpty())
      return Collections.emptySet();

    return new LazySet<Occurrence>(topicMap, resultSet);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getVariantThemes()
   */
  public Collection<Topic> getVariantThemes() {
    return new LazySet<Topic>(topicMap, getWrapped().getVariantThemes());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getVariants(org.tmapi.core.Topic)
   */
  public Collection<Variant> getVariants(Topic theme) {
    if (theme == null)
      throw new IllegalArgumentException("Theme for variants may not be null!!");

    return new LazySet<Variant>(topicMap, wrapped.getVariants(topicMap
        .unwrapTopic(theme)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.ScopedIndex#getVariants(org.tmapi.core.Topic[],
   * boolean)
   */
  @SuppressWarnings("unchecked")
  public Collection<Variant> getVariants(Topic[] themes, boolean matchAll) {
    Check.themeNotNull(themes);

    Set<VariantNameIF> resultSet = new HashSet<VariantNameIF>();
    boolean first = true;
    for (Topic theme : themes) {
      TopicIF oTopic = topicMap.unwrapTopic(theme);
      Collection<VariantNameIF> tmp = wrapped.getVariants(oTopic);
      if ((first) || (!matchAll)) {
        resultSet.addAll(tmp);
        first = false;
      } else {
        resultSet.retainAll(tmp);
      }
    }

    if (resultSet.isEmpty())
      return Collections.emptySet();

    return new LazySet<Variant>(topicMap, resultSet);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#close()
   */
  public void close() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#isAutoUpdated()
   */
  public boolean isAutoUpdated() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#isOpen()
   */
  public boolean isOpen() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#open()
   */
  public void open() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#reindex()
   */
  public void reindex() {
  }

  public ScopeIndexIF getWrapped() {
    return wrapped;
  }
}
