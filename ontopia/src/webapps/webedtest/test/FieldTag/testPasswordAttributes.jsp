<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="attributesTest">
 <webed:field type="passwordField" action="fieldTest" id="ID"
              class="input">VALUE</webed:field>
</webed:form>
</tolog:context>
