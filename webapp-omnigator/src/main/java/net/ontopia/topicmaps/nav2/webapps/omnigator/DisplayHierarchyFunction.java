/*
 * #!
 * Ontopia Omnigator webapplication
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

package net.ontopia.topicmaps.nav2.webapps.omnigator;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.impl.basic.JSPEngineWrapper;
import net.ontopia.topicmaps.nav2.impl.basic.AbstractFunction;
import net.ontopia.topicmaps.nav2.utils.ContextUtils;
import net.ontopia.topicmaps.nav2.utils.TreeWidget;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * INTERNAL: Used by the Omnigator to create the collapsible tree view
 * of hierarchical association types.
 */
public class DisplayHierarchyFunction extends AbstractFunction {
  
  @Override
  public Collection execute(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException {

    ServletRequest request = pageContext.getRequest();
    JspWriter out = pageContext.getOut();
    
    String id = request.getParameter("id");
    String tmid = request.getParameter("tm");
    
    TopicIF atype = (TopicIF) ContextUtils.getSingleValue("topic", pageContext);
    TopicMapIF topicmap = atype.getTopicMap();
    
    QueryProcessorIF processor = QueryUtils.getQueryProcessor(topicmap);
    
    String a = atype.getObjectId();

    TopicIF parent;
    TopicIF child;

    try {
      parent = query(processor,
        "using h for i\"http://www.techquila.com/psi/hierarchy/#\" " +
        "select $RTYPE from " +
        "  instance-of($RTYPE, h:superordinate-role-type), " +
        "  type($ROLE, $RTYPE), " +
        "  association-role($ASSOC, $ROLE), " +
        "  type($ASSOC, @" + a + ") " +
        "limit 1?");
      
      child = query(processor,
        "using h for i\"http://www.techquila.com/psi/hierarchy/#\" " +
        "select $RTYPE from " +
        "  instance-of($RTYPE, h:subordinate-role-type), " +
        "  type($ROLE, $RTYPE), " +
        "  association-role($ASSOC, $ROLE), " +
        "  type($ASSOC, @" + a + ") " +
        "limit 1?");
    } catch (InvalidQueryException e) {
      out.write("<p><b>Error:</b> Missing PSI for parent or child role type.</p>");
      return null;
    } 
      

    if (parent == null || child == null) {
      out.write("<p><b>Error:</b> Missing parent or child role.</p>");
      return null;
    }
    
    String p = parent.getObjectId();
    String c = child.getObjectId();

    String topquery =
      "select $PLAYER from " +
      "  @" + a + "($PLAYER : @" + p + ", $CHILD : @" + c + "), " +
      "  not(@" + a + "($PLAYER : @" + c + ", $PARENT : @" + p + ")) " +
      "order by $PLAYER?";
 
    String query =
      "  @" + a + "(%parent% : @" + p + ", $CHILD : @" + c + ") " +
      "order by $CHILD?";

    try {
      TreeWidget widget = new TreeWidget(topicmap, query, topquery, 
                                         "?id=" + id + "&tm=" + tmid + "&", 
                                         "topic_complete.jsp?tm=" + tmid + "&");
      widget.setWidgetName("/omnigator/" + tmid);
      widget.setImageUrl("/omnigator/images/");
      widget.run(pageContext, out);
    } catch (InvalidQueryException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    } catch (IOException e) {
      throw JSPEngineWrapper.getJspException("IOException in TreeWidget", e);
    } catch (NavigatorRuntimeException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    }

    return null;
  }

  // --- Internal methods

  private TopicIF query(QueryProcessorIF processor, String query)
    throws InvalidQueryException {
    QueryResultIF result = null;
    try {
      result = processor.execute(query);
      if (result.next()) {
        return (TopicIF) result.getValue(0);
      } else {
        return null;
      }
    } finally {
      if (result != null) {
        result.close();
      }
    }
  }
  
}
