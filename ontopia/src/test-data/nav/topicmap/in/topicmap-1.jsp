<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topicmap">
    <tm:topicmap>
      <rn:cluster type="single" renderTemplate="text">
	<rn:display slot="title" object="primary" args="reify"/>
	</rn:cluster>
    </tm:topicmap>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
