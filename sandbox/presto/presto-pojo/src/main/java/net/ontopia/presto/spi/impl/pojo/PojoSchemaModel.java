package net.ontopia.presto.spi.impl.pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class PojoSchemaModel {

  public static void main(String[] args) throws Exception {
    PojoSchemaProvider schemaProvider = parse("pojo-schema-example", "pojo-schema-example.json");
    System.out.println("SP: " + schemaProvider + " " + schemaProvider.getDatabaseId());
  }

  public static PojoSchemaProvider parse(String databaseId, String schemaFilename) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream istream = null;
    try {
      File schemaFile = new File(System.getProperty("user.home") + File.pathSeparator + schemaFilename);
      if (schemaFile.exists()) {
        istream = new FileInputStream(schemaFile);
      } else {
        istream = cl.getResourceAsStream(schemaFilename);
      }
      Reader reader = new InputStreamReader(istream, "UTF-8");
      ObjectMapper mapper = new ObjectMapper();
      ObjectNode objectNode = mapper.readValue(reader, ObjectNode.class);
      return createSchemaProvider(databaseId, objectNode);
    } catch (Exception e) {
      throw new RuntimeException("Problems occured when loading '" + schemaFilename + "'", e);
    } finally {
      try {
        if (istream != null) istream.close();
      } catch (IOException e) {
      }
    }
  }

  private static PojoSchemaProvider createSchemaProvider(String databaseId, ObjectNode json) {
    PojoSchemaProvider schemaProvider = new PojoSchemaProvider();

    //        String id = json.get("id").getTextValue();
    schemaProvider.setDatabaseId(databaseId);

    Map<String,ObjectNode> fieldsMap = new HashMap<String,ObjectNode>();
    ObjectNode fieldsNode = (ObjectNode)json.get("fields");
    Iterator<String> fieldNames = fieldsNode.getFieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      ObjectNode fieldConfig = (ObjectNode)fieldsNode.get(fieldName);
      fieldsMap.put(fieldName, fieldConfig);
    }

    Map<String,ObjectNode> typesMap = new HashMap<String,ObjectNode>();
    ObjectNode typesNode = (ObjectNode)json.get("types");
    Iterator<String> typeNames = typesNode.getFieldNames();
    while (typeNames.hasNext()) {
      String typeName = typeNames.next();
      ObjectNode typeConfig = (ObjectNode)typesNode.get(typeName);
      typesMap.put(typeName, typeConfig);
    }

    //        Map<String,PojoView> views = new HashMap<String,PojoView>();

    Map<String,PojoType> types = new HashMap<String,PojoType>();
    for (String typeId : typesMap.keySet()) {
      ObjectNode typeConfig = typesMap.get(typeId);

      PojoType type = getPojoType(typeId, types, schemaProvider);
      schemaProvider.addType(type);

      // name
      String name = typeConfig.get("name").getTextValue();
      type.setName(name);
      // abstract
      if (typeConfig.has("abstract")) {
        type.setAbstract(typeConfig.get("abstract").getBooleanValue());
      }
      // readOnly
      boolean readOnlyType = false;
      if (typeConfig.has("readOnly")) {
        readOnlyType = typeConfig.get("readOnly").getBooleanValue();
      }
      type.setReadOnly(readOnlyType);
      
      // extends
      if (typeConfig.has("extends")) {
        String superTypeId = typeConfig.get("extends").getTextValue();
        PojoType superType = getPojoType(superTypeId, types, schemaProvider);
        //                type.setSuperType(superType);
        superType.addDirectSubType(type);
      }
      if (typeConfig.has("views")) {
        ArrayNode viewsNode = (ArrayNode)typeConfig.get("views");
        for (JsonNode viewNode_ : viewsNode) {

          // view
          ObjectNode viewNode = (ObjectNode)viewNode_;
          String viewId = viewNode.get("id").getTextValue();
          PojoView view = new PojoView(viewId, schemaProvider);
          type.addView(view);
          // view name
          String viewName = viewNode.get("name").getTextValue();
          view.setName(viewName);
          // fields
          ArrayNode fieldsArray = (ArrayNode)viewNode.get("fields");
          for (JsonNode fieldIdNode : fieldsArray) {
            String fieldId = fieldIdNode.getTextValue();                        
            ObjectNode fieldConfig = fieldsMap.get(fieldId);
            if (fieldConfig == null) {
              throw new RuntimeException("Unknown field '" + fieldId + "'");
            }

            PojoField field = new PojoField(fieldId, schemaProvider);
            type.addField(field);
            field.addDefinedInView(view);

            // name
            String fieldName = fieldConfig.get("name").getTextValue();                        
            field.setName(fieldName);
            // isNameField
            if (fieldConfig.has("nameField")) {
              field.setNameField(fieldConfig.get("nameField").getBooleanValue());
            }
            // isPrimitiveField/isReferenceField
            // dataType
            if (fieldConfig.has("datatype")) {
              String datatype = fieldConfig.get("datatype").getTextValue();
              field.setDataType(datatype);
            } else {
              field.setDataType("string");
            }
            // valueView (using current view for now)
            if (fieldConfig.has("valueView")) {
              String valueViewId = fieldConfig.get("valueView").getTextValue();
              PojoView valueView = new PojoView(valueViewId, schemaProvider);
              field.setValueView(valueView);
            } else {
              field.setValueView(type.getDefaultView());
            } 
            // fieldType (get rid of this one?)

            // minCardinality
            if (fieldConfig.has("minCardinality")) {
              field.setMinCardinality(fieldConfig.get("minCardinality").getIntValue());
            }
            // maxCardinality
            if (fieldConfig.has("maxCardinality")) {
              field.setMaxCardinality(fieldConfig.get("maxCardinality").getIntValue());
            }
            // validationType
            if (fieldConfig.has("validationType")) {
              String validationType = fieldConfig.get("validationType").getTextValue();
              field.setValidationType(validationType);
            }                        
            // isEmbedded
            if (fieldConfig.has("embedded")) {
              field.setEmbedded(fieldConfig.get("embedded").getBooleanValue());
            }
            // isTraversable
            if (fieldConfig.has("traversable")) {
              field.setTraversable(fieldConfig.get("traversable").getBooleanValue());
            }
            // isReadOnly            
            if (fieldConfig.has("readOnly")) {
              field.setReadOnly(fieldConfig.get("readOnly").getBooleanValue());
            } else {
              field.setReadOnly(readOnlyType);
            }
            // isNewValuesOnly
            if (fieldConfig.has("newValuesOnly")) {
              field.setNewValuesOnly(fieldConfig.get("newValuesOnly").getBooleanValue());
            }
            // isExistingValuesOnly
            if (fieldConfig.has("existingValuesOnly")) {
              field.setExistingValuesOnly(fieldConfig.get("existingValuesOnly").getBooleanValue());
            }
            // inverseFieldId
            if (fieldConfig.has("inverseField")) {
              String inverseField = fieldConfig.get("inverseField").getTextValue();
              field.setInverseFieldId(inverseField);
            }
            // interfaceControl
            if (fieldConfig.has("interfaceControl")) {
              String interfaceControl = fieldConfig.get("interfaceControl").getTextValue();
              field.setInterfaceControl(interfaceControl);
            }

            if (field.isReferenceField()) {

              // availableFieldCreateTypes
              if (fieldConfig.has("createTypes")) {
                ArrayNode createTypesArray = (ArrayNode)fieldConfig.get("createTypes");
                for (JsonNode createTypeIdNode : createTypesArray) {
                  String createTypeId = createTypeIdNode.getTextValue();
                  PojoType createType = getPojoType(createTypeId, types, schemaProvider);
                  field.addAvailableFieldCreateType(createType);
                }
              }

              // availableFieldValueTypes
              ArrayNode valueTypesArray = (ArrayNode)fieldConfig.get("valueTypes");
              for (JsonNode valueTypeIdNode : valueTypesArray) {
                String valueTypeId = valueTypeIdNode.getTextValue();
                PojoType valueType = getPojoType(valueTypeId, types, schemaProvider);
                field.addAvailableFieldValueType(valueType);
              }
            }

          }
        }
      }
      types.put(typeId, type);            
    }
    return schemaProvider;
  }

  private static PojoType getPojoType(String typeId, Map<String,PojoType> types, PojoSchemaProvider schemaProvider) {
    PojoType type = types.get(typeId);
    if (type == null) {
      type = new PojoType(typeId, schemaProvider);
      types.put(typeId, type);
    }
    return type;
  }

}
