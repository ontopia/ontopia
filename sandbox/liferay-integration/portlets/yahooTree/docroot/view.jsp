<%@page import="java.util.*,
                net.ontopia.utils.StringifierIF,
                net.ontopia.topicmaps.utils.*,
		net.ontopia.topicmaps.nav2.portlets.pojos.YahooTree,
                net.ontopia.topicmaps.core.*,
                javax.portlet.PortletPreferences"%>
<%
// View Jsp for YahooTreePortlet. Shows the top two levels of a hierarchy,
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<table class="yahoo-tree">
<%
  PortletPreferences prefs = renderRequest.getPreferences();
  TopicMapIF topicmap = tm.OntopiaAdapter.getInstance(true).getTopicMap();
  StringifierIF strify = TopicStringifiers.getDefaultStringifier();

  YahooTree yt = new YahooTree();
  if (prefs.getValue("topquery", "").equals("") ||
      prefs.getValue("childquery", "").equals("")) {
%>
    <p>Portlet not configured.
    </table>
<%
    return;
  }
  yt.setTopQuery(prefs.getValue("topquery", ""));
  yt.setChildQuery(prefs.getValue("childquery", ""));
  yt.setColumns(Integer.parseInt(prefs.getValue("columns", "5")));

  List<List<YahooTree.TreeNode>> rows = yt.makeModel(topicmap);
  for (int rowno = 0; rowno < rows.size(); rowno++) {
    List<YahooTree.TreeNode> row = rows.get(rowno);
%>
  <tr>
<%  
    int colno;
    for (colno = 0; colno < row.size(); colno++) {
      YahooTree.TreeNode node = row.get(colno);
%>
    <td>
      <div class=toplevel
          ><a href="..."><%= strify.toString(node.getTopic()) %></a></div>
<%
      for (TopicIF child : node.getChildren()) {
%>
          <a href="..."><%= strify.toString(child) %></a></div>
<%
      }
    }

    // producing empty columns to fill out last row, if necessary
    for (; colno < yt.getColumns(); colno++) {   
%>
    <td>&nbsp;
<%
    }
  }
%>
</table>

