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

import com.github.pnavais.rezolver.loader.AbstractLoader;
import com.github.pnavais.rezolver.loader.FileLoader;
import com.github.pnavais.rezolver.loader.IResourceLoader;
import com.github.pnavais.rezolver.loader.RemoteLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertEquals;

/**
 * Rezolver JUnit tests
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
        assertNotNull(ctx.getSourceEntity());
        assertEquals("Error retrieving the resolution source", RemoteLoader.class.getSimpleName(), ctx.getSourceEntity());

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

        Rezolver rezolver = Rezolver.newBuilder().withLoader((s, context) -> {
            context.setProperty("key_loader_1", "l1");
            return context;
        }).andLoader(LoaderBuilder.with((s, context) -> {
            context.setProperty("key_loader_2", "l2");
            return context;
        })).andLoader((s, context) -> {
            context.setProperty("key_loader_3", "l3");
            return context;
        }).andLoader((s, context) -> {
            context.setProperty("key_loader_4", "l4");
            return context;
        }, s -> null, s -> {}).build();

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

}

