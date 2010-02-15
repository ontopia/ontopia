<%-- 
    Document   : edit
    Created on : 12.02.2010, 11:08:21
    Author     : mfi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="portlet.Configurator"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

   <%
   String topicId = request.getParameter("topicId");
   String assocId = request.getParameter("assocId");
   String renderUrl = (String) request.getAttribute("renderUrl");

   if(topicId != null){
    Configurator.instance.setTopicId(topicId);
   }
   if(assocId != null){
    Configurator.instance.setAssoctype(assocId);
   }

   %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Preferences</title>
    </head>
    <body>
        <h5><%=renderUrl%></h5>
        <form name="frm_edit" action="<portlet:actionURL><portlet:param name="topicId" value="2800" /><portlet:param name="assocId" value="419" /></portlet:actionURL>">
                <tr><td>Topic Id: </td><td><input type="text" name="topicId" value="<%= Configurator.instance.getTopicId()%>" /></td></tr>
                <tr><td>Association Id:</td><td><input type="text" name="assocId" value="<%= Configurator.instance.getAssocOid()%>" /></td></tr>
            </table>
            <input type="submit" value="Save" name="save" />
        </form>
    </body>
</html>
