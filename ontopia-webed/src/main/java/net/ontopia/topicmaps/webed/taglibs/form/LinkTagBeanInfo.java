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

public class LinkTagBeanInfo extends SimpleBeanInfo {
  
  @Override
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
