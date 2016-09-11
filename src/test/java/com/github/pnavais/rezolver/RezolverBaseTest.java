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

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base class for the Rezolver tests. Initializes
 * all resources used by the tests.
 */
public class RezolverBaseTest {

    /** The testing in-memory file system */
    protected static FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

    @BeforeClass
    public static void setup() throws IOException {
        Path tmp = fileSystem.getPath("/tmp/");
        Files.createDirectory(tmp);
        Path testFile = tmp.resolve("fs_resource.nfo"); // /tmp/resource.nfo
        Files.write(testFile, ImmutableList.of("Test resource physical"), StandardCharsets.UTF_8);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        Path testFile = fileSystem.getPath("/tmp/fs_resource.nfo");
        Files.delete(testFile);
    }

}
