
package net.ontopia.topicmaps.impl.tmapi2;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */
public class TopicMapSystemFactory extends org.tmapi.core.TopicMapSystemFactory {

  Properties properties = new Properties();
  Map<String, Boolean> features = new HashMap<String, Boolean>();
  Feature[] dfeatures = new Feature[] {
      new Feature("http://tmapi.org/features/notation/URI", true, true),
      new Feature("http://tmapi.org/features/model/xtm1.0", true, true),
      new Feature("http://tmapi.org/features/model/xtm1.1", true, false),
      new Feature("http://tmapi.org/features/automerge", true, false),
      new Feature("http://tmapi.org/features/merge/byTopicName", true, false),
      new Feature("http://tmapi.org/features/type-instance-associations", true, false),
      new Feature("http://tmapi.org/features/readOnly", true, false) };

  public final String STORE_PROPERTY = "net.ontopia.topicmaps.store"; 
  
  public TopicMapSystemFactory() {
  }

  /**
   * <p>
   * Create a new TopicMapSystem instance based on the properties set so far.
   * <br>Supported TopicMapSystems:
   * <ul>
   * <li>{@link MemoryTopicMapSystemImpl}
   * <li>{@link RDBMSTopicMapSystemImpl}
   * </ul>
   * </p>
   * <p>In order to configure the TopicMapStore, you need to set the property
   * "net.ontopia.topicmaps.store" to one of the following values:
   * <ul>
   * <li>memory (default)
   * <li>rdbms
   * </ul>
   * </p>
   */
  public TopicMapSystem newTopicMapSystem() throws TMAPIException {
    String store = properties.getProperty(STORE_PROPERTY);
    if (store != null && store.equalsIgnoreCase("rdbms") )
      return new RDBMSTopicMapSystemImpl(this);
    else 
      return new MemoryTopicMapSystemImpl(this);
  }

  public boolean hasFeature(String feature) {
    if (features.containsKey(feature))
      return ((Boolean) features.get(feature)).booleanValue();
    // get default if it exists
    for (int i = 0; i < dfeatures.length; i++) {
      if (dfeatures[i].name.equals(feature))
        return dfeatures[i].defval;
    }
    return false;
  }

  public boolean getFeature(String feature)
      throws FeatureNotRecognizedException {
    if (features.containsKey(feature))
      return ((Boolean) features.get(feature)).booleanValue();
    // get default if it exists
    for (int i = 0; i < dfeatures.length; i++) {
      if (dfeatures[i].name.equals(feature))
        return dfeatures[i].defval;
    }
    throw new FeatureNotRecognizedException("The feature name '"
        + feature + "' is not recognized.");
  }

  public void setFeature(String feature, boolean value)
      throws FeatureNotSupportedException,
      FeatureNotRecognizedException {
    boolean canchg = false;
    for (int i = 0; i < dfeatures.length; i++) {
      Feature f = dfeatures[i];
      if (f.name.equals(feature)) {
        if (f.fixed) {
          if (f.defval == value)
            return;
          else
            throw new FeatureNotSupportedException("Feature '"
                + feature + "' is fixed to " + f.defval
                + " by this implementation.");
        } else {
          canchg = true;
          break;
        }
      }
    }
    if (!canchg)
      throw new FeatureNotRecognizedException("Feature '"
          + feature + "' not recognized by this implementation.");

    features.put(feature, (value ? Boolean.TRUE : Boolean.FALSE));
  }

  public String getProperty(String propname) {
    return properties.getProperty(propname);
  }

  public void setProperty(String propname, String propval) {
    properties.setProperty(propname, propval);
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  @Override
  public void setProperty(String key, Object val) {
    this.setProperty(key, (String)val);
  }

}
