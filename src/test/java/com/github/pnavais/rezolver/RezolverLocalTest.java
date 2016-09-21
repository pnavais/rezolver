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

package com.github.pnavais.rezolver;

import com.github.pnavais.rezolver.loader.FileLoader;
import com.github.pnavais.rezolver.loader.RemoteLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Rezolver JUnit tests
 */
public class RezolverLocalTest extends RezolverBaseTest {

    /** A custom local loader with in-memory filesystem */
    private static FileLoader fileLoader;

    @BeforeClass
    public static void setup() throws IOException {
        RezolverBaseTest.setup();
        fileLoader = new FileLoader();
        fileLoader.setFileSystem(fileSystem);
    }

    @Test
    public void classPathResourceTest() {
        URL clRes = Rezolver.resolve("classpath:META-INF/cl_resource.nfo");
        assertNotNull("Error loading resource from classpath", clRes);
        URL rRes = Rezolver.newBuilder().build().lookup("META-INF/cl_resource.nfo");
        assertNotNull("Error resolving resource from classpath", rRes);
        assertThat(clRes.toExternalForm(), is(rRes.toExternalForm()));
    }

    @Test
    public void nonExistingResourceTest() {
        URL fRes = Rezolver.resolve("/tmp/fs_resource.nfo");
        assertNull("Error resolving resource", fRes);
    }

    @Test
    public void fileSystemResourceTest() {
        Rezolver r = Rezolver.newBuilder().withLoader(fileLoader).build();
        URL fRes = r.lookup("/tmp/fs_resource.nfo");
        assertNotNull("Error resolving resource from classpath", fRes);

        try (InputStream inStream = fRes.openStream()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            List<String> contents = in.lines().collect(Collectors.toList());
            assertEquals(contents, Collections.singletonList("Test resource physical"));
        } catch (IOException e) {
            fail("Error reading lines from file");
        }
    }

    @Test
    public void contextCheckUnresolvedTest() {
        Context ctx = Rezolver.resolveCtx("/tmp/fs_resource.nfo");
        assertNotNull("Error retrieving the resolution context", ctx);
        assertNull("Resource resolution mismatch.Wrong URL", ctx.getResURL());
        assertFalse("Resource resolution status error", ctx.isResolved());
        assertNotNull(ctx.getSourceEntity());
        assertEquals("Error retrieving the resolution source", RemoteLoader.class.getSimpleName(), ctx.getSourceEntity());

    }

    @Test
    public void contextCheckResolvedTest() {
        Rezolver rezolver = Rezolver.newBuilder().withLoader(fileLoader).build();
        Context ctx = rezolver.lookupCtx("/tmp/fs_resource.nfo");
        assertNotNull("Error retrieving the resolution context", ctx);
        assertNotNull("Resource resolution mismatch.Wrong URL", ctx.getResURL());
        assertTrue("Resource resolution status error", ctx.isResolved());
        assertNotNull(ctx.getSourceEntity());
        assertEquals("Error retrieving the resolution source", FileLoader.class.getSimpleName(), ctx.getSourceEntity());
    }

    @Test
    public void defaultBuilderLoadingTest() {
        URL dfRes = Rezolver.resolve("classpath:META-INF/cl_resource.nfo");
        URL bdRes = Rezolver.newBuilder().build().lookup("classpath:META-INF/cl_resource.nfo");
        assertNotNull("Error loading resource from classpath with default chain", dfRes);
        assertNotNull("Error loading resource from classpath with default builder", bdRes);
        assertThat(dfRes.toExternalForm(), is(bdRes.toExternalForm()));
    }

}
