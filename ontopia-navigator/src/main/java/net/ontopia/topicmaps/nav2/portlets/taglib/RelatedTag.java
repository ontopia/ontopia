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

package net.ontopia.topicmaps.nav2.portlets.taglib;

import java.util.List;
import java.util.Collection;
import java.util.Set;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.portlets.pojos.RelatedTopics;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.CompactHashSet;

public class RelatedTag extends TagSupport {
  private RelatedTopics related;
  private String var;
  private String topic;
  private String hideassocs;
  private String exclassocs;
  private String exclroles;
  private String excltopics;
  private String inclassocs;
  private String incltopics;
  private String filterQuery;
  private String headingOrderQuery;
  private int headingOrdering = RelatedTopics.ORDERING_ASC;
  private String childOrderQuery;
  private int childOrdering = RelatedTopics.ORDERING_ASC;
  private boolean aggregateHierarchy;
  private String aggregateAssociations;
  private int maxChildren = -1;
  
  @Override
  public int doStartTag() throws JspTagException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    if (related == null) {
      buildModel(contextTag);
    }

    TopicIF topic = (TopicIF) getVariableValue(this.topic);
    if (topic == null) {
      throw new JspTagException("Couldn't find topic '" + topic + "'");
    }
    List headings = related.makeModel(topic);
    pageContext.setAttribute(var, headings, PageContext.REQUEST_SCOPE);

    // FIXME: make new scope here
    if (contextTag != null) {
      contextTag.getContextManager().setValue(var, headings);
    }
    
    return EVAL_BODY_INCLUDE;
  }

  @Override
  public int doEndTag() throws JspException {
    related = null; // without this line config changes are never picked up
                    // need to think about how/whether to make this more
                    // efficient
    return EVAL_PAGE;
  }
  
  @Override
  public void release() {
    this.related = null;
    this.var = null;
    this.topic = null;
    this.hideassocs = null;
    this.exclassocs = null;
    this.exclroles = null;
    this.excltopics = null;
    this.inclassocs = null;
    this.incltopics = null;
    this.filterQuery = null;
    this.headingOrderQuery = null;
    this.headingOrdering = RelatedTopics.ORDERING_ASC;
    this.childOrderQuery = null;
    this.childOrdering = RelatedTopics.ORDERING_ASC;
    this.aggregateHierarchy = false;
    this.aggregateAssociations = null;
    this.maxChildren = -1;
  }

  private boolean isEmpty(String value) {
    return (value == null || value.trim().equals(""));
  }
  
  // --- Setters

  public void setVar(String var) {
    if (isEmpty(var)) {
      this.var = null;
    } else {
      this.var = var;
    }
  }

  public void setTopic(String topic) {
    if (isEmpty(topic)) {
      this.topic = null;
    } else {
      this.topic = topic;
    }
  }

  public void setHideAssociations(String hideassocs) {
    if (isEmpty(hideassocs)) {
      this.hideassocs = null;
    } else {
      this.hideassocs = hideassocs;
    }
  }

  public void setExcludeAssociations(String exclassocs) {
    if (isEmpty(exclassocs)) {
      this.exclassocs = null;
    } else {
      this.exclassocs = exclassocs;
    }
  }

  public void setExcludeRoles(String exclroles) {
    if (isEmpty(exclroles)) {
      this.exclroles = null;
    } else {
      this.exclroles = exclroles;
    }
  }

  public void setExcludeTopics(String excltopics) {
    if (isEmpty(excltopics)) {
      this.excltopics = null;
    } else {
      this.excltopics = excltopics;
    }
  }

  public void setIncludeAssociations(String inclassocs) {
    if (isEmpty(inclassocs)) {
      this.inclassocs = null;
    } else {
      this.inclassocs = inclassocs;
    }
  }

  public void setIncludeTopics(String incltopics) {
    if (isEmpty(incltopics)) {
      this.incltopics = null;
    } else {
      this.incltopics = incltopics;
    }
  }

  public void setFilterQuery(String filterQuery) {
    if (isEmpty(filterQuery)) {
      this.filterQuery = null;
    } else {
      this.filterQuery = filterQuery;
    }
  }

  public void setMaxChildren(int maxChildren) {
    this.maxChildren = maxChildren;
  }

  public void setHeadingOrderQuery(String headingOrderQuery) {
    if (isEmpty(headingOrderQuery)) {
      this.headingOrderQuery = null;
    } else {
      this.headingOrderQuery = headingOrderQuery;
    }
  }

  public void setHeadingOrdering(String headingOrdering) {
    if (headingOrdering != null && headingOrdering.equalsIgnoreCase("desc")) {
      this.headingOrdering = RelatedTopics.ORDERING_DESC;
    } else {
      this.headingOrdering = RelatedTopics.ORDERING_ASC;
    }
  }

  public void setChildOrderQuery(String childOrderQuery) {
    if (isEmpty(childOrderQuery)) {
      this.childOrderQuery = null;
    } else {
      this.childOrderQuery = childOrderQuery;
    }
  }

  public void setChildOrdering(String childOrdering) {
    if (childOrdering != null && childOrdering.equalsIgnoreCase("desc")) {
      this.childOrdering = RelatedTopics.ORDERING_DESC;
    } else {
      this.childOrdering = RelatedTopics.ORDERING_ASC;
    }
  }

  public void setAggregateHierarchy(boolean aggregateHierarchy) {
    this.aggregateHierarchy = aggregateHierarchy;
  }

  public void setAggregateAssociations(String aggregateAssociations) {
    if (isEmpty(aggregateAssociations)) {
      this.aggregateAssociations = null;
    } else {
      this.aggregateAssociations = aggregateAssociations;
    }
  }
  
  // --- Internal

  private Object getVariableValue(String var) {
    // first try to access an OKS variable
    try {
      Collection coll;
      ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

      if (contextTag != null) {
        coll = contextTag.getContextManager().getValue(var);
        // FIXME: what if it's empty?
        return coll.iterator().next();
      }
    } catch (VariableNotSetException e) {
      // this is OK; we just move on to trying the page context
    }
    
    return InteractionELSupport.getValue(var, pageContext);
  }
  
  private void buildModel(ContextTag context) {
    related = new RelatedTopics();
    if (context != null) {
      related.setTologContext(context.getDeclarationContext());
    }
    related.setWeakAssociationTypes(getUnionOfVariables(hideassocs));
    related.setExcludeAssociationTypes(getUnionOfVariables(exclassocs));
    related.setExcludeRoleTypes(getUnionOfVariables(exclroles));
    related.setExcludeTopicTypes(getUnionOfVariables(excltopics));
    related.setIncludeAssociationTypes(getUnionOfVariables(inclassocs));
    related.setIncludeTopicTypes(getUnionOfVariables(incltopics));
    related.setFilterQuery(filterQuery);
    related.setMaxChildren(maxChildren);
    related.setHeadingOrderQuery(headingOrderQuery);
    related.setHeadingOrdering(headingOrdering);
    related.setChildOrderQuery(childOrderQuery);
    related.setChildOrdering(childOrdering);
    related.setAggregateHierarchy(aggregateHierarchy);
    related.setAggregateAssociations(getUnionOfVariables(aggregateAssociations));
  }

  private Set getUnionOfVariables(String config) {
    if (config == null) {
      return null;
    }
    try {
      List values = FrameworkUtils.evaluateParameterList(pageContext, config);
      if (values.isEmpty()) {
        return null;
      }
      Set result = new CompactHashSet();
      for (int i=0; i < values.size(); i++) {
        Collection v = (Collection)values.get(i);
        if (v != null) {
          result.addAll(v);
        }
      }    
      return result;
    } catch (Throwable t) {
      throw new OntopiaRuntimeException(t);
    }
  }
  
}
