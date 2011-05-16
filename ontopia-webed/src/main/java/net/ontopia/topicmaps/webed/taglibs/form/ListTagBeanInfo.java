
// $Id: ListTagBeanInfo.java,v 1.1 2005/11/24 09:58:18 grove Exp $

package net.ontopia.topicmaps.webed.taglibs.form;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

public class ListTagBeanInfo extends SimpleBeanInfo {
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    List proplist = new ArrayList();
    
    try {
      proplist.add(new PropertyDescriptor("id",  ListTag.class, null, "setId"));
      proplist.add(new PropertyDescriptor("readonly",  ListTag.class, null, "setReadonly"));
      proplist.add(new PropertyDescriptor("class",  ListTag.class, null, "setClass"));
      proplist.add(new PropertyDescriptor("action",  ListTag.class, null, "setAction"));
      proplist.add(new PropertyDescriptor("params",  ListTag.class, null, "setParams"));
      proplist.add(new PropertyDescriptor("collection",  ListTag.class, null, "setCollection"));
      proplist.add(new PropertyDescriptor("selected",  ListTag.class, null, "setSelected"));
      proplist.add(new PropertyDescriptor("unspecified",  ListTag.class, null, "setUnspecified"));
      proplist.add(new PropertyDescriptor("type",  ListTag.class, null, "setType"));
      
    } catch (IntrospectionException ex) {
      // ignore
    }
    PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];
    return ((PropertyDescriptor[]) proplist.toArray(result));
  }
}
