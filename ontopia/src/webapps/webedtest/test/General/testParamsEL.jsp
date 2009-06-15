<%@ taglib uri='/WEB-INF/jsp/webed-form.tld'     prefix='webed'    %>
<%@ taglib uri='/WEB-INF/jsp/tolog.tld'          prefix='tolog'    %>

<tolog:context topicmap="test.ltm">
  <webed:form actiongroup="testActionGroup">
    <tolog:set query="topic($T)?" scope="application"/>
    <webed:button action="delete" text="Delete" params="T"/>
    <webed:button action="delete" text="Delete" params="nonExistent"/>
  </webed:form>
</tolog:context>
