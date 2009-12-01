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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.Level;
import net.ontopia.topicmaps.utils.AssociationWalker;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.utils.SubjectIdentityDecider;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.DeciderIF;

/**
 * INTERNAL: Subtype path element in an path expression. Returns all subtypes 
 * of a given type in the topic map. 
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p><p>
 * <b>Output</b>: TOPIC
 * </p>
 */
@SuppressWarnings("unchecked")
public class SubTypePath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;

  static {
    inputSet = new CompactHashSet();
    inputSet.add(TYPE.TOPIC);
  }

  private AssociationWalker subtypesWalker;
  
  public SubTypePath() {
    super("SUB");
    
    DeciderIF assocDecider = new SubjectIdentityDecider(PSI.getXTMSuperclassSubclass());
    DeciderIF subclassDecider = new SubjectIdentityDecider(PSI.getXTMSubclass());
    DeciderIF superclassDecider = new SubjectIdentityDecider(PSI.getXTMSuperclass());
        
    subtypesWalker = new AssociationWalker(assocDecider, superclassDecider, subclassDecider);
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

  public Collection<TopicIF> evaluate(LocalContext context, Object input) {
    TopicIF topic = (TopicIF) input;
    
    // level is required for this element
    Level l = getLevel();
    
    // use a set as collection for the types, as one type can occur multiple
    // times (and should only be counted once).
    Collection<TopicIF> types = new CompactHashSet();

    int start = l.getStart()*2;
    int end = (l.getEnd() == Integer.MAX_VALUE) ? l.getEnd() : l.getEnd()*2;
    
    Iterator it = subtypesWalker.walkPaths(topic).iterator();
    while (it.hasNext()) {
      List path = (List) it.next();
      for (int idx = start; idx < path.size() && idx <= end; idx += 2) 
        types.add((TopicIF) path.get(idx));
    }
    
    return types;
  }  
}
