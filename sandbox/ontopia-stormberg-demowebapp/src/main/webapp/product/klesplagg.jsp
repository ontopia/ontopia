<%@ taglib prefix="tolog" uri="http://psi.ontopia.net/jsp/taglib/tolog" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

    <tolog:context topicmap="stormberg.xtm">
        <tolog:declare>
            using ontopia for i"http://psi.ontopia.net/ontology/"
            using stormberg for i"http://psi.stormberg.no/"
        </tolog:declare>
        <tolog:set var="TOPIC" reqparam="topic"/>
        <tolog:if var="TOPIC">

            <c:set var="topicName"><tolog:out var="$TOPIC"/></c:set>
            <c:set var="topicId"><tolog:oid var="$TOPIC"/></c:set>
            <head>
                <title>${topicName} - Stormberg</title>
            </head>
            <h1>${topicName}</h1>

            <tolog:if query="stormberg:bildeadresse(%TOPIC%, $BILDEADRESSE)?">
                <c:set var="bildeadresse"><tolog:out var="$BILDEADRESSE"/></c:set>
                <img src="${bildeadresse}" alt="Bilde av ${topicName}"/>
            </tolog:if>

            <tolog:if query="ontopia:description(%TOPIC%, $BESKRIVELSE)?">
                <h2>Beskrivelse</h2>
                <tolog:out var="$BESKRIVELSE"/>
            </tolog:if>


            <tolog:if query="stormberg:veiledende-pris(%TOPIC%, $VEILEDENDE)?">
                <h2>Veiledende pris</h2>
                <tolog:out var="$VEILEDENDE"/>
            </tolog:if>

            <tolog:if query="stormberg:tilbudspris(%TOPIC%, $TILBUDSPRIS)?">
                <h2>Tilbudspris</h2>
                <tolog:out var="$TILBUDSPRIS"/>
            </tolog:if>

            <tolog:foreach query="stormberg:url(%TOPIC%, $URL)?">
                <h2>stormberg.no webadresse</h2>
                <c:set var="url"><tolog:out var="$URL"/></c:set>
                <a href="${url}">${topicName}</a>
            </tolog:foreach>

        </tolog:if>
    </tolog:context>
