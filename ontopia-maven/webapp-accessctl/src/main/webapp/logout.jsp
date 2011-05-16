<% 
  String language = request.getParameter("language");
  session.invalidate();
  response.sendRedirect("/accessctl/index.jsp?language=" + language); 
%>
