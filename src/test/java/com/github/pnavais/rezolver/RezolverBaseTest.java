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

import com.github.pnavais.rezolver.utils.StaticLoader;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.file.FileSystem;

/**
 * Base class for the Rezolver tests. Initializes
 * all resources used by the tests.
 */
public class RezolverBaseTest {

    /** The testing in-memory file system */
    private static FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

    @BeforeClass
    public static void setup() {
        StaticLoader.runOnce(() -> {
            //fileSystem.getPath()
            try {
                fileSystem.getPath("/tmp/test.nfo").toFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
