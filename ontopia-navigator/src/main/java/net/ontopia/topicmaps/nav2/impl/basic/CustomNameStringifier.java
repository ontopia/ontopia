/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Stringifier that stringifies TopicNameIFs and VariantNameIFs
 * by calling their getValue() method. In addition it can be specified
 * which strings to use for the different fail-situations:
 * <ul>
 *  <li>the object is null (no base name/variant name existent)</li>
 *  <li>the value is null (null base name/variant name)</li>
 *  <li>the value is empty (empty string base name/variant name)</li>
 * </ul> 
 */
public class CustomNameStringifier implements Function<Object, String> {

  // define fallback values
  protected String stringNonExistent = "[No name]";
  protected String stringValueNull   = "[Null name]";
  protected String stringValueEmpty  = "[Empty name]";
  
  /**
   * INTERNAL: Stringifies the given name.
   *
   * @param name object, cast to TopicNameIF or VariantNameIF
   *             internally; the given name
   * @return string containing name value or "[No name]"
   */
  @Override
  public String apply(Object name) {
    String stringName = null;
    if (name == null)
      return stringNonExistent;
    if (name instanceof TopicNameIF)
      stringName = ((TopicNameIF) name).getValue();
    else
      stringName = ((VariantNameIF) name).getValue();
    if (stringName == null)
      stringName = stringValueNull;
    else if (stringName.isEmpty())
      stringName = stringValueEmpty;
    return stringName;
  }

  public void setStringNonExistent(String stringNonExistent) {
    this.stringNonExistent = stringNonExistent;
  }
  
  public String getStringNonExistent() {
    return stringNonExistent;
  }

  public void setStringValueNull(String stringValueNull) {
    this.stringValueNull = stringValueNull;
  }
  
  public String getStringValueNull() {
    return stringValueNull;
  }

  public void setStringValueEmpty(String stringValueEmpty) {
    this.stringValueEmpty = stringValueEmpty;
  }
  
  public String getStringValueEmpty() {
    return stringValueEmpty;
  }

}
