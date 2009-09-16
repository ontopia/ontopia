package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Collection;
import java.util.LinkedList;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractTopic;

/**
 * INTERNAL:
 */
public class Topic extends AbstractTopic implements BasicRootIF {

  public Topic(AbstractTopic.TYPE type, String id) {
    super(type, id);
  }

  public Collection<?> evaluate(LocalContext context) {
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

    return new LinkedList();
  }

  private Collection<?> getTopicsByIID(TopicMapIF topicmap) {
    LocatorIF locator = topicmap.getStore().getBaseAddress().resolveAbsolute(
        "#" + getIdentifier());
    TMObjectIF obj = topicmap.getObjectByItemIdentifier(locator);
    Collection<TopicIF> coll = new LinkedList<TopicIF>();
    if (obj instanceof TopicIF) {
      coll.add((TopicIF) obj);
    }
    return coll;
  }

  private Collection<?> getTopicsBySI(TopicMapIF topicmap) {
    LocatorIF locator = URILocator.create(getIdentifier());
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(locator);
    Collection<TopicIF> coll = new LinkedList<TopicIF>();
    coll.add(topic);
    return coll;
  }

  private Collection<?> getTopicsBySL(TopicMapIF topicmap) {
    LocatorIF locator = URILocator.create(getIdentifier());
    TopicIF topic = topicmap.getTopicBySubjectLocator(locator);
    Collection<TopicIF> coll = new LinkedList<TopicIF>();
    coll.add(topic);
    return coll;
  }

  private Collection<?> getTopicsByName(TopicMapIF topicmap) {
    NameIndexIF index = (NameIndexIF) topicmap
        .getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");

    Collection<TopicNameIF> coll = index.getTopicNames(getIdentifier());
    Collection<TopicIF> topics = new LinkedList<TopicIF>();
    for (TopicNameIF name : coll) {
      topics.add(name.getTopic());
    }
    return topics;
  }

  private Collection<?> getTopicsByVar(TopicMapIF topicmap) {
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
