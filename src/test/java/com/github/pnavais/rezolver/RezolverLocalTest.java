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

import com.github.pnavais.rezolver.loader.impl.ClasspathLoader;
import com.github.pnavais.rezolver.loader.impl.LocalLoader;
import org.junit.Test;

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

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Rezolver Local files tests
 */
public class RezolverLocalTest extends RezolverBaseTest {

    @Test
    public void defaultBuilderLoadingTest() {
        URL dfRes = Rezolver.resolve("classpath:META-INF/cl_resource.nfo");
        URL bdRes = Rezolver.withLoaders(Rezolver.DEFAULT_CHAIN).build().lookup("classpath:META-INF/cl_resource.nfo");
        assertNotNull("Error loading resource from classpath with default chain", dfRes);
        assertNotNull("Error loading resource from classpath with default builder", bdRes);
        assertThat(dfRes.toExternalForm(), is(bdRes.toExternalForm()));
    }

    @Test
    public void nonExistingResourceTest() {
        // Files not resolved in the classpath
        assertNull("Error resolving classpath resource", Rezolver.resolve("classpath:cl_resource_3.nfo"));

        // Files not resolved in the file system
        IntStream.range(0, MAX_TEST_FILES).forEach(i -> {
            URL fRes = Rezolver.resolve("/tmp/fs_resource_"+i+".nfo");
            assertNull("Error resolving resource ["+i+"]", fRes);
        });
    }

    @Test
    public void fileSystemResourceTest() {
        Rezolver r = Rezolver.withLoader(localLoader).build();
        resolveTestFiles(r, "/tmp/fs_resource_", ".nfo");
    }

    /**
     * Resolves the test file using the given resolver instance.
     *
     * @param r the resolver instance
     * @param prefix the prefix of the test file path
     * @param suffix the suffix of the test file path
     */
    private void resolveTestFiles(Rezolver<URL> r, String prefix, String suffix) {
        IntStream.range(0, MAX_TEST_FILES).forEach( i -> {
            URL fRes = r.lookup(prefix+i+suffix);
            assertNotNull("Error resolving resource ["+prefix+i+suffix+"] from filesystem", fRes);
            try (InputStream inStream = fRes.openStream()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
                List<String> contents = in.lines().collect(Collectors.toList());
                assertEquals(contents, Collections.singletonList("Dummy Data"));
            } catch (IOException e) {
                fail("Error reading lines from file");
            }
        });
    }

    @Test
    public void resolveFileWithFallBackTest() {
        Rezolver r = Rezolver.<URL>newBuilder().withLoader(localLoader).build();
        IntStream.range(0, MAX_TEST_FILES).forEach( i -> {
            URL fRes = Rezolver.resolve("fs_resource_"+i+".nfo");
            assertNull("Error resolving resource ["+i+"]", fRes);
        });

        // Set the fallback directory and check if the test files are resolved
        localLoader.setFallbackLocation("/tmp/");
        resolveTestFiles(r, "/fs_resource_", ".nfo");
    }

    @Test
    public void resolveFileWithCustomFallBackTest() {

        LocalLoader loader = new LocalLoader();
        loader.setFileSystem(fileSystem);
        Rezolver<URL> r = Rezolver.<URL>newBuilder()
                .withLoader(new ClasspathLoader())
                .andLoader(loader, "/tmp/")
                .build();

        resolveTestFiles(r, "/fs_resource_", ".nfo");
    }

    @Test
    public void resolveFileWithFallBackOrderTest() {
        LocalLoader loader = new LocalLoader();
        loader.setFileSystem(fileSystem);
        Rezolver<URL> r = Rezolver.<URL>newBuilder()
                .withLoaders(Arrays.asList(loader, new ClasspathLoader()))
                .build();

        URL dupRes = r.lookup("dup_resource.nfo");
        assertNotNull("Error resolving the resource", dupRes);
        assertNotNull("Error retrieving the context", r.getContext());
        assertEquals("Resource resolution mismatch", ClasspathLoader.class.getSimpleName(), r.getContext().getSourceEntity());

        // Use a fallback path
        loader.setFallbackLocation("/tmp/");
        dupRes = r.lookup("dup_resource.nfo");
        assertNotNull("Error resolving the resource", dupRes);
        assertNotNull("Error retrieving the context", r.getContext());
        assertEquals("Resource resolution mismatch", LocalLoader.class.getSimpleName(), r.getContext().getSourceEntity());
    }



    @Test
    public void resolveIncorrectFilePathTest() {
        try {
            URL res = Rezolver.resolve("file:incorrect:path:");
            assertNull("Error resolving the resource",res);
        } catch (Exception e) {
            fail("Error handling exceptions");
        }
    }

    @Test
    public void classPathResourceTest() {
        URL clRes = Rezolver.resolve("classpath:META-INF/cl_resource.nfo");
        assertNotNull("Error loading resource from classpath", clRes);
        URL rRes = Rezolver.withDefaults().build().lookup("META-INF/cl_resource.nfo");
        assertNotNull("Error resolving resource from classpath", rRes);
        assertThat(clRes.toExternalForm(), is(rRes.toExternalForm()));

        clRes = Rezolver.resolve("META-INF/cl_resource.nfo");
        assertNotNull("Error loading resource from classpath", clRes);

        clRes = Rezolver.resolve("cl_resource.nfo");
        assertNotNull("Error resolving classpath resource", clRes);

        clRes = Rezolver.resolve("classpath:cl_resource.nfo");
        assertNotNull("Error resolving classpath resource", clRes);
    }

    @Test
    public void resolveClasspathWithFallBackTest() {
        URL clRes = Rezolver.resolve("cl_resource_2.nfo");
        assertNull("Error resolving classpath resource without fallback", clRes);

        clRes = Rezolver.<URL>newBuilder().withLoader(new ClasspathLoader(), "META-INF/fallback").build().lookup("cl_resource_2.nfo");
        assertNotNull("Error resolving resource from classpath", clRes);

        URL clRes2 = Rezolver.resolve("classpath:META-INF/fallback/cl_resource_2.nfo");
        assertThat("Resolved resource mismatch", clRes, is(clRes2));
    }

    @Test
    public void resolveWithCustomClassLoaderTest() {

        URL testJarURL = Rezolver.resolve("classpath:resources.jar");
        assertNotNull("Cannot retrieve internal testing JAR URL", testJarURL);

        assertNull("Error resolving classpath resource", Rezolver.resolve("cl_resource_3.nfo"));

        // Use a custom classloader
        URLClassLoader customClassLoader = URLClassLoader.newInstance(new URL[] {testJarURL});
        ClasspathLoader loader = new ClasspathLoader();
        loader.setClassLoader(customClassLoader);
        URL cRes = Rezolver.withLoader(loader).build().lookup("cl_resource_3.nfo");
        assertNotNull("Error resolving resource from custom classloader", cRes);
    }

    /*@Test
    public void resolveWithCustomFallbackTest() {
        Rezolver rezolver = Rezolver.newBuilder().withLoader(
                (s, context) -> { context;
        }, s -> null, s -> {
        }).build();
    }*/

}
