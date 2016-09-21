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

import com.github.pnavais.rezolver.loader.ClasspathLoader;
import com.github.pnavais.rezolver.loader.FileLoader;
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
import java.util.stream.IntStream;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Rezolver Local files tests
 */
public class RezolverLocalTest extends RezolverBaseTest {

    /** A custom local loader with in-memory filesystem */
    private static FileLoader fileLoader;

    @BeforeClass
    public static void setup() {
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

        clRes = Rezolver.resolve("META-INF/cl_resource.nfo");
        assertNotNull("Error loading resource from classpath", clRes);

        clRes = Rezolver.resolve("classpath:cl_resource.nfo");
        assertNotNull("Error resolving classpath resource", clRes);
    }

    @Test
    public void nonExistingResourceTest() {
        IntStream.range(0, MAX_TEST_FILES).forEach( i -> {
            URL fRes = Rezolver.resolve("/tmp/fs_resource_"+i+".nfo");
            assertNull("Error resolving resource ["+i+"]", fRes);
        });
    }

    @Test
    public void fileSystemResourceTest() {
        Rezolver r = Rezolver.newBuilder().withLoader(fileLoader).build();
        resolveTestFiles(r, "/tmp/fs_resource_", ".nfo");
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
    public void defaultBuilderLoadingTest() {
        URL dfRes = Rezolver.resolve("classpath:META-INF/cl_resource.nfo");
        URL bdRes = Rezolver.newBuilder().build().lookup("classpath:META-INF/cl_resource.nfo");
        assertNotNull("Error loading resource from classpath with default chain", dfRes);
        assertNotNull("Error loading resource from classpath with default builder", bdRes);
        assertThat(dfRes.toExternalForm(), is(bdRes.toExternalForm()));
    }

    @Test
    public void resolveFileWithFallBackTest() {
        Rezolver r = Rezolver.newBuilder().withLoader(fileLoader).build();
        IntStream.range(0, MAX_TEST_FILES).forEach( i -> {
            URL fRes = Rezolver.resolve("fs_resource_"+i+".nfo");
            assertNull("Error resolving resource ["+i+"]", fRes);
        });

        // Set the fallback directory and check if the test files are resolved
        fileLoader.setFallbackPath("/tmp/");
        resolveTestFiles(r, "/fs_resource_", ".nfo");
    }

    @Test
    public void resolveClasspathWithFallBackTest() {
        URL clRes = Rezolver.resolve("cl_resource_2.nfo");
        assertNull("Error resolving classpath resource without fallback", clRes);

        clRes = Rezolver.newBuilder().withLoader(new ClasspathLoader(), "META-INF/fallback").build().lookup("cl_resource_2.nfo");
        assertNotNull("Error resolving resource from classpath", clRes);

        URL clRes2 = Rezolver.resolve("classpath:META-INF/fallback/cl_resource_2.nfo");
        assertThat("Resolved resource mismatch", clRes, is(clRes2));
    }

}
