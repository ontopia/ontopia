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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: ID path element in an path expression. Returns the item identifiers
 * of a given topic map construct as a locator.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * <li>NAME
 * <li>VARIANT
 * <li>OCCURRENCE
 * <li>ASSOCIATION
 * </ul>
 * </p><p>
 * <b>Output</b>: LOCATOR
 * </p>
 */
public class ItemIDPath extends AbstractBasicPathElement {
  
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
    inputSet.add(TYPE.NAME);
    inputSet.add(TYPE.VARIANT);
    inputSet.add(TYPE.OCCURRENCE);
    inputSet.add(TYPE.ASSOCIATION);
  }
  
  public ItemIDPath() {
    super("ID");
  }
  
  protected boolean isLevelAllowed() {
    return false;
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
    return TYPE.LOCATOR;
  }

  @SuppressWarnings("unchecked")
  public Collection<LocatorIF> evaluate(LocalContext context, Object input) {
    TMObjectIF tm = (TMObjectIF) input;
    return tm.getItemIdentifiers();
  }
}
