package net.ontopia.tropics.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.ontopia.tropics.exceptions.UnknownParameterException;
import net.ontopia.tropics.resources.QueryParam;

import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;

public class URIUtils {
  
  private static final String UNSUPPORTED_PARAMETER_MESSAGE_FORMAT = "Parameter <%s> is not supported.";
  
  public static String buildURI(String baseResource, Map<String, String> queryParams) throws UnsupportedEncodingException {
    StringBuilder sb = new StringBuilder(baseResource);
        
    if (queryParams != null) {
      Set<String> keys = queryParams.keySet();
      if (keys.size() > 0) {
        sb.append('?');

        String delimiter = "";
        for (String name : keys) {
          String value = URLEncoder.encode(queryParams.get(name), "utf-8");
          
          sb.append(delimiter).append(name).append('=').append(value);
          
          delimiter = "&";
        }
      }
    }
    return sb.toString();
  }
  
  public static Map<QueryParam, String> extractParameters(Response response, Form form) throws UnknownParameterException {
    Map<QueryParam, String> params = new HashMap<QueryParam, String>();
    
    Map<String, String> valuesMap = form.getValuesMap();
    for (String key : valuesMap.keySet()) {
      QueryParam param = QueryParam.getQueryParam(key);
      
      if (param == null) {
        response.setStatus(Status.SERVER_ERROR_INTERNAL, String.format(UNSUPPORTED_PARAMETER_MESSAGE_FORMAT, key));
        throw new UnknownParameterException(String.format(UNSUPPORTED_PARAMETER_MESSAGE_FORMAT, key));
      }
      
      params.put(param, valuesMap.get(key));
    }
    
    return params;
  }
  
}
