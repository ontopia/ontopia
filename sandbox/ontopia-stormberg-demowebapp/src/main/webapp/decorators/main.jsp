<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%--@ include file="/includes/cache.jsp" --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
    <title><decorator:title default="INTRANET"/></title>
    <decorator:head/>
    <%--@ include file="/includes/style.jsp" --%>
</head>
<body bgcolor="#FFFFFF" background="<%=request.getContextPath()%>/images/bg.gif">
<script type="text/javascript">window.status = "Loading: <decorator:title default="INTRANET" />...";</script>

<%--@ include file="/includes/header.jsp" --%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td height="20" nowrap></td>
    </tr>
    <tr>
        <td width="1%" nowrap></td>
        <td width="16%" valign="top" nowrap>
            <script type="text/javascript">window.status = "Loading: Navigation...";</script>
            <%@ include file="/decorators/left-panel.jspx" %>
        </td>
        <td width="2%" nowrap></td>
        <td valign="top">
            <br>
            <script type="text/javascript">window.status = "Loading: Document body...";</script>
            <div class="docBody"><decorator:body/></div>
        </td>
        <td width="1%" nowrap></td>
    </tr>
</table>
<br>
<%--@ include file="/includes/footer.jsp" --%>
<%--@ include file="/includes/copyright.jsp" --%>
<script type="text/javascript">window.status = "Done";</script>
</body>
</html>