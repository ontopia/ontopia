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
 * INTERNAL: Decider whether the reference object is equal the given
 * object. The implementation uses the Object.equals method.</p>
 */

@Deprecated
public class EqualsDecider<T> implements DeciderIF<T> {

  protected T refobj;
  
  public EqualsDecider(T refobj) {
    this.refobj = refobj;
  }
  
  @Override
  public boolean ok(T object) {
    return refobj.equals(object);
  }

}




