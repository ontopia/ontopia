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
 * INTERNAL: A singleton null object for use where null cannot be used,
 * and an object is required.
 */

@Deprecated
public final class NullObject {

  public static final NullObject INSTANCE = new NullObject();

  private NullObject() {
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof NullObject);                                  
  }

  @Override
  public int hashCode() {
    return 12345678;
  }
  
}




