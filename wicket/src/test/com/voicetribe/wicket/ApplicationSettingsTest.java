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
package com.voicetribe.wicket;

import java.util.List;
import junit.framework.TestCase;
import junit.framework.Assert;
import com.voicetribe.wicket.resource.DummyApplication;
import com.voicetribe.wicket.resource.ComponentStringResourceLoader;
import com.voicetribe.wicket.resource.ApplicationStringResourceLoader;
import com.voicetribe.wicket.resource.BundleStringResourceLoader;

/**
 * Test cases for the <code>ApplicationSettings</code> class.
 *
 * @author Chris Turner
 */
public class ApplicationSettingsTest extends TestCase {

    /**
     * @param message The name of the test being run
     */
    public ApplicationSettingsTest(final String message) {
        super(message);
    }

    public void testExceptionOnMissingResourceDefaultValue() throws Exception {
        ApplicationSettings settings = new ApplicationSettings(new DummyApplication());
        Assert.assertTrue("exceptionOnMissingResource should default to true",
                          settings.isExceptionOnMissingResource());
    }

    public void testExceptionOnMissingResourceSetsCorrectly() throws Exception {
        ApplicationSettings settings = new ApplicationSettings(new DummyApplication());
        Assert.assertSame(settings, settings.setExceptionOnMissingResource(false));
        Assert.assertFalse("exceptionOnMissingResource should have been set to false",
                          settings.isExceptionOnMissingResource());
    }

    public void testUseDefaultOnMissingResourceDefaultValue() throws Exception {
        ApplicationSettings settings = new ApplicationSettings(new DummyApplication());
        Assert.assertTrue("useDefaultOnMissingResource should default to true",
                          settings.isUseDefaultOnMissingResource());
    }

    public void testUseDefaultOnMissingResourceSetsCorrectly() throws Exception {
        ApplicationSettings settings = new ApplicationSettings(new DummyApplication());
        Assert.assertSame(settings, settings.setUseDefaultOnMissingResource(false));
        Assert.assertFalse("useDefaultOnMissingResource should have been set to false",
                          settings.isUseDefaultOnMissingResource());
    }

    public void testDefaultStringResourceLoaderSetup() {
        ApplicationSettings settings = new ApplicationSettings(new DummyApplication());
        List loaders = settings.getStringResourceLoaders();
        Assert.assertEquals("There should be 2 default loaders", 2, loaders.size());
        Assert.assertTrue("First loader one should be the component one",
                          loaders.get(0) instanceof ComponentStringResourceLoader);
        Assert.assertTrue("Second loader should be the application one",
                          loaders.get(1) instanceof ApplicationStringResourceLoader);
    }

    public void testOverrideStringResourceLoaderSetup() {
        ApplicationSettings settings = new ApplicationSettings(new DummyApplication());
        settings.addStringResourceLoader(new BundleStringResourceLoader("com.voicetribe.wicket.resource.DummyResources"));
        settings.addStringResourceLoader(new ComponentStringResourceLoader());
        List loaders = settings.getStringResourceLoaders();
        Assert.assertEquals("There should be 2 overridden loaders", 2, loaders.size());
        Assert.assertTrue("First loader one should be the bundle one",
                          loaders.get(0) instanceof BundleStringResourceLoader);
        Assert.assertTrue("Second loader should be the component one",
                          loaders.get(1) instanceof ComponentStringResourceLoader);
    }

    public void testLocalizer() {
        ApplicationSettings settings = new ApplicationSettings(new DummyApplication());
        Assert.assertNotNull("Localizer should be available", settings.getLocalizer());
    }
}

///////////////////////////////// End of File /////////////////////////////////
