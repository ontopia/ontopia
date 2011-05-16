<%@ page language="java" 
           import="net.ontopia.persistence.proxy.*,
                   net.ontopia.topicmaps.core.TopicMapIF,
                   net.ontopia.topicmaps.core.TopicMapStoreIF,
                   net.ontopia.topicmaps.impl.rdbms.*,
                   net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag,
                   net.ontopia.topicmaps.nav2.utils.FrameworkUtils" %>

<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/template' prefix='template'%>
<%@ taglib uri='http://psi.ontopia.net/jsp/taglib/tolog' prefix='tolog'%>

<%-- JSP page that clears the shared cache for a given topic map --%>

<tolog:context topicmap='<%= request.getParameter("tm") %>'>
<%
String rp_id = request.getParameter("id");
if (rp_id != null) {
  ContextTag ctag = FrameworkUtils.getContextTag(request);
  TopicMapStoreIF store = ctag.getTopicMap().getStore();
  if (store instanceof RDBMSTopicMapStore) {
    TransactionIF txn = ((RDBMSTopicMapTransaction)((RDBMSTopicMapStore)store).getTransaction()).getTransaction();
    StorageCacheIF scache = txn.getStorageAccess().getStorage().getStorageCache();
    if (scache != null) {
      IdentityIF identity = null;
      long numid = Long.parseLong(rp_id.substring(1), 10);
      switch ( rp_id.charAt(0) ) {
      case 'T':
        identity = txn.getAccessRegistrar().createIdentity(Topic.class, numid);
        break;
      case 'A':
        identity = txn.getAccessRegistrar().createIdentity(Association.class, numid);
        break;
      case 'O':
        identity = txn.getAccessRegistrar().createIdentity(Occurrence.class, numid);
        break;
      case 'B':
        identity = txn.getAccessRegistrar().createIdentity(TopicName.class, numid);
        break;
      case 'N':
        identity = txn.getAccessRegistrar().createIdentity(VariantName.class, numid);
        break;
      case 'R':
        identity = txn.getAccessRegistrar().createIdentity(AssociationRole.class, numid);
        break;
      case 'M':
        identity = txn.getAccessRegistrar().createIdentity(TopicMap.class, numid);
        break;
      default:
        identity = null;
      }
      if (identity != null) {
        scache.evictIdentity(identity, true);
        out.write("Evicted " + identity + " from shared cache on topic map '" + request.getParameter("tm") + "'");
      } else {
        out.write("Identity " + identity + " not found in topic map '" + request.getParameter("tm") + "'");
      }
    } else {
      out.write("Topic map '" + request.getParameter("tm") + "' have no shared cache.");
    }
  }
} else {
  out.write("Missing either 'id' or 'class' parameters.");
}
%>
</tolog:context>