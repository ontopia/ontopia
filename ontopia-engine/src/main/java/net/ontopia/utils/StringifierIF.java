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
 * PUBLIC: Stringifies objects. The stringifier interface consists of
 * one method called toString which takes a single Object
 * argument. The return value is a string that is the string
 * representation of that object.</p>
 */

@Deprecated
@FunctionalInterface
public interface StringifierIF<T> extends Function<T, String> {

  /**
   * Returns a stringified version of the object, i.e. a string
   * representation of that object.
   *
   * @param object the object that is to be made a string
   * representation of.
   * @return a string representation of the <code>object</code>
   * argument.
   */
  String toString(T object);

  @Override
  public default String apply(T t) {
    return toString(t);
  }
}




