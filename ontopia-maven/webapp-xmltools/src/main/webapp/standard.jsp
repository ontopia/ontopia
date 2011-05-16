<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog' %>

<tolog:context topicmap="xmltools-tm.xtm">
<tolog:set var="standard" reqparam="id"/>

<title><tolog:out var="standard"/></title>
<h1><tolog:out var="standard"/></h1>

<!-- OCCURRENCES -->
<tolog:if query="occurrence(%standard%, $OCC)?">
<table>
<tolog:foreach query="select $OCCTYPE, $URI from
                        occurrence(%standard%, $OCC),
                        type($OCC, $OCCTYPE),
                        resource($OCC, $URI)
                      order by $OCCTYPE, $URI?">
  <tr><th><tolog:out var="OCCTYPE"/>:
      <td><a href="<tolog:out var="URI"/>"><tolog:out var="URI"/></a>
</tolog:foreach>
</table>
</tolog:if>

<!-- PRODUCTS IMPLEMENTING THE STANDARD -->

<tolog:if query="TMAT_StandardImplemented(%standard% : TMTT_Standard,
                                          $PRODUCT : TMTT_Product)?">
<p>
This standard has been implemented by the following products:
</p>

<ul>
<tolog:foreach query="TMAT_StandardImplemented(%standard% : TMTT_Standard,
                                          $PRODUCT : TMTT_Product)
                      order by $PRODUCT?">
  <li><a href="product.jsp?id=<tolog:id var="PRODUCT"/>"
        ><tolog:out var="PRODUCT"/></a></li>
</tolog:foreach>
</ul>
</tolog:if>


<!-- PRODUCTS IMPLEMENTING THE STANDARD -->

<tolog:if query="TMAT_StandardsUse(%standard% : TMAR_UsedIn,
                                   $PRODUCT : TMAR_UsedBy)?">
<p>
This standard is used by the following products:
</p>

<ul>
<tolog:foreach query="TMAT_StandardsUse(%standard% : TMAR_UsedIn,
                                        $PRODUCT : TMAR_UsedBy)
                      order by $PRODUCT?">
  <li><a href="product.jsp?id=<tolog:id var="PRODUCT"/>"
        ><tolog:out var="PRODUCT"/></a></li>
</tolog:foreach>
</ul>
</tolog:if>

</tolog:context>
