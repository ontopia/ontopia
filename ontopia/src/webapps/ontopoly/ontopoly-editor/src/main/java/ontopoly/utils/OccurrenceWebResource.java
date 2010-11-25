package ontopoly.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import net.ontopia.net.Base64;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.ReaderInputStream;
import ontopoly.models.TMObjectModel;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class OccurrenceWebResource extends WebResource {

  protected TMObjectModel omodel;
  
  public OccurrenceWebResource() {
  }
  
  public OccurrenceWebResource(TMObjectModel omodel) {
    this.omodel = omodel;
  }

  protected Reader getReader() {
    OccurrenceIF occ = (OccurrenceIF)omodel.getObject();
    return (occ == null ? null : occ.getReader());
  }
  
  @Override
  public IResourceStream getResourceStream() {
    Reader reader = getReader();
    return (reader == null ? null : new Base64EncodedResourceStream(reader));
  }

  public static final IResourceStream getResourceStream(OccurrenceIF occ)  {
    if (occ == null) return null;
    Reader reader = occ.getReader();
    return (reader == null ? null : new Base64EncodedResourceStream(reader));
  }
  
  public static class Base64EncodedResourceStream extends AbstractResourceStream {

    protected Reader reader;
    
    Base64EncodedResourceStream(Reader reader) {
      this.reader = reader;
    }
    
    public InputStream getInputStream() throws ResourceStreamNotFoundException {
      try {
        return new Base64.InputStream(new ReaderInputStream(reader, "utf-8"), Base64.DECODE);
      } catch (UnsupportedEncodingException e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    public void close() throws IOException {
      reader.close();
    }
    
  }
}
