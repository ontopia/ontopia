
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

  public void startTopicItemIdentifier(ValueGeneratorIF locator);
  public void startTopicSubjectIdentifier(ValueGeneratorIF locator);
  public void startTopicSubjectLocator(ValueGeneratorIF locator);
  /**
   * The generator makes (or just returns) the topic when asked to.
   */
  public void startTopic(ValueGeneratorIF topic);
  public void addItemIdentifier(ValueGeneratorIF locator);
  public void addSubjectIdentifier(ValueGeneratorIF locator);
  public void addSubjectLocator(ValueGeneratorIF locator);
  public void addTopicType(ValueGeneratorIF topic);
  public void addSubtype(ValueGeneratorIF subtype);
  public void startName(ValueGeneratorIF type, ValueGeneratorIF value);
  public void addScopingTopic(ValueGeneratorIF topic);
  public void addReifier(ValueGeneratorIF topic);
  public void startVariant(ValueGeneratorIF value);
  public void endName();
  public void startOccurrence(ValueGeneratorIF type, ValueGeneratorIF value);
  public void endOccurrence();
  public void endTopic();

  public void startAssociation(ValueGeneratorIF type);
  public void addRole(ValueGeneratorIF type, ValueGeneratorIF player);
  public void endRoles();
  public void endAssociation();

  public void startEmbeddedTopic();
  /**
   * Returns a generator which returns the embedded topic produced by
   * the event.
   */
  public ValueGeneratorIF endEmbeddedTopic();

  public void templateInvocation(String name, List arguments);
}
