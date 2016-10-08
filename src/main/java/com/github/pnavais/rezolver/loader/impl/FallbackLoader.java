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
import com.github.pnavais.rezolver.loader.IResourceLoader;

import java.util.Optional;

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

    /** The location info to use as prefix in case resolution failed */
    protected String fallbackPrefix;

    /** The separator to use when adding the fallback prefix */
    protected String pathSeparator;

    /**
     * Creates a @{@link FallbackLoader} wrapping
     * a given resource loader.
     *
     * @param loader the resource loader to wrap
     */
    public FallbackLoader(IResourceLoader loader) {
        requireNonNull(loader);
        this.loader = loader;
        this.fallbackPrefix = DEFAULT_PATH_SEPARATOR;
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

        // Last resort, try to resolve it using the fallback path as prefix
        if (resource == null) {
            if ((fallbackPrefix != null) && (!location.startsWith(fallbackPrefix))) {
                resource = this.loader.resolve(fallbackPrefix + pathSeparator + location);
            }
        }

        resource = Optional.ofNullable(resource).orElseGet(() -> (!location.startsWith(fallbackPrefix))
                ? this.loader.resolve()
                : ResourceInfo.notSolved(location));

        return resource;
    }

    protected String applyFallback(String location) {
        return fallbackPrefix + pathSeparator + location
    }


    /**
     * Sets the prefix to add to a location as last resort for
     * resource resolution
     *
     * @param fallbackPrefix the fallback location
     */
    public void setFallbackPrefix(String fallbackPrefix) {
        requireNonNull(fallbackPrefix);
        this.fallbackPrefix = fallbackPrefix;
    }

}
