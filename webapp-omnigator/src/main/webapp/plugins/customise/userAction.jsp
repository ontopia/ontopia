<%@ page language="java" 
    import="
    java.util.*,
    java.net.URLEncoder,
    net.ontopia.topicmaps.nav2.core.*,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
    net.ontopia.utils.StringUtils" %>

<%
  // handling submitted 'customise' form (user.jsp)
	
  // get new attributes
  String model = (String) request.getParameter("model");
  String view = (String) request.getParameter("view");
  String skin = (String) request.getParameter("skin");	
  String redirect = (String) request.getParameter("redirect");
  String currentModel = (String) request.getParameter("currentModel");
	
  // a bit of cleaning to avoid writing nulls to session
  if (model == null)
    model = "";
  if (view == null)
    view = "";
  if (skin == null)
    skin = "";
  if (redirect == null || redirect.equals(""))
    redirect = "../../models/index.jsp";

  // replace model name in redirect string if it differs
  if (!model.equals(currentModel)) {
    redirect = StringUtils.replace(redirect, "_"+currentModel+".jsp",
				   "_"+model+".jsp");
  }
	
  // set the values in the user session object
  UserIF user = FrameworkUtils.getUser(pageContext);
  if (user != null) {
    user.setModel(model);
    user.setView(view);
    user.setSkin(skin);
  }

  String resource;
  // bit of tricky work if we are dealing with browse
  if (view.equals("frames")) {
    redirect = "expander.jsp?redirect=" + URLEncoder.encode("../../browse.jsp?info=" + URLEncoder.encode(redirect));
  } else {
    // load topic.jsp into the top of the window
    // this is tricky because we are in a frame 
    // expansion only possible through some client side javascript targetting
    redirect = "expander.jsp?redirect=" + URLEncoder.encode(redirect);
  }

  response.sendRedirect(redirect);
%>
