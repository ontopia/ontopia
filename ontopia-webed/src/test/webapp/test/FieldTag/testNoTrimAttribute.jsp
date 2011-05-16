<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="attributesTest">
 <webed:field type="textField" action="fieldTest" id="FLD"><%@ include file="value.txt" %></webed:field>
</webed:form>
</tolog:context>
