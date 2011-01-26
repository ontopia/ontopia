<%@ page language="java" 
  import="net.ontopia.topicmaps.core.*,
          net.ontopia.topicmaps.entry.*,
          net.ontopia.topicmaps.nav2.utils.*,
          net.ontopia.topicmaps.db2tm.DB2TM"%>
<%
  // did the user press cancel?
  if (request.getParameter("cancel") != null) {
    response.sendRedirect("/omnigator/models/topicmap_complete.jsp?tm=" + 
                          request.getParameter("tm"));
    return;			  
  }

  // the user pressed sync, so go ahead

  //  (1) get a reference to the mapping file
  String cfgfile = getServletContext().getRealPath("/plugins/db2tm/db2tm.xml");
  
  //  (2) get the topic map
  TopicMapRepositoryIF rep = ContextUtils.getRepository(pageContext.getServletContext());
  TopicMapStoreIF store = rep.createStore(request.getParameter("tm"), false);
  try {
    TopicMapIF tm = store.getTopicMap();

    //  (3) run!
    DB2TM.sync(cfgfile, tm);

    store.commit();
  } catch (java.io.IOException ioe) {
     throw new net.ontopia.utils.OntopiaRuntimeException(ioe);
  } catch (Throwable e) {
    store.abort();
    throw e;
  } finally {
    store.close();
  }

  //  (4) go home
  response.sendRedirect("/omnigator/models/topicmap_complete.jsp?tm=" + 
                        request.getParameter("tm"));
%>
