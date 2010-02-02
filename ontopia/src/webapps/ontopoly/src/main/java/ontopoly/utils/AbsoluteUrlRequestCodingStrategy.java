package ontopoly.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

public class AbsoluteUrlRequestCodingStrategy implements IRequestCodingStrategy {

  private final IRequestCodingStrategy defaultStrategy;

  public AbsoluteUrlRequestCodingStrategy(IRequestCodingStrategy defaultStrategy) {
    this.defaultStrategy = defaultStrategy;    
  }
  
  public static CharSequence toAbsoluteUrl(String url) {
    if (url.startsWith("../../")) {
      // make relative links absolute
      HttpServletRequest req = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest();
      return req.getContextPath() + "/" + url.substring("../../".length());
      
//    } else if (url.startsWith("../")) {
//      // make relative links absolute
//      HttpServletRequest req = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest();
//      return req.getContextPath() + "/" + url.substring("../".length());
//      
    } else {
      return RequestUtils.toAbsolutePath(url);
    }
  }
  
  public RequestParameters decode(Request request) {
    return defaultStrategy.decode(request);
  }

  public CharSequence encode(RequestCycle requestCycle, IRequestTarget requestTarget) {
    return toAbsoluteUrl(defaultStrategy.encode(requestCycle, requestTarget).toString());
  }

  public String rewriteStaticRelativeUrl(String string) {
    return defaultStrategy.rewriteStaticRelativeUrl(string);
  }

  public void addIgnoreMountPath(String path) {
    defaultStrategy.addIgnoreMountPath(path);
  }

  public void mount(IRequestTargetUrlCodingStrategy urlCodingStrategy) {
    defaultStrategy.mount(urlCodingStrategy);
  }

  public CharSequence pathForTarget(IRequestTarget requestTarget) {
    return defaultStrategy.pathForTarget(requestTarget);
  }

  public IRequestTarget targetForRequest(RequestParameters requestParameters) {
    return defaultStrategy.targetForRequest(requestParameters);
  }

  public void unmount(String path) {
    defaultStrategy.unmount(path);
  }

  public IRequestTargetUrlCodingStrategy urlCodingStrategyForPath(String path) {
    return new AbsoluteUrlCodingStrategy(defaultStrategy.urlCodingStrategyForPath(path));
  }

  private static class AbsoluteUrlCodingStrategy implements IRequestTargetUrlCodingStrategy {

    private final IRequestTargetUrlCodingStrategy defaultStrategy;
    
    public AbsoluteUrlCodingStrategy(IRequestTargetUrlCodingStrategy defaultStrategy) {
      this.defaultStrategy = defaultStrategy;    
    }
    
    public IRequestTarget decode(RequestParameters requestParameters) {
      return defaultStrategy.decode(requestParameters);
    }
    
    public CharSequence encode(IRequestTarget requestTarget) {
      return AbsoluteUrlRequestCodingStrategy.toAbsoluteUrl(defaultStrategy.encode(requestTarget).toString());
    }
    
    public String getMountPath() {
      return defaultStrategy.getMountPath();
    }
    
    public boolean matches(IRequestTarget requestTarget) {
      return defaultStrategy.matches(requestTarget);
    }
    
    public boolean matches(String path, boolean caseSensitive) {
      return defaultStrategy.matches(path, caseSensitive);
    }

  }
  
}
