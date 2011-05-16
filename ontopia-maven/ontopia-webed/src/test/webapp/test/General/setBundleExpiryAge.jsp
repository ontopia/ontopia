<!-- Sets the bundleExpiryAge of the user to some value, given by the request
  parameter 'bundleExpiryAge'. If this request parameter is not set then
  bundleExpiryAge is reset to it's initial value. -->
<%@ page language="java" 
    import="
    net.ontopia.topicmaps.nav2.impl.framework.User,
    net.ontopia.topicmaps.nav2.core.UserIF,
    net.ontopia.topicmaps.nav2.utils.FrameworkUtils,
    net.ontopia.topicmaps.nav2.utils.NavigatorUtils" 
%>

<%
  NavigatorUtils.getNavigatorApplication(pageContext);
  User user = (User)FrameworkUtils.getUser(pageContext);
  String bundleExpiryAgeString = request.getParameter("bundleExpiryAge");
  if (!(bundleExpiryAgeString == null || bundleExpiryAgeString.equals(""))) {
    long bundleExpiryAge = Long.valueOf(bundleExpiryAgeString).longValue();
    user.setBundleExpiryAge(bundleExpiryAge);
  } else
    user.resetBundleExpiryAge();    
%>
