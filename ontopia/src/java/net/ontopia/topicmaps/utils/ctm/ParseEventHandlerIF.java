
// $Id: ParseEventHandlerIF.java,v 1.3 2009/02/27 12:02:15 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import java.util.List;
import java.net.MalformedURLException;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Interface implemented by event handlers which build the
 * actual topic map, based on events received by the parser. In
 * template definitions the events are stored to be replayed when the
 * template is invoked. The state of generator objects may
 * <em>change</em> after the event method has returned; to store
 * generator objects between invocations it is necessary to make
 * copies.
 */
public interface ParseEventHandlerIF {

  // FIXME: add global start/end events, but do it later

  public void startTopicItemIdentifier(LiteralGeneratorIF locator);
  public void startTopicSubjectIdentifier(LiteralGeneratorIF locator);
  public void startTopicSubjectLocator(LiteralGeneratorIF locator);
  /**
   * The generator makes (or just returns) the topic when asked to.
   */
  public void startTopic(TopicGeneratorIF topic);
  public void addItemIdentifier(LiteralGeneratorIF locator);
  public void addSubjectIdentifier(LiteralGeneratorIF locator);
  public void addSubjectLocator(LiteralGeneratorIF locator);
  public void addTopicType(TopicGeneratorIF topic);
  public void addSubtype(TopicGeneratorIF subtype);
  public void startName(TopicGeneratorIF type, LiteralGeneratorIF value);
  public void addScopingTopic(TopicGeneratorIF topic);
  public void addReifier(TopicGeneratorIF topic);
  public void startVariant(LiteralGeneratorIF value);
  public void endName();
  public void startOccurrence(TopicGeneratorIF type, LiteralGeneratorIF value);
  public void endOccurrence();
  public void endTopic();

  public void startAssociation(TopicGeneratorIF type);
  public void addRole(TopicGeneratorIF type, TopicGeneratorIF player);
  public void endRoles();
  public void endAssociation();

  public void startEmbeddedTopic();
  /**
   * Returns a generator which returns the embedded topic produced by
   * the event.
   */
  public TopicGeneratorIF endEmbeddedTopic();

  public void templateInvocation(String name, List arguments);
}