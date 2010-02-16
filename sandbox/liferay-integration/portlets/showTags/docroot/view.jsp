<%
/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.

This Page will display the tags for an article by using the "related topics" taglibs from ontopia

 */
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/portlets" prefix="portal" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog'%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page import = "net.ontopia.topicmaps.core.TopicIF" %>
<%@ page import = "net.ontopia.topicmaps.nav2.utils.ContextUtils" %>

<portlet:defineObjects />
<tolog:context topicmap="liferay.ltm">
    <!-- TODO: Take parameter and check whether white or blacklisting should be done (use appropriate tags then)-->
    <portal:related topic="topic" var="headings" excludeAssociations="assocTypes">
      <ul>
        <c:forEach items="${headings}" var="heading">
          <li><b><c:out value="${heading.title}"/></b>
            <ul>
              <c:forEach items="${heading.children}" var="assoc">
                  <c:set value="${assoc.player}" var="player"/>
                  <%
                  // get the topic we want to display next as a TopicIF object
                  TopicIF t =(TopicIF) pageContext.getAttribute("player");
                  String oid = t.getObjectId();
                  System.out.println("ShowTags View.jsp OID: " + oid);
                  // every topic should have an url template set, so that we can see how to display it depending on its type
                  String query = "using lr for i\"http://psi.ontopia.net/liferay/\" select $value from value($occ, $value), occurrence($type, $occ), type($occ, lr:url-template), direct-instance-of(@" + oid + ", $type)?";

                  // the next two queries might be needed if we are looking at articles from webcontent
                  String queryForGroupId = "using lr for i\"http://psi.ontopia.net/liferay/\" select $value from value($occ, $value), type($occ, lr:groupid), occurrence($group, $occ), lr:contains( @"+oid+" : lr:containee , $group : lr:container )?";
                  String queryForArticleId = "using lr for i\"http://psi.ontopia.net/liferay/\" select $value from value($occ, $value), type($occ, lr:article_id), occurrence( @"+oid+", $occ)?";
                  %>
                  <!-- get url template -->
                  <tolog:set query="<%= query %>" var = "url"/>

                  <%
                  // url now contains the page we want to link, possibly containing placeholders for values we need to look up first!
                  String url = (String) ContextUtils.getSingleValue("url", pageContext);

                  System.out.println("ShowTags view.jsp URL: " + url);
                  if(url != null){
                      // if this is an article (we can tell by the url template we have received previously), we need to lookup its groupid and article id in order to present it to the user
                      if(url.contains("${groupid}") && url.contains("${articleid}")){%>
                        <tolog:set query="<%= queryForGroupId %>" var = "groupid"/>
                        <tolog:set query="<%= queryForArticleId %>" var = "articleid"/>
                        <%
                        // Then go and retrieve the appropriate values for the placeholders
                        String groupid =(String) ContextUtils.getSingleValue("groupid", pageContext);
                        String articleid = (String) ContextUtils.getSingleValue("articleid", pageContext);
                        System.out.println("ShowTags view.jsp: " + groupid + ", " + articleid);
                        // TODO: Easify using Ontopias StringTemplateUtil
                        url = url.replace("${groupid}", groupid);
                        url = url.replace("${articleid}", articleid);
                        System.out.println("URL has been replaced to : " + url );
                      } else if(url.contains("${topicid}")){
                          System.out.println("In for changing the topicid!");
                          url = url.replace("${topicid}", oid);
                      }
                      System.out.println("URL processing finished, url = " + url);
                   }
                  %>
                  <li><a href="<%= url %>"><tolog:out var="assoc.player"/></a></li>
              </c:forEach>
            </ul>
        </c:forEach>
      </ul>
    </portal:related>
</tolog:context>