<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/webed" prefix="webed" %>
<%@ taglib uri="http://psi.ontopia.net/jsp/taglib/tolog" prefix="tolog" %>

<tolog:context topicmap="test.ltm">
<tolog:set var="list" query="is-related-to(test-topic: parent, $item : child)?" scope="page"/>
<tolog:set var="first" query='subject-identifier($T, "http://psi.ontopia.net/test/test-list-topic-one")?'/>
<webed:form actiongroup="attributesTest">
<webed:list type="checkbox" action="listTest" collection="list" id="ID" selected="first" />
</webed:form>
</tolog:context>
