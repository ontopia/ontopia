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

import java.util.Map;

/**
 * INTERNAL: Utilities for processing string templates containing
 * %param% references.
 *
 * @since 1.3
 */
public class StringTemplateUtils {

  /**
   * INTERNAL: Replaces all %name% references with the value of the
   * "name" key in the paramvalues Map. Single '%' characters in the
   * output must be represented as "%%" in the template.   
   */
  public static String replace(String template, Map<String, Object> paramvalues) {
    return replace(template, paramvalues, '%');
  }
  
  /**
   * INTERNAL: Replaces all references surounded by a separator
   * character with the value of the "name" key in the paramvalues
   * Map. Single separator characters in the output must be
   * represented by placing the separator character two times right
   * behind each other in the template.
   *
   * @param sep_char the separator charactor which delimits the
   *                 references
   * @since 1.3.1
   */
  public static String replace(String template, Map<String, Object> paramvalues,
                               char sep_char) {
    char[] temp = template.toCharArray();
    StringBuilder out = new StringBuilder(temp.length + paramvalues.size()*6);

    int last = 0;
    for (int ix = 0; ix < temp.length; ix++) {
      if (temp[ix] == sep_char) {
        int start = ix;
        out.append(temp, last, start - last);
        for (ix++; ix < temp.length && temp[ix] != sep_char; ix++)
          ;

        if (ix == start + 1) { // we found two sep_chars right next to each
          out.append(sep_char);
        } else if (ix < temp.length) { // found param ref
          String cur_param = new String(temp, start+1, ix - start - 1);
          Object value = paramvalues.get(cur_param);
          if (value == null) {
            throw new OntopiaRuntimeException("Value not set for parameter '" +
                                              cur_param + "' in '" + template + "'.");
          }
          out.append(value.toString());
        } else {
          throw new OntopiaRuntimeException("Parameter reference not terminated");
        }
        
        last = ix+1;
      }
    }

    out.append(temp, last, temp.length - last);
    return out.toString();
  }

  /**
   * INTERNAL: Replaces all %name% references with the value of the
   * parameter param. Single '%' characters in the output must be
   * represented as "%%" in the template.
   *
   * @since 1.3.1
   */
  public static String replace(String template, String param, String value) {
    return replace(template, param, value, '%');
  }
  
  /**
   * INTERNAL: Replaces all references surrounded by a separator
   * character with the value of the parameter param. Single separator
   * characters in the output must be represented by placing the
   * separator character two times right behind each other in the
   * template.
   *
   * @since 1.3.1
   */
  public static String replace(String template, String param, String value,
                               char sep_char) {
    char[] temp = template.toCharArray();
    StringBuilder out = new StringBuilder(temp.length + 6);

    int last = 0;
    for (int ix = 0; ix < temp.length; ix++) {
      if (temp[ix] == sep_char) {
        int start = ix;
        out.append(temp, last, start - last);
        for (ix++; ix < temp.length && temp[ix] != sep_char; ix++)
          ;

        if (ix == start + 1) { // we found two sep_chars right next to each 
          out.append(sep_char);
        } else if (ix < temp.length) { // found param ref
          String cur_param = new String(temp, start+1, ix - start - 1);
          if (!param.equals(cur_param)) {
            throw new OntopiaRuntimeException("Reference to unknown parameter '" +
                                              cur_param + "' in '" + template + "'.");
          }
          if (value == null) {
            throw new OntopiaRuntimeException("Value not set for parameter '" +
                                              cur_param + "' in '" + template + "'.");
          }
          out.append(value);
        } else {
          throw new OntopiaRuntimeException("Parameter reference not terminated");
        }
        
        last = ix+1;
      }
    }

    out.append(temp, last, temp.length - last);
    return out.toString();
  }
  
}
