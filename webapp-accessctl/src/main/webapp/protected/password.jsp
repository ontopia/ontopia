<%@ include file="../fragment/common-header.jsp" %>

<HTML>
  <HEAD>
    <title>Accessctl</title>
    <link rel='stylesheet' href='../accessctl.css' />
  </HEAD>

  <BODY>
    <H2><fmt:message key="ChangePassword"/></H2>
    
    <tolog:context topicmap='<%= request.getParameter("tm") %>'>
      <tolog:declare>
        using userman for i"http://psi.ontopia.net/userman/"
      </tolog:declare>
      <c:set var="user"><%= remoteUser %></c:set>
      <tolog:set var="user"><%= remoteUser %></tolog:set>
      <webed:form actiongroup="password">
        <tolog:query name="userQuery">
          instance-of($USER, userman:user),
          occurrence($USER, $USERNAME),
          value($USERNAME, 
              "<%= pageContext.getAttribute("user").toString() %>"),
          occurrence($USER, $PASSWORD),
          type($PASSWORD, password)?
        </tolog:query>
        <tolog:if query="userQuery">
          <P>
            <fmt:message key="UserName"/>:<BR>
            <tolog:out var="USERNAME"/>
          </P>
          
          <P>
            <fmt:message key="Password"/>:
            <BR>
            
            <webed:field action="set-value" id="enterpw" type="password" 
                params="PASSWORD">
            </webed:field>
          </P>
          <c:set var="Submit"><fmt:message key="Submit"/></c:set>
          <webed:button action="submit" id="submit" text=
                  '<%= pageContext.getAttribute("Submit").toString() %>'/>
          
          <c:set var="Cancel"><fmt:message key="Cancel"/></c:set>
          <input type=button id="cancel" onclick="history.go(-1)" value=
                  "<%= pageContext.getAttribute("Cancel").toString() %>">
        </tolog:if>
      </webed:form>
    </tolog:context>
  </BODY>
</HTML>  
