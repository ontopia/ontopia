<%-- 

  Configuration page for the portlet. Parameters are: query, templateid.

--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.RenderResponse" %>
<%@ page import="javax.portlet.RenderRequest" %>
<%@ page import="javax.portlet.PortletPreferences" %>
   
<%
RenderResponse portletResponse = (RenderResponse)request.getAttribute("javax.portlet.response");
PortletURL actionUrl = portletResponse.createActionURL();

RenderRequest renderRequest = (RenderRequest) request.getAttribute("javax.portlet.request");
PortletPreferences prefs = renderRequest.getPreferences();

String query = prefs.getValue("query", "");
String templateid = prefs.getValue("templateid", "");

%>

<h5>Preferences:</h5>

<form name="frm_edit" action="<%=actionUrl%>" method="post">
<table>
<tr><td>Query:
    <td><textarea name=query cols="60"><%= query %></textarea>

<tr><td>Template ID:
    <td><input name=templateid value="<%= templateid %>" size="10">

<tr><td colspan=2>
  <input type="submit" value="Save" name="save" />
</form>
