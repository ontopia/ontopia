/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: A Comparator for ordering AssociationIFs alphabetically
 * after their type.
 */
public class AssociationComparator implements Comparator {

  protected Comparator tc;
  protected Collection scopes;

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare Associations using no
   * context.
   */  
  public AssociationComparator() {
    StringifierIF bts =
      new GrabberStringifier(new TopicNameGrabber(Collections.EMPTY_LIST),
                             new NameStringifier());
    tc = new StringifierComparator(new GrabberStringifier(new GrabberGrabber(new StringifierGrabber(bts), new UpperCaseGrabber())));
  }

  /**
   * Constructor used to make a comparator which will compare
   * Associations using the context provided.
   */
  public AssociationComparator(Collection context) {
    this.scopes = context;
    if (scopes == null)
      scopes = Collections.EMPTY_LIST;
    StringifierIF bts = new GrabberStringifier(new TopicNameGrabber(scopes),
                                               new NameStringifier());
    tc = new StringifierComparator(new GrabberStringifier(new GrabberGrabber(new StringifierGrabber(bts), new UpperCaseGrabber())));
  }
  
  /**
   * Compares two AssociationIFs.
   */
  public int compare(Object o1, Object o2) {
    AssociationIF a1, a2;
    try {
      a1 = (AssociationIF) o1;
      a2 = (AssociationIF) o2;
    } catch (ClassCastException e) {
      String msg = "AssociationComparator Error: " +
        "This comparator only compares AssociationIFs";
      throw new OntopiaRuntimeException(msg);
    }
    return tc.compare(a1.getType(), a2.getType());
  }
  
}





