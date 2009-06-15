<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topic">
    <rn:containerRender title="Role Players" titleDesc="Topics that play roles in associations of this type">
      <tm:instancesAssociation/>
    </rn:containerRender>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
