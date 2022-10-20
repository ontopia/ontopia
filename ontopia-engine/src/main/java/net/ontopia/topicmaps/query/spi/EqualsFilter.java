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

package net.ontopia.topicmaps.query.spi;

import java.util.Objects;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * EXPERIMENTAL: Sample filter implementation that returns true if all
 * arguments are equal.<p>
 *
 * @since 4.0
 */

public class EqualsFilter extends FilterPredicate {

  @Override
  public boolean filter(Object[] objects) throws InvalidQueryException {
    // return true if  all objects are equal.
    if (objects.length > 1) {
      for (int i=1; i < objects.length; i++) {
        if (!Objects.equals(objects[i-1], objects[i])) {
          return false;
        }
      }      
    }
    return true;
  }

}
