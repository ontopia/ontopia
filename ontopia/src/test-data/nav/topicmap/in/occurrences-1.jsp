<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <tm:server serves="topic">
      <tm:occurrences>
        <rn:cluster type="item" renderTemplate="text">
	  <rn:display slot="title" object="primary" renderTemplate="text" />
        </rn:cluster>
        <rn:block renderTemplate="content"/>
        <rn:layout itemListStringifierArgs=", "/>      
      </tm:occurrences>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
