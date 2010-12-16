package ontopoly.rest.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import ontopoly.model.TopicType;
import ontopoly.rest.editor.Utils.Link;

public class TypeUtils {

  static List<Map<String, Object>> getAvailableTypesTreeLazy(UriInfo uriInfo, Collection<TopicType> topicTypes) {
    List<Map<String,Object>> result = new ArrayList<Map<String,Object>>(); 
    for (TopicType topicType : topicTypes) {
      if (!topicType.isSystemTopic()) {
        Map<String,Object> type = new LinkedHashMap<String,Object>();
        type.put("id", topicType.getId());
        type.put("name", topicType.getName());

        List<Link> links = new ArrayList<Link>();
        if (!topicType.isAbstract()) {
          links.add(new Link("create-instance", uriInfo.getBaseUri() + "editor/create-instance/" + topicType.getTopicMap().getId() + "/" + topicType.getId()));
        }
        if (!topicType.getDirectSubOrdinateTypes().isEmpty()) {
          links.add(new Link("available-types-tree-lazy", uriInfo.getBaseUri() + "editor/available-types-tree-lazy/" + topicType.getTopicMap().getId() + "/" + topicType.getId()));
        }
        type.put("links", links);      
        
        result.add(type);
      }
    }
    return result;
  }

  static List<Map<String, Object>> getAvailableTypesTree(UriInfo uriInfo, Collection<TopicType> topicTypes) {
    List<Map<String,Object>> result = new ArrayList<Map<String,Object>>(); 
    for (TopicType topicType : topicTypes) {
      if (!topicType.isSystemTopic()) {
        Map<String,Object> type = new LinkedHashMap<String,Object>();
        type.put("id", topicType.getId());
        type.put("name", topicType.getName());

        List<Link> links = new ArrayList<Link>();
        if (!topicType.isAbstract()) {
          links.add(new Link("create-instance", uriInfo.getBaseUri() + "editor/create-instance/" + topicType.getTopicMap().getId() + "/" + topicType.getId()));
        }
        type.put("links", links);
        
        List<Map<String, Object>> types = getAvailableTypesTree(uriInfo, topicType.getDirectSubOrdinateTypes());
        if (!types.isEmpty()) {
          type.put("types", types);
        }
        result.add(type);
      }
    }
    return result;
  }

}
