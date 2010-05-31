<div id="topbox" class="topbox">
  <% if (!mainpage) { %>
    <a href="index.jsp"><img src="images/black-logo.png" width="100"
         style="position: absolute; top: 0pt; left: 100px"></a>
  <% } %>

  <div id="searchbox">
    <form action="search.jsp" method="get">
      <input type="text" name="query" class="topSearchField" />
      <input type="submit" value="Search" class="topSearchBut" />
    </form>
  </div>

  <%
    String url = request.getServletPath();
    String fullurl = request.getRequestURI() + "?" + request.getQueryString();
  %>

  <div id="menu">
    <span class="item"
      <% if (url.contains("download.jsp")) { %> id="itemselected" <% } %>
         ><a href="download.jsp">DOWNLOAD</a></span>
    <span class="sep"> | </span>
    <span class="item"
      <% if (url.contains("learn.jsp")) { %> id="itemselected" <% } %>
         ><a href="learn.jsp">LEARN!</a></span>
    <span class="sep"> | </span>
    <span class="item"
      <% if (fullurl.contains("page.jsp?id=get-involved")) { %> id="itemselected" <% } %>
         ><a href="page.jsp?id=get-involved">GET INVOLVED</a></span>
    <span class="sep"> | </span>
    <span class="item"
      <% if (url.contains("services.jsp")) { %> id="itemselected" <% } %>
         ><a href="services.jsp">SERVICES</a></span>
    <span class="sep"> | </span>
    <span class="item"
      <% if (fullurl.contains("page.jsp?id=about")) { %> id="itemselected" <% } %>
         ><a href="page.jsp?id=about">ABOUT</a></span>
    <span class="sep"> | </span>
    <span class="item"
      <% if (fullurl.contains("page.jsp?id=ontopia-forge")) { %> id="itemselected" <% } %>
         ><a href="page.jsp?id=ontopia-forge">ONTOPIA FORGE</a></span>
  </div>
</div>
