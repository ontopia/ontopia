package net.ontopia.topicmaps.utils.tmrap.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class TMRAPTestUtils {

  private static final String SEPARATOR = "|";

  /**
   * Generate a HashMap from a given parameter String.
   * @param parameters The String containing the source parameters.
   * @return A HashMap containig those parameters as key-value pairs.
   */
  public static HashMap tokenizeParameters(String parameters) {
    HashMap retVal = new HashMap();
    
    StringTokenizer tok = new StringTokenizer(parameters);
    
    while (tok.hasMoreTokens()) {
      String currentToken = tok.nextToken("&");
      
      StringTokenizer keyValueTokenizer =  new StringTokenizer(currentToken);
      String key = keyValueTokenizer.hasMoreTokens() ?
          keyValueTokenizer.nextToken("=") : null;
      String value = keyValueTokenizer.hasMoreTokens() ?
          keyValueTokenizer.nextToken("=") : null;
      
      if (key != null)
        addParam(retVal, key, value);
    }

    return retVal;
  }
  
  /**
   * Add a given key-value pair to to a given map.
   * If the mapping already exists:
   *   If it is a Collection then add value to it.
   *   Otherwise, replace it with an ArrayList holding the old and new value.
   * @param map The map to which the key-value pair should be added.
   * @param key The to of the new mapping.
   * @param value The value of the new mapping.
   */
  public static void addParam(Map map, Object key, Object value) {
    if (map.containsKey(key)) {
      String oldValue = (String)map.get(key);
      map.put(key, oldValue + SEPARATOR + value);
    } else
      map.put(key, value);
  }

  /**
   * Generate a Hashtable from the given Map of parameters.
   * @param params The Map from which to generate a Hashtable
   * @return The generated Hashtable.
   */
  public static Hashtable tabularizeParameters(Map params) {
    Hashtable reqParams = new Hashtable();
    Iterator it = params.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      String val = (String) params.get(key);
      // figure out if this is a single value or a multi value field
      if (val.indexOf(SEPARATOR) < 0) {
        reqParams.put(key, val);
      } else {
        StringTokenizer strtok = new StringTokenizer(val, SEPARATOR);
        List values = new ArrayList();
        while (strtok.hasMoreTokens()) {
          String sVal = strtok.nextToken();
          values.add(sVal);
        }
        reqParams.put(key, values.toArray(new String[values.size()]));
      }
    } // while
    return reqParams;
  }
}
