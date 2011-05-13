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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojoSchemaModel {

  private static Logger log = LoggerFactory.getLogger(PojoSchemaModel.class.getName());

  public static void main(String[] args) throws Exception {
    PojoSchemaProvider schemaProvider = parse("pojo-schema-example", "pojo-schema-example.json");
    System.out.println("SP: " + schemaProvider + " " + schemaProvider.getDatabaseId());
  }

  public static PojoSchemaProvider parse(String databaseId, String schemaFilename) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream istream = null;
    try {
      File schemaFile = new File(System.getProperty("user.home") + File.separator + schemaFilename);
      if (schemaFile.exists()) {
        log.warn("Loading presto schema model from file in user's home directory: " + schemaFile.getAbsolutePath());
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
    schemaProvider.setDatabaseId(databaseId);

    Map<String, ObjectNode> fieldsMap = createFieldsMap(json);
    Map<String, ObjectNode> typesMap = createTypesMap(json);

    Map<String,PojoType> types = new HashMap<String,PojoType>();
    for (String typeId : typesMap.keySet()) {
      ObjectNode typeConfig = typesMap.get(typeId);

      PojoType type = getPojoType(typeId, types, schemaProvider);
      schemaProvider.addType(type);

      // name
      String name = typeConfig.get("name").getTextValue();
      type.setName(name);
      // readOnly
      boolean readOnlyType = false;
      if (typeConfig.has("readOnly")) {
        readOnlyType = typeConfig.get("readOnly").getBooleanValue();
      }
      type.setReadOnly(readOnlyType);
      // hidden
      if (typeConfig.has("hidden")) {
        type.setHidden(typeConfig.get("hidden").getBooleanValue());
      }
      // creatable
      if (typeConfig.has("creatable")) {
        type.setCreatable(typeConfig.get("creatable").getBooleanValue());
      }
      
      // extends
      if (typeConfig.has("extends")) {
        String superTypeId = typeConfig.get("extends").getTextValue();
        verifyDeclaredType(superTypeId, typesMap, "extends", type);
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
          for (JsonNode fieldNode : fieldsArray) {
            String fieldId;
            ObjectNode fieldConfig;
            if (fieldNode.isTextual()) {
                fieldId = fieldNode.getTextValue();                        
                fieldConfig = fieldsMap.get(fieldId);
            } else if (fieldNode.isObject()) {
                fieldConfig = (ObjectNode)fieldNode;
                fieldId = fieldConfig.get("id").getTextValue();
            } else {
                throw new RuntimeException("Invalid field declaration or field reference: " + fieldNode);
            }
            if (fieldId == null) {
                throw new RuntimeException("Field id missing on field object: " + fieldConfig);
            }
            if (fieldConfig == null) {
                throw new RuntimeException("Field declaration missing for field with id '" + fieldId + "'");
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
            // externalType
            if (fieldConfig.has("externalType")) {
              String externalType = fieldConfig.get("externalType").getTextValue();
              field.setExternalType(externalType);
            }
            // valueView (using current view for now)
            if (fieldConfig.has("valueView")) {
              String valueViewId = fieldConfig.get("valueView").getTextValue();
              PojoView valueView = new PojoView(valueViewId, schemaProvider);
              field.setValueView(valueView);
            } else {
              field.setValueView(type.getDefaultView());
            } 

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
            // isHidden
            if (fieldConfig.has("hidden")) {
              field.setHidden(fieldConfig.get("hidden").getBooleanValue());
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
            // isSorted
            if (fieldConfig.has("sorted")) {
              field.setSorted(fieldConfig.get("sorted").getBooleanValue());
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
                  verifyDeclaredType(createTypeId, typesMap, "createTypes",type, field);
                  PojoType createType = getPojoType(createTypeId, types, schemaProvider);
                  field.addAvailableFieldCreateType(createType);
                }
              }

              // availableFieldValueTypes
              if (fieldConfig.has("valueTypes")) {
                ArrayNode valueTypesArray = (ArrayNode)fieldConfig.get("valueTypes");
                for (JsonNode valueTypeIdNode : valueTypesArray) {
                  String valueTypeId = valueTypeIdNode.getTextValue();
                  verifyDeclaredType(valueTypeId, typesMap, "valueTypes",type, field);
                  PojoType valueType = getPojoType(valueTypeId, types, schemaProvider);
                  field.addAvailableFieldValueType(valueType);
                }
              }
            }

          }
        }
      }
      types.put(typeId, type);            
    }
    return schemaProvider;
  }

  private static Map<String, ObjectNode> createTypesMap(ObjectNode json) {
    Map<String,ObjectNode> typesMap = new HashMap<String,ObjectNode>();
    ObjectNode typesNode = (ObjectNode)json.get("types");
    Iterator<String> typeNames = typesNode.getFieldNames();
    while (typeNames.hasNext()) {
      String typeName = typeNames.next();
      ObjectNode typeConfig = (ObjectNode)typesNode.get(typeName);
      typesMap.put(typeName, typeConfig);
    }
    return typesMap;
  }

  private static Map<String, ObjectNode> createFieldsMap(ObjectNode json) {
    Map<String,ObjectNode> fieldsMap = new HashMap<String,ObjectNode>();
    ObjectNode fieldsNode = (ObjectNode)json.get("fields");
    Iterator<String> fieldNames = fieldsNode.getFieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      ObjectNode fieldConfig = (ObjectNode)fieldsNode.get(fieldName);
      fieldsMap.put(fieldName, fieldConfig);
    }
    return fieldsMap;
  }

  private static void verifyDeclaredType(String typeId, Map<String, ObjectNode> typesMap, String jsonField, PojoType type) {
    if (!typesMap.containsKey(typeId)) {
          throw new RuntimeException("Unknown type '" + typeId + "' in " + jsonField + " on type '" + type.getId() + "'");
    }
  }

  private static void verifyDeclaredType(String typeId, Map<String, ObjectNode> typesMap, String jsonField, PojoType type, PojoField field) {
    if (!typesMap.containsKey(typeId)) {
          throw new RuntimeException("Unknown type '" + typeId + "' in " + jsonField + " in field '" + field.getId() + "' on type '" + type.getId() + "'");
    }
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
