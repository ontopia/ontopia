<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Example: embedded iframe</title>
</head>

<body>
<h1>Example: embedded iframe</h1>

<script language="JavaScript">
function reloadIframe() {
  document.getElementById('the_iframe').contentDocument.location.reload(true);
}
function calcHeight() {
  //find the height of the internal page
  var odoc = document.getElementById('the_iframe').contentWindow.document;
  var the_height = odoc.body.scrollHeight;
  //change the height of the iframe 
  document.getElementById('the_iframe').height = the_height+50;
}
</script>

<script type="text/javascript">
function popup(url) {
 var width  = 700;
 var height = 600;
 var left   = (screen.width  - width)/2;
 var top    = (screen.height - height)/2;
 var params = 'width='+width+', height='+height;
 params += ', top='+top+', left='+left;
 params += ', directories=no';
 params += ', location=no';
 params += ', menubar=no';
 params += ', resizable=no';
 params += ', scrollbars=no';
 params += ', status=no';
 params += ', toolbar=no';
 newwin=window.open(url,'windowname5', params);
 newwin.onbeforeunload = window.opener.reloadIframe;
 if (window.focus) {newwin.focus()}
 return false;
}
</script>

<a href="javascript: void(0)" 
   onclick="popup('/ontopoly/?wicket:bookmarkablePage=:ontopoly.pages.EmbeddedInstancePage&topicId=<%=
   request.getParameter("topicId") %>&topicTypeId=<%=
   request.getParameter("topicTypeId") %>&topicMapId=<%=
   request.getParameter("topicMapId") %>')">Popup</a>
<a href="javascript: reloadIframe()">Refresh</a>

<iframe
  width="100%" id="the_iframe" onLoad="calcHeight();" scrolling="NO" frameborder="0"
  src="/ontopoly/?wicket:bookmarkablePage=:ontopoly.pages.EmbeddedInstancePage&topicId=<%=
   request.getParameter("topicId") %>&topicTypeId=<%=
   request.getParameter("topicTypeId") %>&topicMapId=<%=
   request.getParameter("topicMapId") %>&ro=<%=
   request.getParameter("ro") %>">
</iframe>

<%-- note that views are also supported, using viewId=xxx --%>

</body>
</html>
