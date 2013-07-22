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

package net.ontopia.topicmaps.core;

import java.util.Collection;

/**
 * PUBLIC: Implemented by an object which represents a topic name, which
 * is a topic characteristic.</p>
 * 
 * This interface is also used for 'variant' elements in the XTM 1.0
 * syntax, when extended by the VariantNameIF interface.</p>
 */

public interface TopicNameIF extends NameIF, ScopedIF, TypedIF, ReifiableIF {

  public static final String EVENT_ADDED = "TopicNameIF.added";
  public static final String EVENT_REMOVED = "TopicNameIF.removed";
  public static final String EVENT_SET_TYPE = "TopicNameIF.setType";
  public static final String EVENT_SET_VALUE = "TopicNameIF.setValue";
  public static final String EVENT_ADD_VARIANT = "TopicNameIF.addVariant";
  public static final String EVENT_REMOVE_VARIANT = "TopicNameIF.removeVariant";
  public static final String EVENT_ADD_THEME = "TopicNameIF.addTheme";
  public static final String EVENT_REMOVE_THEME = "TopicNameIF.removeTheme";

  /**
   * PUBLIC: Gets the variant names of the topic named by this
   * topic name. These correspond to the 'variant' child elements of the
   * 'baseName' element in XTM 1.0. There is no guarantee as to which
   * order these appear in the collection.
   *
   * @return A collection of VariantNameIF objects.
   */
  public Collection<VariantNameIF> getVariants();

}
