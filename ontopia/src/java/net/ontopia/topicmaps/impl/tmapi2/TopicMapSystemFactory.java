
// $Id: TopicMapSystemFactory.java,v 1.6 2005/03/20 11:52:05 grove Exp $

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


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
    new Feature("http://tmapi.org/features/readOnly", true, false)
  };

  public TopicMapSystemFactory() {
  }

  public org.tmapi.core.TopicMapSystem newTopicMapSystem()
    throws org.tmapi.core.TMAPIException {
    return new TopicMapSystemImpl(this);
  }

  public boolean hasFeature(String feature) {
    if (features.containsKey(feature))
      return ((Boolean)features.get(feature)).booleanValue();
    // get default if it exists
    for (int i=0; i < dfeatures.length; i++) {
      if (dfeatures[i].name.equals(feature))
	return dfeatures[i].defval;
    }
    return false;
  }
  
  public boolean getFeature(String feature)
    throws org.tmapi.core.FeatureNotRecognizedException {
    if (features.containsKey(feature))
      return ((Boolean)features.get(feature)).booleanValue();
    // get default if it exists
    for (int i=0; i < dfeatures.length; i++) {
      if (dfeatures[i].name.equals(feature))
	return dfeatures[i].defval;
    }
    throw new org.tmapi.core.FeatureNotRecognizedException("The feature name '" + feature + "' is not recognized.");
  }
   
  public void setFeature(String feature, boolean value)
    throws org.tmapi.core.FeatureNotSupportedException, org.tmapi.core.FeatureNotRecognizedException {
    boolean canchg = false;
    for (int i=0; i < dfeatures.length; i++) {
      Feature f = dfeatures[i];
      if (f.name.equals(feature)) {
	if (f.fixed) {
	  if (f.defval == value)
	    return;
	  else
	    throw new org.tmapi.core.FeatureNotSupportedException("Feature '" + feature +"' is fixed to " + f.defval + " by this implementation.");
	} else {
	  canchg = true;
	  break;
	}
      }
    }
    if (!canchg)
      throw new org.tmapi.core.FeatureNotRecognizedException("Feature '" + feature + "' not recognized by this implementation.");

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
  public void setProperty(String arg0, Object arg1) {
    // TODO Auto-generated method stub
    
  }


}
