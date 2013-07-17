<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Ontopia</title>
<link rel="stylesheet" type="text/css" href="frontpage.css">

<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
</head>

<body>

  <div id="header">
    <table width="100%" cellspacing="0" cellpadding="0" border="0">
      <tbody>
        <tr>
          <!-- this image is transparent, but needed for the layout. the text is
               really part of the background -->
          <td class="topLinks"><img src="background.gif"
            width="550" height="86"><br></td>
          <td align="right" valign="top">
            <div id="oksmenu">
              <a href="/">Home</a>&nbsp;|&nbsp;<a href="/manage/manage.jsp">Manage</a>&nbsp;|&nbsp;<a href="http://www.ontopia.net/">Website</a>&nbsp;|&nbsp;<a href="http://groups.google.com/group/ontopia" title="Mailing list">Support</a>&nbsp;|&nbsp;<a href="/about.jsp">About</a>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div id="contentarea">

  <table class=frontboxes>
  <tr class=toprow> <!-- stupid MSIE doesn't understand :first-child -->

    <td><!-- ===== CREATE ===== -->
    <table class=frontbox><tr><td>
      <h2>Create</h2>

      <table class=anglelink><tr><td class=angles>>>
      <td><a href="ontopoly/">Create and edit topic maps with Ontopoly</a></table>

      <b>Features:</b>
      <ul>
        <li>Build and manage ontologies
        <li>Manage topic map content
      </ul>
    </table>

    <td><!-- ===== NAVIGATE ===== -->
    <table class=frontbox><tr><td>
      <h2>Navigate</h2>

      <table class=anglelink><tr><td class=angles>>>
      <td><a href="omnigator/">Browse topic maps with Omnigator</a></table>

      <b>Features:</b>

      <ul>
        <li>Browse any topic map
        <li>Query topic maps with powerful tolog queries
        <li>Merge topic maps
      </ul>
    </table>

    <td><!-- ===== VISUALIZE ===== -->
    <table class=frontbox><tr><td>
      <h2>Visualize</h2>

      <table class=anglelink><tr><td class=angles>>>
      <td><a href="omnigator/plugins/viz/viz.jsp?tm=ItalianOpera.ltm&id=puccini">Test
      Vizigator on the Opera topic map</a></table>

      <b>Features:</b>

      <ul>
        <li>Visualize any topic map within Ontopoly or Omnigator
        <li>Navigate freely in the visualized topic map
        <li>Customize display
      </ul>
    </table>

  <tr><td>

    <!-- ===== TALK TO US ===== -->
    <table class=frontbox><tr><td>
      <h2>Talk to us</h2>

      <p>
      <ul>
        <li><a href="http://groups.google.com/group/ontopia">Mailing list</a>
        <li><a href="http://code.google.com/p/ontopia/issues/list">Report a bug</a><br><br>
      </ul>
    </table>

    <td>
    <!-- ===== LEARN ===== -->
    <table class=frontbox><tr><td>
      <h2>Learn</h2>

      <br><b>User guides:</b>

      <ul>
        <li><a href="ontopoly/doc/user-guide.html">Ontopoly</a>
        <li><a href="omnigator/docs/omnigator/userguide.html">Omnigator</a>
        <li><a href="omnigator/docs/vizigator/userguide.html">Vizigator</a>
      </ul>
    </table>

    <td>
    <!-- ===== MANAGE ===== -->
    <table class=frontbox><tr><td>
      <h2>Manage</h2>

      <p>
      <ul>
        <li><a href="manage/manage.jsp">Administration Console</a>
       <li><a href="accessctl/">User Administration</a>
       <li><a href="manage/plugins/ftadmin/index_admin.jsp">Full-Text Indexes</a>
      </ul>
    </table>
  </table>
  </div>

  <div id="footer">
    <table width="99%" border="0" cellpadding="0" cellspacing="0"
      id="footerTable">
      <tr>
        <td>Copyright &copy; 2001-2010 <a href="http://www.ontopia.net/">Ontopia</a>.</td>
        <td align="right"><b>*</b> built using <a
          href="http://www.ontopia.net/">Ontopia <%= net.ontopia.Ontopia.getVersion() %></a>.</td>
      </tr>
    </table>
  </div>

</body>
</html>
