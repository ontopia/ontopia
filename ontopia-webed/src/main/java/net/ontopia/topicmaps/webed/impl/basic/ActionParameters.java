
package net.ontopia.topicmaps.webed.impl.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.FileValueIF;
import net.ontopia.topicmaps.webed.core.WebEdRequestIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Default implementation of ActionParametersIF.
 */
public class ActionParameters implements ActionParametersIF {
  private List params;
  private String[] fieldvalues;
  private TopicMapIF topicmap;
  private WebEdRequestIF request;
  private FileValueIF filevalue;
  
  static Logger logger = LoggerFactory.getLogger(ActionParameters.class.getName());

  public ActionParameters(String fieldname, String[] fieldvalues,
                          FileValueIF filevalue,
                          List params, TopicMapIF topicmap, WebEdRequestIF request) {

    this.fieldvalues = fieldvalues;
    this.filevalue = filevalue;
    this.params = params;
    this.topicmap = topicmap;
    this.request = request;

    logger.debug("Field " + fieldname + " had params " + params);
  }

  private ActionParameters(ActionParameters old, List newparams) {
    this.fieldvalues = old.fieldvalues;
    this.filevalue = old.filevalue;
    this.params = newparams;
    this.topicmap = old.topicmap;
    this.request = old.request;
  }

  // ------------------------------------------------------------
  // implementation of ActionParametersIF
  // ------------------------------------------------------------
  
  public Object get(int ix) {
    Collection values = getCollection(ix);
    if (values == null || values.isEmpty())
      return null;

    return values.iterator().next();
  }

  public Collection getCollection(int ix) {
    if (ix >= params.size())
      return null;
    else
      return (Collection) params.get(ix);
  }

  public int getParameterCount() {
    return params.size();
  }

  public String getStringValue() {
    if (fieldvalues == null)
      return null;
    return fieldvalues[0];
  }

  public String[] getStringValues() {
    return fieldvalues;
  }

  public boolean getBooleanValue() {
    return fieldvalues != null && fieldvalues[0].equals("on");  
  }
  
  public TMObjectIF getTMObjectValue() {
    if (fieldvalues == null || fieldvalues[0] == null)
      return null;
    
    return topicmap.getObjectById(fieldvalues[0]);
  }

  public Collection getTMObjectValues() {
    if (fieldvalues == null)
      return Collections.EMPTY_SET;
    
    Set objects = new HashSet();
    for (int ix = 0; ix < fieldvalues.length; ix++) {
      TMObjectIF object = topicmap.getObjectById(fieldvalues[ix]);
      if (object != null)
        objects.add(object);
    }
    return objects;
  }

  public FileValueIF getFileValue() {
    return filevalue;
  }

  public WebEdRequestIF getRequest() {
    return request;
  }

  public ActionParametersIF cloneAndOverride(List newparams) {
    return new ActionParameters(this, newparams);
  }
  
}
