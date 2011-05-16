<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/framework' prefix='framework' %>
<framework:response/>

<template:insert template='/views/template_%view%.jsp'>

<template:put name='title' body='true'>[Omnigator] Feedback form</template:put>

<template:put name='heading' body='true'>
  <h1 class="boxed">Give Ontopia feedback on the Omnigator</h1>
</template:put>

<template:put name='toplinks' body='true'>
  <a href="../../models/index.jsp">Welcome</a>
</template:put>

<template:put name="content" body="true">
  <form method=post action="http://www.ontopia.net/cgi-bin/formmail.py">
    <input type="hidden" name="EMAIL" value="dev@ontopia.net">
    <input type="hidden" name="REQUIRED" value="email feedback">

    <table>
    <tr><td>Your name
        <td><input type=text name=name> <br> 

    <tr><td>Your email
        <td><input type=text name=email> <br> 

    <tr><td valign=top>Your feedback
        <td><textarea name=feedback rows=20 cols=40></textarea>

    <tr><td colspan=2><input type=submit value=Send>
    </table>
  </form>
</template:put>

<template:put name="navigation" body="true">
  <p class=text>
  This form allows you to provide Ontopia with feedback on the
  Omnigator, which will help us develop it into a better product.  We
  don't promise to implement your suggestions, but we do promise to
  consider them seriously. So please take a moment to tell us what you
  think of the Omnigator, what features you would like to see, what
  bugs you have found, or even what you are using it for.  
  </p>
  </template:put>

<%-- ============== Outsourced application wide standards ============== --%>
<template:put name='application' content='/fragments/application.jsp'/>
<template:put name='header-tagline' content='/fragments/tagline-header.jsp'/>
<template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'/>

</template:insert>
