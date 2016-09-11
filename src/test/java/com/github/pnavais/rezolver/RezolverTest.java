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

import com.github.pnavais.rezolver.loader.LocalLoader;
import com.google.common.io.Files;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Rezolver JUnit tests
 */
public class RezolverTest extends RezolverBaseTest {

    /** A custom local loader with in-memory filesystem */
    private static LocalLoader localLoader;

    @BeforeClass
    public static void setup() throws IOException {
        RezolverBaseTest.setup();
        localLoader = new LocalLoader();
        localLoader.setFileSystem(fileSystem);
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
    public void fileSystemResourceTest() {
        URL fRes = Rezolver.newBuilder().withLoader(localLoader).build().lookup("/tmp/fs_resource.nfo");
        assertNotNull("Error resolving resource from classpath", fRes);
        System.out.println("TEMA >> "+fRes);
        try (InputStream inStream = fRes.openStream()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            List<String> lines = in.lines().collect(Collectors.toList());
            System.out.println("LINES >> "+lines.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //List<String> contents = Files.readLines(f, StandardCharsets.UTF_8);
            //assertEquals(contents, Arrays.asList("Test resource physical"));
        //} catch (URISyntaxException e) {
//            fail("Error loading resource from file system");
//        } catch (IOException e) {
//            fail("Error reading lines from file");
//        }
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
