
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.List;
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
  
  public void loadSnapshot(SyncEndpoint endpoint, Snapshot snapshot) {
    throw new UnsupportedOperationException(); // not supported yet
  }

  public void applyFragments(SyncEndpoint endpoint, List<Fragment> fragments) {
    try {
      applyFragments_(endpoint, fragments);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  private void applyFragments_(SyncEndpoint endpoint, List<Fragment> fragments)
    throws IOException {
    // FIXME: so what is the id of our feed? does it matter? do we need it?
    String id = null;

    // (1) build the feed
    StringWriter out = new StringWriter();
    AtomWriter writer = new AtomWriter(out);
    writer.startFeed("Ontopia SDshare push client feed",
                     System.currentTimeMillis(), id);

    for (Fragment fragment : fragments) {
      writer.startEntry("Push fragment", "Some id", fragment.getUpdated());
      writer.addContent(fragment.getContent());
      for (String si : fragment.getTopicSIs())
        writer.addTopicSI(si);
      writer.endEntry();
    }
    
    writer.endFeed();
    String feed = writer.toString();

    // (2) POST the feed to the endpoint
    byte rawdata[] = feed.getBytes("utf-8");
    
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(endpoint.getHandle());
    ByteArrayEntity reqbody = new ByteArrayEntity(rawdata);
    reqbody.setContentType("application/atom+xml");
    httppost.setEntity(reqbody);

    // (3) retrieving the response

    HttpResponse response = httpclient.execute(httppost);
    HttpEntity resEntity = response.getEntity();

    log.warn("Server response: " + response.getStatusLine());

    String msg = StreamUtils.read(new InputStreamReader(resEntity.getContent()));
    log.warn("Body: " + msg);    
  }
}