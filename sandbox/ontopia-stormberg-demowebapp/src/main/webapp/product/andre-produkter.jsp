<%@ taglib prefix="tolog" uri="http://psi.ontopia.net/jsp/taglib/tolog" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

    <tolog:context topicmap="stormberg.xtm">
        <tolog:declare>using stormberg for i"http://psi.stormberg.no/"</tolog:declare>
        <tolog:set var="TOPIC" reqparam="topic"/>
        <tolog:if var="TOPIC">

            <h1>Andre aktuelle produkter</h1>

            <tolog:foreach
                    query="stormberg:aktuelle-produkter(%TOPIC% : stormberg:klesplagg, $OTHER: stormberg:klesplagg)?">
                <c:set var="otherProductName"><tolog:out var='OTHER'/></c:set>
                <c:set var="otherProductID"><tolog:id var='OTHER'/></c:set>

                <div>
                    <a href="${pageContext.request.contextPath}/product/klesplagg.jsp?topic=${otherProductID}">${otherProductName}</a>
                </div>
            </tolog:foreach>
        </tolog:if>
    </tolog:context>
