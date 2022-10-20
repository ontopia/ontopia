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


/**
 * INTERNAL: JDOQL method: Collection.contains(Object). Syntax: 'A.contains(B)'.
 */

public class JDOContains implements JDOExpressionIF {

  protected JDOValueIF left;
  protected JDOValueIF right;

  public JDOContains(JDOField left, JDOValueIF right) {
    this((JDOValueIF)left, right);
  }
  
  public JDOContains(JDOParameter left, JDOVariable right) {
    this((JDOValueIF)left, (JDOValueIF)right);
  }

  public JDOContains(JDOCollection left, JDOValueIF right) {
    this((JDOValueIF)left, right);
  }

  public JDOContains(JDOVariable left, JDOValueIF right) {
    this((JDOValueIF)left, right);
  }
  
  protected JDOContains(JDOValueIF left, JDOValueIF right) {
    // FIXME: Is the value always a field?
    this.left = left;
    this.right = right;
  }
  
  @Override
  public int getType() {
    return CONTAINS;
  }
  
  public JDOValueIF getLeft() {
    return left;
  }
  
  public JDOValueIF getRight() {
    return right;
  }
  
  @Override
  public int hashCode() {
    return left.hashCode() + right.hashCode() + CONTAINS;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JDOContains) {
      JDOContains other = (JDOContains)obj;
      return (left.equals(other.left) &&
              right.equals(other.right));
    }
    return false;
  }

  @Override
  public String toString() {
    return left + ".contains(" + right + ")";
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(left);
    visitor.visitable(right);
  }
  
}






