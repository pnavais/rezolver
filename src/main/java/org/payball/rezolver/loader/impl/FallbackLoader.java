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

import org.payball.rezolver.ResourceInfo;
import org.payball.rezolver.loader.IFileSystemLoader;
import org.payball.rezolver.loader.IResourceLoader;
import org.payball.rezolver.loader.IUrlLoader;

import static java.util.Objects.requireNonNull;

/**
 * <b>FallbackLoader</b>
 * <p>
 *     A loader allowing to decorate a given resource loader
 *     and use a fallback location as prefix
 *     in case the resolution failed.
 * </p>
 */
public class FallbackLoader implements IResourceLoader {

    /** The default path separator */
    public static final String DEFAULT_PATH_SEPARATOR = "/";

    /** The target loader */
    private final IResourceLoader loader;

    /** The location info to append in case resolution failed */
    protected String fallbackPath;

    /**
     * Creates a @{@link FallbackLoader} wrapping
     * a given resource loader.
     *
     * @param loader the resource loader to wrap
     */
     public FallbackLoader(IResourceLoader loader) {
        requireNonNull(loader);
        this.loader = loader;
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

        // Resolve it using the base resolution
        resource = this.loader.resolve(location);

        // Last resort, try to resolve it using the fallback path
        if (!resource.isResolved() && (fallbackPath != null) && (!location.startsWith(fallbackPath))) {
            resource = this.loader.resolve(applyFallback(location));
        }

        return resource;
    }

    /**
     * Modify the current location applying the fallback path.
     * By default, the fallback will be appended to the location
     * using the path separator.
     *
     * @param location location to resolve
     * @return the location updated with fallback information
     */
    protected String applyFallback(String location) {
        requireNonNull(location);

        String prefix = fallbackPath + getSeparator();
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
     * Sets the fallback path to apply to a location as last resort for
     * resource resolution
     *
     * @param fallbackPath the fallback location
     */
    public void setFallbackPath(String fallbackPath) {
        requireNonNull(fallbackPath);
        this.fallbackPath= fallbackPath;
    }

    /**
     * Creates a new fallback loader with the given fallback
     * path.
     *
     * @param loader the resource loader to wrap
     * @param fallbackPath the fallback path
     * @return the fallback loader of the given resource loader
     */
    public static FallbackLoader of(IResourceLoader loader, String fallbackPath) {
        requireNonNull(loader);
        requireNonNull(fallbackPath);
        FallbackLoader fbl = new FallbackLoader(loader);
        fbl.setFallbackPath(fallbackPath);
        return fbl;
    }

}
