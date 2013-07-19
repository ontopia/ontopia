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

package net.ontopia.topicmaps.query.spi;

/**
 * INTERNAL: Class that holds search hit data.<p>
 */
public class Hit {

  private Object value;
  private float score;
  
  public Hit(Object value, float score) {
    this.value = value;
    this.score = score;
  }
  
  /**
   * INTERNAL: Gets the result value.
   */
  public Object getValue() {
    return value;
  }

  /**
   * INTERNAL: Gets the score ;
   */
  public float getScore() {
    return score;
  }

  public String toString() {
    return "[Hit " + getValue() + ", " + getScore() + "]";
  }
  
}





