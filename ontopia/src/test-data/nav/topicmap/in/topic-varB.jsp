<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topic">
    <tm:topic>
      <rn:cluster type="single" renderTemplate="text">
	<rn:display
	  slot="title"
	  object="primary"
	  renderTemplate="noLink"
	  variantNameContext="puccini"
	  variantNameDecider="net.ontopia.topicmaps.nav.utils.deciders.WithinScopeDecider"
	  args="showNone"
	  />
	</rn:cluster>
    </tm:topic>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
  
