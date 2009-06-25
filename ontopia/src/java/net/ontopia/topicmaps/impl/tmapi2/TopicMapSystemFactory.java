// $Id: TopicMapSystemFactory.java,v 1.6 2005/03/20 11:52:05 grove Exp $

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;

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
      new Feature("http://tmapi.org/features/readOnly", true, false) };

  public TopicMapSystemFactory() {
  }

  public TopicMapSystem newTopicMapSystem()
      throws TMAPIException {
    return new TopicMapSystemImpl(this);
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
    setProperty(key, val);
  }

}
