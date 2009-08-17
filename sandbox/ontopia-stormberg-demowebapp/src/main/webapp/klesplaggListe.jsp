<%@ taglib prefix="tolog" uri="http://psi.ontopia.net/jsp/taglib/tolog" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<h1>Klesplaggliste</h1>
<tolog:context topicmap="stormberg.xtm">
    <tolog:declare>using stormberg for i"http://psi.stormberg.no/"</tolog:declare>
    <tolog:query name="TOPICLIST">
        instance-of($KLESPLAGG, stormberg:klesplagg)?
    </tolog:query>
    <tolog:foreach query="TOPICLIST">
        <div>
            <c:set var="topicName"><tolog:out var="KLESPLAGG"/></c:set>
            <c:set var="topicId"><tolog:oid var='KLESPLAGG'/></c:set>

            <a href="${pageContext.request.contextPath}/product/klesplagg.jsp?topic=${topicId}">${topicName}</a>
        </div>
    </tolog:foreach>
</tolog:context>