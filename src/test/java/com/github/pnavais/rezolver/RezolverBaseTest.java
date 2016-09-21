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
import java.util.stream.IntStream;

/**
 * Base class for the Rezolver tests. Initializes
 * all resources used by the tests.
 */
public class RezolverBaseTest {

    /**
     * The testing in-memory file system
     */
    protected static FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

    /**
     * The maximum number of test files
     */
    protected static int MAX_TEST_FILES = 10;

    @BeforeClass
    public static void setup() {
        Path tmp = fileSystem.getPath("/tmp/");
        try {
            Files.createDirectory(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IntStream.range(0, MAX_TEST_FILES).parallel().forEach(i -> writeTestFile(tmp, "fs_resource_" + i + ".nfo"));
    }

    /**
     * Creates a dummy test file in the given directory.
     *
     * @param dir      the target directory
     * @param fileName the file to create
     */
    private static void writeTestFile(Path dir, String fileName) {
        Path testFile = dir.resolve(fileName);
        try {
            Files.write(testFile, ImmutableList.of("Dummy Data"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        IntStream.range(0, MAX_TEST_FILES).parallel().forEach(i -> deleteTestFile("/tmp/fs_resource_" + i + ".nfo"));
        try {
            Files.deleteIfExists(fileSystem.getPath("/tmp/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a file
     *
     * @param fileName the file to remove
     */
    private static void deleteTestFile(String fileName) {
        Path testFile = fileSystem.getPath(fileName);
        try {
            Files.delete(testFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
