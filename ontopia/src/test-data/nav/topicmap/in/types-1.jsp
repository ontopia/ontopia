<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topic">
    <rn:containerRender title="Types" titleDesc="Type information for this topic">
      <tm:types/>
    </rn:containerRender>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
