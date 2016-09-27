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

/**
 * <b>FallbackLoader</b>
 * <p>
 *     A loader allowing to use a fallback location as prefix
 *     in case the resolution failed.
 * </p>
 */
public abstract class FallbackLoader<R> extends ContextAwareLoader<R> {

    /** The location info to use as prefix in case resolution failed */
    protected String fallbackLocation;

    /**
     * Tries to resolve the file using the supplied loader's
     * resolution algorithm but use the fallback location
     * in case resolution failed.
     *
     * @param location the location of the resource
     * @return the resource of null if not resolved
     */
    @Override
    public R resolve(String location) {
        R resource;

        // Resolve it using the base resolution
        resource = super.resolve(location);

        // Last resort, try to resolve it using the fallback path as prefix
        if (resource == null) {
            if ((fallbackLocation != null) && (!location.startsWith(fallbackLocation))) {
                resource = super.resolve(applyFallback(location));
            }
        }
        return resource;
    }

    /**
     * Retrieves the separator to apply between the prefix
     * fallback location and the current location
     *
     * @return the prefix separator
     */
    protected abstract String applyFallback(String location);

    /**
     * Sets the fallback location as last resort for
     * resource resolution
     *
     * @param fallbackLocation the fallback location
     */
    public void setFallbackLocation(String fallbackLocation) {
        this.fallbackLocation = fallbackLocation;
    }



}
