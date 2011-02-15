
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.StringWriter;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.utils.sdshare.AtomWriter;
import net.ontopia.topicmaps.utils.sdshare.client.Fragment;

/**
 * INTERNAL: Backend which uses SDshare push to transfer changes to
 * another machine.
 */
public class PushBackend implements ClientBackendIF {
  static Logger log = LoggerFactory.getLogger(PushBackend.class.getName());
  private static final int SEGMENT_SIZE = 50;
  
  public void loadSnapshot(SyncEndpoint endpoint, Snapshot snapshot) {
    throw new UnsupportedOperationException(); // not supported yet
  }

  public void applyFragments(SyncEndpoint endpoint, List<Fragment> fragments) {
    try {
      // first of all, we break the list into a list of smaller list
      // segments, in order to avoid pushing a feed that's too big.
      List<List<Fragment>> segments = new ArrayList();
      int ix = 1;
      for (; ix * SEGMENT_SIZE < fragments.size(); ix++)
        segments.add(fragments.subList((ix - 1) * SEGMENT_SIZE,
                                       ix * SEGMENT_SIZE));
      segments.add(fragments.subList((ix - 1) * SEGMENT_SIZE, fragments.size()));
      
      // then we loop over the segments, applying each one in turn
      for (List<Fragment> segment : segments)
        applyFragments_(endpoint, segment);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  protected void applyFragments_(SyncEndpoint endpoint, List<Fragment> fragments)
    throws IOException {
    FragmentFeed thefeed = null;
    String id = "http://example.org"; // fallback
    if (!fragments.isEmpty())
      thefeed = fragments.get(0).getFeed();

    if (thefeed != null)
      id = thefeed.getPrefix() + "/fragments";

    // (1) build the feed
    StringWriter out = new StringWriter();
    AtomWriter writer = new AtomWriter(out);
    writer.startFeed("Ontopia SDshare push client feed",
                     System.currentTimeMillis(), id);
    if (thefeed != null)
      writer.addServerPrefix(thefeed.getPrefix());
    
    for (Fragment fragment : fragments) {
      writer.startEntry("Push fragment", "Some id", fragment.getUpdated());
      writer.addContent(fragment.getContent());
      if (fragment.getTopicSIs().isEmpty())
        throw new RuntimeException("Tried making fragment for topic with no " +
                                   "subject identifiers!");
      for (String si : fragment.getTopicSIs())
        writer.addTopicSI(si);
      writer.endEntry();
    }
    
    writer.endFeed();
    String feed = out.toString();

    // (2) POST the feed to the endpoint
    byte rawdata[] = feed.getBytes("utf-8");
    
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(endpoint.getHandle());
    ByteArrayEntity reqbody = new ByteArrayEntity(rawdata);
    reqbody.setContentType("application/atom+xml; charset=utf-8");
    httppost.setEntity(reqbody);

    // (3) retrieving the response

    HttpResponse response = httpclient.execute(httppost);
    HttpEntity resEntity = response.getEntity();

    if (response.getStatusLine().getStatusCode() != 200)
      throw new OntopiaRuntimeException("Error sending SDshare push: " +
                                        response.getStatusLine());
  }
}