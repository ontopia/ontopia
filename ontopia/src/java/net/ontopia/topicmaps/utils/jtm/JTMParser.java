package net.ontopia.topicmaps.utils.jtm;

import java.io.Reader;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.MergeUtils;

/**
 * INTERNAL: Parses an JTM fragment from a reader and stores the
 * result in a provided topic map.
 * 
 * TODO: the parser currently uses a very simple and straightforward validation
 * mechanism, that could/should be changed, also the error reports from the
 * validation could be made in the same way as in the online <a
 * href="http://www.cerny-online.com/jtm/1.0/">JTM Validator</a>, actually
 * returning JSON snippets itself.
 */
public class JTMParser {
  private TopicMapIF tm;

  private enum OBJECT_TYPE {
    TOPICMAP, TOPIC, ASSOCIATION, NAME, VARIANT, OCCURRENCE, ROLE, STRING,
      REFERENCE
  };

  public enum JSON_TYPE {
    STRING, ARRAY, OBJECT
  };

  public JTMParser(TopicMapIF topicmap) {
    this.tm = topicmap;
  }

  public void parse(Reader reader) throws JSONException {
    JSONObject root = new JSONObject(new JSONTokener(reader));

    // we can only import a JTM topicmap fragment
    Map<String, SchemaInfo> keys = getSchema(OBJECT_TYPE.TOPICMAP);
    keys.put("version", new SchemaInfo("1.0", true));
    keys.put("item_type", new SchemaInfo("topicmap", true));

    validate(root, keys);

    // validation was successful -> now import the jtm fragment
    importTopicMap(root);
  }

  private void importTopicMap(JSONObject root) throws JSONException {
    if (root.has("topics")) {
      JSONArray topics = root.getJSONArray("topics");
      for (int i = 0; i < topics.length(); i++) {
        importTopic(topics.getJSONObject(i));
      }
    }

    if (root.has("associations")) {
      JSONArray assocs = root.getJSONArray("associations");
      for (int i = 0; i < assocs.length(); i++) {
        importAssociation(assocs.getJSONObject(i));
      }
    }

    setItemIdentifiers(tm, root);
    setReifier(tm, root);
  }

  private void importTopic(JSONObject obj) throws JSONException {
    TopicIF topic = tm.getBuilder().makeTopic();

    if (obj.has("names")) {
      JSONArray names = obj.getJSONArray("names");
      for (int i = 0; i < names.length(); i++) {
        importName(names.getJSONObject(i), topic);
      }
    }

    if (obj.has("occurrences")) {
      JSONArray occurrences = obj.getJSONArray("occurrences");
      for (int i = 0; i < occurrences.length(); i++) {
        importOccurrence(occurrences.getJSONObject(i), topic);
      }
    }

    topic = (TopicIF) setItemIdentifiers(topic, obj);
    topic = setSubjectIdentifiers(topic, obj);
    setSubjectLocators(topic, obj);
  }

  private void importAssociation(JSONObject obj) throws JSONException {
    TopicIF type = getType(obj);

    AssociationIF assoc = tm.getBuilder().makeAssociation(type);

    Collection<TopicIF> scopes = getScope(obj);
    for (TopicIF scope : scopes) {
      assoc.addTheme(scope);
    }

    if (obj.has("roles")) {
      JSONArray roles = obj.getJSONArray("roles");
      for (int i = 0; i < roles.length(); i++) {
        importRole(roles.getJSONObject(i), assoc);
      }
    }

    setItemIdentifiers(assoc, obj);
    setReifier(assoc, obj);
  }

  private void importRole(JSONObject obj, AssociationIF assoc)
      throws JSONException {
    TopicIF type = getType(obj);
    TopicIF player = getPlayer(obj);

    AssociationRoleIF role = tm.getBuilder().makeAssociationRole(assoc, type,
        player);

    setItemIdentifiers(role, obj);
    setReifier(role, obj);
  }

  private void importName(JSONObject obj, TopicIF topic) throws JSONException {
    String value = obj.getString("value");
    TopicNameIF name = tm.getBuilder().makeTopicName(topic, value);

    TopicIF type = getType(obj);
    if (type != null) {
      name.setType(type);
    }

    Collection<TopicIF> scopes = getScope(obj);
    for (TopicIF scope : scopes) {
      name.addTheme(scope);
    }

    if (obj.has("variants")) {
      JSONArray variants = obj.getJSONArray("variants");
      for (int i = 0; i < variants.length(); i++) {
        importVariant(variants.getJSONObject(i), name);
      }
    }

    setItemIdentifiers(name, obj);
    setReifier(name, obj);
  }

  private void importVariant(JSONObject obj, TopicNameIF name)
      throws JSONException {
    String value = obj.getString("value");
    Collection<TopicIF> scope = getScope(obj);
    LocatorIF datatype = getDataType(obj);

    VariantNameIF variant = tm.getBuilder().makeVariantName(name, value,
        datatype, scope);

    setItemIdentifiers(variant, obj);
    setReifier(variant, obj);
  }

  private void importOccurrence(JSONObject obj, TopicIF topic)
      throws JSONException {
    String value = obj.getString("value");
    LocatorIF datatype = getDataType(obj);
    TopicIF type = getType(obj);

    OccurrenceIF oc = tm.getBuilder().makeOccurrence(topic, type, value,
        datatype);

    Collection<TopicIF> scopes = getScope(obj);
    for (TopicIF scope : scopes) {
      oc.addTheme(scope);
    }

    setItemIdentifiers(oc, obj);
    setReifier(oc, obj);
  }

  private Collection<TopicIF> getScope(JSONObject obj) throws JSONException {
    Collection<TopicIF> scopes = new LinkedList<TopicIF>();
    if (obj.has("scope")) {
      JSONArray arr = obj.getJSONArray("scope");
      if (arr != null) {
        for (int i = 0; i < arr.length(); i++) {
          String ref = arr.getString(i);
          TopicIF scope = getReference(ref);
          if (scope != null) {
            scopes.add(scope);
          }
        }
      }
    }
    return scopes;
  }

  private LocatorIF getDataType(JSONObject obj) throws JSONException {
    String type = null;
    if (obj.has("datatype")) {
      type = obj.getString("datatype");
    }
    if (type == null) {
      type = "http://www.w3.org/2001/XMLSchema#string";
    }
    return getLocator(type);
  }

  private TopicIF getType(JSONObject obj) throws JSONException {
    if (obj.has("type")) {
      String type = obj.getString("type");
      if (type != null) {
        TopicIF ref = getReference(type);
        return ref;
      }
    }
    return null;
  }

  private TopicIF getPlayer(JSONObject obj) throws JSONException {
    if (obj.has("player")) {
      String player = obj.getString("player");
      if (player != null) {
        TopicIF ref = getReference(player);
        return ref;
      }
    }
    return null;
  }

  private TMObjectIF setItemIdentifiers(TMObjectIF element, JSONObject obj)
      throws JSONException {
    if (obj.has("item_identifiers")) {
      JSONArray arr = obj.getJSONArray("item_identifiers");
      if (arr != null) {
        for (int i = 0; i < arr.length(); i++) {
          String ref = arr.getString(i);
          LocatorIF iid = element.getTopicMap().getStore().getBaseAddress()
              .resolveAbsolute("#" + ref);
          
          if (element instanceof TopicIF) {
            TMObjectIF other = tm.getObjectByItemIdentifier(iid);
            if (other != null) {
              if (other instanceof TopicIF)
              element = merge((TopicIF) element, (TopicIF) other);
            }
          }
          
          if (iid != null) {
            element.addItemIdentifier(iid);
          }
        }
      }
    }
    
    return element;
  }

  private TopicIF setSubjectIdentifiers(TopicIF element, JSONObject obj)
      throws JSONException {
    if (obj.has("subject_identifiers")) {
      JSONArray arr = obj.getJSONArray("subject_identifiers");
      if (arr != null) {
        for (int i = 0; i < arr.length(); i++) {
          String ref = arr.getString(i);
          LocatorIF sid = getLocator(ref);
          
          TopicIF other = tm.getTopicBySubjectIdentifier(sid);
          if (other != null) {
            element = merge(element, other);
          }
          
          if (sid != null) {
            element.addSubjectIdentifier(sid);
          }
        }
      }
    }
    
    return element;
  }

  private TopicIF setSubjectLocators(TopicIF element, JSONObject obj)
      throws JSONException {
    if (obj.has("subject_locators")) {
      JSONArray arr = obj.getJSONArray("subject_locators");
      if (arr != null) {
        for (int i = 0; i < arr.length(); i++) {
          String ref = arr.getString(i);
          LocatorIF sl = getLocator(ref);
          
          TopicIF other = tm.getTopicBySubjectIdentifier(sl);
          if (other != null) {
            element = merge(element, other);
          }
          
          if (sl != null) {
            element.addSubjectLocator(sl);
          }
        }
      }
    }
    
    return element;
  }

  private void setReifier(ReifiableIF element, JSONObject obj)
      throws JSONException {
    if (obj.has("reifier")) {
      String reference = obj.getString("reifier");
      if (reference != null) {
        TopicIF ref = getReference(reference);
        if (ref != null) {
          element.setReifier(ref);
        }
      }
    }
  }

  private LocatorIF getLocator(String url) {
    try {
      return new URILocator(url);
    } catch (MalformedURLException e) {
      return null;
    }
  }

  private TopicIF getReference(String reference) {
    String plainRef = reference.substring(3);
    TopicIF topic = null;

    if (reference.startsWith("si:")) {
      LocatorIF loc = getLocator(plainRef);
      topic = tm.getTopicBySubjectIdentifier(loc);
      if (topic == null) {
        topic = tm.getBuilder().makeTopic();
        topic.addSubjectIdentifier(loc);
      }
    } else if (reference.startsWith("sl:")) {
      LocatorIF loc = getLocator(plainRef);
      topic = tm.getTopicBySubjectLocator(loc);
      if (topic == null) {
        topic = tm.getBuilder().makeTopic();
        topic.addSubjectLocator(loc);
      }
    } else if (reference.startsWith("ii:")) {
      LocatorIF loc = tm.getStore().getBaseAddress().resolveAbsolute(
          plainRef);
      
      topic = (TopicIF) tm.getObjectByItemIdentifier(loc);
      if (topic == null) {
        topic = tm.getBuilder().makeTopic();
        topic.addItemIdentifier(loc);
      }
    }

    return topic;
  }

  private TopicIF merge(TopicIF topic, TopicIF other) {
    if (topic == other)
      return topic;

    // get rid of object with lowest id
    if (topic.getObjectId().compareTo(other.getObjectId()) > 0) {
      MergeUtils.mergeInto(other, topic); // topic is now lost...
      return other;
    } else {
      MergeUtils.mergeInto(topic, other); // other is now lost...
      return topic;
    }
  }

  @SuppressWarnings("unchecked")
  public void validate(JSONObject object, Map<String, SchemaInfo> keys)
      throws JSONException {
    Iterator<String> it = object.keys();
    while (it.hasNext()) {
      String key = it.next();
      SchemaInfo schema = keys.remove(key);
      if (schema == null) {
        throw new JSONException("element '" + key
            + "' not specified in the JTM schema.");
      } else {
        switch (schema.type) {
        case STRING:
          String val = object.getString(key);
          if (schema.value != null && !schema.value.equals(val)) {
            throw new JSONException("value for element '" + key
                + "' has to be '" + schema.value + "'.");
          }
          break;
        case ARRAY:
          JSONArray arr = object.getJSONArray(key);
          for (int i = 0; i < arr.length(); i++) {
            Object o = arr.get(i);
            switch (schema.datatype) {
            case STRING:
              break;
            case REFERENCE:
              checkReference((String) o);
              break;
            default:
              validate((JSONObject) o, getSchema(schema.datatype));
              break;
            }
          }
          break;
        case OBJECT:
          // TODO: JTM schema does not contain JSONObject assignment, consider
          // removing it here
          // JSONObject obj = object.getJSONObject(key);
          break;
        }
      }
    }

    checkRequiredKeys(keys);
  }

  private Map<String, SchemaInfo> getSchema(OBJECT_TYPE type) {
    Map<String, SchemaInfo> keys = new HashMap<String, SchemaInfo>();

    switch (type) {
    case TOPICMAP:
      keys.put("topics", new SchemaInfo(JSON_TYPE.ARRAY, OBJECT_TYPE.TOPIC,
          false));
      keys.put("associations", new SchemaInfo(JSON_TYPE.ARRAY,
          OBJECT_TYPE.ASSOCIATION, false));
      keys.put("reifier", new SchemaInfo(JSON_TYPE.STRING,
          OBJECT_TYPE.REFERENCE, false));
      break;

    case TOPIC:
      keys.put("names",
          new SchemaInfo(JSON_TYPE.ARRAY, OBJECT_TYPE.NAME, false));
      keys.put("occurrences", new SchemaInfo(JSON_TYPE.ARRAY,
          OBJECT_TYPE.OCCURRENCE, false));
      keys.put("subject_identifiers", new SchemaInfo(JSON_TYPE.ARRAY,
          OBJECT_TYPE.STRING, false, false));
      keys.put("subject_locators", new SchemaInfo(JSON_TYPE.ARRAY,
          OBJECT_TYPE.STRING, false, false));
      break;

    case ASSOCIATION:
      keys.put("type", new SchemaInfo(JSON_TYPE.STRING, OBJECT_TYPE.REFERENCE,
          true));
      keys.put("scope", new SchemaInfo(JSON_TYPE.ARRAY, OBJECT_TYPE.REFERENCE,
          false));
      keys.put("roles", new SchemaInfo(JSON_TYPE.ARRAY, OBJECT_TYPE.ROLE,
          false, false));
      keys.put("reifier", new SchemaInfo(JSON_TYPE.STRING,
          OBJECT_TYPE.REFERENCE, false));
      break;

    case ROLE:
      keys.put("player", new SchemaInfo(JSON_TYPE.STRING,
          OBJECT_TYPE.REFERENCE, true));
      keys.put("type", new SchemaInfo(JSON_TYPE.STRING, OBJECT_TYPE.REFERENCE,
          true));
      keys.put("reifier", new SchemaInfo(JSON_TYPE.STRING,
          OBJECT_TYPE.REFERENCE, false));
      break;

    case NAME:
      keys.put("value", new SchemaInfo(JSON_TYPE.STRING, OBJECT_TYPE.STRING,
          true));
      keys.put("type", new SchemaInfo(JSON_TYPE.STRING, OBJECT_TYPE.REFERENCE,
          false));
      keys.put("scope", new SchemaInfo(JSON_TYPE.ARRAY, OBJECT_TYPE.REFERENCE,
          false));
      keys.put("variants", new SchemaInfo(JSON_TYPE.ARRAY, OBJECT_TYPE.VARIANT,
          false));
      keys.put("reifier", new SchemaInfo(JSON_TYPE.STRING,
          OBJECT_TYPE.REFERENCE, false));
      break;

    case VARIANT:
      keys.put("value", new SchemaInfo(JSON_TYPE.STRING, OBJECT_TYPE.STRING,
          true));
      keys.put("datatype", new SchemaInfo(JSON_TYPE.STRING, OBJECT_TYPE.STRING,
          false));
      keys.put("scope", new SchemaInfo(JSON_TYPE.ARRAY, OBJECT_TYPE.REFERENCE,
          true));
      keys.put("reifier", new SchemaInfo(JSON_TYPE.STRING,
          OBJECT_TYPE.REFERENCE, false));
      break;

    case OCCURRENCE:
      keys.put("value", new SchemaInfo(JSON_TYPE.STRING, OBJECT_TYPE.STRING,
          true));
      keys.put("type", new SchemaInfo(JSON_TYPE.STRING, OBJECT_TYPE.REFERENCE,
          true));
      keys.put("datatype", new SchemaInfo(JSON_TYPE.STRING, OBJECT_TYPE.STRING,
          false));
      keys.put("scope", new SchemaInfo(JSON_TYPE.ARRAY, OBJECT_TYPE.REFERENCE,
          false));
      keys.put("reifier", new SchemaInfo(JSON_TYPE.STRING,
          OBJECT_TYPE.REFERENCE, false));
      break;
    }

    // present in every object
    keys.put("item_identifiers", new SchemaInfo(JSON_TYPE.ARRAY,
        OBJECT_TYPE.STRING, false));

    return keys;
  }

  private void checkReference(String reference) throws JSONException {
    if (!reference.startsWith("si:") && !reference.startsWith("sl:")
        && !reference.startsWith("ii:")) {
      throw new JSONException("not a valid topic reference '" + reference
          + "'.");
    }
  }

  private void checkRequiredKeys(Map<String, SchemaInfo> keys)
      throws JSONException {
    for (Map.Entry<String, SchemaInfo> entry : keys.entrySet()) {
      if (entry.getValue().required) {
        throw new JSONException("required element '" + entry.getKey()
            + "' missing.");
      }
    }
  }

  static class SchemaInfo {
    public JSON_TYPE type;
    public Object value;
    public OBJECT_TYPE datatype;
    public boolean required;
    public boolean emptyAllowed;

    public SchemaInfo(Object value, boolean required) {
      this(JSON_TYPE.STRING, value, OBJECT_TYPE.STRING, required);
    }

    public SchemaInfo(JSON_TYPE type, OBJECT_TYPE datatype, boolean required) {
      this(type, null, datatype, required);
    }

    public SchemaInfo(JSON_TYPE type, OBJECT_TYPE datatype, boolean required,
        boolean emptyAllowed) {
      this(type, null, datatype, required, emptyAllowed);
    }

    public SchemaInfo(JSON_TYPE type, Object value, OBJECT_TYPE datatype,
        boolean required) {
      this(type, value, datatype, required, true);
    }

    public SchemaInfo(JSON_TYPE type, Object value, OBJECT_TYPE datatype,
        boolean required, boolean emptyAllowed) {
      this.type = type;
      this.value = value;
      this.datatype = datatype;
      this.required = required;
      this.emptyAllowed = emptyAllowed;
    }
  }
}
