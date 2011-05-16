<!--set languagefile-->
<% String language = request.getParameter("language");
  if (! (language == null || language.equals("") 
      || language.equals("null"))) { %>
  <fmt:setLocale value="<%= language %>"/>
<% } %>
<fmt:setBundle basename="languagepack"/>
