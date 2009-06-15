<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="attributesTest">
 <webed:button action="buttonTest" id="ID" text="BUTTON" class="bttn"
               readonly="true"/>
</webed:form> 
</tolog:context>
