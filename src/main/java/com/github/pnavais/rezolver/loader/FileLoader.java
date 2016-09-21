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

import java.io.File;
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
public class FileLoader extends AbstractLoader {

    /** The file system for lookups */
    private FileSystem fileSystem;

    /**
     * Constructor with custom fallback path.
     */
    public FileLoader() {
        this(null);
    }

    /**
     * Constructor with default application path
     * as fallback path for resolution purposes.
     */
    public FileLoader(String path) {
        fallbackPath = (path != null) ? path : getRunningPath();
        fileSystem  = FileSystems.getDefault();
    }

    /**
     * Retrieves the URL from the given resource path
     * in the filesystem.
     *
     * @param resourcePath the path to the resource in the filesystem
     * @return the URL to the resource
     */
    @Override
    public URL resolveResource(String resourcePath) {
        URL resourceURL = null;
        try {
            if (resourcePath != null) {
                Path path = fileSystem.getPath(resourcePath);
                if (Files.exists(path)) {
                    try {
                        resourceURL = path.toUri().toURL();
                    } catch (MalformedURLException e) {
                    }
                }
            }
        } catch (InvalidPathException e) {
        }

        return resourceURL;
    }

    /**
     * Retrieves the URL scheme associated to the loader
     *
     * @return the URL scheme
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
    protected String getPathSeparator() {
        return fileSystem.getSeparator();
    }

    /**
     * Sets the file system for file resolutions
     *
     * @param fileSystem the file system
     */
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
    private String getRunningPath() {
        String path = null;
        Class<?> clazz = FileLoader.class;
        if (Thread.currentThread().getStackTrace().length>2) {
            String className = Thread.currentThread().getStackTrace()[3].getClassName();
            if (className != null) {
                try {
                    clazz = Class.forName(className);
                } catch (Exception ignored) { }
            }
        }

        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        // Check if the code source is inside a file (JAR)
        try {
            File f = new File(location.toURI());
            if (f.isFile()) {
                path = Paths.get(f.getParentFile().getCanonicalFile().toURI()).toString();
            }
        } catch (Exception ignored) {
        }

        return path;
    }

}
