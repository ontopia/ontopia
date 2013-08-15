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

package net.ontopia.topicmaps.utils.ctm;

import java.util.List;

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
