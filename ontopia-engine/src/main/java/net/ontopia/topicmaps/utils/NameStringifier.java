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

/**
 * INTERNAL: Stringifier that stringifies TopicNameIFs and VariantNameIFs
 * by calling their getValue() method.
 */

public class NameStringifier implements Function<NameIF, String> {
  
  /**
   * INTERNAL: Stringifies the given name.
   * @param name object, cast to TopicNameIF or VariantNameIF
   * internally; the given name
   * @return string containing name value or "[No name]"
   */
  @Override
  public String apply(NameIF name) {
    if (name == null)
      return "[No name]";
    return name.getValue();
  }
  
}
