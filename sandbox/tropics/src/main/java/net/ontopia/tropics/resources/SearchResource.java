package net.ontopia.tropics.resources;

import java.io.StringWriter;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.tmrap.TMRAPException;
import net.ontopia.topicmaps.utils.tmrap.TMRAPImplementation;
import net.ontopia.tropics.utils.URIUtils;
import net.ontopia.xml.PrettyPrinter;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;

@SuppressWarnings("deprecation")
public class SearchResource extends BaseResource {

  @Get("xml")
  public Representation search() throws ResourceException {
    Map<QueryParam, String> params = URIUtils.extractParameters(getResponse(), getQuery());

    TopicMapIF tm = getTopicMapFromParameter(params.get(QueryParam.INCLUDE));
    if (tm == null) {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      return null;
    }

    StringWriter sw = new StringWriter();
    PrettyPrinter pp = new PrettyPrinter(sw, "utf-8");
    try {
      QueryProcessorIF qProcessor = QueryUtils.createQueryProcessor(tm);
      QueryResultIF qResult = qProcessor.execute(params.get(QueryParam.QUERY));
      generateTolog(pp, qResult, "tropics");      
    } catch (InvalidQueryException e) {
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
      e.printStackTrace();
      return null;
    } catch (TMRAPException e) {
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
      e.printStackTrace();
      return null;
    } catch (SAXException e) {
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
      e.printStackTrace();
      return null;
    } catch (Exception e) {
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
      e.printStackTrace();
      return null;
    }

    return new StringRepresentation(sw.getBuffer().toString(),
        MediaType.TEXT_XML);
  }
  
  private void generateTolog(DocumentHandler handler, QueryResultIF result, String view) throws SAXException, TMRAPException {
    AttributeListImpl atts = new AttributeListImpl();
    handler.startDocument();

    atts.addAttribute("xmlns:x", "CDATA", "http://www.topicmaps.org/xtm/1.0/");
    atts.addAttribute("xmlns:l", "CDATA", "http://www.w3.org/1999/xlink");
    handler.startElement("result", atts);

    atts.clear();
    handler.startElement("head", atts);
    for (int ix = 0; ix < result.getWidth(); ix++) {
      handler.startElement("column", atts);
      String name = result.getColumnName(ix);
      char[] chars = name.toCharArray();
      handler.characters(chars, 0, chars.length);
      handler.endElement("column");
    }
    handler.endElement("head");

    while (result.next()) {
      handler.startElement("row", atts);
      for (int ix = 0; ix < result.getWidth(); ix++) {
        handler.startElement("value", atts);
        Object value = result.getValue(ix);
        if (value == null) {
          // use empty element
        } else if (value instanceof TopicIF) {
          TopicIF topic = (TopicIF) value;
          if (view == null)
            view = "stub";
          if (view.equals("tropics")) {
            AttributeListImpl valueAtts = new AttributeListImpl(); // this is slow!!
            LocatorIF valueLoc = null;
            if (!topic.getItemIdentifiers().isEmpty()) {
              for (LocatorIF itemIdentifier : topic.getItemIdentifiers()) {
                if (itemIdentifier.getAddress().startsWith(getRequest().getHostRef() + "/api/v1/topics/")) {
                  valueLoc = itemIdentifier;
                  break;
                }
              }
            } else {
              throw new TMRAPException("Not implemented yet"); // FIXME!!
            }

            if (valueLoc == null) {
              TMRAPImplementation.makeStub(topic, handler);
            } else {
              valueAtts.addAttribute("l:href", "CDATA", valueLoc.getExternalForm());
              handler.startElement("x:topicRef", valueAtts);
              handler.endElement("x:topicRef");
            }
          } else if (view.equals("stub")) {
            TMRAPImplementation.makeStub(topic, handler);
          } else if (view.equals("full-name")) {
            TMRAPImplementation.makeFullName(topic, handler);
          }
        } else if (value instanceof String || value instanceof Number) {
          String svalue = value.toString();
          char[] chars = svalue.toCharArray();
          handler.characters(chars, 0, chars.length);
        } else
          throw new TMRAPException("Unsupported value: " + value);

        handler.endElement("value");
      }
      handler.endElement("row");
    }

    handler.endElement("result");
    handler.endDocument();
  }

}
