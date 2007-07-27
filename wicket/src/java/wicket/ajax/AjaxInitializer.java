/*
 * $Id$ $Revision:
 * 4904 $ $Date$
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
package wicket.ajax;

import wicket.Application;
import wicket.IInitializer;
import wicket.markup.html.PackageResource;

/**
 * Initializer for the wicket.ajax package
 * 
 * @see IInitializer
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class AjaxInitializer implements IInitializer
{
	/**
	 * @see wicket.IInitializer#init(wicket.Application)
	 */
	public void init(Application application)
	{
		PackageResource.bind(application, AbstractDefaultAjaxBehavior.class, "wicket-ajax.js");
		PackageResource.bind(application, AbstractDefaultAjaxBehavior.class,
				"wicket-ajax-debug-drag.js");
		PackageResource
				.bind(application, AbstractDefaultAjaxBehavior.class, "wicket-ajax-debug.js");
		PackageResource.bind(application, AbstractDefaultAjaxBehavior.class, "indicator.gif");

	}
}