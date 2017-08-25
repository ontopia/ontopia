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

package net.ontopia.topicmaps.nav.utils.stringifiers;

import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.core.NameIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Stringifier that stringifies TopicNameIFs and VariantNameIFs.
 */
public class ComparatorNameStringifier implements StringifierIF<NameIF> {
  
  /**
   * INTERNAL: Stringifies the given basename or variant name.
   *
   * @param name the name object to use; TopicNameIF or VariantNameIF
   * @return string containing name value or "~~~~~" if name not set
   */
  @Override
  public String toString(NameIF name) {
    if (name == null)
      return "~~~~~";
    if (name instanceof TopicNameIF) {
      return ((TopicNameIF) name).getValue();
    } else {
      VariantNameIF vname = (VariantNameIF) name;
      if (vname.getValue() != null) 
        return vname.getValue();
      else
        return vname.getLocator().getAddress();      
    }
  }
  
}





