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
import com.github.pnavais.rezolver.loader.impl.ClasspathLoader;
import com.github.pnavais.rezolver.loader.impl.FallbackLoader;
import com.github.pnavais.rezolver.loader.impl.LocalLoader;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Rezolver Local files tests
 */
public class RezolverLocalTest extends RezolverTestBase {


    private static final String CLASSPATH_META_INF_CL_RESOURCE_NFO = "classpath:META-INF/cl_resource.nfo";
    private static final String TMP_DIR = "/tmp/";

    @Test
    void defaultBuilderLoadingTest() {
        URL dfRes = Rezolver.lookup(CLASSPATH_META_INF_CL_RESOURCE_NFO);
        URL bdRes = Rezolver.builder().withDefaults().build().resolve(CLASSPATH_META_INF_CL_RESOURCE_NFO).getURL();
        assertNotNull(dfRes, "Error loading resource from classpath with default chain");
        assertNotNull(bdRes, "Error loading resource from classpath with default builder");
        assertThat(dfRes.toExternalForm(), is(bdRes.toExternalForm()));
    }

    @Test
    void nonExistingResourceTest() {
        // Files not resolved in the classpath
        assertNull(Rezolver.lookup("classpath:cl_resource_3.nfo"), "Error resolving classpath resource");

        // Files not resolved in the file system
        IntStream.range(0, MAX_TEST_FILES).forEach(i -> {
            URL fRes = Rezolver.lookup("/tmp/fs_resource_"+i+".nfo");
            assertNull(fRes, "Error resolving resource ["+i+"]");
        });
    }

    @Test
    void fileSystemResourceTest() {
        Rezolver r = Rezolver.builder().add(localLoader).build();
        resolveTestFiles(r, "/tmp/fs_resource_", ".nfo");
    }

    @Test
    void resolveFileWithFallBackTest() {
        IntStream.range(0, MAX_TEST_FILES).forEach( i -> {
            URL fRes = Rezolver.lookup("fs_resource_"+i+".nfo");
            assertNull(fRes, "Error resolving resource ["+i+"]");
        });

        // Set the fallback directory and check if the test files are resolved
        Rezolver r = Rezolver.builder().add(localLoader, TMP_DIR).build();
        resolveTestFiles(r, "/fs_resource_", ".nfo");
    }

    @Test
    void resolveFileWithCustomFallBackTest() {
        LocalLoader loader = new LocalLoader();
        loader.setFileSystem(fileSystem);
        Rezolver r = Rezolver.builder()
                .add(new ClasspathLoader())
                .add(loader, TMP_DIR)
                .build();

        resolveTestFiles(r, "/fs_resource_", ".nfo");
    }

    @Test
    void resolveFileWithFallBackOrderTest() {
        LocalLoader fileLoader = new LocalLoader();
        fileLoader.setFileSystem(fileSystem);
        FallbackLoader fallbackLoader = new FallbackLoader(fileLoader);

        Rezolver r = Rezolver.builder()
                .add(Arrays.asList(fallbackLoader, FallbackLoader.of(new ClasspathLoader(), "META-INF")))
                .build();

        ResourceInfo rezInfo = r.resolve("dup_resource.nfo");
        assertNotNull(rezInfo, "Error resolving the resource");
        assertNotNull(rezInfo.getURL(), "Error retrieving the URL");
        assertEquals(ClasspathLoader.class.getSimpleName(), rezInfo.getSourceEntity(), "Resource resolution mismatch");

        // Use a fallback path
        fallbackLoader.setFallbackPath(TMP_DIR);
        ResourceInfo dupRes = r.resolve("dup_resource.nfo");
        assertNotNull(dupRes, "Error resolving the resource");
        assertNotNull(dupRes.getURL(), "Error retrieving the context");
        assertEquals(LocalLoader.class.getSimpleName(), dupRes.getSourceEntity(), "Resource resolution mismatch");
    }

    @Test
    void resolveIncorrectFilePathTest() {
        try {
            URL res = Rezolver.lookup("file:incorrect:path:");
            assertNull(res,"Error resolving the resource");
        } catch (Exception e) {
            fail("Error handling exceptions");
        }
    }

    @Test
    void classPathResourceTest() {
        URL clRes = Rezolver.lookup(CLASSPATH_META_INF_CL_RESOURCE_NFO);
        assertNotNull(clRes, "Error loading resource from classpath");
        URL rRes = Rezolver.builder().withDefaults().build().resolve("META-INF/cl_resource.nfo").getURL();
        assertNotNull(rRes, "Error resolving resource from classpath");
        assertThat(clRes.toExternalForm(), is(rRes.toExternalForm()));

        clRes = Rezolver.lookup("META-INF/cl_resource.nfo");
        assertNotNull(clRes, "Error loading resource from classpath");

        clRes = Rezolver.lookup("cl_resource.nfo");
        assertNotNull(clRes, "Error resolving classpath resource");

        clRes = Rezolver.lookup("classpath:cl_resource.nfo");
        assertNotNull(clRes, "Error resolving classpath resource");
    }

    @Test
    void resolveClasspathWithFallBackTest() {
        URL clRes = Rezolver.lookup("cl_resource_2.nfo");
        assertNull(clRes, "Error resolving classpath resource without fallback");

        clRes = Rezolver.builder().add(new ClasspathLoader(), "META-INF/fallback").build().resolve("cl_resource_2.nfo").getURL();
        assertNotNull(clRes, "Error resolving resource from classpath");

        URL clRes2 = Rezolver.lookup("classpath:META-INF/fallback/cl_resource_2.nfo");
        assertThat("Resolved resource mismatch", clRes, is(clRes2));
    }

    @Test
    void resolveWithCustomClassLoaderTest() {

        URL testJarURL = Rezolver.lookup("classpath:resources.jar");
        assertNotNull(testJarURL, "Cannot retrieve internal testing JAR URL");

        assertNull(Rezolver.lookup("cl_resource_3.nfo"), "Error resolving classpath resource");

        // Use a custom classloader
        URLClassLoader customClassLoader = URLClassLoader.newInstance(new URL[] {testJarURL});
        ClasspathLoader loader = new ClasspathLoader();
        loader.setClassLoader(customClassLoader);
        URL cRes = Rezolver.builder().add(loader).build().resolve("cl_resource_3.nfo").getURL();
        assertNotNull(cRes, "Error resolving resource from custom classloader");
    }


    @Test
    void contextCheckUnresolvedTest() {
        ResourceInfo ctx = Rezolver.fetch("/tmp/fs_resource.nfo");
        assertNotNull(ctx, "Error retrieving the resolution context");
        assertNotNull(ctx.getSearchPath(), "Error retrieving search path");
        assertEquals("/tmp/fs_resource.nfo", ctx.getSearchPath(), "Search path mismatch");
        assertNull(ctx.getURL(), "Resource resolution mismatch.Wrong URL");
        assertFalse(ctx.isResolved(), "Resource resolution status error");
        assertNotNull(ctx.getSourceEntity(), "Source entity not retrieved correctly");
        assertEquals("Unknown", ctx.getSourceEntity(), "Source entity mismatch");
    }

    /**
     * Resolves the test file using the given resolver instance.
     *
     * @param r the resolver instance
     * @param prefix the prefix of the test file path
     * @param suffix the suffix of the test file path
     */
    private void resolveTestFiles(Rezolver r, String prefix, String suffix) {
        IntStream.range(0, MAX_TEST_FILES).forEach( i -> {
            ResourceInfo info = r.resolve(prefix+i+suffix);
            assertNotNull(info, "Error retrieving resource info");
            assertTrue(info.isResolved(), "Error resolving resource");
            assertNotNull(info.getURL(), "Error retrieving resource URL ["+prefix+i+suffix+"] from filesystem");
            try (InputStream inStream = info.getURL().openStream()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
                List<String> contents = in.lines().collect(Collectors.toList());
                assertEquals(contents, Collections.singletonList("Dummy Data"));
            } catch (IOException e) {
                fail("Error reading lines from file");
            }
        });
    }
}
