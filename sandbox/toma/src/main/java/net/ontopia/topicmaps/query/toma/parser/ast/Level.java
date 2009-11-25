/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.parser.ast;

/**
 * INTERNAL: Indicates the level parameter for several path element expression,
 * e.g. type, instance, super, sub. A Level has a start and end value indicating
 * the range for this operations.
 */
public class Level {
  private int start;
  private int end;

  /**
   * Creates a new range in the form [level, level].
   * 
   * @param level the specified level.
   */
  public Level(int level) {
    this(level, level);
  }

  /**
   * Creates a new range in the form [start, end].
   * 
   * @param start the start of the range.
   * @param end the end of the range.
   */
  public Level(int start, int end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Get the start value.
   * 
   * @return the start value.
   */
  public int getStart() {
    return start;
  }

  /**
   * Set the start value.
   * 
   * @param start the start value to be set.
   */
  public void setStart(int start) {
    this.start = start;
  }

  /**
   * Get the end value.
   * 
   * @return the end value.
   */
  public int getEnd() {
    return end;
  }

  /**
   * Set the end value.
   * 
   * @param end the end value to be set.
   */
  public void setEnd(int end) {
    this.end = end;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (start == end) {
      sb.append(convertLevelToString(start));
    } else {
      sb.append(convertLevelToString(start));
      sb.append("..");
      sb.append(convertLevelToString(end));
    }
    return sb.toString();
  }
  
  private String convertLevelToString(int lvl) {
    if (lvl == Integer.MAX_VALUE) {
      return "*";
    } else {
      return String.valueOf(lvl);
    }
  }
}
