<%-- 
    Document   : edit
    Created on : 12.02.2010, 11:08:21
    Author     : mfi
    
    This page will offer a way to customize the behaviour of the portlet. As of now only association type object ids can be provided to tell
    the portlet which association NOT to follow. This will become more flexible in the future.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@page import="portlet.Configurator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>

<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.RenderResponse" %>
<%@ page import="javax.portlet.RenderRequest" %>
   
<%
// need this to provide the actionUrl below. Using tags from the portlet namespace instead did not work out.
RenderResponse portletResponse = (RenderResponse)request.getAttribute("javax.portlet.response");
PortletURL actionUrl = portletResponse.createActionURL();

String assocIdsParameter = "";
String topicId;
String assocMode;
String filterQuery;

RenderRequest renderRequest = (RenderRequest) request.getAttribute("javax.portlet.request");

String[] assocIdArray = renderRequest.getPreferences().getValues("associds", null);
      if(assocIdArray != null){
        for(String s : assocIdArray){
          assocIdsParameter += s + ",";
        }

        if(assocIdsParameter.endsWith(",")){ // remove trailing comma
          assocIdsParameter = assocIdsParameter.substring(0, assocIdsParameter.length()-1);
        }
      }

      topicId = renderRequest.getPreferences().getValue("topicid", "");

      assocMode = renderRequest.getPreferences().getValue("assocmode", "");

      filterQuery = renderRequest.getPreferences().getValue("filterquery", "");

%>

<h5>Preferences:</h5>
<form name="frm_edit" action="<%=actionUrl%>" method="post">
        <table>
            <!-- TODO: Also add a radiobutton to select white/blacklisting of associations -->
            <tr><td>Topic Id: </td><td><input type="text" name="topicid" value="<%= topicId %>" /></td></tr>
            <tr><td>Association Id:</td><td><input type="text" name="associd" value="<%= assocIdsParameter %>" /></td></tr>
            <tr><td>Mode:</td><td><input type="text" name="assocmode" value="<%= assocMode %>" /></td></tr>
            <tr><td>Filterquery:</td><td><input type="text" name="filterquery" value="<%= filterQuery %>" /></td></tr>
        </table>
    <input type="submit" value="Save" name="save" />
</form>
