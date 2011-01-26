<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template'%>

<template:insert template='/views/template_%view%.jsp'>
  <template:put name='title'>
    [Omnigator] DB2TM
  </template:put>

  <template:put name='plugins' body='true'>
    <framework:pluginList separator=" | " group="topicmap"/>
  </template:put>

  <template:put name="navigation" body="true">

      <p>Here you can run the DB2TM synchronization process. Doing this
      will update the topic map
      <b><%= request.getParameter("tm") %></b>
      with all changes made in the database, as configured in
      <b>db2tm.xml</b>.</p>

      <form method="POST" action="run.jsp">
        <input type=hidden name=tm value="<%= request.getParameter("tm") %>">
        <input type=submit name="sync"   value="Sync">
        <input type=submit name="cancel" value="Cancel">
      </form>

  </template:put>


  <template:put name='outro' body='true'></template:put>

  <%-- ============== Outsourced application wide standards ============== --%>
  <template:put name='application' content='/fragments/application.jsp'/>
  <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
  <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
</template:insert>

