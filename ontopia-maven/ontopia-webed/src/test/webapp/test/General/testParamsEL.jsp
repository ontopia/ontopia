<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/webed' prefix='webed' %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog' %>

<tolog:context topicmap="test.ltm">
  <webed:form actiongroup="testActionGroup">
    <tolog:set query="topic($T)?" scope="application"/>
    <webed:button action="delete" text="Delete" params="T"/>
    <webed:button action="delete" text="Delete" params="nonExistent"/>
  </webed:form>
</tolog:context>
