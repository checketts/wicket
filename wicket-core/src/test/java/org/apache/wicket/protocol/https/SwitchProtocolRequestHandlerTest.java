/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.protocol.https;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.MockPage;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.AbstractMapperTest;
import org.apache.wicket.request.mapper.IMapperContext;
import org.apache.wicket.request.mapper.MountedMapper;
import org.mockito.Mockito;

/**
 * Test for SwitchProtocolRequestHandler
 */
public class SwitchProtocolRequestHandlerTest extends AbstractMapperTest
{

	/**
	 * Tests that SwitchProtocolRequestHandler will call WebResponse.sendRedirect() with "https"
	 * protocol and will use the custom https port 1443
	 * 
	 * @throws MalformedURLException
	 */
	public void testRespond() throws MalformedURLException
	{
		// the URL to redirect to
		final URL httpsUrl = new URL("https://example.com:1443/app?param1=value1&param2=value2");

		final HttpServletRequest httpRequest = Mockito.mock(HttpServletRequest.class);
		Mockito.when(httpRequest.getServerName()).thenReturn(httpsUrl.getHost());
		Mockito.when(httpRequest.getRequestURI()).thenReturn(httpsUrl.getPath());
		Mockito.when(httpRequest.getQueryString()).thenReturn(httpsUrl.getQuery());

		final Url url = Url.parse(httpsUrl.getPath() + "?" + httpsUrl.getQuery());
		final ServletWebRequest webRequest = new ServletWebRequest(httpRequest, "", url);

		final IRequestCycle requestCycle = Mockito.mock(IRequestCycle.class);
		Mockito.when(requestCycle.getRequest()).thenReturn(webRequest);

		HttpsConfig httpsConfig = new HttpsConfig(80, 1443);

		// request secure communication (over https)
		final SwitchProtocolRequestHandler handler = new SwitchProtocolRequestHandler(
			Protocol.HTTPS, httpsConfig);

		final WebResponse webResponse = Mockito.mock(WebResponse.class);
		Mockito.when(requestCycle.getResponse()).thenReturn(webResponse);

		handler.respond(requestCycle);

		Mockito.verify(webResponse).sendRedirect(httpsUrl.toString());
	}

	/**
	 * @see <a href="https://issues.apache.org/jira/browse/WICKET-3380">WICKET-3380</a>
	 */
	public void testMapHandler()
	{
		final MountedMapper encoder = new MountedMapper("/securedPage", SecuredMockPage.class)
		{
			@Override
			protected IMapperContext getContext()
			{
				return context;
			}
		};

		Url originalUrl = Url.parse("securedPage");
		IRequestHandler handler = encoder.mapRequest(getRequest(originalUrl));

		SwitchProtocolRequestHandler switchProtocolRequestHandler = new SwitchProtocolRequestHandler(
			Protocol.HTTPS, handler, new HttpsConfig());

		Url mappedUrl = encoder.mapHandler(switchProtocolRequestHandler);
		assertEquals(originalUrl, mappedUrl);
	}

	@RequireHttps
	private static class SecuredMockPage extends MockPage
	{
		private static final long serialVersionUID = 1L;
	}
}
