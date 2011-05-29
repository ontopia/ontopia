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
        plays-role($USER : userman:user, $ROLE : userman:role),
        has-privilege($ROLE : receiver, 
                userman:priv-admin-users : userman:privilege),
        occurrence($USER, $USERNAME),
        value($USERNAME, "<%= remoteUser %>"), 
        type($USERNAME, userman:username)?
      </tolog:query>
      <tolog:choose>
        <tolog:when query="permissionQuery">
          <!-- every page will have an H1 title -->
          <H1><template:get name='title'/></H1>
          
          <!-- every page will have some content -->
          <template:get name="content"/>
    
        </tolog:when>
        <tolog:otherwise>
          Sorry! You don't have permission to access this page.<BR>
          Please <a href="../logout.jsp"><fmt:message key="Logout"/></a> 
          and try again.
        </tolog:otherwise>
      </tolog:choose>  
    </tolog:context>
    
    <!-- every page will have the same footer -->
    <hr>
    <p align="right"><a href="http://www.ontopia.net/">
    <img src="graphics/poweredByOntopia.gif" alt="Ontopia A/S" border="0"/>
    </a></p>
  </BODY>
</HTML>

