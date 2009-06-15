
// $Id: TologOptions.java,v 1.2 2005/07/13 08:57:21 grove Exp $

package net.ontopia.topicmaps.query.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: Used to parse and represent the options passed to various
 * queries.
 */
public class TologOptions {
  private Map options;
  
  public TologOptions() {
    options = new HashMap();
  }

  public boolean getBooleanValue(String name, boolean defvalue) {
    String value = (String) options.get(name);
    if (value == null)
      return defvalue;
    else
      return value.equalsIgnoreCase("true");
  }

  // --- Option parsing
  
  // "#OPTION: name.of.option: value"
  public void parse(String comment) {
    int pos = ignoreWS(comment, 0);
    pos = check(comment, pos, "#OPTION:");
    if (pos == -1)
      return; // syntax error -> no options here

    pos = ignoreWS(comment, pos);
    if (pos == -1)
      return; // syntax error -> no options here

    String name = getName(comment, pos);
    if (name == null)
      return; // syntax error -> no options here
    pos += name.length();

    pos = ignoreWS(comment, pos);
    if (pos == -1)
      return; // syntax error -> no options here

    pos = check(comment, pos, "=");
    if (pos == -1)
      return; // syntax error -> no options here

    pos = ignoreWS(comment, pos);
    if (pos == -1)
      return; // syntax error -> no options here

    String value = getValue(comment, pos);
    if (value == null)
      return; // syntax error -> no options here

    options.put(name, value); // there was a value
  }

  private int ignoreWS(String data, int pos) {
    if (pos == data.length())
      return -1;
    
    char ch = ' ';
    while (pos < data.length() &&
           (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'))
      ch = data.charAt(pos++);
    return pos - 1;
  }

  private int check(String data, int pos, String expected) {
    int start = pos;
    while (pos < data.length() &&
           pos - start < expected.length() &&
           data.charAt(pos) == expected.charAt(pos - start))
      pos++;
    if (pos == start)
      return -1;
    else
      return pos;
  }

  private String getName(String data, int pos) {
    int start = pos;
    char ch = 'a';
    while (pos < data.length() &&
           ((ch >= 'a' && ch <= 'z') ||
            (ch >= 'A' && ch <= 'Z') ||
            (ch >= '0' && ch <= '9') ||
            ch == '.' || ch == '-'))
      ch = data.charAt(pos++);

    if (pos == start)
      return null;
    
    return data.substring(start, pos - 1);
  }
  
  private String getValue(String data, int pos) {
    return getName(data, pos); // FIXME: too primitive, obviously
  }
}
