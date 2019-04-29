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

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * <b>FallbackLoader</b>
 * <p>
 *     A loader allowing to decorate a given resource loader
 *     and use a fallback location as prefix
 *     in case the resolution failed.
 * </p>
 */
public class FallbackLoader extends AbstractLocationLoader {

    /** The location alternatives to append in case resolution failed */
    protected List<String> fallbackPaths;

    /**
     * Creates a @{@link FallbackLoader} wrapping
     * a given resource loader.
     *
     * @param loader the resource loader to wrap
     */
     public FallbackLoader(IResourceLoader loader) {
        super(loader);
        this.fallbackPaths = new ArrayList<>();
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

        // Last resort, try to resolve it using the fallback paths
        for (int i=0; i<fallbackPaths.size() && !resource.isResolved(); i++) {
            String fallbackPath = fallbackPaths.get(i);
            if (!location.startsWith(fallbackPath+getSeparator())) {
                resource = this.loader.resolve(applyRootPath(fallbackPath, location));
            }
        }

        return resource;
    }


    /**
     * Sets the fallback path to apply to a location as last resort for
     * resource resolution
     *
     * @param fallbackPaths the fallback locations
     */
    public void setFallbackPaths(List<String> fallbackPaths) {
        requireNonNull(fallbackPaths);
        this.fallbackPaths = fallbackPaths;
    }

    /**
     * Adds an additional fallback path to the list.
     *
     * @param fallbackPath the fallback path
     * @return the fallback loader
     */
    public FallbackLoader addFallbackPath(String fallbackPath) {
        requireNonNull(fallbackPath);
        this.fallbackPaths.add(fallbackPath);
        return this;
    }

    /**
     * Creates a new fallback loader with the given fallback
     * path.
     *
     * @param loader the resource loader to wrap
     * @param fallbackPath the fallback path
     * @return the fallback loader of the given resource loader
     */
    public static FallbackLoader of(IResourceLoader loader, String fallbackPath, String... additionalFallbackPaths) {
        requireNonNull(loader);
        requireNonNull(fallbackPath);
        FallbackLoader fbl = new FallbackLoader(loader);
        fbl.addFallbackPath(fallbackPath);
        for (String additionalFallbackPath : additionalFallbackPaths) {
            fbl.addFallbackPath(additionalFallbackPath);
        }

        return fbl;
    }

    /**
     * Creates a new fallback loader with the given fallback
     * path.
     *
     * @param loader the resource loader to wrap
     * @param fallbackPaths the fallback paths
     * @return the fallback loader of the given resource loader
     */
    public static FallbackLoader of(IResourceLoader loader, List<String> fallbackPaths) {
        requireNonNull(loader);
        requireNonNull(fallbackPaths);
        FallbackLoader fbl = new FallbackLoader(loader);
        fbl.setFallbackPaths(fallbackPaths);
        return fbl;
    }

}
