<%@ include file="../fragment/common-header.jsp" %>

<template:insert template='accessctl-template.jsp'>
<template:put name="title" body="true">
  <fmt:message key="Privileges"/>
</template:put>
<template:put name="content" body="true">

<tolog:context topicmap='<%= request.getParameter("tm") %>'>
  <webed:form actiongroup="privileges">
    <tolog:declare>
      using userman for i"http://psi.ontopia.net/userman/"
    </tolog:declare>
    <tolog:set var="topicmap" query="select $TM from topicmap($TM)?"/>
    <tolog:set var="ltmFragment">
      [%new% : privilege = "<fmt:message key="NewPrivilege"/>"]
      {%new%, privilege, [[<fmt:message key="NewPrivilege"/>]]}
    </tolog:set>

    <c:set var="CreateNewPrivilege">
      <fmt:message key="CreateNewPrivilege"/>
    </c:set>
    <input type="hidden" name="language" value="<%= language %>">
    <webed:button action="create" id="create"
        text='<%= pageContext.getAttribute("CreateNewPrivilege").toString() %>' 
            params="topicmap ltmFragment"/>
    <TABLE>
      <c:set var="Delete"><fmt:message key="Delete"/></c:set>
      <tolog:foreach query="instance-of($PRIVILEGE, userman:privilege) order by $PRIVILEGE?">
        <TR><TD>
          <A HREF="privilege.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language%>&old&ag=privileges&id=<tolog:id var="PRIVILEGE"/>">
            <tolog:out var="PRIVILEGE"/>
          </A>
        </TD><TD>
          <tolog:query name="canDeleteQuery">
            %PRIVILEGE% /= userman:priv-admin-users?
          </tolog:query>
          <tolog:choose><tolog:when query="canDeleteQuery">
            <input type="hidden" name="language" value="<%= language %>">
            <webed:button action="delete" params="PRIVILEGE" 
                text='<%= pageContext.getAttribute("Delete").toString() %>'/>
          </tolog:when><tolog:otherwise>
            <input type=button id="delete<tolog:out var='sequence-number'/>"
                onclick='alert("<fmt:message key="CannotDeletePrivilege"/>")'
                value="<%= pageContext.getAttribute("Delete").toString() %>">
          </tolog:otherwise></tolog:choose>
        </TD></TR>
      </tolog:foreach>
    </TABLE>
  </webed:form>
</tolog:context>

<script type="text/javascript">
  function showMessage(String message) {
    alert(message);
  }
</script>
  
</template:put>
</template:insert>

  
