<%@ include file="../fragment/common-header.jsp" %>

<template:insert template='accessctl-template.jsp'>
<template:put name="title" body="true">
  <fmt:message key="MainPage"/>
</template:put>
<template:put name="content" body="true">
  <P>
    <tolog:context topicmap='<%= request.getParameter("tm") %>'>
      <tolog:declare>
        using userman for i"http://psi.ontopia.net/userman/"
      </tolog:declare>
      <tolog:set var="topicmap" query="select $TM from topicmap($TM)?"/>
      <TABLE>
        
        <%
          String maintainPrivileges = request
            .getParameter("maintainPrivileges");
          if (maintainPrivileges == null)
            maintainPrivileges = "false";
            
          String maintainUsers = request
            .getParameter("maintainUsers");
          if (maintainUsers == null)
            maintainUsers = "false";
            
          String maintainUserGroups = request
            .getParameter("maintainUserGroups");
          if (maintainUserGroups == null)
            maintainUserGroups = "false";
          
          String maintainingPrivileges = maintainPrivileges.equals("true") 
            ? "maintainPrivileges=true" : "maintainPrivileges=false";
           
          String maintainingUsers = maintainUsers.equals("true") 
            ? "maintainUsers=true" : "maintainUsers=false";
           
          String maintainingUserGroups = maintainUserGroups.equals("true") 
            ? "maintainUserGroups=true" : "maintainUserGroups=false";
                      
        %>       
        
        <%----------------------- Expand/Collapse all ------------%>
        <TR>
          <TD>
            <A href="main.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>&maintainPrivileges=true&maintainUsers=true&maintainUserGroups=true">
              <img src="../images/expand_all.gif">
            </A>
          </TD>
          <TD>
            <A href="main.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>&">
              <img src="../images/collapse_all.gif">
            </A>
          </TD>
        </TR>
        
        <%------------------ Privileges section ----------------%>
        <TR>
          <c:set var="CreateNew_">
            <fmt:message key="CreateNewPrivilege"/>
          </c:set>
          
          <% String callSelf = "main.jsp?tm=" + request.getParameter("tm") 
                  + "&language=" + language
                  + "&" + maintainingUsers + "&" + maintainingUserGroups;
          %>
          <TD>
            <% if (maintainPrivileges.equals("true")) { %>
              <A HREF="<%= callSelf %>"><img src="../images/collapse.gif"></A>
            <% } else { %>
                <A HREF="<%= callSelf %>&maintainPrivileges=true">
                  <img src="../images/expand.gif">
                </A>
            <% } %>
          </TD>
          <TD>
            <A HREF="privileges.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>">
              <fmt:message key="MaintainPrivileges"/>
            </A>
          </TD>
          <TD>
            <webed:form actiongroup="privileges" id="privilegeCreation">
              <tolog:set var="ltmFragment">
                [%new% : privilege = "<fmt:message key="NewPrivilege"/>"]
                {%new%, privilege, [[<fmt:message key="NewPrivilege"/>]]}
              </tolog:set>
              
              <c:set var="Privilege"><fmt:message key="Privilege"/></c:set>
              <input type="hidden" name="language" value="<%= language %>">
              <webed:button action="create" id="createNewPrivilege" text=
                      '<%= pageContext.getAttribute("CreateNew_").toString() %>'
                      params="topicmap ltmFragment"/>
            </webed:form>
          </TD>
        </TR>
        
        <%----------- Expansion of privilege maintanence ----------%>
        <% if (maintainPrivileges.equals("true")) { %>
          <c:set var="Delete"><fmt:message key="Delete"/></c:set>
          <webed:form actiongroup="index" id="privilegeMaintenance">
            <tolog:foreach query="instance-of($PRIVILEGE, userman:privilege) order by $PRIVILEGE?">
              <TR><TD>
                <img src="../images/spacer.gif">
              </TD><TD>
                <img src="../images/boxed.gif">
                <A HREF="privilege.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>&old&ag=privileges&id=<tolog:id var="PRIVILEGE"/>">
                  <tolog:out var="PRIVILEGE"/>
                </A>
              </TD><TD>
                <tolog:query name="canDeleteQuery">
                  %PRIVILEGE% /= userman:priv-admin-users?
                </tolog:query>
                <tolog:choose><tolog:when query="canDeleteQuery">
                  <input type="hidden" name="maintainPrivileges" 
                      value="<%= request.getParameter("maintainPrivileges")%>"
                      />
                  <input type="hidden" name="maintainUsers" 
                      value="<%= request.getParameter("maintainUsers")%>"
                      />
                  <input type="hidden" name="maintainUserGroups" 
                      value="<%= request.getParameter("maintainUserGroups")%>"
                      />
                  <input type="hidden" name="language" 
                      value="<%= language %>">
                  <webed:button id="delete<tolog:out var='sequence-number'/>"
                      action="delete" params="PRIVILEGE" 
                      text='<%= pageContext.getAttribute("Delete").toString() %>'/>
                </tolog:when><tolog:otherwise>
                  <input type=button id="delete<tolog:out var='sequence-number'/>"
                      value="<%= pageContext.getAttribute("Delete").toString() %>" 
                      onclick='alert("<fmt:message key="CannotDeletePrivilege"/>")'>
                </tolog:otherwise></tolog:choose>
              </TD></TR>
            </tolog:foreach>
          </webed:form>
        <% } %>
        
        <%----------------------- Users section ----------------%>
        <TR>
          <c:set var="CreateNew_">
            <fmt:message key="CreateNewUser"/>
          </c:set>
          <% callSelf = "main.jsp?tm=" + request.getParameter("tm")
                  + "&language=" + language
                  + "&" + maintainingPrivileges + "&" + maintainingUserGroups;
          %>
          <TD>
            <% if (maintainUsers.equals("true")) { %>
              <A HREF="<%= callSelf %>"><img src="../images/collapse.gif"></A>
            <% } else { %>
              <A HREF="<%= callSelf %>&maintainUsers=true">
                <img src="../images/expand.gif">
              </A>
            <% } %>
          </TD>
          <TD>
            <A HREF="users.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>">
              <fmt:message key="MaintainUsers"/>
            </A>
          </TD>
          <TD>
            <webed:form actiongroup="users" id="userCreation">
              <tolog:set var="ltmFragment">
                [%new% : user = "<fmt:message key="NewUser"/>"]
                {%new%, username, [[<fmt:message key="NewUser"/>]]}
                {%new%, password, [[]]}
              </tolog:set>
              
              <input type="hidden" name="language" value="<%= language %>">
              <webed:button action="create" id="createNewUser" 
                  text='<%= pageContext.getAttribute("CreateNew_").toString() %>'
                  params="topicmap ltmFragment"/>
            </webed:form>
          </TD>
        </TR>
          
        <%------------ Expansion of user maintanence --------------%>
        <% if (maintainUsers.equals("true")) { %>
          <c:set var="Delete"><fmt:message key="Delete"/></c:set>
          <webed:form actiongroup="index" id="userMaintenance">
            <tolog:foreach 
                    query="instance-of($USER, userman:user) order by $USER?">
              <TR><TD>
                <img src="../images/spacer.gif">
              </TD><TD>
                <img src="../images/boxed.gif">
                <A HREF="user.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>&old&ag=users&id=<tolog:id var="USER"/>">
                  <tolog:out var="USER"/>
                </A>
              </TD><TD>
                <tolog:query name="canDeleteQuery">
                  %USER% /= $USER2,
                  userman:plays-role($USER2 : userman:user, 
                      $ROLE : userman:role),
                  userman:has-privilege($ROLE : receiver, 
                      userman:priv-admin-users : privilege)?
                </tolog:query>
                <tolog:choose><tolog:when query="canDeleteQuery">
                  <input type="hidden" name="maintainPrivileges" 
                      value="<%= request.getParameter("maintainPrivileges")%>"
                      />
                  <input type="hidden" name="maintainUsers" 
                      value="<%= request.getParameter("maintainUsers")%>"
                      />
                  <input type="hidden" name="maintainUserGroups" 
                      value="<%= request.getParameter("maintainUserGroups")%>"
                      />
                  <input type="hidden" name="language" 
                      value="<%= language %>"/>
                  <webed:button id="delete<tolog:out var='sequence-number'/>"
                      action="delete" params="USER"
                      text='<%= pageContext.getAttribute("Delete").toString() %>'/>
                </tolog:when><tolog:otherwise>
                  <input type=button id="delete<tolog:out var='sequence-number'/>"
                      value="<%= pageContext.getAttribute("Delete").toString() %>" 
                      onclick='alert("<fmt:message key="CannotDeleteUser"/>")'>
                </tolog:otherwise></tolog:choose>
              </TD></TR>
            </tolog:foreach>
          </webed:form>
        <% } %>
        
        <%------------------ User Groups section ----------------%>
        <TR>
          <c:set var="CreateNew_">
            <fmt:message key="CreateNewUserGroup"/>
          </c:set>
         <TD>
            <% callSelf = "main.jsp?tm=" + request.getParameter("tm") 
                    + "&language=" + language
                    + "&" + maintainingPrivileges + "&" + maintainingUsers;
            %>
            
            <% if (maintainUserGroups.equals("true")) { %>
              <A HREF="<%= callSelf %>"><img src="../images/collapse.gif"></A>
            <% } else { %>
              <A HREF="<%= callSelf %>&maintainUserGroups=true">
                <img src="../images/expand.gif">
              </A>
            <% } %>
          </TD>
          <TD>
            <A HREF="userGroups.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>">
              <fmt:message key="MaintainUserGroups"/>
            </A>
          </TD>
          <TD>
            <webed:form actiongroup="userGroups" id="userGroupCreation">
              <tolog:set var="ltmFragment">
                [%new% : user-group = "<fmt:message key="NewUserGroup"/>"]
              </tolog:set>
              
              <c:set var="UserGroup"><fmt:message key="UserGroup"/></c:set>
              <input type="hidden" name="language" value="<%= language %>">
              <webed:button action="create" id="createNewUserGroup"  text=
                      '<%= pageContext.getAttribute("CreateNew_").toString() %>'
                      params="topicmap ltmFragment"/>
            </webed:form>
          </TD>
        </TR>
        
        <%----------- Expansion of user group maintanence ----------%>
        <% if (maintainUserGroups.equals("true")) { %>
          <c:set var="Delete"><fmt:message key="Delete"/></c:set>
          
          <webed:form actiongroup="index" id="userGroupMaintenance">
            <tolog:foreach query="instance-of($USER-GROUP, userman:user-group) order by $USER-GROUP?">
              <TR><TD>
                <img src="../images/spacer.gif">
              </TD><TD>
                <img src="../images/boxed.gif">
                <A HREF="userGroup.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>&old&ag=userGroup&id=<tolog:id var="USER-GROUP"/>">
                  <tolog:out var="USER-GROUP"/>
                </A>
              </TD><TD>
                <tolog:query name="canDeleteQuery">
                  %USER-GROUP% /= $ROLE,
                  userman:plays-role($USER : userman:user, $ROLE : userman:role),
                  userman:has-privilege($ROLE : receiver, 
                      userman:priv-admin-users : privilege)?
                </tolog:query>
                <tolog:choose><tolog:when query="canDeleteQuery">
                  <input type="hidden" name="maintainPrivileges" 
                      value="<%= request.getParameter("maintainPrivileges")%>"
                      />
                  <input type="hidden" name="maintainUsers" 
                      value="<%= request.getParameter("maintainUsers")%>"/>
                  <input type="hidden" name="maintainUserGroups" 
                      value="<%= request.getParameter("maintainUserGroups")%>"/>
                  <input type="hidden" name="language" value="<%= language %>">
                  <webed:button id="delete<tolog:out var='sequence-number'/>" 
                      action="delete" params="USER-GROUP"
                      text='<%= pageContext.getAttribute("Delete").toString() %>'/>
                </tolog:when><tolog:otherwise>
                  <input type=button id="delete<tolog:out var='sequence-number'/>"
                      value="<%= pageContext.getAttribute("Delete").toString() %>" 
                      onclick='alert("<fmt:message key="CannotDeleteUserGroup"/>")'>
                </tolog:otherwise></tolog:choose>
              </TD></TR>
            </tolog:foreach>
          </webed:form>
        <% } %>
        
        <%----------------------- Expand/Collapse all ------------%>
        <TR>
          <TD>
            <A href="main.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>&maintainPrivileges=true&maintainUsers=true&maintainUserGroups=true">
              <img src="../images/expand_all.gif">
            </A>
          </TD>
          <TD>
            <A href="main.jsp?tm=<%= request.getParameter("tm") %>&language=<%= language %>">
              <img src="../images/collapse_all.gif">
            </A>
          </TD>
        </TR>
      
      </TABLE>
    </tolog:context>
  </P>
</template:put>
</template:insert>

  
