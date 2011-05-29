
package net.ontopia.topicmaps.webed.impl.basic;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;

/**
 * INTERNAL: Container for all the ActionData carriers produced for
 * one HTML form.
 */
public class ActionDataSet {
  private String request_id;
  private Map actions; // request param -> ActionData

  public ActionDataSet(String request_id) {
    this.request_id = request_id;
    this.actions = new HashMap();
  }

  public void addActionData(ActionData data) {
    actions.put(data.getFieldName(), data);
  }
  
  public ActionData getActionData(String fieldName) {
    return (ActionData)actions.get(fieldName);
  }

  public Collection getAllActionData() {
    return actions.values();
  }

  public String getRequestId() {
    return request_id;
  }
}
