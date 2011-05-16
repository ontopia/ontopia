
// $Id: StringTemplateUtils.java,v 1.9 2004/03/08 09:36:14 larsga Exp $

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
  public static String replace(String template, Map paramvalues) {
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
  public static String replace(String template, Map paramvalues,
                               char sep_char) {
    char[] temp = template.toCharArray();
    StringBuffer out = new StringBuffer(temp.length + paramvalues.size()*6);

    int last = 0;
    for (int ix = 0; ix < temp.length; ix++) {
      if (temp[ix] == sep_char) {
        int start = ix;
        out.append(temp, last, start - last);
        for (ix++; ix < temp.length && temp[ix] != sep_char; ix++)
          ;

        if (ix == start + 1) // we found two sep_chars right next to each
          out.append(sep_char);
        else if (ix < temp.length) { // found param ref
          String cur_param = new String(temp, start+1, ix - start - 1);
          Object value = paramvalues.get(cur_param);
          if (value == null)
            throw new OntopiaRuntimeException("Value not set for parameter '" +
                                              cur_param + "' in '" + template + "'.");
          out.append(value.toString());
        } else
          throw new OntopiaRuntimeException("Parameter reference not terminated");
        
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
    StringBuffer out = new StringBuffer(temp.length + 6);

    int last = 0;
    for (int ix = 0; ix < temp.length; ix++) {
      if (temp[ix] == sep_char) {
        int start = ix;
        out.append(temp, last, start - last);
        for (ix++; ix < temp.length && temp[ix] != sep_char; ix++)
          ;

        if (ix == start + 1) // we found two sep_chars right next to each 
          out.append(sep_char);
        else if (ix < temp.length) { // found param ref
          String cur_param = new String(temp, start+1, ix - start - 1);
          if (!param.equals(cur_param))
            throw new OntopiaRuntimeException("Reference to unknown parameter '" +
                                              cur_param + "' in '" + template + "'.");
          if (value == null)
            throw new OntopiaRuntimeException("Value not set for parameter '" +
                                              cur_param + "' in '" + template + "'.");
          out.append(value);
        } else
          throw new OntopiaRuntimeException("Parameter reference not terminated");
        
        last = ix+1;
      }
    }

    out.append(temp, last, temp.length - last);
    return out.toString();
  }
  
}
