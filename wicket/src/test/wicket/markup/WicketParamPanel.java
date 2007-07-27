/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup;

import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;
import wicket.model.PropertyModel;

/**
 * Dummy component used for ComponentCreateTagTest
 * 
 * @author Juergen Donnerstag
 */
public class WicketParamPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	
    private String text;
    
    /**
     * Construct.
     * @param id 
     */
    public WicketParamPanel(final String id)
    {
        super(id);
        add(new Label("label", new PropertyModel(this, "text")));
    }

    /**
     * 
     * @return String
     */
    public String getText()
    {
		return this.text;
    }
    
    /* (non-Javadoc)
	 * @see wicket.Component#onComponentTag(wicket.markup.ComponentTag)
	 */
	protected void onComponentTag(ComponentTag tag)
	{
		this.text = tag.getAdditionalAttributes().getString("text");
		super.onComponentTag(tag);
	}
}