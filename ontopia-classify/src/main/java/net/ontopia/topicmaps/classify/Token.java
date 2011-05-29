
package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;
  
/**
 * INTERNAL: 
 */
public class Token {
  public static final int TYPE_VARIANT = 1;
  public static final int TYPE_DELIMITER = 2;
  
  protected String value;
  protected int type;
  
  Token(String value, int type) {
    this.value = value;
    this.type = type;
  }
  
  public String getValue() {
    return value;
  }

  public int getType() {
    return type;
  }
  
  public String toString() {
    return '\'' + getValue() + "\':" + getType();
  }
  
}
