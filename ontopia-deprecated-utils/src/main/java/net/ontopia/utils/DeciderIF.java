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

import java.util.function.Predicate;

/**
 * PUBLIC: Interface for classes that decides whether an object is
 * acceptable or not. A decider is the same as a predicate, and can
 * e.g. be used to filter collections.</p>
 * 
 * @deprecated Use {@link Predicate}
 */
@Deprecated
@FunctionalInterface
public interface DeciderIF<T> extends Predicate<T> {

  /**
   * PUBLIC: Returns true if the object is accepted.
   */
  boolean ok(T object);

  @Override
  default boolean test(T object) {
    return ok(object);
  }
}




