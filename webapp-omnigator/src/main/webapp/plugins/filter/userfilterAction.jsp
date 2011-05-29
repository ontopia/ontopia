<%@ page language="java" 
    import="
    java.util.Collection, 
    java.util.ArrayList, 
    net.ontopia.topicmaps.nav.context.*, 
    net.ontopia.topicmaps.nav2.core.UserIF, 
    net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF, 
    net.ontopia.topicmaps.nav2.utils.NavigatorUtils, 
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils, 
    net.ontopia.topicmaps.core.*, 
    org.apache.log4j.Category" 
%>
<%
  // --------------------------------------------------------------
  // handles form submitted from page userContextFilter.jsp
  // and after setting up the user context filter for all
  // selected themes it redirectes back to original page we
  // started invoking the filter plugin.
  // --------------------------------------------------------------

  // initialization of logging facility
  Category cat = Category.getInstance("net.ontopia.Plugins.Filter.userfilterActionJSP");

  if (request.getParameter("action") != null &&
      request.getParameter("tm") != null) {
    
    // retrieve belonging topicmap object 
    NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);
    String tmid = request.getParameter("tm");
    TopicMapIF topicmap = navApp.getTopicMapById(tmid);
    try {

      Collection basenameThemes = new ArrayList();
      Collection variantnameThemes = new ArrayList();
      Collection occurrenceThemes = new ArrayList();
      Collection associationThemes = new ArrayList();
  
      if (request.getParameter("action").equals("Activate")) {
        // ---- get out of request interesting values
        // (1) BASENAME
        String [] basenames = request.getParameterValues("basename");
        TopicIF actTopic;
        if (basenames != null) {
    	for (int i=0; i < basenames.length; i++) {
    	  //System.out.println("topicmap: " + topicmap + ", id: " + tmid);
    	  cat.debug("basename[" + i +"] = " + basenames[i]);
    	  actTopic = (TopicIF) topicmap.getObjectById(basenames[i]);
    	  //System.out.println(" -> topic " + actTopic);
    	  basenameThemes.add( actTopic );
    	}
        }
        // (2) VARIANTNAME
        String [] variantnames = request.getParameterValues("variantname");
        if (variantnames != null) {
    	for (int i=0; i < variantnames.length; i++) {
    	  //System.out.println("topicmap: " + topicmap + ", id: " + tmid);
    	  cat.debug("variantname[" + i +"] = " + variantnames[i]);
    	  actTopic = (TopicIF) topicmap.getObjectById(variantnames[i]);
    	  //System.out.println(" -> topic " + actTopic);
    	  variantnameThemes.add( actTopic );
    	}
        }
        // (3) OCCURRENCE
        String [] occurrrences = request.getParameterValues("occurrence");
        if (occurrrences != null) {
    	for (int i=0; i < occurrrences.length; i++) {
    	  //System.out.println("topicmap: " + topicmap + ", id: " + tmid);
    	  cat.debug("occurrence[" + i + "] = " + occurrrences[i]);
    	  actTopic = (TopicIF) topicmap.getObjectById(occurrrences[i]);
    	  //System.out.println(" -> topic " + actTopic);
    	  occurrenceThemes.add( actTopic );
    	}
        }
        // (4) ASSOCIATION
        String [] associations = request.getParameterValues("association");
        if (associations != null) {
    	for (int i=0; i < associations.length; i++) {
    	  //System.out.println("topicmap: " + topicmap + ", id: " + tmid);
    	  cat.debug("association[" + i + "] = " + associations[i]);
    	  actTopic = (TopicIF) topicmap.getObjectById(associations[i]);
    	  //System.out.println(" -> topic " + actTopic);
    	  associationThemes.add( actTopic );
    	}
        }
      }
  
      
      // Get User Filter Context out of session
      UserIF user = FrameworkUtils.getUser(pageContext);
      UserFilterContextStore userFilterContext = user.getFilterContext();
      
      // if no context exists create new storage object for all scope themes
      if (userFilterContext == null)
        userFilterContext = new UserFilterContextStore();
  
      // --- Action: "Activate"
      if (request.getParameter("action").equals("Activate")) {
        // set themes for userContext and this topicmap
        userFilterContext.setScopeTopicNames(topicmap, basenameThemes);
        userFilterContext.setScopeVariantNames(topicmap, variantnameThemes);
        userFilterContext.setScopeOccurrences(topicmap, occurrenceThemes);
        userFilterContext.setScopeAssociations(topicmap, associationThemes);
        cat.debug("Activated Filter. UserFilterContext: " +
  		userFilterContext.toString());
      }
      // --- Action: "Reset"
      else if (request.getParameter("action").equals("Reset")) {
        // reset user filter context to a fresh 
        userFilterContext.resetScopeTopicNames(topicmap);
        userFilterContext.resetScopeVariantNames(topicmap);
        userFilterContext.resetScopeOccurrences(topicmap);
        userFilterContext.resetScopeAssociations(topicmap);
        cat.debug("Reset Filter.");
      }

    } finally {
      navApp.returnTopicMap(topicmap);
    }
  }
    
  // check that redirection makes sense
  String redirect = (String) request.getParameter("redirect");
  if (redirect==null || redirect.equals(""))
    redirect="../index.jsp";

  // go back to page we originally came from
  response.sendRedirect(redirect);

%>
