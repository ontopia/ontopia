/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.utils;

import java.util.function.Function;
import net.ontopia.topicmaps.core.OccurrenceIF;

/**
 * INTERNAL: Stringifier that stringifies occurrences to their internal
 * string value and all other objects using obj.toString(). Contents
 * are output without being HTML-escaped.
 *
 * @since 2.0
 */
public class NoEscapeStringifier implements Function<Object, String> {
  
  @Override
  public String apply(Object object) {
    if (object instanceof OccurrenceIF) 
      return ((OccurrenceIF) object).getValue();
    else 
      return (object == null ? null : object.toString());
  }
  
}
