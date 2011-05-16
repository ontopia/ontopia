
// $Id: FileTagBeanInfo.java,v 1.1 2005/11/24 09:58:18 grove Exp $

package net.ontopia.topicmaps.webed.taglibs.form;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

public class FileTagBeanInfo extends SimpleBeanInfo {
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    List proplist = new ArrayList();
    
    try {
      proplist.add(new PropertyDescriptor("id",  FileTag.class, null, "setId"));
      proplist.add(new PropertyDescriptor("readonly",  FileTag.class, null, "setReadonly"));
      proplist.add(new PropertyDescriptor("class",  FileTag.class, null, "setClass"));
      proplist.add(new PropertyDescriptor("action",  FileTag.class, null, "setAction"));
      proplist.add(new PropertyDescriptor("params",  FileTag.class, null, "setParams"));
      
    } catch (IntrospectionException ex) {
      // ignore
    }
    PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];
    return ((PropertyDescriptor[]) proplist.toArray(result));
  }
}
