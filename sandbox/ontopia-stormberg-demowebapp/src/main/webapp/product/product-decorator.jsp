<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ page isELIgnored="false" %>
<style type="text/css">
    #left {
        float: left;
        width:70%;
    }
    #right {
        float: left;
    }
</style>


<head>
    <title><decorator:title default="Single product"/></title>
    <decorator:head/>
</head>
<body>
<div id="left">
    <decorator:body/>
</div>
<div id="right">
    <jsp:include page="/product/andre-produkter.jsp"/>
</div>
</body>
</html>


