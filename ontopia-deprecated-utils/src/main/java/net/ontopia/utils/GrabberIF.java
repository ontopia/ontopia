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

package net.ontopia.utils;

import java.util.function.Function;

/**
 * INTERNAL: Grabs an object from another object.</p>
 * 
 * The object that is grabbed decided by the implementation of this
 * interface.</p>
 * 
 * @deprecated use {@link Function}
 */

@Deprecated
@FunctionalInterface
public interface GrabberIF<O, G> extends Function<O, G> {

  /**
   * Returns an object that is somehow extracted from the given
   * object.
   */
  G grab(O object);

  @Override
  default G apply(O object) {
    return grab(object);
  }
}




