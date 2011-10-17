<%@ page language="java" 
  import="java.io.File,
          net.ontopia.topicmaps.core.*,
          net.ontopia.topicmaps.entry.*,
          net.ontopia.topicmaps.nav2.utils.*,
          net.ontopia.topicmaps.db2tm.DB2TM,
          org.slf4j.*"%><%!

  static Logger log = LoggerFactory.getLogger("net.ontopia.topicmaps.db2tm"+
                        ".OmnigatorPlugin");


%><%
  // did the user press cancel?
  if (request.getParameter("cancel") != null) {
    response.sendRedirect("/omnigator/models/topicmap_complete.jsp?tm=" + 
                          request.getParameter("tm"));
    return;			  
  }

  // the user pressed sync or add, so go ahead

  //  (1) get a reference to the mapping file
  String cfgdir = getServletContext().getRealPath("/plugins/db2tm/");
  String file = request.getParameter("cfgfile");
  String cfgfile = cfgdir + File.separator + file;

  boolean force_rescan = request.getParameter("force_rescan") != null;
  
  //  (2) get the topic map
  TopicMapRepositoryIF rep = ContextUtils.getRepository(pageContext.getServletContext());
  TopicMapStoreIF store = rep.createStore(request.getParameter("tm"), false);
  try {
    TopicMapIF tm = store.getTopicMap();

    //  (3) run!
    if (request.getParameter("sync") != null)
      DB2TM.sync(cfgfile, tm, force_rescan);
    else
      DB2TM.add(cfgfile, tm);

    store.commit();
  } catch (Throwable e) {
    log.error("Couldn't run DB2TM sync", e);
    store.abort();
    throw e;
  } finally {
    store.close();
  }

  //  (4) go home
  response.sendRedirect("/omnigator/models/topicmap_complete.jsp?tm=" + 
                        request.getParameter("tm"));
%>
