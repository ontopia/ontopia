
// $Id: AbstractTaglibTestCase.java,v 1.17 2006/01/04 15:32:28 larsga Exp $

package net.ontopia.topicmaps.nav2.impl.framework.test;

import java.io.File;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.ontopia.test.AbstractOntopiaTestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Abstract class for handling a test case related to the
 * navigator taglib framework.
 */
public class AbstractTaglibTestCase extends AbstractOntopiaTestCase {

  static Logger log = LoggerFactory.getLogger(AbstractTaglibTestCase.class.getName());
  protected String base;
  protected String jspfile;
  protected String topicmapId;
  protected Hashtable reqParams;

  /** Use this character sequence to separate attribute values */
  protected final static String SEPARATOR = "|";
  
  /**
   * Default constructor.
   */
  public AbstractTaglibTestCase(String jspfile, String base,
                                String topicmapId) {
    super("testJSP");
    this.base = base;
    this.jspfile = jspfile;
    this.topicmapId = topicmapId;
  }

  public String toString() {
    // overwrite method from junit.framework.TestCase
    return getName() + "  file: " + jspfile +
      " with topicmap: " + topicmapId;
  }

  /**
   * Sets the parameters of the Request in the fake servlet
   * environment from the map containing parameter key-value pairs.
   */
  protected void setRequestParameters(Map params) {
    reqParams = new Hashtable();
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
  }

  /**
   * Gets the request parameters used by the JSP as input.
   */
  protected Hashtable getRequestParameters() {
    return reqParams;
  }

  protected String getTopicMapId() {
    return topicmapId;
  }

  protected String getJspFileName() {
    return jspfile;
  }

  protected String getJSPSource() {
    return (base + "jsp" + File.separator + jspfile);
  }
  
  protected String getBase() {
    return base;
  }

}
