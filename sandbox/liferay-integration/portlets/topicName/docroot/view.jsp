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
 */
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import = "com.liferay.portal.util.PortalUtil" %>
<%@page import = "java.util.Enumeration" %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog'%>

<portlet:defineObjects />
<tolog:context topicmap="liferay.ltm">
<%
String renderurl = (String)request.getAttribute("renderurl");
String topicid = (String) request.getAttribute("topic");
if(topicid == null){
    out.println("Please provide a topic id to this url: " + renderurl);
}
String query = "object-id($topic, \"" + topicid + "\")?";
%>

    <tolog:set var="topic" query="<%=query%>"/>
        This is the page for the topic:
        <h3><tolog:out var="topic"/></h3>
        <tolog:set var="psi" query="subject-identifier(%topic%, $psi)?" />
        Read more on:
        <a href="<tolog:out var="psi"/>"><tolog:out var="psi"/></a>
</tolog:context>