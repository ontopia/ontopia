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

package net.ontopia.topicmaps.query.impl.utils;

/**
 * INTERNAL: Used as a special, "magic", argument to predicates,
 * inserted by the query optimizer to tell them to behave differently.
 * The meaning of this argument differs depending on the predicate in
 * question.
 */
public class PredicateOptions {
  private String value;  // the meaning of this depends on the user
  private Object column; // the meaning of this depends on the user
  
  public PredicateOptions(String value) {
    this.value = value;
  }
  
  public PredicateOptions(Object column) {
    this.column = column;
  }

  public String getValue() {
    return value;
  }

  public Object getColumn() {
    return column;
  }
  
  public String toString() {
    return "<<<PredicateOptions>>>";
  }
  
}
