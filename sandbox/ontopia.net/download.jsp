<%@ include file="fragments/declarations.jsp"%>

<tolog:context topicmap="ontopia.xtm">
<%@ include file="fragments/tolog.jsp"%>

<template:insert template='fragments/template-2col.jsp'>
<template:put name='title'>Download</template:put>
<template:put name="breadcrumbs">DOWNLOAD</template:put>

<template:put name="menu"><!-- EMPTY --></template:put>

<template:put name="main">

<p>&nbsp;</p>

<div id="success">
  <a href="http://code.google.com/p/ontopia/downloads/list"
    ><img src="images/download-big.png"></a>
</div>

<p> </p>

<table>
<tr><td width="65%">

<p><b>Download Ontopia</b></p>

<p>Click the link above to download the entire Ontopia product. This
comes as a zip file containing a Tomcat web server set up with the
Omnigator, Ontopoly, and Vizigator end-user products, and also the
full developer toolkit consisting of the Topic Maps engine,
Navigator Framework, DB2TM, and so on.</p>

<p>Once you have downloaded, unpack the zip file, and open the
<tt>index.html</tt> file inside the <tt>ontopia</tt> directory. This
has an overview of the product documentation, including a link to the
installation guide.</p>

<td width="35%">
<p><b>RELATED INFORMATION</b></p>

<ul>
<li><a href="http://groups.google.com/group/ontopia">Join the mailing list</a>
<li><a href="http://code.google.com/p/ontopia/downloads/list">Download the software</a>
<li><a href="http://code.google.com/p/ontopia/source/checkout">Check out the source code</a>
</ul>

</table>

</template:put>

</template:insert>
</tolog:context>