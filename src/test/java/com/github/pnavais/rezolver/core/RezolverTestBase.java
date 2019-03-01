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

import com.github.pnavais.rezolver.loader.impl.LocalLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.IntStream;

/**
 * Base class for the Rezolver tests. Initializes
 * all resources used by the tests.
 */
public class RezolverTestBase {

    /** A custom local loader with in-memory filesystem */
    LocalLoader localLoader;

    /** The testing in-memory file system */
    static FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

    /** The maximum number of test files */
    static int MAX_TEST_FILES = 10;

    /**
     * Initializes default loaders
     */
    public RezolverTestBase() {
        localLoader = new LocalLoader();
        localLoader.setFileSystem(fileSystem);
    }

    @BeforeAll
    public static void setup() {

        // Create in memory test files
        Path tmp = fileSystem.getPath("/tmp/");
        try {
            Files.createDirectory(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        IntStream.range(0, MAX_TEST_FILES).parallel().forEach(i -> writeTestFile(tmp, "fs_resource_" + i + ".nfo"));

        // Create the duplicate resource
        writeTestFile(tmp, "dup_resource.nfo");
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

    @AfterAll
    public static void tearDown() {

        Path testDir = fileSystem.getPath("/tmp/");
        try {
            Files.walkFileTree(testDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
