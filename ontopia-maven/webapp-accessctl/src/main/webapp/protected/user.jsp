<%@ include file="../fragment/common-header.jsp" %>

<template:insert template='modify-template.jsp'>
<template:put name="title" body="true">
  <fmt:message key="ModifyUser"/>
</template:put>
<template:put name="content" body="true">

<tolog:context topicmap='<%= request.getParameter("tm") %>'>
  <tolog:declare>
    using userman for i"http://psi.ontopia.net/userman/"
  </tolog:declare>
  <tolog:set var="user" reqparam="id"/>

  <tolog:if query="instance-of(%user%, userman:user)?">
    <tolog:set var="name" query="topic-name(%user%, $NAME)?"/>
    <tolog:set var="utype" query="topic($T), $T = userman:username?"/>
    <tolog:set var="uname" query="occurrence(%user%, $USERNAME),
                                  type($USERNAME, userman:username)?"/>
    <tolog:set var="pwtype" query="topic($T), $T = userman:password?"/>
    <tolog:set var="pw" query="occurrence(%user%, $PASSWORD),
                               type($PASSWORD, userman:password)?"/>
  
    <webed:form actiongroup="user">
      <P>
        <fmt:message key="Name_Colon"/>
        <BR>
        <webed:field action="set-name" id="enterName" type="short"
            params="name">
          <tolog:out var="name"/>
        </webed:field>
      </P>
      
      <P>
        <fmt:message key="UserName_Colon"/>
        <BR>
        <c:set var="duplicateForward" 
               value='<%= "/accessctl/protected/user.jsp?" + 
                          request.getQueryString() + "&duplicate=true" %>'/>
        <% String dup = request.getParameter("duplicate");
           if (dup != null && dup.equals("true")) { %>
           <font color="red">
             <fmt:message key="UsernameAlreadyInUse"/><br>
             <fmt:message key="PleaseChooseDifferentUsername"/><br>
           </font>
        <% } %>
        <webed:field action="set-value-unique" id="enterUsername" type="short"
            params="uname user duplicateForward utype">
          <tolog:if var="uname">
            <tolog:out var="uname"/>
          </tolog:if>
        </webed:field>
      </P>
      
      <P>
        <fmt:message key="Password_Colon"/>
        <BR>
        <webed:field action="set-value" id="enterPassword" type="password"
            params="pw user pwtype">
        </webed:field>
      </P>
      
      <P>
        <fmt:message key="UserGroups_Colon"/><BR>
        
        <tolog:set var="plays-role" query='select $T from subject-identifier(userman:plays-role, $SI), subject-identifier($T, $SI)?'/>
        <tolog:set var="userRole" query='topic($T), not($T /= userman:user)?'/>
        <tolog:set var="roleRole" query='topic($T), not($T /= userman:role)?'/>
        
        <tolog:foreach query="instance-of($USER-GROUP, userman:user-group) order by $USER-GROUP?">
          <tolog:set var="membershipGroups"
                    query="topic($USER-GROUP), userman:plays-role(%user% : userman:user, %USER-GROUP% : userman:role), not($USER-GROUP /= %USER-GROUP%)?"/>
        
          <tolog:set var="memberGroup" query="select $PLAYS-ROLE from type($PLAYS-ROLE, userman:plays-role), association-role($PLAYS-ROLE, $RECEIVER), role-player($RECEIVER, %user%), association-role($PLAYS-ROLE, $USER-GROUP), role-player($USER-GROUP, %USER-GROUP%)?"/>
                    
          <webed:checkbox action="assign" params="memberGroup plays-role user userRole roleRole USER-GROUP" state="membershipGroups"/>
          
          <tolog:out var="USER-GROUP"/>
          <BR>
        </tolog:foreach>
      </P>
      
      <c:set var="Save"><fmt:message key="Save"/></c:set>
      <input type="hidden" name="language" value="<%= language %>">
      <webed:button action="submit" id="save" 
          text='<%= pageContext.getAttribute("Save").toString() %>'/>
      
      <c:set var="Cancel"><fmt:message key="Cancel"/></c:set>
      <% if (request.getParameter("old") == null) { %>
        <input type="hidden" name="language" value="<%= language %>">
        <webed:button action="delete" id="cancel" params="user" 
            text='<%= pageContext.getAttribute("Cancel").toString() %>'/>
      <% } else { %>
        <input type=button id="cancel" onclick="history.go(-1)" value=
                '<%= pageContext.getAttribute("Cancel").toString() %>'>
      <% } %>
    </webed:form>
  </tolog:if>
</tolog:context>
  
</template:put>
</template:insert>

  
