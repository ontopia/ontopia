/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
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
    // make relative links absolute
    if (url.startsWith("../../")) {
      HttpServletRequest req = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest();
      return req.getContextPath() + "/" + url.substring("../../".length());
    } else {
      return RequestUtils.toAbsolutePath(url);
    }
  }
  
  @Override
  public RequestParameters decode(Request request) {
    return defaultStrategy.decode(request);
  }

  @Override
  public CharSequence encode(RequestCycle requestCycle, IRequestTarget requestTarget) {
    return toAbsoluteUrl(defaultStrategy.encode(requestCycle, requestTarget).toString());
  }

  @Override
  public String rewriteStaticRelativeUrl(String string) {
    return defaultStrategy.rewriteStaticRelativeUrl(string);
  }

  @Override
  public void addIgnoreMountPath(String path) {
    defaultStrategy.addIgnoreMountPath(path);
  }

  @Override
  public void mount(IRequestTargetUrlCodingStrategy urlCodingStrategy) {
    defaultStrategy.mount(urlCodingStrategy);
  }

  @Override
  public CharSequence pathForTarget(IRequestTarget requestTarget) {
    return defaultStrategy.pathForTarget(requestTarget);
  }

  @Override
  public IRequestTarget targetForRequest(RequestParameters requestParameters) {
    return defaultStrategy.targetForRequest(requestParameters);
  }

  @Override
  public void unmount(String path) {
    defaultStrategy.unmount(path);
  }

  @Override
  public IRequestTargetUrlCodingStrategy urlCodingStrategyForPath(String path) {
    return new AbsoluteUrlCodingStrategy(defaultStrategy.urlCodingStrategyForPath(path));
  }

  private static class AbsoluteUrlCodingStrategy implements IRequestTargetUrlCodingStrategy {

    private final IRequestTargetUrlCodingStrategy defaultStrategy;
    
    public AbsoluteUrlCodingStrategy(IRequestTargetUrlCodingStrategy defaultStrategy) {
      this.defaultStrategy = defaultStrategy;    
    }
    
    @Override
    public IRequestTarget decode(RequestParameters requestParameters) {
      return defaultStrategy.decode(requestParameters);
    }
    
    @Override
    public CharSequence encode(IRequestTarget requestTarget) {
      return AbsoluteUrlRequestCodingStrategy.toAbsoluteUrl(defaultStrategy.encode(requestTarget).toString());
    }
    
    @Override
    public String getMountPath() {
      return defaultStrategy.getMountPath();
    }
    
    @Override
    public boolean matches(IRequestTarget requestTarget) {
      return defaultStrategy.matches(requestTarget);
    }
    
    @Override
    public boolean matches(String path, boolean caseSensitive) {
      return defaultStrategy.matches(path, caseSensitive);
    }

  }
  
}
