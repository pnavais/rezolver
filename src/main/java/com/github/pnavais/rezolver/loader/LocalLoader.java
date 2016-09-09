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

import com.github.pnavais.rezolver.Context;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * <b>LocalLoader</b>
 * <p>
 *  Resolves the location of a given resource either on the local
 *  file system or in the classpath. In case no schema is specified
 *  in the given path, this loader will append a valid local one(file|classpath) to the
 *  specified resource location string and try to resolve it as last resort.</li>
 * </p>
 */
public class LocalLoader implements ResourceLoader {

    /** The file system for lookups */
    private FileSystem fileSystem;

    /** The classloader for classpath lookup */
    private ClassLoader classLoader;

    /**
     * Private constructor to avoid external instantiation
     */
    public LocalLoader() {
        fileSystem  = FileSystems.getDefault();
        classLoader = getClass().getClassLoader();
    }

    /**
     * Tries to resolve the resource location on the local file system
     * or classpath.
     *
     * @param path the resource location path
     * @return the resolved URL or null if not resolved.
     */
    public Context resolve(String path, Context context) {
        requireNonNull(path);
        URL resourceURL = null;
        Context result = (context != null) ? context : new Context();

        // Try to resolve it using the schema prefix
        if (path.startsWith("file:")) {
            resourceURL = getFileURL(path.replaceFirst("^file:", ""));
        } else if (path.startsWith("classpath:")) {
            resourceURL = getClasspathResourceURL(path.replaceFirst("^classpath:", ""));
        }

        // Set the resolved resource if any
        result.setResURL(resourceURL);
        result.setResolved(resourceURL!=null);

        return result;
    }

    /**
     * Retrieves the URL from the given resource path
     * in the filesystem.
     *
     * @param resourcePath the path to the resource in the filesystem
     * @return the URL to the resource
     */
    private URL getFileURL(String resourcePath) {
        URL resourceURL = null;
        if (resourcePath != null) {

            Path path = fileSystem.getPath(resourcePath);

            if (Files.exists(path)) {
                try {
                    resourceURL = path.toUri().toURL();
                } catch (MalformedURLException ignored) {
                }
            }
        }
        return resourceURL;
    }

    /**
     * Gets the URL from a resource located in the classpath.
     *
     * @param resourcePath the path to the resource in the classpath
     * @return the classpath resource url or null if not found
     */
    private URL getClasspathResourceURL(String resourcePath) {
        // Check the resource in the same class loader
        URL resourceURL = classLoader.getResource(resourcePath);
        // Fallback to the system class loader
        if (resourceURL == null) {
            resourceURL = ClassLoader.getSystemResource(resourcePath);
        }
        return resourceURL;
    }

    /**
     * Sets the classloader for classpath resolution
     *
     * @param classLoader the classloader
     */
    public void setClassLoader(ClassLoader classLoader) {
        requireNonNull(classLoader);
        this.classLoader = classLoader;
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

}
