package ontopoly.rest.editor;

import javax.ws.rs.core.UriInfo;

import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;
import ontopoly.rest.editor.spi.PrestoView;

public class Links {

  protected static String getEditLinkFor(UriInfo uriInfo, PrestoTopic topic, PrestoView fieldsView) {
    return uriInfo.getBaseUri() + "editor/topic/" + topic.getDatabaseId() + "/" + topic.getId() + "/" + fieldsView.getId();
  }

  protected static String getCreateLinkFor(UriInfo uriInfo, PrestoType topicType, PrestoView fieldsView) {
    return uriInfo.getBaseUri() + "editor/topic/" + topicType.getDatabaseId() + "/_" + topicType.getId() + "/" + fieldsView.getId();
  }

  protected static String getCreateInstanceLinkFor(UriInfo uriInfo, PrestoType type) {
    return uriInfo.getBaseUri() + "editor/create-instance/" + type.getDatabaseId() + "/" + type.getId();
  }

}
