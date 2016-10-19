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

package com.github.pnavais.rezolver.loader.impl;

import com.github.pnavais.rezolver.ResourceInfo;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;

import static java.util.Objects.requireNonNull;

/**
 * <b>FileLoader</b>
 * <p>
 *  Resolves the location of a given resource on the local
 *  file system. In case no schema is specified
 *  in the given path, this loader will append a valid local one to the
 *  specified resource location string and try to resolve it as last resort.
 * </p>
 */
public class LocalLoader extends URL_Loader implements IFileSystemLoader {

    /** The file system for lookups */
    private FileSystem fileSystem;

    /**
     * Constructor with default fallback path.
     */
    public LocalLoader() {
        fileSystem  = FileSystems.getDefault();
    }

    /**
     * Retrieves the URL from the given resource path
     * in the filesystem.
     *
     * @param location the path to the resource in the filesystem
     * @return the URL to the resource
     */
    @Override
    public ResourceInfo lookup(String location) {
        URL resourceURL = null;
        try {
            if (location != null) {
                Path path = fileSystem.getPath(location);
                if (Files.exists(path)) {
                    try {
                        resourceURL = path.toUri().toURL();
                    } catch (MalformedURLException e) {
                    }
                }
            }
        } catch (InvalidPathException e) {
        }

        return ResourceInfo.from(location,resourceURL);
    }

    /**
     * Retrieves the loader schema for
     * local URL resources.
     *
     * @return the loader schema
     */
    @Override
    public String getURL_Scheme() {
        return "file";
    }

    /**
     * Retrieves the path separator for the loader.
     *
     * @return the path separator
     */
    @Override
    public String getPathSeparator() {
        return fileSystem.getSeparator();
    }

    /**
     * Sets the file system for file resolutions
     *
     * @param fileSystem the file system
     */
    @Override
    public void setFileSystem(FileSystem fileSystem) {
        requireNonNull(fileSystem);
        this.fileSystem = fileSystem;
    }

    /**
     * Retrieves the running path in string format or
     * null if not able to compute it correctly.
     *
     * Due to UNC file path issues the recommended way
     * for conversions is URL -> URI -> Path.
     *
     * @return the running path or null if not found
     */
    @Override
    public String getRunningPath() {
        String path = "";
        try {
            path = LocalLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString();
        } catch (URISyntaxException e) {
        }
        return path;
    }

}
