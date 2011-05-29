
package net.ontopia.topicmaps.webed.taglibs.form;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

public class CheckboxTagBeanInfo extends SimpleBeanInfo {
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    List proplist = new ArrayList();
    
    try {
      proplist.add(new PropertyDescriptor("id",  CheckboxTag.class, null, "setId"));
      proplist.add(new PropertyDescriptor("readonly",  CheckboxTag.class, null, "setReadonly"));
      proplist.add(new PropertyDescriptor("class",  CheckboxTag.class, null, "setClass"));
      proplist.add(new PropertyDescriptor("action",  CheckboxTag.class, null, "setAction"));
      proplist.add(new PropertyDescriptor("params",  CheckboxTag.class, null, "setParams"));
      proplist.add(new PropertyDescriptor("state",  CheckboxTag.class, null, "setState"));
      
    } catch (IntrospectionException ex) {
      // ignore
    }
    PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];
    return ((PropertyDescriptor[]) proplist.toArray(result));
  }
}
