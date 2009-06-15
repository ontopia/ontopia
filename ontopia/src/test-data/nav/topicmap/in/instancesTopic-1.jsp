<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topic">
    <rn:containerRender title="Topics of this Type" titleDesc="Topics which are instances of this topic type">
       <tm:instancesTopic/>
    </rn:containerRender>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
