package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractTopic;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;

/**
 * INTERNAL: Represents a topic literal.
 */
public class TopicPath extends AbstractTopic implements BasicPathElementIF {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.NONE);
  }

  public TopicPath(AbstractTopic.IDTYPE type, String id) {
    super(type, id);
  }
  
  public String[] getColumnNames() {
    return new String[] { "TOPIC" };
  }

  public int getResultSize() {
    return 1;
  }

  @Override
  protected boolean isChildAllowed() {
    return false;
  }

  @Override
  protected boolean isLevelAllowed() {
    return false;
  }

  @Override
  protected boolean isScopeAllowed() {
    return false;
  }

  @Override
  protected boolean isTypeAllowed() {
    return false;
  }

  public TYPE output() {
    return PathElementIF.TYPE.TOPIC;
  }

  public Set<TYPE> validInput() {
    return inputSet;
  }

  /**
   * Evaluate a topic literal and return the real topics that are identified by
   * the literal.
   * 
   * @return a Collection of {@link TopicIF} objects.
   */
  public Collection<TopicIF> evaluate(LocalContext context, Object input) {
    TopicMapIF topicmap = context.getTopicMap();

    switch (getIDType()) {

    case IID:
      return getTopicsByIID(topicmap);

    case SI:
      return getTopicsBySI(topicmap);

    case SL:
      return getTopicsBySL(topicmap);

    case NAME:
      return getTopicsByName(topicmap);

    case VAR:
      return getTopicsByVar(topicmap);
    }

    // should not be reached anyways
    return new LinkedList<TopicIF>();
  }

  private Collection<TopicIF> getTopicsByIID(TopicMapIF topicmap) {
    LocatorIF locator = topicmap.getStore().getBaseAddress().resolveAbsolute(
        "#" + getIdentifier());
    TMObjectIF obj = topicmap.getObjectByItemIdentifier(locator);
    Collection<TopicIF> coll = new LinkedList<TopicIF>();
    if (obj != null && obj instanceof TopicIF) {
      coll.add((TopicIF) obj);
    }
    return coll;
  }

  private Collection<TopicIF> getTopicsBySI(TopicMapIF topicmap) {
    LocatorIF locator = URILocator.create(getIdentifier());
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(locator);
    Collection<TopicIF> coll = new LinkedList<TopicIF>();
    if (topic != null) {
      coll.add(topic);
    }
    return coll;
  }

  private Collection<TopicIF> getTopicsBySL(TopicMapIF topicmap) {
    LocatorIF locator = URILocator.create(getIdentifier());
    TopicIF topic = topicmap.getTopicBySubjectLocator(locator);
    Collection<TopicIF> coll = new LinkedList<TopicIF>();
    if (topic != null) {
      coll.add(topic);
    }
    return coll;
  }

  @SuppressWarnings("unchecked")
  private Collection<TopicIF> getTopicsByName(TopicMapIF topicmap) {
    NameIndexIF index = (NameIndexIF) topicmap
        .getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");

    Collection<TopicNameIF> coll = index.getTopicNames(getIdentifier());
    Collection<TopicIF> topics = new LinkedList<TopicIF>();
    for (TopicNameIF name : coll) {
      topics.add(name.getTopic());
    }
    return topics;
  }

  @SuppressWarnings("unchecked")
  private Collection<TopicIF> getTopicsByVar(TopicMapIF topicmap) {
    NameIndexIF index = (NameIndexIF) topicmap
        .getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");

    Collection<VariantNameIF> coll = index.getVariants(getIdentifier());
    Collection<TopicIF> topics = new LinkedList<TopicIF>();
    for (VariantNameIF name : coll) {
      topics.add(name.getTopic());
    }
    return topics;
  }
}
