<%@ include file="../fragment/common-header.jsp" %>

<template:insert template='modify-template.jsp'>
<template:put name="title" body="true">
  <fmt:message key="ModifyPrivilege"/>
</template:put>
<template:put name="content" body="true">

<tolog:context topicmap='<%= request.getParameter("tm") %>'>
  <webed:form actiongroup="privilege">
    <tolog:set var="privilege" reqparam="id"/>
    <tolog:if query="instance-of(%privilege%, privilege),
            {topic-name(%privilege%, $NAME),
             subject-identifier(%privilege%, $SI) |
             topic-name(%privilege%, $NAME)}?">
      
      <P>
        <fmt:message key="PrivilegeName_Colon"/>
        <BR>
        <webed:field action="set-name" id="enterName" type="short"
            params="NAME">
          <tolog:out var="NAME"/>
        </webed:field>
      </P>
      
      <P>
        <fmt:message key="SI_Colon"/>
        <BR>
        <webed:field action="set-si" id="enterSI" type="uri"
            params="privilege SI">
          <tolog:if var="SI">
            <tolog:out var="SI"/>
          </tolog:if>
        </webed:field>
      </P>
      
      <c:set var="Save"><fmt:message key="Save"/></c:set>
      <input type="hidden" name="language" value="<%= language %>">
      <webed:button action="submit" id="save"
          text='<%= pageContext.getAttribute("Save").toString() %>'/>
      
      <c:set var="Cancel"><fmt:message key="Cancel"/></c:set>
      <% if (request.getParameter("old") == null) { %>
        <input type="hidden" name="language" value="<%= language %>">
        <webed:button action="delete" id="cancel" params="privilege"
            text='<%= pageContext.getAttribute("Cancel").toString() %>'/>
      <% } else { %>
        <input type=button onclick="history.go(-1)" id="cancel" 
            value='<%= pageContext.getAttribute("Cancel").toString() %>'>
      <% } %>
    </tolog:if>
  </webed:form>
</tolog:context>
  
</template:put>
</template:insert>

  
