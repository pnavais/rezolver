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
import com.github.pnavais.rezolver.loader.IFileSystemLoader;
import com.github.pnavais.rezolver.loader.IResourceLoader;
import com.github.pnavais.rezolver.loader.IUrlLoader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * <b>DirLoader</b>
 * <p>
 *     A loader allowing to decorate a given resource loader
 *     and use a fixed location as prefix
 *     before resolving a resource location. In case the resource
 *     is located outside the fixed location it will be silently
 *     discarted.
 * </p>
 */
public class DirLoader implements IResourceLoader {

    /** The default path separator */
    public static final String DEFAULT_PATH_SEPARATOR = "/";

    /** The target loader */
    private final IResourceLoader loader;

    /** The location info to append in case resolution failed */
    protected String rootPath;

    /**
     * Creates a @{@link DirLoader} wrapping
     * a given resource loader.
     *
     * @param loader the resource loader to wrap
     */
     public DirLoader(IResourceLoader loader, String rootPath) {
         requireNonNull(loader);
         requireNonNull(rootPath);
         this.loader = loader;
         this.rootPath = rootPath;
    }

    /**
     * Tries to resolve the file using the supplied loader's
     * resolution algorithm but use the fallback location
     * in case resolution failed.
     *
     * @param location the location of the resource
     * @return the resource of null if not resolved
     */
    @Override
    public ResourceInfo resolve(String location) {
        ResourceInfo resource;

        // Obtain a path to the location
        Optional<Path> locationPath = getPath(location);

        // Check if path is absolute to avoid prefixing
        Boolean isAbsolute = locationPath.map(Path::isAbsolute).orElse(false);

        // Check if path belongs to the root path
        boolean hasSameRoot = hasSameRoot(locationPath.orElse(null));

        if (!isAbsolute || hasSameRoot){
            // Resolve with the loader
            resource = this.loader.resolve(isAbsolute ? location: applyRootPath(location));
            // Check if relative path is inside root path
            if ((!isAbsolute && (resource.isResolved())) &&
                (!hasSameRoot(getPath(resource.getURL().getPath()).orElse(null)))) {
                    resource = ResourceInfo.builder().build();
                }
        } else {
            // Absolute paths outside the root path are silently discarded
            resource = ResourceInfo.builder().build();
        }

        return resource;
    }

    /**
     * Check if the path has the same root than the loader
     *
     * @param locationPath the location path
     * @return true if same root, false otherwise
     */
    private boolean hasSameRoot(Path locationPath) {
        String normRootPath = getPath(rootPath).map(Path::toString).orElse(rootPath);
        return Optional.ofNullable(locationPath).filter(t -> t.getParent() != null)
                .map(t-> t.getParent().toString().equals(normRootPath))
                .orElse(false);
    }

    /**
     * Retrieves the path of the given location
     *
     * @param location the location
     * @return the actual path
     */
    private Optional<Path> getPath(String location) {
        Optional<Path> locationPath;
        if (loader instanceof LocalLoader) {
            locationPath = Optional.of(((LocalLoader)loader).fileSystem.getPath(location).normalize());
        } else{
            locationPath = Optional.ofNullable(Paths.get(location).normalize());
        }

        return locationPath;
    }

    /**
     * Modify the current location applying the fallback path.
     * By default, the fallback will be appended to the location
     * using the path separator.
     *
     * @param location location to resolve
     * @return the location updated with fallback information
     */
    protected String applyRootPath(String location) {
        requireNonNull(location);

        String prefix = rootPath + getSeparator();
        String newLocation = location;

        // Rearrange the scheme in case of URL Loaders
        if (this.loader instanceof IUrlLoader) {
            newLocation = ((IUrlLoader) this.loader).stripScheme(location);
            prefix = ((IUrlLoader) this.loader).getUrlScheme() + ":" + prefix;
        }

        return prefix + newLocation;
    }

    /**
     * Retrieves the separator
     *
     * @return the separator
     */
    protected String getSeparator() {
        return ((this.loader instanceof IFileSystemLoader)
                ? ((IFileSystemLoader) this.loader).getPathSeparator()
                : DEFAULT_PATH_SEPARATOR);
    }

    /**
     * Sets the root path to apply to a location for
     * resource resolution local to it
     *
     * @param rootPath the root path
     */
    public void setRootPath(String rootPath) {
        requireNonNull(rootPath);
        this.rootPath = rootPath;
    }

    /**
     * Creates a new folder loader with the given root
     * path. Only relative paths contained in the root path will be
     * resolved.
     *
     * @param loader the resource loader to wrap
     * @param rootPath the root path
     * @return the folder loader of the given resource loader
     */
    public static DirLoader of(IResourceLoader loader, String rootPath) {
        requireNonNull(loader);
        requireNonNull(rootPath);
        return new DirLoader(loader, rootPath);
    }

}
