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

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TMObjectIF;

import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all the source locators
 * (as LocatorIF objects) of all the topic map objects in a collection.
 */
public class SourceLocatorsTag extends BaseValueProducingAndAcceptingTag {

  @Override
  public Collection process(Collection tmObjs) throws JspTagException {
    // find all source locators of all topic map objects in collection
    if (tmObjs == null || tmObjs.isEmpty()) {
      return Collections.EMPTY_SET;
    } else {
      List sourceLocators = new ArrayList();
      Iterator iter = tmObjs.iterator();
      
      while (iter.hasNext()) {
        try {
          TMObjectIF tmObj = (TMObjectIF) iter.next();
          // get all source locators as LocatorIF objects
          if (tmObj != null) {
            sourceLocators.addAll( tmObj.getItemIdentifiers() );
          }
        } catch (ClassCastException e) {
        }
      }
      return sourceLocators;
    }
  }

}





