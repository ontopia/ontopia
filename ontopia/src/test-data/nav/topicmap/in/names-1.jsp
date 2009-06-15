<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

<tm:server serves="topic">
  <tm:names>
    <rn:cluster type="blockHeading" renderTemplate="text">
	<rn:display slot="title" object="primary" renderTemplate="text" />
    </rn:cluster>
    <rn:block renderTemplate="title"/>
    <rn:layout blockListStringifierArgs=", "/>
  </tm:names>
</tm:server>

<%@ include file='../../htmlFooter.jsp' %>
