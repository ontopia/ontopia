/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

package net.ontopia.topicmaps.schema.impl.osl.cmdline;

import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.schema.core.TMObjectMatcherIF;
import net.ontopia.topicmaps.schema.impl.osl.TypeSpecification;
import net.ontopia.topicmaps.schema.impl.osl.ScopeSpecification;
import net.ontopia.topicmaps.schema.impl.osl.SubjectIndicatorMatcher;
import net.ontopia.topicmaps.schema.impl.osl.InternalTopicRefMatcher;


public class AbstractSchemaAnalyzer {


  /**
   * Generate the type specification for a topic
   */
  public TypeSpecification getTypeSpecification(TopicIF topic) {

    if (topic == null) return new TypeSpecification();
    LocatorIF tmbase = topic.getTopicMap().getStore().getBaseAddress();
    TypeSpecification spec = new TypeSpecification();
    
    TMObjectMatcherIF matcher = null;

    if (topic.getSubjectIdentifiers().size() > 0) {
      // try to get the subjectindicator matcher
      matcher = new SubjectIndicatorMatcher((LocatorIF) 
                                            topic.getSubjectIdentifiers().iterator().next());
    
    } else if (topic.getItemIdentifiers().size() > 0) {
      // try to get the internal topicref matcher
      LocatorIF loc = (LocatorIF)topic.getItemIdentifiers().iterator().next();
      matcher = new InternalTopicRefMatcher(getRelativeLocator(tmbase, loc));
    }
    
    if (matcher == null)
      return null;
   
    spec.setClassMatcher(matcher);
    return spec;
  }



  /**
   * Generate the scope specification for a ScopedIF object.
   */
  public ScopeSpecification getScopeSpecification(ScopedIF scoped) {

    ScopeSpecification result = new ScopeSpecification();

    Iterator it = scoped.getScope().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF)it.next();

      TMObjectMatcherIF matcher = null;
      if (topic.getSubjectIdentifiers().size() > 0) {
        Iterator iter = topic.getSubjectIdentifiers().iterator();
        while (iter.hasNext()) {
          LocatorIF locator = (LocatorIF)iter.next();
          matcher = new SubjectIndicatorMatcher(locator);
          if (matcher != null) result.addThemeMatcher(matcher);
        }
      } else if (topic.getItemIdentifiers().size() > 0) {
        Iterator iter = topic.getItemIdentifiers().iterator();
        while (iter.hasNext()) {
          LocatorIF tmbase = topic.getTopicMap().getStore().getBaseAddress();
          LocatorIF loc = (LocatorIF)iter.next();
          matcher = new InternalTopicRefMatcher(getRelativeLocator(tmbase, loc));
          if (matcher != null) result.addThemeMatcher(matcher);
        }
      }
    }
    return result;
  }







  private static String getRelativeLocator(LocatorIF base, LocatorIF relative) {
    if (base == null || !base.getNotation().equals(relative.getNotation()))
      return relative.getAddress();
    
    String basea = base.getAddress();
    String relativea = relative.getAddress();
    if (relativea.startsWith(basea))
      return relativea.substring(basea.length());
    else
      return relativea;
  }
  
  protected static String makeKey(TopicIF[] topics) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i < topics.length; i++) {
      if (i > 0)
        sb.append('$');
      sb.append(makeKey(topics[i]));
    }
    return sb.toString();
  }

  protected static String makeKey(TopicIF topic) {
    if (topic == null)
      return "";
    else
      return topic.getObjectId();
  }

  protected static String makeKey(TopicIF topic1, TopicIF topic2) {
    return makeKey(topic1) + '$' + makeKey(topic2);
  }
  
  protected static String makeKey(TopicIF topic1, TopicIF topic2, TopicIF topic3) {
    return makeKey(topic1) + '$' + makeKey(topic2) + '$' + makeKey(topic3);
  }
  
}
