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

package net.ontopia.infoset.fulltext.core;

import java.io.Reader;
  
/**
 * INTERNAL: A generic document field.<p>
 */

public class GenericField implements FieldIF {

  protected String name;
  protected String value;
  protected Reader reader;
  protected boolean store;
  protected boolean index;
  protected boolean tokenize;
  
  public GenericField(String name, String value, boolean store, boolean index, boolean tokenize) {
    this.name = name;
    this.value = value;
    this.store = store;
    this.index = index;
    this.tokenize = tokenize;
  }

  public GenericField(String name, Reader value, boolean store, boolean index, boolean tokenize) {
    this.name = name;
    this.reader = value;
    this.store = store;
    this.index = index;
    this.tokenize = tokenize;
  }

  public static FieldIF createUnstoredField(String name, String value) {
    return new GenericField(name, value, false, true, true);
  }
  public static FieldIF createUnstoredField(String name, Reader reader) {
    return new GenericField(name, reader, false, true, true);
  }
  public static FieldIF createKeywordField(String name, String value) {
    return new GenericField(name, value, true, true, false);
  }
  public static FieldIF createTextField(String name, String value) {
    return new GenericField(name, value, true, true, true);
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public String getValue() {
    return value;
  }

  @Override
  public Reader getReader() {
    return reader;
  }

  @Override
  public boolean isStored() {
    return store;
  }

  @Override
  public boolean isIndexed() {
    return index;
  }

  @Override
  public boolean isTokenized() {
    return tokenize;
  }
  
}
