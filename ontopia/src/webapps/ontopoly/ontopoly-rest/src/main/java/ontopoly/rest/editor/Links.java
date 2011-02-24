package ontopoly.rest.editor;

import javax.ws.rs.core.UriInfo;

import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;
import ontopoly.rest.editor.spi.PrestoView;

public class Links {

  protected static String getEditLinkFor(UriInfo uriInfo, PrestoTopic topic, PrestoView fieldsView) {
    return uriInfo.getBaseUri() + "editor/topic/" + fieldsView.getSchemaProvider().getDatabaseId() + "/" + topic.getId() + "/" + fieldsView.getId();
  }

  protected static String getEditLinkFor(UriInfo uriInfo, PrestoTopic topic, PrestoView fieldsView, boolean readOnlyMode) {
    return uriInfo.getBaseUri() + "editor/topic/" + fieldsView.getSchemaProvider().getDatabaseId() + "/" + topic.getId() + "/" + fieldsView.getId() + (readOnlyMode ? "?readOnly=true" : "");
  }

  protected static String getCreateLinkFor(UriInfo uriInfo, PrestoType topicType, PrestoView fieldsView) {
    return uriInfo.getBaseUri() + "editor/topic/" + topicType.getSchemaProvider().getDatabaseId() + "/_" + topicType.getId() + "/" + fieldsView.getId();
  }

  protected static String getCreateInstanceLinkFor(UriInfo uriInfo, PrestoType type) {
    return uriInfo.getBaseUri() + "editor/create-instance/" + type.getSchemaProvider().getDatabaseId() + "/" + type.getId();
  }

}
