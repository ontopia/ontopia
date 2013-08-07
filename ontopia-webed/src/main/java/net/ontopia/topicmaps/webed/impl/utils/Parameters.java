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

package net.ontopia.topicmaps.webed.impl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.ontopia.topicmaps.webed.core.FileValueIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;

/**
 * INTERNAL: This class is used to hide the API difference between
 * FileUpload and ordinary servlets. Since ordinary servlets can't
 * handle file upload we are forced to create our own class into
 * which we can push request parameters. A Map would not do since we
 * have to deal with both simple strings and string arrays, so this
 * class is a convenience wrapper around a Map. Maybe Servlets 3.0
 * will fix this ridiculous self-created "problem".   
 */
public class Parameters {
  private Map params;
  private Map files;

  public Parameters() {
    this.params = new HashMap();
    this.files = new HashMap();
  }

  public void addParameter(String name, String value) {
    if (params.containsKey(name)) {
      String[] values = (String[]) params.get(name);
      String[] v2 = new String[values.length + 1];
      System.arraycopy(values, 0, v2, 0, values.length);
      v2[values.length] = value;
      addParameter(name, v2);
        
    } else if (value != null && !value.equals("")) {
      String[] values = new String[1];
      values[0] = value;
      addParameter(name, values);
    }
  }
    
  public void addParameter(String name, String[] values) {
    // get rid of image submit speciality (which concats ".[x|y]")
    if (name.endsWith(".x") || name.endsWith(".y")) {
      name = name.substring(0, name.length() - 2);
      // since it doesn't make sense for us to use the image click
      // coordinates stored in this case in the value we fall back.
      values[0] = Constants.RPV_DEFAULT;
    }
      
    params.put(name, values);
  }

  public void addParameter(String name, FileValueIF parameter) {
    addParameter(name, parameter.getFileName());
    files.put(name, parameter);
  }

  public Set getNames() {
    return params.keySet();
  }
  
  public String get(String name) {
    String[] values = (String[]) params.get(name);
    if (values != null)
      return values[0];
    else
      return null;
  }

  public String[] getValues(String name) {
    return (String[]) params.get(name);
  }

  public FileValueIF getFile(String name) {
    return (FileValueIF) files.get(name);
  }

  public Map getMap() {
    return params;
  }
}
