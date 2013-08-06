/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

/**
 * INTERNAL: Represents a prefix declaration.
 */
public class Prefix {

  public static final int TYPE_ITEM_IDENTIFIER = 1;
  public static final int TYPE_SUBJECT_IDENTIFIER = 2;
  public static final int TYPE_SUBJECT_LOCATOR = 4;

  protected String id;
  protected String locator;
  protected int type;
  
  Prefix(String id, String locator, int type) {
    this.id = id;
    this.locator = locator;
    this.type = type;
  }

  /**
   * INTERNAL:
   */
  public String getId() {
    return id;
  }

  /**
   * INTERNAL:
   */
  public String getLocator() {
    return locator;
  }

  /**
   * INTERNAL:
   */
  public int getType() {
    return type;
  }
  
}
