<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topic">
    <rn:containerRender title="Occurrence Instances" titleDesc="Occurrences of this type">
      <tm:instancesOccurrence>
        <rn:cluster type="item">
          <rn:display slot="title" object="primary"/>
          <rn:display slot="bracket" object="parent"/>
        </rn:cluster>
      </tm:instancesOccurrence>
    </rn:containerRender>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
