<%@ include file="../fragment/common-header.jsp" %>

<template:insert template='modify-template.jsp'>
<template:put name="title" body="true">
  <fmt:message key="ModifyUserGroup"/>
</template:put>
<template:put name="content" body="true">

<tolog:context topicmap='<%= request.getParameter("tm") %>'>
  <tolog:declare>
    using userman for i"http://psi.ontopia.net/userman/"
  </tolog:declare>
  <webed:form actiongroup="userGroup">
    <tolog:set var="userGroup" reqparam="id"/>
    <tolog:if query="instance-of(%userGroup%, userman:user-group),
            topic-name(%userGroup%, $NAME)?">
      <P>
        <fmt:message key="UserGroupName_Colon"/>
        <BR>
        <webed:field action="set-name" id="enterName" type="short"
            params="NAME">
          <tolog:out var="NAME"/>
        </webed:field>
      </P>
      
      <P>
        <fmt:message key="Privileges_Colon"/><BR>
        
        <tolog:set var="has-privilege" query='select $T from subject-identifier(userman:has-privilege, $SI), subject-identifier($T, $SI)?'/>
        <tolog:set var="receiver" query='topic($T), not($T /= receiver)?'/>
        <tolog:set var="privilege" 
                query='topic($T), not($T /= userman:privilege)?'/>
        
        <TABLE><tolog:foreach query="instance-of($PRIVILEGE, userman:privilege) order by $PRIVILEGE?"><TR>
          <tolog:set var="assignedPrivileges"
                    query="topic($PRIVILEGE), userman:has-privilege(%userGroup% : receiver, %PRIVILEGE% : userman:privilege), not($PRIVILEGE /= %PRIVILEGE%)?"/>
        
          <TD>
            <tolog:set var="hasPrivilege" query="select $HAS-PRIVILEGE from type($HAS-PRIVILEGE, has-privilege), association-role($HAS-PRIVILEGE, $RECEIVER), role-player($RECEIVER, %userGroup%), association-role($HAS-PRIVILEGE, $PRIVILEGE), role-player($PRIVILEGE, %PRIVILEGE%)?"/>
                      
            <webed:checkbox action="assign" id="checkHasPrivilege"
                params="hasPrivilege has-privilege userGroup receiver privilege PRIVILEGE" state="assignedPrivileges"/>
          </TD><TD>
            <tolog:out var="PRIVILEGE"/>
          </TD>
        </TR></tolog:foreach></TABLE>
      </P>
      
      <c:set var="Save"><fmt:message key="Save"/></c:set>
      <input type="hidden" name="language" value="<%= language %>">
      <webed:button action="submit"  id="save"
          text='<%= pageContext.getAttribute("Save").toString() %>'/>
      
      <c:set var="Cancel"><fmt:message key="Cancel"/></c:set>
      <% if (request.getParameter("old") == null) { %>
        <input type="hidden" name="language" 
            value="<%= language %>">
        <webed:button action="delete" id="cancel" params="userGroup" 
            text='<%= pageContext.getAttribute("Cancel").toString() %>'/>
      <% } else { %>
        <input type=button id="cancel" onclick="history.go(-1)" value=
                '<%= pageContext.getAttribute("Cancel").toString() %>'>
      <% } %>
    </tolog:if>
  </webed:form>
</tolog:context>

</template:put>
</template:insert>
