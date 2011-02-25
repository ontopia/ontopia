package ontopoly.rest.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import ontopoly.rest.editor.jaxb.Link;
import ontopoly.rest.editor.spi.PrestoType;

public class TypeUtils {

  static List<Map<String, Object>> getAvailableTypesTreeLazy(UriInfo uriInfo, Collection<PrestoType> types) {
    List<Map<String,Object>> result = new ArrayList<Map<String,Object>>(); 
    for (PrestoType type : types) {
      if (type.isReadOnly()) {
        continue;
      }
      Map<String,Object> typeMap = new LinkedHashMap<String,Object>();
      typeMap.put("id", type.getId());
      typeMap.put("name", type.getName());

      List<Link> links = new ArrayList<Link>();
      if (!type.isAbstract()) {
        links.add(new Link("create-instance", Links.getCreateInstanceLinkFor(uriInfo, type)));
      }
      if (!type.getDirectSubTypes().isEmpty()) {
        links.add(new Link("available-types-tree-lazy", uriInfo.getBaseUri() + "editor/available-types-tree-lazy/" + type.getSchemaProvider().getDatabaseId() + "/" + type.getId()));
      }
      typeMap.put("links", links);      

      result.add(typeMap);
    }
    return result;
  }

  static List<Map<String, Object>> getAvailableTypesTree(UriInfo uriInfo, Collection<PrestoType> types) {
    List<Map<String,Object>> result = new ArrayList<Map<String,Object>>(); 
    for (PrestoType type : types) {
      if (type.isReadOnly()) {
        continue;
      }
      Map<String,Object> typeMap = new LinkedHashMap<String,Object>();
      typeMap.put("id", type.getId());
      typeMap.put("name", type.getName());

      List<Link> links = new ArrayList<Link>();
      if (!type.isAbstract()) {
        links.add(new Link("create-instance", Links.getCreateInstanceLinkFor(uriInfo, type)));
      }
      typeMap.put("links", links);

      List<Map<String, Object>> typesList = getAvailableTypesTree(uriInfo, type.getDirectSubTypes());
      if (!typesList.isEmpty()) {
        typeMap.put("types", typesList);
      }
      result.add(typeMap);
    }
    return result;
  }

}
