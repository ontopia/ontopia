<%-- 

  Provides a simple interface for configuring the portlet.

--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="portlet.Configurator"%>
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

String topquery = prefs.getValue("topquery", "");
String childquery = prefs.getValue("childquery", "");
String columns = prefs.getValue("columns", "");

%>

<h5>Preferences:</h5>

<form name="frm_edit" action="<%=actionUrl%>" method="post">
<table>
<tr><td>Top query:
    <td><textarea name=topquery cols="60">
          <%= topquery %>
        </textarea>

<tr><td>Child query:
    <td><textarea name=childquery cols="60">
          <%= childquery %>
        </textarea>

<tr><td>Columns:
    <td><input name=columns value="<%= columns %>" size="2">

<tr><td colspan=2>
  <input type="submit" value="Save" name="save" />
</form>
