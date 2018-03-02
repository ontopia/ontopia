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

public class ListTagBeanInfo extends SimpleBeanInfo {
  
  @Override
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
