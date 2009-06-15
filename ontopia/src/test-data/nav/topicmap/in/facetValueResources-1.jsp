<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topic">
    <rn:containerRender title="Relevant Resources" titleDesc="Resources which have this facet value">
      <tm:facetValueResources/>
    </rn:containerRender>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
