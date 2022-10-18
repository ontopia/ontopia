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

package net.ontopia.topicmaps.utils;

import java.util.function.Function;
import net.ontopia.topicmaps.core.NameIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: A convenience class for creating grabbers that grab
 * specific topic characteristics, using various criteria, including
 * scope.
 * @since 1.1
 */

public class TopicCharacteristicGrabbers {

  private TopicCharacteristicGrabbers() {
    // don't call me
  }

  /**
   * INTERNAL: Returns a grabber that will grab the name most suitable
   * for display from a topic.  If the topic has a display name that name
   * will be chosen. If not, the base name in the least constrained
   * scope will be chosen.
   * @return A TopicNameIF or VariantNameIF object; null if the topic
   *         has no base names.
   */
  public static Function<TopicIF, NameIF> getDisplayNameGrabber() {
    // Note: Grabs variant name if it exists.
    NameGrabber grabber = new NameGrabber(PSI.getXTMDisplay(), true);
    grabber.setGrabOnlyTopicName(false);
    return grabber;
  }

  /**
   * INTERNAL: Returns a grabber that will grab the name most suitable
   * for sorting from a topic.  If the topic has a sort name that name
   * will be chosen. If not, the base name in the least constrained
   * scope will be chosen.
   * @return A TopicNameIF or VariantNameIF object; null if the topic
   *         has no base names.
   */
  public static Function<TopicIF, NameIF> getSortNameGrabber() {
    // Note: Grabs variant name if it exists.
    NameGrabber grabber = new NameGrabber(PSI.getXTMSort(), true);
    grabber.setGrabOnlyTopicName(false);
    return grabber;
  }
}
