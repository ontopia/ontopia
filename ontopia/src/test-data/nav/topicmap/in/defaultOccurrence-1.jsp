<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topic">
    <tm:defaultOccurrence>
      <rn:cluster type="single" renderTemplate="text">
        <rn:display object="primary" slot="title" args="lineBreak"/>
      </rn:cluster>
    </tm:defaultOccurrence>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
