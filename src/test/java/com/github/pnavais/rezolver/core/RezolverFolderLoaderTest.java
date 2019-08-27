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
import com.github.pnavais.rezolver.loader.impl.LocalLoader;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void localFolderResourceWindowsCreationTest() {
        FileSystem winFileSystemFS = Jimfs.newFileSystem(Configuration.windows());

        String tmpDir = "c:\\tmp";
        Path tmp = winFileSystemFS.getPath(tmpDir);

        try {
            Files.createDirectory(tmp);
        } catch (IOException e) {
            fail("Cannot create test directory");
        }

        writeTestFile(tmp, "TestFile.txt");

        LocalLoader loader = new LocalLoader();
        loader.setFileSystem(winFileSystemFS);

        URL lookupURL = loader.lookup("c:/tmp/TestFile.txt");
        assertNotNull("Error retrieving URL", lookupURL);
        ResourceInfo resourceInfo = loader.resolve("c:/tmp/TestFile.txt");
        assertNotNull("Error retrieving ResourceInfo", resourceInfo);
        assertTrue(resourceInfo.isResolved(), "Error resolving resource");

        DirLoader dirLoader = new DirLoader(loader);
        dirLoader.setRootPath("c:/tmp");
        Rezolver r = Rezolver.builder()
                .add(dirLoader)
                .build();

        resolveTestFile(r, "TestFile.txt");
        removeDirectory(tmpDir);
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

}
