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

package net.ontopia.utils.ontojsp;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A TaglibTagFactory that creates the correct tags from the
 * <code>net.ontopia.nav2.taglibs</code> packages.  Used for the
 * implentation of the call tag and internally for testing.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.CallTag
 */
public class TaglibTagFactory implements JSPTagFactoryIF {

  // tagPool = <parentTag, <poolKey, pooledTag>>
  private Map<TagSupport, Map<String, TagSupport>> tagPool;
  private final boolean useTagPooling;
  public static final boolean TAGPOOLING_DEFAULT = false;

  // initialize logging facility
  private static final Logger log = LoggerFactory.getLogger(TaglibTagFactory.class.getName());

  // Map between tag names (with namespace prefix) and Java Classes
  private static Map<String, String> classes;

  public TaglibTagFactory() {
    this(TAGPOOLING_DEFAULT);
  }
  public TaglibTagFactory(boolean useTagPooling) {
    this.useTagPooling = useTagPooling;
    if (classes == null) {
      initClassMap();
    }
  }

  public static String getTagName(Class tagclass) {
    String classname = tagclass.getName();
    for (String tagname : classes.keySet()) {
      if (classes.get(tagname).equals(classname)) {
        return tagname;
      }
    }
    return "unresolved tag: " + classname;
  }

  public static boolean isKnownTag(String tagname) {
    if (classes == null) {
      initClassMap();
    }
    return (classes.get(tagname) != null);
  }

  @Override
  public TagSupport getTagInstance(String tagname, Map<String, String> attrVals,
                                   TagSupport parentTag)
    throws NavigatorRuntimeException {

    if (!isKnownTag(tagname)) {
      throw new NavigatorRuntimeException("TaglibTagFactory - " +
                                          "Unknown tag: " + tagname);
    }

    if (useTagPooling) {
      if (tagPool == null) { tagPool = new HashMap<TagSupport, Map<String, TagSupport>>(); }
      Map<String, TagSupport> poolEntry = tagPool.get(parentTag);
      String poolKey = getTagPoolingKey(tagname, attrVals);
      log.debug("Looking up tag with key '" + poolKey + "' in tag pool for parent tag " + parentTag);
      if (poolEntry == null) {
        poolEntry = new HashMap<String, TagSupport>();
        tagPool.put(parentTag, poolEntry);
      } else {
        TagSupport pooledTag = poolEntry.get(poolKey);
        if (pooledTag != null) {
          log.debug("Found matching tag in pool, reusing " + pooledTag);
          return pooledTag;
        }
      }
    }

    // create tag instance
    String classname = classes.get(tagname);
    TagSupport tag = null;
    try {
      // try to get class for tag classname
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class tagclass = Class.forName(classname, true, classLoader);
      // try to instaniate an object of tag class
      tag = (TagSupport) tagclass.newInstance();
    } catch (Exception e) {
      String msg = "TaglibTagFactory - unable to create instance of class " +
        classname + " for tag " + tagname;
      throw new NavigatorRuntimeException(msg, e);
    }

    if (useTagPooling) {
      Map<String, TagSupport> poolEntry = tagPool.get(parentTag);
      String poolKey = getTagPoolingKey(tagname, attrVals);
      log.debug("Found no tag in pool, storing tag " + tag);
      poolEntry.put(poolKey, tag);
    }

    return tag;
  }


  // --- internal helper method

  private String getTagPoolingKey(String tagname, Map<String, String> attrVals) {
    return tagname + " " + StringUtils.join(attrVals.keySet(), " ");
  }

  private static void initClassMap() {
    classes = new HashMap<String, String>();
    // package base name for all tag classes
    String tlpackage = "net.ontopia.topicmaps.nav2.taglibs.";
    String ptlpackage = "net.ontopia.topicmaps.nav2.portlets.taglib.";
    String core = "org.apache.taglibs.standard.tag.el.core.";

    // setup class <-> tagname mapping
    classes.put("tm:associated", tlpackage + "TMvalue.AssociatedTag");
    classes.put("tm:associations", tlpackage + "TMvalue.AssociationsTag");
    classes.put("tm:associationTypeLoop", tlpackage + "TMvalue.AssociationTypeLoopTag");
    classes.put("tm:classesOf", tlpackage + "TMvalue.ClassesOfTag");
    classes.put("tm:classes", tlpackage + "TMvalue.ClassesTag");
    classes.put("tm:filter", tlpackage + "TMvalue.FilterTag");
    classes.put("tm:subjectAddress", tlpackage + "TMvalue.SubjectAddressTag");
    classes.put("tm:indicators", tlpackage + "TMvalue.IndicatorsTag");
    classes.put("tm:instances", tlpackage + "TMvalue.InstancesTag");
    classes.put("tm:locator", tlpackage + "TMvalue.LocatorTag");
    classes.put("tm:lookup", tlpackage + "TMvalue.LookupTag");
    classes.put("tm:name", tlpackage + "TMvalue.NameTag");
    classes.put("tm:names", tlpackage + "TMvalue.NamesTag");
    classes.put("tm:occurrences", tlpackage + "TMvalue.OccurrencesTag");
    classes.put("tm:reifier", tlpackage + "TMvalue.ReifierTag");
    classes.put("tm:reified", tlpackage + "TMvalue.ReifiedTag");
    classes.put("tm:roles", tlpackage + "TMvalue.RolesTag");
    classes.put("tm:scope", tlpackage + "TMvalue.ScopeTag");
    classes.put("tm:splitter", tlpackage + "TMvalue.SplitterTag");
    classes.put("tm:sourceLocators", tlpackage + "TMvalue.SourceLocatorsTag");
    classes.put("tm:subclasses", tlpackage + "TMvalue.SubclassesTag");
    classes.put("tm:superclasses", tlpackage + "TMvalue.SuperclassesTag");
    classes.put("tm:topics", tlpackage + "TMvalue.TopicsTag");
    classes.put("tm:themes", tlpackage + "TMvalue.ThemesTag");
    classes.put("tm:variants", tlpackage + "TMvalue.VariantsTag");
    classes.put("tm:tolog", tlpackage + "TMvalue.TologQueryTag");
    classes.put("logic:call", tlpackage + "logic.CallTag");
    classes.put("logic:context", tlpackage + "logic.ContextTag");
    classes.put("logic:error", tlpackage + "logic.ErrorTag");
    classes.put("logic:foreach", tlpackage + "logic.ForEachTag");
    // deprecated: ("logic:function", tlpackage + "logic.FunctionTag");
    classes.put("logic:include", tlpackage + "logic.IncludeTag");
    classes.put("logic:externalFunction", tlpackage + "logic.ExternalFunctionTag");
    classes.put("logic:if", tlpackage + "logic.IfTag");
    classes.put("logic:then", tlpackage + "logic.IfThenTag");
    classes.put("logic:else", tlpackage + "logic.IfElseTag");
    classes.put("logic:set", tlpackage + "logic.SetTag");
    classes.put("logic:bind", tlpackage + "logic.BindTag");
    classes.put("output:content", tlpackage + "output.ContentTag");
    classes.put("output:count", tlpackage + "output.CountTag");
    classes.put("output:link", tlpackage + "output.LinkTag");
    classes.put("output:name", tlpackage + "output.NameTag");
    classes.put("output:objectid", tlpackage + "output.ObjectIdTag");
    classes.put("output:property", tlpackage + "output.PropertyTag");
    classes.put("output:locator", tlpackage + "output.LocatorTag");
    classes.put("output:element", tlpackage + "output.ElementTag");
    classes.put("output:attribute", tlpackage + "output.AttributeTag");
    // classes.put("output:treediagram", tlpackage + "output.TreeDiagramTag");
    classes.put("output:id", tlpackage + "output.SymbolicIdTag");
    classes.put("output:debug", tlpackage + "output.DebugTag");
    classes.put("value:copy", tlpackage + "value.CopyTag");
    classes.put("value:difference", tlpackage + "value.DifferenceTag");
    classes.put("value:intersection", tlpackage + "value.IntersectionTag");
    classes.put("value:string", tlpackage + "value.StringTag");
    classes.put("value:union", tlpackage + "value.UnionTag");
    classes.put("value:sequence", tlpackage + "value.SequenceTag");
    classes.put("framework:getcontext", tlpackage + "framework.GetContextTag");
    classes.put("framework:setcontext", tlpackage + "framework.SetContextTag");
    classes.put("framework:pluginList", tlpackage + "framework.PluginListTag");

    classes.put("tolog:choose", tlpackage + "tolog.ChooseTag");
    classes.put("tolog:context", tlpackage + "logic.ContextTag");
    classes.put("tolog:declare", tlpackage + "tolog.DeclareTag");
    classes.put("tolog:foreach", tlpackage + "tolog.ForEachTag");
    classes.put("tolog:id", tlpackage + "tolog.SymbolicIdTag");
    classes.put("tolog:if", tlpackage + "tolog.IfTag");
    classes.put("tolog:otherwise", tlpackage + "tolog.OtherwiseTag");
    classes.put("tolog:oid", tlpackage + "tolog.ObjectIdTag");
    classes.put("tolog:out", tlpackage + "tolog.OutTag");
    classes.put("tolog:query", tlpackage + "tolog.QueryTag");
    classes.put("tolog:set", tlpackage + "tolog.SetTag");
    classes.put("tolog:when", tlpackage + "tolog.WhenTag");
    classes.put("tolog:normalize", tlpackage + "tolog.NormalizeWhitespaceTag");

    classes.put("portlet:related", ptlpackage + "RelatedTag");

    classes.put("c:forEach", core + "ForEachTag");
    classes.put("c:out", core + "OutTag");

    classes.put("fmt:message", "org.apache.taglibs.standard.tag.el.fmt.MessageTag");
  }

}
