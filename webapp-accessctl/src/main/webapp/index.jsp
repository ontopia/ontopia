<%@ include file="fragment/common-header.jsp" %>

<HTML>
<HEAD>
  <title>Accessctl</title>
  <link rel='stylesheet' href='accessctl.css' />
</HEAD>
<BODY>

<P>
  <A href="protected/main.jsp?tm=userman.ltm&language=<%= language %>">
    <fmt:message key="ClickEnterAdminPage"/>
  </A>
  <BR>
  <A href="protected/password.jsp?tm=userman.ltm&language=<%= language %>">
    <fmt:message key="ClickEnterPasswordPage"/>
  </A>
</P>
<P align="center">
<A href="index.jsp?tm=userman.ltm&language=en">English</A>
| <A href="index.jsp?tm=userman.ltm&language=de">Deutsch</A>
| <A href="index.jsp?tm=userman.ltm&language=nl">Nederlands</A>
| <A href="index.jsp?tm=userman.ltm&language=no">Norsk</A>
</P>
</BODY>
</HTML>
