/*
 * #!
 * Ontopia Engine
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

package net.ontopia.persistence.query.jdo;

import java.util.Arrays;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: JDOQL value: instance field. Represents the traversal
 * from a value instance to the value of one of its fields. Syntax:
 * 'A.field'.
 */

public class JDOField implements JDOValueIF {

  protected JDOValueIF root;
  protected String[] path;
  protected boolean evaluatable;

  public JDOField(JDOValueIF root, String name) {
    this(root, name, true);
  }
  public JDOField(JDOValueIF root, String name, boolean evaluatable) {
    this(root, new String[] { name }, evaluatable);
    if (name == null) throw new NullPointerException("Field name cannot be null.");
  }

  public JDOField(JDOValueIF root, String name1, String name2) {
    this(root, new String[] { name1, name2 });    
  }
  public JDOField(JDOValueIF root, String name1, String name2, boolean evaluatable) {
    this(root, new String[] { name1, name2 }, evaluatable);    
  }

  public JDOField(JDOValueIF root, String name1, String name2, String name3) {
    this(root, new String[] { name1, name2, name3 });    
  }
  public JDOField(JDOValueIF root, String name1, String name2, String name3, boolean evaluatable) {
    this(root, new String[] { name1, name2, name3 }, evaluatable);    
  }

  public JDOField(JDOValueIF root, String name1, String name2, String name3, String name4) {
    this(root, new String[] { name1, name2, name3, name4 });    
  }
  public JDOField(JDOValueIF root, String name1, String name2, String name3, String name4, boolean evaluatable) {
    this(root, new String[] { name1, name2, name3, name4 }, evaluatable);    
  }

  public JDOField(JDOValueIF root, String name1, String name2, String name3, String name4, String name5) {
    this(root, new String[] { name1, name2, name3, name4, name5 });    
  }
  public JDOField(JDOValueIF root, String name1, String name2, String name3, String name4, String name5, boolean evaluatable) {
    this(root, new String[] { name1, name2, name3, name4, name5 }, evaluatable);    
  }
  
  public JDOField(JDOValueIF root, String[] path) {
    this(root, path, true);
  }

  public JDOField(JDOValueIF root, String[] path, boolean evaluatable) {
    if (root == null) throw new NullPointerException("Field root cannot be null.");
    if (root instanceof JDOField) throw new OntopiaRuntimeException("Please do not use nested field objects.");
    if (path == null || path.length < 1) throw new NullPointerException("Field path cannot be null.");
    this.root = root;
    this.path = path;
    this.evaluatable = evaluatable;
  }

  public int getType() {
    return FIELD;
  }
  
  public JDOValueIF getRoot() {
    return root;
  }
  
  public String[] getPath() {
    return path;
  }

  public boolean getEvaluatable() {
    return evaluatable;
  }

  public int hashCode() {
    int hashCode = root.hashCode();
    for (int ix = 0; ix < path.length; ix++) {
      if (path[ix] != null)
        hashCode = (hashCode + path[ix].hashCode()) & 0x7FFFFFFF;
    }
    return hashCode;
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOField) {
      JDOField other = (JDOField)obj;
      if (root.equals(other.root))
        if (Arrays.equals(path, other.path))
          return true;
    }
    return false;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer(root.toString());
    sb.append('.');
    StringUtils.join(path, ".", sb);
    if (!getEvaluatable()) sb.append('*');
    return sb.toString();
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(root);
  }
  
}






