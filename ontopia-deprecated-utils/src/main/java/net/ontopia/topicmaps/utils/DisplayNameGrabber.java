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
import net.ontopia.utils.GrabberIF;

/**
 * PUBLIC: Grabber that grabs the name most suitable for display from
 * a topic.  If the topic has a display name that will be chosen. If
 * not, the base name in the least constrained scope will be chosen.
 * @deprecated Since 1.1. Use TopicCharacteristicGrabbers instead.
 */

@Deprecated
public class DisplayNameGrabber implements GrabberIF<TopicIF, NameIF> {
  /**
   * PROTECTED: The NameGrabber used to implement the grabbing.
   */
  protected Function<? super TopicIF, NameIF> subGrabber;
 
  /**
   * PUBLIC: Creates the grabber and sets the comparator to be a 
   * ScopedIFComparator using the least constrained scope.
   */ 
  public DisplayNameGrabber() {
    subGrabber = new NameGrabber(PSI.getXTMDisplay());
  }
  
  /**
   * PUBLIC: Grabs the name for display. The name returned is the
   * first display (variant) name found, when the basenames of the
   * give topic have been sorted using the comparator. If there is no
   * display name, then the last base name found is returned,
   * corresponding to the least constrained scope.
   *
   * @param object The topic whose name is being grabbed; formally an object.
   * @return A name to display; an object implementing TopicNameIF or
   * VariantNameIF, null if the topic has no basenames.
   * @exception Throws OntopiaRuntimeException if object is not a topic.
   */
  @Override
  public NameIF grab(TopicIF object) {
    return subGrabber.apply(object);
  }

}





