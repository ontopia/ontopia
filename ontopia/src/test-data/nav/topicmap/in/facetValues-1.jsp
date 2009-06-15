<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topic">
    <rn:containerRender title="Facet Values" titleDesc="Facet information for this topic">
      <tm:facetValues/>
    </rn:containerRender>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
