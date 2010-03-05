<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:wicket="http://wicket.apache.org/">
<head>
<link rel="stylesheet" type="text/css" href="/ontopoly/resources/ontopoly.resources.Resources/stylesheet.css" />

<script type="text/javascript" src="/ontopoly/resources/org.apache.wicket.markup.html.WicketEventReference/wicket-event.js"></script> 
<script type="text/javascript" src="/ontopoly/resources/org.apache.wicket.ajax.WicketAjaxReference/wicket-ajax.js"></script> 
<script type="text/javascript" src="/ontopoly/resources/ontopoly.components.ContextMenuPanel/ContextMenuPanel.js"></script> 
<link rel="stylesheet" type="text/css" href="/ontopoly/resources/ontopoly.components.ContextMenuPanel/ContextMenuPanel.css" /> 
<script type="text/javascript" src="/ontopoly/resources/org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow/res/modal.js"></script> 
<link rel="stylesheet" type="text/css" href="/ontopoly/resources/org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow/res/modal.css" /> 

<script type="text/javascript" src="/ontopoly/resources/ontopoly.resources.Resources/jquery/jquery.js"></script>
<script  type="text/javascript">
$(document).ready(function() {
  $('#content').load("/ontopoly/instance-embedded/${param.topicMapId}/${param.topicId}");
 });
</script>
<title>Example: JQuery AJAX load plugin</title>
</head>

<body>
<h1>Example: JQuery AJAX load plugin</h1>

<div id="content"></div>

</body>
</html>
