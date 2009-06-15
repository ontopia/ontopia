<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template'  prefix='template'  %>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog'  prefix='tolog'      %>

<tolog:if query="{ transforms-from(%topic% : source, $TXN : method) |
                   transforms-to(%topic% : target, $TXN : method) }?">
<h2>Transliterations/transcriptions</h2>

<!-- transxions away from this topic -->
<tolog:if query="transforms-from(%topic% : source, $TXN : method)?">
<table>
<tr><th>Name   </th><th>Target   </th><th>Type</th></tr>

<tolog:foreach query="transforms-from(%topic% : source, $TXN : method),
                      object-id($TXN, $TXNID),
                      transforms-to($TXN : method, $TARGET : target),
                      object-id($TARGET, $TARGETID),
                      instance-of($TXN, $TYPE)?">
<tr><td><a href="transxion.jsp?id=<tolog:out var="TXN"/>"><tolog:out var="TXN"/></a>
    <td><a href="script.jsp?id=<tolog:out var="TARGET"/>"><tolog:out var="TARGET"/></a>
    <td><tolog:out var="TYPE"/></td></tr>
</tolog:foreach>
</table>
</tolog:if>

<!-- transxions to this topic -->
<tolog:if query="transforms-to(%topic% : target, $TXN : method)?">
<table>
<tr><th>Name   </th><th>Source   </th><th>Type</th></tr>

<tolog:foreach query="transforms-to(%topic% : target, $TXN : method),
                      object-id($TXN, $TXNID),
                      transforms-from($TXN : method, $SOURCE : source),
                      object-id($SOURCE, $SOURCEID),
                      instance-of($TXN, $TYPE)?">
<tr><td><a href="transxion.jsp?id=<tolog:out var="TXNID"/>"><tolog:out var="TXN"/></a>
    <td><a href="script.jsp?id=<tolog:out var="SOURCEID"/>"><tolog:out var="SOURCE"/></a>
    <td><tolog:out var="TYPE"/></td></tr>
</tolog:foreach>
</table>
</tolog:if>

</tolog:if>
