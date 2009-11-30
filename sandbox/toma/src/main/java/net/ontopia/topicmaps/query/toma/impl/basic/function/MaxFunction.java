/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Identifies the maximum of a collection.
 * <p>
 * <b>Note</b>: This function is only defined for numbers. In case the 
 * encapsulated expression returns something else, the result will be 0.  
 * </p>
 */
public class MaxFunction extends AbstractAggregateFunction {
  
  public MaxFunction() {
    super("MAX", 0);
  }

  public Object aggregate(Collection<?> values) throws InvalidQueryException {
    double max = Double.MIN_VALUE;
    for (Object val : values) {
      try {
        if (val != null) {
          max = Math.max(max, Double.parseDouble(ToNumFunction
              .convertToNumber(val)));
        }
      } catch (NumberFormatException e) {
        throw new InvalidQueryException("A value as input to the '" + getName()
            + "' function could not be converted to a number.");
      }
    }
    
    // if no valid value could be found, return 0
    if (max == Double.MIN_VALUE) {
      max = 0.0d;
    }
    
    return new Double(max);
  }
}
