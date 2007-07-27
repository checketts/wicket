/*
 * $Id$ $Revision$
 * $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket;

import java.util.Locale;
import java.util.Map;

/**
 * Base class for page request implementations allowing access to request
 * parameters. A Request has a URL and a parameter map. You can retrieve the URL
 * of the request with getURL(). The entire parameter map can be retrieved via
 * getParameterMap(). Individual parameters can be retrieved via
 * getParameter(String). If multiple values are available for a given parameter,
 * they can be retrieved via getParameters(String).
 * 
 * @author Jonathan Locke
 */
public abstract class Request
{
	/**
	 * Construct.
	 */
	public Request()
	{
	}

	/**
	 * An implementation of this method is only required if a subclass wishes to
	 * support sessions via URL rewriting. This default implementation simply
	 * returns the URL String it is passed.
	 * 
	 * @param url
	 *            The URL to decode
	 * @return The decoded url
	 */
	public String decodeURL(final String url)
	{
		return url;
	}

	/**
	 * @return The locale for this request
	 */
	public abstract Locale getLocale();

	/**
	 * Gets a given (query) parameter by name.
	 * 
	 * @param key
	 *            Parameter name
	 * @return Parameter value
	 */
	public abstract String getParameter(final String key);

	/**
	 * Gets a map of (query) parameters sent with the request.
	 * 
	 * @return Map of parameters
	 */
	public abstract Map getParameterMap();

	/**
	 * Gets an array of multiple parameters by name.
	 * 
	 * @param key
	 *            Parameter name
	 * @return Parameter values
	 */
	public abstract String[] getParameters(final String key);

	/**
	 * Retrieves the absolute URL of this request for local use.
	 * 
	 * @return The absolute request URL for local use
	 */
	public abstract String getURL();

	/**
	 * Gets the relative (to some root) url (e.g. in a servlet environment, the
	 * url without the context path and without a leading '/'). Use this method
	 * e.g. to load resources using the servlet context.
	 * 
	 * @return Request URL
	 */
	public abstract String getRelativeURL();

	/**
	 * @return Path info for request
	 */
	public abstract String getPath();
}