/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

package net.ontopia.topicmaps.rest.utils;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Parameter;
import org.restlet.util.Series;

public final class ContextUtils {

	private ContextUtils() {
		// don't call me
	}
	
	public static Context getCurrentApplicationContext() {
		Application application = Application.getCurrent();
		if (application == null) {
			return null;
		}
		return application.getContext();
	}

	public static Series<Parameter> getParameters(Context context) {
		if (context == null) {
			return null;
		}
		return context.getParameters();
	}
	
	public static Parameter getParameter(Context context, String name) {
		Series<Parameter> parameters = getParameters(context);
		if (parameters == null) {
			return null;
		}
		return parameters.getFirst(name);
	}
	
	public static boolean hasParameter(String name) {
		return getParameter(getCurrentApplicationContext(), name) != null;
	}
	
	public static boolean getParameterAsBoolean(String name, boolean fallback) {
		return getParameterAsBoolean(getCurrentApplicationContext(), name, fallback);
	}
	public static boolean getParameterAsBoolean(Context context, String name, boolean fallback) {
		Parameter parameter = getParameter(context, name);
		return getParameterAsBoolean(parameter, fallback);
	}
	public static boolean getParameterAsBoolean(Parameter parameter, boolean fallback) {
		if (parameter == null) {
			return fallback;
		}
		return Boolean.parseBoolean(parameter.getValue());
	}

	public static int getParameterAsInteger(String name, int fallback) {
		return getParameterAsInteger(getCurrentApplicationContext(), name, fallback);
	}
	public static int getParameterAsInteger(Context context, String name, int fallback) {
		Parameter parameter = getParameter(context, name);
		return getParameterAsInteger(parameter, fallback);
	}
	public static int getParameterAsInteger(Parameter parameter, int fallback) {
		if (parameter == null) {
			return fallback;
		}
		return Integer.parseInt(parameter.getValue());
	}
}
