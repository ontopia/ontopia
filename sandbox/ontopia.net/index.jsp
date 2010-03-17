<%@ include file="fragments/declarations.jsp"%>

<tolog:context topicmap="ontopia.xtm">
<%@ include file="fragments/tolog.jsp"%>

<template:insert template='fragments/template.jsp'>
<template:put name='title'>Ontopia home page</template:put>

<template:put name="body">

<div id="midbox" >
  <img src="images/logoBig.gif" alt="Ontopia" id="biglogo">
	
  <div id="top_section">
    <div id="top_section_left">
      <h3>WELCOME TO THE LAND OF ONTOPIA</h3>
      <div id="frontpage_ingress">A complete set of tools for
      building, maintaining and deploying Topic Maps-based
      applications</div>

      <p><a href="learn.jsp">Learn more <img
      src="images/arrows.png"></a></p>

    </div>
    <div id="top_section_right" style="padding: 15px 0px 0px 15px">
      <a href="download.jsp"><img src="images/download-button.png"></a>
    </div>
    <div style="clear:both"></div>
  </div>
</div>

<div style="clear:both"></div>

<div id="mid_section">

<div id="bloghead"><b>BLOG</b></div>

<table id="bottombox" cellspacing="0" width="100%">
<tr><td id=blog class=normal>

<tolog:foreach query="
  instance-of($POST, blog:Post),
  dc:date($POST, $DATE),
  dc:description($POST, $DESCFULL),
  str:substring($DESC, $DESCFULL, 0, 150),
  subject-locator($POST, $URL)
  order by $DATE desc limit 2?">
<p><b><a href="<tolog:out var="URL"/>"
  ><tolog:out var="POST"/></a></b><br>
  <tolog:out var="DESC" escape="false"/>...
</p>

<p class="byline"><tolog:out var="DATE"/> | by ontopia</p>
</tolog:foreach>

<ul>
<tolog:foreach query="
  instance-of($POST, blog:Post),
  dc:date($POST, $DATE),
  subject-locator($POST, $URL)
  order by $DATE desc limit 8 offset 2?">
<li><a href="<tolog:out var="URL"/>"><tolog:out var="POST"/></a>
</tolog:foreach>
</ul>

<td id=spacer>&nbsp;

<td class=normal id=leftcol>
<p><b>NEWS</b></p>

<tolog:foreach query="
  instance-of($POST, o:news-item),
  dc:date($POST, $DATE),
  o:abstract($POST, $ABSTRACT)
  order by $DATE desc limit 1?">
<p><b><a href="page.jsp?id=<tolog:id var="POST"/>"
  ><tolog:out var="POST"/></a></b><br>
  <tolog:out var="ABSTRACT"/>...
</p>
</tolog:foreach>

<div class=ider> </div>

<p><b>LEARN!</b></p>

<tolog:foreach query="
  instance-of($SECTION, o:section),
  o:order($SECTION, $ORDER),
  dc:description($SECTION, $DESC)
  order by $ORDER limit 2?
  ">
  <p><b><a href="section.jsp?id=<tolog:id var="SECTION"/>"
          ><tolog:out var="SECTION"/></a></b><br>
  <tolog:out var="DESC"/></p>
</tolog:foreach>

<p><a href="learn.jsp">Learn! <img src="images/arrows.png"></a></p>

<td class=normal id=rightcol>
<p><b>GET INVOLVED</b></p>

<p><b><a href="...">It's up to you</a></b><br>
The Ontopia product is created by the Ontopia community, and so the
product is what the community makes it. Anyone can join and contribute
to help make the product better. Follow the links below to get involved.</p>

<ul>
<li><a href="http://groups.google.com/group/ontopia">Join the mailing list</a>
<li><a href="download.jsp">Download the software</a>
<li><a href="http://code.google.com/p/ontopia/source/checkout">Check out the source code</a>
</ul>

<p><a href="page.jsp?id=2756">Get involved <img src="images/arrows.png"></a></p>

<div class=ider> </div>

<p><b>ONTOPIA'S SUCCESS STORIES</b></p>

<%!

static class SelectRandomly {

  public static TopicIF selectAtRandom(String variable,
                                       PageContext pageContext) {
    List topics = new ArrayList(ContextUtils.getValue(variable, pageContext));
    Random rnd = new Random();
    int ix = rnd.nextInt(topics.size());
    return (TopicIF) topics.get(ix);
  }

  public static int count(String variable, PageContext pageContext) {
    List topics = new ArrayList(ContextUtils.getValue(variable, pageContext));
    return topics.size();
  }

}
%>

<tolog:set var="allprojects" query="instance-of($PROJECT, o:project)?"/>
<%
  TopicIF photo = SelectRandomly.selectAtRandom("allprojects", pageContext);
  ContextUtils.setSingleValue("prosjekt", pageContext, photo);
%>

<div id=success>
  <p><a href="page.jsp?id=<tolog:id var="prosjekt"/>"
      ><tolog:out var="prosjekt"/></a></p>
  <a href="page.jsp?id=<tolog:id var="prosjekt"/>"
    ><img src="images/<tolog:out query="o:image(%prosjekt%, $IMAGE)?"/>"></a>
</div>

<p><a href="success.jsp">More success stories <img
src="images/arrows.png"></a></p>

</table>
</template:put>

</template:insert>
</tolog:context>