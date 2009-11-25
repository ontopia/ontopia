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
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: Reference path element in an path expression. Returns the locator for
 * an variant or occurrence element.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>VARIANT
 * <li>OCCURRENCE
 * </ul>
 * </p><p>
 * <b>Output</b>: LOCATOR
 * </p>
 */
public class ReferencePath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.VARIANT);
    inputSet.add(TYPE.OCCURRENCE);
  }
  
  public ReferencePath() {
    super("REF");
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
  
  public Collection<LocatorIF> evaluate(LocalContext context, Object input) {
    Collection<LocatorIF> coll = new LinkedList<LocatorIF>();

    if (input instanceof OccurrenceIF) {
      OccurrenceIF oc = (OccurrenceIF) input;
      coll.add(oc.getLocator());
    } else if (input instanceof VariantNameIF) {
      VariantNameIF var = (VariantNameIF) input;
      coll.add(var.getLocator());
    }
    
    return coll;
  }
}
