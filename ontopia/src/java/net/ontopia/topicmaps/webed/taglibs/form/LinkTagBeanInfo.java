
// $Id: LinkTagBeanInfo.java,v 1.3 2006/06/29 13:17:35 grove Exp $

package net.ontopia.topicmaps.webed.taglibs.form;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

public class LinkTagBeanInfo extends SimpleBeanInfo {
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    List proplist = new ArrayList();
    
    try {
      proplist.add(new PropertyDescriptor("id",  LinkTag.class, null, "setId"));
      proplist.add(new PropertyDescriptor("readonly",  LinkTag.class, null, "setReadonly"));
      proplist.add(new PropertyDescriptor("class",  LinkTag.class, null, "setClass"));
      proplist.add(new PropertyDescriptor("action",  LinkTag.class, null, "setAction"));
      proplist.add(new PropertyDescriptor("params",  LinkTag.class, null, "setParams"));
      proplist.add(new PropertyDescriptor("href",  LinkTag.class, null, "setHref"));
      proplist.add(new PropertyDescriptor("target",  LinkTag.class, null, "setTarget"));
      proplist.add(new PropertyDescriptor("title",  LinkTag.class, null, "setTitle"));
      proplist.add(new PropertyDescriptor("type",  LinkTag.class, null, "setType"));
      
    } catch (IntrospectionException ex) {
      // ignore
    }
    PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];
    return ((PropertyDescriptor[]) proplist.toArray(result));
  }
}
