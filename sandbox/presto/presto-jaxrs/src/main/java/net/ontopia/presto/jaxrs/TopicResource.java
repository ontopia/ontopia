package net.ontopia.presto.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import net.ontopia.presto.jaxb.AvailableFieldTypes;
import net.ontopia.presto.jaxb.AvailableFieldValues;
import net.ontopia.presto.jaxb.AvailableTopicMaps;
import net.ontopia.presto.jaxb.AvailableTopicTypes;
import net.ontopia.presto.jaxb.FieldData;
import net.ontopia.presto.jaxb.Link;
import net.ontopia.presto.jaxb.RootInfo;
import net.ontopia.presto.jaxb.Topic;
import net.ontopia.presto.jaxb.TopicMap;
import net.ontopia.presto.jaxb.TopicType;
import net.ontopia.presto.jaxb.Value;
import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoFieldUsage;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoSession;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoType;
import net.ontopia.presto.spi.PrestoView;

@Path("/editor")
public abstract class TopicResource {
    
  public final static String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

  // TODO: add more endpoints: 
  //
  // 1: / - information about server and link to /available-topicmaps
  // 2: /available-topicmaps - lists available topic maps
  // 3: /create-instance/{topicMapId}

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("")
  public RootInfo getRootInfo(@Context UriInfo uriInfo) throws Exception {

    RootInfo result = new RootInfo();

    result.setId(uriInfo.getBaseUri() + "editor");
    result.setVersion(0);
    result.setName("Ontopia Presto REST API");

    List<Link> links = new ArrayList<Link>();
    links.add(new Link("available-topicmaps", uriInfo.getBaseUri() + "editor/available-topicmaps"));
    result.setLinks(links);      
    return result;
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("available-topicmaps")
  public AvailableTopicMaps getTopicMaps(@Context UriInfo uriInfo) throws Exception {

    AvailableTopicMaps result = new AvailableTopicMaps();

    result.setId("topicmaps");
    result.setName("Ontopia Presto REST API");

    Collection<TopicMap> topicmaps = new ArrayList<TopicMap>();
    
    TopicMap topicmap = new TopicMap();
    topicmap.setId("litteraturklubben.xtm");
    topicmap.setName("Litteraturklubben");

    List<Link> links = new ArrayList<Link>();
    links.add(new Link("edit", uriInfo.getBaseUri() + "editor/topicmap-info/litteraturklubben.xtm"));
    topicmap.setLinks(links);    
    
    topicmaps.add(topicmap);
    result.setTopicMaps(topicmaps);      
    return result;
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("topicmap-info/{topicMapId}")
  public TopicMap getTopicMapInfo(
      @Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId) throws Exception {

    PrestoSession session = createSession(topicMapId);
    try {
      TopicMap result = new TopicMap();

      result.setId(session.getDatabaseId());
      result.setName(session.getDatabaseName());

      List<Link> links = new ArrayList<Link>();
      links.add(new Link("available-types-tree", uriInfo.getBaseUri() + "editor/available-types-tree/" + session.getDatabaseId()));
      links.add(new Link("available-types-tree-lazy", uriInfo.getBaseUri() + "editor/available-types-tree-lazy/" + session.getDatabaseId()));
      links.add(new Link("edit-topic-by-id", uriInfo.getBaseUri() + "editor/topic/" + session.getDatabaseId() + "/{topicId}"));
      result.setLinks(links);      
      return result;


    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("create-instance/{topicMapId}/{topicTypeId}")
  public Topic createInstance(
      @Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicTypeId") final String topicTypeId) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();

    try {

      PrestoType topicType = schemaProvider.getTypeById(topicTypeId);
      PrestoView fieldsView = topicType.getDefaultView();

      return postProcess(Utils.getNewTopicInfo(uriInfo, topicType, fieldsView));

    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("create-field-instance/{topicMapId}/{parentTopicId}/{parentFieldId}/{playerTypeId}")
  public Topic createInstance(
      @Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId,
      @PathParam("parentTopicId") final String parentTopicId,
      @PathParam("parentFieldId") final String parentFieldId, 
      @PathParam("playerTypeId") final String playerTypeId) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();
    
    try {

      PrestoType topicType = schemaProvider.getTypeById(playerTypeId);
      PrestoView fieldsView = topicType.getDefaultView();

      return postProcess(Utils.getNewTopicInfo(uriInfo, topicType, fieldsView, parentTopicId, parentFieldId));

    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("topic-data/{topicMapId}/{topicId}")
  public Map<String,Object> getTopicData(
      @Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();
    PrestoDataProvider dataProvider = session.getDataProvider();
    
    try {

      PrestoTopic topic = dataProvider.getTopicById(topicId);
      PrestoType topicType = schemaProvider.getTypeById(topic.getTypeId());
      
      return Utils.getTopicData(uriInfo, topic, topicType);

    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("topic/{topicMapId}/{topicId}")
  public Topic getTopicInDefaultView(
      @Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId,
      @QueryParam("readOnly") final boolean readOnly) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();
    PrestoDataProvider dataProvider = session.getDataProvider();

    try {

      PrestoTopic topic = dataProvider.getTopicById(topicId);
      PrestoType topicType = schemaProvider.getTypeById(topic.getTypeId());
      PrestoView fieldsView = topicType.getDefaultView();
      
      return postProcess(Utils.getTopicInfo(uriInfo, topic, topicType, fieldsView, readOnly));

    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("topic/{topicMapId}/{topicId}/{viewId}")
  public Topic getTopicInView(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId,
      @PathParam("viewId") final String viewId,
      @QueryParam("readOnly") final boolean readOnly) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();
    PrestoDataProvider dataProvider = session.getDataProvider();

    try {

      PrestoTopic topic = dataProvider.getTopicById(topicId);
      PrestoType topicType = schemaProvider.getTypeById(topic.getTypeId());
      PrestoView fieldsView = topicType.getViewById(viewId);

      return postProcess(Utils.getTopicInfo(uriInfo, topic, topicType, fieldsView, readOnly));

    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  @PUT
  @Produces(APPLICATION_JSON_UTF8)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("topic/{topicMapId}/{topicId}/{viewId}")
  public Topic updateTopic(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId, Topic jsonObject) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();
    PrestoDataProvider dataProvider = session.getDataProvider();

    try {

      PrestoTopic topic = null;
      PrestoType topicType;
      if (topicId.startsWith("_")) {
        topicType = schemaProvider.getTypeById(topicId.substring(1));
      } else {
        topic = dataProvider.getTopicById(topicId);
        topicType = schemaProvider.getTypeById(topic.getTypeId());
      }

      PrestoView fieldsView = topicType.getViewById(viewId);
      
      topic = Utils.updateTopic(uriInfo, session, topic, topicType, fieldsView, jsonObject);
      
      Topic result = Utils.getTopicInfo(uriInfo, topic, topicType, fieldsView, false);
      String id = result.getId();
      session.commit();
      onTopicUpdated(id);

      return postProcess(result);
    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();
    }
  }
  
  @POST
  @Produces(APPLICATION_JSON_UTF8)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("add-field-values-at-index/{topicMapId}/{topicId}/{viewId}/{fieldId}")
  public FieldData addFieldValuesAtIndex(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId, 
      @QueryParam("index") final Integer index, 
      @QueryParam("replaceExisting") final Boolean replaceExisting, FieldData jsonObject) throws Exception {

      PrestoSession session = createSession(topicMapId);
      PrestoSchemaProvider schemaProvider = session.getSchemaProvider();
      PrestoDataProvider dataProvider = session.getDataProvider();

      try {

        PrestoTopic topic = dataProvider.getTopicById(topicId);
        PrestoType topicType = schemaProvider.getTypeById(topic.getTypeId());
        PrestoView fieldsView = topicType.getViewById(viewId);

        PrestoFieldUsage field = topicType.getFieldById(fieldId, fieldsView);

        FieldData result = Utils.addFieldValues(uriInfo, session, topic, field, index, replaceExisting, jsonObject);

        String id = topic.getId();

        session.commit();
        onTopicUpdated(id);

        return result;
      } catch (Exception e) {
        session.abort();
        throw e;
      } finally {
        session.close();      
      } 
  }
  
  @POST
  @Produces(APPLICATION_JSON_UTF8)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("move-field-values-to-index/{topicMapId}/{topicId}/{viewId}/{fieldId}/{index}")
  public FieldData moveFieldValuesAtIndex(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId, 
      @PathParam("index") final Integer index, FieldData jsonObject) throws Exception {
      
      Boolean replaceExisting = Boolean.TRUE;
      return addFieldValuesAtIndex(uriInfo, topicMapId, topicId, viewId, fieldId, index, replaceExisting, jsonObject);
  }
  
  @POST
  @Produces(APPLICATION_JSON_UTF8)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("add-field-values/{topicMapId}/{topicId}/{viewId}/{fieldId}")
  public FieldData addFieldValues(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId, FieldData jsonObject) throws Exception {

      Integer index = Integer.MAX_VALUE;
      Boolean replaceExisting = null;
      return addFieldValuesAtIndex(uriInfo, topicMapId, topicId, viewId, fieldId, index, replaceExisting, jsonObject);
  }

  @POST
  @Produces(APPLICATION_JSON_UTF8)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("remove-field-values/{topicMapId}/{topicId}/{viewId}/{fieldId}")
  public FieldData removeFieldValues(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId, FieldData jsonObject) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();
    PrestoDataProvider dataProvider = session.getDataProvider();

    try {

      PrestoTopic topic = dataProvider.getTopicById(topicId);
      PrestoType topicType = schemaProvider.getTypeById(topic.getTypeId());
      PrestoView fieldsView = topicType.getViewById(viewId);

      PrestoFieldUsage field = topicType.getFieldById(fieldId, fieldsView);

      FieldData result =  Utils.removeFieldValues(uriInfo, session, topic, field, jsonObject);

      String id = topic.getId();

      session.commit();
      onTopicUpdated(id);

      return result;
    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    } 
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("available-field-values/{topicMapId}/{topicId}/{viewId}/{fieldId}")
  public AvailableFieldValues getAvailableFieldValues(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();
    PrestoDataProvider dataProvider = session.getDataProvider();

    try {

      PrestoTopic topic;
      PrestoType topicType;
      if (topicId.startsWith("_")) {
        topicType = schemaProvider.getTypeById(topicId.substring(1));
        topic  = null;
      } else {
        topic = dataProvider.getTopicById(topicId);
        topicType = schemaProvider.getTypeById(topic.getTypeId());
      }

      PrestoView fieldsView = topicType.getViewById(viewId);
      
      PrestoFieldUsage field = topicType.getFieldById(fieldId, fieldsView);
      
      Collection<PrestoTopic> availableFieldValues = dataProvider.getAvailableFieldValues(field);
      return createFieldInfoAllowed(uriInfo, field, availableFieldValues);

    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  private AvailableFieldValues createFieldInfoAllowed(UriInfo uriInfo, PrestoFieldUsage field, Collection<PrestoTopic> availableFieldValues) {

    AvailableFieldValues result = new AvailableFieldValues();
    result.setId(field.getId());
    result.setName(field.getName());

    List<Value> values = new ArrayList<Value>(availableFieldValues.size());
    if (!availableFieldValues.isEmpty()) {
      
      PrestoView valueView = field.getValueView();
      boolean traversable = field.isTraversable();
      
      for (PrestoTopic value : availableFieldValues) {
        values.add(Utils.getAllowedTopicFieldValue(uriInfo, value, valueView, traversable));
      }
    } 
    result.setValues(values);

    return result;
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("available-field-types/{topicMapId}/{topicId}/{viewId}/{fieldId}")
  public AvailableFieldTypes getAvailableFieldTypes(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();
    PrestoDataProvider dataProvider = session.getDataProvider();

    try {
      
      PrestoTopic topic = dataProvider.getTopicById(topicId);
      PrestoType topicType = schemaProvider.getTypeById(topic.getTypeId());
      PrestoView fieldsView = topicType.getViewById(viewId);

      PrestoFieldUsage field = topicType.getFieldById(fieldId, fieldsView);
      
      AvailableFieldTypes result = new AvailableFieldTypes();
      result.setId(field.getId());
      result.setName(field.getName());
      
      Collection<PrestoType> availableFieldCreateTypes = field.getAvailableFieldCreateTypes();

      List<TopicType> types = new ArrayList<TopicType>(availableFieldCreateTypes.size());
      for (PrestoType playerType : availableFieldCreateTypes) {
        types.add(Utils.getCreateFieldInstance(uriInfo, topic, field, playerType));
      }

      result.setTypes(types);
      return result;
      
    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("available-types-tree-lazy/{topicMapId}")
  public AvailableTopicTypes getAvailableTypesTreeLazy(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();

    try {

      AvailableTopicTypes result = new AvailableTopicTypes();
      result.setTypes(TypeUtils.getAvailableTypes(uriInfo, schemaProvider.getRootTypes(), false));      
      return result;

    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("available-types-tree-lazy/{topicMapId}/{typeId}")
  public AvailableTopicTypes getAvailableTypesTreeLazy(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("typeId") final String typeId) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();

    try {
      PrestoType type = schemaProvider.getTypeById(typeId);

      AvailableTopicTypes result = new AvailableTopicTypes();
      result.setTypes(TypeUtils.getAvailableTypes(uriInfo, type.getDirectSubTypes(), false));      
      return result;

    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  @GET
  @Produces(APPLICATION_JSON_UTF8)
  @Path("available-types-tree/{topicMapId}")
  public AvailableTopicTypes getAvailableTypesTree(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId) throws Exception {

    PrestoSession session = createSession(topicMapId);
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();

    try {

      AvailableTopicTypes result = new AvailableTopicTypes();
      result.setTypes(TypeUtils.getAvailableTypes(uriInfo, schemaProvider.getRootTypes(), true));      
      return result;

    } catch (Exception e) {
      session.abort();
      throw e;
    } finally {
      session.close();      
    }
  }

  // overridable methods
  
  protected abstract PrestoSession createSession(String topicMapId);

  protected void onTopicUpdated(String topicId) {      
  }

  protected Topic postProcess(Topic topicInfo) {
    return topicInfo;
  }

}
