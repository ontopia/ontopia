package net.ontopia.presto.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import net.ontopia.presto.jaxb.Link;
import net.ontopia.presto.jaxb.TopicTypeTree;
import net.ontopia.presto.spi.PrestoType;

public class TypeUtils {

  static Collection<TopicTypeTree> getAvailableTypes(UriInfo uriInfo, Collection<PrestoType> types, boolean tree) {
    Collection<TopicTypeTree> result = new ArrayList<TopicTypeTree>(); 
    for (PrestoType type : types) {
      if (type.isReadOnly()) {
        continue;
      }
      TopicTypeTree typeMap = new TopicTypeTree();
      typeMap.setId(type.getId());
      typeMap.setName(type.getName());

      List<Link> links = new ArrayList<Link>();
      if (!type.isAbstract()) {
        links.add(new Link("create-instance", Links.getCreateInstanceLinkFor(uriInfo, type)));
      }

      if (tree) {
        Collection<TopicTypeTree> typesList = getAvailableTypes(uriInfo, type.getDirectSubTypes(), true);
        if (!typesList.isEmpty()) {
          typeMap.setTypes(typesList);
        }
      } else {
        if (!type.getDirectSubTypes().isEmpty()) {
          links.add(new Link("available-types-tree-lazy", uriInfo.getBaseUri() + "editor/available-types-tree-lazy/" + type.getSchemaProvider().getDatabaseId() + "/" + type.getId()));
        }
      }
      typeMap.setLinks(links);
      result.add(typeMap);
    }
    return result;
  }

}
