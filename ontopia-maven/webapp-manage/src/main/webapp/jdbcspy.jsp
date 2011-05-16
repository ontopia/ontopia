<%@ page language="java" 
           import="net.ontopia.topicmaps.nav2.core.*,
                   net.ontopia.topicmaps.nav2.utils.FrameworkUtils" %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tmvalue'   prefix='tm'        %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/logic'     prefix='logic' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/output'    prefix='output' %>

<logic:context>
  <template:insert template='/views/template-one-column1.jsp'>
    <template:put name='title' body='true'>[Admin] jdbcspy report</template:put>
 
    <template:put name='manageLinks' body='true'>
      <a href="/manage/manage.jsp">Manage</a>
    </template:put>

    <template:put name='heading' body='true'>
      <h1>jdbcspy report</h1>
    </template:put>

    <%
      UserIF user = FrameworkUtils.getUser(pageContext);
      String skin = user.getSkin();
    %>

    <template:put name='skin' body='true'>skins/<%= skin %>.css</template:put>

    <template:put name='plugins' body='true'>
      <framework:pluginList separator=" | " group="topicmap"/>
    </template:put>

    <template:put name="content" body="true">
      </table>
        <%@ page language="java" import="net.ontopia.persistence.jdbcspy.SpyDriver" %>
        <% 
          if (request.getParameter("clear") != null) {
            SpyDriver.clearStats();
            response.sendRedirect("jdbcspy.jsp");
          } else {
            out.write("<style>th, .event { vertical-align: top; text-align: left } td { vertical-align: top; text-align: right }</style>\n");
            SpyDriver.writeReport(out); 
          }
        %>
      <table class="contentTable" width="100%" cellspacing="0" cellpadding="10" border="0">
    </template:put>

    <template:put name='outro' body='true'></template:put>

    <%-- ============== Outsourced application wide standards ============== --%>
    <template:put name='application' content='/fragments/application.jsp'/>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>
  </template:insert>
</logic:context>
