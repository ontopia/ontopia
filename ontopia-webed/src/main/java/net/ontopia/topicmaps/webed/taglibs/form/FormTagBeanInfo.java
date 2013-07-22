/*
 * #!
 * Ontopia Webed
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
