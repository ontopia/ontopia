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
 * INTERNAL: Decider that stringifies an object and passes it to the
 * subdecider.
 */

public class StringifierDecider<T> implements DeciderIF<T> {

  protected StringifierIF<T> stringifier;
  protected DeciderIF<String> decider;
   
  public StringifierDecider(StringifierIF<T> stringifier, DeciderIF<String> decider) {
    this.stringifier = stringifier;
    this.decider = decider;
  }

  public boolean ok(T object) {
    return decider.ok(stringifier.toString(object));
  }
  
}




