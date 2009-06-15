<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

 <tm:server serves="topic">
  <tm:names>
    <rn:cluster type='item' renderTemplate='text'>
      <rn:display slot='title' object='primary'
                  renderTemplate='text'
                  args='showNone' />
      </rn:cluster>
      <rn:block renderTemplate="content" />
      <rn:layout itemListStringifierArgs='; '/>
  </tm:names>
 </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
