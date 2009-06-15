
// $Id: ClientTest.java,v 1.1 2007/10/29 13:57:44 geir.gronmo Exp $

package net.ontopia.persistence.proxy.test;

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

import org.apache.log4j.Logger;
  
public abstract class ClientTest {
  
  public abstract void run(MasterTest arg);
  
}
