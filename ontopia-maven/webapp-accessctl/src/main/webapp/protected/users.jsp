<%@ include file="../fragment/common-header.jsp" %>

<template:insert template='accessctl-template.jsp'>
<template:put name="title" body="true">
  <fmt:message key="Users"/>
</template:put>
<template:put name="content" body="true">

<tolog:context topicmap='<%= request.getParameter("tm") %>'>
  <webed:form actiongroup="users">
    <tolog:declare>
      using userman for i"http://psi.ontopia.net/userman/"
    </tolog:declare>
    <tolog:set var="topicmap" query="select $TM from topicmap($TM)?"/>
    <tolog:set var="ltmFragment">
      [%new% : user = "<fmt:message key="NewUser"/>"]
    </tolog:set>
    <c:set var="CreateNewUser"><fmt:message key="CreateNewUser"/></c:set>
    <c:set var="User"><fmt:message key="User"/></c:set>
    <input type="hidden" name="language" value="<%= language %>">
    <webed:button action="create" id="create" params="topicmap ltmFragment"
        text='<%= pageContext.getAttribute("CreateNewUser").toString() %>'/>
    <TABLE>
      <c:set var="Delete"><fmt:message key="Delete"/></c:set>
      <tolog:foreach query="instance-of($USER, userman:user) order by $USER?">
        <TR><TD>
          <A HREF="user.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language%>&old&ag=users&id=<tolog:id var="USER"/>">
            <tolog:out var="USER"/>
          </A>
        </TD><TD>
          <tolog:query name="canDeleteQuery">
            %USER% /= $USER2,
            userman:plays-role($USER2 : userman:user, $ROLE : userman:role),
            userman:has-privilege($ROLE : receiver, 
                userman:priv-admin-users : privilege)?
          </tolog:query>
          <tolog:choose><tolog:when query="canDeleteQuery">
            <input type="hidden" name="language" value="<%= language %>">
            <webed:button action="delete" params="USER" 
                text='<%= pageContext.getAttribute("Delete").toString() %>'/>
          </tolog:when><tolog:otherwise>
            <input type=button id="delete<tolog:out var='sequence-number'/>"
                onclick='alert("<fmt:message key="CannotDeleteUser"/>")'
                value="<%= pageContext.getAttribute("Delete").toString() %>">
          </tolog:otherwise></tolog:choose>
        </TD></TR>
      </tolog:foreach>
    </TABLE>
  </webed:form>
</tolog:context>
  
</template:put>
</template:insert>

  
