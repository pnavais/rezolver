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
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Rezolver tests for Context handling
 */
public class RezolverContextTest extends RezolverBaseTest {

    /** A custom local loader with in-memory filesystem */
    private static FileLoader fileLoader;

    @BeforeClass
    public static void setup() {
        RezolverBaseTest.setup();
        fileLoader = new FileLoader();
        fileLoader.setFileSystem(fileSystem);
    }

    @Test
    public void contextCheckUnresolvedTest() {
        Context ctx = Rezolver.resolveCtx("/tmp/fs_resource.nfo");
        assertNotNull("Error retrieving the resolution context", ctx);
        assertNull("Resource resolution mismatch.Wrong URL", ctx.getResURL());
        assertFalse("Resource resolution status error", ctx.isResolved());
        assertNull("Source entity incorrect", ctx.getSourceEntity());
    }

    @Test
    public void contextCheckResolvedTest() {
        Rezolver rezolver = Rezolver.newBuilder().withLoader(fileLoader).build();
        Context ctx = rezolver.lookupCtx("/tmp/fs_resource_0.nfo");
        assertNotNull("Error retrieving the resolution context", ctx);
        assertNotNull("Resource resolution mismatch.Wrong URL", ctx.getResURL());
        assertTrue("Resource resolution status error", ctx.isResolved());
        assertNotNull(ctx.getSourceEntity());
        assertEquals("Error retrieving the resolution source", FileLoader.class.getSimpleName(), ctx.getSourceEntity());
    }

    @Test
    public void contextIncrementalBuildTest() {

        Rezolver rezolver = createTestRezolver();

        Context ctx = rezolver.lookupCtx("dummy");
        assertNotNull("Error retrieving the context", ctx);
        assertFalse("Error during resolution", ctx.isResolved());
        Map<String, Object> data = ctx.getData();
        assertNotNull("Error collecting resolution data", ctx);

        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("key_loader_1", "l1");
        expectedData.put("key_loader_2", "l2");
        expectedData.put("key_loader_3", "l3");
        expectedData.put("key_loader_4", "l4");
        assertEquals("Retrieved resolution data mismatch",expectedData, data);
    }

    @Test
    public void contextResetBuildTest() {

        Rezolver rezolver = createTestRezolver();

        Context ctx = rezolver.lookupCtx("dummy");
        assertNotNull("Error retrieving the context", ctx);
        assertFalse("Error during resolution", ctx.isResolved());

        Context rezCtx = rezolver.getContext();
        assertNotNull("Error retrieving context", rezCtx);
        assertThat("Context mismatch", ctx, is(rezCtx));

        rezCtx.clear();

        ctx = rezolver.lookupCtx("stop_l1");
        assertNotNull("Error retrieving context", ctx);
        assertTrue("Error resolving the resource", ctx.isResolved());
        assertEquals("Context size mismatch",ctx.getData().size(), 1);
        assertEquals("Error retrieving key property from context", "l1", ctx.getProperty("key_loader_1"));
        assertEquals("Error retrieving source entity", "l1", ctx.getSourceEntity());

        ctx = rezolver.lookupCtx("stop_l2");
        assertNotNull("Error retrieving context", ctx);
        assertEquals("Context size mismatch", 4, ctx.getData().size());
        assertFalse("Error resolving the resource",ctx.isResolved());
        assertNull("Error retrieving source entity", ctx.getSourceEntity());
    }

    /**
     * Creates a custom Rezolver with dummy loaders to
     * handle context creation.
     *
     * @return the test Rezolver instance
     */
    private Rezolver createTestRezolver() {
        URL dummyURL = getClass().getProtectionDomain().getCodeSource().getLocation();

        return Rezolver.newBuilder().withLoader((s, context) -> {
                context.setProperty("key_loader_1", "l1");
                context.setSourceEntity("l1");
                return ((s!=null) && (s.equals("stop_l1"))) ? dummyURL : null;
            }).andLoader(LoaderBuilder.with((s, context) -> {
                context.setProperty("key_loader_2", "l2");
                return null;
            })).andLoader((s, context) -> {
                context.setProperty("key_loader_3", "l3");
                return null;
            }).andLoader((s, context) -> {
                context.setProperty("key_loader_4", "l4");
                return null;
            }, s -> null, s -> {}).build();
    }

}

