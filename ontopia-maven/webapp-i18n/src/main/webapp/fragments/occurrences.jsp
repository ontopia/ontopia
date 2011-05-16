<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'  prefix='tolog'      %>

<tolog:if query="occurrence(%topic%, $OCC), resource($OCC, $URL)?">

<h2>More information</h2>

<table>
<tolog:foreach query="occurrence(%topic%, $OCC),
                      type($OCC, $OCCTYPE),
                      resource($OCC, $URL)
                      order by $OCCTYPE, $URL?">
  <%--logic:set name="scope"><tm:scope/></logic:set--%>

<tr><td><b><tolog:out var="OCCTYPE"/>:&#160;&#160;&#160;</b></td>
    <td><a href="<tolog:out var="URL"/>"><tolog:out var="URL"/></a>&#160;&#160;&#160;
    <%--td><logic:foreach name="scope" separator=", "><output:name/></logic:foreach--%>
</tolog:foreach>
</table>

</tolog:if>