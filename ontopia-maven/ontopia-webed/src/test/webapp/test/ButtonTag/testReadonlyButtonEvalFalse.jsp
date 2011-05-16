<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="attributesTest">
  <tolog:set var="foo" query="topicmap($T), $T /= $T?"/>
  <webed:button action="buttonTest" id="ID" text="BUTTON" class="bttn"
                readonly="foo"/>
</webed:form> 
</tolog:context>
