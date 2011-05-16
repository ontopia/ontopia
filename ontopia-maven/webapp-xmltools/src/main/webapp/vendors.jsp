<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog' %>

<tolog:context topicmap="xmltools-tm.xtm">
<title>The vendors</title>
<h1>The vendors</h1>

<ul>
  <tolog:foreach query="  TMAT_ProductVendor($VENDOR : TMAR_Creator,
                                             $PRODUCT : TMTT_Product)
                        order by $VENDOR, $PRODUCT?" groupBy="VENDOR">
    <li><a href="vendor.jsp?id=<tolog:id var="VENDOR"/>"
          ><tolog:out var="VENDOR"/></a>

      <ul>
      <tolog:foreach>
        <li><a href="product.jsp?id=<tolog:id var="PRODUCT"/>"
              ><tolog:out var="PRODUCT"/></a>
      </tolog:foreach>
      </ul>
  </tolog:foreach>
</ul>

</tolog:context>
