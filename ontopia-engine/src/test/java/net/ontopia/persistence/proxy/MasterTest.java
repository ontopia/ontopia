
package net.ontopia.persistence.proxy;

import java.util.*;
import java.net.URL;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import net.ontopia.infoset.core.*;
import net.ontopia.topicmaps.core.*;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.MessageListener;
import org.jgroups.blocks.PullPushAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
public abstract class MasterTest implements java.io.Serializable {
  
  protected transient Map data;
  
  public String testname;
  public String objectId;
  public String value;
  
  public MasterTest(Map data, String testname) {
    this.data = data;
    this.testname = testname;
  }
  
  public abstract void run();

  public TopicIF getTopic() {
    return (TopicIF)data.get("TopicIF");
  }

  public TopicNameIF getTopicName() {
    return (TopicNameIF)data.get("TopicNameIF");
  }

  public VariantNameIF getVariantName() {
    return (VariantNameIF)data.get("VariantNameIF");
  }

  public OccurrenceIF getOccurrence() {
    return (OccurrenceIF)data.get("OccurrenceIF");
  }

  public AssociationIF getAssociation() {
    return (AssociationIF)data.get("AssociationIF");
  }

  public AssociationRoleIF getAssociationRole() {
    return (AssociationRoleIF)data.get("AssociationRoleIF");
  }
  
}
