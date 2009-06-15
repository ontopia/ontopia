// $Id: Render.java,v 1.6 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.structures;

import java.util.*;

import org.apache.log4j.*;

/** 
 * INTERNAL: Provides a structure to allow string generation using
 * templates in a Stringifier.
 * <p>The Navigator operates on a number of "Render" stringifiers
 * which have an empty constructor and only take Render objects to
 * stringify.
 */
public class Render {
  
  protected String template;
  protected String name; 
  protected String type; 
  protected Map strings;
  
  /**
   * Constructor which must be used.
   */
  public Render (String template, String name, Map strings) {
    this.template = template;
    this.name = name;
    this.strings = strings; 
  }
  
  // set methods
  public void setTemplate(String s) {
    template = s;
  }
  
  public void setName(String s) {
    name = s;
  }
  
  public void setStrings(Map m) {
    strings = m;
  }
  
  // get methods
  public String getTemplate() {
    return template;
  }
  
  public String getName() {
    return name;
  }
  
  public Map getStrings() {
    return strings;
  }
}





