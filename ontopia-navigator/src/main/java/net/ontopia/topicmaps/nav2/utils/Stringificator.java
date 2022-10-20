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

package net.ontopia.topicmaps.nav2.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav.context.UserFilterContextStore;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.impl.basic.CustomNameStringifier;
import net.ontopia.topicmaps.utils.NameGrabber;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.GrabberStringifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Utility class to provide easy access to a stringified
 * representation of given topic map objects using the page context to
 * make use of the user defined scope.
 */
public final class Stringificator {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(Stringificator.class.getName());
  
  // default stringifier
  private static final Function DEF_NAME_STRINGIFIER =
    new CustomNameStringifier();
  
  public static String toString(NavigatorPageIF context, Object elem)
    throws NavigatorRuntimeException {
    return toString(context, elem, null, null, null, null);
  }
  
  public static String toString(NavigatorPageIF context, Object elem,
                                String nameGrabberCN, String nameStringifierCN,
                                String basenameScopeVarName, String variantScopeVarName) throws NavigatorRuntimeException {

    Function stringifier = null;
    Function<? extends Object, String> nameGrabber = null;
    Function<? extends Object, String> nameStringifier = null;

    if (nameStringifierCN != null) {
      nameStringifier = (Function) context.getNavigatorApplication()
        .getInstanceOf(nameStringifierCN);
    } else {
      nameStringifier = DEF_NAME_STRINGIFIER;
    }

    if (nameGrabberCN != null) {
      nameGrabber = (Function) context.getNavigatorApplication()
        .getInstanceOf(nameGrabberCN);
    }
    
    // --- stringifier for topic
    if (elem instanceof TopicIF) {
      TopicMapIF topicmap = context.getTopicMap();
      UserIF user = FrameworkUtils.getUser(context.getPageContext());
      UserFilterContextStore filterContext = user.getFilterContext();
      if ((filterContext == null ||
           (filterContext.getScopeTopicNames(topicmap).isEmpty() &&
            filterContext.getScopeVariantNames(topicmap).isEmpty())) &&
          basenameScopeVarName == null &&
          variantScopeVarName == null) {
        // ie: if this is one of the 99% most common cases
        stringifier = new FastStringifier(topicmap);
      } else {
        List basenameScope = new ArrayList();
        List variantScope = new ArrayList();
        // prepare-scope-1: add themes that are in user context filter
        if (filterContext != null) {
          if (topicmap == null) { 
            // FIXME: shouldn't we really throw an exception instead?
            log.warn("No topic map in context.");
          } else {
            basenameScope.addAll(filterContext.getScopeTopicNames(topicmap));
            variantScope.addAll(filterContext.getScopeVariantNames(topicmap));
          }
        }
        // prepare-scope-2: add themes that are specified by attribute scope
        if (basenameScopeVarName != null) {
          basenameScope.addAll(context.getContextManager().getValue(basenameScopeVarName));
        }

        if (variantScopeVarName != null) {
          variantScope.addAll(context.getContextManager().getValue(variantScopeVarName));
        }
      
        // Add display theme if exists (bug #670)
        TopicIF display_theme = topicmap.getTopicBySubjectIdentifier(PSI.getXTMDisplay());
        if (display_theme != null) {
          variantScope.add(display_theme);
        }

        // if name should be grabbed with the custom grabber
        if (nameGrabber != null) {
          // TODO: how to make use of the scope using the custom grabber
          stringifier = new GrabberStringifier(nameGrabber,
                                               nameStringifier);
        }
        else {
          // name should be grabbed in accordance to scope
          stringifier = new GrabberStringifier(new NameGrabber(basenameScope, variantScope, false),
                                               nameStringifier);
        }
      }
    }
    // --- base name or variant
    else if (elem instanceof TopicNameIF) {
      if (nameGrabber != null) {
        stringifier = new GrabberStringifier(nameGrabber, nameStringifier);
      } else {
        stringifier = nameStringifier;
      }
    }
    else if (elem instanceof VariantNameIF) {
      if (nameGrabber != null) {
        stringifier = new GrabberStringifier(nameGrabber, nameStringifier);
      } else {
        stringifier = nameStringifier;
      }
    }
    // --- TopicMapReferenceIF
    else if (elem instanceof TopicMapReferenceIF) {
      return ((TopicMapReferenceIF) elem).getTitle();
    }
    // --- String
    else if (elem instanceof String) {
      return (String) elem;
    }    
    // --- otherwise signal error
    else {
      StringBuilder msg =
        new StringBuilder("Expected collection which contains topics," +
                         " base names, variants or topic map refs as elements.\n");
      if (elem != null) {
        msg.append("But got instance of class " +
                elem.getClass().getName() + ". ");
      } else {
        msg.append("First element in input collection is null. ");
      }
      
      log.error(msg.toString());
      throw new NavigatorRuntimeException(msg.toString());
    }

    // set custom strings to display for fail situations
    if (stringifier instanceof CustomNameStringifier) {
      NavigatorConfigurationIF navConf = context.getNavigatorApplication()
        .getConfiguration();
      String str = null;
      // --- nonexistent
      str = navConf.getProperty(NavigatorConfigurationIF.NAMESTRING_NONEXISTENT, null);
      if (str != null) {
        ((CustomNameStringifier) stringifier).setStringNonExistent(str);
      }
      // --- null value
      str = navConf.getProperty(NavigatorConfigurationIF.NAMESTRING_NULLVALUE, null);
      if (str != null) {
        ((CustomNameStringifier) stringifier).setStringValueNull(str);
      }
      // --- empty value
      str = navConf.getProperty(NavigatorConfigurationIF.NAMESTRING_EMPTYVALUE, null);
      if (str != null) {
        ((CustomNameStringifier) stringifier).setStringValueEmpty(str);
      }
    }

    return (String) stringifier.apply(elem);
  }

  // --- Fast stringifier (optimization for Nokia)

  static class FastStringifier extends CustomNameStringifier {
    private TopicIF vntheme;
    private TopicIF defnametype;
  
    public FastStringifier(TopicMapIF topicmap) {
      this.defnametype = topicmap.getTopicBySubjectIdentifier(PSI.getSAMNameType());
      this.vntheme = topicmap.getTopicBySubjectIdentifier(PSI.getXTMDisplay());
    }

    protected String getValue(String value) {
      if (value == null) {
        return stringValueNull;
      } else if (value.isEmpty()) {
        return stringValueEmpty;
      } else {
        return value;
      }
    }
    
    @Override
    public String apply(Object object) {
      // 0: verify that we have a topic at all
      if (object == null) {
        return stringNonExistent;
      }
      TopicIF topic = (TopicIF) object;

      // 1: pick base name with the fewest topics in scope
      TopicNameIF bn = null;
      int bn_least = 0xEFFF;
      Iterator it = topic.getTopicNames().iterator();
      while (it.hasNext()) {
       TopicNameIF candidate = (TopicNameIF) it.next();
        int themes = candidate.getScope().size();
        if (candidate.getType() == defnametype) {
          themes += Integer.MIN_VALUE; // prefer default name type
       }
        if (themes < bn_least) {
          bn = candidate;
          bn_least = themes;
        }
      }
      if (bn == null) {
        return stringNonExistent;
      }
      
      // 2: if we have a vntheme, pick variant with fewest topics in scope
      //    beyond vntheme; penalty for no vntheme = 0xFF topics
      if (vntheme == null) {
        return getValue(bn.getValue());
      }
      VariantNameIF vn = null;
      int vn_least = 0xEFFF;
      it = bn.getVariants().iterator();
      while (it.hasNext()) {
        VariantNameIF candidate = (VariantNameIF) it.next();
        Collection scope = candidate.getScope();
        int themes;
        if (scope.contains(vntheme)) {
          themes = scope.size() - 1;
          if (themes < vn_least) {
            vn = candidate;
            vn_least = themes;
          }
        }
      }
      if (vn == null || vn.getValue() == null) {
        return getValue(bn.getValue());
      }
      return getValue(vn.getValue());
    }
  }  
}
