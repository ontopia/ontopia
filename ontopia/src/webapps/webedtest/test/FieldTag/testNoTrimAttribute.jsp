<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="attributesTest">
 <webed:field type="textField" action="fieldTest" id="FLD"><%@ include file="value.txt" %></webed:field>
</webed:form>
</tolog:context>
