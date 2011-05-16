<%@ include file="../fragment/common-header.jsp" %>

<template:insert template='accessctl-template.jsp'>
<template:put name="title" body="true">
  <fmt:message key="UserGroups"/>
</template:put>
<template:put name="content" body="true">

<tolog:context topicmap='<%= request.getParameter("tm") %>'>
  <webed:form actiongroup="userGroups">
    <tolog:declare>
      using userman for i"http://psi.ontopia.net/userman/"
    </tolog:declare>
    <tolog:set var="topicmap" query="select $TM from topicmap($TM)?"/>

    <tolog:set var="ltmFragment">
      [%new% : user-group = "<fmt:message key="NewUserGroup"/>"]
    </tolog:set>
    
    <c:set var="CreateNewUserGroup">
      <fmt:message key="CreateNewUserGroup"/>
    </c:set>
    <input type="hidden" name="language" value="<%= language %>">
    <webed:button action="create" id="create" params="topicmap ltmFragment"
        text='<%= pageContext.getAttribute("CreateNewUserGroup").toString() %>' />
    <TABLE>
      <c:set var="Delete"><fmt:message key="Delete"/></c:set>
      <tolog:foreach query="instance-of($USER-GROUP, userman:user-group) order by $USER-GROUP?">
        <TR><TD>
          <A HREF="userGroup.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language%>&old&ag=userGroup&id=<tolog:id var="USER-GROUP"/>">
            <tolog:out var="USER-GROUP"/>
          </A>
        </TD><TD>
          <tolog:query name="canDeleteQuery">
            %USER-GROUP% /= $ROLE,
            userman:plays-role($USER : userman:user, $ROLE : userman:role),
            userman:has-privilege($ROLE : receiver, 
                userman:priv-admin-users : privilege)?
          </tolog:query>
          <tolog:choose><tolog:when query="canDeleteQuery">
            <input type="hidden" name="language" value="<%= language %>">
            <webed:button action="delete" params="USER-GROUP"
                text='<%= pageContext.getAttribute("Delete").toString() %>'/>
          </tolog:when><tolog:otherwise>
            <input type=button id="delete<tolog:out var='sequence-number'/>"
                onclick='alert("<fmt:message key="CannotDeleteUserGroup"/>")'
                value="<%= pageContext.getAttribute("Delete").toString() %>">
          </tolog:otherwise></tolog:choose>
        </TD></TR>
      </tolog:foreach>
    </TABLE>
  </webed:form>
</tolog:context>

</template:put>
</template:insert>
