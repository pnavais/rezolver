/*
 * Copyright 2016 Pablo Navais
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.pnavais.rezolver.core;

import com.github.pnavais.rezolver.ResourceInfo;
import com.github.pnavais.rezolver.Rezolver;
import com.github.pnavais.rezolver.loader.impl.HttpLoader;
import com.github.pnavais.rezolver.loader.impl.RemoteLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Rezolver Local files tests in parallel
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpLoader.class })
public class RemoteLoaderTest extends RezolverTestBase {

    @Test
    public void resolveRemoteResourceTest() throws Exception {

        RemoteLoader remoteLoader = PowerMockito.spy(new HttpLoader());

        remoteLoader.setProxy(Proxy.NO_PROXY);

        // Resolve with custom loader in parallel
        final Rezolver r = Rezolver.builder().add(remoteLoader).build();

        String rezName = "http://mock/resource/resource.nfo";

        // Mock URL
        URL u = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withArguments(rezName).thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection(Proxy.NO_PROXY)).thenReturn(huc);
        //PowerMockito.when(huc.getResponseCode()).thenReturn(200);

        InputStream inputStream = PowerMockito.mock(InputStream.class);
        PowerMockito.when(huc.getInputStream()).thenReturn(inputStream);
        PowerMockito.when(huc.getURL()).thenReturn(u);

        ResourceInfo info = r.resolve(rezName);
        PowerMockito.verifyNew(URL.class).withArguments(rezName);
        Mockito.verify(u).openConnection(Proxy.NO_PROXY);
        Mockito.verify(huc).getInputStream();
        Mockito.verify(huc).getURL();

        assertNotNull("Error processing resource "+rezName, info);
        assertNotNull("Error resolving resource "+rezName, info.getURL());

        URL expectedURL = remoteLoader.lookup(rezName);
        assertEquals("URL resolution mismatch", expectedURL, info.getURL());
        assertNotNull("Error retrieving source entity for resource"+rezName, info.getSourceEntity());
    }

}
