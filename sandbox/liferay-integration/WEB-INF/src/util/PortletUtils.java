
package util;

import java.util.Map;
import java.util.HashMap;
import net.ontopia.utils.StringTemplateUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.utils.QueryWrapper;

/**
 * A collection of utility methods used by the portlets to perform
 * various tasks.
 */
public class PortletUtils {

  /**
   * Produces the correct link to the given topic using the URL template
   * for the topic type as stored in the topic map.
   */
  public static String makeLinkTo(TopicIF topic) {
    // set up
    QueryWrapper w = new QueryWrapper(topic.getTopicMap());
    w.setDeclarations("using lr for i\"http://psi.ontopia.net/liferay/\" ");
    
    // find the template
    Map params = w.makeParams("topic", topic);
    String template = w.queryForString(
       "select $URL from " +                                       
       "direct-instance-of(%topic%, $TYPE), " +
       "lr:url-template($TYPE, $URL)?", params);

    // if there's no template we can't make a link, so just returning an
    // error
    if (template == null)
      return "No template set"; // FIXME: throw exception?

    // start setting up templates
    Map templateparams = new HashMap();
    templateparams.put("topicid", topic.getObjectId());
    
    // is this Liferay web content? if so, we need two more parameters
    if (1 == 2) {
      String gid = w.queryForString(
        "select $GID from " +                                    
        "lr:contains(%topic% : lr:containee, $GROUP : lr:container), " +
        "lr:groupid($GROUP, $GID)?", params);

      String aid = w.queryForString("lr:article_id(%topic%, $AID)?");

      templateparams.put("groupid", gid);
      templateparams.put("articleid", gid);
    }

    // evaluate the template
    return StringTemplateUtils.replace(template, templateparams);
  }

  /**
   * Parses the query string to extract the query parameters as a map.
   */
  public static Map<String, String> parseQueryString(String str) {
    if (str.charAt(0) == '?')
      throw new OntopiaRuntimeException("Idiots: '"+str+"'");

    Map<String, String> params = new HashMap();
    int prevamp = -1;
    int preveq = -1;
    for (int ix = 0; ix < str.length(); ix++) {
      char ch = str.charAt(ix);

      if (ch == '=') {
        if (preveq != -1)
          throw new OntopiaRuntimeException("Bad query string " + str);
        preveq = ix;
      } else if (ch == '&') {
        if (preveq == -1)
          throw new OntopiaRuntimeException("Bad query string " + str);
        String name = str.substring(prevamp + 1, preveq);
        String value = str.substring(preveq + 1, ix);
        params.put(name, value);
        prevamp = ix;
        preveq = -1;
      }
    }

    // must also do last pair
    if (preveq == -1)
      throw new OntopiaRuntimeException("Bad query string " + str);
    String name = str.substring(prevamp + 1, preveq);
    String value = str.substring(preveq + 1);
    params.put(name, value);
    
    return params;
  }
}