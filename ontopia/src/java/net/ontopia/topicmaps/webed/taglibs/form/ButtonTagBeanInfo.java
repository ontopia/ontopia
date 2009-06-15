
// $Id: ButtonTagBeanInfo.java,v 1.1 2005/11/24 09:58:18 grove Exp $

package net.ontopia.topicmaps.webed.taglibs.form;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

public class ButtonTagBeanInfo extends SimpleBeanInfo {
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    List proplist = new ArrayList();
    
    try {
      proplist.add(new PropertyDescriptor("id",  ButtonTag.class, null, "setId"));
      proplist.add(new PropertyDescriptor("readonly",  ButtonTag.class, null, "setReadonly"));
      proplist.add(new PropertyDescriptor("class",  ButtonTag.class, null, "setClass"));
      proplist.add(new PropertyDescriptor("action",  ButtonTag.class, null, "setAction"));
      proplist.add(new PropertyDescriptor("params",  ButtonTag.class, null, "setParams"));
      proplist.add(new PropertyDescriptor("text",  ButtonTag.class, null, "setText"));
      proplist.add(new PropertyDescriptor("image",  ButtonTag.class, null, "setImage"));
      proplist.add(new PropertyDescriptor("reset",  ButtonTag.class, null, "setReset"));
      
    } catch (IntrospectionException ex) {
      // ignore
    }
    PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];
    return ((PropertyDescriptor[]) proplist.toArray(result));
  }
}
