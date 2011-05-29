<%@ include file="../fragment/common-header.jsp" %>

<HTML>
  <HEAD>
    <title>Accessctl</title>
    <link rel='stylesheet' href='../accessctl.css' />
  </HEAD>

  <BODY>
    <title><template:get name='title'/></title>
    
    <!-- This will be our navigation bar, consisting of pointers to the
        most important topic types -->
    <H2>Accessctl</H2>
    
    <tolog:context topicmap='<%= request.getParameter("tm") %>'>
      <tolog:declare>
        using userman for i"http://psi.ontopia.net/userman/"
      </tolog:declare>
      
      <tolog:query name="permissionQuery">
        userman:plays-role($USER : userman:user, $ROLE : userman:role),
        userman:has-privilege($ROLE : receiver, 
                userman:priv-admin-users : privilege),
        occurrence($USER, $USERNAME),
        value($USERNAME, "<%= remoteUser %>"), 
        type($USERNAME, userman:username)?
      </tolog:query>
      <tolog:choose>
        <tolog:when query="permissionQuery">
          <P align=right>
            <A href="main.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>"><fmt:message key="MainPage"/></A> |
            <A href="users.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>"><fmt:message key="Users"/></A> |
            <A href="userGroups.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>"><fmt:message key="UserGroups"/></A> |
            <A href="privileges.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>"><fmt:message key="Privileges"/></A> |
            <A href="../logout.jsp?language=<%= language %>"><fmt:message key="Logout"/></A>
          </P>
          
          <!-- every page will have an H1 title -->
          <H1><template:get name='title'/></H1>
          
          <!-- every page will have some content -->
          <template:get name="content"/>
          
          <% 
            java.util.Enumeration parameterNames = request.getParameterNames();
            String requestString = request.getRequestURL().toString() + "?";
            
            while (parameterNames.hasMoreElements()) {
              String parameterName = (String)parameterNames.nextElement();
              
              if (!parameterName.equals("language")) {
                String parameterValue = request.getParameter(parameterName);
                requestString += "&" + parameterName + "=" + parameterValue;
              }
            }
          %>  
            
          <P align="center">
            <A href="<%= requestString %>&language=en">English</A>
            | <A href="<%= requestString %>&language=de">Deutsch</A>
            | <A href="<%= requestString %>&language=nl">Nederlands</A>
            | <A href="<%= requestString %>&language=no">Norsk</A>
          </P>
          
        </tolog:when>
        <tolog:otherwise>
          <fmt:message key="NoPermission"/><BR>
          <fmt:message key="Please"/> <A href="../logout.jsp?language=<%= language %>"><fmt:message key="logout"/></A> 
          <fmt:message key="AndLogInAsAdministrator"/>
        </tolog:otherwise>
      </tolog:choose>  
    
    </tolog:context>
    
    <!-- every page will have the same footer -->
    <hr>
    <P align="right"><A href="http://www.ontopia.net/">
    <img src="graphics/poweredByOntopia.gif" alt="Ontopia A/S" border="0"/>
    </A></P>
  </BODY>
</HTML>

