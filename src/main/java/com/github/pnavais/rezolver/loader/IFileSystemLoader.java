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

package com.github.pnavais.rezolver.loader;

import java.nio.file.FileSystem;

/**
 * Common interface for resource loaders working with
 * file custom file systems.
 */
public interface IFileSystemLoader extends IURL_Loader {

    /**
     * Retrieves the path separator
     *
     * @return the path separator
     */
    String getPathSeparator();


    /**
     * Sets the file system for file resolutions
     *
     * @param fileSystem the file system
     */
    void setFileSystem(FileSystem fileSystem);

    /**
     * Retrieves the running path in string format or
     * null if not able to compute it correctly.
     *
     * Due to UNC file path issues the recommended way
     * for conversions is URL -> URI -> Path.
     *
     * @return the running path or null if not found
     */
    String getRunningPath();

}
