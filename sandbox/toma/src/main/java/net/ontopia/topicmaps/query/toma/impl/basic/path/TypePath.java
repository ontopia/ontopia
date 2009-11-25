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
package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.Level;

/**
 * INTERNAL: Type path element in an path expression. Returns all types
 * of a given input topic.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p><p>
 * <b>Output</b>: TOPIC
 * </p>
 */
public class TypePath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }
  
  public TypePath() {
    super("TYPE");
  }

  protected boolean isLevelAllowed() {
    return true;
  }

  protected boolean isScopeAllowed() {
    return false;
  }
  
  protected boolean isTypeAllowed() {
    return false;
  }

  protected boolean isChildAllowed() {
    return false;
  }
  
  public Set<TYPE> validInput() {
    return inputSet;
  }
  
  public TYPE output() {
    return TYPE.TOPIC;
  }
  
  @SuppressWarnings("unchecked")
  public Collection<TopicIF> evaluate(LocalContext context, Object input) {
    TopicIF topic = (TopicIF) input;
    
    // level is required for this element
    Level l = getLevel();
    
    // use a set as collection for the types, as one type can occur multiple
    // times (and should only be counted once).
    Collection<TopicIF> types = new HashSet<TopicIF>();

    // include the topic itself, if the start level is 0
    if (l.getStart() == 0) {
      types.add(topic);
    }
    
    fillTypeList(topic.getTypes(), l, 1, types);
    return types;
  }
  
  @SuppressWarnings("unchecked")
  private void fillTypeList(Collection<TopicIF> types, Level l, int depth,
      Collection<TopicIF> result) {
    // we are finished if we reached the end of the range, or the typelist is empty
    if (depth > l.getEnd() || types == null) {
      return;
    }

    // only add the types if we are already within the required range
    if (depth >= l.getStart()) {
      result.addAll(types);
    }
    
    for (TopicIF type : types) {
      fillTypeList(type.getTypes(), l, depth+1, result);
    }
  }
}
