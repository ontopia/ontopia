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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">


<%
String topicid= null;
Set associd = null;

topicid = (String) request.getAttribute("topicid");
associd = (Set) request.getAttribute("associd");

String assocsString = "";
// if a set has been passed, make it readable as a String.
if(associd != null){
    Iterator associationsIterator = associd.iterator();
    while(associationsIterator.hasNext()){
        assocsString += (String) associationsIterator.next();
        if(associationsIterator.hasNext()){
            assocsString += ",";
        }
    }
}
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Preferences</title>
    </head>
    <body>
        <h5>Preferences:</h5>
        <form name="frm_edit" method="post">
                <table>
                    <!-- TODO: Also add a radiobutton to select white/blacklisting of associations -->
                    <!-- TODO: <tr><td>Topic Id: </td><td><input type="text" name="<portlet:namespace/>topicid" value="<%= topicid%>" /></td></tr> -->
                    <tr><td>Association Id:</td><td><input type="text" name="<portlet:namespace/>associd" value="<%= assocsString %>" /></td></tr>
                </table>
            <input type="submit" value="Save" name="save" />
        </form>
    </body>
</html>
