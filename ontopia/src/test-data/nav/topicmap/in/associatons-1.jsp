<%@ include file='../../sharedHeader.jsp' %>
<%@ include file='../../htmlHeader.jsp' %>

  <!-- Related Subjects: Topics with which this topic is associated -->
  
  <tm:server serves="topic">
     <tm:associations
	instances="http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"
	instancesFilter="stop">
	<rn:cluster type='item' renderTemplate='text'>
	  <rn:display slot='title' object='primary'
	    renderTemplate='text'
	    args='showNone' />
	  </rn:cluster>
	  <rn:block renderTemplate="content" />
     </tm:associations>
  </tm:server>

<%@ include file='../../htmlFooter.jsp' %>
