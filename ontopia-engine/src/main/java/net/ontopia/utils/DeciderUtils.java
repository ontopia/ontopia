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

/**
 * INTERNAL: Utility methods for creating various kinds of useful
 * deciders.
 * @since 2.0
 */
public class DeciderUtils {

  /**
   * INTERNAL: Returns a decider which always returns true.
   */
  public static <T> DeciderIF<T> getTrueDecider() {
    return new StaticDecider<T>(true);
  }

  /**
   * INTERNAL: Returns a decider which always returns false.
   */
  public static <T> DeciderIF<T> getFalseDecider() {
    return new StaticDecider<T>(false);
  }
  
  // --- The actual decider classes

  static class StaticDecider<T> implements DeciderIF<T> {
    private boolean value;

    public StaticDecider(boolean value) {
      this.value = value;
    }
    
    @Override
    public boolean ok(T object) {
      return value;
    }
  }
  
}
