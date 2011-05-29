
package net.ontopia.topicmaps.webed.taglibs.form;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

public class FormTagBeanInfo extends SimpleBeanInfo {
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    List proplist = new ArrayList();
    
    try {
      proplist.add(new PropertyDescriptor("id",  FormTag.class, null, "setId"));
      proplist.add(new PropertyDescriptor("readonly",  FormTag.class, null, "setReadonly"));
      proplist.add(new PropertyDescriptor("class",  FormTag.class, null, "setClass"));
      proplist.add(new PropertyDescriptor("actiongroup",  FormTag.class, null, "setActiongroup"));
      proplist.add(new PropertyDescriptor("actionURI",  FormTag.class, null, "setActionURI"));
      proplist.add(new PropertyDescriptor("target",  FormTag.class, null, "setTarget"));
      proplist.add(new PropertyDescriptor("lock",  FormTag.class, null, "setLock"));
      proplist.add(new PropertyDescriptor("enctype",  FormTag.class, null, "setEnctype"));
      proplist.add(new PropertyDescriptor("nested",  FormTag.class, null, "setNested"));
      
    } catch (IntrospectionException ex) {
      // ignore
    }
    PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];
    return ((PropertyDescriptor[]) proplist.toArray(result));
  }
}
