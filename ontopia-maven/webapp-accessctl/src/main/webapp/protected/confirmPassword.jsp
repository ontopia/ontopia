<%@ include file="../fragment/common-header.jsp" %>

<H2><fmt:message key="ChangedPassword"/></H2>

<tolog:context topicmap='<%= request.getParameter("tm") %>'>
  <c:set var="user"><%= request.getRemoteUser() %></c:set>
  <webed:form actiongroup="user">
    <tolog:set var="user" reqparam="user"/>
    <tolog:if query=
           'instance-of(<%= pageContext.getAttribute("user").toString() %>,
            user),
            topic-name(<%= pageContext.getAttribute("user").toString() %>,
            $NAME),
            occurrence(<%= pageContext.getAttribute("user").toString() %>,
            $PASSWORD),
            type($PASSWORD, password)?'>
      <P>
        <fmt:message key="UserName"/>:<BR>
        <tolog:out var="NAME"/>
      </P>
      
      <P>
        <fmt:message key="Password"/>:<BR>
        <tolog:out var="PASSWORD"/>
      </P>
      <c:set var="Submit"><fmt:message key="Submit"/></c:set>
      <webed:button action="submit" text='<%= pageContext.getAttribute("Submit").toString() %>'/>
    </tolog:if>
  </webed:form>
</tolog:context>
  
