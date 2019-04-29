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
import com.github.pnavais.rezolver.loader.impl.DirLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Rezolver folder loader tests
 */
public class RezolverFolderLoaderTest extends RezolverTestBase {

    @BeforeAll
    public static void setupLocal() {
        Path tmp = createDirectory("/tmp/inner");
        IntStream.range(0, MAX_TEST_FILES).parallel().forEach(i -> writeTestFile(tmp, "fs_inner_resource_" + i + ".nfo"));
    }

    @AfterAll
    public static void tearDownLocal() {
        removeDirectory("/tmp/inner/");
    }


    @Test
    void localFolderResourceTest() {
        Rezolver r = Rezolver.builder().add(DirLoader.of(localLoader, "/tmp/inner")).build();
        resolveTestFiles(r, "fs_inner_resource_", ".nfo");
        r = Rezolver.builder().add(DirLoader.of(localLoader, "/tmp/inner/")).build();
        resolveTestFiles(r, "fs_inner_resource_", ".nfo");
    }

    @Test
    void localFolderResourceSimpleCreationTest() {
        DirLoader dirLoader = new DirLoader(localLoader);
        dirLoader.setRootPath("/tmp/inner");
        Rezolver r = Rezolver.builder().add(dirLoader).build();
        resolveTestFiles(r, "fs_inner_resource_", ".nfo");
        r = Rezolver.builder().add(DirLoader.of(localLoader, "/tmp/inner/")).build();
        resolveTestFiles(r, "fs_inner_resource_", ".nfo");
    }

   @Test
    void outsideRootFolderResourceTest() {
        Rezolver r = Rezolver.builder().add(DirLoader.of(localLoader, "/tmp/inner")).build();
        ResourceInfo resInfo = r.resolve("/tmp/inner/fs_inner_resource_0.nfo");
        assertTrue(resInfo.isResolved(), "Error resolving same root path");
        resInfo = r.resolve("/tmp/inner/../inner/fs_inner_resource_0.nfo");
        assertTrue(resInfo.isResolved(), "Error resolving same root path with normalization");

        resInfo = r.resolve("/tmp/fs_resource_0.nfo");
        assertFalse(resInfo.isResolved(), "Error resolving outside root path");

        // Relative checks
        resInfo = r.resolve("../fs_resource_0.nfo");
        assertFalse(resInfo.isResolved(), "Error resolving outside root path with relative location");
        resInfo = r.resolve("../inner/fs_inner_resource_0.nfo");
        assertTrue(resInfo.isResolved(), "Error resolving outside root path with relative location");

        resolveTestFiles(r, "fs_inner_resource_", ".nfo");
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
