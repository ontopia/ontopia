<%@ taglib uri="/WEB-INF/jsp/webed-form.tld" prefix="webed" %>
<%@ taglib uri="/WEB-INF/jsp/tolog.tld" prefix="tolog"     %>

<tolog:context topicmap="test.ltm">
<webed:form actiongroup="testActionGroup" readonly="true">
 <tolog:set var="list" 
            query="is-related-to(test-topic: parent, $item : child)?"/>
 <tolog:set var="first" 
        query='subject-identifier($T, "http://psi.ontopia.net/test/test-list-topic-one")?'/>

  <webed:list action="listTest" collection="list" id="LIST" selected="first"/>
</webed:form>
</tolog:context>
