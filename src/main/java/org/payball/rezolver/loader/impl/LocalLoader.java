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

package org.payball.rezolver.loader.impl;

import org.payball.rezolver.loader.IFileSystemLoader;

import java.net.MalformedURLException;
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
public class LocalLoader extends UrlLoader implements IFileSystemLoader {

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
    public URL lookup(String location) {
        URL resourceURL = null;
        try {
            if (location != null) {
                Path path = fileSystem.getPath(location);
                if (Files.exists(path)) {
                    resourceURL = path.toUri().toURL();
                }
            }
        } catch (MalformedURLException|InvalidPathException e) {
        }

        return resourceURL;
    }

    /**
     * Retrieves the loader schema for
     * local URL resources.
     *
     * @return the loader schema
     */
    @Override
    public String getUrlScheme() {
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

}
