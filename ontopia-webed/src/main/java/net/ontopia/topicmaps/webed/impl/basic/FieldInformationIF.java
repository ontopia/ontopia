/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.basic;

/**
 * INTERNAL: Container for storing information about an input field used for
 * displaying an form as part of the user interface.
 */
public interface FieldInformationIF {

  /**
   * INTERNAL: Gets the name of the input field.
   */
  public String getName();

  /**
   * INTERNAL: Gets the type of the input field. Allowed values are
   * "text" (default) and "textarea".
   */
  public String getType();

  /**
   * INTERNAL: Gets the maximum number of characters allowed for this
   * field to be typed in by the user.
   */
  public String getMaxLength();

  /**
   * INTERNAL: Gets the number of character columns for this field.
   */
  public String getColumns();

  /**
   * INTERNAL: Gets the number of rows for this field.
   */
  public String getRows();

}




